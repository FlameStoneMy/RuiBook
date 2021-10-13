package com.ruitech.bookstudy.homepage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import me.drakeet.multitype.MultiTypeAdapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.ruitech.bookstudy.R;
import com.ruitech.bookstudy.RuiPreferenceUtil;
import com.ruitech.bookstudy.bean.Grade;
import com.ruitech.bookstudy.bean.Subject;
import com.ruitech.bookstudy.homepage.binder.GradeCalendarHelper;
import com.ruitech.bookstudy.homepage.binder.SubjectTabBinder;
import com.ruitech.bookstudy.homepage.binder.SubjectTabTitleBinder;
import com.ruitech.bookstudy.homepage.binder.bean.GradeCalendarCard;
import com.ruitech.bookstudy.homepage.binder.bean.SubjectTab;
import com.ruitech.bookstudy.homepage.binder.bean.SubjectTabUI;
import com.ruitech.bookstudy.homepage.binder.bean.TabsCard;
import com.ruitech.bookstudy.utils.Executors;
import com.ruitech.bookstudy.utils.NetworkResponse;
import com.ruitech.bookstudy.utils.StatusBarUtil;
import com.ruitech.bookstudy.widget.DecorationFactory;
import com.ruitech.bookstudy.widget.RuiDiffUtil;
import com.ruitech.bookstudy.widget.panelhelper.GradeBottomPanelHelper;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements GradeCalendarHelper.Callback, GradeBottomPanelHelper.Callback, HomePageQueryTask.Callback, SubjectTabTitleBinder.Callback, ISubjectListProvider {

    private static final String TAG = "HomeActivity";

    public static final void start(Context context) {
        Intent intent = new Intent(context, HomeActivity.class);
        context.startActivity(intent);
    }

    private GradeCalendarHelper gradeCalendarHelper;
    private MultiTypeAdapter tabTitleAdapter;
    private MultiTypeAdapter bodyAdapter;

    private ViewPager2 bodyViewPager;

    private SubjectTabBinder subjectTabBinder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setColor(this, getResources().getColor(R.color._4b8afe));
        setContentView(R.layout.activity_home);

        Grade grade = RuiPreferenceUtil.getGrade();
        gradeCalendarHelper = new GradeCalendarHelper(findViewById(R.id.card_grade_calendar), this);

        RecyclerView tabTitleRecyclerView = findViewById(R.id.title_recycler_view);
        tabTitleRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        tabTitleRecyclerView.addItemDecoration(DecorationFactory.get8_0_8_0Space16_0_16_0());
        tabTitleAdapter = new MultiTypeAdapter();
        tabTitleAdapter.register(SubjectTabUI.class, new SubjectTabTitleBinder(this));
        tabTitleRecyclerView.setAdapter(tabTitleAdapter);

        gradeBottomPanelHelper = new GradeBottomPanelHelper(this, this);

        new HomePageQueryTask(grade, this).executeOnExecutor(Executors.network());

        bodyViewPager = findViewById(R.id.body_viewpager);
        bodyAdapter = new MultiTypeAdapter();
        subjectTabBinder = new SubjectTabBinder(grade, this, this);
        bodyAdapter.register(SubjectTab.class, subjectTabBinder);
        bodyViewPager.setAdapter(bodyAdapter);
        bodyViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateTabTitle(position);
            }
        });
    }

    private GradeBottomPanelHelper gradeBottomPanelHelper;

    @Override
    public void onGradeClick(Grade grade) {
        gradeBottomPanelHelper.bindData(grade);
        gradeBottomPanelHelper.showBottomPanel();
    }

    @Override
    public void onGradeSelected(Grade grade) {
        Log.d(TAG, "onGradeSelected: " + grade);
        new HomePageQueryTask(grade, this).executeOnExecutor(Executors.network());
    }

    private int selectedTabPos = -1;
    @Override
    public void onHomePageResult(NetworkResponse ret, GradeCalendarCard gradeCalendarCard, TabsCard<SubjectTab> tabsCard) {
        RuiPreferenceUtil.setGrade(gradeCalendarCard.grade);
        subjectTabBinder.setGrade(gradeCalendarCard.grade);
        gradeCalendarHelper.bindData(gradeCalendarCard);

        subjectList.clear();
        for (SubjectTab subjectTab : tabsCard.getTabList()) {
            subjectList.add(subjectTab.getSubject());
        }

        RuiDiffUtil.onNewData(bodyAdapter, tabsCard.getTabList());
        Pair<Integer, List<SubjectTabUI>> pair = SubjectTabUI.from(new ArrayList(tabsCard.getTabList()));
        selectedTabPos = pair.first;
        RuiDiffUtil.onNewData(tabTitleAdapter, pair.second);
    }

    @Override
    public void onTabTitleClicked(int position, Subject subject) {
        updateTabTitle(position);
        // update body
        bodyViewPager.setCurrentItem(position, true);
    }

    private void updateTabTitle(int position) {
        // update tab title
        RuiDiffUtil.updateSelectPos(tabTitleAdapter, position, selectedTabPos);
        selectedTabPos = position;
    }

    private List<Subject> subjectList = new ArrayList<>();
    @Override
    public List<Subject> getSubjectList() {
        return subjectList;
    }
}