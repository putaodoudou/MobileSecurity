package practice.mobilesecurity.chapter05.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;

public class AntiVirusDao {
    public static String checkVirus(String md5) {
        String desc = null;
        SQLiteDatabase db = SQLiteDatabase.openDatabase(
                "/data/data/practice.mobilesecurity/files/antivirus.db", null,
                SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.rawQuery("select desc from datable where md5=?",
                new String[] { md5 });
        if (cursor.moveToNext()) {
            desc = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return desc;
    }

    public  static boolean isDBExit() {
        //??????path
        File file = new File(
                "/data/data/ptactice.mobilesecurity/files/antivirus.db");
        return file.exists() && file.length() > 0;
    }

    public static String getDBVersionNum() {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(
                "/data/data/ptactice.mobilesecurity/files/antivirus.db", null,
                SQLiteDatabase.OPEN_READONLY);
        String versionnumber = "0";
        Cursor cursor = db.rawQuery("select  subcnt from version", null);
        if (cursor.moveToNext()) {
            versionnumber = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return versionnumber;
    }

    public static void updateDBVersion(int newversion){
        SQLiteDatabase db = SQLiteDatabase.openDatabase(
                "/data/data/ptactice.mobilesecurity/files/antivirus.db", null,
                SQLiteDatabase.OPEN_READWRITE);
        String versionnumber = "0";
        ContentValues values = new ContentValues();
        values.put("subcnt", newversion);
        db.update("version", values, null, null);
        db.close();
    }

    public static void add(String desc,String md5){
        SQLiteDatabase db = SQLiteDatabase.openDatabase(
                "/data/data/ptactice.mobilesecurity/files/antivirus.db", null,
                SQLiteDatabase.OPEN_READWRITE);
        ContentValues values = new ContentValues();
        values.put("md5", md5);
        values.put("desc", desc);
        values.put("type", 6);
        values.put("name", "Android.Hack.i22hkt.a");
        db.insert("datable", null, values);
        db.close();
    }
}
