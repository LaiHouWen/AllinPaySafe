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
     * 获取当天的流量
     * @return
     */
    public long getTodayBytes(Context context){
        //开机到现在的总流量mobile
        long totail = TrafficStats.getTotalRxBytes()+TrafficStats.getTotalTxBytes();
        long sum = 0;
        LogUtil.d("flow totail",totail+"");
        //有效时间
        String time_shundown =  SharedPreUtils.getInstanse().getKeyValue(context,Constant.Time_ShunDown);
        LogUtil.d("flow 有效时间",time_shundown);
        String today = DateUtil.getToday();
        synchronized (Constant.flowTodayMonth) {
            if (Constant.flowTodayMonth != null) {
                if (TextUtils.isEmpty(time_shundown)) {//一直没有关机
                    if (Constant.flowTodayMonth.containsKey(DateUtil.getYesterday())) {//判断昨天是否有值
                        sum = Constant.flowTodayMonth.get(today) -
                                Constant.flowTodayMonth.get(DateUtil.getYesterday());
                    } else {
                        sum = Constant.flowTodayMonth.get(DateUtil.getToday());
                    }
                } else {//关机
                    if (Constant.flowTodayMonth.containsKey(DateUtil.getYesterday())) {//判断昨天是否有值
                        sum = Constant.flowTodayMonth.get(DateUtil.getToday()) -
                                Constant.flowTodayMonth.get(DateUtil.getYesterday());
                    } else {//
                        if (!today.equals(time_shundown)) {
                            if (Constant.flowTodayMonth.containsKey(time_shundown))
                                sum = Constant.flowTodayMonth.get(today) -
                                        Constant.flowTodayMonth.get(time_shundown);
                            else sum = Constant.flowTodayMonth.get(today);
                        } else sum = Constant.flowTodayMonth.get(today);
                    }
                }
            }
        }
        LogUtil.e("flow 今日的流量=",sum+"");
        return sum;
    }

    /**
     * 获取当月的流量
     * @return
     */
    public long getMonthBytes(Context context){
        //开机到现在的总流量mobile
        long sum =0;
//        String avaible = SharedPreUtils.getInstanse().getKeyValue(context,Constant.Time_Availble);
        String time_shundown = SharedPreUtils.getInstanse().getKeyValue(context,Constant.Time_Availble_month);
        LogUtil.d("flow 有效时间month"," 有效月="+time_shundown);
        synchronized (Constant.flowTodayMonth) {
            if (Constant.flowTodayMonth != null) {
                if (TextUtils.isEmpty(time_shundown)) {
                    sum = Constant.flowTodayMonth.get(DateUtil.getToday());
                } else {
                    if (!DateUtil.getToday().substring(0, 7).equals(time_shundown.substring(0, 7)))
                        sum = Constant.flowTodayMonth.get(DateUtil.getToday()) -
                                Constant.flowTodayMonth.get(time_shundown);
                    else sum = Constant.flowTodayMonth.get(DateUtil.getToday());
                }
            }
        }
        LogUtil.e("flow 今月的流量=",sum+"");
        return sum;
    }

    /**
     * 根据某个进程查询它今天的流量
     * @return
     */
    public long getTodayBytesByUid(Context context,int uid,String packageName){
        //从开机到关机
//        long flow_mobile =  getMobileBytes(uid);
        long sum =0;
        String time_shundown = SharedPreUtils.getInstanse().getKeyValue(context,Constant.Time_ShunDown);
        synchronized (Constant.flowHistoryList) {
            if (Constant.flowHistoryList != null && Constant.flowHistoryList.containsKey(packageName)) {
                if (TextUtils.isEmpty(time_shundown)) {//无关机
                    if (Constant.flowHistoryList.get(packageName).containsKey(DateUtil.getYesterday())) {
                        sum = Constant.flowHistoryList.get(packageName).get(DateUtil.getToday()) -
                                Constant.flowHistoryList.get(packageName).get(DateUtil.getYesterday());
                    } else {
                        sum = Constant.flowHistoryList.get(packageName).get(DateUtil.getToday());
                    }
                } else {//关机
                    if (Constant.flowHistoryList.get(packageName).containsKey(DateUtil.getYesterday())) {
                        sum = Constant.flowHistoryList.get(packageName).get(DateUtil.getToday()) -
                                Constant.flowHistoryList.get(packageName).get(DateUtil.getYesterday());
                    } else {
                        if (!DateUtil.getToday().equals(time_shundown))
                            sum = Constant.flowHistoryList.get(packageName).get(DateUtil.getToday()) -
                                    Constant.flowHistoryList.get(packageName).get(time_shundown);
                        else
                            sum = Constant.flowHistoryList.get(packageName).get(DateUtil.getToday());
                    }
                }
            } else {
                sum = 0;
            }
        }
        LogUtil.e("flow_uid","uid="+uid+" sum="+sum);
        return sum;
    }

    /**
     * 根据某个进程查询它当月的流量
     * @return
     */
    public long getMonthBytesByUid(Context context,int uid,String packageName) {
        //从开机到关机
//        long flow_mobile = getMobileBytes(uid);
        long sum = 0;
        String time_shundown = SharedPreUtils.getInstanse().getKeyValue(context, Constant.Time_Availble_month);
        synchronized (Constant.flowHistoryList){
            if (Constant.flowHistoryList!=null&&Constant.flowHistoryList.containsKey(packageName)){
                if (TextUtils.isEmpty(time_shundown)){//
                        sum=Constant.flowHistoryList.get(packageName).get(DateUtil.getToday());

                }else {//关机
                    if (!DateUtil.getToday().substring(0,7).equals(time_shundown.substring(0,7)))
                        sum=Constant.flowHistoryList.get(packageName).get(DateUtil.getToday())-
                                Constant.flowHistoryList.get(packageName).get(time_shundown);
                    else sum=Constant.flowHistoryList.get(packageName).get(DateUtil.getToday());
                }
            }else {
                sum=0;
            }
        }
        LogUtil.e("flow_getMonthBytesByUid","month="+DateUtil.getToday().substring(6)+" sum="+sum);
        return sum;
    }

    /**
     * 关机
     * 保存总流量信息
     */
    public long saveFlowDate(Context context,String shunDownTime){
        long sum =0;
        //开机到现在的总流量mobile
        long totail = TrafficStats.getTotalRxBytes()+TrafficStats.getTotalTxBytes();
        LogUtil.d("flow totail saveFlowDate",totail+"");
        //开机时间
        String time_shundown = SharedPreUtils.getInstanse().getKeyValue(context,Constant.Time_ShunDown);
        LogUtil.e("flow_保存总流量___","上一次关机时间="+time_shundown);
        String flowDay = "";
        synchronized (Constant.flowTodayMonth) {
            if (Constant.flowTodayMonth != null) {
                //安装后没有关机状态
                if (TextUtils.isEmpty(time_shundown)) {
                    sum = totail;
                } else if (Constant.flowTodayMonth.containsKey(time_shundown)) {//已经关机过
                    sum = Constant.flowTodayMonth.get(time_shundown) + totail;
                } else sum = totail;
            }

            if (Constant.flowTodayMonth != null)
                Constant.flowTodayMonth.put(DateUtil.getToday(), sum);//保存到map中
            LogUtil.e("flow_保存总流量___", "key=" + shunDownTime + " value=" + sum);
            //把map转换成 保存xml文件

            if (Constant.flowTodayMonth != null)
                flowDay = GsonUtil.mapToJsonString(Constant.flowTodayMonth);
            LogUtil.e("flow_保存总流量", flowDay.toString());
        }
        boolean flag = SharedPreUtils.getInstanse().putKeyValue(context,Constant.FlowPaxTools_Totail,flowDay);
        LogUtil.e("flow_保存总流量 是否成功",flag+"");
        return sum;
    }

    /**
     * 保存uid的流量
     * 关机
     * @param uid
     */
    public long saveFlowByUid(Context context,int uid,String packgeName,String shunDownTime) {
        if (Constant.flowHistoryList == null) return 0;
        //开机到现在的流量
        long totail = TrafficStats.getUidRxBytes(uid) + TrafficStats.getUidTxBytes(uid);
        LogUtil.d("flow totail saveFlowByUid",totail+"");
//        long totail = getTotailByUid(uid);
        String time_shundown = SharedPreUtils.getInstanse().getKeyValue(context, Constant.Time_ShunDown);
        long sum = 0;
        synchronized (Constant.flowHistoryList){
            if (Constant.flowHistoryList != null && Constant.flowHistoryList.containsKey(uid) ) {
                if (TextUtils.isEmpty(time_shundown)) {//没有关机
                    sum = totail;
                    Constant.flowHistoryList.get(packgeName).put(shunDownTime, sum);
                }else {//关机了
                    if (!TextUtils.isEmpty(time_shundown)&&Constant.flowHistoryList.get(uid).containsKey(time_shundown))
                    sum = totail+Constant.flowHistoryList.get(packgeName).get(time_shundown);
                    else sum = totail;
                    Constant.flowHistoryList.get(packgeName).put(shunDownTime, sum);
                }
             }else{
                sum = totail;
                Map<String,Long> map = new HashMap<String,Long>();
                map.put(shunDownTime,sum);
                Constant.flowHistoryList.put(packgeName,map);
            }
        }
        LogUtil.e("flow_保存总流量_add__uid_","uid="+uid +" packge="+packgeName+" key="+shunDownTime+" value="+totail);
        return sum;
    }

    public boolean saveFlowUidBytes(Context context){
        String flow = "";
        if (Constant.flowHistoryList!=null){
            synchronized (Constant.flowHistoryList){
                flow = GsonUtil.mapToJsonIntegerString(Constant.flowHistoryList);
            }
        }
        LogUtil.e("flow-流量保存 uid",flow.toString());
        boolean flag=SharedPreUtils.getInstanse().putKeyValue(context,Constant.FlowPaxToolsByUid,flow);
        return flag;
    }

    /**
     *
     * @return
     */
    public Map<String,Long> getFlowTotail(Context context){
        String totail = SharedPreUtils.getInstanse().getKeyValue(context,Constant.FlowPaxTools_Totail);
        Gson gson = new Gson();
        Type type1 = new TypeToken<Map<String, Long>>(){}.getType();
        Map<String, Long> map = gson.fromJson(totail, type1);
        LogUtil.e("flow--读取总流量",map==null?"null":map.toString());
       if (map!=null)
        for (Map.Entry<String, Long> kk  : map.entrySet()){
            LogUtil.e("flow--读取总流量","key="+kk.getKey()+"  value="+kk.getValue());
        }
        return map==null?new HashMap<String,Long>():map;
    }

    /**
     * uid 所有的流量
     * @return
     */
    public Map<String,Map<String,Long>> getFlowTotailUid(Context context){
        String totail = SharedPreUtils.getInstanse().getKeyValue(context,Constant.FlowPaxToolsByUid);
        Gson gson = new Gson();
        Type type1 = new TypeToken<Map<String,Map<String,Long>>>(){}.getType();
        Map<String,Map<String,Long>> map = gson.fromJson(totail, type1);
        LogUtil.e("flow--读取uid总流量",map==null?"null":map.toString());
        return map==null?new HashMap<String,Map<String,Long>>():map;
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
