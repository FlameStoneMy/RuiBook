package com.ruitech.bookstudy.desktop.binder;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.PaintDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ruitech.bookstudy.R;
import com.ruitech.bookstudy.desktop.CategoryActivity;
import com.ruitech.bookstudy.desktop.bean.Category;
import com.ruitech.bookstudy.homepage.HomeActivity;
import com.ruitech.bookstudy.utils.UIHelper;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import me.drakeet.multitype.ItemViewBinder;

public class Category0Binder extends ItemViewBinder<Category0Binder.ClickRead, Category0Binder.ViewHolder> {

    private static final String TAG = "Category0Binder";

    public static final class ClickRead {
    }

    public Category0Binder() {

    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        ConstraintLayout v = (ConstraintLayout) inflater.inflate(R.layout.binder_category, parent, false);
        return new ViewHolder(v);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull ClickRead item) {
        holder.bindData(item);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Context context;
        private TextView title;
        private ImageView img;
        public ViewHolder(@NonNull ConstraintLayout itemView) {
            super(itemView);
            context = itemView.getContext();
            int[] colorArr = new int[] {
                    context.getResources().getColor(R.color._2cb1fd),
                    context.getResources().getColor(R.color._4175F0)};
            GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colorArr);
            gradientDrawable.setCornerRadius(UIHelper.dp2px(27));
            itemView.setBackground(gradientDrawable);


            ImageView bgImg = new ImageView(context);
            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            bgImg.setBackground(context.getResources().getDrawable(R.mipmap.icon_click_read));
            itemView.addView(bgImg, 0, params);

            title = itemView.findViewById(R.id.title);
            img = itemView.findViewById(R.id.img);
            itemView.setOnClickListener(this);

//            ViewGroup.MarginLayoutParams itemParams = (ViewGroup.MarginLayoutParams) itemView.getLayoutParams();
//            itemParams.leftMargin = UIHelper.dp2px(20);
//            itemParams.rightMargin = UIHelper.dp2px(20);
//            itemParams.topMargin = UIHelper.dp2px(20);
//            itemParams.bottomMargin = UIHelper.dp2px(20);
        }

        private ClickRead clickRead;
        public void bindData(ClickRead clickRead) {
            android.util.Log.d(TAG, "bindData: " + itemView.isFocusable() + " ");
            this.clickRead = clickRead;
            title.setText("课本点读");

//            ((ConstraintLayout.LayoutParams)img.getLayoutParams()).dimensionRatio = bookGenreUI.getSize() + ":1";
//            img.setImageResource(R.mipmap.icon_chinese);
        }

        @Override
        public void onClick(View v) {
            HomeActivity.start(v.getContext());
        }
    }
}
