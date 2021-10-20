package com.ruitech.bookstudy.desktop.task;

import android.os.AsyncTask;
import android.util.Log;

import com.ruitech.bookstudy.desktop.DesktopConst;
import com.ruitech.bookstudy.desktop.bean.Album;
import com.ruitech.bookstudy.desktop.bean.Category;
import com.ruitech.bookstudy.utils.APIUtil;
import com.ruitech.bookstudy.utils.NetworkResponse;
import com.ruitech.bookstudy.utils.UrlInvalidException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Response;

public class CategoryLoadTask extends AsyncTask<Object, Object, NetworkResponse> {
    private static final String TAG = "CategoryLoadTask";
    private Category category;
    private Callback callback;
    public CategoryLoadTask(Category category, Callback callback) {
        this.category = category;
        this.callback = callback;
    }

    @Override
    protected NetworkResponse doInBackground(Object... objects) {
        Log.d(TAG, "doInBackground: ");

        NetworkResponse ret = NetworkResponse.RESPONSE_ERROR;
        Response response;
        try {
            JSONObject sendJSONObj = new JSONObject();
            sendJSONObj.put("currentPage", 0);
            sendJSONObj.put("numPerPage", 100);
            sendJSONObj.put("categoryId", Integer.valueOf(category.id));
//            sendJSONObj.put("moduleId", 0);
            response = APIUtil.postResponse(DesktopConst.MODULE_PAGE_URL, sendJSONObj);
            if (response.code() == 200) {
                JSONObject j = new JSONObject(response.body().string());
                System.out.println("meng here: " + j.toString(2));
                JSONObject jsonObject = j.optJSONObject("data");
                category = Category.initFromJson(jsonObject);
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
        Log.d(TAG, "onPostExecute: " + result + " " + category.albumList.size());
        callback.onCategoryLoad(result, category);
    }

    public interface Callback {
        void onCategoryLoad(NetworkResponse response, Category category);
    }
}
