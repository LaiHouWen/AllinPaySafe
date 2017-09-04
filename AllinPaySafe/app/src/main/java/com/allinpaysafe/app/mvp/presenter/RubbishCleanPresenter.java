package com.allinpaysafe.app.mvp.presenter;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.format.Formatter;

import com.allinpaysafe.app.R;
import com.allinpaysafe.app.adapter.CacheListAdapter;
import com.allinpaysafe.app.adapter.base.BaseRecyclerViewAdapter;
import com.allinpaysafe.app.injector.ContextLifeCycle;
import com.allinpaysafe.app.model.CacheListItem;
import com.allinpaysafe.app.mvp.impl.Presenter;
import com.allinpaysafe.app.mvp.impl.RubbishCleanView;
import com.allinpaysafe.app.mvp.impl.View;
import com.allinpaysafe.app.service.CleanerService;
import com.allinpaysafe.app.ui.MeoryClearActivity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by houwen.lai on 2017/9/4.
 * 内存清理
 *
 */

public class RubbishCleanPresenter implements Presenter,
        CleanerService.OnActionListener{

    RubbishCleanView mRubbishClean;
    private CleanerService mCleanerService;

    private boolean mAlreadyScanned = false;
    private final Context mContext;
    List<CacheListItem> mCacheListItems = new ArrayList<>();
    CacheListAdapter recyclerAdapter;

    public RubbishCleanPresenter(Context mContext) {
        this.mContext = mContext;
    }


    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mCleanerService
                    = ((CleanerService.CleanerServiceBinder) service).getService();
            mCleanerService.setOnActionListener(RubbishCleanPresenter.this);

//              updateStorageUsage();

            if (!mCleanerService.isScanning() && !mAlreadyScanned) {
                mCleanerService.scanCache();
            }
        }


        @Override public void onServiceDisconnected(ComponentName name) {
            mCleanerService.setOnActionListener(null);
            mCleanerService = null;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {

        initViews();
    }
    public void initViews() {

        //0则不执行拖动或者滑动
        ItemTouchHelper.Callback mCallback = new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }


            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                //recyclerAdapter.remove(position);
//                mRubbishClean.showSnackbar("清理" + TextFormater.dataSizeFormat(
//                        mCacheListItems.get(position).getCacheSize()) + "缓存");
//                mCleanerService.cleanCache(
//                        mCacheListItems.get(position).getPackageName());
//                //mAppProcessInfos.remove(mAppProcessInfos.get(i));
//                //mClearMemoryAdapter.notifyDataSetChanged();
//                recyclerAdapter.remove(position);
            }


            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY,
                        actionState, isCurrentlyActive);
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    //滑动时改变Item的透明度
                    final float alpha = 1 - Math.abs(dX) /
                            (float) viewHolder.itemView.getWidth();
                    viewHolder.itemView.setAlpha(alpha);
                    viewHolder.itemView.setTranslationX(dX);
                }
            }
        };

        recyclerAdapter = new CacheListAdapter(mCacheListItems, mContext);
        recyclerAdapter.setOnInViewClickListener(R.id.card_item_root,
                new BaseRecyclerViewAdapter.onInternalClickListenerImpl<CacheListItem>() {
                    @Override
                    public void OnClickListener(android.view.View parentV, android.view.View v, Integer position, CacheListItem values) {
                        super.OnClickListener(parentV, v, position, values);
                        //onRecyclerViewItemClick(position, values);
//                        Intent intent = new Intent();
//                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        intent.setAction(
//                                android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                        intent.setData(Uri.parse(
//                                "package:" + values.getPackageName()));
//                        mContext.startActivity(intent);
                    }
                });
        recyclerAdapter.setFirstOnly(false);
        recyclerAdapter.setDuration(300);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(mCallback);

        mContext.bindService(new Intent(mContext, CleanerService.class),
                mServiceConnection, Context.BIND_AUTO_CREATE);

        mRubbishClean.initViews(recyclerAdapter, mContext, itemTouchHelper);

//        mRubbishClean.initViews(recyclerAdapter, mContext, itemTouchHelper);

    }
    @Override
    public void onResume() {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onStop() {

    }

    @Override public void onDestroy() {
        mContext.unbindService(mServiceConnection);
        //RefWatcher refWatcher = App.getRefWatcher(mContext);
        //refWatcher.watch(this);
    }

    @Override
    public void attachView(View v) {
        mRubbishClean = (MeoryClearActivity) v;
    }

    @Override
    public void onScanStarted(Context context) {
        mRubbishClean.onScanStarted(context);
        mRubbishClean.startRefresh();
        mRubbishClean.enableSwipeRefreshLayout(false);
    }

    @Override
    public void onScanProgressUpdated(Context context, int current, int max, long cacheSize, String packageName) {
        mRubbishClean.onScanProgressUpdated(context, current, max, cacheSize,
                packageName);
    }

    @Override
    public void onScanCompleted(Context context, List<CacheListItem> apps) {
        mCacheListItems.clear();
        mCacheListItems.addAll(apps);
        recyclerAdapter.notifyDataSetChanged();
        mRubbishClean.onScanCompleted();
        mRubbishClean.stopRefresh();
        mRubbishClean.enableSwipeRefreshLayout(true);
    }

    @Override
    public void onCleanStarted(Context context) {

    }

    @Override
    public void onCleanCompleted(Context context, long cacheSize) {
        mRubbishClean.showSnackbar(context.getString(R.string.cleaned,
                Formatter.formatShortFileSize(mContext, cacheSize)));
        mCacheListItems.clear();
        recyclerAdapter.notifyDataSetChanged();
    }

    public void cleanCache() {
        if (mCleanerService != null && !mCleanerService.isScanning() &&
                !mCleanerService.isCleaning() &&
                mCleanerService.getCacheSize() > 0) {

            mCleanerService.cleanCache();
        }
    }

}
