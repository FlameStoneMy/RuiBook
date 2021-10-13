package com.ruitech.bookstudy.utils;

import android.content.Context;

import com.ruitech.bookstudy.App;

import androidx.annotation.DimenRes;

public class UIHelper {

    public static int getDimenPx(@DimenRes int dimenRes) {
        if (dimenRes == 0) {
            return 0;
        }
        return ResourceUtil.getDimenPx(dimenRes);
    }

    public static int dp2px(int dp) {
        return (int) (App.applicationContext().getResources().getDisplayMetrics().density * dp + 0.5);
    }

    public static int px2dp(float px) {
        float density = App.applicationContext().getResources().getDisplayMetrics().density;
        if(density == 0){
            return 0;
        }
        return (int) ((px/density) + 0.5);
    }

    public static int sp2px(float sp) {
        return (int) (App.applicationContext().getResources().getDisplayMetrics().scaledDensity * sp + 0.5);
    }

}
