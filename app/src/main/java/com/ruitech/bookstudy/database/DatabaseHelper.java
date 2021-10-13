package com.ruitech.bookstudy.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ruitech.bookstudy.App;
import com.ruitech.bookstudy.utils.ListUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    private static volatile DatabaseHelper instance;

    public static final int version = 1;
    private static final String name = "study.db";
    private DatabaseHelper(Context context, String name) {
        super(context, name, null, version);
    }

    public static DatabaseHelper getInstance() {
        if (instance == null) {
            synchronized (DatabaseHelper.class) {
                if (instance == null) {
                    instance = new DatabaseHelper(App.applicationContext(), name);
                }
            }
        }
        return instance;
    }

    public static final String TABLE_BOOKS = "books";
    public static final String TABLE_DOWNLOAD_BOOKS = "download_books";

    public static final String ID = "id";
    public static final String GENUINE_ID = "genuine_id";
    public static final String URL = "url";
    public static final String POSTER = "poster";
    public static final String GRADE = "grade";
    public static final String SUBJECT = "subject";
    public static final String SEMESTER = "semester";
    public static final String BOOK_VERSION = "book_version";

    public static final String DOWNLOAD_STATE = "download_state";
    public static final int STATE_DOWNLOAD_STARTED = 1;
    public static final int STATE_DOWNLOAD_SUCC = 2;
    public static final int STATE_DOWNLOAD_ERR = 3;

    public static final String[] PROJECTION_BOOKS;
    public static final String[] PROJECTION_DOWNLOAD_BOOKS;
    static {
        ArrayList<String> list = ListUtils.newArrayList(
                ID,
                GENUINE_ID,
                URL,
                POSTER,
                GRADE,
                SUBJECT,
                SEMESTER,
                BOOK_VERSION);

        PROJECTION_BOOKS = new String[list.size()];
        list.toArray(PROJECTION_BOOKS);

        list.add(DOWNLOAD_STATE);
        PROJECTION_DOWNLOAD_BOOKS = new String[list.size()];
        list.toArray(PROJECTION_DOWNLOAD_BOOKS);
    }

    private static final String CREATE_TABLE_BOOKS_TEMPLATE = "CREATE TABLE " + "%1" +
            "(" +
            ID + " TEXT NOT NULL UNIQUE," +
            GENUINE_ID + " TEXT NOT NULL," +
            URL + " TEXT NOT NULL," +
            POSTER + " TEXT," +
            GRADE + " TEXT NOT NULL," +
            SUBJECT + " TEXT NOT NULL," +
            SEMESTER + " TEXT NOT NULL," +
            BOOK_VERSION + " TEXT NOT NULL" +
            "%2" +
            ")";

    private static final String CREATE_TABLE_BOOKS = CREATE_TABLE_BOOKS_TEMPLATE
            .replaceFirst("%1", TABLE_BOOKS)
            .replaceFirst("%2", "");


    private static final String CREATE_TABLE_DOWNLOAD_BOOKS = CREATE_TABLE_BOOKS_TEMPLATE
            .replaceFirst("%1", TABLE_DOWNLOAD_BOOKS)
            .replaceFirst("%2", "," + DOWNLOAD_STATE + " INTEGER DEFAULT " + STATE_DOWNLOAD_STARTED);

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_BOOKS);
        db.execSQL(CREATE_TABLE_DOWNLOAD_BOOKS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
