package com.pax.ipp.tools.model;

import java.io.Serializable;

/**
 * Created by houwen.lai on 2017/9/15.
 * 统计每天所有的app的总流量
 */

public class MonthFlowModel implements Serializable {

    private String date;//日期
    private long flowWifi;//wifi流量
    private long flowMobile;//移动数据
    private long flowWifiD;//wifi流量当天
    private long flowMobileD;//移动数据当天
    private long flowSize;//总流量
    private long flowSizeTemp;//开机清零流量
    private String typeNet;//网络状态

    public MonthFlowModel(String date, long flowWifi, long flowMobile, long flowWifiD, long flowMobileD, long flowSize, long flowSizeTemp, String typeNet) {
        this.date = date;
        this.flowWifi = flowWifi;
        this.flowMobile = flowMobile;
        this.flowWifiD = flowWifiD;
        this.flowMobileD = flowMobileD;
        this.flowSize = flowSize;
        this.flowSizeTemp = flowSizeTemp;
        this.typeNet = typeNet;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getFlowWifi() {
        return flowWifi;
    }

    public void setFlowWifi(long flowWifi) {
        this.flowWifi = flowWifi;
    }

    public long getFlowMobile() {
        return flowMobile;
    }

    public void setFlowMobile(long flowMobile) {
        this.flowMobile = flowMobile;
    }

    public long getFlowWifiD() {
        return flowWifiD;
    }

    public void setFlowWifiD(long flowWifiD) {
        this.flowWifiD = flowWifiD;
    }

    public long getFlowMobileD() {
        return flowMobileD;
    }

    public void setFlowMobileD(long flowMobileD) {
        this.flowMobileD = flowMobileD;
    }

    public long getFlowSize() {
        return flowSize;
    }

    public void setFlowSize(long flowSize) {
        this.flowSize = flowSize;
    }

    public long getFlowSizeTemp() {
        return flowSizeTemp;
    }

    public void setFlowSizeTemp(long flowSizeTemp) {
        this.flowSizeTemp = flowSizeTemp;
    }

    public String getTypeNet() {
        return typeNet;
    }

    public void setTypeNet(String typeNet) {
        this.typeNet = typeNet;
    }

    @Override
    public String toString() {
        return "MonthFlowModel{" +
                "date='" + date + '\'' +
                ", flowWifi=" + flowWifi +
                ", flowMobile=" + flowMobile +
                ", flowWifiD=" + flowWifiD +
                ", flowMobileD=" + flowMobileD +
                ", flowSize=" + flowSize +
                ", flowSizeTemp=" + flowSizeTemp +
                ", typeNet='" + typeNet + '\'' +
                '}';
    }
}
