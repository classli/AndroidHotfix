package com.sven.fixlib.hotfix;

import android.content.Context;
import android.util.Log;

import com.alipay.euler.andfix.annotation.MethodReplace;
import com.sven.fixlib.util.Common;
import com.sven.fixlib.util.FileUtil;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import dalvik.system.DexFile;

/**
 * Created by weixiao.ll on 17/5/9.
 */

public class HotFixManager {
    private static final String TAG = "HotFixManager";
    // context
    private final Context mContext;
    // optimize directory
    private File mOptDir;
    // whether support Fix
    private boolean mSupport = true;
    // cache for classes will be fixed
    private static Map<String, Class<?>> mFixedClass = new ConcurrentHashMap<String, Class<?>>();

    public HotFixManager(Context context) {
        mContext = context;
        mOptDir = new File(mContext.getFilesDir(), Common.DIR_OPT);
        if (!mOptDir.exists() && !mOptDir.mkdirs()) {
            mSupport = false;
            Log.e(TAG, "opt dir create error.");
        } else if (!mOptDir.isDirectory()) {
            mOptDir.delete();
            mSupport = false;
        }
    }

    /**
     * remove optimize patch file
     *
     * @param file
     */
    public synchronized void removeOptFile(File file) {
        if (file == null) {
            return;
        }
        File optFile = new File(mOptDir, file.getName());
        if (optFile.exists() && !optFile.delete()) {
            Log.e(TAG, optFile.getName() + " delete error.");
        }
    }

    /**
     * Fix Method
     *
     * @param file        patch file
     * @param classLoader classloader of class that will be fixed
     * @param classes     classes will be fixed
     */
    public synchronized void fix(File file, ClassLoader classLoader, final List<String> classes) {
        if (!mSupport || file == null || classLoader == null || classes == null) {
            return;
        }
        try {
            File optFile = new File(mOptDir, file.getName());
            final DexFile dexFile = DexFile.loadDex(file.getAbsolutePath(), optFile.getAbsolutePath(), Context.MODE_PRIVATE);
            ClassLoader patchClassLoader = new ClassLoader() {
                @Override
                protected Class<?> findClass(String name) throws ClassNotFoundException {
                    Class<?> clazz = dexFile.loadClass(name, this);
                    if (clazz == null) {
                        return Class.forName(name);
                    }
                    if (clazz == null) {
                        throw new ClassNotFoundException(name);
                    }
                    return clazz;
                }
            };
            // Enumerate class from dex file
            Enumeration<String> entrys = dexFile.entries();
            Class<?> clazz = null;
            while (entrys.hasMoreElements()) {
                String entry = entrys.nextElement();
                if (!classes.contains(entry)) {
                    continue;
                }
                clazz = dexFile.loadClass(entry, patchClassLoader);
                if (clazz != null) {
                    fixClass(clazz, classLoader);
                }
            }
        } catch (Throwable t) {
            Log.e(TAG, "fix", t);
        }
    }

    /**
     * fix class
     *
     * @param clazz
     * @param classLoader
     */
    private void fixClass(Class<?> clazz, ClassLoader classLoader) {
        if (clazz == null || classLoader == null) {
            return;
        }
        Method[] methods = clazz.getDeclaredMethods();
        MethodReplace methodReplace;
        String clz;
        String mth;
        for (Method method : methods) {
            methodReplace = method.getAnnotation(MethodReplace.class);
            if (methodReplace == null)
                continue;
            clz = methodReplace.clazz();
            mth = methodReplace.method();
            Log.d(TAG, "fixClass:clz=" + clz + " mth=" + mth);
            if (!FileUtil.isEmpty(clz) && !FileUtil.isEmpty(mth)) {
                replaceMethod(classLoader, clz, mth, method);
            }
        }
    }

    /**
     * fix method
     *
     * @param classLoader
     * @param clz         source class
     * @param smethod     source method
     * @param dmethod     target method
     */
    private void replaceMethod(ClassLoader classLoader, String clz,
                               String smethod, Method dmethod) {
        if (classLoader == null || clz == null || smethod == null || dmethod == null) {
            return;
        }
        try {
            String key = clz + "@" + classLoader.toString();
            Class<?> clazz = mFixedClass.get(key);
            if (clazz == null) {
                Class<?> clzz = classLoader.loadClass(clz);
                // initialize target class
                clazz = HotFix.initTargetClass(clzz);
            }
            if (clazz != null) {
                mFixedClass.put(key, clazz);
                Method srcMethod = clazz.getDeclaredMethod(smethod, dmethod.getParameterTypes()); //?
                HotFix.addReplaceMethod(srcMethod, dmethod);
            }
        } catch (Throwable t) {
            Log.e(TAG, "replaceMethod", t);
        }
    }

}
