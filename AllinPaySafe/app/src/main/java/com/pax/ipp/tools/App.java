package com.pax.ipp.tools;

import android.app.Application;
import android.content.Context;

import com.pax.ipp.tools.model.BaseFlowModel;
import com.pax.ipp.tools.model.FlowModel;

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
    }

    public List<BaseFlowModel> baseFlowModels;

    public HashMap<Integer ,HashMap<Long,FlowModel> > ddddd;


}
