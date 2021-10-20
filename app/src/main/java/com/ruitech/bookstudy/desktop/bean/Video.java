package com.ruitech.bookstudy.desktop.bean;

import com.ruitech.bookstudy.bean.Poster;

import org.json.JSONObject;

import java.util.List;

public class Video extends CommonBean {
    public String url;
    public List<Poster> posterList;

    public static Video initFromJson(JSONObject jsonObject) {
        Video v = new Video();
        v.id = jsonObject.optString("id");
        v.name = jsonObject.optString("videoName");
        v.url = jsonObject.optString("url");
        v.posterList = Poster.from(jsonObject.optString("content"));

        return v;
    }
}
