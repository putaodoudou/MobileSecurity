package practice.mobilesecurity.chapter08.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import practice.mobilesecurity.chapter08.service.TrafficMonitoringService;
import practice.mobilesecurity.chapter08.utils.SystemInfoUtils;

public class BootCompleteReceiver extends BroadcastReceiver {
    private static final String serviceName = "practice.mobilesecurity.chapter08.service.TrafficMonitoringService";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!SystemInfoUtils.isServiceRunning(context, serviceName)) {
            context.startService(new Intent(context, TrafficMonitoringService.class));
        }
    }
}
