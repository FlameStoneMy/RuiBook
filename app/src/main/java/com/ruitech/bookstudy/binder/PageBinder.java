package com.ruitech.bookstudy.binder;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ruitech.bookstudy.R;
import com.ruitech.bookstudy.bean.LocationGroup;
import com.ruitech.bookstudy.bean.Page;
import com.ruitech.bookstudy.book.BookUtil;
import com.ruitech.bookstudy.book.CoordinateView;
import com.ruitech.bookstudy.book.PageImpl;
import com.ruitech.bookstudy.settings.ShowBordersEvent;
import com.ruitech.bookstudy.utils.ListUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;
import me.drakeet.multitype.ItemViewBinder;

public class PageBinder extends ItemViewBinder<Page, PageBinder.ViewHolder> implements IUISame<Page> {

    private static final String TAG = "PageBinder";

    private CoordinateView.Callback callback;
    private PageImpl.Callback pageImplCallback;
    public PageBinder(CoordinateView.Callback callback, PageImpl.Callback pageImplCallback) {
        this.callback = callback;
        this.pageImplCallback = pageImplCallback;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View v = inflater.inflate(R.layout.binder_page, parent, false);
        return new ViewHolder(v);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull Page item) {
        holder.bindData(item);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull Page item, List<Object> payloads) {
        holder.bindData(item, payloads);
    }

    @Override
    public boolean areUISame(@NonNull Page oldItem, @NonNull Page newItem) {
        Log.d(TAG, "areUISame: " + oldItem.getPageNum() + " " + newItem.getPageNum() + " " + oldItem.getLocationGroup() + " " + newItem.getLocationGroup());
        return oldItem.getPageNum() == newItem.getPageNum() &&
                (oldItem.getLocationGroup() == newItem.getLocationGroup());
    }

    @Override
    public Object getChangePayload(@NonNull Page oldItem, @NonNull Page newItem) {
        Log.d(TAG, "getChangePayload: ");
        return newItem.getLocationGroup();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements LifecycleEventObserver {//implements PageImpl.Callback {
        private CoordinateView coordinateView;
        private PageImpl pageImpl = new PageImpl();
        private Page page;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            coordinateView = itemView.findViewById(R.id.coordinate_view);
            coordinateView.setCallback(callback);

            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this);
            }
        }

        public void bindData(Page page) {
            Log.d(TAG, "bindData: " + page.getPageNum());
            this.page = page;
            coordinateView.bindData(BookUtil.getPictureUriStr(page.getBookId(), page.getBookGenuineId(), page.getPageNum()));

            pageImpl.loadData(page.getBookId(), page.getBookGenuineId(), page.getPageNum(), pageImplCallback);
        }

        public void bindData(Page page, List<Object> payloads)  {
            Log.d(TAG, "bindData: " + page.getPageNum() + " " + payloads);
            if (ListUtils.isEmpty(payloads)) {
                bindData(page);
            } else for (Object obj : payloads) {
                if (obj instanceof LocationGroup) {
                    if (page.getLocationGroup() != null) {
                        coordinateView.bindData(page.getPageNum(), page.getLocationGroup());
                    }
                }
            }
        }

        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onEvent(ShowBordersEvent showBordersEvent) {
            coordinateView.invalidate();
        }

        @Override
        public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
            if (event == Lifecycle.Event.ON_DESTROY) {
                if (EventBus.getDefault().isRegistered(this)) {
                    EventBus.getDefault().unregister(this);
                }
            }
        }
    }

}
