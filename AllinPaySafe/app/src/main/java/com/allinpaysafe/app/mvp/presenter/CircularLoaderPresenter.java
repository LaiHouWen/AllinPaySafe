package com.allinpaysafe.app.mvp.presenter;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.allinpaysafe.app.R;
import com.allinpaysafe.app.injector.ContextLifeCycle;
import com.allinpaysafe.app.model.AppProcessInfo;
import com.allinpaysafe.app.model.Menu;
import com.allinpaysafe.app.mvp.impl.CircularLoaderView;
import com.allinpaysafe.app.mvp.impl.Presenter;
import com.allinpaysafe.app.mvp.impl.View;
import com.allinpaysafe.app.service.CoreService;
import com.allinpaysafe.app.ui.HomeActivity;
import com.allinpaysafe.app.utils.AppUtils;
import com.allinpaysafe.app.utils.LogUtil;
import com.allinpaysafe.app.utils.TextFormater;
import com.allinpaysafe.app.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

/**
 *
 */
public class CircularLoaderPresenter
        implements Presenter, CoreService.OnProcessActionListener {

    private CircularLoaderView mCircularLoaderView;
    private final Context mContext;
    private long sum, available;
    private float percent;
    private static final int IS_NORMAL = 101;
//    private MenuListAdapter recyclerAdapter;
    private Timer mTimer;

    public CircularLoaderPresenter(Context mContext) {
        this.mContext = mContext;
    }

//    private CoreService mCoreService;

//    private ServiceConnection mServiceConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            mCoreService
//                    = ((CoreService.ProcessServiceBinder) service).getService();
//            mCoreService.setOnActionListener(CircularLoaderPresenter.this);
//            mCoreService.cleanAllProcess();
//            //  updateStorageUsage();
//
//        }
//
//
//        @Override public void onServiceDisconnected(ComponentName name) {
//            mCoreService.setOnActionListener(null);
//            mCoreService = null;
//        }
//    };


//    @Inject
//    public CircularLoaderPresenter(@ContextLifeCycle("Activity") Context context) {
//        this.mContext = context;
//        //this.mPreferenceUtils = preferenceUtils;
//    }

    @Override
    public void attachView(View v) {
        this.mCircularLoaderView = (HomeActivity) v;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
//        initViews();
//        setTimeTask();
    }


    private void initViews() {
//        ArrayList<Menu> menus = new ArrayList<>();
//        menus.add(new Menu.Builder(mContext).content("内存加速")
//                                            .icon(R.drawable.card_icon_speedup)
//                                            .build());
//        menus.add(new Menu.Builder(mContext).content("垃圾清理")
//                                            .icon(R.drawable.card_icon_trash)
//                                            .build());
//        menus.add(new Menu.Builder(mContext).content("自启管理")
//                                            .icon(R.drawable.card_icon_autorun)
//                                            .build());
//        menus.add(new Menu.Builder(mContext).content("软件管理")
//                                            .icon(R.drawable.card_icon_media)
//                                            .build());
//        recyclerAdapter = new MenuListAdapter(menus, mContext);
//        recyclerAdapter.setOnInViewClickListener(R.id.card_item_root,
//                new BaseRecyclerViewAdapter.onInternalClickListenerImpl<Menu>() {
//                    @Override
//                    public void OnClickListener(android.view.View parentV, android.view.View v, Integer position, Menu values) {
//                        super.OnClickListener(parentV, v, position, values);
//                        onRecyclerViewItemClick(position, values);
//                    }
//                });
        //recyclerAdapter.setOnInViewClickListener(R.id.note_more,
        //        new BaseRecyclerViewAdapter.onInternalClickListenerImpl<SNote>() {
        //            @Override
        //            public void OnClickListener(View parentV, View v, Integer position, SNote values) {
        //                super.OnClickListener(parentV, v, position, values);
        //                mainPresenter.showPopMenu(v, position, values);
        //            }
        //        });
//        recyclerAdapter.setFirstOnly(false);
//        recyclerAdapter.setDuration(300);
//        mCircularLoaderView.initViews(recyclerAdapter);
        mCircularLoaderView.initViews();
    }


    public void onRecyclerViewItemClick(int position, Menu value) {
        switch (position) {
            case 0:
//                mContext.startActivity(new Intent(mContext, MemoryClean.class));
                break;
            case 1:
//                mContext.startActivity(
//                        new Intent(mContext, RubbishClean.class));
                break;
            case 2:
//                mContext.startActivity(
//                        new Intent(mContext, AutoStartManage.class));
                break;
            case 3:
//                mContext.startActivity(new Intent(mContext, AppManage.class));
                break;
            default:
                break;
        }
    }

    /**
     * 清理内存
     */
    public void cleanMemory() {
//        mContext.bindService(new Intent(mContext, CoreService.class),
//                mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private Handler mHandler = new Handler() {
        @SuppressLint("HandlerLeak") public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case IS_NORMAL:
                    mCircularLoaderView.updateViews(sum, available, percent);
                    break;
                default:
                    ToastUtil.ToastShort(mContext, msg.obj.toString());
                    break;
            }
        }
    };

    /**
     * 定时器 读取内存的使用情况
     */
    public void setTimeTask() {
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override public void run() {
                Message msg = Message.obtain();
                try {
                    LogUtil.d("time task=");
                    sum = AppUtils.getTotalMemory();
                    available = AppUtils.getAvailMemory(mContext);
                    percent = AppUtils.getPercent(mContext);
                    msg.what = IS_NORMAL;
                    mHandler.sendMessage(msg);
                } catch (Exception e) {
                    msg.what = 3;
                    msg.obj = e.toString();
                    mHandler.sendMessage(msg);
                }
            }
        }, 0, 1000);
    }


    @Override public void onResume() {

    }


    @Override public void onStart() {
        setTimeTask();
    }


    @Override public void onPause() {

    }


    @Override public void onStop() {

    }


    @Override public void onDestroy() {
        mTimer.cancel();
    }


    @Override public void onScanStarted(Context context) {

    }


    @Override
    public void onScanProgressUpdated(Context context, int current, int max, long memory, String processName) {

    }


    @Override
    public void onScanCompleted(Context context, List<AppProcessInfo> apps) {

    }


    @Override public void onCleanStarted(Context context) {
        mCircularLoaderView.onCleanStarted(context);
    }


    @Override public void onCleanCompleted(Context context, long cacheSize) {
        ToastUtil.ToastShort(context, "已清理内存" + TextFormater.dataSizeFormat(cacheSize));
        mCircularLoaderView.onCleanCompleted(context, cacheSize);
//        mContext.unbindService(mServiceConnection);
    }
}
