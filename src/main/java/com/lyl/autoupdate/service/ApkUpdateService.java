package com.lyl.autoupdate.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

import com.lyl.autoupdate.base.ApkUpdateModel;
import com.lyl.autoupdate.base.BaseSetting;
import com.lyl.autoupdate.receiver.AppInstallReceiver;
import com.lyl.autoupdate.receiver.ForceUpdateReceiver;
import com.lyl.autoupdate.tools.CacheOptFactory;
import com.lyl.autoupdate.tools.FileUtils;

import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ApkUpdateService extends Service {
    public static boolean isUpdate = false;
    public static final String PACKAGE_NAME = ApkUpdateService.class.getPackage().getName();
    public static final String APP_NAME = PACKAGE_NAME + "APPNAME";
    public static final String VERSION_STRING = PACKAGE_NAME + "VERSIONSTRING";
    public static final String DOWNLOAD_APK_URL = PACKAGE_NAME + "DOWNLOADAPKURL";
    public static final String FORE_UPDATE = PACKAGE_NAME + "FOREUPDATE";
    public static final String ACTION_UPDATE = PACKAGE_NAME + "ACTIONUPDATE";
    public static final String SAVE_LOCAL_PATH = PACKAGE_NAME + "SAVELOCALPATH";
    private static final int TIMEOUT = 60 * 1000;// 超时
    private static final int DOWN_OK = 1;
    private static final int DOWN_ERROR = 0;
    private static final int mDownStep = 1;// 提示step
    private AppInstallReceiver mInstallReceiver;
    private String mDownLoadUrl, mAppName, mFileName, mVersionString, mSavePath;
    private boolean isFore;
    private NotificationManager mNotificationManager;
    private Notification mNotification;
    private PendingIntent mPendingIntent;
    long mDownloadCount = 0;// 已经下载好的大小
    private int mNotificationId = 1;
    private String downloadChannel = "download";
    private String downloadChannelName = "download_channel_name";

    /**
     * 开始运行下载服务
     *
     * @param context
     */
    public static void startDownLoadService(Context context, ApkUpdateModel apkBean, String savePath) {
        Intent intent = new Intent(context, ApkUpdateService.class);
        intent.setAction(ACTION_UPDATE);
        intent.putExtra(APP_NAME, context.getString(BaseSetting.getInstance().getAppName()));
        intent.putExtra(VERSION_STRING, apkBean.getVersion());
        intent.putExtra(DOWNLOAD_APK_URL, apkBean.getUrl());
        intent.putExtra(FORE_UPDATE, apkBean.getForceUpdate());
        intent.putExtra(SAVE_LOCAL_PATH, savePath);
        context.startService(intent);
    }


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @SuppressLint("WrongConstant")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        flags = START_STICKY;
        if (intent != null) {
            // 已开始下载了不再不接收进入
            if (isUpdate == true) {
                return super.onStartCommand(intent, flags, startId);
            }
            isUpdate = true;
            mDownloadCount = 0;
            mAppName = intent.getStringExtra(APP_NAME);
            mDownLoadUrl = intent.getStringExtra(DOWNLOAD_APK_URL);
            mVersionString = intent.getStringExtra(VERSION_STRING);
            mSavePath = intent.getStringExtra(SAVE_LOCAL_PATH);
            isFore = intent.getIntExtra(FORE_UPDATE, 0) == 1;
            mFileName = mAppName;
            FileUtils.createFile(this, mSavePath, mAppName);
            createNotification();
            createThread();
        }

        return super.onStartCommand(intent,
                Service.START_REDELIVER_INTENT, startId);
    }

    /***
     * 开线程下载
     */
    public void createThread() {
        downloadThread(mDownloadCount, mFileName);
    }


    /***
     * 更新UI
     */
    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWN_OK:
                    // 下载完成，点击安装
                    mDownloadCount = 0;
                    isUpdate = false;
                    IntentFilter filter = new IntentFilter(
                            ConnectivityManager.CONNECTIVITY_ACTION);
                    filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
                    filter.setPriority(Integer.MAX_VALUE);
                    filter.addDataScheme("package");
                    mInstallReceiver = new AppInstallReceiver(() -> {
                        if (mNotificationManager != null)
                            mNotificationManager.cancelAll();
                    });
                    registerReceiver(mInstallReceiver, filter);
                    String urlPath = FileUtils.updateFilename(ApkUpdateService.this, mSavePath, mFileName);
                    CacheOptFactory.saveDownloadUpdateData(getApplicationContext(), mVersionString, urlPath);
                    if (isFore) {
                        ForceUpdateReceiver.sendStop(getApplicationContext(), true);
                    }
                    CacheOptFactory.saveForce(getApplicationContext(), isFore);
                    sendSuccessNotice(AppInstallReceiver.startInstallApk(getApplicationContext(), urlPath));
                    ApkUpdateService.this.stopSelf();
                    break;
                case DOWN_ERROR:
                    sendFailNotice();
                    CacheOptFactory.removeDownloadUpdateData(getApplicationContext());
                    isUpdate = false;
                    if (isFore) {
                        ForceUpdateReceiver.sendStop(getApplicationContext(), false);
                    }
                    break;
                default:
                    ApkUpdateService.this.stopSelf();
                    isUpdate = false;
                    if (isFore) {
                        ForceUpdateReceiver.sendStop(getApplicationContext(), false);
                    }
                    break;
            }
        }
    };

    private void sendFailNotice() {
        mNotificationManager.cancel(mNotificationId);
        mNotification.flags = Notification.FLAG_AUTO_CANCEL;
        buildNotification(getString(BaseSetting.getInstance().getDownloadFail()), getString(BaseSetting.getInstance().getDownloadFail()), null);
        if (mNotificationManager != null)
            mNotificationManager.notify(mNotificationId, mNotification);
    }


    /**
     * 发送下载成功的通知
     *
     * @param intent
     */
    public void sendSuccessNotice(Intent intent) {
        mNotificationManager.cancel(mNotificationId);
        mPendingIntent = PendingIntent.getActivity(
                ApkUpdateService.this, 0, intent, 0);
        buildNotification(getString(BaseSetting.getInstance().getDownloadSuccess()), getString(BaseSetting.getInstance().getDownloadSuccessToClick()), mPendingIntent);
        if (mNotificationManager != null)
            mNotificationManager.notify(mNotificationId, mNotification);
    }
    public void downloadThread(final long startCount, final String filename) {
        new Thread(new DownLoadRunnable(startCount, filename)).start();
    }
    /**
     * 下载Runnable
     */
    class  DownLoadRunnable implements Runnable {
        String fileName;
        final long mStartCount;

        public DownLoadRunnable(long startCount, String ifileName) {
            mStartCount = startCount;
            this.fileName = FileUtils.updateFilename(ApkUpdateService.this, mSavePath, ifileName);
        }

        @Override
        public void run() {
            try {
                long downloadSize = downloadUpdateFile(mStartCount, mDownLoadUrl, fileName);
                if (downloadSize > 0) {
                    // 下载成功
                    mHandler.obtainMessage(DOWN_OK).sendToTarget();
                } else {
                    mHandler.obtainMessage(DOWN_ERROR).sendToTarget();
                }
            } catch (Exception e) {
                e.printStackTrace();
                mHandler.obtainMessage(DOWN_ERROR).sendToTarget();
            }
        }
    }

    /***
     *  更新进度
     */

    public void updateNotification(String title, int progress) {
        mNotification.extras.putString(Notification.EXTRA_TITLE, title);
        mNotification.extras.putString(Notification.EXTRA_TEXT, "下载进度：" + progress + "%");
        mNotification.extras.putInt(Notification.EXTRA_PROGRESS, progress == 100 ? 0 : progress);
        mNotification.extras.putInt(Notification.EXTRA_PROGRESS_MAX, progress == 100 ? 0 : 100);
        mNotification.extras.putBoolean(Notification.EXTRA_PROGRESS_INDETERMINATE, true);
        if (mNotificationManager != null)
            mNotificationManager.notify(mNotificationId, mNotification);
    }

    private void createNotification() {
        buildProgressNotification(getString(BaseSetting.getInstance().getDownloadStart()), getString(BaseSetting.getInstance().getPt_downloading()) + "：0%");
        if (mNotificationManager != null)
            mNotificationManager.notify(mNotificationId, mNotification);
    }

    private void buildProgressNotification(String contentTitle, String contentText) {
        Notification.Builder builder = new Notification.Builder(this);
        Bitmap icon = BitmapFactory.decodeResource(this.getResources(),
                BaseSetting.getInstance().getDownloadLogo());
        builder.setContentIntent(null)
                .setLargeIcon(icon)  // 设置下拉列表中的图标(大图标)
                .setContentTitle(contentTitle) // 设置下拉列表里的标题
                .setProgress(100, 0, true)
                .setSmallIcon(BaseSetting.getInstance().getDownloadLogo()) // 设置状态栏内的小图标
                .setContentText(contentText) // 设置上下文内容
                .setWhen(System.currentTimeMillis());
        builderNotification(builder);
    }

    private void buildNotification(String contentTitle, String contentText, PendingIntent pendingIntent) {
        Notification.Builder builder = new Notification.Builder(this);
        Bitmap icon = BitmapFactory.decodeResource(this.getResources(),
                BaseSetting.getInstance().getDownloadLogo());
        builder.setContentIntent(pendingIntent)
                .setLargeIcon(icon)  // 设置下拉列表中的图标(大图标)
                .setContentTitle(contentTitle) // 设置下拉列表里的标题
                .setSmallIcon(BaseSetting.getInstance().getDownloadLogo()) // 设置状态栏内的小图标
                .setContentText(contentText) // 设置上下文内容
                .setWhen(System.currentTimeMillis());
        builderNotification(builder);
    }

    private void builderNotification(Notification.Builder builder) {
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && null != mNotificationManager) {
            NotificationChannel notificationChannel =
                    new NotificationChannel(downloadChannel, downloadChannelName,
                            NotificationManager.IMPORTANCE_LOW);
            mNotificationManager.createNotificationChannel(notificationChannel);
            builder.setChannelId(downloadChannel);
        }
        mNotification = builder.build(); // 获取构建好的Notification
        mNotification.defaults = Notification.DEFAULT_LIGHTS; //设置为默认的声音
    }

    /***
     * 下载文件
     *
     * @return
     * @throws MalformedURLException
     */
    @SuppressWarnings("resource")
    public long downloadUpdateFile(long downloadStart, String down_url, String file)
            throws Exception {
        long totalSize;// 文件总大小
        int updateCount = 0;// 已经上传的文件大小
        InputStream inputStream;
        RandomAccessFile randomAccessFile = null;
        HttpURLConnection httpURLConnection = getDownloadHead(down_url, downloadStart);
        httpURLConnection.connect();
        // 获取下载文件的size
        totalSize = httpURLConnection.getContentLength() + downloadStart;
        if (httpURLConnection.getResponseCode() == 404) {
            throw new Exception("fail!");
        }
        inputStream = httpURLConnection.getInputStream();
        randomAccessFile = new RandomAccessFile(file, "rwd");
        randomAccessFile.seek(downloadStart);

        byte buffer[] = new byte[4096];
        long downsize = downloadStart;
        int readsize = 0;
        while ((readsize = inputStream.read(buffer)) != -1) {
            randomAccessFile.write(buffer, 0, readsize);
            downsize += readsize;// 时时获取下载到的大小
            if (((downsize * 100 / totalSize) - mDownStep) >= updateCount) {
                updateCount = (int) (downsize * 100 / totalSize);
                sendNotification(updateCount);
                if (isFore)
                    ForceUpdateReceiver.sendUpdate(getApplicationContext(), updateCount);
            }
        }

        if (httpURLConnection != null) {
            httpURLConnection.disconnect();
        }
        inputStream.close();
        randomAccessFile.close();
        if (totalSize != downsize) {
            return -1;
        }
        return downsize;

    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        isUpdate = false;
        if (mInstallReceiver != null) {
            unregisterReceiver(mInstallReceiver);
        }


    }

    /**
     * 获取下载的头信息
     *
     * @param downUrl
     * @param startCount
     * @return
     * @throws Exception
     */
    public HttpURLConnection getDownloadHead(String downUrl, long startCount) throws Exception {
        // 设置文件开始的下载位置 使用 Range字段设置断点续传
        String start = "bytes=" + startCount + "-";
        URL url = new URL(downUrl);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url
                .openConnection();
        httpURLConnection.setRequestProperty("User-Agent", "NetFox");
        httpURLConnection.setRequestProperty("RANGE", start);
        httpURLConnection.setConnectTimeout(TIMEOUT);
        httpURLConnection.setReadTimeout(TIMEOUT);
        httpURLConnection.setRequestMethod("GET");
        httpURLConnection.setDoInput(true);
        return httpURLConnection;
    }

    /**
     * 设置通知栏内容
     *
     * @param updateCount
     */
    public void sendNotification(int updateCount) {
        updateNotification(getString(BaseSetting.getInstance().getPt_downloading()), updateCount);
    }
}

