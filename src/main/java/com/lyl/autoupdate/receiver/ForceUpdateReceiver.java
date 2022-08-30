package com.lyl.autoupdate.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Created by Administrator on 2016/9/27.
 */
public class ForceUpdateReceiver extends BroadcastReceiver {
    public final static String TAG = ForceUpdateReceiver.class.getPackage().getName()+ "ForceUpdateReceiver";
    public final static String REULSTTYPE = ForceUpdateReceiver.class.getPackage().getName()+ "REULSTTYPE";
    public final static String UPDATECOUNT = ForceUpdateReceiver.class.getPackage().getName()+ "UPDATECOUNT";
    public final static String ISSUCCES = ForceUpdateReceiver.class.getPackage().getName()+ "ISSUCCES";
    public final static int START = 0;
    public final static int UPDATE = 1;
    public final static int STOP = 2;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(TAG)) {
            if (mForceUpdateListener != null) {
                int type = intent.getIntExtra(REULSTTYPE, 0);
                switch (type) {
                    case START:
                        mForceUpdateListener.onUpdateStart();
                        break;
                    case UPDATE:
                        int count =intent.getIntExtra(UPDATECOUNT, 0);
                        mForceUpdateListener.onUpdating(count);
                        break;
                    case STOP:
                        mForceUpdateListener.onStop(intent.getBooleanExtra(ISSUCCES, false));
                        break;
                }
            }
        }
    }

    /**
     * 发送强制下载通知
     *
     * @param context
     * @param type
     * @param count
     * @param isSuccess
     */
    public static void sendForceUpdateReceiver(Context context, int type, int count, boolean isSuccess) {
        Intent intent = new Intent();
        intent.setAction(TAG);
        intent.putExtra(REULSTTYPE, type);
        intent.putExtra(UPDATECOUNT, count);
        intent.putExtra(ISSUCCES, isSuccess);
        context.sendBroadcast(intent);

    }

    /**
     * 发送强制下载完成通知
     *
     * @param context
     */
    public static void sendStop(Context context, boolean isSucces) {
        Intent intent = new Intent();
        intent.setAction(TAG);
        intent.putExtra(REULSTTYPE, STOP);
        intent.putExtra(ISSUCCES, isSucces);
        context.sendBroadcast(intent);
    }

    /**
     * 发送强制下载更新通知
     *
     * @param context
     */
    public static void sendUpdate(Context context, int count) {
        Intent intent = new Intent();
        intent.setAction(TAG);
        intent.putExtra(REULSTTYPE, UPDATE);
        intent.putExtra(UPDATECOUNT, count);
        context.sendBroadcast(intent);
    }

    public static void registerForceUpdateReceiver(Context context, ForceUpdateReceiver forceUpdateReceiver) {
        if (forceUpdateReceiver != null) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(TAG);
            context.registerReceiver(forceUpdateReceiver, filter);
        }
    }

    public static void unregisterForceUpdateReceiver(Context context, ForceUpdateReceiver forceUpdateReceiver) {
        if (forceUpdateReceiver != null)
            context.unregisterReceiver(forceUpdateReceiver);
    }

    /**
     * 发送强制下载开始通知
     *
     * @param context
     */
    public static void sendStart(Context context) {
        Intent intent = new Intent();
        intent.setAction(TAG);
        intent.putExtra(REULSTTYPE, START);
        context.sendBroadcast(intent);
    }

    ForceUpdateListener mForceUpdateListener;

    public ForceUpdateListener getForceUpdateListener() {
        return mForceUpdateListener;
    }

    public void setForceUpdateListener(ForceUpdateListener forceUpdateListener) {
        this.mForceUpdateListener = forceUpdateListener;
    }

    public interface ForceUpdateListener {
        void onUpdateStart();

        void onUpdating(int udCount);

        void onStop(boolean isSuccess);
    }
}
