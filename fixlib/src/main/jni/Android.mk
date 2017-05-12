LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := hotfix-lib

LOCAL_SRC_FILES := hotfix.cpp \
                    art/art.cpp \
                    art/art_7_0.cpp \

LOCAL_CFLAGS	:= -std=c++11 -fpermissive -DDEBUG -O0

LOCAL_LDLIBS    := -llog

include $(BUILD_SHARED_LIBRARY)