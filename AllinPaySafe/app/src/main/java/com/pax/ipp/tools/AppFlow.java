package com.pax.ipp.tools;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by houwen.lai on 2017/9/12.
 */

public class AppFlow implements Parcelable {

    private String date;//日期
    private String appName;//app名
    private String appPakge;//app包名
    private long flow;//每天的流量

    public AppFlow(String date, String appName, String appPakge, long flow) {
        this.date = date;
        this.appName = appName;
        this.appPakge = appPakge;
        this.flow = flow;
    }


    protected AppFlow(Parcel in) {
        date = in.readString();
        appName = in.readString();
        appPakge = in.readString();
        flow = in.readLong();
    }

    public static final Creator<AppFlow> CREATOR = new Creator<AppFlow>() {
        @Override
        public AppFlow createFromParcel(Parcel in) {
            return new AppFlow(in);
        }

        @Override
        public AppFlow[] newArray(int size) {
            return new AppFlow[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(date);
        dest.writeString(appName);
        dest.writeString(appPakge);
        dest.writeLong(flow);
    }
}
