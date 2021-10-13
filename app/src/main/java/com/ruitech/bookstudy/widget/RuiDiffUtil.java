package com.ruitech.bookstudy.widget;

import android.util.Log;

import com.ruitech.bookstudy.RuiDiffCallback;
import com.ruitech.bookstudy.uibean.SelectedUI;

import java.util.List;

import androidx.recyclerview.widget.DiffUtil;
import me.drakeet.multitype.MultiTypeAdapter;

public class RuiDiffUtil {

    public static void onNewData(MultiTypeAdapter adapter, List newList) {
        List oldList = adapter.getItems();
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(new RuiDiffCallback(adapter, oldList, newList));
        adapter.setItems(newList);
        result.dispatchUpdatesTo(adapter);
    }

    public static void updateSelectPos(MultiTypeAdapter adapter, int newPos, int currPos) {
        List list = adapter.getItems();
        if (currPos >= 0) {
            Object currPosObj = list.get(currPos);
            if (currPosObj instanceof SelectedUI) {
                ((SelectedUI) currPosObj).setSelected(false);
                adapter.notifyItemChanged(currPos);
            }
        }
        if (newPos >= 0) {
            Object newPosObj = list.get(newPos);
            // valid to handle.
            if (newPosObj instanceof SelectedUI) {
                SelectedUI newPageUI = (SelectedUI) newPosObj;
                newPageUI.setSelected(true);
                adapter.notifyItemChanged(newPos);
            }
        }
    }
}
