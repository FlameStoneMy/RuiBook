package com.ruitech.bookstudy.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;
import androidx.viewpager2.widget.ViewPager2;
import me.drakeet.multitype.MultiTypeAdapter;

import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

//import com.nineoldandroids.animation.Animator;
//import com.nineoldandroids.animation.Animator.AnimatorListener;
//import com.nineoldandroids.animation.ObjectAnimator;
//import mobi.infolife.weather.widget.anfield.R;

import com.ruitech.bookstudy.BodyType;
import com.ruitech.bookstudy.ITitleProvider;
import com.ruitech.bookstudy.R;

import java.util.List;
import java.util.Locale;

public class TabPageIndicator extends HorizontalScrollView {

    public interface IconTabProvider {
        public int getPageIconResId(int position);
    }

    public interface TitleIconTabProvider {
        public final static int NONE_ICON = -1;

        public int getPageIconResId(int position);
    }

    // @formatter:off
    private static final int[] ATTRS = new int[]{android.R.attr.textSize, android.R.attr.textColor};
    // @formatter:on

    private final LinearLayout.LayoutParams defaultTabLayoutParams;
    private final LinearLayout.LayoutParams expandedTabLayoutParams;

    public OnPageChangeListener delegatePageListener;

    private final LinearLayout tabsContainer;
    private ViewPager2 pager;

    private int tabCount;

    private int currentPosition = 0;
    private float currentPositionOffset = 0f;

    private final Paint rectPaint;
    private final Paint dividerPaint;

    private int indicatorColor = 0xFAFAFAFA;//0xFF3F9FE0;;
    private int underlineColor = 0x00000000;
    private int dividerColor = 0x1A000000;

    //ripple效果开关
    private boolean rippleEnable = true;

    private boolean shouldExpand = true;
    /**
     * 所有的字符大写
     */
    private boolean textAllCaps = true;

    private int scrollOffset = 52;
    private int indicatorHeight = 2;
    private int underlineHeight = 2;
    private int dividerPadding = 12;
    private int tabPadding = 10;
    private int dividerWidth = 1;

    private int tabTextSize = 14;
    private int tabTextColor = 0xFFFFFFFF;//0.54透明度
    private int tabTextColorHide = 0x86FFFFFF;//Color.GREEN;
    private Typeface tabTypeface = null;
    private int tabTypefaceStyle = Typeface.BOLD;

    private int lastScrollX = 0;

    private int tabBackgroundDefultResId = R.drawable.selector_actionbar_btn_bg;

    private final boolean isEnableSelectCurrent = true;
    /**
     * 是否展示divider
     */
    private boolean isShowdivider = false;

    /**
     * tab的点击事件的监听
     */
    private OnTabItemClickListener onTabItemClickListener;

    /**
     * 点击的时候 view是否有平滑的切换
     */
    private boolean isTabSmoothScroll = false;

    /**
     * 里面的子view是否能点击
     */
    private boolean isCanClick = true;

    private Locale locale;

    /**
     * 上面的tab的点击事件listener
     */
    public interface OnTabItemClickListener {
        boolean onItemTabClick(int position, View view);
    }

    public TabPageIndicator(Context context) {
        this(context, null);
    }

    public TabPageIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabPageIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setFillViewport(true);
        setWillNotDraw(false);

        tabsContainer = new LinearLayout(context);
        tabsContainer.setOrientation(LinearLayout.HORIZONTAL);
        tabsContainer.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT));
        addView(tabsContainer);

        DisplayMetrics dm = getResources().getDisplayMetrics();

        scrollOffset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, scrollOffset, dm);
        indicatorHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, indicatorHeight, dm);
        underlineHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, underlineHeight, dm);
        dividerPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerPadding, dm);
        tabPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, tabPadding, dm);
        dividerWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerWidth, dm);
        tabTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, tabTextSize, dm);

        // get system attrs (android:textSize and android:textColor)

        TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);

