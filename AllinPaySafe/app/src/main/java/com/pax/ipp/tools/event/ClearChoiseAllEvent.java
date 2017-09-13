package com.pax.ipp.tools.event;

import com.pax.ipp.tools.model.CacheListItem;

import java.util.List;

/**
 * Created by houwen.lai on 2017/9/13.
 */

public class ClearChoiseAllEvent  {

    String type;
    List<CacheListItem> list;
    long memorySize;
    int count;

    public ClearChoiseAllEvent(String type, List<CacheListItem> list, long memorySize, int count) {
        this.type = type;
        this.list = list;
        this.memorySize = memorySize;
        this.count = count;
    }

    public ClearChoiseAllEvent(String type, List<CacheListItem> listItems, long killAppmemory, long count) {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<CacheListItem> getList() {
        return list;
    }

    public void setList(List<CacheListItem> list) {
        this.list = list;
    }

    public long getMemorySize() {
        return memorySize;
    }

    public void setMemorySize(long memorySize) {
        this.memorySize = memorySize;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "ClearChoiseAllEvent{" +
                "type='" + type + '\'' +
                ", list=" + list +
                ", memorySize=" + memorySize +
                ", count=" + count +
                '}';
    }
}
