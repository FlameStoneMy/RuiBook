package com.ruitech.bookstudy.utils;

import com.ruitech.bookstudy.bean.Gender;
import com.ruitech.bookstudy.bean.Grade;
import com.ruitech.bookstudy.bean.Subject;

public class Const {
    public static final String BASE_URL = "https://api.bendiclass.com/external"; //

    public static final String BOOK_QUERY_URL = BASE_URL + "/textbook/v1/getTextbookListByLabel?";
    public static final String getBookQueryUrl(Subject subject, Grade grade) {
        return BOOK_QUERY_URL + "subject=" + subject.id + "&grade=" + grade.id;
    }

    public static final String NICKNAME_LIST_QUERY_URL = BASE_URL + "/configure/v1/queryDefaultNickname";
    public static final String NICKNAME_LIST_GENDER_QUERY_URL = NICKNAME_LIST_QUERY_URL + "?sex=";
    public static final String getNickNameQueryUrl(Gender gender) {
        return NICKNAME_LIST_GENDER_QUERY_URL + gender.id;
    }

    public static final String GRADE_LIST_QUERY_URL = BASE_URL + "/configure/v1/queryGradeList";

    private static final String HOME_PAGE_URL = BASE_URL + "/appHome/v1/queryAppHomeCard?grade=";
    public static final String getHomePageUrl(Grade grade) {
        return HOME_PAGE_URL + grade.id;
    }


    private static final String UPGRADE_URL = "https://test-api.bendiclass.com/external" + "/boxVersion/v1/queryLatestVersion?version=";
    public static final String getUpgradeUrl(long version) {
        return UPGRADE_URL + version;
    }
}
