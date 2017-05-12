//
// Created by sven on 17/5/9.
//

#include <jni.h>
#include <stdio.h>
#include "common.h"
// 获取数组的大小
# define NELEM(x) ((int) (sizeof(x) / sizeof((x)[0])))
// 指定要注册的类，对应完整的java类名
#define JNIREG_CLASS "com/sven/fixlib/hotfix/HotFix"

// art module
extern void art_replaceMethod(JNIEnv *env, jobject src, jobject dest);

extern void art_setFieldFlag(JNIEnv *env, jobject field);

jstring stringFromJNI(JNIEnv *env, jobject /* this */) {
    char hello[] = "Hello from C+++";
    return env->NewStringUTF(hello);
}

static void replaceMethod(JNIEnv *env, jclass clazz, jobject src, jobject dest) {
    art_replaceMethod(env, src, dest);
}

static void setFieldFlag(JNIEnv *env, jclass clazz, jobject field) {
    art_setFieldFlag(env, field);
}

/*
 * JNI registration.
 */
static JNINativeMethod gMethods[] = {
        {"replaceMethod", "(Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;)V", (void *) replaceMethod},
        {"setFieldFlag",  "(Ljava/lang/reflect/Field;)V",                            (void *) setFieldFlag},
};

// 注册native方法到java中
static int registerNativeMethods(JNIEnv *env, const char *className,
                                 JNINativeMethod *gMethods, int numMethods) {
    jclass clazz;
    clazz = env->FindClass(className);
    if (clazz == NULL) {
        return JNI_FALSE;
    }
    if (env->RegisterNatives(clazz, gMethods, numMethods) < 0) {
        return JNI_FALSE;
    }

    return JNI_TRUE;
}

int register_ndk_load(JNIEnv *env) {
    // 调用注册方法
    return registerNativeMethods(env, JNIREG_CLASS,
                                 gMethods, NELEM(gMethods));
}

JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env = NULL;
    jint result = -1;

    if (vm->GetEnv((void **) &env, JNI_VERSION_1_4) != JNI_OK) {
        return result;
    }

    register_ndk_load(env);

    // 返回jni的版本
    return JNI_VERSION_1_4;
}