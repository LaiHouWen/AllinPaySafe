package com.pax.ipp.tools;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.pax.ipp.tools.service.TrafficAidlService;
import com.pax.ipp.tools.service.RequestAlarmReceiver;
import com.pax.ipp.tools.service.SaveFlowService;
import com.pax.ipp.tools.utils.DateUtil;
import com.pax.ipp.tools.utils.FlowUtil;

import java.util.Calendar;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;

/**
 * Created by houwen.lai on 2017/8/31.
 *
 */

public class App extends Application {

    private static App mInstance;

    public static App getInstance() {
        return mInstance;
    }

    public static Context getContext() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Install CustomActivityOnCrash
        CustomActivityOnCrash.install(this);

        Constant.flowTodayMonth = FlowUtil.getInstance().getFlowTotail(this);
        Constant.flowHistoryList = FlowUtil.getInstance().getFlowTotailUid(this);

        startService(new Intent(this, SaveFlowService.class).
                putExtra(Constant.TIME_TEMP, DateUtil.getToday()));

        setAlarmTime(this);
        bindService();
    }

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

    private TrafficFlowManager trafficFlowManager;
    public void bindService() {
        Intent intent = new Intent(this, TrafficAidlService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e("aidl", "绑定开始");
            trafficFlowManager = TrafficFlowManager.Stub.asInterface(service);
//            try {

//                bookManager.registerListener(mOnNewBookArrivedListener);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
        }

        @Override public void onServiceDisconnected(ComponentName name) {
            trafficFlowManager=null;
            Log.e("aidl", "绑定结束");
        }
    };
}
