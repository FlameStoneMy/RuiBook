package com.ruitech.bookstudy;

import android.content.Context;

import androidx.annotation.StringRes;

public enum BodyType implements ITitleProvider {
    CLICK_READ(R.string.click_read),
    TEST(R.string.read_test);

    public final int stringId;
    BodyType(@StringRes int stringId) {
        this.stringId = stringId;
    }

    @Override
    public String getTitle(Context context) {
        return context.getResources().getString(stringId);
    }
}
