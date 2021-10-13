package com.ruitech.bookstudy.homepage.binder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ruitech.bookstudy.R;
import com.ruitech.bookstudy.bean.Grade;
import com.ruitech.bookstudy.binder.IUISame;
import com.ruitech.bookstudy.homepage.binder.bean.GradeCalendarCard;
import com.ruitech.bookstudy.utils.DateUtil;
import com.ruitech.bookstudy.widget.CircleTextView;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;
import me.drakeet.multitype.ItemViewBinder;

public class GradeCalendarBinder extends ItemViewBinder<GradeCalendarCard, GradeCalendarBinder.ViewHolder> implements IUISame<GradeCalendarCard> {

    private static final String TAG = "GradeCalendarBinder";

    private Callback callback;
    public GradeCalendarBinder(Callback callback) {
        this.callback = callback;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View v = inflater.inflate(R.layout.card_grade_calendar, parent, false);
        return new ViewHolder(v);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull GradeCalendarCard item) {
        holder.bindData(item);
    }

    @Override
    public boolean areUISame(@NonNull GradeCalendarCard oldItem, @NonNull GradeCalendarCard newItem) {
        return oldItem.equals(newItem);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Context context;

        private GradeCalendarCard gradeCalendar;
        private TextView gradeTv;
        private CircleTextView[] calendarViewArr;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            gradeTv = itemView.findViewById(R.id.grade_select);
            calendarViewArr = new CircleTextView[] {
                itemView.findViewById(R.id.tv_date1),
                itemView.findViewById(R.id.tv_date2),
                itemView.findViewById(R.id.tv_date3),
                itemView.findViewById(R.id.tv_date4),
                itemView.findViewById(R.id.tv_date5),
                itemView.findViewById(R.id.tv_date6),
                itemView.findViewById(R.id.tv_date7)
            };
            itemView.findViewById(R.id.grade_select).setOnClickListener(this);
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
    }

    public interface Callback {
        void onGradeClick(Grade grade);
    }
}
