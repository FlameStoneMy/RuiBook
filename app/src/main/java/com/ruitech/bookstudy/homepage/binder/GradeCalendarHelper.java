package com.ruitech.bookstudy.homepage.binder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.ruitech.bookstudy.R;
import com.ruitech.bookstudy.bean.Grade;
import com.ruitech.bookstudy.homepage.binder.bean.GradeCalendarCard;
import com.ruitech.bookstudy.utils.DateUtil;
import com.ruitech.bookstudy.widget.CircleTextView;

import androidx.core.util.Pair;

public class GradeCalendarHelper implements View.OnClickListener {
    private static final String TAG = "GradeCalendarHelper";
    private Context context;
    private Callback callback;

    private GradeCalendarCard gradeCalendar;
    private TextView gradeTv;
    private CircleTextView[] calendarViewArr;
    public GradeCalendarHelper(View view, Callback callback) {
        context = view.getContext();
        this.callback = callback;
        gradeTv = view.findViewById(R.id.grade_select);
        calendarViewArr = new CircleTextView[] {
                view.findViewById(R.id.tv_date1),
                view.findViewById(R.id.tv_date2),
                view.findViewById(R.id.tv_date3),
                view.findViewById(R.id.tv_date4),
                view.findViewById(R.id.tv_date5),
                view.findViewById(R.id.tv_date6),
                view.findViewById(R.id.tv_date7)
        };
        view.findViewById(R.id.grade_select).setOnClickListener(this);
    }

    public void bindData(GradeCalendarCard gradeCalendar) {
        this.gradeCalendar = gradeCalendar;
        gradeTv.setText(gradeCalendar.grade.resId);
        Pair<Integer, int[]> pair = DateUtil.getCurrWeekDate();

        for (int i = 0; i < pair.second.length; i++) {
            if (i < pair.first) {
                calendarViewArr[i].setTextColor(context.getResources().getColor(R.color._c1c1c1));
                calendarViewArr[i].setBgColor(context.getResources().getColor(android.R.color.transparent));
            } else if (i > pair.first) {
                calendarViewArr[i].setTextColor(context.getResources().getColor(R.color._4b4b4b));
                calendarViewArr[i].setBgColor(context.getResources().getColor(android.R.color.transparent));
            } else {
                calendarViewArr[i].setTextColor(context.getResources().getColor(R.color._ffffff));
                calendarViewArr[i].setBgColor(context.getResources().getColor(R.color._4989ff));
            }
            calendarViewArr[i].setText(String.valueOf(pair.second[i]));
            calendarViewArr[i].setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.grade_select:
                callback.onGradeClick(gradeCalendar.grade);
                break;
        }
    }

    public interface Callback {
        void onGradeClick(Grade grade);
    }
}
