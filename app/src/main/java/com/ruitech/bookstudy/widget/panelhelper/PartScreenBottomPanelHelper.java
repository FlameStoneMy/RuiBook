package com.ruitech.bookstudy.widget.panelhelper;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.ruitech.bookstudy.R;
import com.ruitech.bookstudy.widget.panelhelper.layout.LayoutBinder;

import androidx.annotation.CallSuper;


public abstract class PartScreenBottomPanelHelper<T extends ViewGroup & LayoutBinder> extends AbstractBottomPanelHelper<T> implements View.OnClickListener {
    private static final String TAG = "PSBottomPanelHelper";

    public PartScreenBottomPanelHelper(Context context) {
        super(context);
    }

    @Override
    protected ViewGroup obtainBottomPanel(ViewGroup bottomContainer) {
        return bottomContainer.findViewById(R.id.bottom_panel);
    }

    @Override
    public void bindView(T container) {
        // we have to block touch event, even we don't need it at all.
        container.setOnClickListener(this);
        super.bindView(container);
    }

    @Override
    protected int getShowTargetHeight() {
        return container.getHeight() - bottomPanelHeight;
    }

    @Override
    protected int getHideTargetHeight() {
        return container.getHeight();
    }

    @Override
    protected void onHidden() {
        super.onHidden();
        container.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onShow() {
        super.onShow();
        container.setVisibility(View.VISIBLE);
    }

    @Override
    public final void onClick(View v) {
        if (!dumped) {
            onClickInternal(v);
        }
    }

    @CallSuper
    protected void onClickInternal(View v) {
        if (container == v) {
            onTouchOutside();
        }
    }

    protected void onTouchOutside() {
        if (canDismiss()) {
            // default action.
            hideBottomPanel();
        }
    }
}
