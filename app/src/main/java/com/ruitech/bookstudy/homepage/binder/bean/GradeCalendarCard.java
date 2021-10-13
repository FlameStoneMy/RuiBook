package com.ruitech.bookstudy.homepage.binder.bean;

import com.ruitech.bookstudy.bean.Grade;
import com.ruitech.bookstudy.homepage.CardType;

import org.json.JSONObject;

public class GradeCalendarCard extends Card {
    public final Grade grade;

    public GradeCalendarCard(CardType cardType, JSONObject jsonObject) {
        super(cardType);
        grade = Grade.from(jsonObject.optString("grade"));
    }
}