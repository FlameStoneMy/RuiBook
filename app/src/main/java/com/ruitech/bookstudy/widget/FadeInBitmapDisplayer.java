package com.ruitech.bookstudy.widget;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;

public class FadeInBitmapDisplayer implements BitmapDisplayer {

    protected int durationMills;

    public FadeInBitmapDisplayer(int durationMills) {
        this.durationMills = durationMills;
    }

    @Override
    public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
        if (loadedFrom != LoadedFrom.NETWORK) {
            imageAware.setImageBitmap(bitmap);
        } else {
            AnimateHelper.alphaView(imageAware, bitmap, durationMills);
        }
    }
}
