package practice.mobilesecurity.chapter09.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import practice.mobilesecurity.chapter09.entity.AppInfo;

public class AppInfoParser {
    public static List<AppInfo> getAppInfos(Context context){
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packInfos = pm.getInstalledPackages(0);
        List<AppInfo> appinfos = new ArrayList<>();
        Log.i("TEST", ""+packInfos.size());
        for(PackageInfo packInfo:packInfos){
            AppInfo appinfo = new AppInfo();
            String packname = packInfo.packageName;
            appinfo.packageName = packname;
            Drawable icon = packInfo.applicationInfo.loadIcon(pm);
            appinfo. icon = icon;
            String appname = packInfo.applicationInfo.loadLabel(pm).toString();
            appinfo.appName = appname;
            String apkpath = packInfo.applicationInfo.sourceDir;
            appinfo.apkPath = apkpath;
            appinfos.add(appinfo);
            appinfo = null;
        }
        return appinfos;
    }
}
