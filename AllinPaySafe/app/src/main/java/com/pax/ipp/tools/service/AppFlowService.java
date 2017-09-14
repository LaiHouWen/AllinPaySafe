package com.pax.ipp.tools.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.jaredrummler.android.processes.AndroidProcesses;
import com.pax.ipp.tools.AppFlow;
import com.pax.ipp.tools.AppFlowManager;
import com.pax.ipp.tools.Constant;
import com.pax.ipp.tools.model.AppProcessInfo;
import com.pax.ipp.tools.model.FlowModel;
import com.pax.ipp.tools.utils.AppUtils;
import com.pax.ipp.tools.utils.DateUtil;
import com.pax.ipp.tools.utils.FlowUtil;
import com.pax.ipp.tools.utils.LogUtil;
import com.pax.ipp.tools.utils.TextFormater;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by houwen.lai on 2017/9/12.
 * adil service
 */

public class AppFlowService extends Service {

    ActivityManager activityManager = null;
    PackageManager packageManager = null;
    Context mContext;

    private Binder mBinder = new AppFlowManager.Stub() {
        @Override
        public List<AppFlow> getAppFlowList() throws RemoteException {
            LogUtil.e("AppFlow_service","getAppFlowList");
            if (Constant.flowHistoryList==null)return null;
            FlowUtil.getInstance().saveFlowDate(mContext,DateUtil.getToday());
            FlowUtil.getInstance().saveFlowUidBytes(mContext);
            List<AppFlow> list=new ArrayList<AppFlow>();
            Map<String,Map<String,Long>> flowHistoryList=new HashMap<String,Map<String,Long>>();//所有的历史流量信息
            flowHistoryList = FlowUtil.getInstance().getFlowTotailUid(mContext);
            Iterator iter = flowHistoryList.entrySet().iterator();
            while (iter.hasNext()){
                Map.Entry entry = (Map.Entry) iter.next();
                String packageName = (String) entry.getKey();
                Map<String,Long> appValues = (Map<String, Long>) entry.getValue();
                LogUtil.d("AppFlow","-packageName="+packageName+"  appValues="+appValues.toString());
               long flows = 0;
                if (appValues!=null&&appValues.containsKey(DateUtil.getToday())){
//                    flows = appValues.get(DateUtil.getToday());
                    flows = FlowUtil.getInstance().getTodayBytesByUid(mContext,flowHistoryList,0,packageName);
                }
                AppFlow appFlow=new AppFlow(DateUtil.getToday(),
                        AppUtils.getAppNameByPackageName(mContext,packageName),
                        packageName, flows);
                list.add(appFlow);
            }

            return list;
        }

        @Override
        public String getAppFlowJsonString() throws RemoteException {
            LogUtil.e("AppFlow_service","getAppFlowJsonString");
            if (Constant.flowHistoryList==null)return null;
            List<AppFlow> list=new ArrayList<AppFlow>();
            Iterator iter = Constant.flowHistoryList.entrySet().iterator();
            while (iter.hasNext()){
                Map.Entry entry = (Map.Entry) iter.next();
                String packageName = (String) entry.getKey();
                Map<String,Long> appValues = (Map<String, Long>) entry.getValue();
                LogUtil.d("AppFlow","packageName="+packageName+"  appValues="+appValues.toString());
                long flows = 0;
                if (appValues!=null&&appValues.containsKey(DateUtil.getToday())){
                    flows = appValues.get(DateUtil.getToday());
                }
                AppFlow appFlow=new AppFlow(DateUtil.getToday(),
                        AppUtils.getAppNameByPackageName(mContext,packageName),
                        packageName, flows);
                list.add(appFlow);
            }
            Gson gson = new Gson();
            String res = gson.toJson(list);
            LogUtil.d("AppFlow","getAppFlowJsonString="+res);
            return res;
        }

        @Override
        public long getTodayAppFlow() throws RemoteException {
            LogUtil.e("AppFlow_service","getTodayAppFlow");
            //流量使用情况
            long flowToday =0;
            if (Constant.flowTodayMonth!=null&&Constant.flowTodayMonth.containsKey(DateUtil.getToday())) {
                flowToday = FlowUtil.getInstance().getTodayBytes(mContext);//今日消耗的流量
            }
            return flowToday;
        }

        @Override
        public long getMonthAppFlow() throws RemoteException {
            LogUtil.e("AppFlow_service","getMonthAppFlow");
            //流量使用情况
            long flowMonth=0;
            if (Constant.flowTodayMonth!=null&&Constant.flowTodayMonth.containsKey(DateUtil.getToday())) {
                flowMonth = FlowUtil.getInstance().getMonthBytes(mContext);//本月的总流量
            }
            return flowMonth;
        }
    };
    
    @Override
    public void onCreate() {
        super.onCreate();

        LogUtil.e("AppFlow_service","onCreate");

        mContext = getApplicationContext();

        try {
            activityManager = (ActivityManager) getSystemService(
                    Context.ACTIVITY_SERVICE);
            packageManager = mContext.getPackageManager();
        } catch (Exception e) {

        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        LogUtil.e("AppFlow_service","onBind");
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.e("AppFlow_service","onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 获取app的流量
     */
    public void getAppFlow(){
        Observable<List<FlowModel>> appProcessInfoObservable = Observable.create(
                new Observable.OnSubscribe<List<FlowModel>>() {
                    @Override
                    public void call(Subscriber<? super List<FlowModel>> subscriber) {
                        ArrayList<FlowModel> listss = new ArrayList<FlowModel>();
                        FlowModel model=null;
                        ApplicationInfo appInfo = null;
                        //得到所有正在运行的进程
                        List<ActivityManager.RunningAppProcessInfo> appProcessList
                                = AndroidProcesses.getRunningAppProcessInfo(mContext);

                        for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessList) {
//
                            model = new FlowModel();
                            String packName = appProcessInfo.processName;
                            try {
                                appInfo = packageManager.getApplicationInfo(
                                        appProcessInfo.processName, 0);

//                                if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
//                                    abAppProcessInfo.isSystem = true;
//                                } else {
//                                    abAppProcessInfo.isSystem = false;
//                                }
                                Drawable icon = appInfo.loadIcon(packageManager);
                                String appName = appInfo.loadLabel(packageManager)
                                        .toString();

                                model.setmIcon(icon);
                                model.setmPackageName(packName);
                                model.setmApplicationName(appName);

                                long flows = 0;
                                //今日
                                flows = FlowUtil.getInstance().getTodayBytesByUid(mContext,appProcessInfo.uid,appName);

                                LogUtil.e("flow", "uid=" + appProcessInfo.uid + " appName=" + appName + " flows=" + flows);
                                model.setFlowSize(flows);
//                                publishProgress(0, lists.size(), 0, "开始扫描");
                                if (flows>0){
                                    listss.add(model);
                                }
                                //abAppProcessInfo.packName = packName;
                            } catch (PackageManager.NameNotFoundException e) {
//
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                            }
                        }
                        subscriber.onNext(listss);
                    }
                });
        Observer<List<FlowModel>> observer = new Observer<List<FlowModel>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List<FlowModel> flowModelList) {


                // 排序
                Collections.sort(flowModelList,
                        new Comparator<FlowModel>() {
                            @Override
                            public int compare(FlowModel o1, FlowModel o2) {
                                return new Double(o1.getFlowSize()).compareTo(new Double(o2.getFlowSize()));
                            }}
                );
                Collections.reverse(flowModelList);


                LogUtil.e("flow",flowModelList.size()+"");

            }
        };

        appProcessInfoObservable.subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(
                observer );
    }

}
