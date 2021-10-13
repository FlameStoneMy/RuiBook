package com.ruitech.bookstudy.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import com.ruitech.bookstudy.R;
import com.ruitech.bookstudy.utils.DeviceUtil;

import androidx.annotation.Nullable;

public class BgView extends View {
    private static final String TAG = "BgView";

    public BgView(Context context) {
        super(context);
    }

    public BgView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BgView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BgView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private static final int BUBBLE_NUM = 4;
    private Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int width, height, windowHeight;
    private Bitmap[] bitmapArr = new Bitmap[BUBBLE_NUM];
    private float[] xArr = new float[BUBBLE_NUM];
    private float[] yArr = new float[BUBBLE_NUM];
    private float[] rArr = new float[BUBBLE_NUM];
    private RectF[] rectArr = new RectF[BUBBLE_NUM];
    private Paint[] bubblePaintArr = new Paint[BUBBLE_NUM];

    {
        Resources resources = getContext().getResources();
        bitmapArr[0] = BitmapFactory.decodeResource(resources, R.mipmap.bg_video);
        bitmapArr[1] = BitmapFactory.decodeResource(resources, R.mipmap.bg_brush);
        bitmapArr[2] = BitmapFactory.decodeResource(resources, R.mipmap.bg_music);
        bitmapArr[3] = BitmapFactory.decodeResource(resources, R.mipmap.bg_english);

        for (int i = 0; i < bubblePaintArr.length; i++) {
            bubblePaintArr[i] = new Paint(Paint.ANTI_ALIAS_FLAG);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (w > 0 && h > 0) {
            width = w;
            height = h;

            DeviceUtil.updateFullScreenHeightIfNeeded(height);
            windowHeight = DeviceUtil.getFullScreenHeight();
            LinearGradient gradient = new LinearGradient(0, 0, 0, windowHeight,
                    new int[] {
                            getResources().getColor(R.color._4b8afe),
                            getResources().getColor(R.color._6ce0ff)},
                    new float[] {0, 1}, Shader.TileMode.CLAMP);
            bgPaint.setShader(gradient);

            xArr[0] = width * 0.896F;
            yArr[0] = windowHeight * 0.05F;
            rArr[0] = width / 2 * 0.408F;

            xArr[1] = width * 0.12F;
            yArr[1] = windowHeight * 0.12F;
            rArr[1] = width / 2 * 0.221F;

            xArr[2] = width * 0.96F;
            yArr[2] = windowHeight * 0.27F;
            rArr[2] = width / 2 * 0.181F;

            xArr[3] = width * 0.2F;
            yArr[3] = windowHeight * 0.37F;
            rArr[3] = width / 2 * 0.165F;


            for (int i = 0; i < BUBBLE_NUM; i++) {
                float r = 0.35F;
                rectArr[i] = new RectF(
                        xArr[i] - rArr[i] * r,
                        yArr[i] - rArr[i] * r,
                        xArr[i] + rArr[i] * r,
                        yArr[i] + rArr[i] * r
                );

                bubblePaintArr[i].setShader(new LinearGradient(0, yArr[i] - rArr[i], 0, yArr[i] + rArr[i],
                        new int[] {
                                getResources().getColor(R.color._33ffffff),
//                                getResources().getColor(R.color._66ffffff)
                                getResources().getColor(android.R.color.transparent)
                        },
                        new float[] {0, 1}, Shader.TileMode.CLAMP));
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(0,0, width, windowHeight, bgPaint);

        for (int i = 0; i < BUBBLE_NUM; i++) {
            canvas.drawCircle(xArr[i], yArr[i], rArr[i], bubblePaintArr[i]);
            canvas.drawBitmap(bitmapArr[i], null, rectArr[i], null);
        }
    }
}
