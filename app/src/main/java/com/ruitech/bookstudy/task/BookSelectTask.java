package com.ruitech.bookstudy.task;

import android.os.AsyncTask;
import android.util.Log;

import com.ruitech.bookstudy.BookDao;
import com.ruitech.bookstudy.bean.Book;

public class BookSelectTask extends AsyncTask<Object, Object, Boolean> {
    private static final String TAG = "BookSelectTask";
    private Book book;
    private boolean selectOp;
    private Callback callback;
    public BookSelectTask(Book book, boolean selectOp, Callback callback) {
        this.book = book;
        this.selectOp = selectOp;
        this.callback = callback;
    }

    @Override
    protected Boolean doInBackground(Object... objects) {
        Log.d(TAG, "doInBackground: " + selectOp);
        if (selectOp) {
            return BookDao.updateSelectedBook(book);
        } else {
            return BookDao.cancelSelectedBook(book);
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        Log.d(TAG, "onPostExecute: " + book + " " + selectOp + " " + result);
        callback.onSelected(book, selectOp, result);
    }

    public interface Callback {
        void onSelected(Book book, boolean selectOp, boolean succ);
    }
}
