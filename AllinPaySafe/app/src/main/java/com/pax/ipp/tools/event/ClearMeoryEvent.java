package com.pax.ipp.tools.event;

/**
 * Created by houwen.lai on 2017/9/6.
 */

public class ClearMeoryEvent {

    private long meorySize = 0;

    public String message = "";

    public ClearMeoryEvent(long meorySize) {
        super();
        this.meorySize = meorySize;
    }

    public ClearMeoryEvent(String message) {
        super();
        this.message = message;
    }

    public long getMeorySize() {
        return meorySize;
    }

    public void setMeorySize(long meorySize) {
        this.meorySize = meorySize;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
