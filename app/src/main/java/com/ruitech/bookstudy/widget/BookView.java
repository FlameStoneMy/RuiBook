package com.ruitech.bookstudy.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.ruitech.bookstudy.R;
import com.ruitech.bookstudy.bean.Book;
import com.ruitech.bookstudy.utils.DisplayOptions;
import com.ruitech.bookstudy.utils.ImageHelper;
import com.ruitech.bookstudy.utils.UIHelper;
import com.ruitech.bookstudy.utils.UIUtil;

import androidx.annotation.Nullable;

public class BookView extends View implements ImageLoadingListener {

    private static final String TAG = "BookView";

    public BookView(Context context) {
        super(context);
    }

    public BookView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public BookView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    public BookView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, attrs);
    }

    private final int MODE_DEFAULT = 0;
    private final int MODE_ADD_PENDING = 1;
    private int mode = MODE_DEFAULT;
    private void initView(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BookView);
        mode = ta.getInt(R.styleable.BookView_mode, MODE_DEFAULT);
        ta.recycle();
    }

    private Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Rect textRect = new Rect();
    private String text;

    private Paint book2Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private RectF book2Rect = new RectF();
    private float book2Round = UIHelper.dp2px(17);
    private Paint book1Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private RectF book1Rect = new RectF();
    private float book1Round = UIHelper.dp2px(13);
    private Paint bookPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint bookPaintDefault = new Paint(Paint.ANTI_ALIAS_FLAG);
    private RectF bookRect = new RectF();
    private Path bookPath = new Path();
    private Path shadowPath = new Path();

    private Paint loadingCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint loadingBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    {
        Context context = getContext();

        text = context.getString(R.string.add_pending);
        textPaint.setTextSize(UIHelper.sp2px(13));
        textPaint.setColor(context.getResources().getColor(R.color._b8d1ff));
        textPaint.getTextBounds(text, 0, text.length(), textRect);

        book2Paint.setColor(context.getResources().getColor(R.color._ededed));
        book1Paint.setColor(context.getResources().getColor(R.color._f4f4f5));
        bookPaint.setColor(context.getResources().getColor(R.color._edf7ff));

        bookPaintDefault.setColor(context.getResources().getColor(R.color._e5e5e5));

        loadingCirclePaint.setColor(context.getResources().getColor(R.color._fe602e));
        loadingBgPaint.setColor(context.getResources().getColor(R.color._1a000000));
    }

    private float textX, textY;
    private float loadingCenterX, loadingCenterY;
    private float loadingRadius;
    private RectF loadingRectF = new RectF(), tmpLoadingRectF = new RectF();
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (w > 0 && h > 0) {
            book2Rect.right = w;
            book1Rect.right = w - UIHelper.dp2px(2);
            bookRect.right = w - UIHelper.dp2px(4);
            bookRect.bottom = book1Rect.bottom = book2Rect.bottom = h;

            textX = (bookRect.right - textRect.width()) / 2;
            textY = (bookRect.bottom + textRect.height()) / 2;
            bookPath.addRoundRect(bookRect, book1Round, book1Round, Path.Direction.CW);

            loadingCenterX = bookRect.right / 2;
            loadingCenterY = bookRect.bottom / 2;
            loadingRadius = bookRect.width() * 16 / 80;
            float loadingBitmapRadius = loadingRadius * 11 / 16;
            loadingRectF.left = loadingCenterX - loadingBitmapRadius;
            loadingRectF.right = loadingCenterX + loadingBitmapRadius;
            loadingRectF.top = loadingCenterY - loadingBitmapRadius;
            loadingRectF.bottom = loadingCenterY + loadingBitmapRadius;
            Log.d(TAG, "onSizeChanged: " + bookRect.bottom + " " + textRect);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        int height = measuredWidth * 127 / 103;
        setMeasuredDimension(measuredWidth, height);
    }

    private long loadingStartTs = -1L;
    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawRoundRect(book2Rect, book2Round, book2Round, book2Paint);
        canvas.drawRoundRect(book1Rect, book1Round, book1Round, book1Paint);

        if (bitmap == null) {
            if (mode == MODE_ADD_PENDING) {
                canvas.drawRoundRect(bookRect, book1Round, book1Round, bookPaint);
                canvas.drawText(text, textX, textY, textPaint);
            } else {
                canvas.drawRoundRect(bookRect, book1Round, book1Round, bookPaintDefault);
            }
        } else {
            canvas.save();
            canvas.clipPath(bookPath);
            canvas.drawBitmap(bitmap, null, bookRect, null);
            canvas.restore();
        }

        if (loading) {
            long currTs = System.currentTimeMillis();
            if (loadingStartTs < 0) {
                loadingStartTs = currTs;
            }

            long curr = (currTs - LOADING_ANIM_DURATION / 4 - loadingStartTs) % LOADING_ANIM_DURATION;
            float y, deltaY;

            if (curr < LOADING_ANIM_DURATION / 2) {
                deltaY = - loadingRadius / 2 + loadingRadius * curr / LOADING_ANIM_DURATION / 2;
            } else {
                deltaY = loadingRadius / 2 - loadingRadius * curr / LOADING_ANIM_DURATION / 2;
            }
            y = loadingCenterY + deltaY;
            adjustTmpLoadingRectF(deltaY);

            canvas.drawPath(bookPath, loadingBgPaint);
            canvas.drawCircle(loadingCenterX, y, loadingRadius, loadingCirclePaint);
            ensureLoadingBitmap();
            canvas.drawBitmap(loadingBitmap, null, tmpLoadingRectF, null);

            invalidate();
        }
//        canvas.clipPath();
//        canvas.drawRoundRect();
    }

    private void adjustTmpLoadingRectF(float offsetY) {
        tmpLoadingRectF.left = loadingRectF.left;
        tmpLoadingRectF.right = loadingRectF.right;
        tmpLoadingRectF.top = loadingRectF.top;
        tmpLoadingRectF.bottom = loadingRectF.bottom;

        tmpLoadingRectF.offset(0, offsetY);
    }

    private static final long LOADING_ANIM_DURATION = 1000L;

    private Book book;
    private String posterUri;
    private Bitmap bitmap;
    private Bitmap loadingBitmap;
    private void ensureLoadingBitmap() {
        if (loadingBitmap == null) {
            loadingBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.downloading);
        }
    }

    public void bindData(Book book) {
        this.book = book;
        if (book != null) {
            posterUri = UIUtil.getAppropriatePosterUrl(book.getPosterList(), 0, 0, true);
            ImageHelper.loadImage(
                    new ImageHelper.Params.Builder()
                            .imageUri(posterUri)
                            .width(0)
                            .height(0)
                            .options(DisplayOptions.getIconDefault())
                            .listener(this)
                            .build());
        } else {
            posterUri = null;
            bitmap = null;
            invalidate();
        }
    }

    @Override
    public void onLoadingStarted(String imageUri, View view) {

    }

    @Override
    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
        bitmap = null;
        invalidate();
    }

    @Override
    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
        if (TextUtils.equals(imageUri, posterUri)) {
            bitmap = loadedImage;
            invalidate();
        }
    }

    @Override
    public void onLoadingCancelled(String imageUri, View view) {
        bitmap = null;
        invalidate();
    }

    private boolean loading;
    public void setLoading(boolean loading) {
        this.loading = loading;
        invalidate();
    }
}
