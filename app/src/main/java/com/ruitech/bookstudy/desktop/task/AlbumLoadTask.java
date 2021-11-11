package com.ruitech.bookstudy.desktop.task;

import android.os.AsyncTask;
import android.util.Log;

import com.ruitech.bookstudy.bean.Grade;
import com.ruitech.bookstudy.desktop.DesktopConst;
import com.ruitech.bookstudy.desktop.bean.Album;
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

public class AlbumLoadTask extends AsyncTask<Object, Object, NetworkResponse> {
    private static final String TAG = "AlbumLoadTask";
    private Album album;
    private Callback callback;
    public AlbumLoadTask(Album album, Callback callback) {
        this.album = album;
        this.callback = callback;
    }

    @Override
    protected NetworkResponse doInBackground(Object... objects) {
        Log.d(TAG, "doInBackground: ");

        NetworkResponse ret = NetworkResponse.RESPONSE_ERROR;
        Response response;
        try {
            response = APIUtil.getResponse(DesktopConst.getAlbumPageUrl(album), DesktopConst.getHeaderMap());
            if (response.code() == 200) {
                JSONObject jsonObject = new JSONObject(response.body().string()).optJSONObject("data");
                System.out.println("meng here: " + jsonObject.toString(2));
                Album.initFromJson(album, jsonObject);
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
        Log.d(TAG, "doInBackground: " + result + " " + album);
        callback.onAlbumLoad(result, album);
    }

    public interface Callback {
        void onAlbumLoad(NetworkResponse response, Album album);
    }
}
