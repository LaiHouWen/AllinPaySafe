package com.pax.ipp.tools.ui;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.jaredrummler.android.processes.AndroidProcesses;
import com.pax.ipp.tools.R;
import com.pax.ipp.tools.adapter.FragmentAdapter;
import com.pax.ipp.tools.entity.TabEntity;
import com.pax.ipp.tools.model.AppProcessInfo;
import com.pax.ipp.tools.model.FlowModel;
import com.pax.ipp.tools.service.CleanerService;
import com.pax.ipp.tools.ui.base.BaseActivity;
import com.pax.ipp.tools.ui.fragment.FlowBFragment;
import com.pax.ipp.tools.ui.fragment.FlowFragment;
import com.pax.ipp.tools.utils.FlowUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;

/**
 * Created by houwen.lai on 2017/9/6.
 * 流量
 * 各个app 今日 本月 所消耗的流量统计
 *
 */

public class FlowActivity  extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_title)
    TextView toolbar_title;

    @BindView(R.id.cTabLayout_list)
    CommonTabLayout cTabLayout;
    @BindView(R.id.viewpager_list)
    ViewPager viewpager;

    List<FlowModel> flowModelList_t;
    List<FlowModel> flowModelList_m;


    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private String[] mTitles = {"今日流量统计", "本月流量统计"};
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    int postion =0;


    @Override
    public int getLayoutId() {
        return R.layout.flow_activity;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        toolbar_title.setText(getString(R.string.text_flow_analysis));
        toolbar.setBackgroundColor(getResources().getColor(R.color.blue_0980));
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        tabLayoutInit();

    }

    @Override
    public void initPresenter() {

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void tabLayoutInit() {
        mFragments.clear();
        mTabEntities.clear();
        mFragments.add(FlowFragment.newInstance(FlowUtil.getTimesTodayMorning()));//今日
        mFragments.add(FlowFragment.newInstance(FlowUtil.getTimesMonthMorning()));//本月
        for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabEntity(mTitles[i]));
        }
        cTabLayout.setIconVisible(false);
        cTabLayout.setTabData(mTabEntities);
        cTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                viewpager.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {
                if (position == 0) {

                }
            }
        });

        viewpager.setAdapter(new FragmentAdapter(getSupportFragmentManager(), mFragments, Arrays.asList(mTitles)));
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                cTabLayout.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        if (getIntent().hasExtra("time")){
            postion=getIntent().getStringExtra("time").equals("today")?0:1;
        }else postion=0;
        cTabLayout.setCurrentTab(postion);
        viewpager.setCurrentItem(postion);
    }

    ActivityManager activityManager = null;
    List<AppProcessInfo> list = null;
    PackageManager packageManager = null;
    Context mContext;
    /**
     *  遍历所以的进程 查询uid 流量
     */
//    private class ScanAllProcessFlow extends AsyncTask<Void, Object, List<FlowModel>>{
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        @Override
//        protected List<FlowModel> doInBackground(Void... params) {
//            list = new ArrayList<>();
//            ApplicationInfo appInfo = null;
//            AppProcessInfo abAppProcessInfo = null;
//            //得到所有正在运行的进程
//            List<ActivityManager.RunningAppProcessInfo> appProcessList
//                    = AndroidProcesses.getRunningAppProcessInfo(mContext);
//            publishProgress(0, appProcessList.size(), 0, "开始扫描");
//
//            for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessList) {
//                abAppProcessInfo = new AppProcessInfo(
//                        appProcessInfo.processName, appProcessInfo.pid,
//                        appProcessInfo.uid);
//                String packName = appProcessInfo.processName;
//                try {
//                    appInfo = packageManager.getApplicationInfo(
//                            appProcessInfo.processName, 0);
//
//                    if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
//                        abAppProcessInfo.isSystem = true;
//                    }
//                    else {
//                        abAppProcessInfo.isSystem = false;
//                    }
//                    Drawable icon = appInfo.loadIcon(packageManager);
//                    String appName = appInfo.loadLabel(packageManager)
//                            .toString();
//                    abAppProcessInfo.icon = icon;
//                    abAppProcessInfo.appName = appName;
//                    FlowUtil.getBytesByUid(mContext,appProcessInfo.uid,FlowUtil.getTimesMonthMorning(),System.currentTimeMillis())
//                    //abAppProcessInfo.packName = packName;
//                } catch (PackageManager.NameNotFoundException e) {
//                    abAppProcessInfo.icon = mContext.getResources()
//                            .getDrawable(
//                                    R.mipmap.ic_launcher);
//                    //String packName = appProcessInfo.processName;
//                    appInfo = getApplicationInfo(
//                            appProcessInfo.processName.split(":")[0]);
//                    if (appInfo != null) {
//                        Drawable icon = appInfo.loadIcon(packageManager);
//                        abAppProcessInfo.icon = icon;
//                        packName = appProcessInfo.processName.split(":")[0];
//                    }
//                    abAppProcessInfo.isSystem = true;
//                    abAppProcessInfo.appName = appProcessInfo.processName;
//                    //abAppProcessInfo.packName = packName;
//                } catch (RemoteException e) {
//                    e.printStackTrace();
//                }
//                abAppProcessInfo.packName = packName;
//                long memory = activityManager.getProcessMemoryInfo(new int[] {
//                        appProcessInfo.pid })[0].getTotalPrivateDirty() * 1024;
//                abAppProcessInfo.memory = memory;
//
//            }
//
//            return list;
//        }
//
//        @Override
//        protected void onProgressUpdate(Object... values) {
//            super.onProgressUpdate(values);
//        }
//
//        @Override
//        protected void onPostExecute(List<FlowModel> flowModels) {
//            super.onPostExecute(flowModels);
//        }
//
//    }

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
}
