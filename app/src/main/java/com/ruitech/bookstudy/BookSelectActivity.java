package com.ruitech.bookstudy;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;


import com.ruitech.bookstudy.bean.Book;
import com.ruitech.bookstudy.bean.Grade;
import com.ruitech.bookstudy.bean.LocationGroup;
import com.ruitech.bookstudy.bean.Subject;
import com.ruitech.bookstudy.binder.BookBinder;
import com.ruitech.bookstudy.book.BookListQueryTask;
import com.ruitech.bookstudy.book.BookQueryTask;
import com.ruitech.bookstudy.book.BookUtil;
import com.ruitech.bookstudy.book.CoordinateView;
import com.ruitech.bookstudy.book.PageImpl;
import com.ruitech.bookstudy.bookselect.BookSelectBinder;
import com.ruitech.bookstudy.homepage.HomeActivity;
import com.ruitech.bookstudy.perm.IPermRequester;
import com.ruitech.bookstudy.uibean.BookUI;
import com.ruitech.bookstudy.uibean.PageUI;
import com.ruitech.bookstudy.utils.Executors;
import com.ruitech.bookstudy.utils.ListUtils;
import com.ruitech.bookstudy.utils.StatusBarUtil;
import com.ruitech.bookstudy.widget.BookView;
import com.ruitech.bookstudy.widget.DecorationFactory;
import com.ruitech.bookstudy.widget.TabPageIndicator;
import com.ruitech.bookstudy.widget.TabPagerViewPagerObserver;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import me.drakeet.multitype.MultiTypeAdapter;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;


public class BookSelectActivity extends BaseActivity implements IPermRequester, View.OnClickListener {

    private static final String TAG = "BookSelectActivity";

    public static void start(Context context, Grade grade, List<Subject> subjectList, int subjectPos) {
        Intent intent = new Intent(context, BookSelectActivity.class);
        intent.putExtra(EXTRA_GRADE, grade);
        intent.putExtra(EXTRA_SUBJECT_LIST, ListUtils.convert2Serializable(subjectList));
        intent.putExtra(EXTRA_SUBJECT_POSITION, subjectPos);
        context.startActivity(intent);
    }

    private static final int REQUEST_PERM_CODE = 1;
    private static final String EXTRA_GRADE = "grade";
    private static final String EXTRA_SUBJECT_LIST = "subject_list";
    private static final String EXTRA_SUBJECT_POSITION = "subject_position";

    protected MultiTypeAdapter adapter;
    private Grade grade;
    private List<Subject> subjectList;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        StatusBarUtil.setColor(this, getResources().getColor(R.color._4b8afe));
        findViewById(R.id.back_img).setOnClickListener(this);
        grade = (Grade) getIntent().getSerializableExtra(EXTRA_GRADE);
        subjectList = (List<Subject>) getIntent().getSerializableExtra(EXTRA_SUBJECT_LIST);
        int subjectPos = getIntent().getIntExtra(EXTRA_SUBJECT_POSITION, 0);
        Log.d(TAG, "onCreate: " + grade + " " + subjectList.get(subjectPos));
        findViewById(R.id.body_container).setBackground
                (new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                        new int[] {getResources().getColor(R.color._4b8afe), getResources().getColor(R.color._93deff)}));
        TabPageIndicator indicator = findViewById(R.id.indicator);
        ViewPager2 subjectViewPager = findViewById(R.id.body_viewpager);
        MultiTypeAdapter adapter = new MultiTypeAdapter().lifecycleOwner(this);
        adapter.register(Subject.class, new BookSelectBinder(grade, this));
        adapter.setItems(subjectList);
        subjectViewPager.setAdapter(adapter);

        indicator.setViewPager(subjectViewPager, new TabPagerViewPagerObserver() {

        }, subjectPos);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_book_selection;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERM_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
                onWriteStoragePermGranted();
            }
        }
    }

    private void onWriteStoragePermGranted() {
        if (pendingRunnable != null) {
            handler.post(pendingRunnable);
        }
    }

    private Handler handler = new Handler();

    private Runnable pendingRunnable;
    @Override
    public void onPermRequest(String permission, Runnable runnable) {
        Log.d(TAG, "onPermRequest: " + permission + " " + runnable);
        pendingRunnable = runnable;
        ActivityCompat.requestPermissions(this, new String[] {permission}, REQUEST_PERM_CODE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_img:
                finish();
                break;
        }
    }
}
