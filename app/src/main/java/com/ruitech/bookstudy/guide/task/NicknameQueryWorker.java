package com.ruitech.bookstudy.guide.task;

import com.ruitech.bookstudy.bean.Gender;
import com.ruitech.bookstudy.utils.Const;

import org.json.JSONException;
import org.json.JSONObject;

public class NicknameQueryWorker extends NicknameBaseQueryWorker {
    private static final String TAG = "NicknameQueryWorker";

    public NicknameQueryWorker(Callback callback) {
        super(callback);
    }

    @Override
    protected String getUrl() {
        return Const.NICKNAME_LIST_QUERY_URL;
    }

    @Override
    protected void parseInternal(JSONObject jsonObject) throws JSONException {
        for (Gender gender : Gender.values()) {
            if (jsonObject.has(gender.id)) {
                parseNicknameInternal(jsonObject, gender);
            }
        }
    }
}
