// AppFlowManager.aidl
package com.pax.ipp.tools;

import com.pax.ipp.tools.TrafficFlowBean;

// Declare any non-default types here with import statements

interface TrafficFlowManager {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
//    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
//            double aDouble, String aString);

    List<TrafficFlowBean> getDayFlow(); // 获取各app当日流量使用情况

    List<TrafficFlowBean> getWeekFlow();//获取各app过去七日的流量使用情况

    List<TrafficFlowBean>  getMonthFlow();//获取各app本月流量使用情况

}
