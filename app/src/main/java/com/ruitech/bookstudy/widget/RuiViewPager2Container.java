package com.ruitech.bookstudy.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class RuiViewPager2Container extends FrameLayout {
    private static final String TAG = "RuiViewPager2Container";

    public RuiViewPager2Container(@NonNull Context context) {
        super(context);
    }

    public RuiViewPager2Container(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RuiViewPager2Container(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RuiViewPager2Container(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        boolean ret = super.dispatchTouchEvent(ev);super.touch
//        Log.d(TAG, "dispatchTouchEvent: " + ev.getAction() + " " + ret);
//        return ret;
//    }

//    private float touchSlop;
//    {
//        touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
//    }

    private float initialX, initialY;
    private boolean disallowed;
    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        boolean ret = super.onInterceptTouchEvent(e);
        Log.d(TAG, "onInterceptTouchEvent: " + e.getAction() + " " + ret);

        requestDisallowInterceptTouchEvent(true); // high tense
//        switch (e.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                initialX = e.getX();
//                initialY = e.getY();
//                disallowed = false;
//                break;
//            case MotionEvent.ACTION_MOVE:
//                if (!disallowed) {
//                    float dx = Math.abs(e.getX() - initialX);
//                    float dy = Math.abs(e.getY() - initialY);
//                    if (dx >= touchSlop && dx > dy) {
//                        requestDisallowInterceptTouchEvent(true);
//                        disallowed = true;
//                    }
//                }
//                break;
//        }
        return false;
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent e) {
//        boolean ret = super.onTouchEvent(e);
//        Log.d(TAG, "onTouchEvent: " + e.getAction() + " " + ret + " " + MotionEvent.ACTION_CANCEL);
//        return ret;
//    }
}
