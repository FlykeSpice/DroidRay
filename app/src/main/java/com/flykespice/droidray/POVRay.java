package com.flykespice.droidray;

import java.nio.ByteBuffer;

public class POVRay {

    static {
        System.loadLibrary("povray");
    }

    //
    //  vfeSession errors
    //
    public static final int vfeNoError                      = 0;
    public static final int vfeNoInputFile                  = 1024;   // "No input file provided"
    public static final int vfeRenderBlockSizeTooSmall      = 1025;   // "Specified block size is too small"
    public static final int vfeFailedToWriteINI             = 1026;   // "Failed to write output INI file"
    public static final int vfeFailedToSetSource            = 1027;   // "Failed to set source file"
    public static final int vfeFailedToParseINI             = 1028;   // "Failed to parse INI file"
    public static final int vfeIORestrictionDeny            = 1029;   // "I/O Restrictions prohibit access to file"
    public static final int vfeFailedToParseCommand         = 1030;   // "Failed to parse command-line option"
    public static final int vfeFailedToSetMaxThreads        = 1031;   // "Failed to set number of render threads"
    public static final int vfeFailedToSendRenderStart      = 1032;   // "Failed to send render start request"
    public static final int vfeRenderOptionsNotSet          = 1033;   // "Render options not set, cannot start render"
    public static final int vfeAlreadyStopping              = 1034;   // "Renderer is already stopping"
    public static final int vfeNotRunning                   = 1035;   // "Renderer is not running"
    public static final int vfeInvalidParameter             = 1036;   // "Something broke but we're not sure what"
    public static final int vfeSessionExists                = 1037;   // "Only one session at once permitted in this version"
    public static final int vfePOVMSInitFailed              = 1038;   // "Failed to initialize local messaging subsystem"
    public static final int vfeOpenContextFailed            = 1039;   // "Failed to open context with core messaging subsystem"
    public static final int vfeConnectFailed                = 1040;   // "Failed to connect to core messaging subsystem"
    public static final int vfeInitializeTimedOut           = 1041;   // "Timed out waiting for worker thread startup"
    public static final int vfeRequestTimedOut              = 1042;   // "Timed out waiting for request to be serviced"
    public static final int vfeFailedToInitObject           = 1043;   // "Failed to initialize internal options storage"
    public static final int vfeCaughtException              = 1044;   // "Caught exception of unexpected type"
    public static final int vfeCaughtCriticalError          = 1045;   // "Caught critical error"
    public static final int vfeDisplayGammaTooSmall         = 1046;   // "Specified display gamma is too small"
    public static final int vfeFileGammaTooSmall            = 1047;   // "Specified file gamma is too small"
    public static final int vfeUnsupportedOptionCombination = 1048;   // "Unsupported option combination"


    //
    // Status bit masks
    //
    public static final int stNone                    = 0x00000000;       // No status to report
    public static final int stClear                   = 0x00000001;       // vfeSession::Clear() has been called
    public static final int stReset                   = 0x00000002;       // vfeSession::Reset() has been called
    public static final int stFailed                  = 0x00000004;       // Render failed
    public static final int stSucceeded               = 0x00000008;       // Render succeeded
    public static final int stStatusMessagesCleared   = 0x00000010;       // vfeSession::ClearStatusMessages() called
    public static final int stOutputFilenameKnown     = 0x00000020;       // vfeSession::AdviseOutputFilename() called
    public static final int stRenderingAnimation      = 0x00000040;       // vfeSession::SetRenderingAnimation() called
    public static final int stAnimationFrameCompleted = 0x00000080;       // vfeSession::AdviseFrameCompleted() called
    public static final int stStreamMessage           = 0x00000100;       // One or more stream messages are available
    public static final int stErrorMessage            = 0x00000200;       // One or more error messages are available
    public static final int stWarningMessage          = 0x00000400;       // One or more warning messages are available
    public static final int stStatusMessage           = 0x00000800;       // One or more status messages are available
    public static final int stAnyMessage              = 0x00000f00;       // A mask of stStream, Error, Warning, and Status message flags.
    public static final int stAnimationStatus         = 0x00001000;       // An animation status update is available
    public static final int stBackendStateChanged     = 0x00002000;       // The state of the backend (reflected in pov_frontend::State) has changed
    public static final int stRenderStartup           = 0x00004000;       // The render engine has started up
    public static final int stRenderShutdown          = 0x00008000;       // The render engine has shut down
    public static final int stShutdown                = 0x10000000;       // The session is shutting down
    public static final int stCriticalError           = 0x20000000;       // A critical error (exception, POVMS memory alloc failure, etc) has occurred
    public static final int stNoIgnore                = 0x30000000;       // A mask containing the status flags which can't be masked off
    public static final int stNoClear                 = 0x30000000;       // A mask containing the status flags which aren't cleared after a call to vfeSession::GetStatus()

    public static ByteBuffer imageBuffer = ByteBuffer.allocateDirect(1920 * 1080 * 4);//.order(ByteOrder.BIG_ENDIAN);

    public static native int renderScene(String filename, String commands);
    public static native int cancelRender();
    public static native String getErrorString(int code);
    public static native int getStatus(boolean clear);

    public static class Message {
        public static final int TYPE_UNCLASSIFIED     = 0;
        public static final int TYPE_DEBUG            = 1;
        public static final int TYPE_INFO             = 2;
        public static final int TYPE_WARNING          = 3;
        public static final int TYPE_POSSIBLE_ERROR   = 4;
        public static final int TYPE_ERROR            = 5;
        public static final int TYPE_ANIMATION_STATUS = 6;
        public static final int TYPE_GENERIC_STATUS   = 7;
        public static final int TYPE_DIVIDER          = 8;

        public int type;
        public String message;

        public Message(int type, String message) {
            this.type = type;
            this.message = message;
        }
    }

    public static native Message[] getMessages();

    private static int onSAFOpenFile(String filename) {
        //filename root path is relative to granted directory access path
        return 0;
    }
}
