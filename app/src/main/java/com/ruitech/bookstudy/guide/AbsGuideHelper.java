package com.ruitech.bookstudy.guide;

import android.content.Context;
import android.graphics.drawable.PaintDrawable;

import com.ruitech.bookstudy.R;
import com.ruitech.bookstudy.guide.task.TaskCallback;
import com.ruitech.bookstudy.utils.UIHelper;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import me.drakeet.multitype.MultiTypeAdapter;

public abstract class AbsGuideHelper {
    protected Context context;
    protected RecyclerView recyclerView;
    protected MultiTypeAdapter adapter;
    protected TaskCallback callback;
    public AbsGuideHelper(RecyclerView recyclerView, TaskCallback callback) {
        this.recyclerView = recyclerView;
        this.callback = callback;

        this.context = recyclerView.getContext();
        PaintDrawable bgDrawable = new PaintDrawable(context.getResources().getColor(R.color._ffffff));
        int bgCornerRadius = UIHelper.dp2px(26);
        bgDrawable.setCornerRadii(new float[] {
                bgCornerRadius, bgCornerRadius, bgCornerRadius, bgCornerRadius,
                0, 0, 0, 0
        });
        recyclerView.setBackground(bgDrawable);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);

        adapter = new MultiTypeAdapter();
        initAdapter();
        gridLayoutManager.setSpanSizeLookup(getSpanSizeLookup());
        recyclerView.setAdapter(adapter);
    }

    protected void initAdapter() {
    }

    protected abstract GridLayoutManager.SpanSizeLookup getSpanSizeLookup();
}
