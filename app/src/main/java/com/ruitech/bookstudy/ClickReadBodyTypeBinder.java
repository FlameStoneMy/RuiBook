package com.ruitech.bookstudy;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.PaintDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ruitech.bookstudy.bean.Book;
import com.ruitech.bookstudy.bean.LocationGroup;
import com.ruitech.bookstudy.bean.Page;
import com.ruitech.bookstudy.binder.PageBinder;
import com.ruitech.bookstudy.binder.PageOverviewBinder;
import com.ruitech.bookstudy.book.CoordinateView;
import com.ruitech.bookstudy.book.PageImpl;
import com.ruitech.bookstudy.book.PageSummaryTask;
import com.ruitech.bookstudy.book.PlayerHelper;
import com.ruitech.bookstudy.settings.ChangeSpeedEvent;
import com.ruitech.bookstudy.uibean.PageUI;
import com.ruitech.bookstudy.utils.Executors;
import com.ruitech.bookstudy.utils.ListUtils;
import com.ruitech.bookstudy.utils.UIHelper;
import com.ruitech.bookstudy.widget.DecorationFactory;
import com.ruitech.bookstudy.widget.IBackPressConsumer;
import com.ruitech.bookstudy.widget.PanelState;
import com.ruitech.bookstudy.widget.RuiDiffUtil;
import com.ruitech.bookstudy.widget.ViewPager2Helper;
import com.ruitech.bookstudy.widget.panelhelper.MenuBottomPanelHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import me.drakeet.multitype.ItemViewBinder;
import me.drakeet.multitype.MultiTypeAdapter;

import static com.ruitech.bookstudy.book.PlayerHelper.STATE_ERROR;
import static com.ruitech.bookstudy.book.PlayerHelper.STATE_INIT;
import static com.ruitech.bookstudy.book.PlayerHelper.STATE_PAUSED;
import static com.ruitech.bookstudy.book.PlayerHelper.STATE_STARTED;

public class ClickReadBodyTypeBinder extends ItemViewBinder<BodyType, ClickReadBodyTypeBinder.ViewHolder> {

    private static final String TAG = "ReadBodyTypeBinder";

