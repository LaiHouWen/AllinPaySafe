package com.pax.ipp.tools.service;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.text.TextUtils;

import com.pax.ipp.tools.Constant;
import com.pax.ipp.tools.mvp.presenter.CircularLoaderPresenter;
import com.pax.ipp.tools.utils.DateUtil;
import com.pax.ipp.tools.utils.LogUtil;
import com.pax.ipp.tools.utils.SharedPreUtils;

/**
 * Created by Administrator on 2017/9/8.
 * 闹钟
 * 保存 数据
 */

public class RequestAlarmReceiver extends BroadcastReceiver {
    final String TAG = RequestAlarmReceiver.class.getSimpleName();
    final int REQUEST_CODE = 0x010101;

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.e(TAG,"===Intent=");
        if (intent.getAction().equals(Constant.ACTION_SEND_ALARM)) {
            String today = DateUtil.getToday();
            String lastDate = SharedPreUtils.getInstanse().getKeyValue(context,Constant.Time_ShunDown);
            if (!TextUtils.isEmpty(lastDate)&&lastDate.equals(today)){//今天开关机
            }else {
                //月
                if (!TextUtils.isEmpty(lastDate)&&lastDate.substring(6).equals(today.substring(6))){
                }else {
                    SharedPreUtils.getInstanse().putKeyValue(context, Constant.Time_Availble_month,lastDate);
                }
            }

            //example:启动程序
            LogUtil.e(TAG,"启动程序 闹钟 定时====");
            context.startService(new Intent(context, SaveFlowService.class).
                    putExtra(Constant.TIME_TEMP, DateUtil.getToday()));

//            Intent intent_1 =new Intent(context, SaveFlowService.class);
//            context.bindService(intent_1,connection, Context.BIND_AUTO_CREATE);

        }
    }
    private SaveFlowService saveFlowService;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            saveFlowService
                    = ((SaveFlowService.CleanerServiceBinder) service).getService();
            saveFlowService.saveFlowDate( DateUtil.getToday());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

}
