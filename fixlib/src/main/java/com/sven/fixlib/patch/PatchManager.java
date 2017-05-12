package com.sven.fixlib.patch;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.sven.fixlib.util.Common;
import com.sven.fixlib.hotfix.HotFixManager;
import com.sven.fixlib.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * patch manager
 * Created by weixiao.ll on 17/5/9.
 */

public class PatchManager {
    private static final String TAG = "PatchManager";
    private final Context mContext;
    private final HotFixManager mHotFixManager;
    private final SortedSet<Patch> mPatchs;
    private final Map<String, ClassLoader> mLoaders;
    // the patch file dir
    private final File mPatchDir;

    public PatchManager(Context context) {
        mContext = context;
        mHotFixManager = new HotFixManager(context);
        mPatchs = new ConcurrentSkipListSet<Patch>();
        mLoaders = new ConcurrentHashMap<String, ClassLoader>();
        mPatchDir = new File(context.getFilesDir(), Common.DIR);
    }

    /**
     * initialize, clear or add exist patch file into mPatchDir
     *
     * @param appVersion app version
     */
    public void init(String appVersion) {
        if (!mPatchDir.exists() && !mPatchDir.mkdirs()) {
            Log.e(TAG, "patch dir create error.");
            return;
        } else if (!mPatchDir.isDirectory()) {
            mPatchDir.delete();
            return;
        }
        SharedPreferences sp = mContext.getSharedPreferences(Common.SP_NAME,
                Context.MODE_PRIVATE);
        String ver = sp.getString(Common.SP_VERSION, null);
        if (ver == null || !ver.equalsIgnoreCase(appVersion)) {
            Log.d(TAG, "new app version.");
            cleanPatch();
            sp.edit().putString(Common.SP_VERSION, appVersion).commit();
        } else {
            initPatchs();
        }
    }

    /**
     * Initialize existing patch file
     */
    private void initPatchs() {
        File[] files = mPatchDir.listFiles();
        for (File file : files) {
            addPatch(file);
        }
    }

    /**
     * add patch file
     *
     * @param file
     * @return patch
     */
    private Patch addPatch(File file) {
        Patch patch = null;
        if (file.getName().endsWith(Common.SUFFIX)) {
            try {
                Log.d(TAG, "addPatch=" + file.getAbsolutePath());
                patch = new Patch(file);
                mPatchs.add(patch);
            } catch (IOException e) {
                Log.e(TAG, "addPatch", e);
            }
        }
        return patch;
    }

    /**
     * clean the exist patch file
     */
    private void cleanPatch() {
        File[] files = mPatchDir.listFiles();
        for (File file : files) {
            mHotFixManager.removeOptFile(file);
            if (!FileUtil.deleteFile(file)) {
                Log.e(TAG, file.getName() + " delete error.");
            }
        }
    }

    /**
     * add patch at runtime
     *
     * @param path
     */
    public void addPatch(String path) throws IOException {
        File src = new File(path);
        File dest = new File(mPatchDir, src.getName());
        if (!src.exists()) {
            Log.d(TAG, "patch [" + path + "] has not exist.");
            return;
        }
        if (dest.exists()) {
            Log.d(TAG, "patch [" + path + "] has be loaded.");
            return;
        }
        // load file
        try {
            FileUtil.copyFile(src, dest);
        } catch (Throwable t) {
            if (dest != null && dest.exists()) {
                Log.d(TAG, "remove dest file");
                dest.delete();
            }
            return;
        }
        Patch patch = addPatch(dest);
        if (patch != null) {
            loadPatch(patch);
        }
    }

    /**
     * load patch in data file
     */
    public void loadPatch() {
        Log.d(TAG, "loadPatch path in data file");
        mLoaders.put("*", mContext.getClassLoader());
        Set<String> patchNames;
        List<String> classes;
        for (Patch patch : mPatchs) {
            patchNames = patch.getPatchClassName();
            for (String patchName : patchNames) {
                classes = patch.getPatchClass(patchName);
                mHotFixManager.fix(patch.getFile(), mContext.getClassLoader(),
                        classes);
            }
        }
    }

    private void loadPatch(Patch patch) {
        Log.d(TAG, "loadPatch path in sdcard");
        Set<String> patchNames = patch.getPatchClassName();
        ClassLoader cl;
        List<String> classes;
        for (String patchName : patchNames) {
            if (mLoaders.containsKey("*")) {
                cl = mContext.getClassLoader();
            } else {
                cl = mLoaders.get(patchName);
            }
            if (cl != null) {
                classes = patch.getPatchClass(patchName);
                mHotFixManager.fix(patch.getFile(), cl, classes);
            }
        }
    }
}
