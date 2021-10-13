package com.ruitech.bookstudy.bean;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.ruitech.bookstudy.database.DatabaseHelper;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class Book implements Serializable {
    private static final long serialVersionUID = 7485617959464582502L;

    private String id;
    private String genuineId;
    private String url;
    private List<Poster> posterList = Collections.singletonList(new Poster());
    private Grade grade;
    private Subject subject;
    private Semester semester;
    private BookVersion bookVersion;

    public Book(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getGenuineId() {
//        ensureGenuineId();
        return genuineId;
    }

//    private void ensureGenuineId() {
//        int index = url.lastIndexOf("/");
//        if (index > 0) {
//            genuineId = url.substring(index + 1).split("\\.")[0];
//        }
//    }

    public String getUrl() {
        return url;
    }

    public List<Poster> getPosterList() {
        return posterList;
    }

    public Grade getGrade() {
        return grade;
    }

    public Subject getSubject() {
        return subject;
    }

    public Semester getSemester() {
        return semester;
    }

    public BookVersion getBookVersion() {
        return bookVersion;
    }

    public String getDesc(Context context) {
        return context.getResources().getString(grade.resId) + " " +
                context.getResources().getString(semester.abbrResId) + "-" +
                bookVersion.getName();
    }

    public static Book from(JSONObject jsonObject) {
        Book ret = null;

        String id = jsonObject.optString("id");
        String genuineId = jsonObject.optString("resourceIdentity");
        String url = jsonObject.optString("resourceUrl");
        Grade grade = Grade.from(jsonObject.optString("grade"));
        Subject subject = Subject.from(jsonObject.optString("subject"));
        Semester semester = Semester.from(jsonObject.optString("schoolYear"));
        String bookVersionId = jsonObject.optString("textbookVersion");
        String bookVersionName = jsonObject.optString("textbookVersionName");
        List<Poster> posterList = Poster.initFromJson(jsonObject.optJSONArray("posters"));

        if (grade != null) {
            ret = new Book(id);
            ret.genuineId = genuineId;
            ret.grade = grade;
            ret.subject = subject;
            ret.semester = semester;
            ret.bookVersion = new BookVersion(bookVersionId, bookVersionName);
            ret.posterList = posterList;
            ret.url = url;
        }
        return ret;
    }

    public void to(ContentValues cv) {
        cv.put(DatabaseHelper.ID, id);
        cv.put(DatabaseHelper.GENUINE_ID, getGenuineId());
        cv.put(DatabaseHelper.URL, url);
        Poster.to(cv, posterList);
        cv.put(DatabaseHelper.GRADE, grade.id);
        cv.put(DatabaseHelper.SUBJECT, subject.id);
        cv.put(DatabaseHelper.SEMESTER, semester.id);
        bookVersion.to(cv);
        cv.put(DatabaseHelper.ID, id);
    }

    public static Book from(Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ID));
        Book book = new Book(id);
        book.genuineId = cursor.getString(cursor.getColumnIndex(DatabaseHelper.GENUINE_ID));
        book.url = cursor.getString(cursor.getColumnIndex(DatabaseHelper.URL));
        book.posterList = Poster.from(cursor);
        book.grade = Grade.from(cursor.getString(cursor.getColumnIndex(DatabaseHelper.GRADE)));
        book.subject = Subject.from(cursor.getString(cursor.getColumnIndex(DatabaseHelper.SUBJECT)));
        book.semester = Semester.from(cursor.getString(cursor.getColumnIndex(DatabaseHelper.SEMESTER)));
        book.bookVersion = BookVersion.from(cursor);
        return book;
    }

    @Override
    public boolean equals(Object object) {
        return (object instanceof Book) && ((Book) object).getId().equals(id);
    }

    @Override
    public String toString() {
        return id + " " + getGenuineId() + " " + grade + " " + subject + " " + semester + " " + bookVersion + " " + url;
    }
}
