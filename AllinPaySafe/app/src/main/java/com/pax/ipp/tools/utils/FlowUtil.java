package com.pax.ipp.tools.utils;

import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.TrafficStats;
import android.os.Build;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.Calendar;
import java.util.TimeZone;

import static android.content.Context.NETWORK_STATS_SERVICE;
import static android.content.Context.TELEPHONY_SERVICE;

/**
 * Created by houwen.lai on 2017/9/4.
 * 流量的统计
 * static long getMobileRxBytes()//获取通过Mobile连接收到的字节总数，但不包含WiFi
 static long getMobileRxPackets()//获取Mobile连接收到的数据包总数
 static long getMobileTxBytes()//Mobile发送的总字节数
 static long getMobileTxPackets()//Mobile发送的总数据包数
 static long getTotalRxBytes()//获取总的接受字节数，包含Mobile和WiFi等
 static long getTotalRxPackets()//总的接受数据包数，包含Mobile和WiFi等
 static long getTotalTxBytes()//总的发送字节数，包含Mobile和WiFi等
 static long getTotalTxPackets()//发送的总数据包数，包含Mobile和WiFi等
 static long getUidRxBytes(int uid)//获取某个网络UID的接受字节数
 static long getUidTxBytes(intuid) //获取某个网络UID的发送字节数

 NetworkStatsManager
 //查询指定网络类型在某时间间隔内的总的流量统计信息
 NetworkStats.Bucket querySummaryForDevice(int networkType, String subscriberId, long startTime, long endTime)

 //查询某uid在指定网络类型和时间间隔内的流量统计信息
 NetworkStats queryDetailsForUid(int networkType, String subscriberId, long startTime, long endTime, int uid)

 //查询指定网络类型在某时间间隔内的详细的流量统计信息（包括每个uid）
 NetworkStats queryDetails(int networkType, String subscriberId, long startTime, long endTime)
 */

public class FlowUtil {

    static final String TAG = FlowUtil.class.getSimpleName();

    /**
     *获得本月第一天0点时间
     * @return
     */
    public static long getTimesMonthMorning() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal.getTimeInMillis();
    }

    /**
     * 获得本月今天0点时间
     * long current=System.currentTimeMillis();//当前时间毫秒数
     long zero=current/(1000*3600*24)*(1000*3600*24)-TimeZone.getDefault().getRawOffset();
     //今天零点零分零秒的毫秒数
     long twelve=zero+24*60*60*1000-1;//今天23点59分59秒的毫秒数
     long yesterday=System.currentTimeMillis()-24*60*60*1000;//昨天的这一时间的毫秒数
     * @return
     */
    public static long getTimesTodayMorning() {
        return System.currentTimeMillis()/(1000*3600*24)*(1000*3600*24)- TimeZone.getDefault().getRawOffset();
    }
    /**
     *
     * @param context
     * @param packageName
     * @return
     */
    public static int getUidByPackageName(Context context, String packageName) {
        int uid = -1;
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_META_DATA);

            uid = packageInfo.applicationInfo.uid;
            Log.i(TAG, packageInfo.packageName + " uid:" + uid);

        } catch (PackageManager.NameNotFoundException e) {
        }
        return uid;
    }

    /**
     * 查询某应用（uid）的数据流量统计信息
     *
     */
    public static long getBytesByUid(Context context,int uid,long startTime,long endTime) throws RemoteException {
        // 获取subscriberId
        TelephonyManager tm = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        String subId = tm.getSubscriberId();

        NetworkStatsManager networkStatsManager = (NetworkStatsManager) context.getSystemService(NETWORK_STATS_SERVICE);

        NetworkStats summaryStats;
        long summaryRx = 0;
        long summaryTx = 0;
        NetworkStats.Bucket summaryBucket = new NetworkStats.Bucket();
        long summaryTotal = 0;
        //本月到现在的流量信息
        summaryStats = networkStatsManager.queryDetailsForUid(ConnectivityManager.TYPE_MOBILE, subId,
                startTime, endTime,uid);
        //getTimesMonthMorning(), System.currentTimeMillis()
        do {
            summaryStats.getNextBucket(summaryBucket);
            int summaryUid = summaryBucket.getUid();
            if (uid == summaryUid) {
                summaryRx += summaryBucket.getRxBytes();
                summaryTx += summaryBucket.getTxBytes();
            }
            summaryTotal += summaryBucket.getRxBytes() + summaryBucket.getTxBytes();
            Log.i(TAG, "uid:" + summaryBucket.getUid() + " rx:" + summaryBucket.getRxBytes() +
                    " tx:" + summaryBucket.getTxBytes()+" total:"+summaryTotal);
        } while (summaryStats.hasNextBucket());
        return summaryTotal;
    }

    public static long getMobileBytes(int uid) {
        return  TrafficStats.getUidRxBytes(uid)+TrafficStats.getUidTxBytes(uid);
        //获取通过Mobile连接收到的字节总数，不包含WiFi
//        return TrafficStats.getMobileRxBytes() == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getMobileRxBytes() / 1024);
    }

    public static long getSystemBytesByUid(Context context,int uid,long startTime,long endTime) throws RemoteException {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            return  getBytesByUid(context,uid,startTime,endTime);
        }else {
            return  getMobileBytes(uid);
        }
    }

    public static long getTotal(){
        return TrafficStats.getTotalRxPackets();
    }


}
