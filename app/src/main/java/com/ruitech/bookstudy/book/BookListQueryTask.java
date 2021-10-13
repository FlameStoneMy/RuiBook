package com.ruitech.bookstudy.book;

import android.os.AsyncTask;
import android.util.Log;

import com.nostra13.universalimageloader.utils.IoUtils;
import com.ruitech.bookstudy.bean.Book;
import com.ruitech.bookstudy.bean.Grade;
import com.ruitech.bookstudy.bean.Subject;
import com.ruitech.bookstudy.utils.APIUtil;
import com.ruitech.bookstudy.utils.Const;
import com.ruitech.bookstudy.utils.UrlInvalidException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

public class BookListQueryTask extends AsyncTask<Object, Object, Boolean> {
    private static final String TAG = "BookListQueryTask";
    private Subject subject;
    private Grade grade;
    private Callback callback;
    private List<Book> list;
    public BookListQueryTask(Subject subject, Grade grade, Callback callback) {
        this.subject = subject;
        this.grade = grade;
        this.callback = callback;
    }

    @Override
    protected Boolean doInBackground(Object[] objects) {
        list = new ArrayList<>();
        boolean ret = false;
        while (true) {
            Response response = null;
            try {
                response = APIUtil.getResponse(Const.getBookQueryUrl(subject, grade));
                Log.d(TAG, "resp code: " + Const.getBookQueryUrl(subject, grade) + " " + response.code());
                if (response.code() != 200) {
                    break;
                }

                JSONObject jsonObject = new JSONObject(response.body().string());
                System.out.println("resp: " + jsonObject.toString(2));
                JSONArray jsonArray = jsonObject.optJSONArray("data");
                if (jsonArray != null) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Book book = Book.from(jsonArray.getJSONObject(i));
                        if (book != null) {
                            list.add(book);
                            Log.d(TAG, "query done: " + book);
                        }
                    }
                }

                ret = true;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (UrlInvalidException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                IoUtils.closeSilently(response);
            }
            break;
        }

        Log.d(TAG, "query done2: " + list.size() +  " " + ret);
        return ret;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            callback.onBookListQuery(list);
        }
    }

    public interface Callback {
        void onBookListQuery(List<Book> list);
    }
}
