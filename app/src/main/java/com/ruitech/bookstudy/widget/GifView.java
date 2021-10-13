package com.ruitech.bookstudy.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.ruitech.bookstudy.R;

import java.io.InputStream;

public class GifView extends View {

    private Movie mMovie;
    private long mStartTime;
    private long mPauseTime;
    private boolean mIsLoop;
    private boolean mIsStart;

    private int mLeft;
    private int mTop;
    private float mScaleX;
    private float mScaleY;
    private ImageView.ScaleType mScaleType = ImageView.ScaleType.CENTER_CROP;
    private Runnable mCalcRunnable = new Runnable() {
        @Override
        public void run() {
            calcScale();
            calcLocation();
        }
    };


    public GifView(Context context) {
        this(context, null);
    }

    public GifView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GifView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initAttrs(attrs);

    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = getResources().obtainAttributes(attrs, R.styleable.GifView);

        mIsLoop = typedArray.getBoolean(R.styleable.GifView_gifLoop, true);
        int id = typedArray.getResourceId(R.styleable.GifView_gifSrc, -1);
        if (id != -1) {
            setSource(id);
        }

        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = 0;
        int height = 0;
        if (mMovie != null) {
            int wMode = MeasureSpec.getMode(widthMeasureSpec);
            int hMode = MeasureSpec.getMode(heightMeasureSpec);
            int wSize = MeasureSpec.getSize(widthMeasureSpec);
            int hSize = MeasureSpec.getSize(heightMeasureSpec);

            if (wMode == MeasureSpec.EXACTLY) {
                width = wSize;
            } else {
                width = mMovie.width();
            }

            if (hMode == MeasureSpec.EXACTLY) {
                height = hSize;
            } else {
                height = mMovie.height();
            }

        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mMovie == null) {
            return;
        }

        long now = SystemClock.uptimeMillis();
        int currentTime = (int) (now - mStartTime);

        if (currentTime >= mMovie.duration()) {
            if (mIsLoop) {
                mStartTime = SystemClock.uptimeMillis();
                currentTime = 0;
                mIsStart = true;
            } else if (mIsStart) {
                currentTime = mMovie.duration();
                mIsStart = false;
            }
        }

        mMovie.setTime(currentTime);
        canvas.save();
        canvas.scale(mScaleX, mScaleY);
        mMovie.draw(canvas, mLeft / mScaleX, mTop / mScaleY);
        canvas.restore();
        if (mIsStart) {
            postInvalidate();
        }
    }


    public void pause() {
        if (!mIsStart) {
            return;
        }
        mIsStart = false;
        // 记录播放的位置
        mPauseTime = SystemClock.uptimeMillis() - mStartTime;
        postInvalidate();
    }


    public void resume() {
        if (mIsStart) {
            return;
        }
        mIsStart = true;
        // 恢复到播放的相对位置
        mStartTime = SystemClock.uptimeMillis() - mPauseTime;
        postInvalidate();
    }


    /**
     * 获取当前播放的帧
     *
     * @return
     */
    public Bitmap getCurrentFrame() {
        if (mMovie == null) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(mMovie.width(), mMovie.height(), Bitmap.Config.RGB_565);

        Canvas canvas = new Canvas(bitmap);

        canvas.scale(mScaleX, mScaleY);

        mMovie.draw(canvas, mLeft, mTop);

        return bitmap;

    }

    public Movie getMovie() {
        return mMovie;
    }


    public void setMovie(Movie movie) {
        mMovie = movie;
        mStartTime = SystemClock.uptimeMillis();
        mIsStart = true;
        if (getMeasuredHeight() == 0 || getMeasuredWidth() == 0) {
            post(mCalcRunnable);
        } else {
            mCalcRunnable.run();
        }
        requestLayout();
        postInvalidate();
    }

    public void setSource(int id) {
        setSource(getResources().openRawResource(id));
    }

    public void setSource(byte[] bytes, int start, int len) {
        setMovie(Movie.decodeByteArray(bytes, start, len));
    }

    public void setSource(InputStream inputStream) {
        setMovie(Movie.decodeStream(inputStream));
    }

    public void setSource(String pathName) {
        setMovie(Movie.decodeFile(pathName));
    }

    public void setLoop(boolean loop) {
        mIsLoop = loop;
        postInvalidate();
    }

    public void setScaleType(ImageView.ScaleType scaleType) {
        if (scaleType == ImageView.ScaleType.MATRIX) {
            throw new UnsupportedOperationException("不支持MATRIX类型缩放");
        }
        this.mScaleType = scaleType;
        if (getMeasuredHeight() == 0 || getMeasuredWidth() == 0) {
            post(mCalcRunnable);
        } else {
            mCalcRunnable.run();
        }
    }

    private void calcScale() {
        if (mMovie == null) {
            return;
        }
        float imageW = mMovie.width();
        float imageH = mMovie.height();
        float viewW = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        float viewH = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();

        if (mScaleType == ImageView.ScaleType.FIT_XY) {
            mScaleX = viewW / imageW;
            mScaleY = viewH / imageH;
        } else if (mScaleType == ImageView.ScaleType.FIT_START
                || mScaleType == ImageView.ScaleType.FIT_CENTER
                || mScaleType == ImageView.ScaleType.FIT_END) {
            mScaleY = mScaleX = viewH / imageH;
        } else if (mScaleType == ImageView.ScaleType.CENTER) {
            mScaleX = mScaleY = 1;
        } else if (mScaleType == ImageView.ScaleType.CENTER_CROP) {
            mScaleX = viewW / imageW;
            mScaleY = viewH / imageH;
            mScaleX = mScaleY = Math.max(mScaleX, mScaleY);
        } else if (mScaleType == ImageView.ScaleType.CENTER_INSIDE) {
            mScaleX = viewW / imageW;
            mScaleY = viewH / imageH;
            mScaleX = mScaleY = Math.min(mScaleX, mScaleY);
        }
    }

    private void calcLocation() {
        if (mMovie == null) {
            return;
        }
        int imageW = mMovie.width();
        int imageH = mMovie.height();
        int viewW = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int viewH = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        int left = getPaddingLeft();
        int top = getPaddingTop();

        if (mScaleType == ImageView.ScaleType.FIT_XY) {
            mLeft = left;
            mTop = top;
        } else if (mScaleType == ImageView.ScaleType.FIT_START) {
            mLeft = left;
            mTop = top;
        } else if (mScaleType == ImageView.ScaleType.FIT_CENTER) {
            mLeft = (int) (left + (viewW - imageW * mScaleX) / 2);
            mTop = top;
        } else if (mScaleType == ImageView.ScaleType.FIT_END) {
            mLeft = (int) (left + viewW - imageW * mScaleX);
            mTop = top;
        } else if (mScaleType == ImageView.ScaleType.CENTER) {
            mLeft = -(imageW - viewW) / 2;
            mTop = -(imageH - viewH) / 2;
        } else if (mScaleType == ImageView.ScaleType.CENTER_CROP) {
            mLeft = (int) -(Math.abs(viewW - imageW * mScaleX) / 2);
            mTop = (int) -(Math.abs(viewH - imageH * mScaleY) / 2);
        } else if (mScaleType == ImageView.ScaleType.CENTER_INSIDE) {
            mLeft = (int) (left + (viewW - imageW * mScaleX) / 2);
            mTop = (int) (top + (viewH - imageH * mScaleY) / 2);
        }
    }
}