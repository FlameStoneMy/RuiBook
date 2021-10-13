package com.ruitech.bookstudy.binder;

import android.content.Context;
import android.graphics.drawable.PaintDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ruitech.bookstudy.R;
import com.ruitech.bookstudy.bean.Book;
import com.ruitech.bookstudy.task.BookSelectTask;
import com.ruitech.bookstudy.uibean.BookUI;
import com.ruitech.bookstudy.utils.DisplayOptions;
import com.ruitech.bookstudy.utils.Executors;
import com.ruitech.bookstudy.utils.ImageHelper;
import com.ruitech.bookstudy.utils.UIHelper;
import com.ruitech.bookstudy.widget.BookView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import me.drakeet.multitype.ItemViewBinder;

public class BookBinder extends ItemViewBinder<BookUI, BookBinder.ViewHolder> implements IUISame<BookUI> {

    private static final String TAG = "BookUIBinder";

    private Callback callback;
    public BookBinder(Callback callback) {
        this.callback = callback;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View v = inflater.inflate(R.layout.binder_book, parent, false);
        return new ViewHolder(v);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull BookUI item) {
        holder.bindData(item);
    }

    @Override
    public boolean areUISame(@NonNull BookUI oldItem, @NonNull BookUI newItem) {
        return oldItem.equals(newItem);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, BookSelectTask.Callback {
        private Context context;
        private BookView bookView;
        private TextView titleTv;
        private TextView descTv;
        private TextView opTv;
        private PaintDrawable btnBgDrawable;

        private BookUI bookUI;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            bookView = itemView.findViewById(R.id.img);
            titleTv = itemView.findViewById(R.id.title);
            descTv = itemView.findViewById(R.id.desc);
            opTv = itemView.findViewById(R.id.btn);
            btnBgDrawable = new PaintDrawable();
            btnBgDrawable.setCornerRadius(UIHelper.dp2px(16));
            opTv.setBackground(btnBgDrawable);
            itemView.setOnClickListener(this);
        }

        public void bindData(BookUI bookUI) {
            this.bookUI = bookUI;
            Book book = bookUI.getValue();
            android.util.Log.d(TAG, "bindData: " + getLayoutPosition() + " " + book);
            titleTv.setText(book.getBookVersion().getName());
            descTv.setText(
                    context.getResources().getString(book.getGrade().resId) + " " +
                    context.getResources().getString(book.getSemester().abbrResId));
            if (bookUI.isSelected()) {
                opTv.setText(R.string.remove_book);
                opTv.setTextColor(context.getResources().getColor(R.color._7b7d82));
                btnBgDrawable.getPaint().setColor(itemView.getResources().getColor(R.color._e9f4fb));
            } else {
                opTv.setText(R.string.add_book);
                opTv.setTextColor(context.getResources().getColor(R.color._ffffff));
                btnBgDrawable.getPaint().setColor(itemView.getResources().getColor(R.color._02abff));
            }

            bookView.bindData(book);
        }

        @Override
        public void onClick(View v) {
            new BookSelectTask(bookUI.getValue(), !bookUI.isSelected(), this).executeOnExecutor(Executors.io());
        }

        @Override
        public void onSelected(Book book, boolean selectOp, boolean succ) {
            if (succ) {
                if (selectOp) {
                    callback.onBookSelected(getLayoutPosition(), bookUI.getValue());
                } else {
                    callback.onBookSelected(-1, null);
                }
            }
        }
    }

    public interface Callback {
        void onBookSelected(int pos, Book book);
    }
}
