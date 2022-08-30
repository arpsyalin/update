package com.lyl.autoupdate.tools;

import android.content.Context;
import android.text.TextUtils;


import java.util.Date;

/**
 * Created by Administrator on 2018/1/11.
 */

public class CacheOptFactory {
    public static void saveAutoCheckUpdate(Context context) {
        String dd = TimeFactory.parseToYYYYMMDD(new Date());
        StorePrferences.saveToText(context, PreferencesConstant.CHECKUPDATE, PreferencesConstant.AUTOCHECKUPDATE, dd);
    }

    public static String getDownloadUpdateVersion(Context context) {
        return StorePrferences.getFromText(context, PreferencesConstant.CHECKUPDATE, PreferencesConstant.DOWNLOADVERSIONVERSION);
    }

    public static String getDownloadUpdateUrl(Context context) {
        return StorePrferences.getFromText(context, PreferencesConstant.CHECKUPDATE, PreferencesConstant.DOWNLOADVERSIONURL);
    }

    public static void saveDownloadUpdateData(Context context, String versionString, String path) {
        StorePrferences.saveToText(context, PreferencesConstant.CHECKUPDATE, PreferencesConstant.DOWNLOADVERSIONVERSION, versionString);
        StorePrferences.saveToText(context, PreferencesConstant.CHECKUPDATE, PreferencesConstant.DOWNLOADVERSIONURL, path);
    }

    public static boolean needAutoCheckUpdate(Context context) {
        String date = StorePrferences.getFromText(context, PreferencesConstant.CHECKUPDATE, PreferencesConstant.AUTOCHECKUPDATE);
        if (!TextUtils.isEmpty(date)) {
            Date lastCheck = TimeFactory.parseYYYYMMDDToDate(date);
            if (lastCheck != null) {
                Date nowTime = new Date();
                int result = TimeFactory.daysOfTwo(nowTime, lastCheck);
                if (result == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void removeDownloadUpdateData(Context context) {
        StorePrferences.removeKey(context, PreferencesConstant.CHECKUPDATE);
    }

    public static void saveForce(Context context, boolean isforce) {
        StorePrferences.saveToBoolean(context, PreferencesConstant.CHECKUPDATE, PreferencesConstant.DOWNLOADISFORCE, isforce);
    }

    public static boolean getForce(Context context) {
        return StorePrferences.getFromBoolean(context, PreferencesConstant.CHECKUPDATE, PreferencesConstant.DOWNLOADISFORCE);
    }
}
