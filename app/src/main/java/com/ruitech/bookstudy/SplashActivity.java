package com.ruitech.bookstudy;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.ruitech.bookstudy.desktop.DesktopActivity;
import com.ruitech.bookstudy.desktop.GuideActivity;
import com.ruitech.bookstudy.guide.GradeGuideActivity;
import com.ruitech.bookstudy.utils.NetworkUtil;
import com.ruitech.bookstudy.utils.StatusBarUtil;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.fullScreen(this);
        setContentView(R.layout.activity_splash);
        handler.sendEmptyMessageDelayed(MSG_FINISH, 1500L);
    }

    private static final int MSG_FINISH = 1;
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_FINISH:
                    if (TextUtils.isEmpty(RuiPreferenceUtil.getNickname())) {
                        if (NetworkUtil.isNetWorkConnected()) {
                            GradeGuideActivity.start(SplashActivity.this);
                        } else {
                            GuideActivity.start(SplashActivity.this);
                        }
                    } else {
                        DesktopActivity.start(SplashActivity.this);
//                        HomeActivity.start(SplashActivity.this);
                    }
//                    DesktopActivity.start(SplashActivity.this);
                    finish();
                    break;
            }
        }
    };
}
