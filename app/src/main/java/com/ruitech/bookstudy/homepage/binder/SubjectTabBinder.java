package com.ruitech.bookstudy.homepage.binder;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ruitech.bookstudy.App;
import com.ruitech.bookstudy.BookActivity;
import com.ruitech.bookstudy.R;
import com.ruitech.bookstudy.bean.Book;
import com.ruitech.bookstudy.bean.Grade;
import com.ruitech.bookstudy.bean.Subject;
import com.ruitech.bookstudy.binder.IUISame;
import com.ruitech.bookstudy.book.BookQueryEvent;
import com.ruitech.bookstudy.book.BookQueryTask;
import com.ruitech.bookstudy.homepage.ISubjectListProvider;
import com.ruitech.bookstudy.homepage.SubjectTabQueryTask;
import com.ruitech.bookstudy.homepage.binder.bean.ClickReadCard;
import com.ruitech.bookstudy.homepage.binder.bean.SubjectTab;
import com.ruitech.bookstudy.utils.Executors;
import com.ruitech.bookstudy.utils.ListUtils;
import com.ruitech.bookstudy.utils.NetworkResponse;
import com.ruitech.bookstudy.widget.DecorationFactory;
import com.ruitech.bookstudy.widget.RuiDiffUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import me.drakeet.multitype.ItemViewBinder;
import me.drakeet.multitype.MultiTypeAdapter;

public class SubjectTabBinder extends ItemViewBinder<SubjectTab, SubjectTabBinder.ViewHolder> implements IUISame<SubjectTab> {

    private static final String TAG = "SubjectTabBinder";

    private Grade grade;
    private ISubjectListProvider iSubjectListProvider;
    private LifecycleOwner lifecycleOwner;
    public SubjectTabBinder(Grade grade, ISubjectListProvider iSubjectListProvider, LifecycleOwner lifecycleOwner) {
        this.grade = grade;
        this.iSubjectListProvider = iSubjectListProvider;
        this.lifecycleOwner = lifecycleOwner;
    }

    public void setGrade(Grade grade) {
        this.grade = grade;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View v = inflater.inflate(R.layout.binder_subject_tab, parent, false);
        return new ViewHolder(v);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull SubjectTab item) {
        holder.bindData(item);
    }

    @Override
    public boolean areUISame(@NonNull SubjectTab oldItem, @NonNull SubjectTab newItem) {
        return oldItem.equals(newItem);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements SubjectTabQueryTask.Callback, LifecycleEventObserver {
        private Context context;

        private RecyclerView recyclerView;
        private MultiTypeAdapter adapter;
        private ClickReadBinder clickReadBinder;
        private SubjectTab subjectTab;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            recyclerView = itemView.findViewById(R.id.recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.addItemDecoration(DecorationFactory.get16_0_16_16Space16_16_16_16());
            adapter = new MultiTypeAdapter().lifecycleOwner(lifecycleOwner);
            clickReadBinder = new ClickReadBinder(iSubjectListProvider);
            adapter.register(ClickReadBinder.ClickReadCardWrapper.class, clickReadBinder);
            recyclerView.setAdapter(adapter);

            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this);
            }
        }

        public void bindData(SubjectTab subjectTab) {
            this.subjectTab = subjectTab;
            clickReadBinder.bindData(grade, subjectTab.getSubject());
            if (ListUtils.isEmpty(subjectTab.getResourceList())) {
                new SubjectTabQueryTask(subjectTab, this).executeOnExecutor(Executors.io());
            } else {
                RuiDiffUtil.onNewData(adapter, ClickReadBinder.ClickReadCardWrapper.convert(subjectTab.getResourceList()));
            }
        }

        @Override
        public void onSubjectTabQueryResult(NetworkResponse ret, SubjectTab subjectTab) {
            switch (ret) {
                case RESPONSE_OK:
                    this.subjectTab.merge(false, subjectTab);
                    RuiDiffUtil.onNewData(adapter, ClickReadBinder.ClickReadCardWrapper.convert(subjectTab.getResourceList()));
                    break;
            }
        }

        private void updateClickReadCard(boolean loading) {
            List list = adapter.getItems();
            for (int i = 0; i < adapter.getItems().size(); i++) {
                Object obj = list.get(i);
                if (obj instanceof ClickReadBinder.ClickReadCardWrapper) {
                    ((ClickReadBinder.ClickReadCardWrapper) obj).setLoading(loading);
                    adapter.notifyItemChanged(i);
                }
            }
        }

        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onEvent(BookQueryEvent bookQueryEvent) {
            android.util.Log.d(TAG, "onEvent: " + bookQueryEvent.fromType + " " + bookQueryEvent.book + " " + bookQueryEvent.state);
            android.util.Log.d(TAG, "onEvent rui: " + needHandle(bookQueryEvent) + " " + recyclerView.isAttachedToWindow());
            if (needHandle(bookQueryEvent)) {
                switch (bookQueryEvent.state) {
                    case START:
                        updateClickReadCard(true);
                        Toast.makeText(App.applicationContext(), R.string.downloading, Toast.LENGTH_SHORT).show();
                        break;
                    case SUCC:
                    case FAIL:
                        updateClickReadCard(false);
                        if (bookQueryEvent.state == BookQueryEvent.State.SUCC) {
                            BookActivity.startBookActivity(context, bookQueryEvent.book);
                        }
                        break;
                }
            }
        }

        private boolean needHandle(BookQueryEvent event) {
            return event.fromType == BookQueryTask.FromType.CLICK_READ_CARD &&
                    event.book.getGrade() == grade &&
                    event.book.getSubject() == subjectTab.getSubject();
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
    }
}
