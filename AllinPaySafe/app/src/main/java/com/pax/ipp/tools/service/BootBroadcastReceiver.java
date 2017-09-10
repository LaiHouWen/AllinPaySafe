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

import com.pax.ipp.tools.Constant;
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
    final int REQUEST_CODE = 0x010101;

    @Override
    public void onReceive(Context context, Intent intent) {
        //开机广播
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            //example:启动程序
            LogUtil.e(TAG,"开机了");
        }

        //系统关闭广播接收器

        if (intent.getAction().equals(Intent.ACTION_SHUTDOWN)) {
            LogUtil.e(TAG,"关机了");
            //关机时间
            long shunt_time = System.currentTimeMillis();
            SharedPreUtils.getInstanse().putKeyValueLong(context, Constant.Time_ShunDown, shunt_time);
            LogUtil.e("flow_time-shunt time保存时间-",shunt_time+"");
//            Intent intent1 = new Intent(context, SaveFlowService.class);
//            intent1.putExtra(Constant.TIME_TEMP,System.currentTimeMillis());
//            context.bindService(intent1,
//                    mServiceConnection, Context.BIND_AUTO_CREATE);

            FlowUtil.getInstance().saveFlowDate(context,shunt_time);//保存总流量

            context.startService(new Intent(context, SaveFlowService.class).
                    putExtra(Constant.TIME_TEMP,shunt_time));
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

    public void setAlarmTime(Context context){
        Intent intent = new Intent(context, RequestAlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context,
                REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        // Schedule the alarm!
        AlarmManager am = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 11);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 50);
        calendar.set(Calendar.MILLISECOND, 0);

        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                INTERVAL, sender);

    }



}
