package com.ruitech.bookstudy.homepage;

import com.ruitech.bookstudy.homepage.binder.bean.Card;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CardFactory {
    private static Map<String, CardType> cardMap = new HashMap<>();
    static {
        for (CardType cardType : CardType.values()) {
            cardMap.put(cardType.getType(), cardType);
        }
    }
    public static final <T extends Card> T createCard(JSONObject jsonObject) {
        String type = jsonObject.optString("type");
        T ret = null;
        if (type != null) {
            CardType cardType = cardMap.get(type);
            if (cardType != null) {
                ret = cardType.parse(jsonObject);
            }
        }
        return ret;
    }

    public static final <T extends Card> List<T> createCard(JSONArray jsonArray) {
        List<T> ret = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                T card = CardFactory.createCard(jsonArray.getJSONObject(i));
                if (card != null) {
                    ret.add(card);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }
}
