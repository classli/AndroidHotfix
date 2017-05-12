package com.sven.fixlib.hotfix;

import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by weixiao.ll on 17/5/9.
 */

public class HotFix {
    private static final String TAG = "HotFix";

    // native method
    private static native void replaceMethod(Method dest, Method src);

    private static native void setFieldFlag(Field field);

    // Used to load library on application startup.
    static {
        System.loadLibrary("hotfix-lib");
    }

    /**
     * replace method's body
     *
     * @param src
     * @param dest
     */
    public static void addReplaceMethod(Method src, Method dest) {
        if (src == null || dest == null) {
            return;
        }
        try {
            replaceMethod(src, dest);
            initFields(dest.getDeclaringClass()); //?
        } catch (Throwable t) {
            Log.e(TAG, "addReplaceMethod", t);
        }
    }

    /**
     * initialize the target class, and modify access flag of class’ fields to public
     *
     * @param clazz
     * @return
     */
    public static Class<?> initTargetClass(Class<?> clazz) {
        try {
            Class<?> targetClazz = Class.forName(clazz.getName(), true, clazz.getClassLoader()); // ?
            initFields(targetClazz);
            return targetClazz;
        } catch (Exception e) {
            Log.e(TAG, "initTargetClass", e);
        }
        return null;
    }

    private static void initFields(Class<?> clazz) {
        Field[] srcFields = clazz.getDeclaredFields();
        for (int i = 0; i < srcFields.length; i++) {
            setFieldFlag(srcFields[i]);
        }
        Log.d(TAG, "initFields over");
    }

}
