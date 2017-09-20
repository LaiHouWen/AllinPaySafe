package com.pax.ipp.tools;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by houwen.lai on 2017/9/12.
 * aidl
 */

public class TrafficFlowBean implements Parcelable {

    private String date;//日期
    private String name;//app名
    private String packName;//app包名
    private long dayMobileFlow;//移动流量
    private long dayWifiFlow;//wifi流量

    public TrafficFlowBean(String date, String name, String packName,long dayMobileFlow, long dayWifiFlow) {
        this.date = date;
        this.name = name;
        this.packName = packName;
        this.dayMobileFlow = dayMobileFlow;
        this.dayWifiFlow = dayWifiFlow;
    }


    protected TrafficFlowBean(Parcel in) {
        date = in.readString();
        name = in.readString();
        packName = in.readString();
        dayMobileFlow = in.readLong();
        dayWifiFlow = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(date);
        dest.writeString(name);
        dest.writeString(packName);
        dest.writeLong(dayMobileFlow);
        dest.writeLong(dayWifiFlow);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TrafficFlowBean> CREATOR = new Creator<TrafficFlowBean>() {
        @Override
        public TrafficFlowBean createFromParcel(Parcel in) {
            return new TrafficFlowBean(in);
        }

        @Override
        public TrafficFlowBean[] newArray(int size) {
            return new TrafficFlowBean[size];
        }
    };

    @Override
    public String toString() {
        return "TrafficFlowBean{" +
                "date='" + date + '\'' +
                ", name='" + name + '\'' +
                ", packName='" + packName + '\'' +
                ", dayMobileFlow=" + dayMobileFlow +
                ", dayWifiFlow=" + dayWifiFlow +
                '}';
    }
}
