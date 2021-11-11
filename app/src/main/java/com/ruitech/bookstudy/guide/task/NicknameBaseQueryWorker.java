package com.ruitech.bookstudy.guide.task;

import android.os.AsyncTask;
import android.util.Log;

import com.ruitech.bookstudy.RuiWorker;
import com.ruitech.bookstudy.bean.Gender;
import com.ruitech.bookstudy.utils.APIUtil;
import com.ruitech.bookstudy.utils.NetworkResponse;
import com.ruitech.bookstudy.utils.UrlInvalidException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;

import okhttp3.Response;

public abstract class NicknameBaseQueryWorker extends RuiWorker<NetworkResponse> {
    private static final String TAG = "NicknameBaseQueryWorker";
    protected EnumMap<Gender, List<String>> retMap = new EnumMap<>(Gender.class);

    protected Callback callback;
    public NicknameBaseQueryWorker(Callback callback) {
        this.callback = callback;
    }

    protected abstract String getUrl();

    @Override
    protected NetworkResponse doInBackground() {
        NetworkResponse ret = NetworkResponse.RESPONSE_ERROR;

        Response response;
        try {
            response = APIUtil.getResponse(getUrl());
            if (response.code() == 200) {
                JSONObject jsonObject = new JSONObject(response.body().string()).optJSONObject("data");
                parseInternal(jsonObject);
                ret = NetworkResponse.RESPONSE_OK;
            }
        } catch (IOException e) {
            ret = NetworkResponse.RESPONSE_NETWORK;
            e.printStackTrace();
        } catch (UrlInvalidException | JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            // Runtime Exception.
            e.printStackTrace();
        }

        Log.d(TAG, "query done: " + retMap);
        return ret;
    }

    protected abstract void parseInternal(JSONObject jsonObject) throws JSONException;

    protected final void parseNicknameInternal(JSONObject jsonObject, Gender gender) throws JSONException {
        JSONArray nicknameJSONArr = jsonObject.optJSONArray(gender.id);
        List<String> nicknameList = new LinkedList<>();
        for (int i = 0; i < nicknameJSONArr.length(); i++) {
            nicknameList.add(nicknameJSONArr.getString(i));
        }
        if (!nicknameList.isEmpty()) {
            retMap.put(gender, nicknameList);
        }
    }

    @Override
    protected void onPreExecute() {
        callback.onLoading();
    }

    @Override
    protected void onPostExecute(NetworkResponse result) {
        callback.onNicknameListQuery(result, retMap);
    }

    public interface Callback extends TaskLoadingCallback {
        void onNicknameListQuery(NetworkResponse ret, EnumMap<Gender, List<String>> map);
    }
}
