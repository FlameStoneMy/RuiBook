package com.ruitech.bookstudy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.ruitech.bookstudy.bean.Book;
import com.ruitech.bookstudy.settings.SettingsDialog;
import com.ruitech.bookstudy.utils.ClickUtil;
import com.ruitech.bookstudy.utils.StatusBarUtil;
import com.ruitech.bookstudy.widget.IBackPressConsumer;
import com.ruitech.bookstudy.widget.TabPageIndicator;
import com.ruitech.bookstudy.widget.TabPagerViewPagerObserver;
import com.ruitech.bookstudy.widget.ViewPager2Helper;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import me.drakeet.multitype.MultiTypeAdapter;

public class BookActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "BookActivity";

    private Book book;

    private static final String EXTRA_BOOK = "BOOK";

    public static final void startBookActivity(Context context, Book book) {
        Log.d(TAG, "startBookActivity");
        new Exception().printStackTrace();
        Intent intent = new Intent(context, BookActivity.class);
        intent.putExtra(EXTRA_BOOK, book);
        context.startActivity(intent);
    }

    private ViewPager2 bodyViewPager;
    protected MultiTypeAdapter adapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        StatusBarUtil.fullScreen(this);
//        StatusBarUtil.setColor(this, getResources().getColor(R.color.white));
//        setContentView(R.layout.activity_book);
        book = (Book) getIntent().getSerializableExtra(EXTRA_BOOK);

        findViewById(R.id.back_img).setOnClickListener(this);
        findViewById(R.id.setting_img).setOnClickListener(this);

        TabPageIndicator indicator = findViewById(R.id.indicator);
        indicator.setIndicatorColorResource(R.color._4989ff);
        bodyViewPager = findViewById(R.id.body_viewpager);
        MultiTypeAdapter bodyAdapter = new MultiTypeAdapter().lifecycleOwner(this);
        bodyAdapter.register(BodyType.class)
                .to(new ClickReadBodyTypeBinder(book, this), new BodyTypeBinder())
                .withClassLinker(bodyType -> {
                    if (bodyType == BodyType.CLICK_READ) {
                        return ClickReadBodyTypeBinder.class;
                    } else {
                        return BodyTypeBinder.class;
                    }
                });
        bodyAdapter.setItems(List.of(BodyType.CLICK_READ
//                , BodyType.TEST
        ));
        bodyViewPager.setAdapter(bodyAdapter);
        indicator.setViewPager(bodyViewPager, new TabPagerViewPagerObserver() {

        }, 0);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_book;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_img:
                finish();
                break;
            case R.id.setting_img:
                if (ClickUtil.filter()) {
                    return;
                }
                new SettingsDialog(this).show();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        RecyclerView.ViewHolder vh = ViewPager2Helper.getCurrentViewHolder(bodyViewPager);
        if (vh != null && vh instanceof IBackPressConsumer && ((IBackPressConsumer) vh).onBackPressed()) {
            // consumed..
        } else super.onBackPressed();
    }
}
