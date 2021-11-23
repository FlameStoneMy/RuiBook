package com.ruitech.bookstudy;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.WindowManager;

import com.ruitech.bookstudy.desktop.DesktopActivity;
import com.ruitech.bookstudy.desktop.GuideActivity;
import com.ruitech.bookstudy.guide.GradeGuideActivity;
import com.ruitech.bookstudy.utils.NetworkUtil;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ArbitraryActivity extends AppCompatActivity {

    private static final String TAG = "ArbitraryActivity";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        if (TextUtils.isEmpty(RuiPreferenceUtil.getNickname())) {
            if (NetworkUtil.isNetWorkConnected()) {

                android.util.Log.d(TAG, "rui here1");
                GradeGuideActivity.start(ArbitraryActivity.this);
//                            NickNameGuideActivity.start(SplashActivity.this);
            } else {

                android.util.Log.d(TAG, "rui here2");
                GuideActivity.start(ArbitraryActivity.this);
            }
        } else {

            android.util.Log.d(TAG, "rui here3");
            DesktopActivity.start(ArbitraryActivity.this);
//                        HomeActivity.start(SplashActivity.this);
        }

        finish();
    }
}
