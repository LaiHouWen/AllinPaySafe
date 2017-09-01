package com.allinpaysafe.app.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.allinpaysafe.app.App;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 判断应用是否在前端还是后端显示
 *
 * @author wwt
 */
public class ApplicationUtils {

    /**
     * 判断当前应用程序处于前台还是后台 <uses-permission
     * android:name="android.permission.GET_TASKS" />
     */
    public static boolean isApplicationBroughtToBackground(final Context context) {
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 应用是否在前端显示
     *
     * @param context
     * @return
     */
    public static boolean isBackground(Context context) {

        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        for (RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
                    // MyLog.d("后台", appProcess.processName);
                    return true;
                } else {
                    // MyLog.d("前台", appProcess.processName);
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * <uses-permission android:name="android.permission.GET_TASKS" />
     *
     * @return
     */
    public boolean isRunningForeground(Context context) {
        String packageName = getPackageName(context);
        String topActivityClassName = getTopActivityName(context);
//		System.out.println("packageName=" + packageName
//				+ ",topActivityClassName=" + topActivityClassName);
        if (packageName != null && topActivityClassName != null
                && topActivityClassName.startsWith(packageName)) {
//			System.out.println("---> isRunningForeGround");
            return true;
        } else {
//			System.out.println("---> isRunningBackGround");
            return false;
        }
    }

    public static String getTopActivityName(Context context) {
        String topActivityClassName = null;
        ActivityManager activityManager = (ActivityManager) (context
                .getSystemService(Context.ACTIVITY_SERVICE));
        List<RunningTaskInfo> runningTaskInfos = activityManager
                .getRunningTasks(1);
        if (runningTaskInfos != null) {
            ComponentName f = runningTaskInfos.get(0).topActivity;
            topActivityClassName = f.getClassName();
        }
        return topActivityClassName;
    }

    public String getPackageName(Context context) {
        String packageName = context.getPackageName();
        return packageName;
    }

    /**
     * 同步一下cookie
     */
    public static void setUrlCookies(Context context, String url, String cookies) {
        CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeSessionCookie();//移除
        cookieManager.setCookie(url, cookies);//cookies是在HttpClient中获得的cookie
        CookieSyncManager.getInstance().sync();
    }

    /**
     * 同步一下cookie
     */
    public static void removeUrlCookies() {//WebView webView,String urlpath
//        if (webView==null)return;
        //清除所有cookie
        CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(App.getInstance());
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeSessionCookie();
        cookieManager.removeAllCookie();
        cookieSyncManager.sync();
    }

//    public static void openReadPdf(){
//        Intent it = new Intent();
//        it.setAction(android.content.Intent.ACTION_VIEW);
//        it.setDataAndType(Uri.fromFile(electronInvoiceFile), "application/pdf");
//        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//        List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(it, 0);
//        //   int removeAppIndex=-1;
//        if (!resInfo.isEmpty()) {
//            List<Intent> targetedIntents = new ArrayList<Intent>();
//            for (int n=0;n<resInfo.size();n++) {
//                ResolveInfo info =resInfo.get(n);
//                String pkg=info.activityInfo.packageName.toLowerCase();
//                //特殊处理,过滤qq等应用不可用选项，com.tencent.mm /mobileqq
//                if (!pkg.contains("com.tencent")) {
//                    Intent chit = new Intent();
//                    chit.setPackage(pkg);
//                    chit.setAction(android.content.Intent.ACTION_VIEW);
//                    chit.setDataAndType(Uri.fromFile(electronInvoiceFile), "application/pdf");
//                    targetedIntents.add(chit);
//                }
//            }
//
//                /*if(removeAppIndex!=-1) {
//                    resInfo.remove(removeAppIndex);
//                }*/
//            Intent chooserIntent = Intent.createChooser(targetedIntents.remove(0), "选择浏览方式");
//            if (chooserIntent == null) {
//                return;
//            }
//            // A Parcelable[] of Intent or LabeledIntent objects as set with
//            // putExtra(String, Parcelable[]) of additional activities to place
//            // a the front of the list of choices, when shown to the user with a
//            // ACTION_CHOOSER.
//            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedIntents.toArray(new Parcelable[]{}));
//            chooserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//            try {
//                getApplicationContext().startActivity(chooserIntent);
//            }catch (ActivityNotFoundException e){
//                e.printStackTrace();
//                BDebug.e(getClass().getSimpleName(), "open pdf choose not found");
//                CommonUtility.showToast(getApplicationContext(),"打开pdf失败，请安装pdf查看器后在进行查看！");
//            }
//
//        }else{
//            CommonUtility.showToast(getApplicationContext(),"打开pdf失败，请安装pdf查看器后在进行查看！");
//            return;
//        }
//    }


    public static int getStatusBarHeight(Activity activity) {
        int result = 0;
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = activity.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static boolean checkDeviceHasNavigationBar(Context activity) {
        //通过判断设备是否有返回键、菜单键(不是虚拟键,是手机屏幕外的按键)来确定是否有navigation bar
        boolean hasMenuKey = ViewConfiguration.get(activity).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        if (!hasMenuKey && !hasBackKey) {
            // 做任何你需要做的,这个设备有一个导航栏
            return true;
        }
        return false;
    }

    public static int getNavigationBarHeight(Activity activity) {
        if (!checkDeviceHasNavigationBar(activity)) return 0;
        Resources resources = activity.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height",
                "dimen", "android");
        //获取NavigationBar的高度
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }

    public static void installApk(Activity activity, String urlPath) {
        LogUtil.d("安装目录：");
        File file = new File(urlPath);
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        activity.startActivity(intent);
//		android.os.Process.killProcess(android.os.Process.myPid());
    }

    public static void installApk(Service service, String urlPath) {
        LogUtil.d("安装目录：");
        File file = new File(urlPath);
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        service.startActivity(intent);
//		android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * 获取设备信息
     *
     * @param mContext
     */
    public static Map<String, String> getDeviceInfo(Activity mContext) {
        if (mContext == null) return null;
        TelephonyManager tmanager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        String uuid = tmanager.getDeviceId();// 序列号
        if (TextUtils.isEmpty(uuid)) {
            uuid = "unknow";
        }
        // String uuid=tmanager.getSimSerialNumber();//序列号
        String device = android.os.Build.MODEL;// 手机型号
        if (TextUtils.isEmpty(device)) {
            device = "unknow";
        }
        String os = "android";// 操作系统
        String osv = android.os.Build.VERSION.RELEASE;// 操作系统版本
        if (TextUtils.isEmpty(osv)) {
            osv = "unknow";
        }
        DisplayMetrics dm = new DisplayMetrics();
        mContext.getWindowManager().getDefaultDisplay().getMetrics(dm);
        String dip = dm.widthPixels + "x" + dm.heightPixels;// 分辨率
        String net = getNetworkType(mContext);// 网络类型
        Map<String, String> map = new HashMap<>();
        map.put("uuid", uuid);
        map.put("device", device);
        map.put("os", os);
        map.put("osv", osv);
        map.put("dpi", dip);
        map.put("net", net);

        String params = "uuid=" + uuid + "&device=" + device + "&os=" + os + "&osv=" + osv + "&dpi=" + dip + "&net="
                + net;
        String deviceInfo = uuid + "&device=" + device + "&os=" + os + "&osv=" + osv + "&dpi=" + dip + "&net=" + net;
//       MyLog.d("api_request="+params.toString());
        return map;
    }

    /**
     * 获取当前的网络状态
     *
     * @param mContext
     * @return
     */
    public static String getCurrentNetType(Context mContext) {
        ConnectivityManager connManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI); // wifi
        NetworkInfo gprs = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE); // gprs
        if (wifi != null && wifi.getState() == NetworkInfo.State.CONNECTED) {
            return "wifi";
        } else if (gprs != null && gprs.getState() == NetworkInfo.State.CONNECTED) {
            return "gprs";
        }
        return "unknow";
    }

    /**
     * 获取网络类型：WiFi、2G、3G
     *
     * @param context
     * @return
     */
    public static String getNetworkType(Context context) {
        String result = "";
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null && ni.isConnected()) {
            int type = ni.getType();
            switch (type) {
                case ConnectivityManager.TYPE_MOBILE:
                    if (ni.getSubtype() == TelephonyManager.NETWORK_TYPE_GPRS
                            || ni.getSubtype() == TelephonyManager.NETWORK_TYPE_CDMA
                            || ni.getSubtype() == TelephonyManager.NETWORK_TYPE_EDGE) {
                        result = "2G";
                    } else {
                        result = "3G/4G";
                    }
                    break;
                case ConnectivityManager.TYPE_WIFI:
                    result = "WiFi";
                    break;

                default:
                    result = "";
                    break;
            }
        }
        return result;
    }

    /**
     * 获得当前版本名VersionName
     *
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        // 获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(),
                    0);
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // PackageInfo pinfo = getPackageManager().getPackageInfo("com.wwt.app",
        // PackageManager.GET_CONFIGURATIONS);
        return packInfo.versionName;
    }

    /**
     * 获得当前版本名VersionCode
     *
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        // 获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(),
                    0);
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // PackageInfo pinfo = getPackageManager().getPackageInfo("com.wwt.app",
        // PackageManager.GET_CONFIGURATIONS);
        return packInfo.versionCode;
    }
}
