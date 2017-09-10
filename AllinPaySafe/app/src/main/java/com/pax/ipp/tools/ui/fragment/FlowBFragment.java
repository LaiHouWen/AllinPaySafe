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
import java.util.List;

import butterknife.BindView;

/**
 * Created by Administrator on 2017/9/7.
 */

public class FlowBFragment extends BaseFragment {

    @BindView(R.id.recyclerView)
    FixedRecyclerView recyclerView;

    FlowListAdapter flowListAdapter;

    Loading_view loading_view;

    List<FlowModel> lists=new ArrayList<FlowModel>();

    static final String TEMP_TIME = "time";

    public static FlowBFragment newInstance(long startTime) {
        Bundle arguments = new Bundle();
        arguments.putLong(TEMP_TIME,startTime);
        FlowBFragment fragment = new FlowBFragment();
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
        if (isVisible){
            new FlowBFragment.ScanAllProcessFlow().execute(getArguments().getLong(TEMP_TIME));//查询时间
        }else {

        }

    }

    ActivityManager activityManager = null;
    List<AppProcessInfo> list = null;
    PackageManager packageManager = null;
    /**
     *  遍历所以的进程 查询uid 流量
     */
    private class ScanAllProcessFlow extends AsyncTask<Object, Object, List<FlowModel>> {

        @Override
        protected void onPreExecute() {

            loading_view.show();
        }

        @Override
        protected List<FlowModel> doInBackground(Object... params) {
            ArrayList<FlowModel> listt = new ArrayList<>();
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

                    long flows= FlowUtil.getInstance().getSystemBytesByUid(mContext,appProcessInfo.uid,
                            FlowUtil.getInstance().getTimesMonthMorning(),System.currentTimeMillis());

//                   long flows= FlowUtil.getBytesByUid(mContext,appProcessInfo.uid,
//                            FlowUtil.getTimesMonthMorning(),System.currentTimeMillis());
                    LogUtil.e("flow","uid="+appProcessInfo.uid+" appName="+appName+" flows="+ flows);
                    model.setFlowSize(flows);
                    listt.add(model);
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
                    listt.add(model);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }finally {
                    abAppProcessInfo.packName = packName;
                    long memory = activityManager.getProcessMemoryInfo(new int[] {
                            appProcessInfo.pid })[0].getTotalPrivateDirty() * 1024;
                    abAppProcessInfo.memory = memory;

                    listt.add(model);
                }

            }
            LogUtil.e("flow___",listt.size()+"");
            return listt;
        }

        @Override
        protected void onProgressUpdate(Object... values) {

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
}
