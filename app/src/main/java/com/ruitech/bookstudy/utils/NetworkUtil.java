package com.ruitech.bookstudy.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.ruitech.bookstudy.App;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;

public class NetworkUtil {

    public static OkHttpClient getClient() {
        ExecutorService threadPoolExecutor = Executors.network();
        Dispatcher dispatcher = new Dispatcher(threadPoolExecutor);
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(15 * 1000, TimeUnit.MILLISECONDS)
                .readTimeout(30 * 1000, TimeUnit.MILLISECONDS).dispatcher(dispatcher)
                .followRedirects(true).build();
        return client;
    }

    public static boolean isNetWorkConnected() {
        try {
            ConnectivityManager cm = (ConnectivityManager) App.applicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnectedOrConnecting();
        } catch (Exception ignored) {
        }
        return false;
    }
}
