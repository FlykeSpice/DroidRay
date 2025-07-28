LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := povray

LOCAL_CFLAGS := -DLIBJPEG_MISSING -DLIBPNG_MISSING -DLIBTIFF_MISSING -DOPENEXR_MISSING -DX_DISPLAY_MISSING -DDISTRIBUTION_MESSAGE_2=\"flykespice\"

LOCAL_CPPFLAGS := -std=c++17

LOCAL_CPP_FEATURES := exceptions rtti

LOCAL_C_INCLUDES :=\
	$(LOCAL_PATH)/source\
	$(LOCAL_PATH)/source/frontend\
	$(LOCAL_PATH)/source/backend\
	$(LOCAL_PATH)/source/base\
	$(LOCAL_PATH)/vfe\
	$(LOCAL_PATH)/vfe/android

LOCAL_LDLIBS := -lz -lm -llog

LOCAL_SRC_FILES :=\
	$(wildcard $(LOCAL_PATH)/source/*.cpp)\
	$(wildcard $(LOCAL_PATH)/source/frontend/*.cpp)\
	$(wildcard $(LOCAL_PATH)/source/backend/*.cpp)\
	$(wildcard $(LOCAL_PATH)/source/backend/**/*.cpp)\
	$(wildcard $(LOCAL_PATH)/source/base/*.cpp)\
	$(wildcard $(LOCAL_PATH)/source/base/font/*.cpp)\
	$(wildcard $(LOCAL_PATH)/source/base/image/*.cpp)\
	$(wildcard $(LOCAL_PATH)/vfe/*.cpp)\
	$(wildcard $(LOCAL_PATH)/vfe/android/*.cpp)

include $(BUILD_SHARED_LIBRARY)
