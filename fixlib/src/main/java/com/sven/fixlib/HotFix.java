package com.sven.fixlib;

/**
 * Created by weixiao.ll on 17/5/9.
 */

public class HotFix {

    public native String getJniString();

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("hotfix-lib");
    }
}
