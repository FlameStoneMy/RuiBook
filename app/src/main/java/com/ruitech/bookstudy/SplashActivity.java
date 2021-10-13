package com.ruitech.bookstudy;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.ruitech.bookstudy.guide.GradeGuideActivity;
import com.ruitech.bookstudy.homepage.HomeActivity;
import com.ruitech.bookstudy.utils.StatusBarUtil;
import com.ruitech.bookstudy.widget.RuiDiffUtil;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DiffUtil;
import me.drakeet.multitype.MultiTypeAdapter;

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
                        GradeGuideActivity.start(SplashActivity.this);
                    } else {
                        HomeActivity.start(SplashActivity.this);
                    }
                    finish();
                    break;
            }
        }
    };
}
