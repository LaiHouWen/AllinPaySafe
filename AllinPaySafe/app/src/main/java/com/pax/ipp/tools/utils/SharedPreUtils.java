package com.pax.ipp.tools.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by houwen.lai on u2017/9/8.
 * 存储数据
 *
 */

public class SharedPreUtils {

    private static String Preferences_TAG = SharedPreUtils.class
            .getSimpleName();
    private static SharedPreUtils preferencesUtils = null;

    public static SharedPreUtils getInstanse() {
        if (preferencesUtils == null) {
            synchronized (SharedPreUtils.class){
                if (preferencesUtils == null)
                    preferencesUtils = new SharedPreUtils();
            }
        }
        return preferencesUtils;
    }

    private SharedPreferences sharedPreferences =null;
    private SharedPreferences.Editor editor =null;

    /**
     *
     * @param context
     *
     */
    public boolean putKeyValue(Context context, String key, String value) {
        if (context == null) return false;
        if (sharedPreferences==null)sharedPreferences = context.getSharedPreferences(
                Preferences_TAG, Context.MODE_WORLD_READABLE);
        if (editor==null) editor = sharedPreferences.edit();
        editor.putString(key,value);
        return editor.commit();
    }
    public void putKeyValue(Context context, String key,int value) {
        if (context == null) return;
        if (sharedPreferences==null)sharedPreferences = context.getSharedPreferences(
                Preferences_TAG, Context.MODE_WORLD_READABLE);
        if (editor==null) editor = sharedPreferences.edit();
        editor.putInt(key,value);
        editor.commit();
    }
    public void putKeyValueLong(Context context, String key,long value) {
        if (context == null) return;
        if (sharedPreferences==null)sharedPreferences = context.getSharedPreferences(
                Preferences_TAG, Context.MODE_WORLD_READABLE);
        if (editor==null) editor = sharedPreferences.edit();
        editor.putLong(key,value);
        editor.commit();
    }
    public void putKeyValue(Context context, String key,boolean value) {
        if (context == null) return;
        if (sharedPreferences==null)sharedPreferences = context.getSharedPreferences(
                Preferences_TAG, Context.MODE_WORLD_READABLE);
        if (editor==null) editor = sharedPreferences.edit();
        editor.putBoolean(key,value);
        editor.commit();
    }

    public String getKeyValue(Context context,String key) {
        if (context == null) return "";
        if (sharedPreferences==null)sharedPreferences = context.getSharedPreferences(
                Preferences_TAG, Context.MODE_WORLD_READABLE);
        return sharedPreferences.getString(key,null);
    }
    public long getKeyValueLong(Context context,String key) {
        if (context == null) return 0;
        if (sharedPreferences==null)sharedPreferences = context.getSharedPreferences(
                Preferences_TAG, Context.MODE_WORLD_READABLE);
        return sharedPreferences.getLong(key,0);
    }
    /**
     * 移除key
     * @param context
     * @param key
     */
    public void removeKeyValue(Context context,String key) {
        if (context == null) return;
        if (sharedPreferences==null)sharedPreferences = context.getSharedPreferences(
                Preferences_TAG, Context.MODE_WORLD_READABLE);
        if (editor==null) editor = sharedPreferences.edit();
        editor.remove(key);
        editor.commit();
    }

}
