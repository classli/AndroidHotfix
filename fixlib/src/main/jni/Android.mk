LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE    := hotfix-lib
LOCAL_SRC_FILES := hotfix.cpp
LOCAL_LDLIBS    := -llog

include $(BUILD_SHARED_LIBRARY)