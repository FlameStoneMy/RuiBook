package com.ruitech.bookstudy.desktop.task;

import android.os.AsyncTask;
import android.util.Log;

import com.ruitech.bookstudy.bean.Grade;
import com.ruitech.bookstudy.desktop.DesktopConst;
import com.ruitech.bookstudy.desktop.bean.Category;
import com.ruitech.bookstudy.utils.APIUtil;
import com.ruitech.bookstudy.utils.NetworkResponse;
import com.ruitech.bookstudy.utils.UrlInvalidException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

public class CategoryListLoadTask extends AsyncTask<Object, Object, NetworkResponse> {
    private static final String TAG = "CategoryListLoadTask";
    private Grade grade;
    private List<Category> list = new ArrayList<>();
    private Callback callback;
    public CategoryListLoadTask(Grade grade, Callback callback) {
        this.grade = grade;
        this.callback = callback;
    }

    @Override
    protected NetworkResponse doInBackground(Object... objects) {
        Log.d(TAG, "doInBackground: ");

        NetworkResponse ret = NetworkResponse.RESPONSE_ERROR;
        Response response;
        try {
            response = APIUtil.getResponse(DesktopConst.getCategoryPageUrl(grade));
            if (response.code() == 200) {
                JSONArray jsonArray = new JSONObject(response.body().string()).optJSONArray("data");
//                System.out.println("rui here: " + jsonArray.toString(2));
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Category c = new Category();
                    c.id = jsonObject.getString("id");
                    c.name = jsonObject.getString("categoryName");
                    android.util.Log.d(TAG, "c: " + c.id + " " + c.name);
                    list.add(c);
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

    @Override
    protected void onPostExecute(NetworkResponse result) {
        Log.d(TAG, "onPostExecute: " + result + " " + list.size());
        callback.onCategoryListLoad(result, list);
    }

    public interface Callback {
        void onCategoryListLoad(NetworkResponse response, List<Category> list);
    }
}
