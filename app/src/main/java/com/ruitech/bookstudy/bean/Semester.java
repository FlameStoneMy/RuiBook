package com.ruitech.bookstudy.bean;

import android.text.TextUtils;

import com.ruitech.bookstudy.R;

import androidx.annotation.StringRes;

public enum Semester {
    FIRST_SEMESTER("LAST_SEMESTER", R.string.first_semester),
    SECOND_SEMESTER("NEXT_SEMESTER", R.string.second_semester),
    SUMMER_VACATION("SUMMER_VACATION", R.string.summer_vacation),
    WINTER_VACATION("WINTER_VACATION", R.string.winter_vacation),
    OTHER_SCHOOL_YEAR("OTHER_SCHOOL_YEAR", R.string.others);

    public final String id;
    public final @StringRes int abbrResId;
    Semester(String id, @StringRes int abbrResId) {
        this.id = id;
        this.abbrResId = abbrResId;
    }

    public static Semester from(String id) {
        Semester ret = null;
        for (Semester Semester : values()) {
            if (TextUtils.equals(id, Semester.id)) {
                ret = Semester;
                break;
            }
        }
        return ret;
    }
}
