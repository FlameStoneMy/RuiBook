package com.ruitech.bookstudy.desktop.binder;

import android.graphics.drawable.PaintDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ruitech.bookstudy.R;
import com.ruitech.bookstudy.desktop.CategoryActivity;
import com.ruitech.bookstudy.desktop.bean.Category;
import com.ruitech.bookstudy.utils.UIHelper;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import me.drakeet.multitype.ItemViewBinder;

public class CategoryBinder extends ItemViewBinder<Category, CategoryBinder.ViewHolder> {

    private static final String TAG = "CategoryBinder";

    public CategoryBinder() {

    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View v = inflater.inflate(R.layout.binder_category, parent, false);
        return new ViewHolder(v);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull Category item) {
        holder.bindData(item);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView title;
        private ImageView img;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            PaintDrawable p = new PaintDrawable();
            p.setCornerRadius(UIHelper.dp2px(27));
            p.getPaint().setColor(itemView.getContext().getResources().getColor(R.color._2cb1fd));
            itemView.setBackground(p);
            title = itemView.findViewById(R.id.title);
            img = itemView.findViewById(R.id.img);
            itemView.setOnClickListener(this);
        }

        private Category category;
        public void bindData(Category category) {
            android.util.Log.d(TAG, "bindData: " + itemView.isFocusable() + " ");
            this.category = category;
            title.setText(category.name);

//            ((ConstraintLayout.LayoutParams)img.getLayoutParams()).dimensionRatio = bookGenreUI.getSize() + ":1";
            img.setImageResource(R.mipmap.icon_chinese);
        }

        @Override
        public void onClick(View v) {
            CategoryActivity.start(v.getContext(), category);
        }
    }
}
