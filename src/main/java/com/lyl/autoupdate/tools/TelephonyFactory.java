package com.lyl.autoupdate.tools;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * Created by Administrator on 2016/7/5.
 */
public class TelephonyFactory {
    /**
     * 获取deviceid
     *
     * @param context
     * @return
     */
    public static String getIMEI(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Activity.TELEPHONY_SERVICE);
        String id = tm.getDeviceId();
        if (TextUtils.isEmpty(id)) {
            String ANDROID_ID = Settings.System.getString(context.getContentResolver(), Settings.System.ANDROID_ID);
            return ANDROID_ID;
        }
        return id;
    }

    /**
     * 获取手机型号
     *
     * @return
     */
    public static String getPhoneType() {
        return android.os.Build.MODEL;
    }


    /**
     * 返回当前程序版本名
     */
    public static String getAppVersionName(Context context) {
        String versionName = "";
        int versionCode = 0;
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            versionCode = pi.versionCode;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {

        }
        return versionName;
    }

    public static int compareVersion(Context context, String version) {
        String oldVersion = getAppVersionName(context);
        return compareVersion(version, oldVersion);
    }

    public static int compareVersion(String version, String oldVersion) {
        String[] vs = version.split("\\.");
        String[] ovs = oldVersion.split("\\.");
        int count = vs.length;
        int reuslt = 0;
        for (int i = 0; i < count; i++) {
            reuslt = compareString(vs[i], ovs[i]);
            if (reuslt != 0) {
                return reuslt;
            }
        }
        return reuslt;
    }

    private static int compareString(String v, String ov) {
        int iv = Integer.parseInt(v);
        int iov = Integer.parseInt(ov);
        if (iv == iov) {
            return 0;
        } else if (iv > iov) {
            return 1;
        } else {
            return -1;
        }
    }
}
