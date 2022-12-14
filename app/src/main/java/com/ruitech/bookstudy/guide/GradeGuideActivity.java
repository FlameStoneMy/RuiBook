package com.ruitech.bookstudy.guide;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ruitech.bookstudy.R;
import com.ruitech.bookstudy.RuiPreferenceUtil;
import com.ruitech.bookstudy.desktop.DesktopActivity;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class GradeGuideActivity extends AbsGuideActivity<GradeHelper> {

    private static final String TAG = "GradeGuideActivity";

    public static final void start(Context context) {
        Intent intent = new Intent(context, GradeGuideActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        findViewById(R.id.body).setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        android.util.Log.d(TAG, "rui here1");
        reload();
    }

    @Override
    protected String getPageNumStr() {
        return "1/2";
    }

    @Override
    protected int getCoreLayoutId() {
        return R.layout.activity_core_grade_guide;
    }

    @Override
    protected void reload() {
        guideHelper.load();
    }

    @Override
    protected GradeHelper getGuideHelper(RecyclerView recyclerView) {
        return new GradeHelper(recyclerView, this);
    }

    @Override
    protected void onNextClicked() {
        RuiPreferenceUtil.setGrade(guideHelper.getCurrGrade());
        NickNameGuideActivity.start(this);
//        DesktopActivity.start(this);
        finish();
    }

    public void finish() {
        super.finish();
        android.util.Log.d(TAG, "rui finish");
        new Exception().printStackTrace();
    }

    @Override
    public void onBackPressed() {

    }
}