//        tabTextSize = a.getDimensionPixelSize(0, tabTextSize);
//        tabTextColor = a.getColor(1, tabTextColor);

        a.recycle();

        // get custom attrs

        a = context.obtainStyledAttributes(attrs, R.styleable.TabPageIndicator);

        indicatorColor = a.getColor(R.styleable.TabPageIndicator_pstsIndicatorColor, indicatorColor);
        underlineColor = a.getColor(R.styleable.TabPageIndicator_pstsUnderlineColor, underlineColor);
        dividerColor = a.getColor(R.styleable.TabPageIndicator_pstsDividerColor, dividerColor);
        indicatorHeight = a.getDimensionPixelSize(R.styleable.TabPageIndicator_pstsIndicatorHeight, indicatorHeight);
        underlineHeight = a.getDimensionPixelSize(R.styleable.TabPageIndicator_pstsUnderlineHeight, underlineHeight);
        dividerPadding = a.getDimensionPixelSize(R.styleable.TabPageIndicator_pstsDividerPadding, dividerPadding);
        tabPadding = a.getDimensionPixelSize(R.styleable.TabPageIndicator_pstsTabPaddingLeftRight, tabPadding);
        tabBackgroundDefultResId = a.getResourceId(R.styleable.TabPageIndicator_pstsTabBackground,
                tabBackgroundDefultResId);
        shouldExpand = a.getBoolean(R.styleable.TabPageIndicator_pstsShouldExpand, shouldExpand);
        scrollOffset = a.getDimensionPixelSize(R.styleable.TabPageIndicator_pstsScrollOffset, scrollOffset);
        textAllCaps = a.getBoolean(R.styleable.TabPageIndicator_pstsTextAllCaps, textAllCaps);
        isShowdivider = a.getBoolean(R.styleable.TabPageIndicator_pstsIsShowdivider, isShowdivider);

        tabTextSize = a.getDimensionPixelSize(R.styleable.TabPageIndicator_pstsTextSize, tabTextSize);
