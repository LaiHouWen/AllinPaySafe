package com.pax.ipp.tools.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

/**
 * Created by houwen.lai on 2017/9/14.
 * 监听网络状态的变化
 */

public class NetStatusBroadcastReceiver extends BroadcastReceiver{


    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent==null)return;
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)){

        }


    }


}
