package com.ruitech.bookstudy.desktop;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.ruitech.bookstudy.R;
import com.ruitech.bookstudy.desktop.bean.Album;
import com.ruitech.bookstudy.desktop.bean.Video;
import com.ruitech.bookstudy.desktop.binder.VideoBinder;
import com.ruitech.bookstudy.desktop.task.AlbumLoadTask;
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

public class AlbumActivity extends AppCompatActivity implements AlbumLoadTask.Callback {
    private static final String TAG = "BookGenreActivity";

    public static void start(Context context, Album album) {
        Intent intent = new Intent(context, AlbumActivity.class);
        intent.putExtra(EXTRA_ALBUM, album);
        context.startActivity(intent);
    }
    private static final String EXTRA_ALBUM = "album";

    private MultiTypeAdapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.fullScreen(this);
        setContentView(R.layout.activity_album);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        adapter = new MultiTypeAdapter();

        adapter.register(Video.class, new VideoBinder());

        Album album = (Album) getIntent().getSerializableExtra(EXTRA_ALBUM);
        ((TextView)findViewById(R.id.title)).setText(album.name);

        ImageView bookImg = findViewById(R.id.book_img);
        ImageHelper.displayImage(bookImg, new ImageHelper.Params.Builder().posters(album.posterList).build());

        ((TextView) findViewById(R.id.book_title)).setText(album.name);
//        ((TextView) findViewById(R.id.book_desc)).setText(book.desc);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2, RecyclerView.VERTICAL, false));
        recyclerView.addItemDecoration(DecorationFactory.get9_8_9_8Space0_0_0_8());


        new AlbumLoadTask(album, this).executeOnExecutor(Executors.network());
    }

    @Override
    public void onAlbumLoad(NetworkResponse response, Album album) {
        android.util.Log.d(TAG, "onAlbumLoad: " + album.videoList.size());
        adapter.setItems(album.videoList);
        adapter.notifyDataSetChanged();
    }
}
