package com.ruitech.bookstudy.desktop.binder;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ruitech.bookstudy.R;
import com.ruitech.bookstudy.desktop.PlayActivity;
import com.ruitech.bookstudy.desktop.bean.Video;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import me.drakeet.multitype.ItemViewBinder;

public class VideoBinder extends ItemViewBinder<Video, VideoBinder.ViewHolder> {

    private static final String TAG = "VideoBinder";

    public VideoBinder() {

    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View v = inflater.inflate(R.layout.binder_video, parent, false);
        return new ViewHolder(v);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull Video item) {
        holder.bindData(item);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView idView;
        private TextView nameView;
        private Video video;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            idView = itemView.findViewById(R.id.id);
            nameView = itemView.findViewById(R.id.name);
            itemView.setOnClickListener(this);
        }

        public void bindData(Video video) {
            android.util.Log.d(TAG, "bindData: " + itemView.isFocusable() + " ");
            this.video = video;
            idView.setText("第" + (getLayoutPosition() + 1) + "节");
            nameView.setText(video.name);
        }

        @Override
        public void onClick(View v) {
            PlayActivity.start(itemView.getContext(), video);
        }

    }
}
