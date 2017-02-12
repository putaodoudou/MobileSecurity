package practice.mobilesecurity.chapter03.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsMessage;

import practice.mobilesecurity.chapter02.utils.Constant;
import practice.mobilesecurity.chapter03.db.dao.BlackNumberDao;

public class InterceptSmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences mSP = context.getSharedPreferences(
                Constant.CONFIG, Context.MODE_PRIVATE);
        boolean BlackNumStatus = mSP.getBoolean("BlackNumStatus", true);
        if (!BlackNumStatus) {
            return;
        }
        BlackNumberDao dao =  new BlackNumberDao(context);
        Object[] objects = (Object[]) intent.getExtras().get("pdus");
        for (Object object : objects) {
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte []) object);
            String sender = smsMessage.getOriginatingAddress();
            String body = smsMessage.getMessageBody();
            if (sender.startsWith("+86")) {
                sender = sender.substring(3, sender.length());
            }
            int mode = dao.getBlackContactMode(sender);
            if (mode == practice.mobilesecurity.
                        chapter03.utils.Constant.PHONE_AND_SMS
                    || mode == practice.mobilesecurity.
                        chapter03.utils.Constant.SMS) {
                abortBroadcast();
            }
        }
    }
}
