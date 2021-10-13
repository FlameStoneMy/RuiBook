package com.ruitech.bookstudy.network;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import com.ruitech.bookstudy.utils.DeviceUtil;

public class NetWorkGuide {

    public static final int REQUEST_CODE_NETWORK_SETTINGS = 100;

    public static final int RETURN_TYPE_VIDEO = 201;
    public static final int RETURN_TYPE_HOME = 202;

    public static int returnType = RETURN_TYPE_HOME;


    public static void launchNetWorkSetting(Context context) {
        launchNetWorkSetting(context, false);
    }

    public static void launchNetWorkSetting(Context context, int type) {
        returnType = type;
        launchNetWorkSetting(context, false);
    }

    public static void resetReturnType() {
        returnType = RETURN_TYPE_HOME;
    }

    public static void launchNetWorkSetting(Context context, boolean newTask) {
        if (context == null) {
            return;
        }
        if (context instanceof Activity && ((Activity) context).isFinishing()) {
            return;
        }
        try {
            if (DeviceUtil.hasSimCard(context) && DeviceUtil.hasTelephoneFeature(context)) {
                launchMobileSetting(context, newTask);
            } else {
                launchWifiSetting(context, newTask);
            }
        } catch (Exception e) {
            launchSettingPage(context, newTask);
        }
    }

    private static void launchMobileSetting(Context context, boolean newTask) {
        Intent intent = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
        if (isMeiZu()) {
            intent.setPackage("com.meizu.connectivitysettings");
        } else if (isSamSungN9500()) {
            intent = new Intent(Intent.ACTION_MAIN);
            intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings$DataUsageSummaryActivity"));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        } else {
            intent.setPackage("com.android.phone");
        }
        doLaunchNewTaskIntent(context, intent, newTask);
    }

    private static boolean isSamSungN9500() {
        return TextUtils.equals("SM-N9500", Build.MODEL);
    }

    private static boolean isMeiZu() {
        String product = Build.PRODUCT;
        return !TextUtils.isEmpty(product) && (product.contains("meizu") || product.contains("Meizu"));
    }

    private static void launchWifiSetting(Context context, boolean newTask) {
        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        doLaunchNewTaskIntent(context, intent, newTask);
    }

    private static void doLaunchNewTaskIntent(Context context, Intent intent, boolean newTask) {
        if (newTask) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        if (context instanceof Activity) {
            ((Activity) context).startActivityForResult(intent, REQUEST_CODE_NETWORK_SETTINGS);
        } else {
            context.startActivity(intent);
        }
    }

    private static void launchSettingPage(Context context, boolean newTask) {
        try {
            Intent intent = new Intent(Settings.ACTION_SETTINGS);
            doLaunchNewTaskIntent(context, intent, newTask);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isNetWorkRequestCode(int requestCode) {
        return requestCode == REQUEST_CODE_NETWORK_SETTINGS;
    }

    public static void launchPermissionSetting(Context context) {
        if (context == null) {
            return;
        }
        if (context instanceof Activity && ((Activity) context).isFinishing()) {
            return;
        }
        try {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.fromParts("package", context.getPackageName(), null));
            context.startActivity(intent);
        } catch (Exception e) {
            launchSettingPage(context, false);
        }
    }
}
