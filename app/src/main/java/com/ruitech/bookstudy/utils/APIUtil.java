package com.ruitech.bookstudy.utils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class APIUtil {

    public static Response getResponse(String url) throws IOException, UrlInvalidException {
        HttpUrl httpUrl = getHttpUrl(url);
        return get(NetworkUtil.getClient(), httpUrl);
    }

    private static Response get(OkHttpClient client, HttpUrl httpUrl) throws IOException {
        Request.Builder builder = new Request.Builder();
        builder.url(httpUrl);

        Request request = builder.build();
        return execute(client, request);
    }

    private static Response execute(OkHttpClient client, Request request) throws IOException {
        Call call = client.newCall(request);

        Response res = call.execute();

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
            return HttpUrl.get(url);
        } catch (IllegalArgumentException e) {
            throw new UrlInvalidException(e.getMessage());
        }
    }
}
