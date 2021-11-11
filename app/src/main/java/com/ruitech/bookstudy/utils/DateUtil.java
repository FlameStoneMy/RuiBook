package com.ruitech.bookstudy.utils;

import android.text.TextUtils;
import android.util.Log;

import java.util.Arrays;
import java.util.Calendar;
import java.util.TimeZone;

import androidx.core.util.Pair;

public class DateUtil {

    private static final String TAG = "DateUtil";

    public static Pair<Integer, int[]> getCurrWeekDate() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
//        int currDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        calendar.add(Calendar.DATE, Calendar.SUNDAY - dayOfWeek);
        int firstDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.add(Calendar.DATE, 6);
        int lastDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int[] ret = new int[7];
        int index = ret.length - 1;
        while (lastDayOfMonth >= 1 && index >= 0) {
            ret[index--] = lastDayOfMonth--;
        }
        while (index >= 0) {
            ret[index] = firstDayOfMonth + index--;
        }
//        Log.d(TAG, "getCurrWeekDate: " + Arrays.toString(ret));
        return new Pair<>(dayOfWeek - 1, ret);
    }

    public static String parseDuration(int duration) {
        duration /= 1000;
        int hour = duration / 3600;
        String secondStr = null;
        String hourStr = null;
        if (hour > 0) {
            hourStr = String.valueOf(hour);
        }

        int minute = (duration % 3600) / 60;
        String minuteStr = null;
        if (minute > 0) {
            minuteStr = String.valueOf(minute);
        }

        int second = (duration % 3600) % 60;
        secondStr = String.valueOf(second);

        StringBuilder builder = new StringBuilder();
        if (!TextUtils.isEmpty(hourStr)) {
            if (hourStr.length() == 1) {
                builder.append("0");
                builder.append(hourStr);
            } else {
                builder.append(hourStr);
            }
            builder.append(":");
        }

        if (TextUtils.isEmpty(minuteStr)) {
            builder.append("00:");
        } else {
            if (minuteStr.length() == 1) {
                builder.append("0");
                builder.append(minuteStr);
            } else {
                builder.append(minuteStr);
            }
            builder.append(":");
        }

        if (TextUtils.isEmpty(secondStr)) {
            builder.append("00");
        } else {
            if (secondStr.length() == 1) {
                builder.append("0");
            }

            builder.append(secondStr);
        }

        return builder.toString();
    }
}
