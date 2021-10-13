package com.ruitech.bookstudy.bean;

import android.content.ContentValues;
import android.database.Cursor;

import com.ruitech.bookstudy.database.DatabaseHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class BookVersion implements Serializable {
    private static final long serialVersionUID = 3249479278459688683L;

    private String id; // PEP
    private String name; // 人教版
    public BookVersion(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return id;
    }

    public static BookVersion from(Cursor cursor) {
        String bookVersionStr = cursor.getString(cursor.getColumnIndex(DatabaseHelper.BOOK_VERSION));
        BookVersion ret = null;
        try {
            JSONObject jsonObject = new JSONObject(bookVersionStr);
            ret = initFromJSON(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return ret;
    }

    public void to(ContentValues cv) {
        cv.put(DatabaseHelper.BOOK_VERSION, toJson(this).toString());
    }

    public static JSONObject toJson(BookVersion bookVersion) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", bookVersion.id);
            jsonObject.put("name", bookVersion.name);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }



    public static BookVersion initFromJSON(JSONObject jsonObject) {
        return new BookVersion(
                jsonObject.optString("id"),
                jsonObject.optString("name"));
    }
}
