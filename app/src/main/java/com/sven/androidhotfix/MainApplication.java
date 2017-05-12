package com.sven.androidhotfix;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import com.sven.fixlib.patch.PatchManager;

import java.io.IOException;

/**
 * Created by weixiao.ll on 17/5/10.
 */

public class MainApplication extends Application {
    private static final String TAG = "MainApplication";
    private static final String APATCH_PATH = "/out.apatch";
    private PatchManager mPatchManager;

    @Override
    public void onCreate() {
        super.onCreate();
        mPatchManager = new PatchManager(this);
        mPatchManager.init("1.0");
        Log.d(TAG, "inited.");
        // load patch
        mPatchManager.loadPatch();
        Log.d(TAG, "apatch loaded.");
        try {
            // .apatch file path
            String patchFileString = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + APATCH_PATH;
            Log.d(TAG, "apatch:" + patchFileString + " added.");
            mPatchManager.addPatch(patchFileString);
        } catch (IOException e) {
            Log.e(TAG, "", e);
        }
    }
}
