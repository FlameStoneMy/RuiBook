package com.ruitech.bookstudy.homepage;

import com.ruitech.bookstudy.homepage.binder.bean.Card;
import com.ruitech.bookstudy.homepage.binder.bean.ClickReadCard;
import com.ruitech.bookstudy.homepage.binder.bean.GradeCalendarCard;
import com.ruitech.bookstudy.homepage.binder.bean.SubjectTab;
import com.ruitech.bookstudy.homepage.binder.bean.Tab;
import com.ruitech.bookstudy.homepage.binder.bean.TabsCard;

import org.json.JSONObject;

public enum CardType {

    CARD_GRADE_CALENDAR("card_grade_calendar") {
        @Override
        public Card parse(JSONObject jsonObject) {
            return new GradeCalendarCard(this, jsonObject);
        }
    },

    CARD_CLICK_READ("card_click_read") {
        @Override
        public Card parse(JSONObject jsonObject) {
            return new ClickReadCard(this, jsonObject);
        }
    },

    TABS("tabs") {
        @Override
        public Card parse(JSONObject jsonObject) {
            return new TabsCard(this, jsonObject);
        }
    },

    TAB_SUBJECT("tab_subject") {
        @Override
        public Tab parse(JSONObject jsonObject) {
            return new SubjectTab(this, jsonObject);
        }
    };

    public final String getType() {
        return type;
    }
    public abstract <T extends Card> T parse(JSONObject jsonObject);
    String type;
    CardType(String type) {
        this.type = type;
    }
}
