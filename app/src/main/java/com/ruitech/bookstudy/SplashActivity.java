package com.ruitech.bookstudy;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.ruitech.bookstudy.bean.Grade;
import com.ruitech.bookstudy.desktop.DesktopActivity;
import com.ruitech.bookstudy.desktop.GuideActivity;
import com.ruitech.bookstudy.desktop.bean.Category;
import com.ruitech.bookstudy.desktop.task.CategoryListLoadTask;
import com.ruitech.bookstudy.guide.GradeGuideActivity;
import com.ruitech.bookstudy.guide.NickNameGuideActivity;
import com.ruitech.bookstudy.homepage.HomePageQueryTask;
import com.ruitech.bookstudy.homepage.binder.bean.GradeCalendarCard;
import com.ruitech.bookstudy.homepage.binder.bean.SubjectTab;
import com.ruitech.bookstudy.homepage.binder.bean.TabsCard;
import com.ruitech.bookstudy.utils.DateUtil;
import com.ruitech.bookstudy.utils.Executors;
import com.ruitech.bookstudy.utils.NetworkResponse;
import com.ruitech.bookstudy.utils.NetworkUtil;
import com.ruitech.bookstudy.utils.StatusBarUtil;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends BaseActivity {

    private static final String TAG = "SplashActivity";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        StatusBarUtil.fullScreen(this);
//        setContentView(R.layout.activity_splash);
        handler.sendEmptyMessageDelayed(MSG_FINISH, 3000L);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    private static final int MSG_FINISH = 1;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_FINISH:
                    android.util.Log.d(TAG, "rui here: " + RuiPreferenceUtil.getNickname());
//                    GradeGuideActivity.start(SplashActivity.this);
                    if (TextUtils.isEmpty(RuiPreferenceUtil.getNickname())) {
                        if (NetworkUtil.isNetWorkConnected()) {
                            android.util.Log.d(TAG, "rui here1");
                            GradeGuideActivity.start(SplashActivity.this);
//                            NickNameGuideActivity.start(SplashActivity.this);
                        } else {
                            GuideActivity.start(SplashActivity.this);
                        }
                    } else {
                        DesktopActivity.start(SplashActivity.this);
//                        HomeActivity.start(SplashActivity.this);
                    }
//                    DesktopActivity.start(SplashActivity.this);
//                    new CategoryListLoadTask(Grade.FIFTH_GRADE, new CategoryListLoadTask.Callback() {
//                        @Override
//                        public void onCategoryListLoad(NetworkResponse response, List<Category> list) {
//                            new HomePageQueryTask(Grade.FIFTH_GRADE, new HomePageQueryTask.Callback() {
//                                @Override
//                                public void onHomePageResult(NetworkResponse ret, GradeCalendarCard gradeCalendarCard, TabsCard<SubjectTab> tabsCard) {
//
//                                }
//                            }).executeOnExecutor(Executors.network());
//                        }
//                    }).executeOnExecutor(Executors.network());

                    finish();
//                    WindowManager windowManager = SplashActivity.this.getWindowManager();
//                    DisplayMetrics dm = new DisplayMetrics();
//                    windowManager.getDefaultDisplay().getMetrics(dm);
//                    android.util.Log.d(TAG, "rui here3: " + dm.widthPixels + " " + dm.heightPixels);
                    break;
            }
        }
    };
}
