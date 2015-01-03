LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_LDLIBS    := -lm -llog 
LOCAL_MODULE    := recong
LOCAL_SRC_FILES := recong.cpp \
                   xxplateresult.cpp

 LOCAL_SHARED_LIBRARIES := libcutils libutils
include $(BUILD_SHARED_LIBRARY)
