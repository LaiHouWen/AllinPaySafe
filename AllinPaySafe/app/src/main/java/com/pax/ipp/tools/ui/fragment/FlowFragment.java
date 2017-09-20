package com.pax.ipp.tools.ui.fragment;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.jaredrummler.android.processes.AndroidProcesses;
import com.pax.ipp.tools.Constant;
import com.pax.ipp.tools.R;
import com.pax.ipp.tools.adapter.FlowListAdapter;
import com.pax.ipp.tools.model.AppProcessInfo;
import com.pax.ipp.tools.model.BaseFlowModel;
import com.pax.ipp.tools.model.FlowModel;
import com.pax.ipp.tools.mvp.impl.Presenter;
import com.pax.ipp.tools.ui.base.BaseFragment;
import com.pax.ipp.tools.ui.view.FixedRecyclerView;
import com.pax.ipp.tools.ui.view.Loading_view;
import com.pax.ipp.tools.utils.DateUtil;
import com.pax.ipp.tools.utils.FlowUtil;
import com.pax.ipp.tools.utils.LogUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by houwen.lai on 2017/9/7.
 */

public class FlowFragment extends BaseFragment {

    @BindView(R.id.recyclerView)
    FixedRecyclerView recyclerView;

    FlowListAdapter flowListAdapter;

    Loading_view loading_view;

    List<FlowModel> lists=new ArrayList<FlowModel>();

    final String FLAG_TIME = "flag_time";

    boolean flag = false;

//    public static FlowFragment newInstance(boolean flag) {
//        Bundle arguments = new Bundle();
//        arguments.putBoolean(FLAG_TIME,flag);
//        FlowFragment fragment = new FlowFragment();
//        fragment.setArguments(arguments);
//        return fragment;
//    }

    @Override
    protected int getLayoutView() {
        return R.layout.fixed_recycler_view;
    }


