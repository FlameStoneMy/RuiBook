package com.ruitech.bookstudy.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

public class RuiConstraintLayout extends ConstraintLayout {
    public RuiConstraintLayout(@NonNull Context context) {
        super(context);
    }

    public RuiConstraintLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RuiConstraintLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RuiConstraintLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private int width, height;
    private Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (w > 0 && h > 0) {
            width = w;
            height = h;
            LinearGradient linearGradient = new LinearGradient(w, 0, 0.618F * w, h,
                    new int[] {
                        0xff5120EC, 0xff0015AB
                    }, new float[] {0,1}, Shader.TileMode.CLAMP);
            bgPaint.setShader(linearGradient);
        }
    }


    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (width > 0 && height > 0) {
            canvas.drawRect(0, 0, width, height, bgPaint);
        }
    }
}
