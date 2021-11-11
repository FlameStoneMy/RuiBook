package com.ruitech.bookstudy.utils;

import android.net.Uri;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class APIUtil {

    private static final String TAG = "APIUtil";

    public static Response getResponse(String url) throws IOException, UrlInvalidException {
        return getResponse(url, null);
    }

    public static Response getResponse(String url, Map<String, String> headerMap) throws IOException, UrlInvalidException {
        HttpUrl httpUrl = getHttpUrl(url);

        Request.Builder builder = new Request.Builder();
        builder.url(httpUrl);
        if (headerMap != null) {
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                builder.header(entry.getKey(), entry.getValue());
            }
        }

        Request request = builder.build();
        return execute(NetworkUtil.getClient(), request);
    }

    public static Response postResponse(String url, JSONObject jsonObject) throws IOException, UrlInvalidException {
        HttpUrl httpUrl = getHttpUrl(url);

        Request.Builder builder = new Request.Builder();
        builder.url(httpUrl);
        builder.post(RequestBody.create(JSON, jsonObject.toString()));

        Request request = builder.build();
        return execute(NetworkUtil.getClient(), request);
    }

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static Response execute(OkHttpClient client, Request request) throws IOException {
        android.util.Log.d(TAG, "execute");
        Call call = client.newCall(request);

        android.util.Log.d(TAG, "execute2");
        Response res = call.execute();
        android.util.Log.d(TAG, "execute3");
        return res;
    }

    // We check url valid at the very beginning in all public methods and
    // convert any Unchecked Exception to Checked.
    private static final HttpUrl getHttpUrl(String url) throws UrlInvalidException {
        if (url == null) throw new UrlInvalidException("url == null");

        // Silently replace web socket URLs with HTTP URLs.
        if (url.regionMatches(true, 0, "ws:", 0, 3)) {
            url = "http:" + url.substring(3);
        } else if (url.regionMatches(true, 0, "wss:", 0, 4)) {
            url = "https:" + url.substring(4);
        }
        try {
            return HttpUrl.get(URI.create(url));
        } catch (IllegalArgumentException e) {
            throw new UrlInvalidException(e.getMessage());
        }
    }
}
