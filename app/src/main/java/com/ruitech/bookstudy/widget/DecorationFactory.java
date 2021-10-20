package com.ruitech.bookstudy.widget;

import com.ruitech.bookstudy.utils.UIHelper;

import androidx.recyclerview.widget.RecyclerView;

public class DecorationFactory {
    public static RecyclerView.ItemDecoration get8_0_8_0Space16_0_16_0() {
        int dp8 = UIHelper.dp2px(8);
        int dp16 = UIHelper.dp2px(16);
        return new SimpleItemDecoration(
                dp8, 0, dp8, 0,
                dp16, 0, dp16, 0);
    }

    public static RecyclerView.ItemDecoration get16_0_16_16Space16_16_16_16() {
        int dp16 = UIHelper.dp2px(16);
        return new SimpleItemDecoration(
                dp16, 0, dp16, dp16,
                dp16, dp16, dp16, dp16);
    }

    public static RecyclerView.ItemDecoration get2_0_2_0Space16_0_16_0() {
        int dp2 = UIHelper.dp2px(2);
        int dp16 = UIHelper.dp2px(16);
        return new SimpleItemDecoration(
                dp2, 0, dp2,0,
                dp16, 0, dp16, 0);
    }

    public static RecyclerView.ItemDecoration get14_0_14_0Space36_28_36_28() {
        int dp4 = UIHelper.dp2px(4);
        int dp8 = UIHelper.dp2px(8);
        int dp20 = UIHelper.dp2px(20);
        return new SimpleItemDecoration(
                dp8, dp8, dp8, dp8,
                dp4, dp20, dp4, dp20);
    }

    public static RecyclerView.ItemDecoration get30_30_30_30Space0_0_0_0() {
        int dp30 = UIHelper.dp2px(10);
        return new SimpleItemDecoration(
                dp30, dp30, dp30, dp30,
                dp30, dp30, dp30, dp30);
    }

    public static RecyclerView.ItemDecoration get22_19_22_19Space0_0_0_19() {
        int dp22 = UIHelper.dp2px(22);
        int dp19 = UIHelper.dp2px(19);
        return new SimpleItemDecoration(
                dp22, dp19, dp22, dp19,
                0, 0, 0, dp19);
    }

    public static RecyclerView.ItemDecoration get9_8_9_8Space0_0_0_8() {
        int dp9 = UIHelper.dp2px(9);
        int dp8 = UIHelper.dp2px(8);
        return new SimpleItemDecoration(
                dp9, dp8, dp9, dp8,
                0, 0, 0, dp8);
    }
}
