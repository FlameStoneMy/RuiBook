package com.ruitech.bookstudy.desktop;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ruitech.bookstudy.R;
import com.ruitech.bookstudy.desktop.bean.Album;
import com.ruitech.bookstudy.desktop.bean.Category;
import com.ruitech.bookstudy.desktop.binder.AlbumBinder;
import com.ruitech.bookstudy.desktop.task.CategoryLoadTask;
import com.ruitech.bookstudy.utils.Executors;
import com.ruitech.bookstudy.utils.NetworkResponse;
import com.ruitech.bookstudy.utils.StatusBarUtil;
import com.ruitech.bookstudy.widget.DecorationFactory;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import me.drakeet.multitype.MultiTypeAdapter;

public class CategoryActivity extends AppCompatActivity implements CategoryLoadTask.Callback, View.OnClickListener {

    private static final String TAG = "CategoryActivity";

    public static void start(Context context, Category category) {
        Intent intent = new Intent(context, CategoryActivity.class);
        intent.putExtra(EXTRA_CATEGORY, category);
        context.startActivity(intent);
    }
    private static final String EXTRA_CATEGORY = "category";

    private MultiTypeAdapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.fullScreen(this);
//        Toast.makeText(this, "Page 2 start", Toast.LENGTH_SHORT).show();
        setContentView(R.layout.activity_category);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        adapter = new MultiTypeAdapter();

        adapter.register(Album.class, new AlbumBinder());

        Category category = (Category) getIntent().getSerializableExtra(EXTRA_CATEGORY);

        ((TextView)findViewById(R.id.title)).setText(category.name);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3, RecyclerView.VERTICAL, false));
        recyclerView.addItemDecoration(DecorationFactory.get22_19_22_19Space0_0_0_19());

        new CategoryLoadTask(category, this).executeOnExecutor(Executors.network());

        findViewById(R.id.back).setOnClickListener(this);
    }

    @Override
    public void onCategoryLoad(NetworkResponse response, Category category) {
        adapter.setItems(category.albumList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        finish();
    }
}
