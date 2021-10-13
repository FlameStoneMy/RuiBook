package com.ruitech.bookstudy.widget;

import android.widget.ImageView;

import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

public class MyViewWare extends ImageViewAware {
    private final int width;
    private final int height;

    public MyViewWare(ImageView imageView, int width, int height) {
        super(imageView, true);
        this.width = width;
        this.height = height;
    }

    @Override
    public int getWidth() {
        if (width == -1)
            return super.getWidth();

        else return width;
    }

    @Override
    public int getHeight() {
        if (height == -1)
            return super.getHeight();

        return height;
    }
}
