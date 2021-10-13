package com.ruitech.bookstudy.utils;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.ruitech.bookstudy.widget.FadeInBitmapDisplayer;

public class DisplayOptions {

    public static final int DURATION_MIDDLE = 220;

    private static DisplayImageOptions icon;
    public static DisplayImageOptions getIconDefault() {
        if (icon == null) {
            icon = new DisplayImageOptions.Builder()
                    .showImageOnLoading(android.R.color.white)
                    .showImageForEmptyUri(android.R.color.white)
                    .showImageOnFail(android.R.color.white)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .displayer(new FadeInBitmapDisplayer(DURATION_MIDDLE))
                    .considerExifParams(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .build();
        }

        return icon;
    }
}
