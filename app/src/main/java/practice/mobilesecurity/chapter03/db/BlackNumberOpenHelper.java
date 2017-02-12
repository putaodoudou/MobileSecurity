package practice.mobilesecurity.chapter03.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import practice.mobilesecurity.chapter03.utils.Constant;

/**
 * 用于创建黑名单数据库
 */
public class BlackNumberOpenHelper extends SQLiteOpenHelper {
    public BlackNumberOpenHelper(Context context) {
        super(context, Constant.FILE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Constant.DB_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion, int newVersion) { }
}
