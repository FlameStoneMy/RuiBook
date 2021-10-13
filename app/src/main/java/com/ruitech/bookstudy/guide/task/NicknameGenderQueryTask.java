package com.ruitech.bookstudy.guide.task;

import com.ruitech.bookstudy.bean.Gender;
import com.ruitech.bookstudy.utils.Const;
import com.ruitech.bookstudy.utils.NetworkResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class NicknameGenderQueryTask extends NicknameBaseQueryTask {
    private static final String TAG = "NicknameGenderQueryTask";
    private Gender gender;
    public NicknameGenderQueryTask(Gender gender, Callback callback) {
        super(callback);
        this.gender = gender;
    }

    @Override
    protected String getUrl() {
        return Const.getNickNameQueryUrl(gender);
    }

    @Override
    protected void parseInternal(JSONObject jsonObject) throws JSONException {
        parseNicknameInternal(jsonObject, gender);
    }
}
