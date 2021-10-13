package com.ruitech.bookstudy.book;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.ruitech.bookstudy.R;
import com.ruitech.bookstudy.RuiPreferenceUtil;
import com.ruitech.bookstudy.bean.LocationGroup;
import com.ruitech.bookstudy.utils.DisplayOptions;
import com.ruitech.bookstudy.utils.ImageHelper;
import com.ruitech.bookstudy.utils.UIHelper;

import androidx.annotation.Nullable;

public class CoordinateView extends View implements ImageLoadingListener {

    private static final String TAG = "CoordinateView";

    public CoordinateView(Context context) {
        super(context);
    }

    public CoordinateView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CoordinateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CoordinateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private float width;
    private float height;
    private float radius;
    private RectF bitmapRectF = new RectF();
    private Paint rectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint rectSelectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public static final int NO_ZONE = -1;
    private int clickedZone = NO_ZONE;

    {
        Context context = getContext();
        rectPaint.setColor(context.getResources().getColor(R.color._4989ff));
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setStrokeWidth(UIHelper.dp2px(1));

        rectSelectPaint.setColor(context.getResources().getColor(R.color._f2c542));
        rectSelectPaint.setStyle(Paint.Style.STROKE);
        rectSelectPaint.setStrokeWidth(UIHelper.dp2px(1));

        radius = UIHelper.dp2px(5);

        GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                Log.d(TAG, "onSingleTapUp.");
                clickedZone = locationGroup.getZoneNum(e.getX() / width, e.getY() / height);
                if (clickedZone < 0) {
                    callback.onPageMarginClick(pageNum);
                } else {
                    callback.onPageZoneClick(pageNum, clickedZone);
                    if (RuiPreferenceUtil.showClickReadTranslation()) {
                        if (!TextUtils.isEmpty(locationGroup.translationArr[clickedZone])) {
                            Toast t = Toast.makeText(context, locationGroup.translationArr[clickedZone], Toast.LENGTH_SHORT);
//                        CoordinateView.this.getLocationInWindow();
//                        int[] locationArr = new int[2];
//                        CoordinateView.this.getLocationInWindow(locationArr);
//                        if (locationArr[1] > 0) {
//                            t.setGravity(Gravity.TOP, 0, locationArr[1] - context.getResources().getDimensionPixelSize(R.dimen.status_bar_height));
//                        }
                            t.show();
                        }
                    }
                }
                invalidate();
                return true;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }
        });
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0) {
            width = w;
            height = h;
            bitmapRectF.right = w;
            bitmapRectF.bottom = h;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, null, bitmapRectF, null);
            if (RuiPreferenceUtil.showClickReadBorders()) {
                if (locationGroup != null) {
                    for (int i = 0; i < locationGroup.num; i++) {
                        canvas.drawRoundRect(
                                locationGroup.leftArr[i] * width,
                                locationGroup.topArr[i] * height,
                                locationGroup.rightArr[i] * width,
                                locationGroup.bottomArr[i] * height,
                                radius, radius, i == clickedZone ? rectSelectPaint : rectPaint);
                    }
                }
            }
        }
    }

    private String imgUri;
    private int pageNum;
    private LocationGroup locationGroup;
    private Bitmap bitmap;
    public void bindData(String imgUri) {
        this.imgUri = imgUri;
        ImageHelper.loadImage(new ImageHelper.Params.Builder()
                .imageUri(imgUri)
                .options(DisplayOptions.getIconDefault())
                .listener(this)
                .build());
    }

    public void bindData(int pageNum, LocationGroup locationGroup) {
        this.pageNum = pageNum;
        this.locationGroup = locationGroup;
        invalidate();
    }

    @Override
    public void onLoadingStarted(String imageUri, View view) {

    }

    @Override
    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

    }

    @Override
    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
        if (TextUtils.equals(imgUri, imageUri)) {
            bitmap = loadedImage;
            invalidate();
        }
    }

    @Override
    public void onLoadingCancelled(String imageUri, View view) {

    }


    private Callback callback;
    public void setCallback(Callback callback) {
        this.callback = callback;
    }
    public interface Callback {
        void onPageMarginClick(int pageNum);
        void onPageZoneClick(int pageNum, int zoneNum);
    }
}
