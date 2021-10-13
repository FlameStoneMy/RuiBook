package com.ruitech.bookstudy.homepage.binder.bean;

import com.ruitech.bookstudy.bean.Subject;
import com.ruitech.bookstudy.homepage.CardType;

import org.json.JSONObject;

public class SubjectTab extends Tab {
    private Subject subject;
    public SubjectTab(CardType cardType, JSONObject jsonObject) {
        super(cardType, jsonObject);
        subject = Subject.from(jsonObject.optString("subject"));
    }

    public Subject getSubject() {
        return subject;
    }
}
