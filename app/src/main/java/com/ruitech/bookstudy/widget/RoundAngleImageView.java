package com.ruitech.bookstudy.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;


import com.ruitech.bookstudy.R;

import androidx.annotation.Nullable;

@SuppressLint("AppCompatCustomView")
public class RoundAngleImageView extends ImageView {

    private int radius;
    private int orientation;
    private int width;
    private int height;
    private Path path;

    public RoundAngleImageView(Context context) {
        this(context, null);
    }

    public RoundAngleImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundAngleImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (Build.VERSION.SDK_INT < 18) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RoundAngleImageView);
        int defaultRadius = 0;
        int defaultOrientation = 0;
        radius = ta.getDimensionPixelOffset(R.styleable.RoundAngleImageView_radius, defaultRadius);
        orientation = ta.getInt(R.styleable.RoundAngleImageView_round_angle_orientation, defaultOrientation);
        ta.recycle();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = getWidth();
        height = getHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (width >= 2*radius && height >= 2*radius) {
            if (path == null) {
                path = new Path();
            }
            path.moveTo(radius, 0);

            path.lineTo(width - radius, 0);
            path.quadTo(width, 0, width, radius);

            path.lineTo(width, height - radius);
            path.quadTo(width, height, width - radius, height);

            path.lineTo(radius, height);
            path.quadTo(0, height, 0, height - radius);

            path.lineTo(0, radius);
            path.quadTo(0, 0, radius, 0);

            canvas.clipPath(path);
        } else if (width >= 2*radius) {
            if (path == null) {
                path = new Path();
            }
            if (orientation == 2) {
                path.moveTo(radius, 0);

                path.lineTo(width - radius, 0);
                path.quadTo(width, 0, width, radius);

                path.lineTo(width, height);
                path.lineTo(0, height);

                path.lineTo(0, radius);
                path.quadTo(0, 0, radius, 0);

            } else if (orientation == 4) {
                path.moveTo(0, 0);

                path.lineTo(width, 0);
                path.lineTo(width, height - radius);
                path.quadTo(width, height, width - radius, height);

                path.lineTo(radius, height);
                path.quadTo(0, height, 0, height - radius);

                path.lineTo(0, 0);
            }
            canvas.clipPath(path);
        } else if (height >= 2*radius) {
            if (path == null) {
                path = new Path();
            }

            if (orientation == 1) {
                path.moveTo(radius, 0);

                path.lineTo(width, 0);
                path.lineTo(width, height);
                path.lineTo(radius, height);
                path.quadTo(0, height, 0, height - radius);

                path.lineTo(0, radius);
                path.quadTo(0, 0, radius, 0);

            } else if (orientation == 3) {
                path.moveTo(0, 0);
                path.lineTo(width - radius, 0);
                path.quadTo(width, 0, width, radius);

                path.lineTo(width, height - radius);
                path.quadTo(width, height, width - radius, height);

                path.lineTo(0, height);
                path.lineTo(0, 0);
            }
            canvas.clipPath(path);
        }
        super.onDraw(canvas);
    }

}
