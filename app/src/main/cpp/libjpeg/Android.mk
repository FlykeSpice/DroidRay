LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := libjpeg

LOCAL_SRC_FILES := $(LOCAL_PATH)/wrtarga.c \
$(LOCAL_PATH)/wrrle.c \
$(LOCAL_PATH)/wrppm.c \
$(LOCAL_PATH)/wrgif.c \
$(LOCAL_PATH)/wrbmp.c \
$(LOCAL_PATH)/transupp.c \
$(LOCAL_PATH)/rdtarga.c \
$(LOCAL_PATH)/rdswitch.c \
$(LOCAL_PATH)/rdrle.c \
$(LOCAL_PATH)/rdppm.c \
$(LOCAL_PATH)/rdgif.c \
$(LOCAL_PATH)/rdcolmap.c \
$(LOCAL_PATH)/rdbmp.c \
$(LOCAL_PATH)/jutils.c \
$(LOCAL_PATH)/jquant2.c \
$(LOCAL_PATH)/jquant1.c \
$(LOCAL_PATH)/jmemansi.c \
$(LOCAL_PATH)/jmemmgr.c \
$(LOCAL_PATH)/jidctint.c \
$(LOCAL_PATH)/jidctfst.c \
$(LOCAL_PATH)/jidctflt.c \
$(LOCAL_PATH)/jfdctint.c \
$(LOCAL_PATH)/jfdctfst.c \
$(LOCAL_PATH)/jfdctflt.c \
$(LOCAL_PATH)/jerror.c \
$(LOCAL_PATH)/jdtrans.c \
$(LOCAL_PATH)/jdsample.c \
$(LOCAL_PATH)/jdpostct.c \
$(LOCAL_PATH)/jdmerge.c \
$(LOCAL_PATH)/jdmaster.c \
$(LOCAL_PATH)/jdmarker.c \
$(LOCAL_PATH)/jdmainct.c \
$(LOCAL_PATH)/jdinput.c \
$(LOCAL_PATH)/jdhuff.c \
$(LOCAL_PATH)/jddctmgr.c \
$(LOCAL_PATH)/jdcolor.c \
$(LOCAL_PATH)/jdcoefct.c \
$(LOCAL_PATH)/jdatasrc.c \
$(LOCAL_PATH)/jdatadst.c \
$(LOCAL_PATH)/jdarith.c \
$(LOCAL_PATH)/jdapistd.c \
$(LOCAL_PATH)/jdapimin.c \
$(LOCAL_PATH)/jctrans.c \
$(LOCAL_PATH)/jcsample.c \
$(LOCAL_PATH)/jcprepct.c \
$(LOCAL_PATH)/jcparam.c \
$(LOCAL_PATH)/jcomapi.c \
$(LOCAL_PATH)/jcmaster.c \
$(LOCAL_PATH)/jcmarker.c \
$(LOCAL_PATH)/jcmainct.c \
$(LOCAL_PATH)/jcinit.c \
$(LOCAL_PATH)/jchuff.c \
$(LOCAL_PATH)/jcdctmgr.c \
$(LOCAL_PATH)/jccolor.c \
$(LOCAL_PATH)/jccoefct.c \
$(LOCAL_PATH)/jcarith.c \
$(LOCAL_PATH)/jcapistd.c \
$(LOCAL_PATH)/jcapimin.c \
$(LOCAL_PATH)/jaricom.c \
$(LOCAL_PATH)/cdjpeg.c 


LOCAL_C_INCLUDES := $(LOCAL_PATH)

LOCAL_EXPORT_C_INCLUDES := $(LOCAL_C_INCLUDES)

include $(BUILD_STATIC_LIBRARY)
