package com.ruitech.bookstudy.bookselect;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ruitech.bookstudy.R;
import com.ruitech.bookstudy.bean.Book;
import com.ruitech.bookstudy.binder.IUISame;
import com.ruitech.bookstudy.widget.BookView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import me.drakeet.multitype.ItemViewBinder;

public class BookCurrentBinder extends ItemViewBinder<BookCurrentBinder.BookWrapper, BookCurrentBinder.ViewHolder> implements IUISame<BookCurrentBinder.BookWrapper> {

    private static final String TAG = "BookCurrentBinder";

    public static class BookWrapper {
        public Book book;
        private boolean loading;
        public BookWrapper() {
        }

        public void setBook(Book book) {
            this.book = book;
            if (book == null) {
                loading = false;
            }
        }

        public boolean hasBook() {
            return book != null;
        }

        public void setLoading(boolean loading) {
            this.loading = loading;
        }

        public boolean isLoading() {
            return loading;
        }
    }

    private Callback callback;
    public BookCurrentBinder(Callback callback) {
        this.callback = callback;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View v = inflater.inflate(R.layout.binder_current_book, parent, false);
        return new ViewHolder(v);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull BookWrapper item) {
        holder.bindData(item);
    }

    @Override
    public boolean areUISame(@NonNull BookWrapper oldItem, @NonNull BookWrapper newItem) {
        return oldItem.equals(newItem);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Context context;
        private TextView titleTv;
        private TextView descTv;
        private BookView bookView;

        private Book book;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            titleTv = itemView.findViewById(R.id.title);
            descTv = itemView.findViewById(R.id.desc);

            bookView = itemView.findViewById(R.id.img);
            itemView.setOnClickListener(this);
        }

        public void bindData(BookWrapper bookWrapper) {
            this.book = bookWrapper.book;
            android.util.Log.d(TAG, "bindData: " + book + " " + bookWrapper.isLoading());
            bookView.bindData(book);
            if (bookWrapper.hasBook()) {
                titleTv.setText(book.getBookVersion().getName());
                descTv.setText(
                        context.getResources().getString(book.getGrade().resId) + " " +
                                context.getResources().getString(book.getSemester().abbrResId));
                titleTv.setVisibility(View.VISIBLE);
                descTv.setVisibility(View.VISIBLE);
            } else {
                titleTv.setVisibility(View.INVISIBLE);
                descTv.setVisibility(View.INVISIBLE);
            }
            bookView.setLoading(bookWrapper.isLoading());
        }

        @Override
        public void onClick(View v) {
            if (book != null) {
                callback.onCurrentBookClicked(book);
            }
        }
    }

    public interface Callback {
        void onCurrentBookClicked(Book book);
    }
}
