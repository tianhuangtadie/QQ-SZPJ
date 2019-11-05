package com.example.qqsz;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by MXY on 2019/2/8.
 */
public class MyDividerItem extends RecyclerView.ItemDecoration {
    private int leftRight;
    private int topBottom;

    public MyDividerItem(int leftRight, int topBottom) {
        this.leftRight = leftRight;
        this.topBottom = topBottom;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        GridLayoutManager layoutManager = (GridLayoutManager) parent.getLayoutManager();
        final GridLayoutManager.LayoutParams lp = (GridLayoutManager.LayoutParams) view.getLayoutParams();
        final int childPosition = parent.getChildAdapterPosition(view);
        final int spanCount = layoutManager.getSpanCount();
        if (layoutManager.getOrientation() == GridLayoutManager.VERTICAL) {
            //判断是否在第一排
            if (layoutManager.getSpanSizeLookup().getSpanGroupIndex(childPosition, spanCount) == 0) {
                //第一排的需要上面
//                outRect.top = topBottom;
            }
            outRect.bottom = topBottom;
            //这里忽略和合并项的问题，只考虑占满和单一的问题
            if (lp.getSpanSize() == spanCount) {
                //占满
                outRect.left = leftRight;
                outRect.right = leftRight;
            } else {
                outRect.left = (int) (((float) (spanCount - lp.getSpanIndex())) / spanCount * leftRight);
                outRect.right = (int) (((float) leftRight * (spanCount + 1) / spanCount) - outRect.left);
            }
        } else {
            if (layoutManager.getSpanSizeLookup().getSpanGroupIndex(childPosition, spanCount) == 0) {
                //第一排的需要left
//                outRect.left = leftRight;
            }
            outRect.right = leftRight;
            //这里忽略和合并项的问题，只考虑占满和单一的问题
            if (lp.getSpanSize() == spanCount) {
                //占满
                outRect.top = topBottom;
                outRect.bottom = topBottom;
            } else {
                outRect.top = (int) (((float) (spanCount - lp.getSpanIndex())) / spanCount * topBottom);
                outRect.bottom = (int) (((float) topBottom * (spanCount + 1) / spanCount) - outRect.top);
            }
        }
    }
}

