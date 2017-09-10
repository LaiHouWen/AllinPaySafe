package com.pax.ipp.tools.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.pax.ipp.tools.Constant;
import com.pax.ipp.tools.utils.LogUtil;

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
            //example:启动程序
            LogUtil.e(TAG,"启动程序 闹钟 定时====");
            context.startService(new Intent(context, SaveFlowService.class).
                    putExtra(Constant.TIME_TEMP,System.currentTimeMillis()));
        }

    }


}
