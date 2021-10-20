package com.ruitech.bookstudy.upgrade;

public class UpgradeBean {
    public int versionCode;
    public String versionName;
    public String url;
    public int size;

    public UpgradeBean(int versionCode, String versionName, String url, int size) {
        this.versionCode = versionCode;
        this.versionName = versionName;
        this.url = url;
        this.size = size;
    }
}
