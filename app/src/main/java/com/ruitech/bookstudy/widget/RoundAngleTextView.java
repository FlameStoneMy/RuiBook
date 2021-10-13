package com.ruitech.bookstudy.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;

import com.ruitech.bookstudy.R;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class RoundAngleTextView extends DrawableTextView {
    public RoundAngleTextView(@NonNull Context context) {
        super(context);
    }

    public RoundAngleTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttr(context, attrs);
    }

    public RoundAngleTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
    }
    private GradientDrawable bgDrawable;
    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RoundAngleTextView);

        bgDrawable = new GradientDrawable();
        setBackground(bgDrawable);
        if (ta.hasValue(R.styleable.RoundAngleTextView_bgRadius)) {
            bgDrawable.setCornerRadius(ta.getDimensionPixelOffset(R.styleable.RoundAngleTextView_bgRadius, 0));
        }

        if (ta.hasValue(R.styleable.RoundAngleTextView_bgColor)) {
            int color = ta.getColor(R.styleable.RoundAngleTextView_bgColor, 0);
            setBgColor(color);
        }

        if (ta.hasValue(R.styleable.RoundAngleTextView_strokeColor) && ta.hasValue(R.styleable.RoundAngleTextView_strokeWidth)) {
            int strokeWidth = ta.getDimensionPixelOffset(R.styleable.RoundAngleTextView_strokeWidth, 0);
            int strokeColor = ta.getColor(R.styleable.RoundAngleTextView_strokeColor, 0);
            bgDrawable.setStroke(strokeWidth, strokeColor);
        }

        ta.recycle();
    }

    public void setBgColor(@ColorInt int color) {
        bgDrawable.setColor(color);
    }
}
