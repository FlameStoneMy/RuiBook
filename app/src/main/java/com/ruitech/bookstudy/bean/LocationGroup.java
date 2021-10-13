package com.ruitech.bookstudy.bean;

import android.util.Log;

public class LocationGroup {
    private static final String TAG = "LocationGroup";
    public int num;
    public float[] leftArr;
    public float[] topArr;
    public float[] rightArr;
    public float[] bottomArr;

    public String[] translationArr;

    public LocationGroup() {
        // default, num = 0.
    }

    public LocationGroup(int num,
                         float[] leftArr, float[] topArr,
                         float[] rightArr, float[] bottomArr,
                         String[] translationArr) {
        this.num = num;
        this.leftArr = leftArr;
        this.topArr = topArr;
        this.rightArr = rightArr;
        this.bottomArr = bottomArr;
        this.translationArr = translationArr;
        android.util.Log.d(TAG, "translationArr: " + translationArr);
    }

    /**
     * @return -1 means margin.
     */
    public int getZoneNum(float x, float y) {
        int ret = -1;
        for (int i = 0; i < num; i++) {
            if ((leftArr[i] <= x && x <= rightArr[i]) &&
                    (topArr[i] <= y && y <= bottomArr[i])) {
                ret = i;
                break;
            }
        }
//        Log.d(TAG, "getZoneNum: " + x + " " + y + " " + ret);
        return ret;
    }
}
