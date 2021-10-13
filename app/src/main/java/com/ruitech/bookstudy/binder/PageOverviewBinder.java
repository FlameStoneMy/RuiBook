package com.ruitech.bookstudy.binder;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ruitech.bookstudy.R;
import com.ruitech.bookstudy.bean.Page;
import com.ruitech.bookstudy.book.BookUtil;
import com.ruitech.bookstudy.uibean.PageUI;
import com.ruitech.bookstudy.utils.DisplayOptions;
import com.ruitech.bookstudy.utils.ImageHelper;
import com.ruitech.bookstudy.utils.UIHelper;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import me.drakeet.multitype.ItemViewBinder;

public class PageOverviewBinder extends ItemViewBinder<PageUI, PageOverviewBinder.ViewHolder> implements IUISame<PageUI> {

    private static final String TAG = "PageOverviewBinder";

    private Callback callback;
    public PageOverviewBinder(Callback callback) {
        this.callback = callback;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
//        Log.d(TAG, "onCreateViewHolder: " + this);
        View v = inflater.inflate(R.layout.binder_page_overview, parent, false);
        return new ViewHolder(v);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull PageUI item) {
//        Log.d(TAG, "onBindViewHolder: " + this);
        holder.bindData(item);
    }

    @Override
    public boolean areUISame(@NonNull PageUI oldItem, @NonNull PageUI newItem) {
        return oldItem.equals(newItem);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView img;
        private TextView pageNumTv;
        private Page page;
        private PaintDrawable bgDrawable;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            pageNumTv = itemView.findViewById(R.id.page_num_tv);
            itemView.setOnClickListener(this);
            bgDrawable = new PaintDrawable();
            bgDrawable.setCornerRadius(UIHelper.dp2px(16));
            itemView.setBackground(bgDrawable);
        }

        public void bindData(PageUI pageUI) {
            page = pageUI.getValue();
            android.util.Log.d(TAG, "bindData: " + page.getPageNum() + " " + img.getWidth() + " " + img.getHeight());
            pageNumTv.setText(String.valueOf(pageUI.getValue().getPageNum()));
            ImageHelper.displayImage(img,
                    new ImageHelper.Params.Builder()
                            .imageUri(BookUtil.getPictureUriStr(page.getBookId(), page.getBookGenuineId(), page.getPageNum()))
                            .width(img.getWidth())
                            .height(img.getHeight())
                            .options(DisplayOptions.getIconDefault())
                            .build());
            if (pageUI.isSelected()) {
                bgDrawable.getPaint().setColor(itemView.getResources().getColor(R.color._4989ff));
//                itemView.setBackgroundColor();
                pageNumTv.setTextColor(pageNumTv.getResources().getColor(R.color.white));
            } else {
                bgDrawable.getPaint().setColor(itemView.getResources().getColor(R.color.white));
                pageNumTv.setTextColor(pageNumTv.getResources().getColor(R.color._5e5e5e));
            }
        }

        @Override
        public void onClick(View v) {
            callback.onPageSelectedFromOverview(getLayoutPosition());
        }
    }

    public interface Callback {
        // position in overview list.
        void onPageSelectedFromOverview(int pos);
    }
}
