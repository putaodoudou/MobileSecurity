package practice.mobilesecurity.chapter08.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.TrafficStats;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import practice.mobilesecurity.chapter08.db.dao.TrafficDao;

public class TrafficMonitoringService extends Service {
    private long mOldRxBytes;
    private long mOldTxBytes;
    private TrafficDao dao;
    private SharedPreferences mSP;
    private long usedFlow;
    boolean flag = true;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mOldRxBytes = TrafficStats.getMobileRxBytes();
        mOldTxBytes = TrafficStats.getTotalTxBytes();
        dao = new TrafficDao(this);
        mSP = getSharedPreferences("config", MODE_PRIVATE);
        mThread.start();
    }

    private Thread mThread = new Thread() {
        @Override
        public void run() {
            while (flag) {
                try {
                    Thread.sleep(2000 * 60);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                updateTodayGPRS();
            }
        }

        private void updateTodayGPRS() {
            usedFlow = mSP.getLong("usedflow", 0);
            Date date = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            if (calendar.DAY_OF_MONTH == 1 & calendar.HOUR_OF_DAY == 0
                    & calendar.MINUTE < 1 & calendar.SECOND < 30) {
                usedFlow = 0;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dataString = sdf.format(date);
            long moblieGPRS = dao.getMobileGPRS(dataString);
            long mobileRxBytes = TrafficStats.getMobileRxBytes();
            long mobileTxBytes = TrafficStats.getMobileTxBytes();
            long newGprs = (mobileRxBytes + mobileTxBytes) - mOldRxBytes
                    - mOldTxBytes;
            mOldRxBytes = mobileRxBytes;
            mOldTxBytes = mobileTxBytes;
            if (newGprs < 0) {
                newGprs = mobileRxBytes + mobileTxBytes;
            }
            if (moblieGPRS == -1) {
                dao.insertTodayGPRS(newGprs);
            } else {
                if (moblieGPRS < 0) {
                    moblieGPRS = 0;
                }
                dao.UpdateTodayGPRS(moblieGPRS + newGprs);
            }
            usedFlow = usedFlow + newGprs;
            SharedPreferences.Editor edit = mSP.edit();
            edit.putLong("usedflow", usedFlow);
            edit.commit();
        }
    };
}
