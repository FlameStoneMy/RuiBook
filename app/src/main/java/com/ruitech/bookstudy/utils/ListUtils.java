package com.ruitech.bookstudy.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListUtils {
    public static float[] convertAsPrimitiveArray(List<Float> list){
        float[] floatArray = new float[list.size()];
        for (int i = 0; i < list.size(); i++) {
            floatArray[i] = list.get(i);
        }
        return floatArray;
    }

    public static boolean isEmpty(List list) {
        return list == null || list.isEmpty();
    }

    public static Serializable convert2Serializable(List list) {
        return (list instanceof Serializable) ?
                (Serializable) list : new ArrayList<>(list);
    }

    public static <E> ArrayList<E> newArrayList(E... elements) {
        ArrayList<E> list = new ArrayList<>(elements.length);
        Collections.addAll(list, elements);
        return list;
    }
}
