package com.ruitech.bookstudy.homepage.binder.bean;

import com.ruitech.bookstudy.homepage.CardFactory;
import com.ruitech.bookstudy.homepage.CardType;

import org.json.JSONObject;

import java.util.List;

public abstract class Tab extends Card {
    private String refreshUrl;
    private String nextUrl;
    private List<Card> resourceList;
    public Tab(CardType cardType, JSONObject jsonObject) {
        super(cardType);
        refreshUrl = jsonObject.optString("refreshUrl");
        nextUrl = jsonObject.optString("nextUrl");

        resourceList = CardFactory.createCard(jsonObject.optJSONArray("resources"));
    }

    public String getRefreshUrl() {
        return refreshUrl;
    }

    public String getNextUrl() {
        return nextUrl;
    }

    public List<Card> getResourceList() {
        return resourceList;
    }

    public void merge(boolean reload, Tab tab) {
        refreshUrl = tab.refreshUrl;
        nextUrl = tab.nextUrl;
        if (reload) {
            resourceList.clear();
        }
        resourceList.addAll(tab.resourceList);
    }
}
