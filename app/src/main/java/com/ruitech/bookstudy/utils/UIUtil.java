package com.ruitech.bookstudy.utils;

import com.ruitech.bookstudy.bean.Poster;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class UIUtil {

    public static String getAppropriatePosterUrl(List<Poster> posterList, int widthKey, int heightKey, boolean getInappropriatePoster) {
        Poster poster = getAppropriatePoster(posterList, widthKey, heightKey, getInappropriatePoster);
        return poster == null ? null : poster.getUrl();
    }

    public static Poster getAppropriatePoster(List<Poster> posterList, int widthKey, int heightKey, boolean getInappropriatePoster) {
        if (posterList == null || posterList.size() == 0)
            return null;

        if (getInappropriatePoster && posterList.size() == 1) {
            return posterList.get(0);
        }

        Poster poster = getAppropriatePosterByRatio(posterList, widthKey, heightKey);
        if (poster != null) {
            return poster;
        }

        poster = getAppropriatePosterByRatioNearly(posterList, widthKey, heightKey);
        if (poster != null) {
            return poster;
        }

        if (getInappropriatePoster) {
            return posterList.size() >= 1 ? posterList.get(0) : null;
        } else {
            return null;
        }
    }


    public static Poster getAppropriatePosterByRatio(List<Poster> posterList, int widthKey, int heightKey) {
        List<Poster> list = new LinkedList<>();
        for (Poster poster : posterList) {
            int height = poster.getHeight();
            int width = poster.getWidth();

            if (height == heightKey && width == widthKey)
                return poster;

            if (height == -1 || width == -1)
                continue;

            if (Math.abs(width / (float) height - (widthKey / (float) heightKey)) < 0.05) {
                list.add(poster);
            }
        }

        if (list.size() == 1) {
            return list.get(0);
        }

        if (list.size() > 1) {
            Poster fake = new Poster();
            fake.setWidth(widthKey);
            fake.setHeight(heightKey);

            list.add(fake);

            Collections.sort(list, (o1, o2) -> o1.getWidth() * o1.getHeight() - o2.getHeight() * o2.getWidth());

            int index = -1;
            for (int i = 0; i < list.size(); i++) {
                Poster poster = list.get(i);
                if (poster == fake) {
                    index = i;
                    break;
                }
            }

            if (index == 0) {
                return list.get(1);
            }

            if (index == list.size() - 1) {
                return list.get(list.size() - 2);
            }

            Poster previous = list.get(index - 1);
            Poster next = list.get(index + 1);

            float scalePrevious = widthKey * heightKey / (float) (previous.getWidth() * previous.getHeight());
            float scaleNext = next.getWidth() * next.getHeight() / (float) (widthKey * heightKey);

            if (scalePrevious < scaleNext) {
                return previous;
            } else {
                return next;
            }
        }

        return null;
    }

    public static Poster getAppropriatePosterByRatioNearly(List<Poster> posterList, int widthKey, int heightKey) {
        List<Poster> list = new LinkedList<>();
        for (Poster poster : posterList) {
            int height = poster.getHeight();
            int width = poster.getWidth();

            if (height == heightKey && width == widthKey)
                return poster;

            if (height == -1 || width == -1)
                continue;

            if (Math.abs(width / (float) height - (widthKey / (float) heightKey)) < 0.25) {
                list.add(poster);
            }
        }

        if (list.size() == 1) {
            return list.get(0);
        }

        if (list.size() > 1) {
            Collections.sort(list, (o1, o2) -> Math.abs(o1.getWidth() * heightKey - widthKey * o1.getHeight()) - Math.abs(o2.getWidth() * heightKey - widthKey * o2.getHeight()));

            return list.get(0);
        }

        return null;
    }

}
