package com.pax.ipp.tools.service;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import com.jaredrummler.android.processes.AndroidProcesses;
import com.pax.ipp.tools.Constant;
import com.pax.ipp.tools.R;
import com.pax.ipp.tools.model.AppProcessInfo;
import com.pax.ipp.tools.model.FlowModel;
import com.pax.ipp.tools.utils.AppUtils;
import com.pax.ipp.tools.utils.DateUtil;
import com.pax.ipp.tools.utils.FlowUtil;
import com.pax.ipp.tools.utils.LogUtil;
import com.pax.ipp.tools.utils.NetWorkUtil;
import com.pax.ipp.tools.utils.SharedPreUtils;
import com.pax.ipp.tools.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by houwen.lai on 2017/9/8.
 * 保存 流量的 service
 */

public class SaveFlowService extends Service {

    public class CleanerServiceBinder extends Binder {

        public SaveFlowService getService() {
            return SaveFlowService.this;
        }
    }

    private CleanerServiceBinder mBinder = new CleanerServiceBinder();

    Context mContext;
    String time_temp =  DateUtil.getToday();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();

        try {
            activityManager = (ActivityManager) getSystemService(
                    Context.ACTIVITY_SERVICE);
            packageManager = mContext.getPackageManager();
        } catch (Exception e) {

        }
    }

    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {
        if (intent!=null)
        time_temp= intent.getStringExtra(Constant.TIME_TEMP);
        else  time_temp= DateUtil.getToday();
        LogUtil.e("flow===","save flow service");
        saveFlowDate(time_temp);

        setTimeTask();

//        synchronized (SaveProcessFlow.class){
//            new SaveProcessFlow().execute(time_temp);
//        }

        return super.onStartCommand(intent, flags, startId);

    }
    SaveProcessFlow saveProcessFlow=null;
    public void saveFlowDate(String date){
        time_temp= date;
        LogUtil.e("flow===","save flow saveFlowDate");
        synchronized (SaveProcessFlow.class){
            new SaveProcessFlow().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,time_temp);
        }
//        new SaveProcessFlow().execute(time_temp);
    }

    ActivityManager activityManager = null;
    List<AppProcessInfo> list = null;
    PackageManager packageManager = null;
    /**
     *  遍历所以的进程 查询uid 流量
     */
    private class SaveProcessFlow extends AsyncTask<Object, Object,String> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(Object... params) {
            String time_p = (String) params[0];
            LogUtil.e("flow_time-保存时间-",(String) params[0]+"");
            String time_last = SharedPreUtils.getInstanse().getKeyValue(mContext,Constant.Time_Availble_Last);
            LogUtil.e("flow_last time=",time_last+"");

            String netType = NetWorkUtil.isWifiConnected(mContext)?Constant.NET_TYPE_wifi:Constant.NET_TYPE_GPRS;

//            FlowUtil.getInstance().saveFlowDate(mContext,netType,time_p,time_last);//保存总流量

            FlowModel model=null;
            ApplicationInfo appInfo = null;
            AppProcessInfo abAppProcessInfo = null;

            long sum=0;//所有app的流量
            //得到所有正在运行的进程
            List<ActivityManager.RunningAppProcessInfo> appProcessList
                    = AndroidProcesses.getRunningAppProcessInfo(mContext);
            publishProgress(0, appProcessList.size(), 0, "开始扫描");

            LogUtil.e("flow_size=",appProcessList.size()+"");

            for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessList) {
                abAppProcessInfo = new AppProcessInfo(
                        appProcessInfo.processName, appProcessInfo.pid,
                        appProcessInfo.uid);
                model=new FlowModel();
                String packName = appProcessInfo.processName;
                try {
                    appInfo = packageManager.getApplicationInfo(
                            appProcessInfo.processName, 0);

                    if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                        abAppProcessInfo.isSystem = true;
                    }
                    else {
                        abAppProcessInfo.isSystem = false;
                    }
                    Drawable icon = appInfo.loadIcon(packageManager);
                    String appName = appInfo.loadLabel(packageManager)
                            .toString();
                    abAppProcessInfo.icon = icon;
                    abAppProcessInfo.appName = appName;

                    model.setmIcon(icon);
                    model.setmPackageName(packName);
                    model.setmApplicationName(appName);

//                    FlowUtil.getInstance().saveFlowDate(mContext,time_p);//保存总流量
                    sum +=  FlowUtil.getInstance().saveFlowByUid(mContext,appProcessInfo.uid,packName,
                            appName,netType,time_p,time_last);

//                    LogUtil.e("flow","uid="+appProcessInfo.uid+" appName="+appName+" flows=");

                    publishProgress(0, "a", 0, "开始扫描");
                    //abAppProcessInfo.packName = packName;
                } catch (PackageManager.NameNotFoundException e) {

                }finally {

                }
            }
            //保存各个app的值
            FlowUtil.getInstance().saveFlowUidBytes(mContext,Constant.flowHistoryList);
            //保存总流量
            FlowUtil.getInstance().saveFlowDate(mContext,netType,time_p,time_last,sum);//保存总流量

            //记录保存的时间
            SharedPreUtils.getInstanse().putKeyValue(mContext,Constant.Time_Availble_Last,time_temp);
            return "a";
        }

        @Override
        protected void onProgressUpdate(Object... values) {

        }

        @Override
        protected void onPostExecute(String s) {
            LogUtil.e("flow___ok-save-end","保存成功sssssss");
//            mContext.unbindService((ServiceConnection) mBinder);
            onCancelled();
        }
    }

    private Timer mTimer;
    private static final int IS_NORMAL = 104;
    private Handler mHandler = new Handler() {
        @SuppressLint("HandlerLeak") public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case IS_NORMAL:
                    LogUtil.e("time_time","定时器保存定时器保存定时器保存定时器保存");
                    saveFlowDate(DateUtil.getToday());
                    break;
                default:
//                    ToastUtil.ToastShort(mContext, msg.obj.toString());
                    break;
            }
        }
    };
    /**
     * 定时器 读取内存的使用情况
     */
    public void setTimeTask() {
        if (mTimer!=null)return;
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override public void run() {
                Message msg = Message.obtain();
                try {
                    LogUtil.d("time task=");
                    msg.what = IS_NORMAL;
                    mHandler.sendMessage(msg);
                } catch (Exception e) {
                    LogUtil.d("time task=error");
                    msg.what = 3;
                    msg.obj = e.toString();
                    mHandler.sendMessage(msg);
                }
            }
        }, 0, 1*60*1000);
        //每10s刷一次
    }
}
