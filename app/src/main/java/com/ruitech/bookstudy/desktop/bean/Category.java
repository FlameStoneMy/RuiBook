package com.ruitech.bookstudy.desktop.bean;

import com.ruitech.bookstudy.bean.Poster;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class Category extends CommonBean {

    public List<Album> albumList;

    public static Category initFromJson(JSONObject jsonObject) {
        if (jsonObject != null) {
            return initFromJson(new Category(), jsonObject);
        } else {
            return null;
        }
    }

    public static Category initFromJson(Category category, JSONObject jsonObject) {
        category.albumList = new LinkedList<>();
        JSONArray jsonArray = jsonObject.optJSONArray("recordList");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObj = jsonArray.optJSONObject(i);
            Album album = new Album();
            album.id = jsonObj.optString("videoAlbumId");
            album.name = jsonObj.optString("videoAlbumName").replace("\n", "");
            album.posterList = Poster.from(jsonObj.optString("coverImage"));
            category.albumList.add(album);
        }
        return category;
    }
}
