package com.pax.ipp.tools;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by houwen.lai on 2017/9/6.
 * 变量
 * 常量
 *
 */

public class Constant {

    public static final int RequestCode_meory = 0x0034;
    public static final int RequestCode_meory_all = 0x0035;

    public static final String FlowPaxTools_Totail = "flow_pax_tools_totail";//保存流量信息
    public static final String FlowPaxToolsByUid = "flow_pax_tools_by_uid";//保存流量信息
    public static Map<Integer,Map<String,Long>> flowHistoryList=new HashMap<Integer,Map<String,Long>>();//所有的历史流量信息
    public static Map<String,Long> flowTodayMonth = new HashMap<String,Long>();//每天的总流量

    public static final String Time_ShunDown = "shunt_down_time";//关机时间
    public static final String Time_StartDown = "start_down_time";//开机时间
    public static final String Time_Availble = "time_avarible";//上次关机有效时间
    public static final String Time_Availble_month = "time_avarible_month";//上个月有效时间


    public static final String TIME_TEMP = "time_temp_service";

    public static final String ACTION_SEND_ALARM = "ACTION_SEND_ALARM";//定时器



}
