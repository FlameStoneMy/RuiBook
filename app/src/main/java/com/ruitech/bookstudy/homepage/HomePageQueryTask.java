package com.ruitech.bookstudy.homepage;

import android.os.AsyncTask;
import android.util.Log;

import com.ruitech.bookstudy.bean.Grade;
import com.ruitech.bookstudy.bean.Subject;
import com.ruitech.bookstudy.homepage.binder.bean.Card;
import com.ruitech.bookstudy.homepage.binder.bean.ClickReadCard;
import com.ruitech.bookstudy.homepage.binder.bean.GradeCalendarCard;
import com.ruitech.bookstudy.homepage.binder.bean.SubjectTab;
import com.ruitech.bookstudy.homepage.binder.bean.TabsCard;
import com.ruitech.bookstudy.utils.APIUtil;
import com.ruitech.bookstudy.utils.Const;
import com.ruitech.bookstudy.utils.NetworkResponse;
import com.ruitech.bookstudy.utils.UrlInvalidException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

public class HomePageQueryTask extends AsyncTask<Object, Object, NetworkResponse> {
    private static final String TAG = "HomePageQueryTask";
    private GradeCalendarCard gradeCalendarCard;
    private TabsCard<SubjectTab> tabsCard;
    private Grade grade;
    private Callback callback;
    public HomePageQueryTask(Grade grade, Callback callback) {
        this.grade = grade;
        this.callback = callback;
    }

    @Override
    protected NetworkResponse doInBackground(Object[] objects) {
        NetworkResponse ret = NetworkResponse.RESPONSE_ERROR;

        Response response;
        try {
            response = APIUtil.getResponse(Const.getHomePageUrl(grade));
            if (response.code() == 200) {
                JSONArray jsonArray = new JSONObject(response.body().string()).optJSONArray("data");
                System.out.println("meng here: " + jsonArray.toString(2));

                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        Card card = CardFactory.createCard(jsonArray.getJSONObject(i));
                        if (card != null) {
                            if (card instanceof GradeCalendarCard) {
                                gradeCalendarCard = (GradeCalendarCard) card;
                            } else if (card instanceof TabsCard) {
                                tabsCard = (TabsCard) card;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
//                retList.add(new ClickReadCard(CardType.CARD_CLICK_READ, null));
                ret = NetworkResponse.RESPONSE_OK;
            }
        } catch (IOException e) {
            ret = NetworkResponse.RESPONSE_NETWORK;
            e.printStackTrace();
        } catch (UrlInvalidException | JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "query done: ");
        return ret;
    }

    @Override
    protected void onPostExecute(NetworkResponse result) {
        callback.onHomePageResult(result, gradeCalendarCard, tabsCard);
    }

    public interface Callback {
        void onHomePageResult(NetworkResponse ret, GradeCalendarCard gradeCalendarCard, TabsCard<SubjectTab> tabsCard);
    }
}