    private Book book;
    private LifecycleOwner lifecycleOwner;
    public ClickReadBodyTypeBinder(Book book, LifecycleOwner lifecycleOwner) {
        this.book = book;
        this.lifecycleOwner = lifecycleOwner;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View v = inflater.inflate(R.layout.container_read, parent, false);
        return new ViewHolder(v, book, lifecycleOwner);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull BodyType item) {
        holder.bindData(item);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements PageSummaryTask.Callback, MenuBottomPanelHelper.Callback, View.OnClickListener, PageOverviewBinder.Callback, CoordinateView.Callback, PageImpl.Callback, LifecycleEventObserver, IBackPressConsumer, PlayerHelper.Callback {
        private Book book;
        private String bookId;
        private String bookGenuineId;

        private Context context;
        private ViewPager2 pagePager;
        private RecyclerView overviewView;
        private MultiTypeAdapter overViewAdapter;
        protected MultiTypeAdapter adapter;
        private List<View> readViewList = new LinkedList<>();
        private List<View> playViewList = new LinkedList<>();

        private ImageView playPauseImg;
        private View repeatReadGuideContainer;
        private TextView repeatReadGuideTitle;
        
        public ViewHolder(@NonNull View itemView, Book book, LifecycleOwner lifecycleOwner) {
            super(itemView);
            this.book = book;
            Log.d(TAG, "ViewHolder: " + book);
            this.bookId = book.getId();
            this.bookGenuineId = book.getGenuineId();
            context = itemView.getContext();

            View consecutiveReadImg = itemView.findViewById(R.id.consecutive_read_img);
            consecutiveReadImg.setOnClickListener(this);
            View consecutiveReadTv = itemView.findViewById(R.id.consecutive_read_tv);
            consecutiveReadTv.setOnClickListener(this);
            View menuImg = itemView.findViewById(R.id.menu_img);
            menuImg.setOnClickListener(this);
            View menuTv = itemView.findViewById(R.id.menu_tv);
            menuTv.setOnClickListener(this);
            View repeatReadImg = itemView.findViewById(R.id.repeat_read_img);
            repeatReadImg.setOnClickListener(this);
            View repeatReadTv = itemView.findViewById(R.id.repeat_read_tv);
            repeatReadTv.setOnClickListener(this);

            readViewList.add(consecutiveReadImg);
            readViewList.add(consecutiveReadTv);
            readViewList.add(menuImg);
            readViewList.add(menuTv);
            readViewList.add(repeatReadImg);
            readViewList.add(repeatReadTv);

            playPauseImg = itemView.findViewById(R.id.play_pause_img);
            playPauseImg.setOnClickListener(this);
            View stopImg = itemView.findViewById(R.id.stop_img);
            stopImg.setOnClickListener(this);

            playViewList.add(playPauseImg);
            playViewList.add(stopImg);

            itemView.findViewById(R.id.previous_img).setOnClickListener(this);
            itemView.findViewById(R.id.next_img).setOnClickListener(this);

            pagePager = itemView.findViewById(R.id.page_pager);
            ViewPager2Helper.setNullItemAnimator(pagePager);
            adapter = new MultiTypeAdapter().lifecycleOwner(lifecycleOwner);
            adapter.register(Page.class, new PageBinder(this, this));
            pagePager.setAdapter(adapter);
            pagePager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    Log.d(TAG, "onPageSelected: " + position);
                    stopCurrentPlay();

                    pageClickedZone = CoordinateView.NO_ZONE;
                    Page page = (Page) adapter.getItems().get(position);
                    pageNum = page.getPageNum();
                    LocationGroup locationGroup = page.getLocationGroup();
                    if (locationGroup != null) {
                        pageMaxZone = locationGroup.num - 1;
                    } else {
                        pageMaxZone = 0;
                    }

                    // only MAIN/OVERVIEW preserved.
                    Iterator<AnimLayer> iter = bottomViewList.iterator();
                    while (iter.hasNext()) {
                        AnimLayer animLayer = iter.next();
                        if (animLayer.type == BottomType.PLAY_PAUSE ||
                                animLayer.type == BottomType.REPEAT) {
                            iter.remove();
                        }
                    }
                    if (setOverviewPage(position)) {
                        menuBottomPanelHelper.setPagePosition(position);
                    }
                }
            });

            overviewView = itemView.findViewById(R.id.overview_view);
            overViewAdapter = new MultiTypeAdapter();
            overViewAdapter.register(PageUI.class, new PageOverviewBinder(this));
            overviewView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            overviewView.addItemDecoration(DecorationFactory.get2_0_2_0Space16_0_16_0());
            overviewView.setNestedScrollingEnabled(true);
            overviewView.setAdapter(overViewAdapter);
            repeatReadGuideContainer = itemView.findViewById(R.id.repeat_read_guide_container);

            PaintDrawable paintDrawable = new PaintDrawable();
            paintDrawable.setCornerRadius(UIHelper.dp2px(16));
            paintDrawable.getPaint().setColor(itemView.getResources().getColor(R.color._e9f4fb));
            repeatReadGuideContainer.setBackground(paintDrawable);
            repeatReadGuideContainer.setVisibility(View.INVISIBLE);

            repeatReadGuideContainer.findViewById(R.id.exit).setOnClickListener(this);
            repeatReadGuideContainer.findViewById(R.id.icon).setOnClickListener(this);

            repeatReadGuideTitle = repeatReadGuideContainer.findViewById(R.id.title);

            overviewView.setVisibility(View.INVISIBLE);
            for (View v : playViewList) {
                v.setVisibility(View.INVISIBLE);
            }

            Log.d(TAG, "overviewView: " + overviewView.getHeight() + " " + UIHelper.dp2px(120));

            mainLayer = new AnimLayer(BottomType.MAIN, readViewList, 0);
            overviewLayer = new AnimLayer(BottomType.OVERVIEW, Collections.singletonList(overviewView), UIHelper.dp2px(120) + 100);
            playPauseLayer = new AnimLayer(BottomType.PLAY_PAUSE, playViewList, UIHelper.dp2px(62) + 140);
            repeatLayer = new AnimLayer(BottomType.REPEAT, Collections.singletonList(repeatReadGuideContainer), UIHelper.dp2px(120) + 100);

