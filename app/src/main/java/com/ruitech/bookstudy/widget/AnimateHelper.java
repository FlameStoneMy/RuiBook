package com.ruitech.bookstudy.widget;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.imageaware.ImageAware;

import androidx.annotation.NonNull;

public class AnimateHelper {

    public static void alphaView(ImageAware imageAware, Bitmap bitmap, int duration) {
        if (imageAware == null || bitmap == null || imageAware.getWrappedView() == null) {
            return;
        }
        alphaView(imageAware, new BitmapDrawable(imageAware.getWrappedView().getResources(), bitmap), duration);
    }

    public static void alphaView(ImageAware imageAware, Drawable drawable, int duration) {
        if (imageAware == null || imageAware.getWrappedView() == null || drawable == null) {
            return;
        }
        ImageView imageView = (ImageView) imageAware.getWrappedView();
        alphaView(imageView, drawable, duration);
    }

    public static void alphaView(ImageView imageView, Drawable drawable, int duration) {
        if (imageView == null || drawable == null) {
            return;
        }
        TransitionDrawable transitionDrawable =  new TransitionDrawable(new Drawable[]{getDefDrawable(imageView, drawable), drawable});
        transitionDrawable.setCrossFadeEnabled(true);
        imageView.setImageDrawable(transitionDrawable);
        transitionDrawable.startTransition(duration);
    }

    private static Drawable transparentDrawable;
    private static Drawable getDefDrawable(@NonNull ImageView imageView, Drawable drawable) {
        Drawable result = imageView.getDrawable();
        if (result == null) {
            if (transparentDrawable == null) {
                transparentDrawable = imageView.getResources().getDrawable(android.R.color.transparent);
            }
            return transparentDrawable;
        }
        return result;
    }
}
