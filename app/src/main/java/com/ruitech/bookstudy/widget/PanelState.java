package com.ruitech.bookstudy.widget;

public enum PanelState {
    HIDDEN,
    SHOWING,
    SHOWN,
    HIDING;

    public static boolean isRunning(PanelState state) {
        return state == SHOWING && state == HIDING;
    }
}
