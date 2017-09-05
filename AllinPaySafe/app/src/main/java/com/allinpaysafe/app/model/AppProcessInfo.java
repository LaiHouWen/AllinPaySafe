package com.allinpaysafe.app.model;

import android.graphics.drawable.Drawable;

/**
 *
 * app process
 */
public class AppProcessInfo implements Comparable<AppProcessInfo> {
    /**
     * The app name.
     */
    public String appName;

    /**
     * The name of the process that this object is associated with.
     */
    public String processName;

    /**
     * The pid of this process; 0 if none.
     */
    public int pid;

    /**
     * The user id of this process.
     */
    public int uid;

    /**
     * The icon.
     */
    public Drawable icon;

    /**
     * 占用的内存.
     */
    public long memory;

    /**
     * 占用的内存.
     */
    public String cpu;

    public String packName;

    /**
     * 进程的状态，其中S表示休眠，R表示正在运行，Z表示僵死状态，N表示该进程优先值是负数.
     */
    public String status;

    /**
     * 当前使用的线程数.
     */
    public String threadsCount;

    public boolean checked = true;

    /**
     * 是否是系统进程.
     */
    public boolean isSystem;

    public boolean isFilterProcess;//是否过滤


    /**
     * Instantiates a new ab process info.
     */
    public AppProcessInfo() {
        super();
    }


    /**
     * Instantiates a new ab process info.
     *
     * @param processName the process name
     * @param pid the pid
     * @param uid the uid
     */
    public AppProcessInfo(String processName, int pid, int uid) {
        super();
        this.processName = processName;
        this.pid = pid;
        this.uid = uid;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public long getMemory() {
        return memory;
    }

    public void setMemory(long memory) {
        this.memory = memory;
    }

    public void setIsFilterProcess(boolean isFilterProcess) {
        this.isFilterProcess = isFilterProcess;
    }
    public boolean getIsFilterProcess() {
       return this.isFilterProcess;
    }

    public String getCpu() {
        return cpu;
    }

    public void setCpu(String cpu) {
        this.cpu = cpu;
    }

    public String getPackName() {
        return packName;
    }

    public void setPackName(String packName) {
        this.packName = packName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getThreadsCount() {
        return threadsCount;
    }

    public void setThreadsCount(String threadsCount) {
        this.threadsCount = threadsCount;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isSystem() {
        return isSystem;
    }

    public void setSystem(boolean system) {
        isSystem = system;
    }

    @Override public int compareTo(AppProcessInfo another) {
        if (this.processName.compareTo(another.processName) == 0) {
            if (this.memory < another.memory) {
                return 1;
            }
            else if (this.memory == another.memory) {
                return 0;
            }
            else {
                return -1;
            }
        }
        else {
            return this.processName.compareTo(another.processName);
        }
    }
}
