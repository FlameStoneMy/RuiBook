package com.ruitech.bookstudy.book;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.ruitech.bookstudy.App;
import com.ruitech.bookstudy.BookDao;
import com.ruitech.bookstudy.R;
import com.ruitech.bookstudy.bean.Book;
import com.ruitech.bookstudy.utils.APIUtil;
import com.ruitech.bookstudy.utils.UrlInvalidException;
import com.ruitech.bookstudy.utils.ZipUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;

import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;
import okio.Sink;

public class BookQueryTask extends AsyncTask<Object, Object, Boolean> {
    private static final String TAG = "BookQueryTask";
    private Book book;
    public enum FromType {
        CLICK_READ_CARD,
        CURRENT_BOOK_CARD
    }
    private FromType fromType;
    public BookQueryTask(Book book, FromType fromType) {
        this.book = book;
        this.fromType = fromType;
    }

    @Override
    protected synchronized Boolean doInBackground(Object[] objects) {
        boolean ret = false;
        while (true) {
            if (BookDao.isDownloaded(book)) {
                ret = true;
                return ret;
            }

            BookDao.startDownload(book);
            EventBus.getDefault().post(new BookQueryEvent(book, BookQueryEvent.State.START, fromType));

            File booksDir = BookUtil.getBookIdDir(book.getId());
            if (!booksDir.exists()) {
                if (!booksDir.mkdirs()) {
                    break;
                }
            }


            Response response = null;
            try {
                response = APIUtil.getResponse(book.getUrl());
                Log.d(TAG, "code: " + response.code());
                if (response.code() != 200) {
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (UrlInvalidException e) {
                e.printStackTrace();
            }

            String bookFileName = book.getGenuineId();
            if (TextUtils.isEmpty(bookFileName)) {
                break;
            }

            BufferedSink bufferedSink = null;
            File zipFile = BookUtil.getBookZipFile(book.getId(), book.getGenuineId());

            android.util.Log.d(TAG, "zipFile: " + zipFile.getAbsolutePath() + " ");
            try {
                Sink sink = Okio.sink(zipFile);
                bufferedSink = Okio.buffer(sink);
                bufferedSink.writeAll(response.body().source());

                bufferedSink.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (bufferedSink != null) {
                    try {
                        bufferedSink.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            Log.d(TAG, "zipFile: " + zipFile.length() + " " + zipFile.getAbsolutePath());
            try {
                ZipUtil.unzip(zipFile, booksDir, false);
                zipFile.delete();

                ret = true;
            } catch (IOException e) {
                e.printStackTrace();
            }

            break;
        }

        BookDao.updateDownloadRet(book, ret);
        Log.d(TAG, "query done: " + book +  " " + ret);
        return ret;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onPostExecute(Boolean result) {
        EventBus.getDefault().post(new BookQueryEvent(book,
                result ? BookQueryEvent.State.SUCC : BookQueryEvent.State.FAIL,
                fromType));
    }
}
