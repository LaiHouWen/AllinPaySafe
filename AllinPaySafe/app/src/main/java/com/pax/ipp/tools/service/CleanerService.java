package com.pax.ipp.tools.service;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.Toast;

import com.pax.ipp.tools.R;
import com.pax.ipp.tools.model.AppProcessInfo;
import com.pax.ipp.tools.model.CacheListItem;
import com.pax.ipp.tools.utils.LogUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 内存的清理 service
 */
public class CleanerService extends Service {

    public static final String ACTION_CLEAN_AND_EXIT
            = "edu.wkd.towave.service.cleaner.CLEAN_AND_EXIT";

    private static final String TAG = "CleanerService";

    private Method mGetPackageSizeInfoMethod, mFreeStorageAndNotifyMethod,
            mDeleteApplicationCacheFiles;
    private OnActionListener mOnActionListener;
    private boolean mIsScanning = false;
    private boolean mIsCleaning = false;
    private long mCacheSize = 0;

    ActivityManager activityManager = null;
    List<AppProcessInfo> list = null;
    PackageManager packageManager = null;
    Context mContext;

    public static interface OnActionListener {
        public void onScanStarted(Context context);

        public void onScanProgressUpdated(Context context, int current, int max, long cacheSize, String packageName);

//        public void onScanProgressUpdated(Context context, int current, int max, long cacheSize, String packageName, CacheListItem item);

        public void onScanCompleted(Context context, List<CacheListItem> apps);

        public void onCleanStarted(Context context);

        public void onCleanCompleted(Context context, long cacheSize);
    }

    public class CleanerServiceBinder extends Binder {

        public CleanerService getService() {
            return CleanerService.this;
        }
    }

    private CleanerServiceBinder mBinder = new CleanerServiceBinder();

