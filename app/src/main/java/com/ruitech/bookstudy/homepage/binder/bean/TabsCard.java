package com.ruitech.bookstudy.homepage.binder.bean;

import com.ruitech.bookstudy.homepage.CardFactory;
import com.ruitech.bookstudy.homepage.CardType;

import org.json.JSONObject;

import java.util.List;

public class TabsCard<T extends Tab> extends Card {
    private List<T> tabList;
    public TabsCard(CardType cardType, JSONObject jsonObject) {
        super(cardType);

        tabList = CardFactory.createCard(jsonObject.optJSONArray("resources"));
    }

    public List<T> getTabList() {
        return tabList;
    }
}
