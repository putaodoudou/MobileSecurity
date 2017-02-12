package practice.mobilesecurity.chapter04.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;

import com.stericson.RootTools.RootTools;

import practice.mobilesecurity.chapter04.entity.AppInfo;

// 业务工具类
public class EngineUtils {
    // share app
    public static void shareApplication(Context context, AppInfo appInfo) {
        Intent intent = new Intent("android.intent.action.SEND");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "推荐您使用一款软件，名称叫作:" +
                appInfo.appName);
        context.startActivity(intent);
    }

    // start app
    public static void startApplication(Context context, AppInfo appInfo) {
        PackageManager pm = context.getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(appInfo.packageName);
        if (null != intent) {
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "该应用没有启动界面", Toast.LENGTH_SHORT).show();
        }
    }

    // setting
    public static void SettingAppDetail(Context context, AppInfo appInfo) {
        Intent intent = new Intent();
        intent.setAction("android.settings.APPLICATION_DEFAULT_SETTINGS");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setData(Uri.parse("package:"+appInfo.packageName));
        context.startActivity(intent);
    }

    // uninstall
    public static void uninstallApplication(Context context, AppInfo appInfo) {
        if (appInfo.isUserApp) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_DELETE);
            intent.setData(Uri.parse("package:" + appInfo.packageName));
            context.startActivity(intent);
        } else {
            // 系统应用需要root权限，利用linux命令删除
            if (!RootTools.isRootAvailable()) {
                Toast.makeText(context, "卸载系统应用，必须要root权限",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                if (!RootTools.isAccessGiven()) {
                    Toast.makeText(context, "请授权黑马小护卫root权限",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                RootTools.sendShell("mount -o remount ,rw /system", 3000);
                RootTools.sendShell("rm -r" + appInfo.apkPath, 3000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
