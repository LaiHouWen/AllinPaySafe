package com.pax.ipp.tools.event;

/**
 * Created by Administrator on 2017/9/11.
 */

public class MeoryClearEvent {

    private long cacheSize=0;

    public MeoryClearEvent() {
    }

    public MeoryClearEvent(long cacheSize) {
        this.cacheSize = cacheSize;
    }

    public long getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(long cacheSize) {
        this.cacheSize = cacheSize;
    }
}
