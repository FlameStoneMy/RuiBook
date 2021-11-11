package com.ruitech.bookstudy.bean;

import android.text.TextUtils;

import com.ruitech.bookstudy.R;

import androidx.annotation.StringRes;

public enum GradeCategory {
    PRIMARY("PRIMARY_GRADES", R.string.primary),
    JUNIOR("MIDDLE_GRADES", R.string.junior),
    SENIOR("HIGH_GRADES", R.string.senior);

    public final String id;
    public final @StringRes int resId;
    GradeCategory(String id, @StringRes int resId) {
        this.id = id;
        this.resId = resId;
    }

    public static GradeCategory from(String id) {
        GradeCategory ret = PRIMARY;
        for (GradeCategory gradeCategory : values()) {
            if (TextUtils.equals(id, gradeCategory.id)) {
                ret = gradeCategory;
                break;
            }
        }
        return ret;
    }
}
