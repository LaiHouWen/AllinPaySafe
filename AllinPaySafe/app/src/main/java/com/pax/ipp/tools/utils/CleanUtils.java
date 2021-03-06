package com.pax.ipp.tools.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.os.RemoteException;
import android.util.Log;

import com.pax.ipp.tools.model.AppProcessInfo;

/**
 * @author DongYinPing 1045612812@qq.com
 * @version 1.0 一键清理工具类:主要清理 1.系统的内存 2.App的缓存 3.系统的临时文件 .apk .log .tmp .temp;
 * */
public class CleanUtils {

    private Context cx;
    private ActivityManager am;
    private PackageManager pm;

    public CleanUtils(Context context) {
        super();
        cx = context;
        am = (ActivityManager) cx.getSystemService(Context.ACTIVITY_SERVICE);
        pm = cx.getPackageManager();
    }

    // ################################ 清理进程 ###################################

    /**
     * 获取进程白名单
     * */
    private List<String> getFilterPackgeName() {

        List<String> filterPackgeNames = new ArrayList<String>(); // 过滤一些进程

        ActivityInfo launcherInfo = new Intent(Intent.ACTION_MAIN).addCategory(
                Intent.CATEGORY_HOME).resolveActivityInfo(pm, 0);

        filterPackgeNames.add(launcherInfo.packageName); // Launcher
        filterPackgeNames.add("com.hitv.locker"); // 定时锁屏管理
        filterPackgeNames.add(cx.getPackageName()); // 自己
        filterPackgeNames.add(getCurTopPackgeName()); // 过滤正在运行的进程      

        return filterPackgeNames;
    }

    /**
     * 系统剩余的内存
     * */
    public float getSurplusMemory() {
        MemoryInfo info = new MemoryInfo();
        am.getMemoryInfo(info);
        return (float) info.availMem / 1024 / 1024;
    }

    /**
     * 系统总内存
     * */
    public float getTotalMemory() {
        String str1 = "/proc/meminfo";
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;
        try {
            FileReader fileReader = new FileReader(str1);
            BufferedReader bufferedReader = new BufferedReader(fileReader, 8192);
            str2 = bufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            initial_memory = Integer.valueOf(arrayOfString[1]);
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (float) (initial_memory / 1024);
    }

    /**
     * 系统内存:当前使用百分比/字符串格式
     * */
    public String usePercentNumString() {
        float surplus_size = getSurplusMemory();// 剩余MB
        float all_size = getTotalMemory();// 总共MB
        // 小数不足2位,以0补足
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        String percentnum = decimalFormat
                .format((1 - surplus_size / all_size) * 100);
        return percentnum;
    }

    /**
     * 系统内存:当前使用百分比/数字格式
     * */
    public float usePercentNum() {// 用于清理后UI更新
        float surplus_size = getSurplusMemory();// 剩余MB
        float all_size = getTotalMemory();// 总共MB
        return (1 - surplus_size / all_size) * 100;
    }

    /**
     * 使用反射方法调用系统隐藏api：forceStopPackage 通过包名杀死进程
     * */
    public boolean forceStopPackageByPackageName(String packageName) {
        boolean forceSucceed = false;
        Class<ActivityManager> clazz = ActivityManager.class;
        Method method;
        try {
            method = clazz.getDeclaredMethod("forceStopPackage", String.class);
            method.invoke(am, packageName);
            forceSucceed = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return forceSucceed;
    }

    /**
     * 判断是否属于系统app
     * */
    public boolean isSystemApp(ApplicationInfo info) {
        if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
            return false;// 表示是系统程序，但用户更新过，也算是用户安装的程序
        } else if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
            return false; // 一定是用户安装的程序
        }
        return true;
    }

    /**
     * 获取最顶层的app包名,若是自己，则指定为其上一个
     * */
    public String getCurTopPackgeName() {
        String curAppTaskPackgeName = null;
        String myPackageName = cx.getPackageName();
        List<RunningTaskInfo> appTask = am.getRunningTasks(Integer.MAX_VALUE);
        if (appTask.size() > 0) {
            curAppTaskPackgeName = appTask.get(0).topActivity.getPackageName();
            if (appTask.size() > 1) {
                if (curAppTaskPackgeName.equals(myPackageName)
                        && appTask.get(1) != null) {
                    curAppTaskPackgeName = appTask.get(1).topActivity
                            .getPackageName();
                }
            }
        }
        return curAppTaskPackgeName;
    }

