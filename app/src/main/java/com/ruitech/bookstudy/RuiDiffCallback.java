package com.ruitech.bookstudy;

import com.ruitech.bookstudy.binder.IUISame;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import me.drakeet.multitype.ItemViewBinder;
import me.drakeet.multitype.MultiTypeAdapter;

public class RuiDiffCallback extends DiffUtil.Callback {

    private MultiTypeAdapter adapter;
    private List oldList;
    private List newList;

    public RuiDiffCallback(MultiTypeAdapter adapter, List oldList, List newList) {
        this.adapter = adapter;
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList == null ? 0 : oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList == null ? 0 : newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        Object oldItem = oldList.get(oldItemPosition);
        Object newItem = newList.get(newItemPosition);
        return oldItem.equals(newItem);
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Object oldItem = oldList.get(oldItemPosition);
        Object newItem = newList.get(newItemPosition);
        ItemViewBinder<?, ?> binder = getItemBinder(oldItemPosition); // same binder as newItemPosition.
        if (binder instanceof IUISame) {
            return ((IUISame) binder).areUISame(oldItem, newItem);
        } else {
            return false; // default action.
        }
    }

    @Nullable
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        ItemViewBinder<?, ?> binder = getItemBinder(oldItemPosition);
        if (binder instanceof IUISame) {
            Object oldItem = oldList.get(oldItemPosition);
            Object newItem = newList.get(newItemPosition);
            return ((IUISame) binder).getChangePayload(oldItem, newItem);
        } else {
            return null;
        }
    }

    private ItemViewBinder<?, ?> getItemBinder(int position) {
        int index = adapter.getItemViewType(position);
        ItemViewBinder<?, ?> binder = adapter.getTypePool().getItemViewBinders().get(index);
        return binder;
    }
}
