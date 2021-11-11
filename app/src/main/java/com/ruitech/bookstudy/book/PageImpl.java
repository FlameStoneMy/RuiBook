package com.ruitech.bookstudy.book;

import android.os.AsyncTask;
import android.util.Log;

import com.nostra13.universalimageloader.utils.IoUtils;
import com.ruitech.bookstudy.bean.LocationGroup;
import com.ruitech.bookstudy.utils.Executors;
import com.ruitech.bookstudy.utils.ListUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class PageImpl {
    private static final String TAG = "PageImpl";

//    public PageImpl() {
//    }

    private AsyncTask pageTask;
    public void loadData(String bookId, String bookGenuineId, int pageNum, Callback callback) {
        Log.d(TAG, "loadData: " + pageNum + " " + pageTask);
        if (pageTask != null) {
            pageTask.cancel(true);
        }
        pageTask = new PageTask(bookId, bookGenuineId, pageNum, callback).executeOnExecutor(Executors.io());
    }

    public interface Callback {
        void onPageDetailsRet(int pageNum, LocationGroup locationGroup);
    }

    private class PageTask extends AsyncTask<Object, Object, Boolean> {
        private static final String TAG = "PageTask";
        private String bookId;
        private String bookGenuineId;
        private int pageNum;
        private Callback callback;
        private LocationGroup locationGroup;

        public PageTask(String bookId, String bookGenuineId, int pageNum, Callback callback) {
            this.bookId = bookId;
            this.bookGenuineId = bookGenuineId;
            this.pageNum = pageNum;
            this.callback = callback;
        }

        private boolean parseCoordinate() {
            boolean ret = false;
            // parse XY.txt
            File coordinateFile = BookUtil.getCoordinateFile(bookId, bookGenuineId, pageNum);
            // parse Char.txt
            File translationFile = BookUtil.getTranslationFile(bookId, bookGenuineId, pageNum);
            Log.d(TAG, "parseCoordinate1 " + coordinateFile.getAbsolutePath() + " " + this);
            if (coordinateFile.exists()) {
                Log.d(TAG, "parseCoordinate2 " + this);
                if (translationFile.exists()) {
                    Log.d(TAG, "parseCoordinate3 " + this);
                    int num = 0;
                    ArrayList<Float> leftList = new ArrayList<>();
                    ArrayList<Float> topList = new ArrayList<>();
                    ArrayList<Float> rightList = new ArrayList<>();
                    ArrayList<Float> bottomList = new ArrayList<>();
                    ArrayList<String> translationList = new ArrayList<>();

                    BufferedReader bufferedReader = null;
                    BufferedReader bufferedReader1 = null;
                    try {
                        bufferedReader = new BufferedReader(new FileReader(coordinateFile));
                        bufferedReader1 = new BufferedReader(new FileReader(translationFile));
                        String line;
                        String line1;
                        while ((line = bufferedReader.readLine()) != null && (line1 = bufferedReader1.readLine()) != null) {
                            if (line.endsWith("#") && line1.endsWith("#")) {
                                continue;
                            }
                            String[] arr = line.split(",");
                            if (arr.length != 4) {
                                throw new IOException("coordinate arr length invalid: " + arr.length);
                            }

                            leftList.add(Float.parseFloat(arr[0]));
                            topList.add(Float.parseFloat(arr[1]));
                            rightList.add(Float.parseFloat(arr[2]));
                            bottomList.add(Float.parseFloat(arr[3]));

                            if (line1.equals("-")) {
                                translationList.add(null);
                            } else {
                                translationList.add(line1);
                            }

                            num++;

                            if (isCancelled()) {
                                Log.d(TAG, "parseCoordinate: cancelled");
                                return ret;
                            }
                        }
                    } catch (IOException | NumberFormatException e) {
                        e.printStackTrace();
                    } finally {
                        IoUtils.closeSilently(bufferedReader);
                        IoUtils.closeSilently(bufferedReader1);
                    }

                    if (num > 0) {
                        locationGroup = new LocationGroup(num,
                                ListUtils.convertAsPrimitiveArray(leftList),
                                ListUtils.convertAsPrimitiveArray(topList),
                                ListUtils.convertAsPrimitiveArray(rightList),
                                ListUtils.convertAsPrimitiveArray(bottomList),
                                translationList.toArray(new String[translationList.size()]));
                    } else {
                        locationGroup = new LocationGroup();
                    }
                } else {
                    locationGroup = new LocationGroup();
                }
                ret = true;
            }

            return ret;
        }

        @Override
        protected Boolean doInBackground(Object... objects) {
            return parseCoordinate();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            Log.d(TAG, "onPostExecute: " + result);
            if (result) {
                callback.onPageDetailsRet(pageNum, locationGroup);
            }
            if (pageTask == this) {
                pageTask = null;
            }
        }
    }
}