    /**
     * 遍历所有正在运行的进程列表,将所有应用的信息传入AppInfo中
     * */
    public List<AppProcessInfo> getRunningAppProcesses() {
        List<RunningAppProcessInfo> runningAppProcessInfos = am
                .getRunningAppProcesses();// 正在运行的进程
        List<AppProcessInfo> appInfos = new ArrayList<AppProcessInfo>();
        List<String> filterPackgeName = getFilterPackgeName();
        for (RunningAppProcessInfo appProcessInfo : runningAppProcessInfos) {
            AppProcessInfo info = new AppProcessInfo();
            int id = appProcessInfo.pid;
            info.setPid(id);
            String appProcessName = appProcessInfo.processName;
            info.setIsFilterProcess(filterPackgeName.contains(appProcessName));// 需过滤的包名
            info.setProcessName(appProcessName);// 设置进程名
            try {
                ApplicationInfo applicationInfo = pm.getPackageInfo(appProcessName, 0).applicationInfo;
                Drawable icon = applicationInfo.loadIcon(pm);
                info.setIcon(icon);
                String name = applicationInfo.loadLabel(pm).toString();
                info.setAppName(name);
                info.setSystem(isSystemApp(applicationInfo));
                android.os.Debug.MemoryInfo[] memoryInfo = am
                        .getProcessMemoryInfo(new int[] { id });
                int memory = memoryInfo[0].getTotalPrivateDirty();
                info.setMemory(memory);
                appInfos.add(info);
                info = null;
            } catch (Exception e) {
                // e.printStackTrace();
                info.setProcessName(appProcessName);
                info.setSystem(true);
            }
        }
        return appInfos;
    }

    /**
     * 过滤掉系统和白名单进程 获取最终要杀死的进程列表
     * */
    public ArrayList<AppProcessInfo> getKillRunningAppProcesses() {
        ArrayList<AppProcessInfo> killRunningAppProcesses = new ArrayList<AppProcessInfo>();
        List<AppProcessInfo> runningAppProcessInfos = getRunningAppProcesses();// 正在运行的进程
        for (int i = 0; i < runningAppProcessInfos.size(); i++) {
            AppProcessInfo info = runningAppProcessInfos.get(i);
            if (!info.isSystem() && !info.getIsFilterProcess()) {
                killRunningAppProcesses.add(info);
            }
        }
        return killRunningAppProcesses;
    }

    /**
     * 遍历进程列表并杀死
     * */
    public String killProcesses(ArrayList<AppProcessInfo> appInfos) {
        float killMemorySize = 0;
        for (AppProcessInfo appInfo : appInfos) {
            if (forceStopPackageByPackageName(appInfo.getPackName())) {
                float size = appInfo.getMemory();
                Log.d("1----- DYP -----1","killProcesses-->>   packageName:" + appInfo.getPackName()+ " | size:" + size);
                killMemorySize = killMemorySize + appInfo.getMemory();
            }
        }
        return numToString((float) killMemorySize / 1024);
    }

    // ################################ 清理文件 ###################################
    /**
     * 获取单个文件大小
     * */

    public float getFileSize(File file) {
        float size = 0;
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            size = inputStream.available();
        } catch (Exception e) {
            Log.d("--- DYP --- getFileSize", "catch");
            // e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (java.io.IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return (float) size / 1024;
    }

    /**
     * 删除文件，并返回文件大小
     * */
    public String deleteFile(List<File> files) {
        float allFileSize = 0;
        float size = 0;
        for (File file : files) {
            size = getFileSize(file);
            Log.d("3----- DYP -----3", "deleteFile-->>  filePath:" + file.getPath()+ " | size:" + size);
            if(file.delete()){
                allFileSize = allFileSize + getFileSize(file);
            }
        }
        // list.clear();
        return numToString((float) allFileSize / 1024);
    }

    /**
     * 查找指定目录下的文件，
     * */
    List<File> fileList = new ArrayList<File>();

