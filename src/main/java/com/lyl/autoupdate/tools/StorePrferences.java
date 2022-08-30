package com.lyl.autoupdate.tools;

import android.content.Context;
import android.content.SharedPreferences;

import java.lang.reflect.Type;

/**
 * 缓存处理类
 *
 * @author yalin
 */

public class StorePrferences {

    public static void saveToJson(Context context, String key, String subKey,
                                  Object value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(key,
                Context.MODE_PRIVATE).edit();
        String v = JsonFactory.toJson(value);
        editor.putString(subKey, v);
        editor.commit();
    }

    public static long getFromLong(Context context, String key, String subKey) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(key,
                Context.MODE_PRIVATE);
        long value = sharedPreferences.getLong(subKey, 0);
        return value;
    }

    public static String getFromText(Context context, String key, String subKey) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(key,
                Context.MODE_PRIVATE);
        String value = sharedPreferences.getString(subKey, "");
        return value;
    }

    public static String getFromText(Context context, String key, String subKey, String defaut) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(key,
                Context.MODE_PRIVATE);
        String value = sharedPreferences.getString(subKey, defaut);
        return value;
    }

    public static boolean getFromBoolean(Context context, String key,
                                         String subKey) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(key,
                Context.MODE_PRIVATE);
        boolean value = sharedPreferences.getBoolean(subKey, false);

        return value;
    }

    public static <T> T getFromJson(Context context, String key, Class<T> cls) {
        String value = getFromText(context, key, cls.getName());
        return JsonFactory.getFromJson(value, cls);
    }


    public static <T> T getFromJson(Context context, String key, String subKey,
                                    Class<T> cls) {
        String value = getFromText(context, key, subKey);
        return JsonFactory.getFromJson(value, cls);
    }


    public static <T> T getFromJson(Context context, String key, String subKey,
                                    Type type) {
        String value = getFromText(context, key, subKey);
        return JsonFactory.getFromJson(value, type);
    }

    public static void saveToLong(Context context, String key, String subKey,
                                  long value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(key,
                Context.MODE_PRIVATE).edit();
        editor.putLong(subKey, value);
        editor.commit();
    }

    public static void saveToText(Context context, String key, String subKey,
                                  String value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(key,
                Context.MODE_PRIVATE).edit();
        editor.putString(subKey, value);
        editor.commit();
    }

    public static void saveToBoolean(Context context, String key,
                                     String subKey, boolean value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(key,
                Context.MODE_PRIVATE).edit();
        editor.putBoolean(subKey, value);
        editor.commit();
    }

    public static void removeSubKey(Context context, String key, String subKey) {
        SharedPreferences.Editor editor = context.getSharedPreferences(key,
                Context.MODE_PRIVATE).edit();
        editor.remove(subKey);
        editor.commit();
    }

    public static void removeKey(Context context, String key) {
        SharedPreferences.Editor editor = context.getSharedPreferences(key,
                Context.MODE_PRIVATE).edit();
        if (editor != null) {
            editor.clear();
            editor.commit();
        }
    }


    /**
     * 是否包含该key
     *
     * @param context
     * @param key
     */
    public static boolean isHaveKey(Context context, String key) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(key,
                Context.MODE_PRIVATE);
        return sharedpreferences.contains(key);
    }


}