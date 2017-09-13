package com.pax.ipp.tools.mvp.presenter;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.text.format.Formatter;

import com.pax.ipp.tools.Constant;
import com.pax.ipp.tools.R;
import com.pax.ipp.tools.adapter.CacheListAdapter;
import com.pax.ipp.tools.adapter.base.BaseRecyclerViewAdapter;
import com.pax.ipp.tools.event.ClearChoiseAllEvent;
import com.pax.ipp.tools.event.ClearMeoryEvent;
import com.pax.ipp.tools.event.MeoryClearEvent;
import com.pax.ipp.tools.model.CacheListItem;
import com.pax.ipp.tools.mvp.impl.Presenter;
import com.pax.ipp.tools.mvp.impl.RubbishCleanView;
import com.pax.ipp.tools.mvp.impl.View;
import com.pax.ipp.tools.service.CleanerService;
import com.pax.ipp.tools.ui.base.RubbishActivity;
import com.pax.ipp.tools.utils.LogUtil;
import com.pax.ipp.tools.utils.TextFormater;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

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
    public List<CacheListItem> mCacheListItems = new ArrayList<>();
    public CacheListAdapter recyclerAdapter;

    long killAppmemory = 0;
    int count = 0;

    private boolean flagConnection=false;

    public RubbishCleanPresenter(Context mContext) {
        this.mContext = mContext;
    }


    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mCleanerService
                    = ((CleanerService.CleanerServiceBinder) service).getService();
            mCleanerService.setOnActionListener(RubbishCleanPresenter.this);
            flagConnection=true;
//              updateStorageUsage();

            //绑定service 扫描内存
            if (!mCleanerService.isScanning() && !mAlreadyScanned) {
                mCleanerService.scanCache();
            }
        }

        @Override public void onServiceDisconnected(ComponentName name) {
            mCleanerService.setOnActionListener(null);
            mCleanerService = null;
            flagConnection=false;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        initBindService();
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
//                int position = viewHolder.getAdapterPosition();
//                recyclerAdapter.remove(position);
//                mRubbishClean.showSnackbar("清理" + TextFormater.dataSizeFormat(
//                        mCacheListItems.get(position).getCacheSize()) + "缓存");
//                mCleanerService.cleanCache(
//                        mCacheListItems.get(position).getPackageName());
//                mAppProcessInfos.remove(mAppProcessInfos.get(i));
//                mClearMemoryAdapter.notifyDataSetChanged();
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


        mRubbishClean.initViews(recyclerAdapter, mContext, itemTouchHelper);

//        mRubbishClean.initViews(recyclerAdapter, mContext, itemTouchHelper);

    }


    public void initBindService(){
        mContext.bindService(new Intent(mContext, CleanerService.class),
                mServiceConnection, Context.BIND_AUTO_CREATE);
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
        if (flagConnection)
        mContext.unbindService(mServiceConnection);

    }

    @Override
    public void attachView(View v) {
        mRubbishClean =  (RubbishActivity)v;
    }

    @Override
    public void onScanStarted(Context context) {
        mRubbishClean.onScanStarted(context);
        mRubbishClean.startRefresh();
        mRubbishClean.enableSwipeRefreshLayout(false);
    }

