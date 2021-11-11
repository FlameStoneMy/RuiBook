package com.ruitech.bookstudy.guide.task;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.ruitech.bookstudy.RuiWorker;
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

public class GradeListQueryWorker extends RuiWorker<NetworkResponse> {
    private static final String TAG = "GradeListQueryTask";
    private List retList = new ArrayList();
    private Callback callback;

//    private Handler mainHandler = new Handler(Looper.getMainLooper());
    public GradeListQueryWorker(Callback callback) {
        this.callback = callback;
//        onPreExecute();
    }

//    public final void execute() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                NetworkResponse t = doInBackground();
//                mainHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        onPostExecute(t);
//                        dump();
//                    }
//                });
//            }
//        }).start();
//    }

//    public void execute() {
//        onPreExecute();
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                NetworkResponse t = doInBackground();
//                mainHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        onPostExecute(t);
//                        dump();
//                    }
//                });
//            }
//        });
//        thread.start();
//    }

//    @Override
    protected NetworkResponse doInBackground() {
        NetworkResponse ret = NetworkResponse.RESPONSE_ERROR;

        Response response;
        try {
            response = APIUtil.getResponse(Const.GRADE_LIST_QUERY_URL);

            Log.d(TAG, "meng here doInBackground2");
            if (response.code() == 200) {
                JSONArray jsonArray = new JSONObject(response.body().string()).optJSONArray("data");

                Log.d(TAG, "meng here doInBackground3 " + jsonArray.toString(2));
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

        return ret;
    }

//    private void dump() {
//        mainHandler.removeCallbacksAndMessages(null);
//        mainHandler = null;
//    }

//    @Override
//    protected NetworkResponse doInBackground() {
//        Log.d(TAG, "meng here doInBackground");
//        NetworkResponse ret = NetworkResponse.RESPONSE_ERROR;
//
//        Response response;
//        try {
//            response = APIUtil.getResponse(Const.GRADE_LIST_QUERY_URL);
//
//            Log.d(TAG, "meng here doInBackground2");
//            if (response.code() == 200) {
//                JSONArray jsonArray = new JSONObject(response.body().string()).optJSONArray("data");
//                for (int i = 0; i < jsonArray.length(); i++) {
//
//                    Log.d(TAG, "meng here doInBackground3 " + i + " " + jsonArray.length());
//                    JSONObject jsonObject = jsonArray.optJSONObject(i);
//                    JSONArray gradeJSONArr = jsonObject.optJSONArray("resources");
//                    List<Grade> gradeList = new LinkedList<>();
//                    for (int j = 0; j < gradeJSONArr.length(); j++) {
//                        JSONObject gradeJSONObj = gradeJSONArr.optJSONObject(j);
//                        if (gradeJSONObj != null) {
//                            Grade grade = Grade.from(gradeJSONObj.optString("id"));
//                            if (grade != null) {
//                                gradeList.add(grade);
//                            }
//                        }
//                    }
//                    if (!gradeList.isEmpty()) {
//                        GradeCategory gradeCategory = GradeCategory.from(jsonObject.optString("id"));
//                        if (gradeCategory != null) {
//                            retList.add(gradeCategory);
//                            retList.addAll(gradeList);
//                        }
//                    }
//                }
//                ret = NetworkResponse.RESPONSE_OK;
//            }
//        } catch (IOException e) {
//            ret = NetworkResponse.RESPONSE_NETWORK;
//            e.printStackTrace();
//        } catch (UrlInvalidException | JSONException e) {
//            e.printStackTrace();
//        }
//
//        Log.d(TAG, "query done: " + retList);
//        return ret;
//    }

//    @Override
//    protected void onPreExecute() {
//        callback.onLoading();
//    }

//    @Override
    protected void onPreExecute() {
        callback.onLoading();
    }

//    @Override
    protected void onPostExecute(NetworkResponse result) {
        callback.onGradeListQuery(result, retList);
    }

    public interface Callback extends TaskLoadingCallback {
        void onGradeListQuery(NetworkResponse ret, List list);
    }
}