    /**
     * 扫描
     */
    private class TaskScan
            extends AsyncTask<Void, Object, List<CacheListItem>> {

        private int mAppCount = 0;

        @Override
        protected void onPreExecute() {
            if (mOnActionListener != null) {
                mOnActionListener.onScanStarted(CleanerService.this);
            }
        }


        @Override
        protected List<CacheListItem> doInBackground(Void... params) {
            mCacheSize = 0;

            final List<ApplicationInfo> packages
                    = getPackageManager().getInstalledApplications(
                    PackageManager.GET_META_DATA);

            publishProgress(0, packages.size(), 0, "开始扫描");
            //publishProgress(0, packages.size());

            final CountDownLatch countDownLatch = new CountDownLatch(
                    packages.size());

            final List<CacheListItem> apps = new ArrayList<>();

            try {
                for (ApplicationInfo pkg : packages) {
//                    if (pkg.processName.contains("com.android.system"))
//                        continue;

                    mGetPackageSizeInfoMethod.invoke(getPackageManager(),
                            pkg.packageName, new IPackageStatsObserver.Stub() {

                                @Override
                                public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
                                        throws RemoteException {
                                    synchronized (apps) {

                                        //publishProgress(++mAppCount,
                                        //        packages.size());


                                        if (succeeded && pStats.cacheSize > 0) {
                                            try {

                                                apps.add(new CacheListItem(
                                                        pStats.packageName,
                                                        getPackageManager().getApplicationLabel(
                                                                getPackageManager()
                                                                        .getApplicationInfo(
                                                                                pStats.packageName,
                                                                                PackageManager.GET_META_DATA))
                                                                           .toString(),
                                                        getPackageManager().getApplicationIcon(
                                                                pStats.packageName),
                                                        pStats.cacheSize));

                                                mCacheSize += pStats.cacheSize;
                                                //
//                                                publishProgress(++mAppCount,
//                                                        packages.size(),
//                                                        mCacheSize,
//                                                        pStats.packageName);
                                                //
                                                publishProgress(++mAppCount,
                                                        packages.size(),
                                                        mCacheSize,
                                                        pStats.packageName,
                                                        apps.get(apps.size()-1));

                                            } catch (PackageManager.NameNotFoundException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                    synchronized (countDownLatch) {
                                        countDownLatch.countDown();
                                    }
                                }
                            });
                }

                countDownLatch.await();
            } catch (InvocationTargetException | InterruptedException | IllegalAccessException e) {
                e.printStackTrace();
            }

            return new ArrayList<>(apps);
        }


        @Override
        protected void onProgressUpdate(Object... values) {
            if (mOnActionListener != null) {
                mOnActionListener.onScanProgressUpdated(CleanerService.this,
                        Integer.parseInt(values[0] + ""),
                        Integer.parseInt(values[1] + ""),
                        Long.parseLong(values[2] + ""), values[3] + "");
            }
        }


        @Override
        protected void onPostExecute(List<CacheListItem> result) {
            mIsScanning = false;
            if (mOnActionListener != null) {
                mOnActionListener.onScanCompleted(CleanerService.this, result);
            }
        }
    }

    /**
     * 清除缓存
     */
    private class TaskClean extends AsyncTask<Void, Void, Long> {

        @Override
        protected void onPreExecute() {
            if (mOnActionListener != null) {
                mOnActionListener.onCleanStarted(CleanerService.this);
            }
        }


        @Override
        protected Long doInBackground(Void... params) {
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            LogUtil.d("service-cleanr");
            StatFs stat = new StatFs(
                    Environment.getDataDirectory().getAbsolutePath());

            try {
                mFreeStorageAndNotifyMethod.invoke(getPackageManager(),
                        (long) stat.getBlockCount() *
                                (long) stat.getBlockSize(),
                        new IPackageDataObserver.Stub() {
                            @Override
                            public void onRemoveCompleted(String packageName, boolean succeeded)
                                    throws RemoteException {
                                countDownLatch.countDown();
                            }
                        });

                countDownLatch.await();
            } catch (InvocationTargetException | InterruptedException | IllegalAccessException e) {
                e.printStackTrace();
            }

            return mCacheSize;
        }


        @Override
        protected void onPostExecute(Long result) {
            mCacheSize = 0;

            if (mOnActionListener != null) {
                mOnActionListener.onCleanCompleted(CleanerService.this, result);
            }

            mIsCleaning = false;
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    @Override
    public void onCreate() {
        mContext = getApplicationContext();

        try {
            activityManager = (ActivityManager) getSystemService(
                    Context.ACTIVITY_SERVICE);
            packageManager = mContext.getPackageManager();
        } catch (Exception e) {

        }
        try {
            mGetPackageSizeInfoMethod = getPackageManager().getClass()
                                                           .getMethod(
                                                                   "getPackageSizeInfo",
                                                                   String.class,
                                                                   IPackageStatsObserver.class);

            mFreeStorageAndNotifyMethod = getPackageManager().getClass()
                                                             .getMethod(
                                                                     "freeStorageAndNotify",
                                                                     long.class,
                                                                     IPackageDataObserver.class);
            mDeleteApplicationCacheFiles = getPackageManager().getClass()
                                                              .getMethod(
                                                                      "deleteApplicationCacheFiles",
                                                                      String.class,
                                                                      IPackageDataObserver.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();

        if (action != null) {
            if (action.equals(ACTION_CLEAN_AND_EXIT)) {
//                setOnActionListener(new OnActionListener() {
//                    @Override
//                    public void onScanStarted(Context context) {
//
//                    }
//
//                    @Override
//                    public void onScanProgressUpdated(Context context, int current, int max, long cacheSize, String packageName) {
//
//                    }
//
////                    @Override
////                    public void onScanProgressUpdated(Context context, int current, int max, long cacheSize, String packageName, CacheListItem item) {
////
////                    }
//
//
//                    @Override
//                    public void onScanCompleted(Context context, List<CacheListItem> apps) {
//                        if (getCacheSize() > 0) {
//                            cleanCache();
//                        }
//                    }
//
//
//                    @Override
//                    public void onCleanStarted(Context context) {
//
//                    }
//
//
//                    @Override
//                    public void onCleanCompleted(Context context, long cacheSize) {
//                        String msg = getString(R.string.cleaned,
//                                Formatter.formatShortFileSize(
//                                        CleanerService.this, cacheSize));
//
//                        Log.d(TAG, msg);
//
//                        Toast.makeText(CleanerService.this, msg,
//                                Toast.LENGTH_LONG).show();
//
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                stopSelf();
//                            }
//                        }, 5000);
//                    }
//                });

//                scanCache();
            }
        }

        return START_NOT_STICKY;
    }


    public void scanCache() {
        mIsScanning = true;

        new TaskScan().execute();
    }


    public void cleanCache() {
        mIsCleaning = true;

//        new TaskClean().execute();
        cleanAllProcess();
    }


    public void setOnActionListener(OnActionListener listener) {
        mOnActionListener = listener;
    }

    /**
     * 清除单个app缓存
     * @param packageName
     */
    public void cleanCache(String packageName) {
        try {

            //需要系统级权限
            mDeleteApplicationCacheFiles.invoke(getPackageManager(),
                    packageName, new IPackageDataObserver.Stub() {
                        @Override
                        public void onRemoveCompleted(String packageName, boolean succeeded)
                                throws RemoteException {
                            // TODO Auto-generated method stub

                        }
                    });
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }


    public boolean isScanning() {
        return mIsScanning;
    }

    public boolean isCleaning() {
        return mIsCleaning;
    }

    public long getCacheSize() {
        return mCacheSize;
    }

    public void cleanAllProcess() {
        mIsCleaning = true;
        new TaskCleanProcess().execute();
    }

    /**
     * 杀死进程
     */
    private class TaskCleanProcess extends AsyncTask<Void, Void, Long> {

//        private FinalDb mFinalDb = FinalDb.create(mContext);


        @Override protected void onPreExecute() {
            if (mOnActionListener != null) {
                mOnActionListener.onCleanStarted(CleanerService.this);
            }
        }


        @Override protected Long doInBackground(Void... params) {

            killAll();

            long beforeMemory = 0;
            long endMemory = 0;
//            ActivityManager.MemoryInfo memoryInfo
//                    = new ActivityManager.MemoryInfo();
//            activityManager.getMemoryInfo(memoryInfo);
//            beforeMemory = memoryInfo.availMem;
//            List<RunningAppProcessInfo> appProcessList
//                    = AndroidProcesses.getRunningAppProcessInfo(mContext);
//            ApplicationInfo appInfo = null;
//            for (RunningAppProcessInfo info : appProcessList) {
//                String packName = info.processName;
//                if( info.processName.contains("com.android.system")
//                        ||info.pid==android.os.Process.myPid())//跳过系统 及当前进程
//                    continue;
//                try {
//                    packageManager.getApplicationInfo(info.processName, 0);
//                } catch (PackageManager.NameNotFoundException e) {
//                    appInfo = getApplicationInfo(info.processName.split(":")[0]);
//                    if (appInfo != null) {
//                        packName = info.processName.split(":")[0];
//                    }
//                }
//                //忽略进程
////                List<Ignore> ignores = mFinalDb.findAllByWhere(Ignore.class,
////                        "packName='" + packName + "'");
////                if (ignores.size() == 0) {
//                    LogUtil.e(info.processName);
//                    killBackgroundProcesses(info.processName);
////                }
//            }
//            activityManager.getMemoryInfo(memoryInfo);
//            endMemory = memoryInfo.availMem;
            return endMemory - beforeMemory;
        }


        @Override protected void onPostExecute(Long result) {
            mIsCleaning=false;
            mIsScanning=false;
            if (mOnActionListener != null) {
                mOnActionListener.onCleanCompleted(CleanerService.this, result);
            }
        }
    }

    public void killBackgroundProcesses(String processName) {
        mIsScanning = true;

        String packageName = null;
        try {
            if (processName.indexOf(":") == -1) {
                packageName = processName;
            }
            else {
                packageName = processName.split(":")[0];
            }

            activityManager.killBackgroundProcesses(packageName);

            //app使用FORCE_STOP_PACKAGES权限，app必须和这个权限的声明者的签名保持一致！
            Method forceStopPackage = activityManager.getClass()
                    .getDeclaredMethod(
                            "forceStopPackage",
                            String.class);
            forceStopPackage.setAccessible(true);
            forceStopPackage.invoke(activityManager, packageName);
            mIsScanning=false;
        } catch (Exception e) {
            e.printStackTrace();
            mIsScanning=false;
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
int MAX_TASKS =1000;
    /*
    * 杀死后台进程
    */
    public void killAll(){
        //获取一个ActivityManager 对象
        ActivityManager activityManager = (ActivityManager)
                getSystemService(Context.ACTIVITY_SERVICE);
        //获取系统中所有正在运行的进程
        List<RunningAppProcessInfo> appProcessInfos = activityManager
                .getRunningAppProcesses();
        int count=0;//被杀进程计数
        String nameList="";//记录被杀死进程的包名
        long beforeMem = getAvailMemory(mContext);//清理前的可用内存
        Log.i(TAG, "清理前可用内存为 : " + beforeMem);

        for (RunningAppProcessInfo appProcessInfo:appProcessInfos) {
            nameList="";
            if( appProcessInfo.processName.contains("com.android.system")
                    ||appProcessInfo.pid==android.os.Process.myPid())//跳过系统 及当前进程
                continue;
            String[] pkNameList=appProcessInfo.pkgList;//进程下的所有包名
            for(int i=0;i<pkNameList.length;i++){
                String pkName=pkNameList[i];
                activityManager.killBackgroundProcesses(pkName);//杀死该进程
                count++;//杀死进程的计数+1
                nameList+="  "+pkName;
            }
            Log.i(TAG, nameList+"---------------------");
        }

        long afterMem = getAvailMemory(mContext);//清理后的内存占用

//        Toast.makeText(mContext, "杀死 " + (count+1) + " 个进程, 释放"
//                + formatFileSize(afterMem - beforeMem) + "内存", Toast.LENGTH_LONG).show();
        Log.i(TAG, "清理后可用内存为 : " + afterMem);
        Log.i(TAG, "清理进程数量为 : " + count+1);
        LogUtil.e( "杀死 " + (count+1) + " 个进程, 释放"
                + formatFileSize(afterMem - beforeMem) + "内存");

//        final ActivityManager am = (ActivityManager)
//                mContext.getSystemService(Context.ACTIVITY_SERVICE);
//        final List<ActivityManager.RecentTaskInfo> recentTasks =
//                am.getRecentTasks(MAX_TASKS, ActivityManager.RECENT_IGNORE_UNAVAILABLE);
//        for(ActivityManager.RecentTaskInfo rt:recentTasks ) {
//            if (am != null) am.removeTask(rt.persistentId);
//            am.re;
//        }
        mIsScanning=false;
    }

    /*
     * *获取可用内存大小
     */
    private long getAvailMemory(Context context) {
        // 获取android当前可用内存大小
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        return mi.availMem;
    }

    /*
     * *字符串转换 long-string KB/MB
     */
    private String formatFileSize(long number){
        return Formatter.formatFileSize(mContext, number);
    }
}
