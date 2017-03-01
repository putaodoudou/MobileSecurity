package practice.mobilesecurity.chapter09.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

public class NumBelongtoDao {
    public static String getLocation(String phoneNumber) {
        String location = phoneNumber;
        SQLiteDatabase db = SQLiteDatabase.openDatabase(
                "/data/data/practice.mobilesecurity/files/address.db", null,
                SQLiteDatabase.OPEN_READONLY);

        if (phoneNumber.matches("^1[34578]\\d{9}$")) {
            Cursor cursor = db.rawQuery("select location from data2 where id=(select outkey from data1 where id=?)",
                            new String[] { phoneNumber.substring(0, 7) });
            if (cursor.moveToNext()) {
                location = cursor.getString(0);
            }
            cursor.close();
        } else {
            switch (phoneNumber.length()) {
                case 3:
                    if ("110".equals(phoneNumber)) {
                        location = "匪警";
                    } else if ("120".equals(phoneNumber)) {
                        location = "急救";
                    } else {
                        location = "报警号码";
                    }
                    break;
                case 4:
                    location = "模拟器";
                    break;
                case 5:
                    location = "客服电话";
                    break;
                case 7:
                    location = "本地电话";
                    break;
                case 8:
                    location = "本地电话";
                    break;
                default:
                    if(location.length()>=9&& location.startsWith("0")){
                        String address = null;
                        //select location from data2 where area = '10'
                        Cursor cursor = db.rawQuery("select location from data2 where area = ?", new String[]{location.substring(1, 3)});
                        if(cursor.moveToNext()){
                            String str = cursor.getString(0);
                            address = str.substring(0, str.length()-2);
                        }
                        cursor.close();
                        cursor = db.rawQuery("select location from data2 where area = ?", new String[]{location.substring(1, 4)});
                        if(cursor.moveToNext()){
                            String str = cursor.getString(0);
                            address = str.substring(0, str.length()-2);
                        }
                        cursor.close();
                        if(!TextUtils.isEmpty(address)){
                            location = address;
                        }
                    }
                    break;
            }
        }
        db.close();
        return location;
    }
}
