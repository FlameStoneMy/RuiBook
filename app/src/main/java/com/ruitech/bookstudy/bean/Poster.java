package com.ruitech.bookstudy.bean;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.ruitech.bookstudy.database.DatabaseHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Poster implements Serializable {

    public static final String TYPE_PORTRAIT = "portrait";
    public static final String TYPE_LANDSCAPE = "landscape";
    public static final String TYPE_SQUARE = "square";
    private static final long serialVersionUID = 2003792282366703944L;

    private int width = 200;
    private int height = 200;
    private String url = "https://qqcdnpictest.mxplay.com/pic/gc_10/hi/1x1/312x312/gc_10_400_400.webp";
    private String type = TYPE_PORTRAIT;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static Poster initFromJson(JSONObject jsonObject) {
        Poster poster = new Poster();
        poster.width = jsonObject.optInt("width", -1);
        poster.height = jsonObject.optInt("height", -1);
        String url = jsonObject.optString("url");
        poster.type = jsonObject.optString("type");

        if (!TextUtils.isEmpty(url)) {
            poster.url = url;
        }
        return poster;
    }

    public static List<Poster> initFromJson(JSONArray jsonArray) {
        List<Poster> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                Poster poster = initFromJson(jsonArray.getJSONObject(i));
                list.add(poster);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public static JSONObject toJson(Poster poster) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("width", poster.width);
            jsonObject.put("height", poster.height);
            jsonObject.put("url", poster.url);
            jsonObject.put("type", poster.type);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    public static JSONArray toJson(List<Poster> list) {
        JSONArray jsonArray = new JSONArray();
        for (Poster poster : list) {
            JSONObject jsonObject = toJson(poster);
            jsonArray.put(jsonObject);
        }
        return jsonArray;
    }

    public static void to(ContentValues cv, List<Poster> posterList) {
        cv.put(DatabaseHelper.POSTER, toJson(posterList).toString());
    }

    public static List<Poster> from(Cursor cursor) {
        List<Poster> ret = null;
        String posterListStr = cursor.getString(cursor.getColumnIndex(DatabaseHelper.POSTER));
        try {
            JSONArray posterListArr = new JSONArray(posterListStr);
            ret = initFromJson(posterListArr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ret;
    }
}
