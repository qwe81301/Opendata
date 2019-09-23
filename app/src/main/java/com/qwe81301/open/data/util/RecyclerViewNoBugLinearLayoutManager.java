package com.qwe81301.open.data.util;

import android.content.Context;
import android.util.AttributeSet;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * author:       bearshih
 * project:      EHRMS
 * date:         2019/7/4
 * version:
 * description: 處理官方 RecyclerView 取值越界造成crash問題(真的很瞎 官方那麼大的bug 竟然遲遲不修)
 *
 * 參考兩個網路分享的做法
 * https://www.jianshu.com/p/a15764f6d673
 * https://www.jianshu.com/p/24d73974bc41
 */
public class RecyclerViewNoBugLinearLayoutManager extends LinearLayoutManager {
    public RecyclerViewNoBugLinearLayoutManager(Context context) {
        super(context);
    }

    public RecyclerViewNoBugLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public RecyclerViewNoBugLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            //try catch一下
            super.onLayoutChildren(recycler, state);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

    }
}
