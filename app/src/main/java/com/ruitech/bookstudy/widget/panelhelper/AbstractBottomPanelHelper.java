package com.ruitech.bookstudy.widget.panelhelper;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.FrameLayout;

import com.ruitech.bookstudy.App;
import com.ruitech.bookstudy.BuildConfig;
import com.ruitech.bookstudy.R;
import com.ruitech.bookstudy.utils.KeyboardUtil;
import com.ruitech.bookstudy.utils.StatusBarUtil;
import com.ruitech.bookstudy.utils.Util;
import com.ruitech.bookstudy.widget.panelhelper.layout.LayoutBinder;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialog;
import androidx.core.view.ViewCompat;
import androidx.customview.widget.ViewDragHelper;

import static androidx.customview.widget.ViewDragHelper.STATE_IDLE;

public abstract class AbstractBottomPanelHelper<T extends ViewGroup & LayoutBinder> {

    private static final boolean DEBUG = BuildConfig.DEBUG;
    private static final String TAG = "AbsBottomPanelHelper";

    private ViewDragHelper viewDragHelper;
    protected Handler handler = new Handler();

    protected T container;
    protected ViewGroup bottomPanel;
    protected int bottomPanelHeight;

    private boolean shouldShow;
    private boolean isBottomPanelShow;

    protected Context context;
    public AbstractBottomPanelHelper(Context context) {
        this.context = context;
    }

    protected abstract ViewGroup obtainBottomPanel(ViewGroup bottomContainer);

    public View getBottomPanel() {
        return bottomPanel;
    }

    protected Context getContext() {
        return bottomPanel.getContext();
    }

