package com.ruitech.bookstudy.widget.panelhelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ruitech.bookstudy.R;
import com.ruitech.bookstudy.bean.Grade;
import com.ruitech.bookstudy.guide.GradeHelper;
import com.ruitech.bookstudy.guide.task.TaskCallback;
import com.ruitech.bookstudy.utils.NetworkResponse;
import com.ruitech.bookstudy.widget.panelhelper.layout.ConstraintLayoutPanelContainer;

import androidx.annotation.CallSuper;
import androidx.recyclerview.widget.RecyclerView;

public class GradeBottomPanelHelper extends TaskBottomPanelHelper implements GradeHelper.OnGradeClickListener {
    private Callback callback;
    public GradeBottomPanelHelper(Context context, Callback callback) {
        super(context);
        this.callback = callback;
        bindView();
    }

    private GradeHelper gradeHelper;
    private Grade grade;
    public void bindData(Grade grade) {
        this.grade = grade;
        reload();
    }

    private RecyclerView recyclerView;
    private void bindView() {
        recyclerView = container.findViewById(R.id.recycler_view);
        container.findViewById(R.id.close).setOnClickListener(this);
        gradeHelper = new GradeHelper(recyclerView, this);
        gradeHelper.setOnGradeClickListener(this);
    }

    @Override
    protected View getCoreLayout(ViewGroup parent) {
        RecyclerView recyclerView = new RecyclerView(parent.getContext());
        recyclerView.setId(R.id.recycler_view);
        return recyclerView;
    }

    @Override
    protected void reload() {
        gradeHelper.tryLoad(grade);
    }

    @Override
    public void onGradeClick(Grade grade) {
        callback.onGradeSelected(grade);
        Toast.makeText(context, R.string.op_succ, Toast.LENGTH_SHORT).show();
        hideBottomPanel();
    }

    public interface Callback {
        void onGradeSelected(Grade grade);
    }

    @Override
    @CallSuper
    protected void onClickInternal(View v) {
        switch (v.getId()) {
            case R.id.close:
                hideBottomPanel();
                break;
            default:
                super.onClickInternal(v);
                break;
        }
    }
}
