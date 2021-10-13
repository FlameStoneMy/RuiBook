package com.ruitech.bookstudy.homepage;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import com.ruitech.bookstudy.R;
import com.ruitech.bookstudy.utils.UIHelper;

import androidx.annotation.Nullable;

public class HomePageBgView extends View {
    public HomePageBgView(Context context) {
        super(context);
    }

    public HomePageBgView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HomePageBgView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public HomePageBgView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private int width, height;
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (w > 0 && h > 0) {
            width = w;
            height = h;
            LinearGradient gradient = new LinearGradient(0, 0, 0, h,
                    new int[] {
                            getResources().getColor(R.color._4b8afe),
                            getResources().getColor(R.color._6ce0ff)},
                    new float[] {0, 1}, Shader.TileMode.CLAMP);
            bgPaint.setShader(gradient);

            path.reset();
            int dist = UIHelper.dp2px(10);
            path.rLineTo(0, h - dist);
            path.arcTo(0, h - 2 * dist, w, h, 180, -180, false);
            path.rLineTo(0, dist - h);
            path.rLineTo(0, 0);
        }
    }

    private Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Path path = new Path();
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.clipPath(path);
        canvas.drawRect(0,0, width, height, bgPaint);
    }
}
