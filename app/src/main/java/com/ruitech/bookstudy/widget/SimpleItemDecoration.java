package com.ruitech.bookstudy.widget;

import android.graphics.Rect;
import android.view.View;

import java.util.Arrays;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import me.drakeet.multitype.ItemViewBinder;
import me.drakeet.multitype.MultiTypeAdapter;

public class SimpleItemDecoration extends RecyclerView.ItemDecoration {
    private int leftSpace, topSpace;
    private int rightSpace, bottomSpace;
    private int left, right;
    private int top, bottom;

    public SimpleItemDecoration(int left, int top, int right, int bottom, boolean flag){
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        if (flag) {
            this.leftSpace = left;
            this.topSpace = top;
            this.rightSpace = right;
            this.bottomSpace = bottom;
        }
    }

    public SimpleItemDecoration(int left, int top, int right, int bottom, int leftSpace, int topSpace, int rightSpace, int bottomSpace){
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.leftSpace = leftSpace;
        this.topSpace = topSpace;
        this.rightSpace = rightSpace;
        this.bottomSpace = bottomSpace;

    }

    private boolean isLastRow(int childPosition, int totalCount, int spanCount, GridLayoutManager gridLayoutManager) {
        int nextChildPos = childPosition + 1;
        int length = 0;
        for (int i = nextChildPos; i < totalCount; i++) {
            length += gridLayoutManager.getSpanSizeLookup().getSpanSize(nextChildPos);
            if (length >= spanCount) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.left = left;
        outRect.top = top;
        outRect.right = right;
        outRect.bottom = bottom;
        LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager){
            int totalCount = layoutManager.getItemCount();
            int spanCount = ((GridLayoutManager)layoutManager).getSpanCount();
            //divide exactly
            int surplusCount = totalCount % spanCount;
            int childPosition = parent.getChildAdapterPosition(view);

            int spanIndex = ((GridLayoutManager)layoutManager).getSpanSizeLookup().getSpanIndex(childPosition, spanCount);//getSpanSize(childPosition);
            if (layoutManager.getOrientation() == GridLayoutManager.VERTICAL) {
                boolean arr[] = new boolean[4];
                //left column
                if (spanIndex % spanCount == 0){
                    outRect.left = leftSpace;
                    arr[0] = true;
                }
                //top raw
                if (spanIndex >= childPosition && spanIndex < spanCount){
                    outRect.top = topSpace;
                    arr[1] = true;
                }
                //right column
                if ((spanIndex + 1) % spanCount == 0) {
                    outRect.right = rightSpace;
                    arr[2] = true;
                }

                if (isLastRow(childPosition, totalCount, spanCount, (GridLayoutManager)layoutManager)) {
                    outRect.bottom = bottomSpace;
                    arr[3] = true;
                }

//                android.util.Log.d("meng here", "getItemOffsets: " + childPosition + " " + spanIndex + " " + spanCount + " " + totalCount
//                + " " + Arrays.toString(arr));



//                //bottom raw
//                if (surplusCount == 0 && childPosition > totalCount - spanCount - 1) {
//                    outRect.bottom = bottomSpace;
//                } else if (surplusCount != 0 && childPosition > totalCount - surplusCount - 1) {
//                    outRect.bottom = bottomSpace;
//                }
//                if (spanCount >= 3) {
//                    int widthSpace = leftSpace + rightSpace + ((left + right) * (spanCount - 1));
//                    int widthAverage = (int) (widthSpace * 1.0f / spanCount);
//                    if (childPosition % spanCount == 0) {
//                        outRect.right = Math.max(0, widthAverage - outRect.left);
//                    } else if ((childPosition + 1) % spanCount == 0){
//                        outRect.left = Math.max(0, widthAverage - outRect.right);
//                    } else {
//                        outRect.left = (int) (widthAverage / 2.f);
//                        outRect.right = (int) (widthAverage / 2.f);
//                    }
//                }
            } else {
                //left column
                if (childPosition < spanCount){
                    outRect.left = leftSpace;
                }
                //top raw
                if (childPosition % spanCount == 0){
                    outRect.top = topSpace;
                }
                //right column
                if (surplusCount == 0 && childPosition > totalCount - spanCount - 1) {
                    outRect.right = rightSpace;
                } else if (surplusCount != 0 && childPosition > totalCount - surplusCount - 1) {
                    outRect.right = rightSpace;
                }
                //bottom raw
                if ((childPosition + 1) % spanCount == 0) {
                    outRect.bottom = bottomSpace;
                }
            }
        } else {
            int totalCount = layoutManager.getItemCount();
            if (layoutManager.getOrientation() == LinearLayoutManager.VERTICAL){
                if (parent.getChildAdapterPosition(view) == 0) {
                    outRect.top = topSpace;
                }
                if (parent.getChildAdapterPosition(view) == totalCount - 1) {
                    outRect.bottom = bottomSpace;
                }
                outRect.left = leftSpace;
                outRect.right = rightSpace;
            } else {
                int pos = parent.getChildAdapterPosition(view);
                if (pos == 0){
                    outRect.left = leftSpace;
                }
                if (pos == totalCount - 1) {
                    outRect.right = rightSpace;
                }
//                if (pos >= 0) {
//                    if (isZeroMarginItem(parent.getAdapter(), pos)) {
//                        if (pos > 0) {
//                            outRect.left = 0;
//                        }
//                        if (pos != totalCount - 1) {
//                            outRect.right = 0;
//                        }
//                    }
//                }

                outRect.top = topSpace;
                outRect.bottom = bottomSpace;
            }
        }
    }

//    private boolean isZeroMarginItem(RecyclerView.Adapter adapter, int pos) {
//        if (adapter instanceof MultiTypeAdapter) {
//            int itemViewType = adapter.getItemViewType(pos);
//            if (itemViewType >= 0) {
//                int itemViewBindersSize = ((MultiTypeAdapter) adapter).getTypePool().getItemViewBinders().size();
//                if (itemViewType < itemViewBindersSize) {
//                    ItemViewBinder binder = ((MultiTypeAdapter) adapter).getTypePool().getItemViewBinders().get(itemViewType);
//                    return binder instanceof IZeroMarginDecoration;
//                }
//            }
//        }
//        return false;
//    }
//
//    public interface IZeroMarginDecoration {
//    }
}
