package com.lyl.autoupdate.tools;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import androidx.core.content.FileProvider;

public class FileUtils {
    public static String FILE_PROVIDER = "com.lyl.autoupdate.FileProvider";

    public static String getFilesPath(Context context) {
        String filePath;
        if (isHasSdcard()) {
            //外部存储可用
            filePath = context.getExternalFilesDir(null).getPath();
        } else {
            //外部存储不可用
            filePath = context.getFilesDir().getPath();
        }
        return filePath;
    }

    // 判断是否有SD卡
    public static boolean isHasSdcard() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            return true;
        } else {
            return false;
        }
    }


    /***
     * 创建APK文件
     */
    public static void createFile(Context context, String saveDir, String name) {
        File updateDir = new File(getFilesPath(context) + "/" + saveDir);
        if (!updateDir.exists()) {
            updateDir.mkdirs();
        }
        File updateFile = new File(updateDir.getPath() + "/" + name + ".apk");
        if (updateFile.exists()) {
            updateFile.delete();
            updateFile = new File(updateDir.getPath() + "/" + name + ".apk");
        }
        try {
            updateFile.createNewFile();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            updateFile.mkdir();
        }
    }


    /**
     * 创建APK文件
     *
     * @param name
     * @return
     */
    public static String updateFilename(Context context, String apkDir, String name) {
        File updateDir = new File(getFilesPath(context) + "/" + apkDir);
        if (!updateDir.exists()) {
            boolean isTrue = updateDir.mkdirs();
            if (!isTrue) {
                return null;
            }
        }
        File updateFile = new File(updateDir.getPath() + "/" + name + ".apk");
        if (updateFile.exists()) {
            return updateFile.getAbsolutePath();
        }
        try {
            updateFile.createNewFile();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return updateFile.getPath();
    }


}
