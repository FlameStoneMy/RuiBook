package com.ruitech.bookstudy.upgrade;

import android.os.AsyncTask;
import android.util.Log;

import com.ruitech.bookstudy.App;
import com.ruitech.bookstudy.desktop.DesktopConst;
import com.ruitech.bookstudy.desktop.bean.Album;
import com.ruitech.bookstudy.utils.APIUtil;
import com.ruitech.bookstudy.utils.Const;
import com.ruitech.bookstudy.utils.NetworkResponse;
import com.ruitech.bookstudy.utils.UrlInvalidException;
import com.ruitech.bookstudy.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Response;

public class UpgradeQueryTask extends AsyncTask<Object, Object, UpgradeBean> {
    private static final String TAG = "UpgradeQueryTask";
    private Callback callback;
    public UpgradeQueryTask(Callback callback) {
        this.callback = callback;
    }

    @Override
    protected UpgradeBean doInBackground(Object... objects) {
        Log.d(TAG, "doInBackground: ");

        UpgradeBean upgradeBean = null;
        Response response;
        try {
            response = APIUtil.getResponse(Const.getUpgradeUrl(Util.getPackageVersion()));
            android.util.Log.d(TAG, "response: " + response.code());
            if (response.code() == 200) {
                JSONObject jsonObject = new JSONObject(response.body().string()).optJSONObject("data");
                if (jsonObject != null) {
                    int versionCode = jsonObject.optInt("version");
                    String versionName = jsonObject.optString("versionName");
                    String url = jsonObject.optString("url");
                    int size = jsonObject.optInt("size");
                    upgradeBean = new UpgradeBean(versionCode, versionName, url, size);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UrlInvalidException | JSONException e) {
            e.printStackTrace();
        }
        return upgradeBean;
    }

    @Override
    protected void onPostExecute(UpgradeBean upgradeBean) {
        Log.d(TAG, "onPostExecute: " + upgradeBean);
        if (upgradeBean != null) {
            callback.onNewVersion(upgradeBean);
        }
    }

    public interface Callback {
        void onNewVersion(UpgradeBean upgradeBean);
    }
}
