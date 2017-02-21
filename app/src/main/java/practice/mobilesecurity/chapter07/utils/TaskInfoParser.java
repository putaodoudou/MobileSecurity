package practice.mobilesecurity.chapter07.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Debug;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import practice.mobilesecurity.R;
import practice.mobilesecurity.chapter07.entity.TaskInfo;

public class TaskInfoParser {
    public static List<TaskInfo> getRunningTaskInfos(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager packageManager = context.getPackageManager();
        List<RunningAppProcessInfo> processInfos = manager.getRunningAppProcesses();
        Log.i("processInfos", processInfos.size()+"");
        List<TaskInfo> taskInfos = new ArrayList<>();
        for (RunningAppProcessInfo processInfo : processInfos) {
            String packName = processInfo.processName;
            TaskInfo taskInfo = new TaskInfo();
            taskInfo.packageName = packName;
            Debug.MemoryInfo[] memoryInfos = manager.getProcessMemoryInfo(new int[] {processInfo.pid});
            long memSize = memoryInfos[0].getTotalPrivateDirty() * 1024;
            taskInfo.appMemory = memSize;
            try {
                PackageInfo packageInfo = packageManager.getPackageInfo(packName, 0);
                Drawable icon = packageInfo.applicationInfo.loadIcon(packageManager);
                taskInfo.appIcon = icon;
                String appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
                taskInfo.appName = appName;
                if ((ApplicationInfo.FLAG_SYSTEM & packageInfo.applicationInfo.flags) != 0) {
                    taskInfo.isUserApp = false;
                } else {
                    taskInfo.isUserApp = true;
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                taskInfo.appName = packName;
                taskInfo.appIcon = context.getResources().getDrawable(R.drawable.ic_default);
            }
            taskInfos.add(taskInfo);
        }
        return taskInfos;
    }
}
