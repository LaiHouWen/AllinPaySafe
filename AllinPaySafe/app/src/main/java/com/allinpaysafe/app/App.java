package com.allinpaysafe.app;

import android.app.Application;
import android.content.Context;

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
    }
}
