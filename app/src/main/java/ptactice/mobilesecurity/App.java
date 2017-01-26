package ptactice.mobilesecurity;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import ptactice.mobilesecurity.chapter02.utils.Constant;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        correctSIM();
    }

    public void correctSIM() {
        // 检查SIM卡是否发生变化
        SharedPreferences sp = getSharedPreferences(Constant.CONFIG,
                Context.MODE_PRIVATE);
        // 获取防盗保护的状态
        boolean protecting = sp.getBoolean(Constant.PROTECTING, true);
        if (protecting) {
            String bingSIM = sp.getString(Constant.SIM, "");
            TelephonyManager tm = (TelephonyManager) getSystemService(
                    Context.TELEPHONY_SERVICE);

            @SuppressLint("HardwareIds")
            String realSIM = tm.getSimSerialNumber();

            if (bingSIM.equals(realSIM)) {
                Log.i("", "SIM卡未发生变化，还是你的手机");
            } else {
                Log.i("", "SIM卡变化了");
                String safeNumber = sp.getString(Constant.SAFE_PHONE, "");
                if (!TextUtils.isEmpty(safeNumber)) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(safeNumber, null,
                            "你的亲友的SIM已经被更换！", null, null);
                }
            }
        }
    }
}
