package com.pax.ipp.tools.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.pax.ipp.tools.App;
import com.pax.ipp.tools.R;

import java.util.Date;

public class ToastUtil {

    public static void ToastLong(Context context, String text) {
        Toast.makeText(context.getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }

    public static void ToastShort(Context context, String text) {
        Toast.makeText(context.getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    public static void ToastShort(Context context, int resId) {
        Toast.makeText(context.getApplicationContext(), resId, Toast.LENGTH_SHORT).show();
    }

    public static void showSafeToast(final Activity activity, final String msg) {
        if (Thread.currentThread().getName().equals("main")) {
            Toast.makeText(activity.getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        } else {
            activity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    Toast.makeText(activity.getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
/******************************************************************************/

    public static volatile ToastUtil mToastUtils;

    public static ToastUtil getInstance() {
        ToastUtil toast = mToastUtils;
        if(null == toast){
            synchronized (ToastUtil.class){
                toast = mToastUtils;
                if(null == toast){
                    toast = new ToastUtil();
                    mToastUtils = toast;
                }
            }
        }
        return toast;
    }

    private long time = 0;
    private final long intervalTime = 2000;//吐司的间隔时间

    public void ToastShortFromNet(String text) {
        long newTime = new Date().getTime();
        if (0 == time) {
            time = newTime;
            Toast.makeText(App.getContext(), text, Toast.LENGTH_SHORT).show();
        } else {
            long l = newTime - time;
            LogUtil.e("TIME**********************************=="+l);
            if (l >= intervalTime) {
                Toast.makeText(App.getContext(), text, Toast.LENGTH_SHORT).show();
                time = newTime;
            }
        }
    }

//    public static Toast toast;
//    public static void ToastLong(Context context, String text,int granty) {
//        toast = new Toast(context);
//        LayoutInflater inflate = (LayoutInflater)
//                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View v = inflate.inflate(R.layout.toast_costum, null);
//        TextView tv = (TextView)v.findViewById(R.id.message_toast);
//        tv.setText(text);
//        toast.setView(v);
//        toast.show();
//    }
//    public static void ToastLong(Context context, int resText,int granty) {
//        toast = new Toast(context);
//        LayoutInflater inflate = (LayoutInflater)
//                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View v = inflate.inflate(R.layout.toast_costum, null);
//        TextView tv = (TextView)v.findViewById(R.id.message_toast);
//        tv.setText(resText);
//        toast.setView(v);
//        toast.setDuration(Toast.LENGTH_LONG);
//        toast.show();
//    }

    private final static Context context = App.getInstance();
//    private final static Context context =null;

    private static Toast toast;
    private static long flagToast=0;

    public enum ToastDisplayTime {
        TOAST_DISPLAY_LONG,
        TOAST_DISPLAY_SHORT
    }

    @SuppressLint("ShowToast")
    public static void checkToast() {
        if (context!=null) {
            toast = Toast.makeText(context, null, Toast.LENGTH_SHORT);
        }
    }

    public static void setToastDisplayTime(ToastDisplayTime time){
        if(time== ToastDisplayTime.TOAST_DISPLAY_LONG)
            toast.setDuration(Toast.LENGTH_LONG);
        else {
            toast.setDuration(Toast.LENGTH_SHORT);
        }
    }

    public static void show(Activity activity,String msg, ToastDisplayTime time,View view,int yfloat) {
        toast = Toast.makeText(activity, null, Toast.LENGTH_SHORT);
        toast.setText(msg);
        setToastDisplayTime(time);
        toast.setGravity(Gravity.TOP, 0, yfloat);
        toast.setView(view);

        if (System.currentTimeMillis()-flagToast>2000) {
            flagToast=System.currentTimeMillis();
            toast.show();
        }
    }

    public static void show(String msg, ToastDisplayTime time) {
        if(TextUtils.isEmpty(msg))return;
        checkToast();
        toast.setText(msg);
        setToastDisplayTime(time);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        if (System.currentTimeMillis()-flagToast>2000) {
            flagToast=System.currentTimeMillis();
            toast.show();
        }
    }

    public static void show(int msg,int time) {
        checkToast();
        LayoutInflater inflate = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflate.inflate(R.layout.toast_costum, null);
        TextView tv = (TextView)v.findViewById(R.id.message_toast);
        tv.setText(msg);
        toast.setView(v);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(time);
        if (System.currentTimeMillis()-flagToast>2000) {
            flagToast=System.currentTimeMillis();
            toast.show();
        }
    }

    public static void showLong(String msg) {
        show(msg, ToastDisplayTime.TOAST_DISPLAY_SHORT);
    }

    public static void showLong(int msg) {
        show(msg, 1000);
    }

    public static void showShort(String msg) {
        show(msg, ToastDisplayTime.TOAST_DISPLAY_SHORT);
    }

    public static void showShortView(Activity activity, String msg,int layoutId,int yfloat) {
        View view = activity.getLayoutInflater().inflate(layoutId,null);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        show(activity,msg, ToastDisplayTime.TOAST_DISPLAY_SHORT, view, yfloat);
    }

    public static void showShort(int msg) {
        show(msg, 1000);
    }
}
