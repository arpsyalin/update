package com.lyl.autoupdate.tools;

import com.google.gson.Gson;

import java.lang.reflect.Type;


/**
 * Created by Administrator on 2016/7/4.
 */
public class JsonFactory {
    /**
     * 将OBJECT转换成JSON串
     *
     * @param o
     * @return
     */
    public static String toJson(Object o) {
        Gson gson = new Gson();
        return gson.toJson(o);
    }

    /**
     * 将JSON串转换成OBJECT
     *
     * @param value
     * @param cls
     * @param <T>
     * @return
     */
    public static <T> T getFromJson(String value, Class<T> cls) {
        Gson gson = new Gson();
        T t = null;
        try {
            t = gson.fromJson(value, cls);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return t;
    }

    public static <T> T getFromJson(String value, Type type) {
        Gson gson = new Gson();
        T t = null;
        try {
            t = gson.fromJson(value, type);

        } catch (Exception e) {
            // TODO: handle exception
        }
        return t;
    }
}
