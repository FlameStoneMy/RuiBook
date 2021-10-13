package com.ruitech.bookstudy.widget.panelhelper.layout;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;


import com.ruitech.bookstudy.widget.panelhelper.AbstractBottomPanelHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class FrameLayoutPanelContainer extends FrameLayout implements LayoutBinder {
    private static final String TAG = "FLPanelContainer";

    public FrameLayoutPanelContainer(@NonNull Context context) {
        super(context);
    }

    public FrameLayoutPanelContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FrameLayoutPanelContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public FrameLayoutPanelContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private AbstractBottomPanelHelper bottomPanelHelper;
    @Override
    public void bindData(AbstractBottomPanelHelper bottomPanelHelper) {
        this.bottomPanelHelper = bottomPanelHelper;
    }

    @Override
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        bottomPanelHelper.onContainerLayout();
    }
}