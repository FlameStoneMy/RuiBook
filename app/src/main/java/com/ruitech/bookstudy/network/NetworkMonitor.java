package com.ruitech.bookstudy.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Pair;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;

public final class NetworkMonitor {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            NETWORK_TYPE.TYPE_NONE,
            NETWORK_TYPE.TYPE_WIFI,
            NETWORK_TYPE.TYPE_OTHER,
            NETWORK_TYPE.TYPE_2G,
            NETWORK_TYPE.TYPE_3G,
            NETWORK_TYPE.TYPE_4G
    })
    public @interface NETWORK_TYPE {

        int TYPE_NONE = -1;
        int TYPE_WIFI = 0;
        int TYPE_OTHER = 10;
        int TYPE_2G = 20;
        int TYPE_3G = 30;
        int TYPE_4G = 40;
    }

    public static interface OnNetworkListener {
        void onNetworkChanged(Pair<Integer, Boolean> last, Pair<Integer, Boolean> current);
    }

    private Context context;
    private BroadcastReceiver broadcastReceiver;

    private Pair<Integer, Boolean> lastNetworkType;
    private Pair<Integer, Boolean> lastNetworkTypeNotify;
    private OnNetworkListener listener;

    public NetworkMonitor(Context context, OnNetworkListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void start() {
        start(false);
    }

    public void start(boolean sticky) {
        if (broadcastReceiver != null)
            return;

        lastNetworkType = getNetworkType(context);
        if (!sticky) {
            lastNetworkTypeNotify = lastNetworkType;
        }

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                Pair<Integer, Boolean> current = getNetworkType(context);
                if (!current .equals(lastNetworkTypeNotify)) {
                    Pair<Integer, Boolean> last = lastNetworkType;
                    lastNetworkType = current;
                    lastNetworkTypeNotify = lastNetworkType;
                    if (listener != null) {
                        listener.onNetworkChanged(last, current);
                    }
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);

        try {
            context.getApplicationContext().registerReceiver(broadcastReceiver,intentFilter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Pair<Integer, Boolean> getNetworkType(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo == null || !netInfo.isConnectedOrConnecting())
            return new Pair<>(NETWORK_TYPE.TYPE_NONE, false);
        else if (netInfo.getType() == ConnectivityManager.TYPE_WIFI)
            return new Pair<>(NETWORK_TYPE.TYPE_WIFI, false);
        else if (netInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            int subtype = netInfo.getSubtype();
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            if (subtype == TelephonyManager.NETWORK_TYPE_LTE
                    || subtype > TelephonyManager.NETWORK_TYPE_IWLAN) {//4G
                return new Pair<>(NETWORK_TYPE.TYPE_4G, telephonyManager.isNetworkRoaming());
            } else if (subtype == TelephonyManager.NETWORK_TYPE_UMTS
                    || subtype == TelephonyManager.NETWORK_TYPE_EVDO_0
                    || subtype == TelephonyManager.NETWORK_TYPE_EVDO_A
                    || subtype == TelephonyManager.NETWORK_TYPE_1xRTT
                    || subtype == TelephonyManager.NETWORK_TYPE_HSDPA
                    || subtype == TelephonyManager.NETWORK_TYPE_HSUPA
                    || subtype == TelephonyManager.NETWORK_TYPE_HSPA
                    || subtype == TelephonyManager.NETWORK_TYPE_EVDO_B
                    || subtype == TelephonyManager.NETWORK_TYPE_HSPAP
                    || subtype == TelephonyManager.NETWORK_TYPE_TD_SCDMA
                    ) {//3G
                return new Pair<>(NETWORK_TYPE.TYPE_3G, telephonyManager.isNetworkRoaming());
            }
        }

        return new Pair<>(NETWORK_TYPE.TYPE_OTHER, false);
    }

    public void stop() {
        if (broadcastReceiver == null)
            return;

        try {
            context.getApplicationContext().unregisterReceiver(broadcastReceiver);
        } catch (Exception ignored) {}
        broadcastReceiver = null;
    }

    public void release() {
        listener = null;
        stop();
    }

    public static boolean isConnected(Context context) {
        try {
            ConnectivityManager cm =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo == null || !netInfo.isConnectedOrConnecting())
                return false;
            else
                return true;
        } catch (Exception ignored) {}
        return true;
    }

    public static boolean canRecommendVideos(Pair<Integer, Boolean> network) {
        return NetworkMonitor.NETWORK_TYPE.TYPE_WIFI == network.first
                || (NetworkMonitor.NETWORK_TYPE.TYPE_4G == network.first && !network.second);
    }
}
