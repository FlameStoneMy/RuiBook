package com.ruitech.bookstudy.bean;

import android.text.TextUtils;

import com.ruitech.bookstudy.R;

import androidx.annotation.StringRes;

public enum Grade {
    FIRST_GRADE("FIRST_GRADE", R.string.grade_one, GradeCategory.PRIMARY),
    SECOND_GRADE("SECOND_GRADE", R.string.grade_two, GradeCategory.PRIMARY),
    THIRD_GRADE("THIRD_GRADE", R.string.grade_three, GradeCategory.PRIMARY),
    FOURTH_GRADE("FOURTH_GRADE", R.string.grade_four, GradeCategory.PRIMARY),
    FIFTH_GRADE("FIFTH_GRADE", R.string.grade_five, GradeCategory.PRIMARY),
    SIXTH_GRADE("SIXTH_GRADE", R.string.grade_six, GradeCategory.PRIMARY),
    JUNIOR_ONE("GRADE_ONE", R.string.junior_one, GradeCategory.JUNIOR),
    JUNIOR_TWO("GRADE_TWO", R.string.junior_two, GradeCategory.JUNIOR),
    JUNIOR_THREE("GRADE_THREE", R.string.junior_three, GradeCategory.JUNIOR),
    SENIOR_ONE("SENIOR_ONE", R.string.senior_one, GradeCategory.SENIOR),
    SENIOR_TWO("SENIOR_TWO", R.string.senior_two, GradeCategory.SENIOR),
    SENIOR_THREE("SENIOR_THREE", R.string.senior_three, GradeCategory.SENIOR),
    OTHER_GRADE("OTHER_GRADE", R.string.others, null);

    public final String id;
    public final @StringRes int resId;
    public final GradeCategory gradeCategory;
    Grade(String id, @StringRes int resId, GradeCategory gradeCategory) {
        this.id = id;
        this.resId = resId;
        this.gradeCategory = gradeCategory;
    }

    public static Grade from(String id) {
        Grade ret = null;
        for (Grade grade : values()) {
            if (TextUtils.equals(id, grade.id)) {
                ret = grade;
                break;
            }
        }
        return ret;
    }
}
