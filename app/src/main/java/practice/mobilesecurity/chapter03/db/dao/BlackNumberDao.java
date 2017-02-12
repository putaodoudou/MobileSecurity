package practice.mobilesecurity.chapter03.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;

import java.util.ArrayList;
import java.util.List;

import practice.mobilesecurity.chapter03.db.BlackNumberOpenHelper;
import practice.mobilesecurity.chapter03.entity.BlackContactInfo;
import practice.mobilesecurity.chapter03.utils.Constant;

/**
 * 用于对黑名单中的数据进行增、删、查等操作
 */
public class BlackNumberDao {
    private BlackNumberOpenHelper blackNumberOpenHelper;

    public BlackNumberDao(Context context) {
        super();
        blackNumberOpenHelper = new BlackNumberOpenHelper(context);
    }

    public boolean add(BlackContactInfo blackContactInfo) {
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        if (blackContactInfo.getPhoneNumber().startsWith("+86")) {
            blackContactInfo.setPhoneNumber(blackContactInfo.getPhoneNumber()
                    .substring(3, blackContactInfo.getPhoneNumber().length()));
        }
        values.put(Constant.NUMBER, blackContactInfo.getPhoneNumber());
        values.put(Constant.NAME, blackContactInfo.getContactName());
        values.put(Constant.MODE, blackContactInfo.getMode());
        // table name instead of filename
        long rowId = db.insert(Constant.TABLE_NAME, null, values);
        return -1 != rowId;
    }

    public boolean delete(BlackContactInfo blackContactInfo) {
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
        int rowNumber = db.delete(Constant.TABLE_NAME, "number=?",
                new String[] {blackContactInfo.getPhoneNumber()});
        return rowNumber != 0;
    }

    public List<BlackContactInfo> getPageBlackNumber(
            int pageNumber, int pageSize) {
        SQLiteDatabase db = blackNumberOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select number,mode,name from " +
                Constant.TABLE_NAME + " limit ? offset ?",
                new String[] {String.valueOf(pageSize),
                String.valueOf(pageSize *pageNumber)});
        List<BlackContactInfo> mBlackContactInfos = new ArrayList<>();
        while (cursor.moveToNext()) {
            BlackContactInfo info = new BlackContactInfo();
            info.setPhoneNumber(cursor.getString(0));
            info.setMode(cursor.getInt(1));
            info.setContactName(cursor.getString(2));
            mBlackContactInfos.add(info);
        }
        cursor.close();
        db.close();
        SystemClock.sleep(30);
        return mBlackContactInfos;
    }

    public boolean IsNumberExist(String number) {
        SQLiteDatabase db = blackNumberOpenHelper.getReadableDatabase();
        Cursor cursor = db.query(Constant.TABLE_NAME, null, "number=?",
                new String[] { number }, null, null, null);
        if (cursor.moveToNext()) {
            cursor.close();
            db.close();
            return true;
        }
        cursor.close();
        db.close();
        return false;
    }

    public int getBlackContactMode(String number) {
        SQLiteDatabase db = blackNumberOpenHelper.getReadableDatabase();
        Cursor cursor = db.query(Constant.TABLE_NAME, new String[] {"mode"},
                "number=?", new String[] { number }, null, null, null);
        int mode = 0;
        if (cursor.moveToNext()) {
            mode = cursor.getInt(cursor.getColumnIndex("mode"));
        }
        cursor.close();
        db.close();
        return mode;
    }

    public int getTotalNumber() {
        SQLiteDatabase db = blackNumberOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(*) from " +
                Constant.TABLE_NAME, null);
        cursor.moveToNext();
        int count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count;
    }
}
