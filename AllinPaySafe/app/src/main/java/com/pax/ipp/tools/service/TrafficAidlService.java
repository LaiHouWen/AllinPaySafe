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
import android.support.annotation.Nullable;

import com.jaredrummler.android.processes.AndroidProcesses;
import com.pax.ipp.tools.TrafficFlowBean;
import com.pax.ipp.tools.Constant;
import com.pax.ipp.tools.TrafficFlowManager;
import com.pax.ipp.tools.model.BaseFlowModel;
import com.pax.ipp.tools.model.FlowModel;
import com.pax.ipp.tools.utils.AppUtils;
import com.pax.ipp.tools.utils.DateUtil;
import com.pax.ipp.tools.utils.FlowUtil;
import com.pax.ipp.tools.utils.LogUtil;
import com.pax.ipp.tools.utils.NetWorkUtil;
import com.pax.ipp.tools.utils.SharedPreUtils;

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

public class TrafficAidlService extends Service {

    ActivityManager activityManager = null;
    PackageManager packageManager = null;
    Context mContext;

    private Binder mBinder = new TrafficFlowManager.Stub() {
        @Override
        public List<TrafficFlowBean> getDayFlow() throws RemoteException {
            LogUtil.e("AppFlow_service","getDayFlow");
//            if (Constant.flowHistoryList==null)return null;
            String time_last = SharedPreUtils.getInstanse().getKeyValue(mContext,Constant.Time_Availble_Last);
            String netType = NetWorkUtil.isWifiConnected(mContext)?Constant.NET_TYPE_wifi:Constant.NET_TYPE_GPRS;
            //保存数据
//            FlowUtil.getInstance().saveFlowDate(mContext,netType,DateUtil.getToday(),time_last);
//            FlowUtil.getInstance().saveFlowUidBytes(mContext,Constant.flowHistoryList);

            List<TrafficFlowBean> list=new ArrayList<TrafficFlowBean>();
            Map<String,Map<String,BaseFlowModel>> flowHistoryList=new HashMap<String,Map<String,BaseFlowModel>>();//所有的历史流量信息
            flowHistoryList = FlowUtil.getInstance().getFlowTotailUid(mContext);
            Iterator iter = flowHistoryList.entrySet().iterator();
            while (iter.hasNext()){
                Map.Entry entry = (Map.Entry) iter.next();
                String packageName = (String) entry.getKey();
                Map<String,BaseFlowModel> appValues = (Map<String, BaseFlowModel>) entry.getValue();
                LogUtil.d("TrafficFlowBean","-packageName="+packageName+"  appValues="+appValues.toString());
                long mobileFlow=0;
                long wifiFlow=0;
                BaseFlowModel baseFlowModel = FlowUtil.getInstance().getBytesByUidOneDay(mContext,flowHistoryList,packageName,DateUtil.getToday());
//                if (appValues!=null&&appValues.containsKey(DateUtil.getToday())){
//                    mobileFlow=FlowUtil.getInstance().getTodayBytesByUidMobile(mContext,flowHistoryList,packageName);
//                    wifiFlow=FlowUtil.getInstance().getTodayBytesByUidWifi(mContext,flowHistoryList,packageName);
//                }
                if (baseFlowModel != null) {
                    mobileFlow=baseFlowModel.getFlowMobileD();
                    wifiFlow=baseFlowModel.getFlowWifiD();
                }
                TrafficFlowBean trafficFlowBean =new TrafficFlowBean(DateUtil.getToday(),
                        AppUtils.getAppNameByPackageName(mContext,packageName),
                        packageName,mobileFlow,wifiFlow);
                list.add(trafficFlowBean);
            }
            return list;
        }

        @Override
        public List<TrafficFlowBean> getWeekFlow() throws RemoteException {
            LogUtil.e("AppFlow_service","getWeekFlow");
//            if (Constant.flowHistoryList==null)return null;
            String time_last = SharedPreUtils.getInstanse().getKeyValue(mContext,Constant.Time_Availble_Last);
            String netType = NetWorkUtil.isWifiConnected(mContext)?Constant.NET_TYPE_wifi:Constant.NET_TYPE_GPRS;
            //保存数据
//            FlowUtil.getInstance().saveFlowDate(mContext,netType,DateUtil.getToday(),time_last);
//            FlowUtil.getInstance().saveFlowUidBytes(mContext,Constant.flowHistoryList);


            List<TrafficFlowBean> list=new ArrayList<TrafficFlowBean>();
            Map<String,Map<String,BaseFlowModel>> flowHistoryList=new HashMap<String,Map<String,BaseFlowModel>>();//所有的历史流量信息
            flowHistoryList = FlowUtil.getInstance().getFlowTotailUid(mContext);
            Iterator iter = flowHistoryList.entrySet().iterator();
            while (iter.hasNext()){
                Map.Entry entry = (Map.Entry) iter.next();
                String packageName = (String) entry.getKey();
                Map<String,BaseFlowModel> appValues = (Map<String, BaseFlowModel>) entry.getValue();
                LogUtil.d("TrafficFlowBean","-packageName="+packageName+"  appValues="+appValues.toString());
                long mobileFlow=0;
                long wifiFlow=0;
                BaseFlowModel bFlow_1 = FlowUtil.getInstance().getBytesByUidOneDay(mContext,flowHistoryList,packageName,DateUtil.getToday());
                BaseFlowModel bFlow_2 = FlowUtil.getInstance().getBytesByUidOneDay(mContext,flowHistoryList,packageName,DateUtil.getLastDay(1));
                BaseFlowModel bFlow_3 = FlowUtil.getInstance().getBytesByUidOneDay(mContext,flowHistoryList,packageName,DateUtil.getLastDay(2));
                BaseFlowModel bFlow_4 = FlowUtil.getInstance().getBytesByUidOneDay(mContext,flowHistoryList,packageName,DateUtil.getLastDay(3));
                BaseFlowModel bFlow_5 = FlowUtil.getInstance().getBytesByUidOneDay(mContext,flowHistoryList,packageName,DateUtil.getLastDay(4));
                BaseFlowModel bFlow_6 = FlowUtil.getInstance().getBytesByUidOneDay(mContext,flowHistoryList,packageName,DateUtil.getLastDay(5));
                BaseFlowModel bFlow_7 = FlowUtil.getInstance().getBytesByUidOneDay(mContext,flowHistoryList,packageName,DateUtil.getLastDay(6));

//                if (appValues!=null&&appValues.containsKey(DateUtil.getToday())){
//                    mobileFlow=FlowUtil.getInstance().getTodayBytesByUidMobile(mContext,flowHistoryList,packageName);
//                    wifiFlow=FlowUtil.getInstance().getTodayBytesByUidWifi(mContext,flowHistoryList,packageName);
//                }

                mobileFlow=(bFlow_1==null?0:bFlow_1.getFlowMobileD())+(bFlow_2==null?0:bFlow_2.getFlowMobileD())+
                        (bFlow_3==null?0:bFlow_3.getFlowMobileD())+(bFlow_4==null?0:bFlow_4.getFlowMobileD())+
                        (bFlow_5==null?0:bFlow_5.getFlowMobileD())+(bFlow_6==null?0:bFlow_6.getFlowMobileD())+
                        (bFlow_7==null?0:bFlow_7.getFlowMobileD());
                wifiFlow=(bFlow_1==null?0:bFlow_1.getFlowWifiD())+(bFlow_2==null?0:bFlow_2.getFlowWifiD())+
                        (bFlow_3==null?0:bFlow_3.getFlowWifiD())+(bFlow_4==null?0:bFlow_4.getFlowWifiD())+
                        (bFlow_5==null?0:bFlow_5.getFlowWifiD())+(bFlow_6==null?0:bFlow_6.getFlowWifiD())+
                        (bFlow_7==null?0:bFlow_7.getFlowWifiD());

                TrafficFlowBean trafficFlowBean =new TrafficFlowBean(DateUtil.getToday(),
                        AppUtils.getAppNameByPackageName(mContext,packageName),
                        packageName,mobileFlow,wifiFlow);
                list.add(trafficFlowBean);
            }
            return list;
        }

        @Override
        public List<TrafficFlowBean> getMonthFlow() throws RemoteException {
            LogUtil.e("AppFlow_service","getMonthFlow");
//            if (Constant.flowHistoryList==null)return null;
            String time_last = SharedPreUtils.getInstanse().getKeyValue(mContext,Constant.Time_Availble_Last);
            String netType = NetWorkUtil.isWifiConnected(mContext)?Constant.NET_TYPE_wifi:Constant.NET_TYPE_GPRS;
            //保存数据
//            FlowUtil.getInstance().saveFlowDate(mContext,netType,DateUtil.getToday(),time_last);
//            FlowUtil.getInstance().saveFlowUidBytes(mContext,Constant.flowHistoryList);

            List<TrafficFlowBean> list=new ArrayList<TrafficFlowBean>();
            Map<String,Map<String,BaseFlowModel>> flowHistoryList=new HashMap<String,Map<String,BaseFlowModel>>();//所有的历史流量信息
            flowHistoryList = FlowUtil.getInstance().getFlowTotailUid(mContext);
            Iterator iter = flowHistoryList.entrySet().iterator();
            while (iter.hasNext()){
                Map.Entry entry = (Map.Entry) iter.next();
                String packageName = (String) entry.getKey();
                Map<String,BaseFlowModel> appValues = (Map<String, BaseFlowModel>) entry.getValue();
                LogUtil.d("TrafficFlowBean","-packageName="+packageName+"  appValues="+appValues.toString());
                long mobileFlow=0;
                long wifiFlow=0;
                BaseFlowModel baseFlowModel = FlowUtil.getInstance().getBytesByUidOneDay(mContext,flowHistoryList,packageName,DateUtil.getToday());
//                if (appValues!=null&&appValues.containsKey(DateUtil.getToday())){
//                    mobileFlow=FlowUtil.getInstance().getMonthBytesByUidMobile(mContext,flowHistoryList,packageName);
//                    wifiFlow=FlowUtil.getInstance().getMonthBytesByUidWifi(mContext,flowHistoryList,packageName);
//                }
                if (baseFlowModel!=null){
                    mobileFlow=baseFlowModel.getFlowMobile();
                    wifiFlow=baseFlowModel.getFlowWifi();
                }

                TrafficFlowBean trafficFlowBean =new TrafficFlowBean(DateUtil.getToday(),
                        AppUtils.getAppNameByPackageName(mContext,packageName),
                        packageName,mobileFlow,wifiFlow);
                list.add(trafficFlowBean);
            }
            return list;
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
//                                flows = FlowUtil.getInstance().getTodayBytesByUid(mContext,appProcessInfo.uid,appName);

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
