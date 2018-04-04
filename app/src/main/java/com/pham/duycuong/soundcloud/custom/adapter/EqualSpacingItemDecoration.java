package com.pham.duycuong.soundcloud.custom.adapter;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class EqualSpacingItemDecoration extends RecyclerView.ItemDecoration {

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    public static final int GRID = 2;

    private int mSpacing;
    private int mDisplayMode;

    public EqualSpacingItemDecoration(int spacing) {
        this(spacing, -1);
    }

    public EqualSpacingItemDecoration(int spacing, int displayMode) {
        this.mSpacing = spacing;
        this.mDisplayMode = displayMode;
    }

    @Override
    public void getItemOffsets(
            Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildViewHolder(view).getAdapterPosition();
        int itemCount = state.getItemCount();
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        setSpacingForDirection(outRect, layoutManager, position, itemCount);
    }

    private void setSpacingForDirection(Rect outRect,
                                        RecyclerView.LayoutManager layoutManager,
                                        int position,
                                        int itemCount) {

        // Resolve display mode automatically
        if (mDisplayMode == -1) {
            mDisplayMode = resolveDisplayMode(layoutManager);
        }

        switch (mDisplayMode) {
            case HORIZONTAL:
                outRect.left = mSpacing;
                outRect.right = position == itemCount - 1 ? mSpacing : 0;
                outRect.top = mSpacing;
                outRect.bottom = mSpacing;
                break;
            case VERTICAL:
                outRect.left = mSpacing;
                outRect.right = mSpacing;
                outRect.top = mSpacing;
                outRect.bottom = position == itemCount - 1 ? mSpacing : 0;
                break;
            case GRID:
                if (layoutManager instanceof GridLayoutManager) {
                    GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
                    int cols = gridLayoutManager.getSpanCount();
                    int rows = itemCount / cols;
                    if (position % cols == 0) {
                        outRect.left = mSpacing;
                        outRect.right = mSpacing / 2;
                    } else if (position % cols == 1) {
                        outRect.right = mSpacing;
                        outRect.left = mSpacing / 2;
                    } else {
                        outRect.left = mSpacing / 2;
                        outRect.right = mSpacing / 2;
                    }
                    outRect.top = mSpacing;
                    outRect.bottom = (position > (rows * cols - 1)) ? mSpacing : 0;
                    if (rows * cols == itemCount) {
                        outRect.bottom = (position >= (rows * cols - cols)) ? mSpacing : 0;
                    }
                }
                break;
        }
    }

    private int resolveDisplayMode(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager instanceof GridLayoutManager) return GRID;
        if (layoutManager.canScrollHorizontally()) return HORIZONTAL;
        return VERTICAL;
    }
}
