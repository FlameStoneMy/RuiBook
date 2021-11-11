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

            Context context = itemView.getContext();
            int[] colorArr = new int[] {
                    context.getResources().getColor(R.color._2cb1fd),
                    context.getResources().getColor(R.color._4175F0)};
            GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colorArr);
            gradientDrawable.setCornerRadius(UIHelper.dp2px(27));
            itemView.setBackground(gradientDrawable);
            title = itemView.findViewById(R.id.title);
            img = itemView.findViewById(R.id.img);
            itemView.setOnClickListener(this);

//            ViewGroup.MarginLayoutParams itemParams = (ViewGroup.MarginLayoutParams) itemView.getLayoutParams();
//            itemParams.leftMargin = UIHelper.dp2px(20);
//            itemParams.rightMargin = UIHelper.dp2px(20);
//            itemParams.topMargin = UIHelper.dp2px(20);
//            itemParams.bottomMargin = UIHelper.dp2px(20);
        }

        private Category category;
        public void bindData(Category category) {
            android.util.Log.d(TAG, "bindData: " + itemView.isFocusable() + " " + category.name);
            this.category = category;
            title.setText(category.name);

//            ((ConstraintLayout.LayoutParams)img.getLayoutParams()).dimensionRatio = bookGenreUI.getSize() + ":1";

            img.setImageResource(getIcon(getAdapterPosition()));
        }

        @Override
        public void onClick(View v) {
            CategoryActivity.start(v.getContext(), category);
        }
    }

    private static int getIcon(int position) {
        return iconArr[position % iconArr.length];
    }

    private static int[] iconArr = new int[] {
            R.mipmap.icon1,
            R.mipmap.icon2,
            R.mipmap.icon3,
            R.mipmap.icon4,
            R.mipmap.icon5,
            R.mipmap.icon6
    };
}
