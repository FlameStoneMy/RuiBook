package com.ruitech.bookstudy.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.telephony.TelephonyManager;
import android.view.Display;
import android.view.WindowManager;

import com.ruitech.bookstudy.App;

public class DeviceUtil {
    public static boolean hasSimCard(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return tm != null
                    && !(tm.getSimState() == TelephonyManager.SIM_STATE_ABSENT)
                    // some phones return a status of SIM_STATE_UNKNOWN when there is no sim card. we treat transition state as no sim card.
                    && !(tm.getSimState() == TelephonyManager.SIM_STATE_UNKNOWN);
        } catch (Exception ignored) {
        }
        return false;
    }

    public static boolean hasTelephoneFeature(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            return pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
        } catch (Exception ignored) {

        }
        // we return false only when the above check is certainly.
        return true;
    }

    public static int getWindowHeight() {
//        WindowManager manager = (WindowManager) App.applicationContext()
//                .getSystemService(Context.WINDOW_SERVICE);
//        return manager.getCurrentWindowMetrics().getBounds().height();
        WindowManager manager = (WindowManager) App.applicationContext()
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getHeight();
    }

    public static int fullScreenHeight = -1;
    public static final void updateFullScreenHeightIfNeeded(int height) {
        if (fullScreenHeight < 0) {
            if (height >= getWindowHeight()) {
                fullScreenHeight = height;
            }
        }
    }
    public static int getFullScreenHeight() {
        return fullScreenHeight;
    }
}
