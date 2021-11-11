package com.ruitech.bookstudy.desktop.bean;

import com.ruitech.bookstudy.bean.Poster;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class Album extends CommonBean {

    private static final String TAG = "Album";
    public List<Poster> posterList;
    public List<Video> videoList;

    public static Album initFromJson(JSONObject jsonObject) {
        return initFromJson(new Album(), jsonObject);
    }
    public static Album initFromJson(Album album, JSONObject jsonObject) {
        album.id = jsonObject.optString("id");
        album.name = jsonObject.optString("albumName").replace("\n", "");
        android.util.Log.d(TAG, "initFromJson: [" + album.name + "]");

        album.videoList = new LinkedList<>();
        JSONArray jsonArray = jsonObject.optJSONArray("videoInfos");
        for (int i = 0; i < jsonArray.length(); i++) {
            Video video = Video.initFromJson(jsonArray.optJSONObject(i));
            album.videoList.add(video);
        }
        return album;
    }
}
