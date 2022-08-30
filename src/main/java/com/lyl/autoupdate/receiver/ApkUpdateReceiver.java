package com.lyl.autoupdate.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.widget.Toast;


import com.lyl.autoupdate.base.ApkUpdateModel;
import com.lyl.autoupdate.tools.CacheOptFactory;
import com.lyl.autoupdate.tools.TelephonyFactory;

import java.io.File;
import java.util.List;

public class ApkUpdateReceiver extends BroadcastReceiver {

    public static String APK_UPDATE = ApkUpdateReceiver.class.getPackage().getName() + "APKUPDATE";
    public static String VAILDISINSTALL = ApkUpdateReceiver.class.getPackage().getName() + "VAILDISINSTALL";
    public static String UPDATEDATA = ApkUpdateReceiver.class.getPackage().getName() + "UPDATEDATA";
    public static boolean SHOW_TOAST = false;

    public ApkUpdateReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        String action = intent.getAction();
        if (APK_UPDATE.equals(action)) {
            if (mApkUpdate != null) {
                ApkUpdateModel apkUpdate = (ApkUpdateModel) intent.getSerializableExtra(UPDATEDATA);
                if (apkUpdate != null) {
                    String newVersion = apkUpdate.getVersion();
                    if (TelephonyFactory.compareVersion(context, newVersion) > 0) {
                        String dVersion = CacheOptFactory.getDownloadUpdateVersion(context);
                        String downloadPath = CacheOptFactory.getDownloadUpdateUrl(context);
                        File file = new File(downloadPath);
                        if (!TextUtils.isEmpty(dVersion) && TelephonyFactory.compareVersion(newVersion, dVersion) == 0 && file.exists()) {
                            mApkUpdate.isDownloadNoInstall(newVersion, downloadPath, apkUpdate.getForceUpdate() == 1);
                        } else {
                            mApkUpdate.haveUpdate(apkUpdate, apkUpdate.getForceUpdate() == 1);
                        }
                    } else {
                        mApkUpdate.noUpdate();
                    }
                } else {
                    if (SHOW_TOAST) Toast.makeText(context, "当前版本是最新版！", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (SHOW_TOAST) Toast.makeText(context, "当前版本是最新版！", Toast.LENGTH_SHORT).show();
            }
        } else if (action.equals(VAILDISINSTALL)) {
            String dVersion = CacheOptFactory.getDownloadUpdateVersion(context);
            boolean isForce = CacheOptFactory.getForce(context);
            String downloadPath = CacheOptFactory.getDownloadUpdateUrl(context);
            File file = new File(downloadPath);
            if (isForce) {
                if (!TextUtils.isEmpty(dVersion) && TelephonyFactory.compareVersion(context, dVersion) > 0 && file.exists()) {
                    mApkUpdate.isDownloadNoInstall(dVersion, downloadPath, isForce);
                }
            }
        }
    }


    ApkUpdate mApkUpdate;

    public ApkUpdate getApkUpdate() {
        return mApkUpdate;
    }

    public void setApkUpdate(ApkUpdate apkUpdate) {
        mApkUpdate = apkUpdate;
    }

    public interface ApkUpdate {
        void haveUpdate(ApkUpdateModel apkUpdate, boolean isforce);

        void noUpdate();

        void isDownloadNoInstall(String version, String path, boolean isforce);
    }

    public static String getAppProcessName(Context context) {
        //当前应用pid
        int pid = android.os.Process.myPid();
        //任务管理类
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //遍历所有应用
        List<ActivityManager.RunningAppProcessInfo> infos = manager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : infos) {
            if (info.pid == pid)//得到当前应用
                return info.processName;//返回包名
        }
        return "";
    }

    public static void setBaseString(Context context) {
        String packagename = getAppProcessName(context);
        APK_UPDATE = packagename + "APKUPDATE";
        VAILDISINSTALL = packagename + "VAILDISINSTALL";
        UPDATEDATA = packagename + "UPDATEDATA";
    }

    public static void startApkUpdateReceiver(Context context, ApkUpdateModel apkUpdate) {
        Intent intent = new Intent();
        intent.setAction(APK_UPDATE);
        intent.putExtra(UPDATEDATA, apkUpdate);
        context.sendBroadcast(intent);

    }

    public static void startVaildUpdate(Context context) {
        boolean isForce = CacheOptFactory.getForce(context);
        if (isForce) {
            Intent intent = new Intent();
            intent.setAction(VAILDISINSTALL);
            context.sendBroadcast(intent);
        }
    }

    public static void registerApkUpdateReceiver(Context context, ApkUpdateReceiver apkUpdateReceiver) {
        setBaseString(context);
        if (apkUpdateReceiver != null) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(ApkUpdateReceiver.APK_UPDATE);
            filter.addAction(ApkUpdateReceiver.VAILDISINSTALL);
            context.registerReceiver(apkUpdateReceiver, filter);
        }
    }


    public static void unregisterApkUpdateReceiver(Context context, ApkUpdateReceiver apkUpdateReceiver) {
        if (apkUpdateReceiver != null) {
            context.unregisterReceiver(apkUpdateReceiver);
        }
    }
}
