package com.pax.ipp.tools.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;

import com.pax.ipp.tools.Constant;
import com.pax.ipp.tools.utils.DateUtil;
import com.pax.ipp.tools.utils.FlowUtil;
import com.pax.ipp.tools.utils.LogUtil;
import com.pax.ipp.tools.utils.SharedPreUtils;

import java.util.Calendar;

/**
 * Created by houwwen.lai on 2017/9/8.
 *
 * 获取开关机广播
 * 系统启动完成广播接收器
 *
 * 注: onReceive中代码的执行时间不要超过5s,否则android会弹出超时dialog.
 如果需要做耗时处理,可以在onReceive()里开始一个Service,让Service去做耗时处理.
 最好不要用子线程,因为BroadcastReceiver的生命周期很短,子线程可能还没有结束BroadcastReceiver就先结束了.
 */

public class BootBroadcastReceiver extends BroadcastReceiver {
    final String TAG = BootBroadcastReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        //开机广播
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            //example:启动程序
            LogUtil.e(TAG,"开机了");

            LogUtil.e(TAG,"启动程序 闹钟 定时====");
            context.startService(new Intent(context, SaveFlowService.class).
                    putExtra(Constant.TIME_TEMP, DateUtil.getToday())
                    .putExtra(Constant.TIME_TEMP_INIT,Constant.TIME_TEMP_INIT));

            String today = DateUtil.getToday();
            String lastDate = SharedPreUtils.getInstanse().getKeyValue(context,Constant.Time_ShunDown);
            if (!TextUtils.isEmpty(lastDate)&&lastDate.equals(today)){//今天开关机
            }else {
                //月
                if (!TextUtils.isEmpty(lastDate)&&!lastDate.substring(0,7).equals(today.substring(0,7))){
                    SharedPreUtils.getInstanse().putKeyValue(context, Constant.Time_Availble_month,lastDate);
                }
                //有效值
                SharedPreUtils.getInstanse().putKeyValue(context, Constant.Time_Availble, lastDate);
            }
            //保存开机时间
            SharedPreUtils.getInstanse().putKeyValue(context,Constant.Time_StartDown,today);
            //月
            setAlarmTime(context);//启动定时器
        }

        //系统关闭广播接收器

        if (intent.getAction().equals(Intent.ACTION_SHUTDOWN)) {

            LogUtil.e(TAG,"关机了");
            String type= DateUtil.getToday();//年月日
            SharedPreUtils.getInstanse().putKeyValue(context, Constant.Time_ShunDown, type);
            LogUtil.e("flow_time-shunt关机 time保存时间=",type+"");
            //开机时间
            String startDate = SharedPreUtils.getInstanse().getKeyValue(context,Constant.Time_StartDown);

            if (!TextUtils.isEmpty(startDate)&&startDate.equals(type)){//今天开关机
            }else {
                //月
                if (!TextUtils.isEmpty(startDate)&&!type.substring(0,7).equals(startDate.substring(0,7))){
                    SharedPreUtils.getInstanse().putKeyValue(context, Constant.Time_Availble_month,type);
                }
                //有效值
                SharedPreUtils.getInstanse().putKeyValue(context, Constant.Time_Availble, type);
            }

            FlowUtil.getInstance().saveAppFlowByte(context,Constant.flowTodayMonth);//保存总流量
            FlowUtil.getInstance().saveFlowUidBytes(context,Constant.flowHistoryList);

            context.startService(new Intent(context, SaveFlowService.class).
                    putExtra(Constant.TIME_TEMP,type));
        }
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private static final int INTERVAL = 1000 * 60 * 60 * 24;// 24h
    final int REQUEST_CODE = 0x010101;

    public void setAlarmTime(Context context){
        Intent intent = new Intent(context, RequestAlarmReceiver.class);
        intent.setAction(Constant.ACTION_SEND_ALARM);
        PendingIntent sender = PendingIntent.getBroadcast(context,
                REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        // Schedule the alarm!
        AlarmManager am = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 19);
        calendar.set(Calendar.MINUTE,02);
        calendar.set(Calendar.SECOND, 20);
        calendar.set(Calendar.MILLISECOND, 0);

        //触发服务的起始时间
        long triggerAtTime = SystemClock.elapsedRealtime();

        am.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtTime,
                3*60*1000, sender);

//        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
//                INTERVAL, sender);


    }



}
