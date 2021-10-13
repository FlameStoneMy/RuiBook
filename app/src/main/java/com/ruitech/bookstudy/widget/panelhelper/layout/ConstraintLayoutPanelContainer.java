package com.ruitech.bookstudy.widget.panelhelper.layout;

import android.content.Context;
import android.util.AttributeSet;


import com.ruitech.bookstudy.widget.panelhelper.AbstractBottomPanelHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

public class ConstraintLayoutPanelContainer extends ConstraintLayout implements LayoutBinder {
    private static final String TAG = "CLPanelContainer";

    public ConstraintLayoutPanelContainer(@NonNull Context context) {
        super(context);
    }

    public ConstraintLayoutPanelContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ConstraintLayoutPanelContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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