package com.ruitech.bookstudy.utils;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;

import com.ruitech.bookstudy.App;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class LocalBroadcastUtil {
    private static LocalBroadcastManager manager;
    private static LocalBroadcastManager getManager() {
        if (manager == null) {
            manager = LocalBroadcastManager.getInstance(App.applicationContext());
        }
        return manager;
    }
    public static void sendBroadcast(String action) {
        getManager().sendBroadcast(new Intent(action));
    }

    public static void sendBroadcast(Intent intent) {
        getManager().sendBroadcast(intent);
    }

    public static void registerReceiver(@NonNull BroadcastReceiver receiver,
                            @NonNull IntentFilter filter) {
        getManager().registerReceiver(receiver, filter);
    }

    public static void unregisterReceiver(@NonNull BroadcastReceiver receiver) {
        getManager().unregisterReceiver(receiver);
    }
}
