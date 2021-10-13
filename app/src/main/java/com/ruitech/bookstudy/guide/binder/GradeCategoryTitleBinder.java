package com.ruitech.bookstudy.guide.binder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ruitech.bookstudy.R;
import com.ruitech.bookstudy.bean.GradeCategory;
import com.ruitech.bookstudy.binder.IUISame;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import me.drakeet.multitype.ItemViewBinder;

public class GradeCategoryTitleBinder extends ItemViewBinder<GradeCategory, GradeCategoryTitleBinder.ViewHolder> implements IUISame<GradeCategory> {
    private static final String TAG = "GradeCategoryTitleBinder";

    public GradeCategoryTitleBinder() {
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View v = inflater.inflate(R.layout.binder_title, parent, false);
        return new ViewHolder(v);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull GradeCategory item) {
        holder.bindData(item);
    }

    @Override
    public boolean areUISame(@NonNull GradeCategory oldItem, @NonNull GradeCategory newItem) {
        return oldItem == newItem;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
        }

        public void bindData(GradeCategory gradeCategory) {
            title.setText(gradeCategory.resId);
        }
    }
}
