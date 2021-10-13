package com.ruitech.bookstudy.widget;

import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback;

import java.util.ArrayList;

public class TabPagerViewPagerObserver extends OnPageChangeCallback {


    private ArrayList<OnPageChangeCallback> mListeners = new ArrayList<OnPageChangeCallback>();

    public void addListener(OnPageChangeCallback listener) {
        if (listener != null) {
            mListeners.add(listener);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        for (OnPageChangeCallback l : mListeners) {
            if (l != null) {
                l.onPageScrollStateChanged(state);
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        for (OnPageChangeCallback l : mListeners) {
            if (l != null) {
                l.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        }
    }

    @Override
    public void onPageSelected(int position) {
        for (OnPageChangeCallback l : mListeners) {
            if (l != null) {
                l.onPageSelected(position);
            }
        }
    }

}
