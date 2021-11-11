package com.ruitech.bookstudy.desktop;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ruitech.bookstudy.BaseActivity;
import com.ruitech.bookstudy.R;
import com.ruitech.bookstudy.decoration.SpacesItemDecoration;
import com.ruitech.bookstudy.desktop.bean.Album;
import com.ruitech.bookstudy.desktop.bean.Category;
import com.ruitech.bookstudy.desktop.binder.AlbumBinder;
import com.ruitech.bookstudy.desktop.task.CategoryLoadTask;
import com.ruitech.bookstudy.guide.TaskActivity;
import com.ruitech.bookstudy.utils.Executors;
import com.ruitech.bookstudy.utils.NetworkResponse;
import com.ruitech.bookstudy.utils.StatusBarUtil;
import com.ruitech.bookstudy.utils.UIHelper;
import com.ruitech.bookstudy.widget.DecorationFactory;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import me.drakeet.multitype.MultiTypeAdapter;

public class CategoryActivity extends TaskActivity implements CategoryLoadTask.Callback, View.OnClickListener {

    private static final String TAG = "CategoryActivity";

    public static void start(Context context, Category category) {
        Intent intent = new Intent(context, CategoryActivity.class);
        intent.putExtra(EXTRA_CATEGORY, category);
        context.startActivity(intent);
    }
    private static final String EXTRA_CATEGORY = "category";

    private MultiTypeAdapter adapter;
    private Category category;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        StatusBarUtil.fullScreen(this);
//        Toast.makeText(this, "Page 2 start", Toast.LENGTH_SHORT).show();
//        setContentView(R.layout.activity_category);

        category = (Category) getIntent().getSerializableExtra(EXTRA_CATEGORY);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        adapter = new MultiTypeAdapter();

        adapter.register(Album.class, new AlbumBinder(category));

//        android.util.Log.d(TAG, "onCreate: " + category.name);
        ((TextView) coreLayout.findViewById(R.id.title)).setText(category.name);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3, RecyclerView.VERTICAL, false));
        recyclerView.addItemDecoration(new SpacesItemDecoration(UIHelper.dp2px(18), UIHelper.dp2px(18)));

        new CategoryLoadTask(category, this).executeOnExecutor(Executors.network());

        findViewById(R.id.back).setOnClickListener(this);
    }

    @Override
    protected int getCoreLayoutId() {
        return R.layout.activity_category;
    }

    @Override
    protected void reload() {
        new CategoryLoadTask(category, this).executeOnExecutor(Executors.network());
    }

    @Override
    public void onCategoryLoad(NetworkResponse response, Category category) {
        onLoaded(response);
        if (response != NetworkResponse.RESPONSE_OK) {
            return;
        }
        adapter.setItems(category.albumList);
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
