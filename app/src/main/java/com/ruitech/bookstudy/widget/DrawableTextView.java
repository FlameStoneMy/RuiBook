package com.ruitech.bookstudy.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.ruitech.bookstudy.R;

import androidx.appcompat.widget.AppCompatTextView;

public class DrawableTextView extends AppCompatTextView {

    public DrawableTextView(Context context) {
        this(context, null);
    }

    public DrawableTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        int[] widths = new int[4];
        int[] heights = new int[4];
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.DrawableTextView);

        boolean hasLeft = false;
        if (ta.hasValue(R.styleable.DrawableTextView_leftDrawableWidth) && ta.hasValue(R.styleable.DrawableTextView_leftDrawableHeight)) {
            widths[0] = ta.getDimensionPixelSize(R.styleable.DrawableTextView_leftDrawableWidth, 0);
            heights[0] = ta.getDimensionPixelSize(R.styleable.DrawableTextView_leftDrawableHeight, 0);
            hasLeft = true;
        }

        boolean hasTop = false;
        if (ta.hasValue(R.styleable.DrawableTextView_topDrawableWidth) && ta.hasValue(R.styleable.DrawableTextView_topDrawableHeight)) {
            widths[1] = ta.getDimensionPixelSize(R.styleable.DrawableTextView_topDrawableWidth, 0);
            heights[1] = ta.getDimensionPixelSize(R.styleable.DrawableTextView_topDrawableHeight, 0);
            hasTop = true;
        }

        boolean hasRight = false;
        if (ta.hasValue(R.styleable.DrawableTextView_rightDrawableWidth) && ta.hasValue(R.styleable.DrawableTextView_rightDrawableHeight)) {
            widths[2] = ta.getDimensionPixelSize(R.styleable.DrawableTextView_rightDrawableWidth, 0);
            heights[2] = ta.getDimensionPixelSize(R.styleable.DrawableTextView_rightDrawableHeight, 0);
            hasRight = true;
        }

        boolean hasBottom = false;
        if (ta.hasValue(R.styleable.DrawableTextView_bottomDrawableWidth) && ta.hasValue(R.styleable.DrawableTextView_bottomDrawableHeight)) {
            widths[3] = ta.getDimensionPixelSize(R.styleable.DrawableTextView_bottomDrawableWidth, 0);
            heights[3] = ta.getDimensionPixelSize(R.styleable.DrawableTextView_bottomDrawableHeight, 0);
            hasBottom = true;
        }

        if (hasLeft || hasTop || hasRight || hasBottom) {
            Drawable[] drawables = getCompoundDrawables();
            if (hasLeft && drawables[0] != null) {
                drawables[0].setBounds(0, 0, widths[0], heights[0]);
            }
            if (hasTop && drawables[1] != null) {
                drawables[1].setBounds(0, 0, widths[1], heights[1]);
            }
            if (hasRight && drawables[1] != null) {
                drawables[2].setBounds(0, 0, widths[2], heights[2]);
            }
            if (hasBottom && drawables[1] != null) {
                drawables[3].setBounds(0, 0, widths[3], heights[3]);
            }
            setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawables[3]);
        }
        ta.recycle();
    }
}
