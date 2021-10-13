package com.ruitech.bookstudy.utils;

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
}
