package com.pax.ipp.tools.ui.fragment;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.jaredrummler.android.processes.AndroidProcesses;
import com.pax.ipp.tools.R;
import com.pax.ipp.tools.adapter.FlowListAdapter;
import com.pax.ipp.tools.model.AppProcessInfo;
import com.pax.ipp.tools.model.FlowModel;
import com.pax.ipp.tools.mvp.impl.Presenter;
import com.pax.ipp.tools.ui.base.BaseFragment;
import com.pax.ipp.tools.ui.view.FixedRecyclerView;
import com.pax.ipp.tools.ui.view.Loading_view;
import com.pax.ipp.tools.utils.FlowUtil;
import com.pax.ipp.tools.utils.LogUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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

    static final String TEMP_TIME = "time";

    boolean flag = false;

    public static FlowFragment newInstance(long startTime) {
        Bundle arguments = new Bundle();
        arguments.putLong(TEMP_TIME,startTime);
        FlowFragment fragment = new FlowFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

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
        super.onFragmentVisibleChange(isVisible);
        if (isVisible &!flag){

            scanChache();
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

                    long flows= FlowUtil.getSystemBytesByUid(mContext,appProcessInfo.uid,
                            FlowUtil.getTimesMonthMorning(),System.currentTimeMillis());
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
                } catch (RemoteException e) {
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
    public void scanChache(){
        loading_view.show();
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
//                            abAppProcessInfo = new AppProcessInfo(
//                                    appProcessInfo.processName, appProcessInfo.pid,
//                                    appProcessInfo.uid);
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
//                                abAppProcessInfo.icon = icon;
//                                abAppProcessInfo.appName = appName;

                                model.setmIcon(icon);
                                model.setmPackageName(packName);
                                model.setmApplicationName(appName);

                                long flows = FlowUtil.getSystemBytesByUid(mContext, appProcessInfo.uid,
                                        FlowUtil.getTimesMonthMorning(), System.currentTimeMillis());
//                   long flows= FlowUtil.getBytesByUid(mContext,appProcessInfo.uid,
//                            FlowUtil.getTimesMonthMorning(),System.currentTimeMillis());
                                LogUtil.e("flow", "uid=" + appProcessInfo.uid + " appName=" + appName + " flows=" + flows);
                                model.setFlowSize(flows);
//                                publishProgress(0, lists.size(), 0, "开始扫描");
                                if (flows>0){
                                    listss.add(model);
                                }

                                //abAppProcessInfo.packName = packName;
                            } catch (PackageManager.NameNotFoundException e) {
//                                abAppProcessInfo.icon = mContext.getResources()
//                                        .getDrawable(
//                                                R.mipmap.ic_launcher);
                                //String packName = appProcessInfo.processName;
                                appInfo = getApplicationInfo(
                                        appProcessInfo.processName.split(":")[0]);
                                if (appInfo != null) {
                                    Drawable icon = appInfo.loadIcon(packageManager);
//                                    abAppProcessInfo.icon = icon;
                                    packName = appProcessInfo.processName.split(":")[0];
                                    model.setmIcon(icon);
                                }
//                                abAppProcessInfo.isSystem = true;
//                                abAppProcessInfo.appName = appProcessInfo.processName;

                                model.setmPackageName(packName);
                                model.setmApplicationName(appProcessInfo.processName);
                                //abAppProcessInfo.packName = packName;
                                listss.add(model);
                            } catch (RemoteException e) {
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
                loading_view.dismiss();
                flag=true;
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


                LogUtil.e("flow",flowModelList.size()+"");
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
