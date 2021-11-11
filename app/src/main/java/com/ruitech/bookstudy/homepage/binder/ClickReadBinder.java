package com.ruitech.bookstudy.homepage.binder;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ruitech.bookstudy.BookSelectActivity;
import com.ruitech.bookstudy.R;
import com.ruitech.bookstudy.bean.Book;
import com.ruitech.bookstudy.bean.Grade;
import com.ruitech.bookstudy.bean.Subject;
import com.ruitech.bookstudy.binder.IUISame;
import com.ruitech.bookstudy.book.BookQueryTask;
import com.ruitech.bookstudy.bookselect.BookSelectEvent;
import com.ruitech.bookstudy.homepage.ISubjectListProvider;
import com.ruitech.bookstudy.homepage.binder.bean.ClickReadCard;
import com.ruitech.bookstudy.task.BookSelectQueryTask;
import com.ruitech.bookstudy.utils.ClickUtil;
import com.ruitech.bookstudy.utils.Executors;
import com.ruitech.bookstudy.utils.ListUtils;
import com.ruitech.bookstudy.utils.Util;
import com.ruitech.bookstudy.widget.BookView;

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

public class ClickReadBinder extends ItemViewBinder<ClickReadBinder.ClickReadCardWrapper, ClickReadBinder.ViewHolder> implements IUISame<ClickReadBinder.ClickReadCardWrapper> {

    private static final String TAG = "ClickReadBinder";

    public static class ClickReadCardWrapper {
        private ClickReadCard clickReadCard;
        private boolean loading;
        public ClickReadCardWrapper(ClickReadCard clickReadCard) {
            this.clickReadCard = clickReadCard;
        }

        public void setLoading(boolean loading) {
            this.loading = loading;
        }

        public boolean isLoading() {
            return loading;
        }

        public static List convert(List list) {
            if (!ListUtils.isEmpty(list)) {
                for (int i = 0; i < list.size(); i++) {
                    Object obj = list.get(i);
                    if (obj instanceof ClickReadCard) {
                        list.set(i, new ClickReadCardWrapper((ClickReadCard) obj));
                    }
                }
            }
            return list;
        }
    }

    private Grade grade;
    private Subject subject;
    private ISubjectListProvider iSubjectListProvider;
    public ClickReadBinder(ISubjectListProvider iSubjectListProvider) {
        this.iSubjectListProvider = iSubjectListProvider;
    }

    public void bindData(Grade grade, Subject subject) {
        this.grade = grade;
        this.subject = subject;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View v = inflater.inflate(R.layout.card_click_read, parent, false);
        return new ViewHolder(v);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull ClickReadCardWrapper item) {
        holder.bindData(item);
    }

    @Override
    public boolean areUISame(@NonNull ClickReadCardWrapper oldItem, @NonNull ClickReadCardWrapper newItem) {
        return oldItem.equals(newItem);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, BookSelectQueryTask.Callback, LifecycleEventObserver {
        private Context context;

        private BookView bookView;
        private TextView changeTv;
        private TextView descTv;
        private TextView actionTv;
        private Book book;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            bookView = itemView.findViewById(R.id.img);
            changeTv = itemView.findViewById(R.id.change);
            changeTv.setOnClickListener(this);
            descTv = itemView.findViewById(R.id.desc);
            actionTv = itemView.findViewById(R.id.action_tv);
            actionTv.setOnClickListener(this);

            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this);
            }
        }

        public void bindData(ClickReadCardWrapper clickReadCardWrapper) {
            bookView.setLoading(clickReadCardWrapper.isLoading());
            new BookSelectQueryTask(subject, grade, this).executeOnExecutor(Executors.io());
        }

        @Override
        public void onClick(View v) {
            if (Util.filterClick()) {
                return;
            }
            switch (v.getId()) {
                case R.id.change:
                    startBookSelectActivity();
                    break;
                case R.id.action_tv:
                    if (ClickUtil.filter()) {
                        return;
                    }

                    if (book == null) {
                        startBookSelectActivity();
                    } else {
                        new BookQueryTask(book, BookQueryTask.FromType.CLICK_READ_CARD).executeOnExecutor(Executors.network());
                    }
                    break;
            }
        }

        private void startBookSelectActivity() {
            List<Subject> list = iSubjectListProvider.getSubjectList();
            BookSelectActivity.start(context, grade, list, list.indexOf(subject));
        }

        @Override
        public void onSelectQuery(Subject subject, Grade grade, Book book) {
            Log.d(TAG, "onSelectQuery: " + subject + " " + grade + " " + book);
            onBookSelected(subject, grade, book);
        }

        private void onBookSelected(Subject subject, Grade grade, Book book) {
            Log.d(TAG, "onBookSelected: " + subject + " " + grade + " " + book);
            if (ClickReadBinder.this.subject == subject &&
                    ClickReadBinder.this.grade == grade) {
                this.book = book;
                if (book == null) {
                    descTv.setText(R.string.get_click_read_book);
                    actionTv.setText(R.string.add_book);
                    changeTv.setVisibility(View.INVISIBLE);
                } else {
                    descTv.setText(book.getDesc(context));
                    actionTv.setText(R.string.click_read_start);
                    changeTv.setVisibility(View.VISIBLE);
                }
                bookView.bindData(book);
            }
        }

        @Override
        public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
            Log.d(TAG, "onStateChanged: " + event);
            switch (event) {
                case ON_DESTROY:
                    if (EventBus.getDefault().isRegistered(this)) {
                        EventBus.getDefault().unregister(this);
                    }
                    break;
            }
        }

        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onEvent(BookSelectEvent event) {
            onBookSelected(event.subject, event.grade, event.book);
        }
    }
}
