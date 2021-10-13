package com.ruitech.bookstudy.utils;

import android.util.SparseIntArray;

import com.ruitech.bookstudy.App;

import androidx.annotation.DimenRes;

public class ResourceUtil {

    private static SparseIntArray dimenMap = new SparseIntArray(20);

    public static int getDimenPx(@DimenRes int dimenRes) {
        int integer = dimenMap.get(dimenRes, Integer.MIN_VALUE);
        if (integer != Integer.MIN_VALUE)
            return integer;

        int pixelSize = App.applicationContext().getResources().getDimensionPixelSize(dimenRes);
        dimenMap.put(dimenRes, pixelSize);

        return pixelSize;
    }
}
