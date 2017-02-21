package practice.mobilesecurity.chapter08.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Judge if a service is running
 */
public class SystemInfoUtils {
    public static boolean isServiceRunning(Context context, String className) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> infos = manager.getRunningServices(200);
        for (ActivityManager.RunningServiceInfo info : infos) {
            String serviceClassName = info.service.getClassName();
            if (className.equals(serviceClassName)) {
                return true;
            }
        }
        return false;
    }
}
