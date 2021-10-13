package com.ruitech.bookstudy.uibean;

import com.ruitech.bookstudy.bean.Book;
import com.ruitech.bookstudy.bean.Page;
import com.ruitech.bookstudy.utils.ListUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PageUI extends SelectedUI<Page> {
    public PageUI(Page page, boolean selected) {
        super(page, selected);
    }

    public static final List<PageUI> from(List<Page> list) {
        List<PageUI> ret = null;
        if (!ListUtils.isEmpty(list)) {
            ret = new ArrayList<>();
            Iterator<Page> iter = list.iterator();
            int i = 0;
            ret.add(new PageUI(iter.next(), true));
            while (iter.hasNext()) {
                ret.add(new PageUI(iter.next(), false));
            }
        }
        return ret;
    }

    public static final int from(List<Page> list, List<PageUI> tarList) {
        int selectedPos = -1;
        if (!ListUtils.isEmpty(list)) {
            Iterator<Page> iter = list.iterator();
            tarList.add(new PageUI(iter.next(), true));
            selectedPos = 0;
            while (iter.hasNext()) {
                tarList.add(new PageUI(iter.next(), false));
            }
        }
        return selectedPos;
    }
}