    public List<File> getallFiles(String sd, String[] clearType) {

        try {//遍历可能遇到.开头的文件

            File file = new File(sd);
            if (file.exists()) {
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isDirectory()) {
                        getallFiles(files[i].getAbsolutePath(), clearType);// 递归查找
                    } else {
                        for (int j = 0; j < clearType.length; j++) {
                            if (files[i].getAbsolutePath().endsWith((clearType[j]))) {// 以.apk这些结尾
//                          if (files[i].getAbsolutePath().endsWith(".apk")) {
//                              if (isInstallApp(files[i].getAbsolutePath())) {// 安装过可以删除
//                                  fileList.add(files[i]);
//                              }
//                          } else {
//                              fileList.add(files[i]);
//                          }
                                fileList.add(files[i]);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return fileList;
    }

    // ################################ 清理缓存 ###################################

    float cacheSize = 0;

    /**
     * 使用反射方法调用系统隐藏api： getPackageSizeInfo 获取app缓存大小
     * */
    public float getPackageSizeInfo(final String packageName) {
        cacheSize = 0;
        try {
            Method method = PackageManager.class.getMethod(
                    "getPackageSizeInfo", new Class[] { String.class,
                            IPackageStatsObserver.class });
            method.invoke(pm, new Object[] { packageName,
                    new IPackageStatsObserver.Stub() {
                        @Override
                        public void onGetStatsCompleted(PackageStats pStats,
                                boolean succeeded) throws RemoteException {
                            cacheSize = pStats.cacheSize;
                        }
                    } });
        } catch (Exception e) {
            cacheSize = 0;
            // e.printStackTrace();
        }
        return cacheSize;
    }

    boolean isCleanCacheSucceed = false;

    /**
     * 使用反射方法调用系统隐藏api： deleteApplicationCacheFiles 返回清除是否成功
     * */
    private boolean deleteApplicationCacheFiles(String packageName) {
        isCleanCacheSucceed = false;
        try {
            Method method = PackageManager.class.getMethod(
                    "deleteApplicationCacheFiles", new Class[] { String.class,
                            IPackageDataObserver.class });
            method.invoke(pm, packageName, new IPackageDataObserver.Stub() {
                @Override
                public void onRemoveCompleted(String packageName,
                        boolean succeeded) throws RemoteException {
                    if(succeeded){
                        Log.d("DYP", "deleteApplicationCacheFiles -->> succeeded "+succeeded);
                        Log.d("DYP", "deleteApplicationCacheFiles -->> packageName "+packageName);
                    }
                    isCleanCacheSucceed = succeeded;
                }
            });
        } catch (Exception e) {
            // e.printStackTrace();
            Log.d("DYP", "deleteApplicationCacheFiles -->> catch ");
        }
        return isCleanCacheSucceed;
    }

    float allCacheSize = 0;

    /**
     * 清除所有安装app 返回的总缓存
     * */
    public String cleanCache(List<PackageInfo> packageInfos) {
        allCacheSize = 0;
        for (int i = 0; i < packageInfos.size(); i++) {
            String packageName = packageInfos.get(i).packageName;
            if (packageName != null) {
                float size = getPackageSizeInfo(packageName);
                //Log.d("2----- DYP -----2", "cleanCache-->  packageName:" + packageName+ " | size:" + size);
                if(deleteApplicationCacheFiles(packageName)){
                    allCacheSize = allCacheSize + size;
                    Log.d("2----- DYP -----2", "cleanCache-->  packageName:" + packageName+ " | size:" + size);
                }
            }
        }
        return numToString((float) allCacheSize / 1024);
    }

    /**
     * 判断app是否安装
     * */
    public boolean isInstallApp(String abPath) {
        PackageManager pm = cx.getPackageManager();
        try {
            pm.getPackageInfo(abPath, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    /**
     * 获取所有已经安装的应用程序 ,包括那些卸载了的，但没有清除数据的应用程序
     * */
    public List<PackageInfo> getPackageInfos() {
        List<PackageInfo> packageInfos = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
        // List<ApplicationInfo> listAppcations = pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        return packageInfos;
    }

    // #############################  将float转化为字符串形式返回 #########################

    public String numToString(float f) {
        // 小数不足2位,以0补足
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        String s = decimalFormat.format(f);
        return s;
    }
    // ############################# 开始清理 并返回清理数据的大小 #########################
    /** 清除指定垃圾文件 */
    String[] clearType = { ".apk", ".log", ".tmp", ".temp", ".bak" };
    String SDCARD_ROOT = "/mnt/sdcard";

    public String startDeleteFile() {
        return deleteFile(getallFiles(SDCARD_ROOT, clearType));
    }

    public String startCleanCache() {
        return cleanCache(getPackageInfos());
    }

    public String startkillProcesses() {
        return killProcesses(getKillRunningAppProcesses());
    }
}

