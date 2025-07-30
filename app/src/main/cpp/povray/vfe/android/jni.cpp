#include <unistd.h>
#include <jni.h>
#include "vfe.h"

#include <algorithm>
#include <chrono>
#include <iostream>
#include <thread>

#include <android/log.h>
#define ANDROID_LOG_ERR(fmt, str) __android_log_print(ANDROID_LOG_ERROR, "POVRAY_JNI", fmt, str)

using namespace vfe;

#define JNI_POVRAY_METHOD(func) Java_com_flykespice_droidray_POVRay_##func

//#define DELETE_FILE unlink

class AndroidVfeSession : public vfeSession
{
public:
	UCS2String GetTemporaryPath(void) const override
	{
		return ASCIItoUCS2String("/data/local/tmp");
	}
	
	UCS2String CreateTemporaryFile(void) const override
	{
		char str [FILE_NAME_LENGTH] = "";
		std::snprintf(str, FILE_NAME_LENGTH, "%spov%d", GetTemporaryPath().c_str(), getpid ());
		DELETE_FILE (str);
		
		return ASCIItoUCS2String (str);
	}
	
	void DeleteTemporaryFile(const UCS2String& filename) const override
	{
		DELETE_FILE (UCS2toASCIIString (filename).c_str());
	}
	
	bool TestAccessAllowed(const Path& file, bool isWrite) const override
	{
		return access(UCS2toASCIIString(file()).c_str(), isWrite ? W_OK : R_OK) == 0;
	}
	
	POV_LONG GetTimestamp(void) const override
	{
		auto time_point = std::chrono::steady_clock::now();
		return std::chrono::duration_cast<std::chrono::milliseconds>(time_point.time_since_epoch()).count();
	}
	
	void NotifyCriticalError (const char *message, const char *filename, int line) override
	{
		ANDROID_LOG_ERR("POV-Ray Critical Error: %s", message);
	}
	
	int RequestNewOutputPath(int CallCount, const string& Reason, const UCS2String& OldPath, UCS2String& NewPath) override
	{
		return 0;
	}
};

static std::uint8_t* imageBuffer;
static constexpr size_t BUFFER_W = 1920u, BUFFER_H = 1080u;

class AndroidDisplay : public vfeDisplay 
{
public:
	AndroidDisplay(unsigned int width, unsigned int height, GammaCurvePtr gamma, vfeSession *session, bool visible = false)
		: vfeDisplay(width, height, gamma, session, visible)
	{}
	void Initialise() override
	{
		std::fill_n(reinterpret_cast<std::uint32_t*>(imageBuffer), BUFFER_W*BUFFER_H, 0);
	}


	void Clear() override
	{
		std::fill_n(reinterpret_cast<std::uint32_t*>(imageBuffer), GetWidth()*GetHeight(), 0);
	}

	void DrawPixel(unsigned int x, unsigned int y, const RGBA8& colour) override 
	{
		//ARGB8 format used by Android ImageBitmap class
		std::uint8_t* pixel = &imageBuffer[(y * (GetWidth()*4)) + (x*4)];
		pixel[3] = 0xff;//colour.alpha;
		pixel[0] = colour.red;
		pixel[1] = colour.green;
		pixel[2] = colour.blue;
	}
};


static AndroidVfeSession* _vfeSession;
//static std::thread statusPollingThread;

static vfeDisplay *AndroidDisplayCreator(unsigned int width, unsigned int height, GammaCurvePtr gamma, vfeSession *session, bool visible)
{
	return new AndroidDisplay(width, height, gamma, session, visible);
}

#define RETURN_ON_ERROR(x)\
{\
	auto ret = x;\
	if (ret != vfeNoError)\
		return ret;\
}


static void throwRuntimeException(JNIEnv* env, const char* message)
{
    jclass exClass = env->FindClass("java/lang/RuntimeException");
    env->ThrowNew(exClass, message);
}

