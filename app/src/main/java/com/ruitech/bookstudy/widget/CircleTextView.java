package com.ruitech.bookstudy.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.AttributeSet;
import android.view.Gravity;

import com.ruitech.bookstudy.R;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

public class CircleTextView extends AppCompatTextView {
    public CircleTextView(@NonNull Context context) {
        super(context);
    }

    public CircleTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttr(context, attrs);
    }

    public CircleTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
    }
    {
        setGravity(Gravity.CENTER);
    }
    private ShapeDrawable bgDrawable;
    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CircleTextView);

        if (ta.hasValue(R.styleable.CircleTextView_circleBgColor)) {
            int color = ta.getColor(R.styleable.CircleTextView_circleBgColor, 0);
            ensureBgDrawable();
            bgDrawable.getPaint().setColor(color);
        }

        ta.recycle();
    }

    private void ensureBgDrawable() {
        if (bgDrawable == null) {
            bgDrawable = new ShapeDrawable(new OvalShape());
        }
        setBackground(bgDrawable);
    }

    public void setBgColor(@ColorInt int color) {
        ensureBgDrawable();
        bgDrawable.getPaint().setColor(color);
    }
}
