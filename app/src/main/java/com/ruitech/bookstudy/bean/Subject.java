package com.ruitech.bookstudy.bean;

import android.content.Context;
import android.text.TextUtils;

import com.ruitech.bookstudy.ITitleProvider;
import com.ruitech.bookstudy.R;

import androidx.annotation.StringRes;

public enum Subject implements ITitleProvider {
    MATH("MATHEMATICS", R.string.math),
    CHINESE("CHINESE", R.string.chinese),
    ENGLISH("ENGLISH", R.string.english),
    MORALITY("MORALITY", R.string.morality),
    HISTORY("HISTORY", R.string.history),
    GEOGRAPHY("GEOGRAPHY", R.string.geography),
    CHEMISTRY("CHEMISTRY", R.string.chemistry),
    PHYSICS("PHYSICS", R.string.physics),
    BIOLOGY("BIOLOGY", R.string.biology),
    SCIENCE("SCIENCE", R.string.science),
    OTHER("OTHER", R.string.others),
    VOLUME("VOLUME", R.string.volume),
    FINE_ARTS("FINE_ARTS", R.string.fine_arts),
    WRITE("WRITE", R.string.write);

    public final String id;
    public final @StringRes int resId;
    Subject(String id, @StringRes int resId) {
        this.id = id;
        this.resId = resId;
    }

    public static Subject from(String id) {
        Subject ret = null;
        for (Subject subject : values()) {
            if (TextUtils.equals(id, subject.id)) {
                ret = subject;
                break;
            }
        }
        return ret;
    }

    @Override
    public String getTitle(Context context) {
        return context.getResources().getString(resId);
    }
}
