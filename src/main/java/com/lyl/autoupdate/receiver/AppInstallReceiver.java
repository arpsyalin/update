package com.lyl.autoupdate.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;


import com.lyl.autoupdate.base.BaseSetting;
import com.lyl.autoupdate.tools.FileUtils;

import java.io.File;

import androidx.core.content.FileProvider;


public class AppInstallReceiver extends BroadcastReceiver {
    InstallComplete installComplete;

    public AppInstallReceiver() {
        super();
    }

    public AppInstallReceiver(InstallComplete installComplete) {
        super();
        this.installComplete = installComplete;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println(intent.getAction());
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {
            PackageManager manager = context.getPackageManager();
            String packageName = intent.getData().getSchemeSpecificPart();
            Toast.makeText(context, BaseSetting.getInstance().getInstallSuccess() + packageName, Toast.LENGTH_LONG)
                    .show();
            if (installComplete != null)
                installComplete.onFinish();
        } else if (intent.getAction().equals(
                "android.net.conn.CONNECTIVITY_CHANGE")) {
        }

    }

    public interface InstallComplete {
        void onFinish();

    }

    public static Intent startInstallApk(Context context, String url) {
        Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(context, FileUtils.FILE_PROVIDER, new File(url));
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(url)), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
//        context.startActivity(intent);
        return intent;
    }


    public static void toInstallApk(Context context, File file) {
        Uri uri = Uri.fromFile(file);
        String packageName = context.getPackageName();
        Intent intent;
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//			 intent = new Intent(Intent.ACTION_VIEW);
//			intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//			Uri contentUri = FileProvider.getUriForFile(context, packageName+".fileprovider", file);
//			intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
//		} else {
        intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		}
        context.startActivity(intent);
    /*
        intent.setDataAndType(uri,
				"application/vnd.android.package-archive");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);*/
    }

    public static void registerAppInstallReceiver(Context context, InstallComplete installComplete) {

    }
}