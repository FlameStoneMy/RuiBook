package com.ruitech.bookstudy.guide;

import android.util.Log;

import com.ruitech.bookstudy.bean.Grade;
import com.ruitech.bookstudy.bean.GradeCategory;
import com.ruitech.bookstudy.guide.binder.GradeBinder;
import com.ruitech.bookstudy.guide.binder.GradeCategoryTitleBinder;
import com.ruitech.bookstudy.guide.task.GradeListQueryTask;
import com.ruitech.bookstudy.guide.task.GradeListQueryWorker;
import com.ruitech.bookstudy.guide.task.TaskCallback;
import com.ruitech.bookstudy.homepage.HomePageQueryTask;
import com.ruitech.bookstudy.homepage.binder.bean.GradeCalendarCard;
import com.ruitech.bookstudy.homepage.binder.bean.SubjectTab;
import com.ruitech.bookstudy.homepage.binder.bean.TabsCard;
import com.ruitech.bookstudy.uibean.GradeUI;
import com.ruitech.bookstudy.utils.APIUtil;
import com.ruitech.bookstudy.utils.Const;
import com.ruitech.bookstudy.utils.Executors;
import com.ruitech.bookstudy.utils.ListUtils;
import com.ruitech.bookstudy.utils.NetworkResponse;
import com.ruitech.bookstudy.utils.UrlInvalidException;
import com.ruitech.bookstudy.widget.RuiDiffUtil;

import java.io.IOException;
import java.util.List;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.Response;

import static com.ruitech.bookstudy.utils.Executors.SIMPLE_THREAD_EXECUTOR;

public class GradeHelper extends AbsGuideHelper implements GradeListQueryWorker.Callback, GradeBinder.Callback {
    private static final String TAG = "GradeHelper";

    public GradeHelper(RecyclerView recyclerView, TaskCallback callback) {
        super(recyclerView, callback);
    }

    public GradeHelper(RecyclerView recyclerView, TaskCallback callback, Grade grade) {
        super(recyclerView, callback);
        currGrade = grade;
    }

    public void load() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//        Response response;
//        try {
//            response = APIUtil.getResponse(Const.GRADE_LIST_QUERY_URL);
//            android.util.Log.d(TAG, "response.code(): " + response.code() + " [" + response.body().string() + "]");
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (UrlInvalidException e) {
//            e.printStackTrace();
//        }
//            }
//        }).start();

//        new HomePageQueryTask(Grade.FIFTH_GRADE, new HomePageQueryTask.Callback() {
//            @Override
//            public void onHomePageResult(NetworkResponse ret, GradeCalendarCard gradeCalendarCard, TabsCard<SubjectTab> tabsCard) {
//
//            }
//        }).executeOnExecutor(Executors.network());

//        new GradeListQueryTask(this).executeOnExecutor(java.util.concurrent.Executors.newSingleThreadExecutor());//SIMPLE_THREAD_EXECUTOR);
//        new GradeListQueryTask(this).executeOnExecutor(Executors.network());
        new GradeListQueryWorker(this).execute();
//        android.util.Log.d(TAG, "rui here");
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
                            onGradeSelected(i, (GradeUI) obj, false);
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
        onGradeSelected(position, gradeUI, true);
    }

    private void onGradeSelected(int position, GradeUI gradeUI, boolean manual) {
        Log.d(TAG, "onGradeSelected: " + position + " " + gradeUI.getValue() + " " + manual);
        RuiDiffUtil.updateSelectPos(adapter, position, currGradePos);
        currGradePos = position;
        currGrade = gradeUI.getValue();
        if (manual && onGradeClickListener != null) {
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
