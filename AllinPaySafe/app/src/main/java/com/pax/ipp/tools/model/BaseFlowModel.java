package com.pax.ipp.tools.model;

import java.io.Serializable;

/**
 * Created by houwen.lai on 2017/9/7.
 *
 */

public class BaseFlowModel implements Serializable {

    private int uid;//进程uid
    private String packageName;//应用包名
    private String appName;//应用名
    private String date;//日期
    private long flowWifi;//wifi流量
    private long flowMobile;//移动数据
    private long flowWifiD;//wifi流量当天
    private long flowMobileD;//移动数据当天
    private long flowSize;//总流量
    private long flowSizeTemp;//开机清零流量
    private String typeNet;//网络状态

    public BaseFlowModel(int uid, String packageName, String appName, String date, long flowWifi, long flowMobile, long flowWifiD, long flowMobileD, long flowSize, long flowSizeTemp, String typeNet) {
        this.uid = uid;
        this.packageName = packageName;
        this.appName = appName;
        this.date = date;
        this.flowWifi = flowWifi;
        this.flowMobile = flowMobile;
        this.flowWifiD = flowWifiD;
        this.flowMobileD = flowMobileD;
        this.flowSize = flowSize;
        this.flowSizeTemp = flowSizeTemp;
        this.typeNet = typeNet;
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

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
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

    public long getFlowSize() {
        return flowSize;
    }

    public void setFlowSize(long flowSize) {
        this.flowSize = flowSize;
    }

    public String getTypeNet() {
        return typeNet;
    }

    public void setTypeNet(String typeNet) {
        this.typeNet = typeNet;
    }

    public long getFlowSizeTemp() {
        return flowSizeTemp;
    }

    public void setFlowSizeTemp(long flowSizeTemp) {
        this.flowSizeTemp = flowSizeTemp;
    }

    @Override
    public String toString() {
        return "BaseFlowModel{" +
                "uid=" + uid +
                ", packageName='" + packageName + '\'' +
                ", appName='" + appName + '\'' +
                ", date='" + date + '\'' +
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


