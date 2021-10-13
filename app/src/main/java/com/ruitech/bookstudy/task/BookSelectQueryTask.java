package com.ruitech.bookstudy.task;

import android.os.AsyncTask;
import android.util.Log;

import com.ruitech.bookstudy.BookDao;
import com.ruitech.bookstudy.bean.Book;
import com.ruitech.bookstudy.bean.Grade;
import com.ruitech.bookstudy.bean.Subject;

public class BookSelectQueryTask extends AsyncTask<Object, Object, Book> {
    private static final String TAG = "BookSelectQueryTask";
    private Subject subject;
    private Grade grade;
    private Callback callback;
    public BookSelectQueryTask(Subject subject, Grade grade, Callback callback) {
        this.subject = subject;
        this.grade = grade;
        this.callback = callback;
    }

    @Override
    protected Book doInBackground(Object... objects) {
        return BookDao.getSelectedBook(subject, grade);
    }

    @Override
    protected void onPostExecute(Book result) {
        callback.onSelectQuery(subject, grade, result);
    }

    public interface Callback {
        void onSelectQuery(Subject subject, Grade grade, Book book);
    }
}
