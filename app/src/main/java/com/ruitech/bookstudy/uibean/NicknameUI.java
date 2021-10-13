package com.ruitech.bookstudy.uibean;

import com.ruitech.bookstudy.utils.ListUtils;

import java.util.ArrayList;
import java.util.List;

public class NicknameUI extends SelectedUI<String> {
    public NicknameUI(String nickname, boolean selected) {
        super(nickname, selected);
    }

    public static final List convert(List list) {
        List ret = null;
        if (!ListUtils.isEmpty(list)) {
            ret = new ArrayList(list);
            for (int i = 0; i < ret.size(); i++) {
                Object obj = ret.get(i);
                if (obj instanceof String) {
                    ret.set(i, new NicknameUI((String) obj, false));
                }
            }
        }
        return ret;
    }
}
