package com.ruitech.bookstudy;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ruitech.bookstudy.bean.Page;
import com.ruitech.bookstudy.binder.IUISame;
import com.ruitech.bookstudy.widget.panelhelper.MenuBottomPanelHelper.Callback;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import me.drakeet.multitype.ItemViewBinder;

public class BodyTypeBinder extends ItemViewBinder<BodyType, BodyTypeBinder.ViewHolder> {

    private static final String TAG = "BodyTypeBinder";

    public BodyTypeBinder() {
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View v = inflater.inflate(R.layout.container_read, parent, false);
        return new ViewHolder(v);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull BodyType item) {
        holder.bindData(item);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bindData(BodyType bodyType) {
            android.util.Log.d(TAG, "bindData: ");
        }
    }
}
