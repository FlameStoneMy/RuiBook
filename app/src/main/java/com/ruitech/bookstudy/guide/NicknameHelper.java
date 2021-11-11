package com.ruitech.bookstudy.guide;

import android.util.Log;

import com.ruitech.bookstudy.bean.Gender;
import com.ruitech.bookstudy.guide.binder.GenderTitleBinder;
import com.ruitech.bookstudy.guide.binder.NicknameBinder;
import com.ruitech.bookstudy.guide.task.NicknameBaseQueryTask;
import com.ruitech.bookstudy.guide.task.NicknameBaseQueryWorker;
import com.ruitech.bookstudy.guide.task.NicknameGenderQueryTask;
import com.ruitech.bookstudy.guide.task.NicknameGenderQueryWorker;
import com.ruitech.bookstudy.guide.task.NicknameQueryTask;
import com.ruitech.bookstudy.guide.task.NicknameQueryWorker;
import com.ruitech.bookstudy.guide.task.TaskCallback;
import com.ruitech.bookstudy.uibean.NicknameUI;
import com.ruitech.bookstudy.utils.Executors;
import com.ruitech.bookstudy.utils.NetworkResponse;
import com.ruitech.bookstudy.widget.RuiDiffUtil;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class NicknameHelper extends AbsGuideHelper implements NicknameBinder.Callback, NicknameBaseQueryWorker.Callback, GenderTitleBinder.Callback {
    private static final String TAG = "NicknameHelper";

    public NicknameHelper(RecyclerView recyclerView, TaskCallback callback) {
        super(recyclerView, callback);
    }

    protected void load() {
//        new NicknameQueryTask(this).executeOnExecutor(Executors.network());
        new NicknameQueryWorker(this).execute();
    }

    @Override
    protected void initAdapter() {
        recyclerView.setItemAnimator(null);
        adapter.register(Gender.class, new GenderTitleBinder(this));
        adapter.register(NicknameUI.class, new NicknameBinder(this));
    }

    @Override
    protected GridLayoutManager.SpanSizeLookup getSpanSizeLookup() {
        return new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                Object obj = adapter.getItems().get(position);
                if (obj instanceof Gender) {
                    return 3;
                } else {
                    return 1;
                }
            }
        };
    }

    private int currNicknamePos = -1;
    private String currNickname;
    private Gender currGender;
    public String getCurrNickname() {
        return currNickname;
    }

    public Gender getCurrGender() {
        return currGender;
    }

    @Override
    public void onNicknameClick(int position, NicknameUI nicknameUI) {
        RuiDiffUtil.updateSelectPos(adapter, position, currNicknamePos);
        currNicknamePos = position;
        currNickname = nicknameUI.getValue();
    }

    private EnumMap<Gender, List<NicknameUI>> genderMap = new EnumMap<>(Gender.class);
    @Override
    public void onNicknameListQuery(NetworkResponse ret, EnumMap<Gender, List<String>> map) {
        callback.onLoaded(ret);
        if (ret != NetworkResponse.RESPONSE_OK) {
            return;
        }
        for (Map.Entry<Gender, List<String>> entry : map.entrySet()) {
            Gender gender = entry.getKey();
            if (gender == currGender) {
                // need recalculate.
                currNicknamePos = -1;
                currNickname = null;
                currGender = null;
            }
            genderMap.put(gender, NicknameUI.convert(entry.getValue()));
        }
        List list = new ArrayList();
        for (Map.Entry<Gender, List<NicknameUI>> entry : genderMap.entrySet()) {
            Gender gender = entry.getKey();
            list.add(gender);

            List<NicknameUI> nicknameUIList = entry.getValue();
            // ensure a nickname.
            if (currNicknamePos == -1) {
                nicknameUIList.get(0).setSelected(true);
                currNicknamePos = list.size();
                currNickname = ((NicknameUI) nicknameUIList.get(currNicknamePos)).getValue();
                currGender = gender;
            }
            list.addAll(nicknameUIList);
        }
        Log.d(TAG, "onNicknameListQuery: " + list);
        RuiDiffUtil.onNewData(adapter, list);
    }

    @Override
    public void onChangeClicked(Gender gender) {
//        new NicknameGenderQueryTask(gender, this).executeOnExecutor(Executors.network());
        new NicknameGenderQueryWorker(gender, this).execute();
    }

    @Override
    public void onLoading() {
        callback.onLoading();
    }
}