//        tabTextSizeHide = a.getDimensionPixelSize(R.styleable.TabPageIndicator_pstsTextHideSize, tabTextSize);
        tabTextColor = a.getColor(R.styleable.TabPageIndicator_pstsTextColor, tabTextColor);
        tabTextColorHide = a.getColor(R.styleable.TabPageIndicator_pstsTextHideColor, tabTextColor);

        a.recycle();

        rectPaint = new Paint();
        rectPaint.setAntiAlias(true);
        rectPaint.setStyle(Style.FILL);

        dividerPaint = new Paint();
        dividerPaint.setAntiAlias(true);
        dividerPaint.setStrokeWidth(dividerWidth);

        defaultTabLayoutParams = new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT);
        expandedTabLayoutParams = new LinearLayout.LayoutParams(0, android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                1.0f);

        if (locale == null) {
            locale = getResources().getConfiguration().locale;
        }
    }

    private MultiTypeAdapter adapter;
    public void setViewPager(ViewPager2 view, TabPagerViewPagerObserver listener, int initialPosition) {
        if (view == null || listener == null) {
            return;
        }
        if (!(view.getAdapter() instanceof MultiTypeAdapter)) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }
        adapter = (MultiTypeAdapter) view.getAdapter();
        pager = view;
        view.registerOnPageChangeCallback(listener);
        listener.addListener(onPageChangeCallback);
        view.setCurrentItem(initialPosition, false);
        setCurrentItem(initialPosition);
        notifyDataSetChanged();
    }

    public void setCurrentItem(int item) {
        if (pager == null) {
            throw new IllegalStateException("ViewPager has not been bound.");
        }

        if (isEnableSelectCurrent) {
            //TODO
        }
    }

    /**
     * 更新Tab的文字颜色
     *
     * @param position
     */
    private void updateTabTextColor(int position) {
        for (int i = 0; i < tabCount; i++) {
            View v = tabsContainer.getChildAt(i);
            if (v instanceof LinearLayout) {
                v = ((LinearLayout) v).getChildAt(0);
            }

            if (v instanceof TextView) {
                TextView tab = (TextView) v;
                if (i == position) {
                    tab.setTextColor(tabTextColor);
                } else {
                    tab.setTextColor(tabTextColorHide);
                }
            }

            //添加外面点击ripple效果
            if (v instanceof FrameLayout) {
                FrameLayout tab = (FrameLayout) v;
                TextView textview = (TextView) tab.getChildAt(0);
                if (i == position) {
                    textview.setTextColor(tabTextColor);
                } else {
                    textview.setTextColor(tabTextColorHide);
                }
                // pre-ICS-build
                if (textAllCaps) {
                    textview.setText(textview.getText().toString().toUpperCase(locale));
                }
            }
        }
    }

    private ViewPager2.OnPageChangeCallback onPageChangeCallback = new ViewPager2.OnPageChangeCallback() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            currentPosition = position;
            currentPositionOffset = positionOffset;
            scrollToChild(position, (int) (positionOffset * tabsContainer.getChildAt(position).getWidth()));
            invalidate();
            if (delegatePageListener != null) {
                delegatePageListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager2.SCROLL_STATE_IDLE) {
                scrollToChild(pager.getCurrentItem(), 0);
            }

            if (delegatePageListener != null) {
                delegatePageListener.onPageScrollStateChanged(state);
            }
        }

        @Override
        public void onPageSelected(int position) {
            if (delegatePageListener != null) {
                delegatePageListener.onPageSelected(position);
            }
            updateTabTextColor(position);
        }
    };

    public void setOnPageChangeListener(OnPageChangeListener listener) {
        delegatePageListener = listener;
    }

    public void notifyDataSetChanged() {

        tabsContainer.removeAllViews();

        List itemList = adapter.getItems();
        tabCount = itemList.size();

        for (int i = 0; i < tabCount; i++) {

            ITitleProvider titleProvider = (ITitleProvider) itemList.get(i);
            String title = titleProvider.getTitle(getContext());
            if (pager.getAdapter() instanceof TitleIconTabProvider) {
                addTextIconTab(i, title,
                        ((TitleIconTabProvider) pager.getAdapter()).getPageIconResId(i));
            } else if (pager.getAdapter() instanceof IconTabProvider) {
                addIconTab(i, ((IconTabProvider) pager.getAdapter()).getPageIconResId(i));
            } else {
                addTextTab(i, title);
            }

        }

        updateTabStyles();

        getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                //                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                //                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                //                } else {
                //                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                //                }
                getViewTreeObserver().removeGlobalOnLayoutListener(this);
                currentPosition = pager.getCurrentItem();
                scrollToChild(currentPosition, 0);
            }
        });

    }

    private void addTextIconTab(final int position, String title, int resId) {

        if (resId == TitleIconTabProvider.NONE_ICON) {
            addTextTab(position, title);
            return;
        }

        TextView tab = new TextView(getContext());
        tab.setText(title);
        tab.setGravity(Gravity.CENTER);
        tab.setSingleLine();

        ImageView icon = new ImageView(getContext());
        icon.setImageResource(resId);

        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        linearLayout.addView(tab, defaultTabLayoutParams);
        linearLayout.addView(icon, defaultTabLayoutParams);

        addTab(position, linearLayout);
    }

    TextView[] tabs = new TextView[4];

    private void addTextTab(final int position, String title) {
        TextView tab = new TextView(getContext());
        if (rippleEnable) {
//            MaterialRippleLayout viewgroup = new MaterialRippleLayout(getContext());
            FrameLayout viewgroup = new FrameLayout(getContext());
//            viewgroup.setAvoidTwiceTouch(true);
            LayoutParams params = new LayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT);

            tab.setText(title);
            tab.setGravity(Gravity.CENTER);
            tab.setSingleLine();
            viewgroup.addView(tab, params);
            addTab(position, viewgroup);
        } else {
            tab.setText(title);
            tab.setGravity(Gravity.CENTER);
            tab.setSingleLine();
            addTab(position, tab);
        }
        tabs[position] = tab;
    }

    private void addIconTab(final int position, int resId) {

        ImageButton tab = new ImageButton(getContext());
        tab.setImageResource(resId);
        addTab(position, tab);

    }

    private void addTab(final int position, View tab) {
        tab.setFocusable(true);
        tab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTabSmoothScroll) {
                    pager.setCurrentItem(position, true);
                } else {
                    pager.setCurrentItem(position, false);
                }
                if (onTabItemClickListener != null) {
                    onTabItemClickListener.onItemTabClick(position, v);
                }
            }
        });

        tab.setPadding(tabPadding, 0, tabPadding, 0);
        tabsContainer.addView(tab, position, shouldExpand ? expandedTabLayoutParams : defaultTabLayoutParams);
    }

    private void updateTabStyles() {

        for (int i = 0; i < tabCount; i++) {

            View v = tabsContainer.getChildAt(i);

            v.setBackgroundResource(tabBackgroundDefultResId);

            if (v instanceof LinearLayout) {
                v = ((LinearLayout) v).getChildAt(0);
            }

            if (v instanceof TextView) {

                TextView tab = (TextView) v;
                tab.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabTextSize);
                tab.setTypeface(tabTypeface, tabTypefaceStyle);
                tab.setTextColor(tabTextColor);

                // pre-ICS-build
                if (textAllCaps) {
                    tab.setText(tab.getText().toString().toUpperCase(locale));
                }
            }
            //添加外面点击ripple效果
            if (v instanceof FrameLayout) {
                FrameLayout tab = (FrameLayout) v;
                TextView textview = (TextView) tab.getChildAt(0);
                textview.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabTextSize);
                textview.setTypeface(tabTypeface, tabTypefaceStyle);
                textview.setTextColor(tabTextColor);

                // pre-ICS-build
                if (textAllCaps) {
                    textview.setText(textview.getText().toString().toUpperCase(locale));
                }
            }
        }

    }

    private void scrollToChild(int position, int offset) {
        if (tabCount == 0) {
            return;
        }

        int newScrollX = tabsContainer.getChildAt(position).getLeft() + offset;

        if (position > 0 || offset > 0) {
            newScrollX -= scrollOffset;
        }

        if (newScrollX != lastScrollX) {
            lastScrollX = newScrollX;
            scrollTo(newScrollX, 0);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isInEditMode() || tabCount == 0) {
            return;
        }

        final int height = getHeight();

        // draw indicator line

        rectPaint.setColor(indicatorColor);

        // default: line below current tab
        View currentTab = tabsContainer.getChildAt(currentPosition);
        float lineLeft = currentTab.getLeft();
        float lineRight = currentTab.getRight();

        // if there is an offset, start interpolating left and right coordinates between current and next tab
        if (currentPositionOffset > 0f && currentPosition < tabCount - 1) {

            View nextTab = tabsContainer.getChildAt(currentPosition + 1);
            final float nextTabLeft = nextTab.getLeft();
            final float nextTabRight = nextTab.getRight();

            lineLeft = (currentPositionOffset * nextTabLeft + (1f - currentPositionOffset) * lineLeft);
            lineRight = (currentPositionOffset * nextTabRight + (1f - currentPositionOffset) * lineRight);
        }

        canvas.drawRect(lineLeft, height - indicatorHeight, lineRight, height, rectPaint);

        // draw underline

        rectPaint.setColor(underlineColor);
        canvas.drawRect(0, height - underlineHeight, tabsContainer.getWidth(), height, rectPaint);

        // draw divider

        if (isShowdivider) {
            dividerPaint.setColor(dividerColor);
            for (int i = 0; i < tabCount - 1; i++) {
                View tab = tabsContainer.getChildAt(i);
                canvas.drawLine(tab.getRight(), dividerPadding, tab.getRight(), height - dividerPadding, dividerPaint);
            }
        }
    }

    public void setIndicatorColor(int indicatorColor) {
        this.indicatorColor = indicatorColor;
        invalidate();
    }

    public void setIndicatorColorResource(int resId) {
        indicatorColor = getResources().getColor(resId);
        invalidate();
    }

    public int getIndicatorColor() {
        return indicatorColor;
    }

    public void setIndicatorHeight(int indicatorLineHeightPx) {
        indicatorHeight = indicatorLineHeightPx;
        invalidate();
    }

    public int getIndicatorHeight() {
        return indicatorHeight;
    }

    public void setUnderlineColor(int underlineColor) {
        this.underlineColor = underlineColor;
        invalidate();
    }

    public void setUnderlineColorResource(int resId) {
        underlineColor = getResources().getColor(resId);
        invalidate();
    }

    public int getUnderlineColor() {
        return underlineColor;
    }

    public void setDividerColor(int dividerColor) {
        this.dividerColor = dividerColor;
        invalidate();
    }

    public void setDividerColorResource(int resId) {
        dividerColor = getResources().getColor(resId);
        invalidate();
    }

    public int getDividerColor() {
        return dividerColor;
    }

    public void setUnderlineHeight(int underlineHeightPx) {
        underlineHeight = underlineHeightPx;
        invalidate();
    }

    public int getUnderlineHeight() {
        return underlineHeight;
    }

    public void setDividerPadding(int dividerPaddingPx) {
        dividerPadding = dividerPaddingPx;
        invalidate();
    }

    public int getDividerPadding() {
        return dividerPadding;
    }

    public void setScrollOffset(int scrollOffsetPx) {
        scrollOffset = scrollOffsetPx;
        invalidate();
    }

    public int getScrollOffset() {
        return scrollOffset;
    }

    public void setShouldExpand(boolean shouldExpand) {
        this.shouldExpand = shouldExpand;
        requestLayout();
    }

    public boolean getShouldExpand() {
        return shouldExpand;
    }

    public boolean isTextAllCaps() {
        return textAllCaps;
    }

    public void setAllCaps(boolean textAllCaps) {
        this.textAllCaps = textAllCaps;
    }

    public void setTextSize(int textSizePx) {
        tabTextSize = textSizePx;
        updateTabStyles();
    }

    public int getTextSize() {
        return tabTextSize;
    }

    public void setTextColor(int textColor) {
        tabTextColor = textColor;
        updateTabStyles();
    }

    public void setTextColorResource(int resId) {
        tabTextColor = getResources().getColor(resId);
        updateTabStyles();
    }

    public void setTextHideColor(int textColor) {
        tabTextColorHide = textColor;
        updateTabStyles();
    }

    public int getTextColor() {
        return tabTextColor;
    }

    public void setTypeface(Typeface typeface, int style) {
        tabTypeface = typeface;
        tabTypefaceStyle = style;
        updateTabStyles();
    }

    /**
     * tab切换viewpager是否平滑过渡
     *
     * @return
     */
    public boolean isTabSmoothScroll() {
        return isTabSmoothScroll;
    }

    /**
     * 设置tab点击viewpager是切换是否有平滑
     * 如果为true 切换有平滑过渡
     * 如果为false viewpager直接切换到
     *
     * @param isTabSmoothScroll
     */
    public void setTabSmoothScroll(boolean isTabSmoothScroll) {
        this.isTabSmoothScroll = isTabSmoothScroll;
    }

    /**
     * 监听tab的点击事件
     *
     * @param onTabItemClickListener
     */
    public void setOnTabItemClickListener(OnTabItemClickListener onTabItemClickListener) {
        this.onTabItemClickListener = onTabItemClickListener;
    }

    public void setTabDefualtBackground(int resId) {
        tabBackgroundDefultResId = resId;
    }

    public void setTabPaddingLeftRight(int paddingPx) {
        tabPadding = paddingPx;
        updateTabStyles();
    }

    public int getTabPaddingLeftRight() {
        return tabPadding;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        currentPosition = savedState.currentPosition;
        requestLayout();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.currentPosition = currentPosition;
        return savedState;
    }

    static class SavedState extends BaseSavedState {
        int currentPosition;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            currentPosition = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(currentPosition);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    /**
     * 设置子view是否能点击
     *
     * @param isCanClick true 里面的view能点击
     *                   false 不能点击
     */
    public void setCanClick(boolean isCanClick) {
        this.isCanClick = isCanClick;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (isCanClick) {
            return super.dispatchTouchEvent(ev);
        } else {
            return false;
        }
    }

    public View getTabTextView(int index) {
        return tabs[index];
    }

    public void showTextByAnim(int index) {
        final View v = tabs[index];
        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(v, "alpha", 0f, 1f);
        alphaAnim.setDuration(300);
        alphaAnim.start();
        alphaAnim.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator arg0) {
                v.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animator arg0) {

            }

            @Override
            public void onAnimationEnd(Animator arg0) {
            }

            @Override
            public void onAnimationCancel(Animator arg0) {

            }
        });
    }

    public void hideTextByAnim(int index) {
        final View v = tabs[index];
        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(v, "alpha", 1f, 0f);
        alphaAnim.setDuration(300);
        alphaAnim.start();
        alphaAnim.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator arg0) {
                v.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animator arg0) {

            }

            @Override
            public void onAnimationEnd(Animator arg0) {
            }

            @Override
            public void onAnimationCancel(Animator arg0) {

            }
        });
    }
}
