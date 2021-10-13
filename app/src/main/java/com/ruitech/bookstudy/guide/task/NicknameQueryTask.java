package com.ruitech.bookstudy.guide.task;

import com.ruitech.bookstudy.bean.Gender;
import com.ruitech.bookstudy.utils.Const;
import com.ruitech.bookstudy.utils.NetworkResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class NicknameQueryTask extends NicknameBaseQueryTask {
    private static final String TAG = "NicknameQueryTask";

    public NicknameQueryTask(Callback callback) {
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
