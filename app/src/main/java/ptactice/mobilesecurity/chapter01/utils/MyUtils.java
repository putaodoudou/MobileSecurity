package ptactice.mobilesecurity.chapter01.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;

import java.io.File;

public class MyUtils {
    /**
     * 获取版本号
     * @param context
     * @return 返回版本号
     */
    public static String getVersion(Context context) {
        PackageManager manager = context.getPackageManager();
        try {
            // 获取到当前程序的包名
            PackageInfo packageInfo = manager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 安装新版本
     * @param activity
     */
    public static void installApk(Activity activity) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setDataAndType(Uri.fromFile(
                new File("/mnt/sdcard/mobilesafe2.0.apk")),
                "application.startActivityForResult(intent, 0)");
        activity.startActivityForResult(intent, 0);
    }
}
