package com.ruitech.bookstudy.guide;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ruitech.bookstudy.BaseActivity;
import com.ruitech.bookstudy.R;
import com.ruitech.bookstudy.guide.task.TaskCallback;
import com.ruitech.bookstudy.network.NetWorkGuide;
import com.ruitech.bookstudy.network.NetworkMonitor;
import com.ruitech.bookstudy.utils.NetworkResponse;
import com.ruitech.bookstudy.utils.NetworkUtil;
import com.ruitech.bookstudy.utils.StatusBarUtil;

import androidx.annotation.CallSuper;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public abstract class TaskActivity extends BaseActivity implements TaskCallback, View.OnClickListener {

    private static final String TAG = "TaskActivity";
    protected View coreLayout, noNetworkLayout, errorLayout, loadingLayout;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewGroup container = findViewById(R.id.container);
        coreLayout = LayoutInflater.from(this).inflate(getCoreLayoutId(), container, false);
        coreLayout.setVisibility(View.GONE);
        container.addView(coreLayout);
        noNetworkLayout = findViewById(R.id.no_network_layout);
        noNetworkLayout.findViewById(R.id.turn_on_internet).setOnClickListener(this);
        errorLayout = findViewById(R.id.error_layout);
        errorLayout.findViewById(R.id.error_action).setOnClickListener(this);
        loadingLayout = findViewById(R.id.loading_layout);
    }

    @Override
    protected final int getLayoutId() {
        return R.layout.activity_task;
    }

    protected abstract @LayoutRes int getCoreLayoutId();

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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.error_action:
                reload();
                break;
            case R.id.turn_on_internet:
                connectNetworkIfNeeded();
                break;
        }
    }

    private NetworkMonitor networkMonitor;
    private void connectNetworkIfNeeded() {
        if (!NetworkUtil.isNetWorkConnected()) {
            NetWorkGuide.launchNetWorkSetting(this);
            if (networkMonitor == null) {
                networkMonitor = new NetworkMonitor(this, (last, current) -> {
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
    public void onDestroy() {
        super.onDestroy();
        if (networkMonitor != null) {
            networkMonitor.release();
            networkMonitor = null;
        }
    }
}