    @Override
    protected Presenter getPresenter() {
        try {
            activityManager = (ActivityManager) mContext.getSystemService(
                    Context.ACTIVITY_SERVICE);
            packageManager = mContext.getPackageManager();
        } catch (Exception e) {

        }

        loading_view = new Loading_view(mContext,R.style.CustomDialog);

        flowListAdapter = new FlowListAdapter(lists);
        flowListAdapter.setFirstOnly(false);
        flowListAdapter.setDuration(300);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL,
                        false));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(flowListAdapter);
        return null;
    }

    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        if (isVisible && !flag){

//            scanChache();
            getFlowApp();
//            new ScanAllProcessFlow().execute(getArguments().getLong(TEMP_TIME));//查询时间
        }else {

        }
    }

    ActivityManager activityManager = null;
    List<AppProcessInfo> list = null;
    PackageManager packageManager = null;
    /**
     *  遍历所以的进程 查询uid 流量
     */
    private class ScanAllProcessFlow extends AsyncTask<Object, Object,List<FlowModel>> {

        @Override
        protected void onPreExecute() {
            loading_view.show();
        }

        @Override
        protected List<FlowModel> doInBackground(Object... params) {
            list = new ArrayList<>();
            lists = new ArrayList<FlowModel>();
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

//                    long flows= FlowUtil.getInstance().getTodayBytesByUid(mContext,appProcessInfo.uid,packName);
                    long flows=0;
//                   long flows= FlowUtil.getBytesByUid(mContext,appProcessInfo.uid,
//                            FlowUtil.getTimesMonthMorning(),System.currentTimeMillis());
                    LogUtil.e("flow","uid="+appProcessInfo.uid+" appName="+appName+" flows="+ flows);
                    model.setFlowSize(flows);
                    publishProgress(0, lists.size(), 0, "开始扫描");
                    lists.add(model);
                    //abAppProcessInfo.packName = packName;
                } catch (PackageManager.NameNotFoundException e) {
                    abAppProcessInfo.icon = mContext.getResources()
                            .getDrawable(
                                    R.mipmap.ic_launcher);
                    //String packName = appProcessInfo.processName;
                    appInfo = getApplicationInfo(
                            appProcessInfo.processName.split(":")[0]);
                    if (appInfo != null) {
                        Drawable icon = appInfo.loadIcon(packageManager);
                        abAppProcessInfo.icon = icon;
                        packName = appProcessInfo.processName.split(":")[0];
                        model.setmIcon(icon);
                    }
                    abAppProcessInfo.isSystem = true;
                    abAppProcessInfo.appName = appProcessInfo.processName;

                    model.setmPackageName(packName);
                    model.setmApplicationName(appProcessInfo.processName);
                    //abAppProcessInfo.packName = packName;
                    lists.add(model);
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    abAppProcessInfo.packName = packName;
                    long memory = activityManager.getProcessMemoryInfo(new int[] {
                            appProcessInfo.pid })[0].getTotalPrivateDirty() * 1024;
                    abAppProcessInfo.memory = memory;

                    lists.add(model);
                }

            }
            LogUtil.e("flow___",lists.size()+"");
            return lists;
        }

        @Override
        protected void onProgressUpdate(Object... values) {

        LogUtil.e("flow===",
                Integer.parseInt(values[1]+"")+""+ values[3] + "");
        }

        @Override
        protected void onPostExecute(List<FlowModel> flowModels) {
            loading_view.dismiss();
            lists.clear();
            LogUtil.e("flow",flowModels.size()+"");
            lists.addAll(flowModels);
            flowListAdapter.setList(flowModels);
            flowListAdapter.notifyDataSetChanged();
        }

    }

    public ApplicationInfo getApplicationInfo(String processName) {
        if (processName == null) {
            return null;
        }
        List<ApplicationInfo> appList = packageManager.getInstalledApplications(
                PackageManager.GET_UNINSTALLED_PACKAGES);
        for (ApplicationInfo appInfo : appList) {
            if (processName.equals(appInfo.processName)) {
                return appInfo;
            }
        }
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     *
     */
    public void getFlowApp(){
        loading_view.show();
        Observable<List<FlowModel>> appProcessInfoObservable = Observable.create(
                new Observable.OnSubscribe<List<FlowModel>>() {
                    @Override
                    public void call(Subscriber<? super List<FlowModel>> subscriber) {
                        ArrayList<FlowModel> listss = new ArrayList<FlowModel>();
                        FlowModel model=null;
                        ApplicationInfo appInfo = null;
                        PackageManager packageManager =mContext.getPackageManager();
                        Map<String,Map<String,BaseFlowModel>> flowHistoryList=new HashMap<String,Map<String,BaseFlowModel>>();//所有的历史流量信息
//                        flowHistoryList = FlowUtil.getInstance().getFlowTotailUid(mContext);
                        flowHistoryList.clear();
                        flowHistoryList.putAll(Constant.flowHistoryList);
                        LogUtil.d("fragment_f",Constant.flowHistoryList==null?"null":Constant.flowHistoryList.toString());
                            if (flowHistoryList != null) {
                                Iterator iter = flowHistoryList.entrySet().iterator();
                                while (iter.hasNext()) {
                                    Map.Entry entry = (Map.Entry) iter.next();
                                    String packageName = (String) entry.getKey();
                                    Map<String, BaseFlowModel> appValues = (Map<String, BaseFlowModel>) entry.getValue();
                                    LogUtil.d("TrafficFlowBean", "-packageName=" + packageName + "  appValues=" + appValues.toString());

                                    long flows = 0;
                                    BaseFlowModel baseFlowModel=FlowUtil.getInstance().getBytesByUidOneDay(mContext,flowHistoryList,packageName, DateUtil.getToday());
                                   if (baseFlowModel==null){
                                       flows=0;
                                   }else {
                                       if (getArguments().getBoolean(FLAG_TIME)) {//今日
//                                        flows = FlowUtil.getInstance().getTodayBytesByUid(mContext,flowHistoryList,packageName);
                                           flows = baseFlowModel.getFlowMobileD() + baseFlowModel.getFlowWifiD();
                                       } else {//月
//                                        flows = FlowUtil.getInstance().getMonthBytesByUid(mContext,flowHistoryList,packageName);
                                           flows = baseFlowModel.getFlowSize();
                                       }
                                   }
                                    LogUtil.e("flow_fragment",  " packageName=" + packageName + " flows=" + flows);

                                    ApplicationInfo application= null;
                                    Drawable d = null;
                                    String appName="";
                                    try {
                                        application = packageManager.getPackageInfo(packageName, 0).applicationInfo;
                                        d = application.loadIcon(packageManager);
                                        appName = application.loadLabel(packageManager).toString();
                                        FlowModel models=new FlowModel(packageName,appName,d,flows);
                                        listss.add(models);
                                    } catch (PackageManager.NameNotFoundException e) {
                                        e.printStackTrace();
                                    }
                            }
                        }
                        subscriber.onNext(listss);
                    }
                });
        Observer<List<FlowModel>> observer = new Observer<List<FlowModel>>() {
            @Override
            public void onCompleted() {
                loading_view.dismiss();
            }

            @Override
            public void onError(Throwable e) {
                loading_view.dismiss();
            }

            @Override
            public void onNext(List<FlowModel> flowModelList) {
                loading_view.dismiss();
                if(flowModelList==null||flowModelList.size()==0){
                    flag=false;
                }else flag=true;

                lists.clear();

                // 排序
                Collections.sort(flowModelList,
                        new Comparator<FlowModel>() {
                            @Override
                            public int compare(FlowModel o1, FlowModel o2) {
                                return new Double(o1.getFlowSize()).compareTo(new Double(o2.getFlowSize()));
                            }}
                );
                Collections.reverse(flowModelList);


                LogUtil.e("flow_fragment",flowModelList.size()+"");
                lists.addAll(flowModelList);
                flowListAdapter.setList(flowModelList);
                flowListAdapter.notifyDataSetChanged();

            }
        };

        appProcessInfoObservable.subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(
                observer );
    }
}
