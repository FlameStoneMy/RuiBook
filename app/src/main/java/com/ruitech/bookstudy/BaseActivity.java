package com.ruitech.bookstudy;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.ruitech.bookstudy.utils.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    private View shaderView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.fullScreen(this);

        FrameLayout f = new FrameLayout(this);
        LayoutInflater.from(this).inflate(getLayoutId(), f);
        shaderView = new View(this);
        shaderView.setBackground(new ColorDrawable(getResources().getColor(R.color._26ffb334)));
        f.addView(shaderView);
        setContentView(f);

        shaderView.setVisibility(App.eyeProtectEnabled() ? View.VISIBLE : View.INVISIBLE);

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    protected abstract int getLayoutId();

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EyeProtectEvent event) {
        shaderView.setVisibility(App.eyeProtectEnabled() ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

}
