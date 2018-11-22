package com.pham.duycuong.soundcloud.util;

import android.os.Environment;
import java.io.File;

public class FileUtil {
    public static String getRootCachePath() {
        String mCacheRootPath = null;
        if (Environment.getExternalStorageDirectory().exists()) {
            mCacheRootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        if (mCacheRootPath != null && mCacheRootPath.trim().length() != 0) {
            File testFile = new File(mCacheRootPath);
            if (!(testFile.exists() && testFile.canRead() && testFile.canWrite())) {
                mCacheRootPath = null;
            }
        }
        if (mCacheRootPath == null || mCacheRootPath.trim().length() == 0) {
//            mCacheRootPath = mContext.getCacheDir().getPath();
        }
        return mCacheRootPath;
    }
}
