package com.allinpaysafe.app.mvp.impl;

import android.os.Bundle;

/**
 * Created by Administrator on 2016/5/4.
 */
public interface Presenter {

    void onCreate(Bundle savedInstanceState);

    void onResume();

    void onStart();

    void onPause();

    void onStop();

    void onDestroy();

    void attachView(View v);
}
