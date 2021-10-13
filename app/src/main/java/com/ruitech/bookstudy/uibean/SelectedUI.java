package com.ruitech.bookstudy.uibean;

import android.util.Log;

public class SelectedUI<T> extends BaseUI<T> {
    private static final String TAG = "SelectedUI";
    private boolean selected;

    public SelectedUI(T t, boolean selected) {
        super(t);
        this.selected = selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof SelectedUI) {
            boolean ret = selected == ((SelectedUI) object).selected &&
                    super.equals(object);
            return ret;
        } else return false;
    }
}
