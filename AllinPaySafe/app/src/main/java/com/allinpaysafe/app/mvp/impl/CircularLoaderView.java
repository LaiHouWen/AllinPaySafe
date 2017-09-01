package com.allinpaysafe.app.mvp.impl;

import android.content.Context;

/**
 *
 * 水波纹 接口
 *
 */
public interface CircularLoaderView extends View {

//    void initViews(MenuListAdapter recyclerAdapter);
    void initViews();

    void updateViews(long sum, long available, float percent);

    void onCleanStarted(Context context);

    void onCleanCompleted(Context context, long memory);
}
