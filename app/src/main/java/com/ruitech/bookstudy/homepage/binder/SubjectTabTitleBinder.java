package com.ruitech.bookstudy.homepage.binder;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ruitech.bookstudy.R;
import com.ruitech.bookstudy.bean.Subject;
import com.ruitech.bookstudy.binder.IUISame;
import com.ruitech.bookstudy.homepage.binder.bean.SubjectTab;
import com.ruitech.bookstudy.homepage.binder.bean.SubjectTabUI;
import com.ruitech.bookstudy.utils.UIHelper;
import com.ruitech.bookstudy.widget.RoundAngleTextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import me.drakeet.multitype.ItemViewBinder;

public class SubjectTabTitleBinder extends ItemViewBinder<SubjectTabUI, SubjectTabTitleBinder.ViewHolder> implements IUISame<SubjectTabUI> {

    private static final String TAG = "SubjectTabUITitleBinder";

    private Callback callback;
    public SubjectTabTitleBinder(Callback callback) {
        this.callback = callback;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View v = inflater.inflate(R.layout.binder_subject_tab_title, parent, false);
        return new ViewHolder(v);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull SubjectTabUI item) {
        holder.bindData(item);
    }

    @Override
    public boolean areUISame(@NonNull SubjectTabUI oldItem, @NonNull SubjectTabUI newItem) {
        return oldItem.equals(newItem);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Context context;

        private RoundAngleTextView title;
        private SubjectTab subjectTab;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            title = itemView.findViewById(R.id.title);
            title.setOnClickListener(this);
            selectedBgDrawable.setColor(context.getResources().getColor(R.color._f7b500));
            selectedBgDrawable.setCornerRadius(UIHelper.dp2px(8));
        }

        private GradientDrawable selectedBgDrawable = new GradientDrawable();
        public void bindData(SubjectTabUI subjectTabUI) {
            this.subjectTab = subjectTabUI.getValue();
            title.setText(subjectTab.getSubject().resId);
            if (subjectTabUI.isSelected()) {
                title.setTextColor(context.getResources().getColor(R.color._ffffff));
                title.setBackground(selectedBgDrawable);
            } else {
                title.setTextColor(context.getResources().getColor(R.color._f7b500));
                title.setBackground(context.getResources().getDrawable(R.mipmap.bg_unselected));
            }
        }

        @Override
        public void onClick(View v) {
            callback.onTabTitleClicked(getLayoutPosition(), subjectTab.getSubject());
        }
    }

    public interface Callback {
        void onTabTitleClicked(int position, Subject subject);
    }
}