    protected void bindView(T container) {
        this.container = container;

        bottomPanel = obtainBottomPanel(container);
        bottomPanel.setClickable(true);

        container.bindData(this);

        viewDragHelper = ViewDragHelper.create(container, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(@NonNull View child, int pointerId) {
                return true;
            }

            public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx,
                                              int dy) {
//                Log.d(TAG, "onViewPositionChanged: " + top + " " + dy + " " + getShowTargetHeight());
//                System.out.println("onViewPositionChanged: " + top + " " + dy + " " + getShowTargetHeight());
                if (needRecordScrollPos) {
                    scrollPos = top;
                }
            }

            public void onViewDragStateChanged(int state) {
//                Log.d(TAG, "onViewDragStateChanged: " + state + "\n");
//                System.out.println("onViewDragStateChanged: " + state + " " + bottomPanel.getY());
                if (state == STATE_IDLE) {
                    scrollPos = -1;
                    needRecordScrollPos = false;
                } else {
                    scrollPos = (int) bottomPanel.getY(); // start position.
                    needRecordScrollPos = true;
                }
            }

            private boolean needRecordScrollPos;
        });
        initContainerColor();
    }

    protected void initContainerColor() {
        container.setBackgroundColor(context.getResources().getColor(R.color._88000000));
    }

    private int scrollPos = -1;

    public void onContainerLayout() {
//        Log.d(TAG, "onContainerLayout: " + scrollPos + " " + container.getHeight() + " " + bottomPanel.getHeight() +
//                " [" + (scrollPos - container.getHeight() + bottomPanel.getHeight()) + "]");
        if (scrollPos >= 0) {
            bottomPanel.offsetTopAndBottom(scrollPos - container.getHeight() + bottomPanel.getHeight());
        }
    }

    public boolean onBackPressed() {
        boolean handled = false;
        if (DEBUG) {
            Log.d(TAG, "onBackPressed: " + isBottomPanelShow);
        }
        if (needHandleBackPressed()) {
            hideBottomPanel();
            handled = true;
        }
        return handled;
    }

    // dismiss on touch outside or press back btn
    protected boolean canDismiss() {
        return true;
    }

    protected boolean needHandleBackPressed() {
        return canDismiss() && isBottomPanelShow;
    }

    protected static final int STATE_HIDE  = 0;
    protected static final int STATE_SHOWING  = 1;
    protected static final int STATE_SHOW  = 2;
    protected static final int STATE_HIDING = 3;
    protected int state = STATE_HIDE;

    private static final int PENDING_NULL = 0;
    private static final int PENDING_SHOW = 1;
    private static final int PENDING_HIDE = 2;
    private int pendingOp = PENDING_NULL;

    private Dialog dialog;
    private Dialog getDialog() {
        if (dialog == null) {
            dialog = new AppCompatDialog(context, R.style.TaskDialogTheme) {
                @Override
                public void onBackPressed() {
                    AbstractBottomPanelHelper.this.onBackPressed();
                }
            };

            FrameLayout f = new FrameLayout(context);
            f.addView(container);
            View shaderView = new View(context);
            shaderView.setBackground(new ColorDrawable(context.getResources().getColor(R.color._26ffb334)));
            f.addView(shaderView);

            shaderView.setVisibility(App.eyeProtectEnabled() ? View.VISIBLE : View.INVISIBLE);

            dialog.setContentView(f);

            Window window = dialog.getWindow();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setNavigationBarColor(context.getResources().getColor(R.color.white));
            }
            StatusBarUtil.setTransparentBar(window);
        }

        return dialog;
    }

    public void showBottomPanel() {
        if (DEBUG) {
            Log.d(TAG, "showBottomPanel: " + bottomPanelHeight + " " + state + " " + pendingOp);
        }
        switch (state) {
            case STATE_SHOWING:
            case STATE_HIDING:
                // store in 'pendingOp's, until current anim finish.
                pendingOp = PENDING_SHOW;
            case STATE_SHOW:
                // do nothing.
                return;
        }
//        state = STATE_SHOWING;

        // Move from last line in bindView().
        // We obtain TreeObserver every time in case not alive due to dialog dismiss.
        bottomPanel.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (DEBUG) {
                    Log.d(TAG, "onGlobalLayout: " + shouldShow + " " + bottomPanelHeight + " " + bottomPanel.getHeight());
                }
                bottomPanel.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                if (bottomPanelHeight != bottomPanel.getHeight()) {
                    bottomPanelHeight = bottomPanel.getHeight();
                }
                if (shouldShow) {
                    if (immediateShowBottomPanel()) {
                        showBottomPanelInternal();
                        shouldShow = false;
                    }
                }
            }
        });
        getDialog().show(); // just before onShow().
        onShow();
        if (bottomPanelHeight > 0 && immediateShowBottomPanel()) {
            showBottomPanelInternal();
        } else {
            shouldShow = true;
        }
    }

    protected boolean immediateShowBottomPanel() {
        return true;
    }

    // For whole screen, temp reserved.
    protected int getShowTargetHeight() {
        return 0;
    }

    protected void showBottomPanelInternal() {
        state = STATE_SHOWING;
        handler.post(new Runnable() {
            @Override
            public void run() {
                bottomPanel.offsetTopAndBottom(bottomPanelHeight); // to start position.
                bottomPanel.setVisibility(View.VISIBLE);

                if (viewDragHelper.smoothSlideViewTo(bottomPanel, 0, getShowTargetHeight())) {
                    ViewCompat.postOnAnimation(bottomPanel, new SettleRunnable(bottomPanel));
                }
            }
        });
    }

    // For whole screen, temp reserved.
    protected int getHideTargetHeight() {
        return bottomPanelHeight;
    }

    public void hideBottomPanel() {
        if (DEBUG) {
            Log.d(TAG, "hideBottomPanel: " + bottomPanelHeight + " " + state + " " + pendingOp);
        }
        switch (state) {
            case STATE_SHOWING:
            case STATE_HIDING:
                // store in 'pendingOp's, until current anim finish.
                pendingOp = PENDING_HIDE;
            case STATE_HIDE:
                // do nothing.
                return;
        }

        state = STATE_HIDING;
        onHide();
        hideBottomPanelInternal();
    }

    protected boolean needHideAnim = true;
    private void hideBottomPanelInternal() {
        if (needHideAnim) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (viewDragHelper.smoothSlideViewTo(bottomPanel, 0, getHideTargetHeight())) {
                        ViewCompat.postOnAnimation(bottomPanel, new SettleRunnable(bottomPanel, true));
                    }
                }
            });
        } else {
            onHideAnimEnd();
        }
    }

    private void onHideAnimEnd() {
        bottomPanel.offsetTopAndBottom(-bottomPanelHeight); // reset position. (If re-layout triggered, no need.)
        onHidden();

        // In some case, activity should close as bottom panel hides.
        // Currently, we preserve the hiding anim.
        // If anim finishes before Activity's onDestroy(), everything is fine.
        // Else, Activity removes Decor & any Window on it.
        // We will face an exception (i.e. View not attached to window manager) when tries to dismiss.
        if (container.getWindowToken() != null) {
            getDialog().dismiss(); // just after onHidden().
        }
        state = STATE_HIDE;
        if (pendingOp == PENDING_SHOW) {
            showBottomPanel();
        }
    }

    private class SettleRunnable implements Runnable {
        private final View view;
        private boolean needHide;

        SettleRunnable(View view) {
            this(view, false);
        }

        SettleRunnable(View view, boolean needHide) {
            this.view = view;
            this.needHide = needHide;
        }

        @Override
        public void run() {
            if (context instanceof Activity) {
                if (!Util.isValidActivity((Activity) context)) {
                    return;
                }
            }
            if (viewDragHelper != null) {
                onShownBefore();
                boolean ret = viewDragHelper.continueSettling(true);

                if (ret) {
                    ViewCompat.postOnAnimation(view, this);
                } else {
                    if (needHide) {
                        onHideAnimEnd();
                    } else {
                        onShown();
                        state = STATE_SHOW;
                        if (pendingOp == PENDING_HIDE) {
                            hideBottomPanel();
                        }
                    }
                    pendingOp = PENDING_NULL;
                    isBottomPanelShow = !needHide;
                    Log.d(TAG, "isBottomPanelShow: " + needHide + " " + isBottomPanelShow);
                }

//                Log.d(TAG, "SettleRunnable: " + ret + " " + view.getTop() + " " + bottomPanelHeight + " " + needHide);
            }
        }
    }

    protected void onHidden() {
        if (needRemeasure()) {
            bottomPanel.setVisibility(View.GONE);
            bottomPanelHeight = 0;
        } else {
            bottomPanel.setVisibility(View.INVISIBLE);
        }
    }

    protected void onShownBefore() {

    }

    protected void onShown() {

    }

    protected void onShow() {
        KeyboardUtil.hideKeyboard(context, getDialog().getWindow());
        bottomPanel.setVisibility(View.INVISIBLE);
        Log.d(TAG, "bottomPanel: " + bottomPanel + " (" + bottomPanel.getVisibility() + ")");
    }

    protected void onHide() {
        KeyboardUtil.hideKeyboard(context, getDialog().getWindow());
    }

    protected boolean needRemeasure() {
        return false;
    }

    protected boolean dumped;
    @CallSuper
    public void dump() {
        dumped = true;
    }
}
