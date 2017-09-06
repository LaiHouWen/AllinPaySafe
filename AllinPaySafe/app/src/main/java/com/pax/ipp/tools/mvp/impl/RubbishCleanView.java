package com.pax.ipp.tools.mvp.impl;

import android.content.Context;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.pax.ipp.tools.adapter.CacheListAdapter;

import java.util.List;

/**
 * Created by Administrator on 2017/9/4.
 * 内存清理
 *
 */

public interface RubbishCleanView extends View {

    void initViews(CacheListAdapter recyclerAdapter, Context context, ItemTouchHelper itemTouchHelper);
//    void initViews(Context context);

    void onScanStarted(Context context);

    void onScanProgressUpdated(Context context, int current, int max, long cacheSize, String packageName);

//    void onScanProgressUpdated(Context context, int current, int max, long cacheSize, String packageName,CacheListItem result);
    void onScanCompleted();

    void stopRefresh();

    void startRefresh();

    boolean isRefreshing();

    void enableSwipeRefreshLayout(boolean enable);

    void showSnackbar(String message);

}
