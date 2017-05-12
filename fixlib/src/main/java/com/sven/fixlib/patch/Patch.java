package com.sven.fixlib.patch;

import android.util.Log;

import com.sven.fixlib.util.Common;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * Created by weixiao.ll on 17/5/9.
 */

public class Patch implements Comparable<Patch>{
    private static final String TAG = "Patch";
    // patch file
    private final File mFile;
    // patchName
    private String patchName;
    // patch create time for sort
    private Date mTime;

    // classes of patch
    private Map<String, List<String>> mClassMap;

    public Patch(File file) throws IOException {
        mFile = file;
        init();
    }

    private void init() throws IOException {
        Log.d(TAG, "init patch");
        JarFile jarFile = null;
        InputStream inputStream = null;
        try {
            jarFile = new JarFile(mFile);
            JarEntry entry = jarFile.getJarEntry(Common.ENTRY_NAME);
            inputStream = jarFile.getInputStream(entry);
            Manifest manifest = new Manifest(inputStream);
            Attributes  main = manifest.getMainAttributes();
            patchName = main.getValue(Common.PATCH_NAME);
            mTime = new Date(main.getValue(Common.CREATED_TIME));
            mClassMap = new HashMap<String, List<String>>();
            Attributes.Name attrName;
            String name;
            List<String> strings;
            for (Iterator<?> it = main.keySet().iterator(); it.hasNext();) {
                attrName = (Attributes.Name) it.next();
                name = attrName.toString();
                if (name.endsWith(Common.CLASSES)) {
                    strings = Arrays.asList(main.getValue(attrName).split(","));
                    if (name.equalsIgnoreCase(Common.PATCH_CLASSES)) {
                        mClassMap.put(patchName, strings);
                    } else {
                        // remove-Classes"
                        mClassMap.put(name.trim().substring(0, name.length() - 8), strings);
                    }
                }
            }
        } catch (Throwable t) {
            Log.e(TAG, t.toString());
        } finally {
            if (jarFile != null) {
                jarFile.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    public String getPatchName() {
        return patchName;
    }

    public File getFile() {
        return mFile;
    }

    public Set<String> getPatchClassName() {
        return mClassMap.keySet();
    }

    public List<String> getPatchClass(String patchName) {
        return mClassMap.get(patchName);
    }

    public Date getTime() {
        return mTime;
    }

    @Override
    public int compareTo(Patch patch) {
        return mTime.compareTo(patch.getTime());
    }
}
