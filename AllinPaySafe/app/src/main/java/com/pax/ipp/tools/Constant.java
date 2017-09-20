package com.pax.ipp.tools;

import com.pax.ipp.tools.model.BaseFlowModel;
import com.pax.ipp.tools.model.MonthFlowModel;

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
    public static Map<String,Map<String,BaseFlowModel>> flowHistoryList=new HashMap<String,Map<String,BaseFlowModel>>();//所有的历史流量信息
    public static Map<String,MonthFlowModel> flowTodayMonth = new HashMap<String,MonthFlowModel>();//每天的总流量

    public static final String Time_ShunDown = "shunt_down_time";//关机时间
    public static final String Time_StartDown = "start_down_time";//开机时间
    public static final String Time_Availble = "time_avarible";//上次关机有效时间
    public static final String Time_Availble_month = "time_avarible_month";//上个月有效时间
    public static final String Time_Availble_Last = "time_avarible_last";//上次保存时间

    public static final String TIME_TEMP = "time_temp_service";

    public static final String ACTION_SEND_ALARM = "ACTION_SEND_ALARM";//定时器

    public static final String TYPE_CLEARN_MEORY_ALL="type_meory_all";

    public static final String NET_TYPE_wifi = "net_type_wifi";//wifi

    public static final String NET_TYPE_GPRS = "net_type_gprs";//其它网络

    public static final String TIME_TEMP_INIT = "time_temp_service_init";

}
