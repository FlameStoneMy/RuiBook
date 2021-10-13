package com.ruitech.bookstudy.homepage.binder.bean;

import com.ruitech.bookstudy.uibean.SelectedUI;
import com.ruitech.bookstudy.utils.ListUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import androidx.core.util.Pair;

public class SubjectTabUI extends SelectedUI<SubjectTab> {
    public SubjectTabUI(SubjectTab subjectTab, boolean selected) {
        super(subjectTab, selected);
    }

    public static final Pair<Integer, List<SubjectTabUI>> from(List<SubjectTab> list) {
        List<SubjectTabUI> ret = new ArrayList<>();
        int selectedPos = -1;
        if (!ListUtils.isEmpty(list)) {
            for (int i = 0; i < list.size(); i++) {
                SubjectTab subjectTab = list.get(i);
                boolean needSelected = needSelected(selectedPos, subjectTab);
                if (needSelected) {
                    selectedPos = i;
                }
                ret.add(new SubjectTabUI(subjectTab, needSelected));
            }
        }
        return new Pair<>(selectedPos, ret);
    }

    private static final boolean needSelected(int selectedPos, SubjectTab subjectTab) {
        return selectedPos < 0 && !subjectTab.getResourceList().isEmpty();
    }
}
