package com.pax.ipp.tools.model;

import android.graphics.drawable.Drawable;

/**
 * Created by Administrator on 2017/9/4.
 * 内存清理
 */

public class CacheListItem {
    private long mCacheSize;
    private String mPackageName, mApplicationName;
    private Drawable mIcon;
    private boolean isChoise=false;

    public CacheListItem(String packageName, String applicationName, Drawable icon, long cacheSize) {
        mCacheSize = cacheSize;
        mPackageName = packageName;
        mApplicationName = applicationName;
        mIcon = icon;
    }


    public Drawable getApplicationIcon() {
        return mIcon;
    }


    public String getApplicationName() {
        return mApplicationName;
    }


    public long getCacheSize() {
        return mCacheSize;
    }


    public String getPackageName() {
        return mPackageName;
    }

    public void setIsChoise(boolean isChoise) {
        this.isChoise = isChoise;
    }

    public boolean getIsChoise() {
        return isChoise;
    }
}
