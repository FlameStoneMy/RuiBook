package com.ruitech.bookstudy.bean;

import android.text.TextUtils;

import com.ruitech.bookstudy.R;

import androidx.annotation.StringRes;

public enum Gender {
    BOY("boy", R.string.boy),
    GIRL("girl", R.string.girl);

    public final String id;
    public final @StringRes int resId;
    Gender(String id, @StringRes int resId) {
        this.id = id;
        this.resId = resId;
    }

    public static Gender from(String id) {
        Gender ret = BOY;
        for (Gender Gender : values()) {
            if (TextUtils.equals(id, Gender.id)) {
                ret = Gender;
                break;
            }
        }
        return ret;
    }
}
