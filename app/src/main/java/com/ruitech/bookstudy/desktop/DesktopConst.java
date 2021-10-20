package com.ruitech.bookstudy.desktop;

import com.ruitech.bookstudy.bean.Grade;
import com.ruitech.bookstudy.desktop.bean.Album;

import static com.ruitech.bookstudy.utils.Const.BASE_URL;

public class DesktopConst {

    private static final String CATEGORY_PAGE_URL = BASE_URL + "/smallClass/v1/querySmallClassCategoryByGrade?grade=";
    public static final String getCategoryPageUrl(Grade grade) {
        return CATEGORY_PAGE_URL + grade.id;
    }

    public static final String MODULE_PAGE_URL = BASE_URL + "/smallClass/v1/querySmallClassByGradePage";

    private static final String ALBUM_PAGE_URL = BASE_URL + "/videoClass/v1/albumInfo?albumId=";
    public static final String getAlbumPageUrl(Album album) {
        return ALBUM_PAGE_URL + album.id;
    }
}