//    @Override
//    public void onScanProgressUpdated(Context context, int current, int max, long cacheSize, String packageName,CacheListItem item) {
//        mRubbishClean.onScanProgressUpdated(context, current, max, cacheSize,
//                packageName,item);
//    }

    @Override
    public void onScanProgressUpdated(Context context, int current, int max, long cacheSize, String packageName) {
        mRubbishClean.onScanProgressUpdated(context, current, max, cacheSize,
                packageName);
    }

    @Override
    public void onScanCompleted(Context context, List<CacheListItem> apps) {
        mCacheListItems.clear();
        mCacheListItems.addAll(apps);
        if (recyclerAdapter!=null)
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

        if (mCacheListItems!=null)
        mCacheListItems.clear();
        if (recyclerAdapter!=null)
        recyclerAdapter.notifyDataSetChanged();
    }

    public void scanCache(){
        if (mCleanerService!=null&&!mCleanerService.isScanning() && !mAlreadyScanned) {
            mCleanerService.scanCache();
        }
    }

    public void cleanCache() {
        LogUtil.e("clean Cacheclean ");
        if (mCleanerService != null &&
                !mCleanerService.isScanning() &&
                !mCleanerService.isCleaning() &&
                mCleanerService.getCacheSize() >= 0) {
            LogUtil.e("clean Cacheclean CachecleanCache");
            mCleanerService.cleanCache();

        }else {
            //TODO
            mRubbishClean.showSnackbar("");
        }
    }

    /**
     *
     * 清除选中的app
     * type  all
     *
     */
    public void cleanMemory(String type) {

        new clearApp().execute(type);

//
//                long killAppmemory = 0;
//                long count = 0;
//                List<CacheListItem> listItems =new ArrayList<CacheListItem>();
//                for (int i = mCacheListItems.size() - 1; i >= 0; i--) {
//                    long memory = mCacheListItems.get(i).getCacheSize();
//                    if (mCacheListItems.get(i).getIsChoise()) {
//                        EventBus.getDefault().post(new String("t"));
//                        count++;
//                        killAppmemory += memory;
//                        mCleanerService.killBackgroundProcesses(
//                                mCacheListItems.get(i).getPackageName());
//                        //mAppProcessInfos.remove(mAppProcessInfos.get(i));
//                        //mClearMemoryAdapter.notifyDataSetChanged();
//                        listItems.add(mCacheListItems.get(i));
//                        recyclerAdapter.remove(mCacheListItems.get(i));
//                    }
//                }
//                mCacheListItems.removeAll(listItems);
//
////        mRubbishClean.updateBadge(0);
////        mRubbishClean.updateTitle(mContext, 0);
//        EventBus.getDefault().postSticky(new ClearMeoryEvent(killAppmemory,""));
//        if (count>0)
//        EventBus.getDefault().post(new MeoryClearEvent());
//
//        mRubbishClean.showSnackbar(count > 0 ? "共清理" + count + "个进程,共占内存" +
//                TextFormater.dataSizeFormat(killAppmemory) +
//                "内存" : "未选中要清理的进程");
    }

    /**
     * 清理选中的apps
     */
   private class clearApp extends AsyncTask<String,Void,List<CacheListItem>>{

       @Override
       protected void onPreExecute() {
           super.onPreExecute();
           EventBus.getDefault().post(new String("t"));
       }

       @Override
       protected List<CacheListItem> doInBackground(String... params) {
           List<CacheListItem> listItems =new ArrayList<CacheListItem>();
           String type = params[0];
           for (int i = mCacheListItems.size() - 1; i >= 0; i--) {
               long memory = mCacheListItems.get(i).getCacheSize();
               if ((!TextUtils.isEmpty(type)&&type.equals(Constant.TYPE_CLEARN_MEORY_ALL))
                       ||mCacheListItems.get(i).getIsChoise()) {
                   count++;
                   killAppmemory += memory;
                   mCleanerService.killBackgroundProcesses(
                           mCacheListItems.get(i).getPackageName());
                   listItems.add(mCacheListItems.get(i));
               }
           }
           EventBus.getDefault().post(new ClearChoiseAllEvent(type,listItems,killAppmemory,count));
           return listItems;
       }

       @Override
        protected void onPostExecute(List<CacheListItem> cacheListItems) {
           if (recyclerAdapter!=null&&cacheListItems!=null){
               for (int i=0;i<cacheListItems.size();i++){
                   recyclerAdapter.remove(cacheListItems.get(i));
               }
               mCacheListItems.removeAll(cacheListItems);
               recyclerAdapter.notifyDataSetChanged();
           }
            //MeoryClearActivity.class
           EventBus.getDefault().postSticky(new ClearMeoryEvent(killAppmemory,count > 0 ? "共清理" + count + "个进程,共占内存" +
                   TextFormater.dataSizeFormat(killAppmemory) +
                   "内存" : "未选中要清理的进程"));
           if (count>0)
               EventBus.getDefault().post(new MeoryClearEvent(killAppmemory));
//
           mRubbishClean.showSnackbar(count > 0 ? "共清理" + count + "个进程,共占内存" +
                   TextFormater.dataSizeFormat(killAppmemory) +
                   "内存" : "未选中要清理的进程");

        }
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onMeoryEvent(ClearChoiseAllEvent event){
//        if (event==null)return;
//        LogUtil.e("eventbus",event.toString());
//        if (event.getType().equals(Constant.TYPE_CLEARN_MEORY_ALL)){
//            mRubbishClean.showSnackbar(event.getCount() > 0 ? "共清理" + event.getCount() + "个进程,共占内存" +
//                    TextFormater.dataSizeFormat(event.getMemorySize()) +
//                    "内存" : "未选中要清理的进程");
//            EventBus.getDefault().post(new Long(event.getMemorySize()));
//        }else {//列表
//            if (recyclerAdapter!=null&&event.getList()!=null){
//                for (int i=0;i<event.getList().size();i++){
//                    recyclerAdapter.remove(event.getList().get(i));
//                }
//                mCacheListItems.removeAll(event.getList());
//                recyclerAdapter.notifyDataSetChanged();
//            }
//            mRubbishClean.showSnackbar(event.getCount() > 0 ? "共清理" + event.getCount() + "个进程,共占内存" +
//                    TextFormater.dataSizeFormat(event.getMemorySize()) +
//                    "内存" : "未选中要清理的进程");
//        }
//    }

}
