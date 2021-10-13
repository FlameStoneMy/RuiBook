package com.ruitech.bookstudy.bean;

import android.text.TextUtils;

public class Page {
    private int ordinal;
    private int pageNum;
    private String bookId;
    private String bookGenuineId;
    private String pageDesc;

    private LocationGroup locationGroup;

    public Page(int ordinal, int pageNum, String bookId, String bookGenuineId) {
        this(ordinal, pageNum, null, bookId, bookGenuineId);
    }

    public Page(int ordinal, int pageNum, String pageDesc, String bookId, String bookGenuineId) {
        this.ordinal = ordinal;
        this.pageNum = pageNum;
        this.pageDesc = pageDesc;
        this.bookId = bookId;
        this.bookGenuineId = bookGenuineId;
    }

    public int getOrdinal() {
        return ordinal;
    }

    public int getPageNum() {
        return pageNum;
    }

    public String getPageDesc() {
        return pageDesc;
    }

    public String getBookId() {
        return bookId;
    }

    public String getBookGenuineId() {
        return bookGenuineId;
    }

    public void setLocationGroup(LocationGroup locationGroup) {
        this.locationGroup = locationGroup;
    }

    public LocationGroup getLocationGroup() {
        return locationGroup;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Page) {
            Page tarPage = (Page) object;
            return pageNum == tarPage.getPageNum() &&
                    TextUtils.equals(bookId, tarPage.bookId);
        } else return false;
    }
}
