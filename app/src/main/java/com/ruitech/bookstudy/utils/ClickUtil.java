package com.ruitech.bookstudy.utils;

import android.os.SystemClock;
import android.view.View;

public class ClickUtil {
    private static ClickUtil instance = new ClickUtil();
    private static long DEFAULT_THRESHOLD = 400;//400 millisecond

    private static long lastClickTime;

    public static void resetLastClickTime(){
        lastClickTime = 0;
    }

    public static boolean filter() {
        return instance.doFilter(null, DEFAULT_THRESHOLD);
    }

    public static boolean filter(View resource) {
        return instance.doFilter(resource, DEFAULT_THRESHOLD);
    }

    public static boolean filter(long threshold) {
        return instance.doFilter(null, threshold);
    }

    public static boolean filter(View resource, long thresholdInMs) {
        return instance.doFilter(resource, thresholdInMs);
    }

    private boolean doFilter(View resource, long thresholdInMs) {
        long now = SystemClock.elapsedRealtime();
        long elapse = now - lastClickTime;
        lastClickTime = now;
        return elapse < thresholdInMs;
    }

    private ClickUtil() {

    }

    public static abstract class MXOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (ClickUtil.filter(v))
                return;

            doOnClick(v);
        }

        public abstract void doOnClick(View v);
    }
}
