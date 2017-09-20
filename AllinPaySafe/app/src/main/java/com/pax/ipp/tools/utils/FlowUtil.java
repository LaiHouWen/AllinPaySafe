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
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pax.ipp.tools.Constant;
import com.pax.ipp.tools.model.BaseFlowModel;
import com.pax.ipp.tools.model.MonthFlowModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
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

   private static FlowUtil instance = null;

    public static FlowUtil getInstance(){
        if (instance==null){
            synchronized (FlowUtil.class){
                if (instance==null)instance=new FlowUtil();
            }
        }
        return instance;
    }


    /**
     *获得本月第一天0点时间
     * @return
     */
    public long getTimesMonthMorning() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal.getTimeInMillis();
    }
    /**
     *获得本月第一天0点时间
     * 前一个月的最后一秒
     *
     * @return
     */
    public long getTimesMonthYestoday() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal.getTimeInMillis()-1;
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
    public long getTimesTodayMorning() {
        return System.currentTimeMillis()/(1000*3600*24)*(1000*3600*24)- TimeZone.getDefault().getRawOffset();
    }

    /**
     * 昨天最后时间23:59:59
     * @return
     */
    public long getTimesYestodayEnd() {
        return System.currentTimeMillis()/(1000*3600*24)*(1000*3600*24)- TimeZone.getDefault().getRawOffset()-1;
    }
    /**
     *
     * @param context
     * @param packageName
     * @return
     */
    public int getUidByPackageName(Context context, String packageName) {
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
    public long getBytesByUid(Context context,int uid,long startTime,long endTime) throws RemoteException {
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

    /**
     * 通过UId获取某进程从开机到现在所消耗的流量
     * @param uid
     * @return
     */
    public long getMobileBytes(int uid) {
        return  TrafficStats.getUidRxBytes(uid)+TrafficStats.getUidTxBytes(uid);
        //获取通过Mobile连接收到的字节总数，不包含WiFi
//        return TrafficStats.getMobileRxBytes() == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getMobileRxBytes() / 1024);
    }

    public long getSystemBytesByUid(Context context,int uid,long startTime,long endTime) throws RemoteException {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            return  getBytesByUid(context,uid,startTime,endTime);
        }else {
            return  getMobileBytes(uid);
        }
    }

    public long getTotal(){
        return TrafficStats.getTotalRxPackets();
    }

    /**
     *
     * 获取开机时间
     *
     */
    // 返回开机时间，单位微妙
    public long getBootTime() {
        return System.currentTimeMillis() - SystemClock.elapsedRealtimeNanos() / 1000000;
    }

    /**
     * 获取某一天的总流量
     * @param context
     * @param date
     * @return
     */
    public MonthFlowModel getMonthBytesByDtate(Context context,Map<String,MonthFlowModel> map,String date){
        if (map==null)return null;
        if (map.containsKey(date)){
            LogUtil.d("getmonth flow",map.get(date).toString());
            return map.get(date);
        }else return null;
    }

    /**
     * 获取当天的流量
     * @return
     */
//    public long getTodayBytes(Context context){
//        //开机到现在的总流量mobile
//        long totail = TrafficStats.getTotalRxBytes()+TrafficStats.getTotalTxBytes();
//        long sum = 0;
//        LogUtil.d("flow totail",totail+"");
//        //有效时间
//        String time_shundown =  SharedPreUtils.getInstanse().getKeyValue(context,Constant.Time_ShunDown);
//        LogUtil.d("flow 有效时间",time_shundown);
//        String today = DateUtil.getToday();
//        synchronized (Constant.flowTodayMonth) {
//            if (Constant.flowTodayMonth != null) {
//                if (TextUtils.isEmpty(time_shundown)) {//一直没有关机
//                    if (Constant.flowTodayMonth.containsKey(DateUtil.getYesterday())) {//判断昨天是否有值
//                        sum = Constant.flowTodayMonth.get(today) -
//                                Constant.flowTodayMonth.get(DateUtil.getYesterday());
//                    } else {
//                        sum = Constant.flowTodayMonth.get(DateUtil.getToday());
//                    }
//                } else {//关机
//                    if (Constant.flowTodayMonth.containsKey(DateUtil.getYesterday())) {//判断昨天是否有值
//                        sum = Constant.flowTodayMonth.get(DateUtil.getToday()) -
//                                Constant.flowTodayMonth.get(DateUtil.getYesterday());
//                    } else {//
//                        if (!today.equals(time_shundown)) {
//                            if (Constant.flowTodayMonth.containsKey(time_shundown))
//                                sum = Constant.flowTodayMonth.get(today) -
//                                        Constant.flowTodayMonth.get(time_shundown);
//                            else sum = Constant.flowTodayMonth.get(today);
//                        } else sum = Constant.flowTodayMonth.get(today);
//                    }
//                }
//            }
//        }
//        LogUtil.e("flow 今日的流量=",sum+"");
//        return sum;
//    }

    /**
     * 获取当月的流量
     * @return
     */
//    public long getMonthBytes(Context context){
//        //开机到现在的总流量mobile
//        long sum =0;
////        String avaible = SharedPreUtils.getInstanse().getKeyValue(context,Constant.Time_Availble);
//        String time_shundown = SharedPreUtils.getInstanse().getKeyValue(context,Constant.Time_Availble_month);
//        LogUtil.d("flow 有效时间month"," 有效月="+time_shundown);
//        synchronized (Constant.flowTodayMonth) {
//            if (Constant.flowTodayMonth != null) {
//                if (TextUtils.isEmpty(time_shundown)) {
//                    sum = Constant.flowTodayMonth.get(DateUtil.getToday());
//                } else {
//                    if (!DateUtil.getToday().substring(0, 7).equals(time_shundown.substring(0, 7)))
//                        sum = Constant.flowTodayMonth.get(DateUtil.getToday()) -
//                                Constant.flowTodayMonth.get(time_shundown);
//                    else sum = Constant.flowTodayMonth.get(DateUtil.getToday());
//                }
//            }
//        }
//        LogUtil.e("flow 今月的流量=",sum+"");
//        return sum;
//    }

    /**
     * 根据某个进程查询它今天的流量
     * @return
     */
    public long getTodayBytesByUid(Context context,Map<String,Map<String,BaseFlowModel>> flowHistoryList,String packageName){
        //从开机到关机
        long sum =0;
        String time_shundown = SharedPreUtils.getInstanse().getKeyValue(context,Constant.Time_ShunDown);
            if (flowHistoryList != null && flowHistoryList.containsKey(packageName)) {
                if (TextUtils.isEmpty(time_shundown)) {//无关机
                    if (flowHistoryList.get(packageName).containsKey(DateUtil.getYesterday())) {
                        sum = getContainsKey(flowHistoryList.get(packageName),DateUtil.getToday()) -
                                flowHistoryList.get(packageName).get(DateUtil.getYesterday()).getFlowSize();
                    } else {
                        sum =!flowHistoryList.get(packageName).containsKey(DateUtil.getToday())
                                ?0:flowHistoryList.get(packageName).get(DateUtil.getToday()).getFlowSize();
                    }
                } else {//关机
                    if (flowHistoryList.get(packageName).containsKey(DateUtil.getYesterday())) {
                        sum =getContainsKey(flowHistoryList.get(packageName),DateUtil.getToday()) -
                                flowHistoryList.get(packageName).get(DateUtil.getYesterday()).getFlowSize();
                    } else {
                        if (!DateUtil.getToday().equals(time_shundown))
                            sum = getContainsKey(flowHistoryList.get(packageName),DateUtil.getToday()) -
                                    flowHistoryList.get(packageName).get(time_shundown).getFlowSize();
                        else
                            sum =getContainsKey(flowHistoryList.get(packageName),DateUtil.getToday());
                    }
                }
            } else {
                sum = 0;
            }
        LogUtil.e("flow_today_uid","packgerName="+packageName+" sum="+sum);
        return sum;
    }
    public static long getContainsKey(Map<String,BaseFlowModel> map,String key){
        if (map==null)return 0;
        if (map.containsKey(key))return map.get(key).getFlowSize();
        else return 0;
    }
    public long getTodayBytesByUidMobile(Context context,Map<String,Map<String,BaseFlowModel>> flowHistoryList,String packageName){
        //从开机到关机
        long sum =0;
        String time_shundown = SharedPreUtils.getInstanse().getKeyValue(context,Constant.Time_ShunDown);
        if (flowHistoryList != null && flowHistoryList.containsKey(packageName)) {
            if (TextUtils.isEmpty(time_shundown)) {//无关机
                if (flowHistoryList.get(packageName).containsKey(DateUtil.getYesterday())) {
                    sum = getContainsKeyMobile(flowHistoryList.get(packageName),DateUtil.getToday()) -
                            flowHistoryList.get(packageName).get(DateUtil.getYesterday()).getFlowMobile();
                } else {
                    sum =!flowHistoryList.get(packageName).containsKey(DateUtil.getToday())
                            ?0:flowHistoryList.get(packageName).get(DateUtil.getToday()).getFlowMobile();
                }
            } else {//关机
                if (flowHistoryList.get(packageName).containsKey(DateUtil.getYesterday())) {
                    sum =getContainsKeyMobile(flowHistoryList.get(packageName),DateUtil.getToday()) -
                            flowHistoryList.get(packageName).get(DateUtil.getYesterday()).getFlowMobile();
                } else {
                    if (!DateUtil.getToday().equals(time_shundown))
                        sum = getContainsKeyMobile(flowHistoryList.get(packageName),DateUtil.getToday()) -
                                flowHistoryList.get(packageName).get(time_shundown).getFlowMobile();
                    else
                        sum =getContainsKeyMobile(flowHistoryList.get(packageName),DateUtil.getToday());
                }
            }
        } else {
            sum = 0;
        }
        LogUtil.e("flow_today_uid","packgerName="+packageName+" sum="+sum);
        return sum;
    }
    public static long getContainsKeyMobile(Map<String,BaseFlowModel> map,String key){
        if (map==null)return 0;
        if (map.containsKey(key))return map.get(key).getFlowMobile();
        else return 0;
    }
    public long getTodayBytesByUidWifi(Context context,Map<String,Map<String,BaseFlowModel>> flowHistoryList,String packageName){
        //从开机到关机
        long sum =0;
        String time_shundown = SharedPreUtils.getInstanse().getKeyValue(context,Constant.Time_ShunDown);
        if (flowHistoryList != null && flowHistoryList.containsKey(packageName)) {
            if (TextUtils.isEmpty(time_shundown)) {//无关机
                if (flowHistoryList.get(packageName).containsKey(DateUtil.getYesterday())) {
                    sum = getContainsKeyWifi(flowHistoryList.get(packageName),DateUtil.getToday()) -
                            flowHistoryList.get(packageName).get(DateUtil.getYesterday()).getFlowWifi();
                } else {
                    sum =!flowHistoryList.get(packageName).containsKey(DateUtil.getToday())
                            ?0:flowHistoryList.get(packageName).get(DateUtil.getToday()).getFlowWifi();
                }
            } else {//关机
                if (flowHistoryList.get(packageName).containsKey(DateUtil.getYesterday())) {
                    sum =getContainsKeyWifi(flowHistoryList.get(packageName),DateUtil.getToday()) -
                            flowHistoryList.get(packageName).get(DateUtil.getYesterday()).getFlowWifi();
                } else {
                    if (!DateUtil.getToday().equals(time_shundown))
                        sum = getContainsKeyWifi(flowHistoryList.get(packageName),DateUtil.getToday()) -
                                flowHistoryList.get(packageName).get(time_shundown).getFlowWifi();
                    else
                        sum =getContainsKeyWifi(flowHistoryList.get(packageName),DateUtil.getToday());
                }
            }
        } else {
            sum = 0;
        }
        LogUtil.e("flow_today_uid","packgerName="+packageName+" sum="+sum);
        return sum;
    }
    public static long getContainsKeyWifi(Map<String,BaseFlowModel> map,String key){
        if (map==null)return 0;
        if (map.containsKey(key))return map.get(key).getFlowWifi();
        else return 0;
    }

    /**
     * 获取packageName某天的移动数据流量
     * @param context
     * @param flowHistoryList
     * @param packageName
     * @return
     */
    public BaseFlowModel getBytesByUidOneDay(Context context,Map<String,Map<String,BaseFlowModel>> flowHistoryList,String packageName,String date){
        if (flowHistoryList != null && flowHistoryList.containsKey(packageName)) {
            if (flowHistoryList.get(packageName).containsKey(date)){
//                LogUtil.e("flow_package_day"," baseMoede="+flowHistoryList.get(packageName).get(date).toString());
                return flowHistoryList.get(packageName).get(date);
            }else return null;
        }else return null;
    }

    /**
     * 根据某个进程查询它当月的流量
     * @return
     */
//    public long getMonthBytesByUid(Context context,int uid,String packageName) {
//        //从开机到关机
////        long flow_mobile = getMobileBytes(uid);
//        long sum = 0;
//        String time_shundown = SharedPreUtils.getInstanse().getKeyValue(context, Constant.Time_Availble_month);
//        synchronized (Constant.flowHistoryList){
//            if (Constant.flowHistoryList!=null&&Constant.flowHistoryList.containsKey(packageName)){
//                if (TextUtils.isEmpty(time_shundown)){//
//                        sum=Constant.flowHistoryList.get(packageName).get(DateUtil.getToday());
//
//                }else {//关机
//                    if (!DateUtil.getToday().substring(0,7).equals(time_shundown.substring(0,7)))
//                        sum=Constant.flowHistoryList.get(packageName).get(DateUtil.getToday())-
//                                Constant.flowHistoryList.get(packageName).get(time_shundown);
//                    else sum=Constant.flowHistoryList.get(packageName).get(DateUtil.getToday());
//                }
//            }else {
//                sum=0;
//            }
//        }
//        LogUtil.e("flow_getMonthBytesByUid","month="+DateUtil.getToday().substring(6)+" sum="+sum);
//        return sum;
//    }
    /**
     * 根据某个进程查询它当月的流量
     * @return
     */
    public long getMonthBytesByUid(Context context,Map<String,Map<String,BaseFlowModel>> flowHistoryList,String packageName) {
        //从开机到关机
        long sum = 0;
        String time_last = SharedPreUtils.getInstanse().getKeyValue(context,Constant.Time_Availble_Last);
        String time_month_end = SharedPreUtils.getInstanse().getKeyValue(context, Constant.Time_Availble_month);
        if (flowHistoryList!=null&&flowHistoryList.containsKey(packageName)){
            if (TextUtils.isEmpty(time_month_end)||!flowHistoryList.get(packageName).containsKey(time_month_end)){//
                sum=getContainsKey(flowHistoryList.get(packageName),time_last);
            }else{
                sum=getContainsKey(flowHistoryList.get(packageName),time_last)-
                        getContainsKey(flowHistoryList.get(packageName),time_month_end);
            }
        }
        LogUtil.e("flow_getMonthBytesByUid","month="+time_last.substring(6)+" pacgage="+packageName+" sum="+sum);
        return sum;
    }
    public long getMonthBytesByUidMobile(Context context,Map<String,Map<String,BaseFlowModel>> flowHistoryList,String packageName) {
        //从开机到关机
        long sum = 0;
        String time_last = SharedPreUtils.getInstanse().getKeyValue(context,Constant.Time_Availble_Last);
        String time_month_end = SharedPreUtils.getInstanse().getKeyValue(context, Constant.Time_Availble_month);
        if (flowHistoryList!=null&&flowHistoryList.containsKey(packageName)){
            if (TextUtils.isEmpty(time_month_end)||!flowHistoryList.get(packageName).containsKey(time_month_end)){//
                sum=getContainsKeyMobile(flowHistoryList.get(packageName),time_last);
            }else{
                sum=getContainsKeyMobile(flowHistoryList.get(packageName),time_last)-
                        getContainsKeyMobile(flowHistoryList.get(packageName),time_month_end);
            }
        }
        LogUtil.e("flow_getMonthBytesByUid","month="+time_last.substring(6)+" pacgage="+packageName+" sum="+sum);
        return sum;
    }
    public long getMonthBytesByUidWifi(Context context,Map<String,Map<String,BaseFlowModel>> flowHistoryList,String packageName) {
        //从开机到关机
        long sum = 0;
        String time_last = SharedPreUtils.getInstanse().getKeyValue(context,Constant.Time_Availble_Last);
        String time_month_end = SharedPreUtils.getInstanse().getKeyValue(context, Constant.Time_Availble_month);
        if (flowHistoryList!=null&&flowHistoryList.containsKey(packageName)){
            if (TextUtils.isEmpty(time_month_end)||!flowHistoryList.get(packageName).containsKey(time_month_end)){//
                sum=getContainsKeyWifi(flowHistoryList.get(packageName),time_last);
            }else{
                sum=getContainsKeyWifi(flowHistoryList.get(packageName),time_last)-
                        getContainsKeyWifi(flowHistoryList.get(packageName),time_month_end);
            }
        }
        LogUtil.e("flow_getMonthBytesByUid","month="+time_last.substring(6)+" pacgage="+packageName+" sum="+sum);
        return sum;
    }

    /**
     * 关机
     * 保存总流量信息
     */
    public void saveFlowDate(Context context,String netStauts,String saveTime,String lastTime,long sum){
        if (Constant.flowTodayMonth==null)return;

        //开机到现在的总流量mobile
//        long totail = TrafficStats.getTotalRxBytes()+TrafficStats.getTotalTxBytes();
//        LogUtil.d("flow totail saveFlowDate",totail+"");
        long flowWifi=0;
        long flowMobile=0;
        long flowSize=0;
        long flowMobileD=0;
        long flowWifiD=0;
        long temp=0;
        MonthFlowModel monthFlowModel=null;
        synchronized (Constant.flowTodayMonth) {
            if (Constant.flowTodayMonth.containsKey(lastTime)) {//第二次保存
                MonthFlowModel model = Constant.flowTodayMonth.get(lastTime);
                //上一次是否有值
                if (!TextUtils.isEmpty(lastTime) && Constant.flowTodayMonth.containsKey(lastTime)) {//是否
//                    if (totail-model.getFlowSizeTemp()<0){
//                        temp = totail;
//                    }else {
//                        temp = totail - model.getFlowSizeTemp();
//                    }
                    temp=sum;

                    flowSize = model.getFlowSize() + temp;
                    if (lastTime.equals(saveTime)) {//同一天
                        if (netStauts.equals(Constant.NET_TYPE_wifi)) {
                            flowWifi = model.getFlowWifi() + temp;
                            flowMobile = model.getFlowMobile();
                            flowWifiD = model.getFlowWifiD() + temp;
                            flowMobileD = model.getFlowMobileD();
                        } else {
                            flowWifi = model.getFlowWifi();
                            flowMobile = model.getFlowMobile() + temp;
                            flowWifiD = model.getFlowWifiD();
                            flowMobileD = model.getFlowMobileD() + temp;
                        }
                        monthFlowModel = new MonthFlowModel(saveTime, flowWifi, flowMobile, flowWifiD, flowMobileD, flowSize, sum, netStauts);
                        Constant.flowTodayMonth.put(saveTime, monthFlowModel);
                    } else {//第二天
                        if (netStauts.equals(Constant.NET_TYPE_wifi)) {
                            flowWifi = model.getFlowWifi() + temp;
                            flowMobile = model.getFlowMobile();
                        } else {
                            flowWifi = model.getFlowWifi();
                            flowMobile = model.getFlowMobile() + temp;
                        }
                        flowMobileD = 0;
                        flowWifiD = 0;
                        //第二个月开始保存
                        if (!lastTime.substring(0, 7).equals(saveTime.substring(0, 7))) {//
                            flowSize = 0;
                            flowWifi = 0;
                            flowMobile = 0;
                        }
                        monthFlowModel = new MonthFlowModel(saveTime, flowWifi, flowMobile, flowWifiD, flowMobileD, flowSize, sum, netStauts);
                        Constant.flowTodayMonth.put(saveTime, monthFlowModel);
                    }
                } else {//首次保存日期值
                    flowSize = 0;
                    flowWifi = 0;
                    flowMobile = 0;
                    flowMobileD = 0;
                    flowWifiD = 0;
                    monthFlowModel = new MonthFlowModel(saveTime, flowWifi, flowMobile, flowWifiD, flowMobileD, flowSize, sum, netStauts);
                    Constant.flowTodayMonth.put(saveTime, monthFlowModel);
                }
            } else {//首次保存
                flowSize = 0;
                flowWifi = 0;
                flowMobile = 0;
                flowWifiD = 0;
                flowMobileD = 0;
                monthFlowModel = new MonthFlowModel(saveTime, flowWifi, flowMobile, flowWifiD, flowMobileD, flowSize, sum, netStauts);
                Constant.flowTodayMonth.put(saveTime, monthFlowModel);
            }
        }
        saveAppFlowByte(context,Constant.flowTodayMonth);
    }

    /**
     * 保存总流量
     * @param context
     * @param map
     */
    public void saveAppFlowByte(Context context,Map<String,MonthFlowModel> map){
        //把map转换成 保存xml文件
        String flowDay = GsonUtil.mapToJsonString(map);
        LogUtil.e("flow_保存总流量", flowDay.toString());
        boolean flag = SharedPreUtils.getInstanse().putKeyValue(context,Constant.FlowPaxTools_Totail,flowDay);
//        LogUtil.e("flow_保存总流量 是否成功",flag+"");
    }

    /**
     * 保存uid的流量
     * 关机
     * @param uid
     */
    public long saveFlowByUid(Context context,int uid,String packgeName,String appName,String netStauts,String saveTime,String lastTime) {
        if (Constant.flowHistoryList == null) return 0;
        //开机到现在的流量
        long totail = TrafficStats.getUidRxBytes(uid) + TrafficStats.getUidTxBytes(uid);

        long flowWifi=0;
        long flowMobile=0;
        long flowSize=0;
        long flowMobileD=0;
        long flowWifiD=0;
        long temp=0;
        BaseFlowModel baseFlowModel=null;
        Map<String,BaseFlowModel> model=null;
        synchronized (Constant.flowHistoryList){
            if (Constant.flowHistoryList != null && Constant.flowHistoryList.containsKey(packgeName) ) {
                model=Constant.flowHistoryList.get(packgeName);
                //上一次是否有值
                if (!TextUtils.isEmpty(lastTime)&&model.containsKey(lastTime)){//是否
                    temp = totail-model.get(lastTime).getFlowSizeTemp();
                    if (temp<0){
                        temp = totail;
                    }
//                    LogUtil.d("flowtotail_saveFlowByUid","totail="+totail+"  temp="+(temp));
                    flowSize = model.get(lastTime).getFlowSize()+temp;
                    if (lastTime.equals(saveTime)){//同一天
                        if (netStauts.equals(Constant.NET_TYPE_wifi)){
                            flowWifi=model.get(lastTime).getFlowWifi()+temp;
                            flowMobile=model.get(lastTime).getFlowMobile();
                            flowWifiD=model.get(lastTime).getFlowWifiD()+temp;
                            flowMobileD=model.get(lastTime).getFlowMobileD();
                        }else{
                            flowWifi=model.get(lastTime).getFlowWifi();
                            flowMobile=model.get(lastTime).getFlowMobile()+temp;
                            flowWifiD=model.get(lastTime).getFlowWifiD();
                            flowMobileD=model.get(lastTime).getFlowMobileD()+temp;
                        }
                        baseFlowModel=new BaseFlowModel(uid,packgeName,appName,saveTime,flowWifi,flowMobile,flowWifiD,flowMobileD,flowSize,totail,netStauts);
                        Constant.flowHistoryList.get(packgeName).put(saveTime, baseFlowModel);
                    }else {//第二天
                        if (netStauts.equals(Constant.NET_TYPE_wifi)){
                            flowWifi=model.get(lastTime).getFlowWifi()+temp;
                            flowMobile=model.get(lastTime).getFlowMobile();
                        }else{
                            flowWifi=model.get(lastTime).getFlowWifi();
                            flowMobile=model.get(lastTime).getFlowMobile()+temp;
                        }
                        flowMobileD=0;
                        flowWifiD=0;
                        //第二个月开始保存
                        if (!lastTime.substring(0,7).equals(saveTime.substring(0,7))){//
                            flowSize=0;
                            flowWifi=0;
                            flowMobile=0;
                        }
                        baseFlowModel=new BaseFlowModel(uid,packgeName,appName,saveTime,flowWifi,flowMobile,flowWifiD,flowMobileD,flowSize,totail,netStauts);
                        Constant.flowHistoryList.get(packgeName).put(saveTime, baseFlowModel);
                    }
                }else {//首次保存日期值
                    flowSize=0;
                    flowWifi=0;
                    flowMobile=0;
                    flowMobileD=0;
                    flowWifiD=0;
                    baseFlowModel=new BaseFlowModel(uid,packgeName,appName,saveTime,flowWifi,flowMobile,flowWifiD,flowMobileD,flowSize,totail,netStauts);
                    Constant.flowHistoryList.get(packgeName).put(saveTime, baseFlowModel);
                }
            }else{//首次保存
                flowSize = 0;
                flowWifi=0;flowMobile=0;
                flowWifiD=0;flowMobileD=0;
                baseFlowModel=new BaseFlowModel(uid,packgeName,appName,saveTime,flowWifi,flowMobile,flowWifiD,flowMobileD,flowSize,totail,netStauts);
                Map<String,BaseFlowModel> map = new HashMap<String,BaseFlowModel>();
                map.put(saveTime,baseFlowModel);
                Constant.flowHistoryList.put(packgeName,map);
            }
        }
//        LogUtil.d("flowtotail_uid",baseFlowModel==null?"null":baseFlowModel.toString());
//        saveFlowUidBytes(context,Constant.flowHistoryList);
        return temp;
    }

    /**
     * 各个app流量保存
     * @param context
     * @param map
     * @return
     */
    public boolean saveFlowUidBytes(Context context,Map<String,Map<String,BaseFlowModel>> map){
        String flow = GsonUtil.mapToJsonIntegerModel(map);
//        LogUtil.d("flowsaveFlowUidBytes 各个app",flow.toString());
        boolean flag=SharedPreUtils.getInstanse().putKeyValue(context,Constant.FlowPaxToolsByUid,flow);
        return flag;
    }

    /**
     * 获取月的
     * @return
     */
    public Map<String,MonthFlowModel> getFlowTotail(Context context){
        String totail = SharedPreUtils.getInstanse().getKeyValue(context,Constant.FlowPaxTools_Totail);
        Gson gson = new Gson();
        Type type1 = new TypeToken<Map<String, MonthFlowModel>>(){}.getType();
        Map<String, MonthFlowModel> map = gson.fromJson(totail, type1);
        LogUtil.e("flowmonth",map==null?"null":map.toString());
       if (map!=null)
        for (Map.Entry<String, MonthFlowModel> kk  : map.entrySet()){
//            LogUtil.e("flowmonth_kk","key="+kk.getKey()+"  value="+kk.getValue());
        }
        return map==null?new HashMap<String,MonthFlowModel>():map;
    }

    /**
     * uid 所有的流量
     * @return
     */
    public Map<String,Map<String,BaseFlowModel>> getFlowTotailUid(Context context){
        String totail = SharedPreUtils.getInstanse().getKeyValue(context,Constant.FlowPaxToolsByUid);
        Gson gson = new Gson();
        Type type1 = new TypeToken<Map<String,Map<String,BaseFlowModel>>>(){}.getType();
        Map<String,Map<String,BaseFlowModel>> map=null;
        try{
             map = gson.fromJson(totail, type1);
            LogUtil.e("flowuid",map==null?"null":map.toString());
        }catch (Exception e){
            cleanCachaFlowByUid(context);
        }

        return map==null?new HashMap<String,Map<String,BaseFlowModel>>():map;
    }

    /**
     * 清除流量缓存
     * flow
     */
    public void cleanCachaFlow(Context context){
        if (Constant.flowTodayMonth!=null){
            Constant.flowTodayMonth.clear();
            SharedPreUtils.getInstanse().putKeyValue(context,Constant.FlowPaxTools_Totail,"");
            LogUtil.e("删除 总流量");
        }
    }
    public void cleanCachaFlowByUid(Context context){
        if (Constant.flowHistoryList!=null){
            Constant.flowHistoryList.clear();
            SharedPreUtils.getInstanse().putKeyValue(context,Constant.FlowPaxToolsByUid,"");
            LogUtil.e("删除 所以的app流量");
            SharedPreUtils.getInstanse().putKeyValue(context,Constant.Time_ShunDown,null);
        }
    }

    public static long getTotailByUid(int uid){
        if ((TrafficStats.getUidRxBytes(uid) == -1) && (TrafficStats.getUidTxBytes(uid) == -1)) {
            return getTotalBytesManual(uid);
        }
        return 0;
    }



    /**
     * 通过uid查询文件夹中的数据
     * @param localUid
     * @return
     */
    private static Long getTotalBytesManual(int localUid) {
//        Log.e("BytesManual*****", "localUid:" + localUid);
        File dir = new File("/proc/uid_stat/");
        String[] children = dir.list();
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < children.length; i++) {
            stringBuffer.append(children[i]);
            stringBuffer.append("   ");
        }
//        Log.e("children*****", children.length + "");
//        Log.e("children22*****", stringBuffer.toString());
        if (!Arrays.asList(children).contains(String.valueOf(localUid))) {
            return 0L;
        }
        File uidFileDir = new File("/proc/uid_stat/" + String.valueOf(localUid));
        File uidActualFileReceived = new File(uidFileDir, "tcp_rcv");
        File uidActualFileSent = new File(uidFileDir, "tcp_snd");
        String textReceived = "0";
        String textSent = "0";
        try {
            BufferedReader brReceived = new BufferedReader(new FileReader(uidActualFileReceived));
            BufferedReader brSent = new BufferedReader(new FileReader(uidActualFileSent));
            String receivedLine;
            String sentLine;

            if ((receivedLine = brReceived.readLine()) != null) {
                textReceived = receivedLine;
//                Log.e("receivedLine*****", "receivedLine:" + receivedLine);
            }
            if ((sentLine = brSent.readLine()) != null) {
                textSent = sentLine;
//                Log.e("sentLine*****", "sentLine:" + sentLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
//            Log.e("IOException*****", e.toString());
        }
//        Log.e("BytesManualEnd*****", "localUid:" + localUid);
        return Long.valueOf(textReceived).longValue() + Long.valueOf(textSent).longValue();
    }

}
