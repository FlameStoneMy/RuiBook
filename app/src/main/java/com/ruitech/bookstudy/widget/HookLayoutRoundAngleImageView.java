package com.ruitech.bookstudy.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;

import com.ruitech.bookstudy.BuildConfig;

import androidx.annotation.Nullable;

/**
 * This class aims to hook method requestLayout() if size is fixed to prevent unwanted refresh.
 * Currently only setImageDrawable() is hooked.
 */
public class HookLayoutRoundAngleImageView extends RoundAngleImageView {
    private static final boolean DEBUG = BuildConfig.DEBUG;
    private static final String TAG = "HookLayoutRAImgView";

    public HookLayoutRoundAngleImageView(Context context) {
        super(context);
    }

    public HookLayoutRoundAngleImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HookLayoutRoundAngleImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        if (DEBUG) {
            Log.d(TAG, "setImageDrawable: " + canHook);
        }
        if (canHook) {
            hooking = true;
        }
        super.setImageDrawable(drawable);
        hooking = false;
    }

    private boolean canHook;
    private boolean hooking;
    private int layoutWidth, layoutHeight;
    @Override
    public void requestLayout() {
        if (!hooking) {
            ViewGroup.LayoutParams params = getLayoutParams();
            if (params != null) {
                layoutWidth = params.width;
                layoutHeight = params.height;
                canHook = layoutWidth > 0 && layoutHeight > 0; // Fixed size.
            }
            super.requestLayout();
        }
        if (DEBUG) {
            Log.d(TAG, "requestLayout: " + hooking + " " + layoutWidth + " " + layoutHeight);
        }
    }
}
