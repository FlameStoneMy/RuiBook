package com.ruitech.bookstudy.uibean;

public class BaseUI<T> {
    protected T t;
    public BaseUI(T t) {
        this.t = t;
    }

    public T getValue() {
        return t;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof BaseUI) {
            return t.equals(((BaseUI) object).t);
        } else return false;

    }
}
