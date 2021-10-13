package com.ruitech.bookstudy.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class RuiRecyclerView extends RecyclerView {
    private static final String TAG = "RuiRecyclerView";

    public RuiRecyclerView(@NonNull Context context) {
        super(context);
    }

    public RuiRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RuiRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean ret = super.dispatchTouchEvent(ev);
        Log.d(TAG, "dispatchTouchEvent: " + ev.getAction() + " " + ret);
        return ret;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        boolean ret = super.onInterceptTouchEvent(e);
        Log.d(TAG, "onInterceptTouchEvent: " + e.getAction() + " " + ret);
        if (ret) {
            requestDisallowInterceptTouchEvent(true);
        }
        return ret;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        boolean ret = super.onTouchEvent(e);
        Log.d(TAG, "onTouchEvent: " + e.getAction() + " " + ret + " " + MotionEvent.ACTION_MOVE);
        return ret;
    }
}
