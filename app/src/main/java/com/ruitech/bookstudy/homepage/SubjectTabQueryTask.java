package com.ruitech.bookstudy.homepage;

import android.os.AsyncTask;
import android.util.Log;

import com.ruitech.bookstudy.homepage.binder.bean.SubjectTab;
import com.ruitech.bookstudy.utils.APIUtil;
import com.ruitech.bookstudy.utils.NetworkResponse;
import com.ruitech.bookstudy.utils.UrlInvalidException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Response;

public class SubjectTabQueryTask extends AsyncTask<Object, Object, NetworkResponse> {
    private static final String TAG = "SubjectTabQueryTask";
    private SubjectTab oriSubjectTab, newSubjectTab;
    private Callback callback;
    public SubjectTabQueryTask(SubjectTab subjectTab, Callback callback) {
        this.oriSubjectTab = subjectTab;
        this.callback = callback;
    }

    @Override
    protected NetworkResponse doInBackground(Object[] objects) {
        NetworkResponse ret = NetworkResponse.RESPONSE_ERROR;

        Response response;
        try {
            response = APIUtil.getResponse(oriSubjectTab.getRefreshUrl());
            if (response.code() == 200) {
                JSONObject jsonObject = new JSONObject(response.body().string()).optJSONObject("data");
                System.out.println("meng here: " + jsonObject.toString(2));
                newSubjectTab = CardType.TAB_SUBJECT.parse(jsonObject);

                ret = NetworkResponse.RESPONSE_OK;
            }
        } catch (IOException e) {
            ret = NetworkResponse.RESPONSE_NETWORK;
            e.printStackTrace();
        } catch (UrlInvalidException | JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "query done: ");
        return ret;
    }

    @Override
    protected void onPostExecute(NetworkResponse result) {
        callback.onSubjectTabQueryResult(result, newSubjectTab);
    }

    public interface Callback {
        void onSubjectTabQueryResult(NetworkResponse ret, SubjectTab subjectTab);
    }
}