extern "C" JNIEXPORT jint JNICALL JNI_POVRAY_METHOD(renderScene) (JNIEnv* env, jobject thisObject, jstring filename, jstring commands)
{
	//TODO: Startup vfe to render from file
	if (_vfeSession == nullptr)
	{
		if (imageBuffer == nullptr)
		{
			// Step 1: Find the Java class containing the static ByteBuffer
			jclass myClass = env->FindClass("com/flykespice/droidray/POVRay");
			if (myClass == nullptr)
			{
				throwRuntimeException(env, "POVRay class not found");
			}

			// Step 2: Get the static field ID of the ByteBuffer
			jfieldID byteBufferFieldID = env->GetStaticFieldID(myClass, "imageBuffer", "Ljava/nio/ByteBuffer;");
			if (byteBufferFieldID == nullptr)
			{
				throwRuntimeException(env, "Static field 'imageBuffer' not found!!");
			}

			// Step 3: Get the static ByteBuffer object
			jobject byteBufferObject = env->GetStaticObjectField(myClass, byteBufferFieldID);
			if (byteBufferObject == nullptr)
			{
				throwRuntimeException(env, "ByteBuffer object is null!");
			}

			// Step 4: Check if the ByteBuffer is direct and get the memory address
			imageBuffer = reinterpret_cast<unsigned char*>(env->GetDirectBufferAddress(byteBufferObject));
			if (imageBuffer == nullptr)
			{
				throwRuntimeException(env, "The ByteBuffer is not a direct buffer!");
			}
		}

		_vfeSession = new AndroidVfeSession;

		auto ret = _vfeSession->Initialize(NULL, NULL);
		if (ret != vfeNoError)
		{
			delete _vfeSession;
			return ret;
		}

		_vfeSession->SetDisplayCreator(AndroidDisplayCreator);
	}


	const char* str = env->GetStringUTFChars(filename, nullptr);
	const char* cmd = env->GetStringUTFChars(commands, nullptr);

	vfeRenderOptions renderOptions;
	renderOptions.SetSourceFile(str); //filename's path must be in Android internal directory 
	renderOptions.SetThreadCount(4); //Android devices are very sensitive to overheating, let's not default to POVRAY exhausting all cores
	renderOptions.AddCommand(cmd);
	
	env->ReleaseStringUTFChars(filename, str);
	env->ReleaseStringUTFChars(commands, cmd);
	
	RETURN_ON_ERROR(_vfeSession->SetOptions(renderOptions))

	RETURN_ON_ERROR(_vfeSession->StartRender())

	return vfeNoError;
}

/**
 * Cancel rendering
 */
extern "C" JNIEXPORT jint JNICALL JNI_POVRAY_METHOD(cancelRender) (JNIEnv* env, jobject thisObject)
{
	if (_vfeSession == nullptr)
		return 0;

	return _vfeSession->CancelRender();
}

extern "C" JNIEXPORT jint JNICALL JNI_POVRAY_METHOD(getStatus) (JNIEnv* env, jobject thisObject, jboolean clear)
{
	if (_vfeSession == nullptr)
		return 0;

	return _vfeSession->GetStatus(clear, 200);
}

extern "C" JNIEXPORT jstring JNICALL JNI_POVRAY_METHOD(getErrorString) (JNIEnv* env, jobject thisObject, jint code)
{
	if (_vfeSession == nullptr)
		throwRuntimeException(env, "VFE session not initialized yet!!");

	return env->NewStringUTF(_vfeSession->GetErrorString(code));
}


extern "C" JNIEXPORT jobjectArray JNICALL JNI_POVRAY_METHOD(getMessages) (JNIEnv* env, jobject thisObject)
{
	jclass msgClass = env->FindClass("com/flykespice/droidray/POVRay$Message");
	if (!msgClass)
		throwRuntimeException(env, "POVRay.Message class couldn't be found");

	if (_vfeSession == nullptr)
		return env->NewObjectArray(0, msgClass, nullptr);

	vfeSession::MessageType type;
	std::string message;

	std::vector<std::pair<vfeSession::MessageType, std::string>> messages;

	while (_vfeSession->GetNextCombinedMessage(type, message))
	{
		messages.push_back(std::make_pair(type, message));
	}

	jobjectArray array = env->NewObjectArray(messages.size(), msgClass, nullptr);
	jmethodID constructor = env->GetMethodID(msgClass, "<init>", "(ILjava/lang/String;)V");

	if (!constructor)
		throwRuntimeException(env, "Unable to retrieve POVRay.Message constructor method ID");

	for (int i=0; i<messages.size(); i++)
	{
		const auto& [type, message] = messages[i];
		jstring messageString = env->NewStringUTF(message.c_str());
		jobject messageObj = env->NewObject(msgClass, constructor, type, messageString);

		env->SetObjectArrayElement(array, i, messageObj);
	}

	return array;
}

