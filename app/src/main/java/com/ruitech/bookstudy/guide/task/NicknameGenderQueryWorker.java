package com.ruitech.bookstudy.guide.task;

import com.ruitech.bookstudy.bean.Gender;
import com.ruitech.bookstudy.utils.Const;

import org.json.JSONException;
import org.json.JSONObject;

public class NicknameGenderQueryWorker extends NicknameBaseQueryWorker {
    private static final String TAG = "NicknameGenderQueryWorker";
    private Gender gender;
    public NicknameGenderQueryWorker(Gender gender, Callback callback) {
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
