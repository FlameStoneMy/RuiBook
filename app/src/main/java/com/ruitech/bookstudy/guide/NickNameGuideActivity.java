package com.ruitech.bookstudy.guide;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import com.ruitech.bookstudy.homepage.HomeActivity;
import com.ruitech.bookstudy.R;
import com.ruitech.bookstudy.RuiPreferenceUtil;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class NickNameGuideActivity extends AbsGuideActivity<NicknameHelper> {
    private static final String TAG = "NickNameGuideActivity";

    public static final void start(Context context) {
        Intent intent = new Intent(context, NickNameGuideActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reload();
    }

    @Override
    protected String getPageNumStr() {
        return "2/2";
    }

    @Override
    protected int getCoreLayoutId() {
        return R.layout.activity_core_nickname_guide;
    }

    @Override
    protected void reload() {
        guideHelper.load();
    }

    @Override
    protected NicknameHelper getGuideHelper(RecyclerView recyclerView) {
        return new NicknameHelper(recyclerView, this);
    }

    @Override
    protected void onNextClicked() {
        RuiPreferenceUtil.setNickname(guideHelper.getCurrNickname());
        RuiPreferenceUtil.setGender(guideHelper.getCurrGender());
        HomeActivity.start(this);
        finish();
    }
}
