package com.ruitech.bookstudy.bookselect;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ruitech.bookstudy.R;
import com.ruitech.bookstudy.bean.Book;
import com.ruitech.bookstudy.binder.IUISame;
import com.ruitech.bookstudy.utils.DisplayOptions;
import com.ruitech.bookstudy.utils.ImageHelper;
import com.ruitech.bookstudy.utils.UIHelper;
import com.ruitech.bookstudy.utils.UIUtil;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import me.drakeet.multitype.ItemViewBinder;

public class AddBookHintBinder extends ItemViewBinder<AddBookHintBinder.AddBookHint, AddBookHintBinder.ViewHolder> implements IUISame<AddBookHintBinder.AddBookHint> {

    public static final class AddBookHint {
    }

    private static final String TAG = "BookBinder";

    public AddBookHintBinder() {
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View v = inflater.inflate(R.layout.binder_add_book_hint, parent, false);
        return new ViewHolder(v);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull AddBookHint item) {
        holder.bindData(item);
    }

    @Override
    public boolean areUISame(@NonNull AddBookHint oldItem, @NonNull AddBookHint newItem) {
        return true;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Context context = itemView.getContext();

            int[] colorArr = new int[] {
                    context.getResources().getColor(R.color._4989ff),
                    context.getResources().getColor(android.R.color.transparent)};
            GradientDrawable leftGradient = new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, colorArr);
            leftGradient.setCornerRadius(UIHelper.dp2px(2));
            GradientDrawable rightGradient = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colorArr);
            rightGradient.setCornerRadius(UIHelper.dp2px(2));
            itemView.findViewById(R.id.left_divider).setBackground(leftGradient);
            itemView.findViewById(R.id.right_divider).setBackground(rightGradient);
        }

        public void bindData(AddBookHint addBookHint) {
        }
    }
}
