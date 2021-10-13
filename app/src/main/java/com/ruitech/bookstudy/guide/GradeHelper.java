package com.ruitech.bookstudy.guide;

import android.util.Log;

import com.ruitech.bookstudy.bean.Grade;
import com.ruitech.bookstudy.bean.GradeCategory;
import com.ruitech.bookstudy.guide.binder.GradeBinder;
import com.ruitech.bookstudy.guide.binder.GradeCategoryTitleBinder;
import com.ruitech.bookstudy.guide.task.GradeListQueryTask;
import com.ruitech.bookstudy.guide.task.TaskCallback;
import com.ruitech.bookstudy.uibean.GradeUI;
import com.ruitech.bookstudy.utils.Executors;
import com.ruitech.bookstudy.utils.ListUtils;
import com.ruitech.bookstudy.utils.NetworkResponse;
import com.ruitech.bookstudy.widget.RuiDiffUtil;

import java.util.List;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class GradeHelper extends AbsGuideHelper implements GradeListQueryTask.Callback, GradeBinder.Callback {
    private static final String TAG = "GradeHelper";

    public GradeHelper(RecyclerView recyclerView, TaskCallback callback) {
        super(recyclerView, callback);
    }

    public GradeHelper(RecyclerView recyclerView, TaskCallback callback, Grade grade) {
        super(recyclerView, callback);
        currGrade = grade;
    }

    public void load() {
        new GradeListQueryTask(this).executeOnExecutor(Executors.network());
    }

    private Grade preSelectedGrade;
    public void tryLoad(Grade grade) {
        if (currGradePos < 0) {
            // first time.
            preSelectedGrade = grade;
            load();
        } else {
            List list = adapter.getItems();
            if (!ListUtils.isEmpty(list)) {
                for (int i = 0; i < list.size(); i++) {
                    Object obj = list.get(i);
                    if (obj instanceof GradeUI) {
                        if (((GradeUI) obj).getValue() == grade) {
                            onGradeClick(i, (GradeUI) obj);
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onGradeListQuery(NetworkResponse ret, List list) {
        Log.d(TAG, "onGradeListQuery: " + list);
        callback.onLoaded(ret);
        if (ret != NetworkResponse.RESPONSE_OK) {
            return;
        }
        currGradePos = GradeUI.convert(list, preSelectedGrade);
        currGrade = ((GradeUI) list.get(currGradePos)).getValue();
        RuiDiffUtil.onNewData(adapter, list);
        preSelectedGrade = null;
    }

    private int currGradePos = -1;
    private Grade currGrade;
    public Grade getCurrGrade() {
        return currGrade;
    }

    @Override
    public void onGradeClick(int position, GradeUI gradeUI) {
        Log.d(TAG, "onGradeClick: " + position + " " + gradeUI.getValue());
        RuiDiffUtil.updateSelectPos(adapter, position, currGradePos);
        currGradePos = position;
        currGrade = gradeUI.getValue();
        if (onGradeClickListener != null) {
            onGradeClickListener.onGradeClick(gradeUI.getValue());
        }
    }

    @Override
    public void onLoading() {
        callback.onLoading();
    }

    public interface OnGradeClickListener {
        void onGradeClick(Grade grade);
    }
    private OnGradeClickListener onGradeClickListener;
    public void setOnGradeClickListener(OnGradeClickListener listener) {
        this.onGradeClickListener = listener;
    }

    @Override
    protected void initAdapter() {
        adapter.register(GradeCategory.class, new GradeCategoryTitleBinder());
        adapter.register(GradeUI.class, new GradeBinder(this));
    }

    @Override
    protected GridLayoutManager.SpanSizeLookup getSpanSizeLookup() {
        return new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                Object obj = adapter.getItems().get(position);
                if (obj instanceof GradeCategory) {
                    return 3;
                } else {
                    return 1;
                }
            }
        };
    }
}
