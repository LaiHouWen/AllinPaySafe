package com.pax.ipp.tools.ui.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.pax.ipp.tools.R;

/**
 * Created by houwen.lai on 2017/9/7.
 *
 *  loading = new Loading_view(this,R.style.CustomDialog);
 loading.show();
 new Handler().postDelayed(new Runnable() {//定义延时任务模仿网络请求
 @Override
 public void run() {
 loading.dismiss();//3秒后调用关闭加载的方法
 }
 }, 3000);
 */

public class Loading_view extends ProgressDialog {

    public Loading_view(Context context) {
        super(context);
    }

    public Loading_view(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(getContext());
    }
    private void init(Context context) {
        setCancelable(true);
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.view_loading);//loading的xml文件
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);
    }
    @Override
    public void show() {//开启
        super.show();
    }
    @Override
    public void dismiss() {//关闭
        super.dismiss();
    }

}
