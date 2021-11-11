package com.ruitech.bookstudy.desktop.binder;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ruitech.bookstudy.R;
import com.ruitech.bookstudy.desktop.AlbumActivity;
import com.ruitech.bookstudy.desktop.PlayActivity;
import com.ruitech.bookstudy.desktop.bean.Album;
import com.ruitech.bookstudy.desktop.bean.Category;
import com.ruitech.bookstudy.utils.ImageHelper;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import me.drakeet.multitype.ItemViewBinder;

public class AlbumBinder extends ItemViewBinder<Album, AlbumBinder.ViewHolder> {

    private static final String TAG = "AlbumBinder";

    private Category category;
    public AlbumBinder(Category category) {
        this.category = category;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View v = inflater.inflate(R.layout.binder_album, parent, false);
        return new ViewHolder(v);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull Album item) {
        holder.bindData(item);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView img;
        private TextView title;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            title = itemView.findViewById(R.id.title);
            itemView.setOnClickListener(this);
        }

        private Album album;
        public void bindData(Album album) {
            android.util.Log.d(TAG, "bindData: " + itemView.isFocusable() + " ");
            this.album = album;
            ImageHelper.displayImage(img, new ImageHelper.Params.Builder().posters(album.posterList).build());
            title.setText(album.name);
        }

        @Override
        public void onClick(View v) {
            AlbumActivity.start(v.getContext(), category, album);
//            BookActivity.start(itemView.getContext(), book);
        }
    }
}
