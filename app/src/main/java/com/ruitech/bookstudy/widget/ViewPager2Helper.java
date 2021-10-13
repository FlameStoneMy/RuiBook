package com.ruitech.bookstudy.widget;

import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerViewHelper;
import androidx.viewpager2.widget.ViewPager2;

public class ViewPager2Helper {

    public static LinearLayoutManager getLayoutManager(ViewPager2 viewPager2) {
        RecyclerView recyclerView = getInternalRecyclerView(viewPager2);
        return (LinearLayoutManager) recyclerView.getLayoutManager();
    }

    public static RecyclerView.ViewHolder getCurrentViewHolder(ViewPager2 viewPager2) {
        RecyclerView.ViewHolder ret = null;
        View v = getLayoutManager(viewPager2).findViewByPosition(viewPager2.getCurrentItem());
        if (v != null) {
            ret = RecyclerViewHelper.getViewHolder(((RecyclerView.LayoutParams) v.getLayoutParams()));
        }
        return ret;
    }

    public static void setNullItemAnimator(ViewPager2 viewPager2) {
        RecyclerView recyclerView = getInternalRecyclerView(viewPager2);
        recyclerView.setItemAnimator(null);
    }

    private static RecyclerView getInternalRecyclerView(ViewPager2 viewPager2) {
        return (RecyclerView) viewPager2.getChildAt(0);
    }
}
