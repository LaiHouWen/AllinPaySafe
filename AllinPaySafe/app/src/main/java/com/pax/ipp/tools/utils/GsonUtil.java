package com.pax.ipp.tools.utils;

import com.google.gson.Gson;

import java.util.Map;

/**
 * Created by houwen.lai on 2017/9/8.
 *
 * gson的使用
 *
 */

public class GsonUtil {

    /**
     * 将Map转化为Json
     *
     * @param map
     * @return String
     */
    public static <T> String mapToJson(Map<String, T> map) {
        Gson gson = new Gson();
        String jsonStr = gson.toJson(map);
        return jsonStr;
    }
    /**
     * map转换json
     * @param map
     * @return
     */
    public static String mapToJsonLong(Map<Long, Long> map) {
        Gson gson = new Gson();
        String jsonStr = gson.toJson(map);
        return jsonStr;
    }
    public static String mapToJsonString(Map<String, Long> map) {
        Gson gson = new Gson();
        String jsonStr = gson.toJson(map);
        return jsonStr;
    }
    /**
     * 将Map转化为Json
     *
     * @param map
     * @return String
     */
    public static String mapToJsonIntegerLong(Map<Integer, Map<Long,Long>> map) {
        Gson gson = new Gson();
        String jsonStr = gson.toJson(map);
        return jsonStr;
    }
    public static String mapToJsonIntegerString(Map<String, Map<String,Long>> map) {
        Gson gson = new Gson();
        String jsonStr = gson.toJson(map);
        return jsonStr;
    }
}
