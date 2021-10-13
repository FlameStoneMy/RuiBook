package com.ruitech.bookstudy.guide.binder;

import android.content.Context;
import android.graphics.drawable.PaintDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ruitech.bookstudy.R;
import com.ruitech.bookstudy.binder.IUISame;
import com.ruitech.bookstudy.uibean.NicknameUI;
import com.ruitech.bookstudy.utils.UIHelper;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import me.drakeet.multitype.ItemViewBinder;

public class NicknameBinder extends ItemViewBinder<NicknameUI, NicknameBinder.ViewHolder> implements IUISame<NicknameUI> {
    private static final String TAG = "NicknameBinder";

    private Callback callback;
    public NicknameBinder(Callback callback) {
        this.callback = callback;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View v = inflater.inflate(R.layout.binder_selectable_title, parent, false);
        return new ViewHolder(v);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull NicknameUI item) {
        holder.bindData(item);
    }

    @Override
    public boolean areUISame(@NonNull NicknameUI oldItem, @NonNull NicknameUI newItem) {
        return oldItem.equals(newItem);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView title;
        private PaintDrawable paintDrawable;
        private Context context;
        private NicknameUI nicknameUI;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            title = itemView.findViewById(R.id.title);
            paintDrawable = new PaintDrawable();
            paintDrawable.setCornerRadius(UIHelper.dp2px(18));
            title.setBackground(paintDrawable);
            itemView.setOnClickListener(this);
        }

        public void bindData(NicknameUI nicknameUI) {
            Log.d(TAG, "bindData: " + nicknameUI.getValue());
            this.nicknameUI = nicknameUI;
            title.setText(nicknameUI.getValue());
            if (nicknameUI.isSelected()) {
                paintDrawable.getPaint().setColor(context.getResources().getColor(R.color._ffae4e));
                title.setTextColor(context.getResources().getColor(R.color._ffffff));
            } else {
                paintDrawable.getPaint().setColor(context.getResources().getColor(R.color._f5f3f6));
                title.setTextColor(context.getResources().getColor(R.color._b3b3b3));
            }
        }

        @Override
        public void onClick(View v) {
            callback.onNicknameClick(getLayoutPosition(), nicknameUI);
        }
    }

    public interface Callback {
        void onNicknameClick(int position, NicknameUI nicknameUI);
    }
}
