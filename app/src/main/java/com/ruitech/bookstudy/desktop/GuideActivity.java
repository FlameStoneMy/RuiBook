package com.ruitech.bookstudy.desktop;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import com.ruitech.bookstudy.BaseActivity;
import com.ruitech.bookstudy.R;
import com.ruitech.bookstudy.guide.GradeGuideActivity;
import com.ruitech.bookstudy.network.NetworkMonitor;
import com.ruitech.bookstudy.utils.NetworkUtil;
import com.ruitech.bookstudy.utils.StatusBarUtil;
import com.ruitech.bookstudy.utils.UIHelper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class GuideActivity extends BaseActivity implements View.OnClickListener {

    public static final void start(Context context) {
        Intent intent = new Intent(context, GuideActivity.class);
        context.startActivity(intent);
    }

    private NetworkMonitor networkMonitor;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        StatusBarUtil.fullScreen(this);
//        setContentView(R.layout.activity_guide);

        GradientDrawable containerDrawable = new GradientDrawable();new GradientDrawable();
        containerDrawable.setColors(new int[] {
                getResources().getColor(R.color._6308ff),
                getResources().getColor(R.color._0a1fbe)
        });
        containerDrawable.setCornerRadius(UIHelper.dp2px(32));

        findViewById(R.id.body_container).setBackground(containerDrawable);

        findViewById(R.id.action).setOnClickListener(this);

        if (networkMonitor == null) {
            networkMonitor = new NetworkMonitor(this, (last, current) -> {
                if (NetworkUtil.isNetWorkConnected()) {
                    GradeGuideActivity.start(GuideActivity.this);
                    finish();
                }
                networkMonitor.release();
                networkMonitor = null;
            });
        }
        networkMonitor.start();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_guide;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action:
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                break;
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
