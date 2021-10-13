package com.ruitech.bookstudy;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ruitech.bookstudy.bean.Book;
import com.ruitech.bookstudy.bean.Grade;
import com.ruitech.bookstudy.bean.Subject;
import com.ruitech.bookstudy.database.DatabaseHelper;

public class BookDao {

    private static final String TAG = "BookDao";

    public static Book getSelectedBook(Subject subject, Grade grade) {
        Book ret = null;
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        try (Cursor cursor = db.query(DatabaseHelper.TABLE_BOOKS,
                DatabaseHelper.PROJECTION_BOOKS,
                DatabaseHelper.SUBJECT + " = '" + subject.id + "' AND " + DatabaseHelper.GRADE + " = '" + grade.id + "'",
                null, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                ret = Book.from(cursor);
            }
        }

        Log.d(TAG, "getSelectedBook: " + subject + " " + grade + " " + ret);
        return ret;
    }

    public static boolean updateSelectedBook(Book book) {
        Log.d(TAG, "updateSelectedBook: " + book);
        boolean succ;
        ContentValues cv = new ContentValues();
        book.to(cv);
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        if (db.update(DatabaseHelper.TABLE_BOOKS, cv,
                DatabaseHelper.SUBJECT + " = '" + book.getSubject().id + "' AND " + DatabaseHelper.GRADE + " = '" + book.getGrade().id + "'",
                null) > 0) {
            succ = true;
        } else {
            succ = db.insert(DatabaseHelper.TABLE_BOOKS, null, cv) >= 0;
        }

        Log.d(TAG, "updateSelectedBook: " + succ);
        return succ;
    }

    public static boolean cancelSelectedBook(Book book) {
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        return db.delete(DatabaseHelper.TABLE_BOOKS,
                DatabaseHelper.SUBJECT + " = '" + book.getSubject().id + "' AND " + DatabaseHelper.GRADE + " = '" + book.getGrade().id + "'",
                null) > 0;
    }

    public static boolean updateDownloadRet(Book book, boolean ret) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.DOWNLOAD_STATE, ret ? DatabaseHelper.STATE_DOWNLOAD_SUCC : DatabaseHelper.STATE_DOWNLOAD_ERR);
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        boolean succ = db.update(DatabaseHelper.TABLE_DOWNLOAD_BOOKS, cv,
                DatabaseHelper.ID + " = '" + book.getId() + "'",
                null) > 0;
        Log.d(TAG, "updateDownloadRet: " + ret);
        return succ;
    }

    public static boolean isDownloaded(Book book) {
        boolean ret = false;
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        try (Cursor cursor = db.query(DatabaseHelper.TABLE_DOWNLOAD_BOOKS,
                new String[] {DatabaseHelper.DOWNLOAD_STATE},
                DatabaseHelper.ID + " = '" + book.getId() + "'",
                null, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                Log.d(TAG, "isDownloaded22: " + cursor.getInt(cursor.getColumnIndex(DatabaseHelper.DOWNLOAD_STATE)));
                ret = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.DOWNLOAD_STATE)) == DatabaseHelper.STATE_DOWNLOAD_SUCC;
            }
        }

        Log.d(TAG, "isDownloaded: " + book + " " + ret);
        return ret;
    }

    public static boolean startDownload(Book book) {
        boolean succ;
        ContentValues cv = new ContentValues();
        book.to(cv);
        cv.put(DatabaseHelper.DOWNLOAD_STATE, DatabaseHelper.STATE_DOWNLOAD_STARTED);
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        if (db.update(DatabaseHelper.TABLE_DOWNLOAD_BOOKS, cv,
                DatabaseHelper.ID + " = '" + book.getId() + "'",
                null) > 0) {
            succ = true;
        } else {
            succ = db.insert(DatabaseHelper.TABLE_DOWNLOAD_BOOKS, null, cv) >= 0;
        }

        Log.d(TAG, "startDownload: " + succ);
        return succ;
    }
}
