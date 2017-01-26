package ptactice.mobilesecurity.chapter02.receiver;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;

import ptactice.mobilesecurity.R;
import ptactice.mobilesecurity.chapter02.service.GPSLocationService;

public class SmsLostFindReceiver extends BroadcastReceiver {
    private static final String TAG =
            SmsLostFindReceiver.class.getSimpleName();
    private SharedPreferences sharedPreferences;

    @Override
    public void onReceive(Context context, Intent intent) {
        sharedPreferences = context.getSharedPreferences("config",
                Activity.MODE_PRIVATE);
        boolean protecting = sharedPreferences.getBoolean("protecting", true);
        if (protecting) {
            DevicePolicyManager dpm = (DevicePolicyManager)
                    context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            Object[] objects = (Object[]) intent.getExtras().get("pdus");
            for (Object object : objects) {
                SmsMessage smsMessage =
                        SmsMessage.createFromPdu((byte[]) object);
                String sender = smsMessage.getOriginatingAddress();
                String body = smsMessage.getMessageBody();
                String safePhone =
                        sharedPreferences.getString("safePhone", null);
                if (!TextUtils.isEmpty(safePhone) & sender.equals(safePhone)) {
                    if ("#*location*#".equals(body)) {
                        Log.i(TAG, "return location");
                        Intent service = new Intent(context,
                                GPSLocationService.class);
                        context.startService(service);
                        abortBroadcast();
                    } else if ("#*alarm*#".equals(body)) {
                        Log.i(TAG, "alarm music");
                        MediaPlayer player = MediaPlayer.create(context,
                                R.raw.ylzs);
                        player.setVolume(1.0f, 1.0f);
                        player.start();
                        abortBroadcast();
                    } else if ("#*wipedata*#".equals(body)) {
                        Log.i(TAG, "wipe data");
                        dpm.wipeData(DevicePolicyManager
                                .WIPE_EXTERNAL_STORAGE);
                        abortBroadcast();
                    } else if ("#*lockscreen*#".equals(body)) {
                        Log.i(TAG, "lock screen");
                        dpm.resetPassword("123", 0);
                        dpm.lockNow();//crush when root failed
                        abortBroadcast();
                    }
                }
            }
        }
    }
}
