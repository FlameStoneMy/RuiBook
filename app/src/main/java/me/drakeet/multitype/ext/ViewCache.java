package me.drakeet.multitype.ext;

import android.view.LayoutInflater;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import me.drakeet.multitype.MultiTypeAdapter;

public interface ViewCache {
    View getViewById(int layoutId, Object tag);

    LayoutInflater getInflater();

    @Deprecated
    boolean recycle(RecyclerView recyclerView);

    @Deprecated
    boolean recycle(MultiTypeAdapter adapter);
}
