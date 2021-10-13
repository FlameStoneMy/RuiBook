package com.ruitech.bookstudy.binder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ruitech.bookstudy.R;
import com.ruitech.bookstudy.bean.Page;
import com.ruitech.bookstudy.uibean.PageUI;
import com.ruitech.bookstudy.widget.panelhelper.MenuBottomPanelHelper.Callback;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import me.drakeet.multitype.ItemViewBinder;

public class MenuItemBinder extends ItemViewBinder<PageUI, MenuItemBinder.ViewHolder> implements IUISame<PageUI> {

    private static final String TAG = "MenuItemBinder";

    private Callback callback;
    public MenuItemBinder(Callback callback) {
        this.callback = callback;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View v = inflater.inflate(R.layout.binder_menu_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull PageUI item) {
        holder.bindData(item);
    }

    @Override
    public boolean areUISame(@NonNull PageUI oldItem, @NonNull PageUI newItem) {
        return oldItem.equals(newItem);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Context context;
        private Page page;
        private TextView descTv;
        private TextView pageTv;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            descTv = itemView.findViewById(R.id.desc_tv);
            pageTv = itemView.findViewById(R.id.page_tv);
            itemView.setOnClickListener(this);
        }

        public void bindData(PageUI pageUI) {
            android.util.Log.d(TAG, "bindData: ");
            this.page = pageUI.getValue();
            descTv.setText(page.getPageDesc());
            pageTv.setText(itemView.getResources().getString(R.string.page_num, page.getPageNum()));
            int textColor = context.getResources().getColor(pageUI.isSelected() ? R.color._4989ff : R.color._7b7d82);
            descTv.setTextColor(textColor);
            pageTv.setTextColor(textColor);
        }

        @Override
        public void onClick(View v) {
            callback.onPageSelectedFromMenu(page);
        }
    }
}
