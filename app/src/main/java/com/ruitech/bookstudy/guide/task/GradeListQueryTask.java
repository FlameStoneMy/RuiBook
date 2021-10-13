package com.ruitech.bookstudy.guide.task;

import android.os.AsyncTask;
import android.util.Log;

import com.ruitech.bookstudy.bean.Grade;
import com.ruitech.bookstudy.bean.GradeCategory;
import com.ruitech.bookstudy.utils.APIUtil;
import com.ruitech.bookstudy.utils.Const;
import com.ruitech.bookstudy.utils.NetworkResponse;
import com.ruitech.bookstudy.utils.UrlInvalidException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import okhttp3.Response;

public class GradeListQueryTask extends AsyncTask<Object, Object, NetworkResponse> {
    private static final String TAG = "GradeListQueryTask";
    private List retList = new ArrayList();
    private Callback callback;
    public GradeListQueryTask(Callback callback) {
        this.callback = callback;
    }

    @Override
    protected NetworkResponse doInBackground(Object[] objects) {
        NetworkResponse ret = NetworkResponse.RESPONSE_ERROR;

        Response response;
        try {
            response = APIUtil.getResponse(Const.GRADE_LIST_QUERY_URL);
            if (response.code() == 200) {
                JSONArray jsonArray = new JSONObject(response.body().string()).optJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                    JSONArray gradeJSONArr = jsonObject.optJSONArray("resources");
                    List<Grade> gradeList = new LinkedList<>();
                    for (int j = 0; j < gradeJSONArr.length(); j++) {
                        JSONObject gradeJSONObj = gradeJSONArr.optJSONObject(j);
                        if (gradeJSONObj != null) {
                            Grade grade = Grade.from(gradeJSONObj.optString("id"));
                            if (grade != null) {
                                gradeList.add(grade);
                            }
                        }
                    }
                    if (!gradeList.isEmpty()) {
                        GradeCategory gradeCategory = GradeCategory.from(jsonObject.optString("id"));
                        if (gradeCategory != null) {
                            retList.add(gradeCategory);
                            retList.addAll(gradeList);
                        }
                    }
                }
                ret = NetworkResponse.RESPONSE_OK;
            }
        } catch (IOException e) {
            ret = NetworkResponse.RESPONSE_NETWORK;
            e.printStackTrace();
        } catch (UrlInvalidException | JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "query done: " + retList);
        return ret;
    }

    @Override
    protected void onPreExecute() {
        callback.onLoading();
    }

    @Override
    protected void onPostExecute(NetworkResponse result) {
        callback.onGradeListQuery(result, retList);
    }

    public interface Callback extends TaskLoadingCallback {
        void onGradeListQuery(NetworkResponse ret, List list);
    }
}
