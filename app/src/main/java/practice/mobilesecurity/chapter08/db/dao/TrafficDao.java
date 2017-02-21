package practice.mobilesecurity.chapter08.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import practice.mobilesecurity.chapter08.db.TrafficOpenHelper;

public class TrafficDao {
    private TrafficOpenHelper helper;

    public TrafficDao(Context context) {
        helper = new TrafficOpenHelper(context);
    }

    /**
     * Get for a day
     */
    public long getMobileGPRS(String dataString) {
        SQLiteDatabase db = helper.getReadableDatabase();
        long gprs = 0;
        Cursor cursor = db.rawQuery("select gprs from traffic where date=?", new String[] {"datetime(" +
                dataString +")"});
        if (cursor.moveToNext()) {
            String gprsStr = cursor.getString(0);
            if (!TextUtils.isEmpty(gprsStr)) {
                gprs = Long.parseLong(gprsStr);
            }
        } else {
            gprs = -1;
        }
        return gprs;
    }

    /**
     * Today
     */
    public void insertTodayGPRS(long gprs) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Date dNow = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dNow);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dataString = dateFormat.format(dNow);
        ContentValues values = new ContentValues();
        values.put("gprs", String.valueOf(gprs));
        values.put("date", "datetime(" + dataString + ")");
        db.insert("traffic", null, values);
    }

    public void UpdateTodayGPRS(long gprs) {
        SQLiteDatabase db = helper.getWritableDatabase();
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dataString = dateFormat.format(date);
        ContentValues values = new ContentValues();
        values.put("gprs", String.valueOf(gprs));
        values.put("date", "datetime(" + dataString + ")");
        db.update("traffic", values, "date=?", new String[] { "datetime("
                + dataString + ")" });
    }
}
