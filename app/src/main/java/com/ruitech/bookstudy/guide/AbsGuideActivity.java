package com.ruitech.bookstudy.guide;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.ruitech.bookstudy.R;
import com.ruitech.bookstudy.guide.task.TaskCallback;
import com.ruitech.bookstudy.utils.NetworkResponse;
import com.ruitech.bookstudy.utils.StatusBarUtil;

import androidx.annotation.CallSuper;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import me.drakeet.multitype.MultiTypeAdapter;

public abstract class AbsGuideActivity<T extends AbsGuideHelper> extends TaskActivity implements View.OnClickListener, TaskCallback {

    protected RecyclerView recyclerView;
    protected MultiTypeAdapter adapter;

    protected T guideHelper;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView pageNumTv = findViewById(R.id.page_num_tv);
        SpannableString pageNum = new SpannableString(getPageNumStr());
        pageNum.setSpan(new ForegroundColorSpan(getResources().getColor(R.color._ffffff)), 0, 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        pageNumTv.setText(pageNum);

        findViewById(R.id.next).setOnClickListener(this);

        guideHelper = getGuideHelper(findViewById(R.id.recycler_view));
    }

    protected abstract String getPageNumStr();

    protected abstract T getGuideHelper(RecyclerView recyclerView);

    @Override
    @CallSuper
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next:
                onNextClicked();
                break;
            default:
                super.onClick(v);
        }
    }

    protected abstract void onNextClicked();

    @Override
    protected boolean setCurrLayout(View layout) {
        boolean ret = super.setCurrLayout(layout);
        if (ret) {
            if (layout == coreLayout) {
                StatusBarUtil.setColor(this, getResources().getColor(R.color._02abff));
            } else {
                StatusBarUtil.setColor(this, getResources().getColor(android.R.color.transparent));
            }
        }
        return ret;
    }
}
