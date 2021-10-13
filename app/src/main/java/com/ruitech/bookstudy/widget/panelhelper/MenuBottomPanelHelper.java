package com.ruitech.bookstudy.widget.panelhelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.ruitech.bookstudy.R;
import com.ruitech.bookstudy.bean.Book;
import com.ruitech.bookstudy.bean.Page;
import com.ruitech.bookstudy.binder.MenuItemBinder;
import com.ruitech.bookstudy.uibean.BookUI;
import com.ruitech.bookstudy.uibean.PageUI;
import com.ruitech.bookstudy.widget.RuiDiffUtil;
import com.ruitech.bookstudy.widget.panelhelper.layout.ConstraintLayoutPanelContainer;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import me.drakeet.multitype.MultiTypeAdapter;

public class MenuBottomPanelHelper extends PartScreenBottomPanelHelper<ConstraintLayoutPanelContainer> {
    private static final String TAG = "MenuBottomPanelHelper";

    public MenuBottomPanelHelper(Context context, Callback callback) {
        super(context);
        bindView(callback);
    }

    private int currPos;
    private List<PageUI> pageUIList = new ArrayList<>();
    public void bindData(Book book, List<Page> menuItemList) {
        title.setText(context.getResources().getString(book.getSubject().resId) + " " + book.getBookVersion().getName());
        pageUIList.clear();
        currPos = PageUI.from(menuItemList, pageUIList);
        RuiDiffUtil.onNewData(adapter, pageUIList);
    }

    private int pagePos2MenuPos(int pagePos) {
        int ret = -1;
        for (int i = 0; i < pageUIList.size(); i++) {
            if (pagePos >= pageUIList.get(i).getValue().getOrdinal()) {
                ret = i;
            } else {
                break;
            }
        }
        return ret;
    }

    public void setPagePosition(int pagePos) {
        int pos = pagePos2MenuPos(pagePos);
        android.util.Log.d(TAG, "setPagePosition: " + pagePos + " " + pos);
        List list = adapter.getItems();
        Object currPosObj = list.get(currPos);
        Object newPosObj = list.get(pos);
        // valid to handle.
        if (currPosObj instanceof PageUI && newPosObj instanceof PageUI) {
            ((PageUI) currPosObj).setSelected(false);
            adapter.notifyItemChanged(currPos);

            PageUI newPageUI = (PageUI) newPosObj;
            newPageUI.setSelected(true);
            adapter.notifyItemChanged(pos);
            currPos = pos;
        }
    }

    private RecyclerView recyclerView;
    private TextView title;
    private MultiTypeAdapter adapter = new MultiTypeAdapter();
    private void bindView(Callback callback) {
        super.bindView((ConstraintLayoutPanelContainer) LayoutInflater.from(context).inflate(R.layout.bottom_panel_menu, null));
        recyclerView = container.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter.register(PageUI.class, new MenuItemBinder(callback));
        recyclerView.setAdapter(adapter);

        title = container.findViewById(R.id.title);
    }

    public interface Callback {
        void onPageSelectedFromMenu(Page page);
    }
}
