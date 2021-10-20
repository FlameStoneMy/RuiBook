package com.ruitech.bookstudy.desktop;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.ruitech.bookstudy.R;
import com.ruitech.bookstudy.RuiPreferenceUtil;
import com.ruitech.bookstudy.bean.Grade;
import com.ruitech.bookstudy.desktop.bean.Category;
import com.ruitech.bookstudy.desktop.binder.Category0Binder;
import com.ruitech.bookstudy.desktop.binder.CategoryBinder;
import com.ruitech.bookstudy.desktop.task.CategoryListLoadTask;
import com.ruitech.bookstudy.upgrade.UpgradeBean;
import com.ruitech.bookstudy.upgrade.UpgradeDialog;
import com.ruitech.bookstudy.upgrade.UpgradeQueryDialog;
import com.ruitech.bookstudy.upgrade.UpgradeQueryTask;
import com.ruitech.bookstudy.utils.Executors;
import com.ruitech.bookstudy.utils.NetworkResponse;
import com.ruitech.bookstudy.utils.StatusBarUtil;
import com.ruitech.bookstudy.widget.DecorationFactory;
import com.ruitech.bookstudy.widget.panelhelper.GradeBottomPanelHelper;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import me.drakeet.multitype.MultiTypeAdapter;

public class DesktopActivity extends AppCompatActivity implements CategoryListLoadTask.Callback,
        View.OnClickListener, GradeBottomPanelHelper.Callback,
        UpgradeQueryTask.Callback, UpgradeQueryDialog.Callback {

    private static final String TAG = "DesktopActivity";

    public static final void start(Context context) {
        Intent intent = new Intent(context, DesktopActivity.class);
        context.startActivity(intent);
    }

    private TextView gradeSelectTv;
    private Grade grade;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.fullScreen(this);
        setContentView(R.layout.activity_home2);

        recyclerView = findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 4, RecyclerView.VERTICAL, false);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == 0) {
                    return 2;
                }
                return 1;
            }
        });
        recyclerView.setLayoutManager(layoutManager);
        adapter.register(Category0Binder.ClickRead.class, new Category0Binder());
        adapter.register(Category.class, new CategoryBinder());
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(DecorationFactory.get30_30_30_30Space0_0_0_0());

        gradeSelectTv = findViewById(R.id.grade_select);
        gradeSelectTv.setOnClickListener(this);
        findViewById(R.id.settings).setOnClickListener(this);
        findViewById(R.id.eye_protect).setOnClickListener(this);

        grade = RuiPreferenceUtil.getGrade();
        gradeSelectTv.setText(grade.resId);

        gradeBottomPanelHelper = new GradeBottomPanelHelper(this, this);

        new CategoryListLoadTask(grade, this).executeOnExecutor(Executors.network());

        long curr = System.currentTimeMillis();
        android.util.Log.d(TAG, "curr: " + curr + " " + RuiPreferenceUtil.getLastUpgradeTs());
        if (curr - RuiPreferenceUtil.getLastUpgradeTs() > 24 * 60 * 60 * 1000L) {
            RuiPreferenceUtil.setLastUpgradeTs(curr);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    new UpgradeQueryTask(DesktopActivity.this).executeOnExecutor(Executors.network());
                }
            }, 3000L);
        }
    }

    private Handler handler = new Handler();

    @Override
    public void onCategoryListLoad(NetworkResponse response, List<Category> list) {
        android.util.Log.d(TAG, "onCategoryListLoad: " + list.size());
        List l = new ArrayList();
        l.add(new Category0Binder.ClickRead());
        l.addAll(list);
        adapter.setItems(l);
        adapter.notifyDataSetChanged();
    }

    private RecyclerView recyclerView;
    private MultiTypeAdapter adapter = new MultiTypeAdapter();

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.settings:
                startActivity(new Intent(Settings.ACTION_SETTINGS));
                break;
            case R.id.grade_select:
                gradeBottomPanelHelper.bindData(grade);
                gradeBottomPanelHelper.showBottomPanel();
                break;
            case R.id.eye_protect:
                boolean pending = false;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.canDrawOverlays(DesktopActivity.this)) {
                        pending = true;
                        DesktopActivity.this.startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + DesktopActivity.this.getPackageName())), 100);
                    }
                }

                if (!pending) {
                    toggleEyeProtectMode();
                }

                break;
        }
    }

    private void toggleEyeProtectMode() {
        if (!inEyeProtectMode) {
            getProtectDialog().show();
            Log.d(TAG, "show ProtectDialog");
        } else {
            getProtectDialog().dismiss();
            Log.d(TAG, "dismiss ProtectDialog");
        }
        inEyeProtectMode = !inEyeProtectMode;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 100) {
            android.util.Log.d(TAG, "onActivityResult: " + resultCode);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(DesktopActivity.this)) {
                    toggleEyeProtectMode();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean inEyeProtectMode;
    private View bodyView;
    private Dialog protectDialog;
    private Dialog getProtectDialog() {
        if (protectDialog == null) {
            protectDialog = new Dialog(this, R.style.dialog_translucent);
            protectDialog.setContentView(R.layout.dialog);

            WindowManager.LayoutParams lp = protectDialog.getWindow().getAttributes();
//            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            lp.flags =
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
//                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE; |
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
            if (Build.VERSION.SDK_INT >= 26){
                lp.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                lp.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
            }

//            bodyView = protectDialog.findViewById(R.id.body_container);
//            bodyView.setBackgroundColor(getFilterColor(80));
//            protectDialog.getWindow().setAttributes(lp);


            Window window = protectDialog.getWindow();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setNavigationBarColor(getResources().getColor(R.color.white));
            }
            StatusBarUtil.setTransparentBar(window);
        }
        return protectDialog;
    }

    public static int getFilterColor(int blueFilterPercent) {
        int realFilter = blueFilterPercent;
        if (realFilter < 10) {
            realFilter = 10;
        } else if (realFilter > 80) {
            realFilter = 80;
        }
        int a = (int) (realFilter / 80f * 180);
        int r = (int) (200 - (realFilter / 80f) * 190);
        int g = (int) (180 - (realFilter / 80f) * 170);
        int b = (int) (60 - realFilter / 80f * 60);

        Log.d(TAG, "getFilterColor: " + a + " " + r + " " + g + " " + b);
        return Color.argb(125, 0, 255, 255);
    }


    private GradeBottomPanelHelper gradeBottomPanelHelper;

    @Override
    public void onGradeSelected(Grade grade) {
        this.grade = grade;
        RuiPreferenceUtil.setGrade(grade);
        gradeSelectTv.setText(grade.resId);
        new CategoryListLoadTask(grade, this).executeOnExecutor(Executors.network());
    }

    @Override
    public void onNewVersion(UpgradeBean upgradeBean) {
        new UpgradeQueryDialog(this, upgradeBean, this).show();
    }

    @Override
    public void onVersionUpgrade(UpgradeBean upgradeBean) {
        new UpgradeDialog(this, upgradeBean).show();
    }
}
