package com.ruitech.bookstudy.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.SystemClock;
import android.view.View;

import com.ruitech.bookstudy.App;

public class Util {
    public static boolean isValidActivity(Activity activity) {
        if (Build.VERSION.SDK_INT >= 17)
            return activity != null && !activity.isFinishing() && !activity.isDestroyed();
        else {
            return activity != null && !activity.isFinishing();
        }
    }

    private static long lastClickTime;
    public static boolean filterClick() {
        long now = SystemClock.elapsedRealtime();
        long elapse = now - lastClickTime;
        lastClickTime = now;
        return elapse < 700L;
    }

    public static int getPackageVersion() {
        Context context = App.applicationContext();
        int ret = 0;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            ret = pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return ret;
    }
}
