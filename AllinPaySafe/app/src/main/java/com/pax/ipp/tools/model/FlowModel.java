package com.pax.ipp.tools.model;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

/**
 * Created by houwen.lai on 2017/9/7.
 */

public class FlowModel implements Serializable{

    private String mPackageName, mApplicationName;
    private Drawable mIcon;
    private long flowSize;

    public String getmPackageName() {
        return mPackageName;
    }

    public void setmPackageName(String mPackageName) {
        this.mPackageName = mPackageName;
    }

    public String getmApplicationName() {
        return mApplicationName;
    }

    public void setmApplicationName(String mApplicationName) {
        this.mApplicationName = mApplicationName;
    }

    public Drawable getmIcon() {
        return mIcon;
    }

    public void setmIcon(Drawable mIcon) {
        this.mIcon = mIcon;
    }

    public long getFlowSize() {
        return flowSize;
    }

    public void setFlowSize(long flowSize) {
        this.flowSize = flowSize;
    }
}
