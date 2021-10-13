package com.ruitech.bookstudy;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.ruitech.bookstudy.bean.Gender;
import com.ruitech.bookstudy.bean.Grade;

public class RuiPreferenceUtil {
    private static final String TAG = "RuiPreferenceUtil";

    private static final String SHARED_PREFERENCE = "general";

    public static final SharedPreferences getOnlineSharedPreference() {
        return App.applicationContext().getSharedPreferences(SHARED_PREFERENCE, Context.MODE_PRIVATE);
    }

    private static final String KEY_NICKNAME = "nickname";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_GRADE = "grade";

    public static final void setNickname(String nickname) {
        getOnlineSharedPreference().edit().putString(KEY_NICKNAME, nickname).apply();
    }

    public static final String getNickname() {
        return getOnlineSharedPreference().getString(KEY_NICKNAME, null);
    }

    public static final void setGender(Gender gender) {
        getOnlineSharedPreference().edit().putString(KEY_GENDER, gender.id).apply();
    }

    public static final Gender getGender() {
        return Gender.from(getOnlineSharedPreference().getString(KEY_GENDER, null));
    }

    public static final void setGrade(Grade grade) {
        getOnlineSharedPreference().edit().putString(KEY_GRADE, grade.id).apply();
    }

    public static final Grade getGrade() {
        return Grade.from(getOnlineSharedPreference().getString(KEY_GRADE, null));
    }

    private static final String KEY_CLICK_READ_SPEED = "click_read_speed";
    private static final String KEY_SHOW_CLICK_READ_TRANSLATION = "show_click_read_translation";
    private static final String KEY_SHOW_CLICK_READ_BORDERS = "show_click_read_borders";

    public static final void setClickReadSpeed(float speed) {
        Log.d(TAG, "setClickReadSpeed: " + speed);
        getOnlineSharedPreference().edit().putFloat(KEY_CLICK_READ_SPEED, speed).apply();
    }

    public static final float getClickReadSpeed() {
        return getOnlineSharedPreference().getFloat(KEY_CLICK_READ_SPEED, 1F);
    }

    public static final void setShowClickReadTranslation(boolean show) {
        getOnlineSharedPreference().edit().putBoolean(KEY_SHOW_CLICK_READ_TRANSLATION, show).apply();
    }

    public static final boolean showClickReadTranslation() {
        return getOnlineSharedPreference().getBoolean(KEY_SHOW_CLICK_READ_TRANSLATION, true);
    }

    public static final void setShowClickReadBorders(boolean show) {
        getOnlineSharedPreference().edit().putBoolean(KEY_SHOW_CLICK_READ_BORDERS, show).apply();
    }

    public static final boolean showClickReadBorders() {
        return getOnlineSharedPreference().getBoolean(KEY_SHOW_CLICK_READ_BORDERS, true);
    }
}
