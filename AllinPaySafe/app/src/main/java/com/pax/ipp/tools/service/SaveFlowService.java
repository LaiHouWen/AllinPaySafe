package com.pax.ipp.tools.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import com.jaredrummler.android.processes.AndroidProcesses;
import com.pax.ipp.tools.Constant;
import com.pax.ipp.tools.R;
import com.pax.ipp.tools.model.AppProcessInfo;
import com.pax.ipp.tools.model.FlowModel;
import com.pax.ipp.tools.utils.FlowUtil;
import com.pax.ipp.tools.utils.LogUtil;
import com.pax.ipp.tools.utils.SharedPreUtils;

import java.util.ArrayList;
import java.util.List;

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
    long time_temp = 0;

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
        time_temp= intent.getLongExtra(Constant.TIME_TEMP,System.currentTimeMillis());
        else  time_temp=System.currentTimeMillis();
        LogUtil.e("flow===","save flow service");

        synchronized (SaveProcessFlow.class){
            new SaveProcessFlow().execute(time_temp);
        }

        return super.onStartCommand(intent, flags, startId);

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

            Log.e("flow_bao cun","save flow onPreExecute");
        }

        @Override
        protected String doInBackground(Object... params) {
            long time_p = (long)params[0];
            LogUtil.e("flow_time-保存时间-",(long)params[0]+"");

            FlowUtil.getInstance().saveFlowDate(mContext,time_p);//保存总流量

            FlowModel model=null;
            ApplicationInfo appInfo = null;
            AppProcessInfo abAppProcessInfo = null;
            //得到所有正在运行的进程
            List<ActivityManager.RunningAppProcessInfo> appProcessList
                    = AndroidProcesses.getRunningAppProcessInfo(mContext);
            publishProgress(0, appProcessList.size(), 0, "开始扫描");

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
                    FlowUtil.getInstance().saveFlowByUid(mContext,appProcessInfo.uid,time_p);

                    LogUtil.e("flow","uid="+appProcessInfo.uid+" appName="+appName+" flows=");

                    publishProgress(0, "a", 0, "开始扫描");
                    //abAppProcessInfo.packName = packName;
                } catch (PackageManager.NameNotFoundException e) {

                }finally {

                }
            }
            FlowUtil.getInstance().saveFlowUidBytes(mContext);
            LogUtil.e("flow___ok-save","保存成功");
            return "a";
        }

        @Override
        protected void onProgressUpdate(Object... values) {

        }

        @Override
        protected void onPostExecute(String s) {
            LogUtil.e("flow___ok-save-end","保存成功sssssss");
        }

    }
}
