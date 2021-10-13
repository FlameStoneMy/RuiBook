package com.ruitech.bookstudy.guide.binder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ruitech.bookstudy.R;
import com.ruitech.bookstudy.bean.Gender;
import com.ruitech.bookstudy.bean.GradeCategory;
import com.ruitech.bookstudy.binder.IUISame;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import me.drakeet.multitype.ItemViewBinder;

public class GenderTitleBinder extends ItemViewBinder<Gender, GenderTitleBinder.ViewHolder> implements IUISame<Gender> {
    private static final String TAG = "GenderTitleBinder";

    private Callback callback;
    public GenderTitleBinder(Callback callback) {
        this.callback = callback;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View v = inflater.inflate(R.layout.binder_changable_title, parent, false);
        return new ViewHolder(v);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull Gender item) {
        holder.bindData(item);
    }

    @Override
    public boolean areUISame(@NonNull Gender oldItem, @NonNull Gender newItem) {
        return oldItem == newItem;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView title;
        private Gender gender;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            itemView.findViewById(R.id.change).setOnClickListener(this);
        }

        public void bindData(Gender gender) {
            this.gender = gender;
            title.setText(gender.resId);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.change:
                    callback.onChangeClicked(gender);
                    break;
            }
        }
    }

    public interface Callback {
        void onChangeClicked(Gender gender);
    }
}
