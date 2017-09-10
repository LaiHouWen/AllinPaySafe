package com.pax.ipp.tools;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.pax.ipp.tools.model.BaseFlowModel;
import com.pax.ipp.tools.model.FlowModel;
import com.pax.ipp.tools.service.RequestAlarmReceiver;
import com.pax.ipp.tools.utils.FlowUtil;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

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

        Constant.flowTodayMonth = FlowUtil.getInstance().getFlowTotail(this);
        Constant.flowHistoryList = FlowUtil.getInstance().getFlowTotailUid(this);

        setAlarmTime(this);
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

        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                300*1000, sender);

//        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
//                INTERVAL, sender);

    }

}
