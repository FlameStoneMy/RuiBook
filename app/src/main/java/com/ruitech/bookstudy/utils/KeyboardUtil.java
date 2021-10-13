package com.ruitech.bookstudy.utils;

import android.app.Activity;
import android.content.Context;
import android.os.IBinder;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

public class KeyboardUtil {
    public static boolean hideKeyboard(Context context) {
        if (context != null && context instanceof Activity) {
            return hideKeyboard(context, ((Activity) context).getWindow());
        }
        return false;
    }

    public static boolean hideKeyboard(Context context, Window window) {
        return hideKeyboard(context, window.getDecorView().getWindowToken());
    }

    public static boolean hideKeyboard(Context context, IBinder windowToken) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
        return imm.hideSoftInputFromWindow(windowToken, 0);
    }

    public static boolean showKeyboard(Context context, View v) {
        if (context == null) {
            return false;
        }
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm.showSoftInput(v, 0);
    }

    public static void toggleSoftInput(Context context) {
        if (context == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }
}
