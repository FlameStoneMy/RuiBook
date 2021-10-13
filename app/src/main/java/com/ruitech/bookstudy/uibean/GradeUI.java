package com.ruitech.bookstudy.uibean;

import com.ruitech.bookstudy.bean.Grade;
import com.ruitech.bookstudy.utils.ListUtils;

import java.util.ArrayList;
import java.util.List;

public class GradeUI extends SelectedUI<Grade> {
    public GradeUI(Grade grade, boolean selected) {
        super(grade, selected);
    }

    /**
     * @return selected grade. (default the first one.)
     */
    public static final int convert(List list) {
        return convert(list, null);
    }

    public static final int convert(List list, Grade selectedGrade) {
        int ret = -1;
        int firstGradePos = -1;
        if (!ListUtils.isEmpty(list)) {
            boolean first = true;
            for (int i = 0; i < list.size(); i++) {
                Object obj = list.get(i);
                if (obj instanceof Grade) {
                    Grade currGrade = (Grade)obj;
                    if (currGrade == selectedGrade) {
                        ret = i;
                        list.set(i, new GradeUI(currGrade, true));
                    } else {
                        if (first) {
                            firstGradePos = i;
                            first = false;
                        }
                        list.set(i, new GradeUI(currGrade, false));
                    }

                }
            }

            if (ret < 0 && firstGradePos >= 0) {
                ret = firstGradePos;
                ((GradeUI) list.get(firstGradePos)).setSelected(true);
            }
        }
        return ret;
    }
}
