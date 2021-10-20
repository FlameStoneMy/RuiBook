package com.ruitech.bookstudy.upgrade;

import com.ruitech.bookstudy.App;

import java.io.File;

public class UpgradeUtil {

    // files/.nomedia/upgrade/2/
    public static File getUpgradeDir(int versionCode) {
        return new File(App.applicationContext().getExternalFilesDir(".nomedia"), "upgrade/" + versionCode);
    }

    public static File getUpgradeZipFile(int versionCode) {
        return new File(getUpgradeDir(versionCode), "apk.zip");
    }

    public static File getUpgradeApkFile(int versionCode) {
        return new File(getUpgradeDir(versionCode), "app-release.apk");
    }
}
