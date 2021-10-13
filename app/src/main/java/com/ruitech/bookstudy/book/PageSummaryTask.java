package com.ruitech.bookstudy.book;

import android.os.AsyncTask;
import android.util.Log;

import com.nostra13.universalimageloader.utils.IoUtils;
import com.ruitech.bookstudy.bean.Page;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class PageSummaryTask extends AsyncTask<Object, Object, Boolean> {
    private static final String TAG = "PageSummaryTask";
    private String bookId;
    private String bookGenuineId;
    private Callback callback;

    private List<Page> pageList = new LinkedList<>();
    private List<Page> menuItemList = new LinkedList<>();

    public interface Callback {
        void onPageSummary(List<Page> pageList, List<Page> menuItemList);
    }

    public PageSummaryTask(String bookId, String bookGenuineId, Callback callback) {
        this.bookId = bookId;
        this.bookGenuineId = bookGenuineId;
        this.callback = callback;
    }

    @Override
    protected Boolean doInBackground(Object... objects) {
        boolean ret = false;
        File dirListFile = BookUtil.getDirListFile(bookId, bookGenuineId);
        Log.d(TAG, "dirListFile: " + dirListFile.getAbsolutePath());
        if (dirListFile.exists()) {
            BufferedReader bufferedReader = null;
            try {
                bufferedReader = new BufferedReader(new FileReader(dirListFile));
                String line;
                int ordinal = 0;
                while ((line = bufferedReader.readLine()) != null) {
                    if (line.endsWith("#")) {
                        continue;
                    }
                    String[] arr = line.split("/");
                    android.util.Log.d(TAG, "arr: " + arr.length);
                    try {
                        int pageNum = Integer.parseInt(arr[0]);
                        switch (arr.length) {
                            case 1:
                                pageList.add(new Page(ordinal++, pageNum, bookId, bookGenuineId));
                                break;
                            case 2:
                                Page page = new Page(ordinal++, pageNum, arr[1], bookId, bookGenuineId);
                                pageList.add(page);
                                menuItemList.add(page);
                                break;
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }

                ret = true;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IoUtils.closeSilently(bufferedReader);
            }
        }
        return ret;
    }

    @Override
    protected void onPostExecute(Boolean ret) {
        Log.d(TAG, "onPostExecute: " + ret + " " + pageList.size() + " " + menuItemList.size());
        callback.onPageSummary(pageList, menuItemList);
    }
}
