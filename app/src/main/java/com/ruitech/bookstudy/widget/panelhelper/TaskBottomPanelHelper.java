package com.ruitech.bookstudy.widget.panelhelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ruitech.bookstudy.R;
import com.ruitech.bookstudy.guide.task.TaskCallback;
import com.ruitech.bookstudy.network.NetWorkGuide;
import com.ruitech.bookstudy.network.NetworkMonitor;
import com.ruitech.bookstudy.utils.NetworkResponse;
import com.ruitech.bookstudy.utils.NetworkUtil;
import com.ruitech.bookstudy.widget.panelhelper.layout.ConstraintLayoutPanelContainer;

import androidx.annotation.CallSuper;
import androidx.annotation.LayoutRes;
import androidx.constraintlayout.widget.ConstraintLayout;

public abstract class TaskBottomPanelHelper extends PartScreenBottomPanelHelper<ConstraintLayoutPanelContainer> implements TaskCallback, View.OnClickListener {
    protected View coreLayout, noNetworkLayout, errorLayout, loadingLayout;

    public TaskBottomPanelHelper(Context context) {
        super(context);
        bindView();
    }

    private void bindView() {
        super.bindView((ConstraintLayoutPanelContainer) LayoutInflater.from(context).inflate(R.layout.bottom_panel_task, null));

        coreLayout = getCoreLayout(bottomPanel);

        coreLayout.setVisibility(View.GONE);
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        params.topToBottom = R.id.close;
        params.bottomToBottom = bottomPanel.getId();
        bottomPanel.addView(coreLayout, params);
        noNetworkLayout = bottomPanel.findViewById(R.id.no_network_layout);
        noNetworkLayout.findViewById(R.id.turn_on_internet).setOnClickListener(this);
        errorLayout = bottomPanel.findViewById(R.id.error_layout);
        errorLayout.findViewById(R.id.error_action).setOnClickListener(this);
        loadingLayout = bottomPanel.findViewById(R.id.loading_layout);
    }

    protected View getCoreLayout(ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(getCoreLayoutId(), parent, false);
    }

    protected @LayoutRes int getCoreLayoutId() {
        return -1;
    }

    protected View currLayout;

    @Override
    public void onLoading() {
        if (currLayout != coreLayout) {
            setCurrLayout(loadingLayout);
        }
    }

    @Override
    public void onLoaded(NetworkResponse networkResponse) {
        switch (networkResponse) {
            case RESPONSE_OK:
                setCurrLayout(coreLayout);
                break;
            case RESPONSE_NETWORK:
                setCurrLayout(noNetworkLayout);
                break;
            case RESPONSE_ERROR:
                setCurrLayout(errorLayout);
                break;
        }
    }

    protected boolean setCurrLayout(View layout) {
        boolean changed = false;
        if (currLayout != layout) {
            if (currLayout != null) {
                currLayout.setVisibility(View.GONE);
            }
            layout.setVisibility(View.VISIBLE);
            currLayout = layout;
            changed = true;
        }
        return changed;
    }

    protected abstract void reload();

    @Override
    @CallSuper
    protected void onClickInternal(View v) {
        switch (v.getId()) {
            case R.id.error_action:
                reload();
                break;
            case R.id.turn_on_internet:
                connectNetworkIfNeeded();
                break;
            default:
                super.onClickInternal(v);
                break;
        }
    }

    private NetworkMonitor networkMonitor;
    private void connectNetworkIfNeeded() {
        if (!NetworkUtil.isNetWorkConnected()) {
            NetWorkGuide.launchNetWorkSetting(context);
            if (networkMonitor == null) {
                networkMonitor = new NetworkMonitor(context, (last, current) -> {
                    if (NetworkUtil.isNetWorkConnected()) {
                        reload();
                    }
                    networkMonitor.release();
                    networkMonitor = null;
                });
            }
            networkMonitor.start();
        } else {
            reload();
        }
    }

    @Override
    public void dump() {
        super.dump();
        if (networkMonitor != null) {
            networkMonitor.release();
            networkMonitor = null;
        }
    }
}
