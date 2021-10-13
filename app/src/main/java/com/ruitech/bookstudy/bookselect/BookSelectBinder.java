package com.ruitech.bookstudy.bookselect;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.PaintDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ruitech.bookstudy.App;
import com.ruitech.bookstudy.BookActivity;
import com.ruitech.bookstudy.PermDialog;
import com.ruitech.bookstudy.R;
import com.ruitech.bookstudy.bean.Book;
import com.ruitech.bookstudy.bean.Grade;
import com.ruitech.bookstudy.bean.Subject;
import com.ruitech.bookstudy.binder.BookBinder;
import com.ruitech.bookstudy.binder.IUISame;
import com.ruitech.bookstudy.book.BookListQueryTask;
import com.ruitech.bookstudy.book.BookQueryEvent;
import com.ruitech.bookstudy.book.BookQueryTask;
import com.ruitech.bookstudy.perm.IPermRequester;
import com.ruitech.bookstudy.task.BookSelectQueryTask;
import com.ruitech.bookstudy.uibean.BookUI;
import com.ruitech.bookstudy.utils.Executors;
import com.ruitech.bookstudy.utils.ListUtils;
import com.ruitech.bookstudy.utils.LocalBroadcastUtil;
import com.ruitech.bookstudy.utils.UIHelper;
import com.ruitech.bookstudy.utils.Util;
import com.ruitech.bookstudy.widget.DecorationFactory;
import com.ruitech.bookstudy.widget.RuiDiffUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import me.drakeet.multitype.ItemViewBinder;
import me.drakeet.multitype.MultiTypeAdapter;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class BookSelectBinder extends ItemViewBinder<Subject, BookSelectBinder.ViewHolder> implements IUISame<Subject> {

    private static final String TAG = "BookSelectBinder";

    private Grade grade;
    private IPermRequester iPermRequester;
    public BookSelectBinder(Grade grade, IPermRequester iPermRequester) {
        this.grade = grade;
        this.iPermRequester = iPermRequester;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View v = inflater.inflate(R.layout.binder_book_select, parent, false);
        return new ViewHolder(v);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull Subject item) {
        holder.bindData(item);
    }

    @Override
    public boolean areUISame(@NonNull Subject oldItem, @NonNull Subject newItem) {
        return oldItem == newItem;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements BookListQueryTask.Callback, BookBinder.Callback, BookCurrentBinder.Callback, BookSelectQueryTask.Callback, LifecycleEventObserver, DialogInterface.OnDismissListener {
        private Context context;
        private RecyclerView recyclerView;
        protected MultiTypeAdapter adapter;
        private Subject subject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            recyclerView = itemView.findViewById(R.id.recycler_view);
            PaintDrawable paintDrawable = new PaintDrawable(context.getResources().getColor(R.color._ffffff));
            paintDrawable.setCornerRadius(UIHelper.dp2px(15));
            recyclerView.setBackground(paintDrawable);

            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this);
            }
        }

        public void bindData(Subject subject) {
            this.subject = subject;
            GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2, RecyclerView.VERTICAL, false);
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (position == 0 || position == 1) {
                        return 2;
                    } else {
                        return 1;
                    }

                }
            });
            recyclerView.setLayoutManager(gridLayoutManager);
            adapter = new MultiTypeAdapter();
            adapter.register(BookCurrentBinder.BookWrapper.class, new BookCurrentBinder(this));
            adapter.register(AddBookHintBinder.AddBookHint.class, new AddBookHintBinder());
            adapter.register(BookUI.class, new BookBinder(this));
            recyclerView.setAdapter(adapter);
            recyclerView.addItemDecoration(DecorationFactory.get14_0_14_0Space36_28_36_28());
            new BookSelectQueryTask(subject, grade, this).executeOnExecutor(Executors.io());
            new BookListQueryTask(subject, grade, this).executeOnExecutor(Executors.network());
        }

        private BookCurrentBinder.BookWrapper bookWrapper;
        private void ensureBookWrapper() {
            if (bookWrapper == null) {
                bookWrapper = new BookCurrentBinder.BookWrapper();
            }
            bookWrapper.setBook(selectedBook);
        }

        @Override
        public void onBookListQuery(List<Book> list) {
            List dataList = new ArrayList();
            ensureBookWrapper();
            dataList.add(bookWrapper);
            dataList.add(new AddBookHintBinder.AddBookHint());
            currPos = BookUI.from(list, dataList, selectedBook);
            RuiDiffUtil.onNewData(adapter, dataList);
        }

        private Book selectedBook;
        @Override
        public void onBookSelected(int pos, Book book) {
            updateCurrentBook(book);
            updateBook(pos);
            EventBus.getDefault().post(new BookSelectEvent(subject, grade, book));
        }

        private void updateCurrentBook(Book book) {
            selectedBook = book;
            ensureBookWrapper();
            adapter.notifyItemChanged(0);
        }

        private int currPos = -1;
        private void updateBook(int pos) {
            Log.d(TAG, "setOverviewPage: " + currPos + " " + pos);
            RuiDiffUtil.updateSelectPos(adapter, pos, currPos);
            currPos = pos;
        }

        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onEvent(BookQueryEvent bookQueryEvent) {
            Log.d(TAG, "onEvent: " + bookQueryEvent.state);
            if (needHandle(bookQueryEvent)) {
                switch (bookQueryEvent.state) {
                    case START:
                        bookWrapper.setLoading(true);
                        adapter.notifyItemChanged(0);
                        Toast.makeText(App.applicationContext(), R.string.downloading, Toast.LENGTH_SHORT).show();
                        break;
                    case SUCC:
                    case FAIL:
                        bookWrapper.setLoading(false);
                        adapter.notifyItemChanged(0);
                        if (bookQueryEvent.state == BookQueryEvent.State.SUCC) {
                            BookActivity.startBookActivity(context, bookQueryEvent.book);
                        }
                        break;
                }
            }
        }

        private boolean needHandle(BookQueryEvent event) {
            return event.fromType == BookQueryTask.FromType.CURRENT_BOOK_CARD &&
                    event.book.getGrade() == grade &&
                    event.book.getSubject() == subject;
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

        @Override
        public void onCurrentBookClicked(Book book) {
            if (Util.filterClick()) {
                return;
            }
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
                Dialog dialog = new PermDialog(context);
                dialog.setOnDismissListener(this);
                dialog.show();
            } else {
                new BookQueryTask(book, BookQueryTask.FromType.CURRENT_BOOK_CARD).executeOnExecutor(Executors.network());
            }

            // meng here
//            new BookQueryTask(book).executeOnExecutor(Executors.network());
        }

        @Override
        public void onSelectQuery(Subject subject, Grade grade, Book book) {
            Log.d(TAG, "onSelectQuery: " + subject + " " + grade + " " + book);
            if (book != null &&
                    subject == this.subject &&
                    grade == BookSelectBinder.this.grade) {
                updateCurrentBook(book);

                List list = adapter.getItems();
                if (!ListUtils.isEmpty(list)) {
                    for (int i = 2; i < list.size(); i++){
                        Object object = list.get(i);
                        if (object instanceof BookUI && ((BookUI) object).getValue().equals(book)) {
                            updateBook(i);
                        }
                    }
                }
            }
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            Log.d(TAG, "onDismiss: " + dialog);
            iPermRequester.onPermRequest(Manifest.permission.WRITE_EXTERNAL_STORAGE, new Runnable() {
                @Override
                public void run() {
                    new BookQueryTask(selectedBook, BookQueryTask.FromType.CURRENT_BOOK_CARD).executeOnExecutor(Executors.network());
                }
            });
        }
    }
}
