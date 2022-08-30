package com.lyl.autoupdate.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.lyl.autoupdate.tools.TelephonyFactory;

public class CheckUpdateService extends IntentService
{
    public static final String CLASSTAG = CheckUpdateService.class.getSimpleName();
    // TODO: Rename actions, choose action names that describe tasks that this
    private static final String ACTION_CHECKUPDATA = "CHECKUPDATA";
    private static final String ACTION_CHECKUPINTERF = "CHECKUPINTERF";
    static ToCheckUpdate mToCheckUpdate;

    public CheckUpdateService() {
        super("CheckUpdateService");
    }

    public static void startActionCheckUpdate(Context context) {
        Intent intent = new Intent(context, CheckUpdateService.class);
        intent.setAction(ACTION_CHECKUPDATA);
        context.startService(intent);
    }

    public static ToCheckUpdate getToCheckUpdate() {
        return mToCheckUpdate;
    }

    public static void setToCheckUpdate(ToCheckUpdate toCheckUpdate) {
        mToCheckUpdate = toCheckUpdate;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            String pkgName = intent.getComponent().getPackageName();
            if (ACTION_CHECKUPDATA.equals(action)) {
                if (mToCheckUpdate != null) {
                    mToCheckUpdate.handleActionCheckUpdate(pkgName, TelephonyFactory.getIMEI(getApplicationContext()));
                }
            }
        }
    }

    public interface ToCheckUpdate {
        void handleActionCheckUpdate(String packageName, String imei);
    }
}
