package com.sven.fixlib.util;

/**
 * Created by weixiao.ll on 17/5/9.
 */

public class Common {

    // patch extension, the apk patch tool automatically generation these fields
    public static final String SUFFIX = ".apatch";
    public static final String DIR = "apatch";
    public static final String SP_VERSION = "version";
    public static final String SP_NAME = "_andfix_";

    // path file information, patch tool automatically generation
    public static final String ENTRY_NAME = "META-INF/PATCH.MF";
    public static final String CLASSES = "-Classes";
    public static final String PATCH_CLASSES = "Patch-Classes";
    public static final String CREATED_TIME = "Created-Time";
    public static final String PATCH_NAME = "Patch-Name";

    // optimize directory
    public static final String DIR_OPT = "apatch_opt";
}
