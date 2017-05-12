//
// Created by sven on 17/5/10.
//

#include "art.h"

extern void __attribute__ ((visibility ("hidden"))) art_replaceMethod(JNIEnv *env,
                                                                      jobject src, jobject dest) {
    replace_7_0(env, src, dest);
}

extern void __attribute__ ((visibility ("hidden"))) art_setFieldFlag(
        JNIEnv *env, jobject field) {
    setFieldFlag_7_0(env, field);
}