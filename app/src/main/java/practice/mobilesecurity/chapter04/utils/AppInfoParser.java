package practice.mobilesecurity.chapter04.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import practice.mobilesecurity.chapter04.entity.AppInfo;

public class AppInfoParser {
    /**
     * 获取手机里面所有的应用程序
     * @param context 上下文
     * @return
     */
    public static List<AppInfo> getAppInfos(Context context) {
        // 得到一个包管理器
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packageInfos = pm.getInstalledPackages(0);
        List<AppInfo> appInfos = new ArrayList<>();
        for (PackageInfo packageInfo : packageInfos) {
            AppInfo appInfo = new AppInfo();
            String packname = packageInfo.packageName;
            appInfo.packageName = packname;

            Drawable icon = packageInfo.applicationInfo.loadIcon(pm);
            appInfo.icon = icon;

            String appName = packageInfo.applicationInfo.loadLabel(pm)
                    .toString();
            appInfo.appName = appName;

            //应用程序apk包的路径
            String apkPath = packageInfo.applicationInfo.sourceDir;
            appInfo.apkPath = apkPath;

            File file = new File(apkPath);
            long appSize = file.length();
            appInfo.appSize = appSize;

            // 应用程序的安装位置
            int flags = packageInfo.applicationInfo.flags;
            if ((ApplicationInfo.FLAG_EXTERNAL_STORAGE & flags) != 0) {
                appInfo.isInRoom = false;
            } else {
                appInfo.isInRoom = true;
            }
            if ((ApplicationInfo.FLAG_SYSTEM & flags) != 0) {
                appInfo.isUserApp = false;
            } else {
                appInfo.isUserApp = true;
            }
            appInfos.add(appInfo);
            appInfo = null;
        }
        return appInfos;
    }
}