            bottomViewList.add(mainLayer);

            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this);
            }
        }

        public void bindData(BodyType bodyType) {
            android.util.Log.d(TAG, "bindData: ");

            new PageSummaryTask(bookId, bookGenuineId, this).executeOnExecutor(Executors.io());
            menuBottomPanelHelper = new MenuBottomPanelHelper(context, this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.consecutive_read_img:
                case R.id.consecutive_read_tv:
                    Log.d(TAG, "consecutive: " + pageNum + " " + pageClickedZone + " " + pageMaxZone);
                    if (pageClickedZone == CoordinateView.NO_ZONE) {
                        if (pageMaxZone > 0) {
                            playerHelper.playRepeatedly(context, bookId, bookGenuineId, pageNum, 0, pageMaxZone);
                            anyAnimHelper.tryShow(playPauseLayer);
                        }
                    } else {
                        playerHelper.playRepeatedly(context, bookId, bookGenuineId, pageNum, pageClickedZone);
                        anyAnimHelper.tryShow(playPauseLayer);
                    }
                    break;
                case R.id.menu_img:
                case R.id.menu_tv:
                    menuBottomPanelHelper.showBottomPanel();
                    break;
                case R.id.repeat_read_img:
                case R.id.repeat_read_tv:
                    anyAnimHelper.tryShow(repeatLayer);
                    mode = Mode.REPEAT_READ_GUIDE_1;
                    repeatReadGuideTitle.setText(R.string.repeat_read_step1);
                    break;
                case R.id.exit:
                case R.id.icon:
                    anyAnimHelper.tryHide(repeatLayer);
                    mode = Mode.NORMAL;
                    break;
                case R.id.previous_img:
                    int currPage = pagePager.getCurrentItem();
                    int targetPage = currPage - 1;
                    if (targetPage >= 0) {
//                        stopCurrentPlay();
                        pagePager.setCurrentItem(targetPage, true);
                        Log.d(TAG, "setCurrentItem previous: " + targetPage);
                    } else {
                        Toast.makeText(context, R.string.first_page, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.next_img:
                    currPage = pagePager.getCurrentItem();
                    targetPage = currPage + 1;
                    int totalSize = adapter.getItems() != null ? adapter.getItems().size() : 0;
                    if (targetPage < totalSize) {
//                        stopCurrentPlay();
                        pagePager.setCurrentItem(targetPage, true);
                        Log.d(TAG, "setCurrentItem next: " + targetPage);
                    } else {
                        Toast.makeText(context, R.string.last_page, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.play_pause_img:
                    Log.d(TAG, "onClick play/pause: " + playerState);
                    if (playerState == STATE_PAUSED) {
                        playerHelper.resume();
                    } else if (playerState == STATE_STARTED) {
                        playerHelper.pause();
                    }
                    break;
                case R.id.stop_img:
                    stopCurrentPlay();
                    break;
            }
        }

        private void stopCurrentPlay() {
            playerHelper.stop();
            anyAnimHelper.tryHide(playPauseLayer);
            mode = Mode.NORMAL;
        }

        private int currPos;
        @Override
        public void onPageSummary(List<Page> pageList, List<Page> menuItemList) {
            Log.d(TAG, "onPageSummary: " + pageList.size());
            RuiDiffUtil.onNewData(adapter, pageList);
            RuiDiffUtil.onNewData(overViewAdapter, PageUI.from(pageList));
            menuBottomPanelHelper.bindData(book, menuItemList);
        }

        private MenuBottomPanelHelper menuBottomPanelHelper;

        @Override
        public void onPageSelectedFromMenu(Page page) {
            menuBottomPanelHelper.hideBottomPanel();
            pagePager.setCurrentItem(page.getOrdinal(), true);
            Log.d(TAG, "setCurrentItem from menu: " + page.getPageNum());
        }

        @Override
        public void onPageSelectedFromOverview(int pos) {
            if (setOverviewPage(pos)) {
                menuBottomPanelHelper.setPagePosition(pos);
                pagePager.setCurrentItem(pos, true);
                Log.d(TAG, "setCurrentItem from overview: " + pos);
            }
        }

        private boolean setOverviewPage(int pos) {
            boolean ret = false;
            Log.d(TAG, "setOverviewPage: " + pos);
            List list = overViewAdapter.getItems();
            Object currPosObj = list.get(currPos);
            Object newPosObj = list.get(pos);
            // valid to handle.
            if (currPosObj instanceof PageUI && newPosObj instanceof PageUI) {
                ((PageUI) currPosObj).setSelected(false);
                overViewAdapter.notifyItemChanged(currPos);

                PageUI newPageUI = (PageUI) newPosObj;
                newPageUI.setSelected(true);
                overViewAdapter.notifyItemChanged(pos);
                currPos = pos;

                overviewView.smoothScrollToPosition(pos);
                ret = true;

            }
            return ret;
        }

        @Override
        public void onPageDetailsRet(int pageNum, LocationGroup locationGroup) {
            Log.d(TAG, "onPageDetailsRet: " + pageNum);
            List list = adapter.getItems();
            if (!ListUtils.isEmpty(list)) {
                for (int i = 0; i < list.size(); i++) {
                    Object obj = list.get(i);
                    if (obj instanceof Page) {
                        Page page = (Page) obj;
                        if (page.getPageNum() == pageNum) {
                            page.setLocationGroup(locationGroup);
                            adapter.notifyItemChanged(i, locationGroup);
                            Log.d(TAG, "onPageDetailsRet2: " + i);

                            if (pageNum == this.pageNum) {
                                pageMaxZone = locationGroup.num - 1;
                            }
                            break;
                        }
                    }
                }
            }
        }

        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onEvent(ChangeSpeedEvent changeSpeedEvent) {
            playerHelper.changeSpeed();
        }

        @Override
        public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
            Log.d(TAG, "onStateChanged: " + event);
            if (event == Lifecycle.Event.ON_STOP) {
                playerHelper.stop();
            } else if (event == Lifecycle.Event.ON_DESTROY) {
                if (EventBus.getDefault().isRegistered(this)) {
                    EventBus.getDefault().unregister(this);
                }
            }
        }

        @Override
        public boolean onBackPressed() {
            if (anyAnimHelper.tryHide()) {
                Log.d(TAG, "onBackPressed: here");
                return true;
            } else return false;
        }

        private int playerState = STATE_INIT;
        @Override
        public void onStateChanged(int state) {
            playerState = state;
            switch (state) {
                case STATE_STARTED:
                    playPauseImg.setImageResource(R.mipmap.pause);
                    break;
                case STATE_PAUSED:
                    playPauseImg.setImageResource(R.mipmap.play);
                    break;
                case STATE_ERROR:
                    playPauseImg.setImageResource(R.mipmap.play);
                    break;
            }
        }

        public enum BottomType {
            MAIN,
            OVERVIEW,
            PLAY_PAUSE,
            REPEAT
        }
        private static class AnimLayer {
            BottomType type;
            List<View> viewList;
            int animHeight;

            public AnimLayer(BottomType type, List<View> viewList, int animHeight) {
                this.type = type;
                this.viewList = viewList;
                this.animHeight = animHeight;
            }

            @Override
            public String toString() {
                return type.toString();
            }
        }

        private AnimLayer mainLayer;
        private AnimLayer overviewLayer;
        private AnimLayer playPauseLayer;
        private AnimLayer repeatLayer;

        private ArrayList<AnimLayer> bottomViewList = new ArrayList<>();

        private AnyAnimHelper anyAnimHelper = new AnyAnimHelper();

        private class AnyAnimHelper extends AnimHelper {

            @Override
            protected List<View> getOldViewList() {
                return oldLayer.viewList;
            }

            @Override
            protected List<View> getNewViewList() {
                return newLayer.viewList;
            }

            @Override
            protected int getNewAnimTotal() {
                return newLayer.animHeight;
            }

            public boolean tryToggle(AnimLayer animLayer) {
                AnimLayer lastAnimLayer = bottomViewList.get(bottomViewList.size() - 1);
                if (lastAnimLayer.type != animLayer.type) {
                    Log.d(TAG, "tryToggle: " + animLayer.viewList.size() + " " + animLayer.viewList.get(0));
                    return tryShowAnim(lastAnimLayer, animLayer, false);
                } else {
                    return tryHideAnim(bottomViewList.get(bottomViewList.size() - 2), lastAnimLayer);
                }
            }

            public boolean tryHide(AnimLayer animLayer) {
                int size = bottomViewList.size();
                if (size >= 2) {
                    AnimLayer lastAnimLayer = bottomViewList.get(size - 1);
                    if (lastAnimLayer.type == animLayer.type) {
                        return tryHideAnim(bottomViewList.get(size - 2), lastAnimLayer);
                    }
                }
                return false;
            }

            public boolean tryHide() {
                Log.d(TAG, "tryhide: " + bottomViewList);
                if (bottomViewList.size() > 1) {
                    return tryHideAnim(
                            bottomViewList.get(bottomViewList.size() - 2),
                            bottomViewList.get(bottomViewList.size() - 1));
                } return false;
            }

            public boolean tryShow(AnimLayer animLayer) {
               return tryShow(animLayer, false);
            }

            public boolean tryShow(AnimLayer animLayer, boolean removeOld) {
                AnimLayer lastAnimLayer = bottomViewList.get(bottomViewList.size() - 1);
                Log.d(TAG, "tryShow: " + lastAnimLayer.type + " " + animLayer.type);
                if (lastAnimLayer.type != animLayer.type) {
                    return tryShowAnim(lastAnimLayer, animLayer, removeOld);
                } return false;
            }
        }

        private abstract class AnimHelper implements ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener {
            protected AnimLayer oldLayer, newLayer;

            private ValueAnimator overviewAnim;
            private int overviewAnimTotal;
            protected List<View> newViewList;

            private void ensureOverviewAnim() {
                if (overviewAnim == null) {
                    overviewAnim = new ValueAnimator();
                    overviewAnim.setDuration(500L);
                    overviewAnim.addUpdateListener(this);
                    overviewAnim.addListener(this);
                }
            }
            private void ensureOverviewTotal() {
                if (overviewAnimTotal == 0) {
                    overviewAnimTotal = getNewAnimTotal();
                }
            }

            protected abstract List<View> getOldViewList();

            protected abstract List<View> getNewViewList();

            protected abstract int getNewAnimTotal();

            public boolean isAnimating() {
                return panelState == PanelState.HIDING || panelState == PanelState.SHOWING;
            }

            public void bind(AnimLayer oldLayer, AnimLayer newLayer) {
                this.oldLayer = oldLayer;
                this.newLayer = newLayer;

                newViewList = getNewViewList();
                Log.d(TAG, "newViewArr: " + newViewList.size());
            }

            private PanelState panelState = PanelState.HIDDEN;
            private void startAnimInternal() {
                ensureOverviewAnim();
                ensureOverviewTotal();
                overviewAnim.setFloatValues(0f, 1f);
                overviewAnim.start();
            }

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
//                Log.d(TAG, "onAnimationUpdate: " + value + " " + overViewPanelState);
                switch (panelState) {
                    case HIDING:
                        for (View view : newViewList) {
                            view.setTranslationY(value * overviewAnimTotal);
                        }
                        for (View view : getOldViewList()) {
                            view.setAlpha(value);
                        }
                        break;
                    case SHOWING:
                        for (View view : newViewList) {
                            view.setTranslationY(overviewAnimTotal - value * overviewAnimTotal);
                        }
                        for (View view : getOldViewList()) {
                            view.setAlpha(1 - value);
                        }
                        break;
                }
            }

            @Override
            public void onAnimationStart(Animator animation) {
                switch (panelState) {
                    case HIDING:
                        for (View view : getOldViewList()) {
                            view.setAlpha(0F);
                            view.setVisibility(View.VISIBLE);
                        }
                        break;
                }
            }

            private void removeTopLayer(AnimLayer tarLayer) {
                int lastPos = bottomViewList.size() - 1;
                AnimLayer animLayer = bottomViewList.get(lastPos);
                if (animLayer == tarLayer) {
                    bottomViewList.remove(lastPos);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                switch (panelState) {
                    case HIDING:
                        for (View view : newViewList) {
                            view.setVisibility(View.INVISIBLE);
                        }
                        panelState = PanelState.HIDDEN;
                        for (View view : getOldViewList()) {
                            view.setAlpha(1F);
                        }
                        Log.d(TAG, "bottomViewList remove: " + (bottomViewList.size() - 1));

                        removeTopLayer(newLayer);

                        break;
                    case SHOWING:
                        panelState = PanelState.SHOWN;
                        for (View view : getOldViewList()) {
                            view.setAlpha(1F);
                            view.setVisibility(View.INVISIBLE);
                        }
                        if (removeOld) {
                            Log.d(TAG, "bottomViewList remove2: " + (bottomViewList.size() - 1));
                            removeTopLayer(oldLayer);
                        }
                        bottomViewList.add(newLayer);

                        Log.d(TAG, "bottomViewList add: " + bottomViewList);
                        break;
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                onAnimationEnd(animation);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

//            public boolean tryToggleAnim() {
//                if (tryHideAnim() || tryShowAnim()) {
//                    return true;
//                } else return false;
//            }

            public boolean tryHideAnim(AnimLayer oldLayer, AnimLayer newLayer) {
                Log.d(TAG, "tryHideAnim: " + panelState + " " + oldLayer + " " + newLayer);
                new Exception().printStackTrace();
//                if (panelState == PanelState.SHOWN) {
                if (!isAnimating()) {
                    bind(oldLayer, newLayer);
                    panelState = PanelState.HIDING;
                    startAnimInternal();
                    return true;
                } else return false;
            }

            private boolean removeOld;
            public boolean tryShowAnim(AnimLayer oldLayer, AnimLayer newLayer, boolean removeOld) {
                Log.d(TAG, "tryShowAnim: " + panelState + " " + oldLayer + " " + newLayer + " " + removeOld);
                new Exception().printStackTrace();
//                if (panelState == PanelState.HIDDEN) {
                if (!isAnimating()) {
                    this.removeOld = removeOld;
                    bind(oldLayer, newLayer);
                    for (View view : newViewList) {
                        view.setTranslationY(overviewAnimTotal);
                        view.setVisibility(View.VISIBLE);
                    }
                    panelState = PanelState.SHOWING;
                    startAnimInternal();
                    return true;
                } return false;
            }
        }

        @Override
        public void onPageMarginClick(int pageNum) {
            if (mode == Mode.NORMAL) {
                pageClickedZone = CoordinateView.NO_ZONE;
                anyAnimHelper.tryToggle(overviewLayer);
            }
        }

        private PlayerHelper playerHelper = new PlayerHelper(this);

        private int pageClickedZone = CoordinateView.NO_ZONE;
        private int pageNum;
        private int pageMaxZone;
        @Override
        public void onPageZoneClick(int pageNum, int zoneNum) {
            Log.d(TAG, "onPageZoneClick: " + pageNum + " " + zoneNum + " " + mode);
            if (mode == Mode.NORMAL) {
                pageClickedZone = zoneNum;
                playerHelper.playOnce(context, bookId, bookGenuineId, pageNum, zoneNum);
                anyAnimHelper.tryHide(overviewLayer);
            } else if (mode == Mode.REPEAT_READ_GUIDE_1) {
                repeatReadPageClickedZone = zoneNum;
                repeatReadGuideTitle.setText(R.string.repeat_read_step2);
                mode = Mode.REPEAT_READ_GUIDE_2;
            } else if (mode == Mode.REPEAT_READ_GUIDE_2) {
                if (repeatReadPageClickedZone < zoneNum) {
                    playerHelper.playRepeatedly(context, bookId, bookGenuineId, pageNum, repeatReadPageClickedZone, zoneNum);
                } else if (repeatReadPageClickedZone > zoneNum) {
                    playerHelper.playRepeatedly(context, bookId, bookGenuineId, pageNum, zoneNum, repeatReadPageClickedZone);
                } else {
                    playerHelper.playRepeatedly(context, bookId, bookGenuineId, pageNum, zoneNum);
                }
                anyAnimHelper.tryShow(playPauseLayer, true);
                mode = Mode.REPEAT_READ;
            }
        }

        private int repeatReadPageClickedZone;

        private Mode mode = Mode.NORMAL;
        private enum Mode {
            NORMAL,
            REPEAT_READ_GUIDE_1,
            REPEAT_READ_GUIDE_2,
            REPEAT_READ
        }
    }
}
