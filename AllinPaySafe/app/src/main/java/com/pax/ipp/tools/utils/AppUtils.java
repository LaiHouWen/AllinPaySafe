package com.pax.ipp.tools.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigDecimal;

/**
 * Created by houwen.lai on 2017/9/1.
 */
public class AppUtils {

    /**
     * 描述：获取可用内存.
     */
    public static long getAvailMemory(Context context) {
        // 获取android当前可用内存大小
        ActivityManager activityManager
                = (ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE);
        MemoryInfo memoryInfo = new MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        // 当前系统可用内存 ,将获得的内存大小规格化
        return memoryInfo.availMem;
    }


    /**
     * 描述：总内存.
     */
    public static long getTotalMemory() {
        // 系统内存信息文件
        String file = "/proc/meminfo";
        String memInfo;
        String[] strs;
        long memory = 0;
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader,
                    8192);
            // 读取meminfo第一行，系统内存大小
            memInfo = bufferedReader.readLine();
            strs = memInfo.split("\\s+");
            // 获得系统总内存，单位KB
            memory = Integer.valueOf(strs[1]).intValue();
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Byte转位KB或MB
        return memory * 1024;
    }


    public static float getPercent(long memory) {
        long y = getTotalMemory();
        final double x = ((memory / (double) y) * 100);
        return new BigDecimal(x).setScale(2, BigDecimal.ROUND_HALF_UP)
                                .floatValue();
    }

    public static float getPercent(Context context) {
        long l = getAvailMemory(context);
        long y = getTotalMemory();
        return getPercent(y - l);
    }

/**
 * 通过包名获取应用程序的名称。
 * @param context
 *     Context对象。
 * @param packageName
 *            包名。
 * @return 返回包名所对应的应用程序的名称。
 */
    public static String getAppNameByPackageName(Context context,
                                                     String packageName) {
        PackageManager pm = context.getPackageManager();
        String name = null;
        try {
            name = pm.getApplicationLabel(
                    pm.getApplicationInfo(packageName,
                            PackageManager.GET_META_DATA)).toString();
        }  catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return name;
    }

    /**
     *
     * @param context
     * @param packageName
     * @return
     * @throws PackageManager.NameNotFoundException
     */
    public Drawable getAppInfoByPackageName(Context context,String packageName) throws PackageManager.NameNotFoundException {
// TODO Auto-generated constructor stub
        PackageManager packageManager =context.getPackageManager();
        ApplicationInfo application= packageManager.getPackageInfo(packageName, 0).applicationInfo;

//        String[] appInfo=new String[]{packageName,application.loadLabel(packageManager).toString()};
        Drawable d = application.loadIcon(packageManager);
        return d;
    }
}
