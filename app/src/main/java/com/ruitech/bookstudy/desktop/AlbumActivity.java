package com.ruitech.bookstudy.desktop;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ruitech.bookstudy.BaseActivity;
import com.ruitech.bookstudy.R;
import com.ruitech.bookstudy.RuiPreferenceUtil;
import com.ruitech.bookstudy.desktop.bean.Album;
import com.ruitech.bookstudy.desktop.bean.Category;
import com.ruitech.bookstudy.desktop.bean.Video;
import com.ruitech.bookstudy.desktop.binder.VideoBinder;
import com.ruitech.bookstudy.desktop.task.AlbumLoadTask;
import com.ruitech.bookstudy.guide.TaskActivity;
import com.ruitech.bookstudy.utils.Executors;
import com.ruitech.bookstudy.utils.ImageHelper;
import com.ruitech.bookstudy.utils.NetworkResponse;
import com.ruitech.bookstudy.utils.StatusBarUtil;
import com.ruitech.bookstudy.widget.DecorationFactory;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import me.drakeet.multitype.MultiTypeAdapter;

public class AlbumActivity extends TaskActivity implements AlbumLoadTask.Callback, View.OnClickListener {
    private static final String TAG = "AlbumActivity";

    public static void start(Context context, Category category, Album album) {
        Intent intent = new Intent(context, AlbumActivity.class);
        intent.putExtra(EXTRA_CATEGORY, category);
        intent.putExtra(EXTRA_ALBUM, album);
        context.startActivity(intent);
    }
    private static final String EXTRA_ALBUM = "album";
    private static final String EXTRA_CATEGORY = "category";

    private MultiTypeAdapter adapter;
    private Album album;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        StatusBarUtil.fullScreen(this);
//        setContentView(R.layout.activity_album);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        adapter = new MultiTypeAdapter();

        adapter.register(Video.class, new VideoBinder());

        Category category = (Category) getIntent().getSerializableExtra(EXTRA_CATEGORY);
        album = (Album) getIntent().getSerializableExtra(EXTRA_ALBUM);
        ((TextView) coreLayout.findViewById(R.id.title)).setText(category.name);

        ImageView bookImg = findViewById(R.id.book_img);
        ImageHelper.displayImage(bookImg, new ImageHelper.Params.Builder().posters(album.posterList).build());

        ((TextView) findViewById(R.id.book_title)).setText(album.name);

//        ((TextView) findViewById(R.id.book_desc)).setText(getResources().getString(RuiPreferenceUtil.getGrade().resId));

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2, RecyclerView.VERTICAL, false));
        recyclerView.addItemDecoration(DecorationFactory.get9_8_9_8Space0_0_0_8());

        new AlbumLoadTask(album, this).executeOnExecutor(Executors.network());

        findViewById(R.id.back).setOnClickListener(this);
    }

    @Override
    protected int getCoreLayoutId() {
        return R.layout.activity_album;
    }

    @Override
    protected void reload() {
        new AlbumLoadTask(album, this).executeOnExecutor(Executors.network());
    }

    @Override
    public void onAlbumLoad(NetworkResponse response, Album album) {
        android.util.Log.d(TAG, "onAlbumLoad: " + album.videoList.size());
        onLoaded(response);
        if (response != NetworkResponse.RESPONSE_OK) {
            return;
        }

        adapter.setItems(album.videoList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                onBackPressed();
                break;
            default:
                super.onClick(v);
                break;
        }
    }
}
