// AppFlowManager.aidl
package com.pax.ipp.tools;

import com.pax.ipp.tools.AppFlow;

// Declare any non-default types here with import statements

interface AppFlowManager {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
//    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
//            double aDouble, String aString);

    List<AppFlow> getAppFlowList(); // 返回各个app流量列表

    String getAppFlowJsonString();//返回各个app流量列表转化成jsonstring

    long getTodayAppFlow();//返回今天的总流量

    long getMonthAppFlow();//返回本月的总流量


}
