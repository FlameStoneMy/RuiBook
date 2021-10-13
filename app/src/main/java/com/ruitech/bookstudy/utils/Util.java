package com.ruitech.bookstudy.utils;

import android.app.Activity;
import android.os.Build;
import android.os.SystemClock;
import android.view.View;

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
}
