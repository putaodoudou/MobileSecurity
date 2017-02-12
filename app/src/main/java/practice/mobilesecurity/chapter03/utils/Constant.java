package practice.mobilesecurity.chapter03.utils;

/**
 *
 */
public class Constant {
    public static final String FILE_NAME = "blackNumber.db";
    public static final String TABLE_NAME = "black_number";

    public static final String NUMBER = "number";
    public static final String NAME = "name";
    public static final String MODE = "mode";

    public static final String DB_CREATE = "create table " + TABLE_NAME +
            " (id integer primary key autoincrement, " + NUMBER + " varchar(20), " +
            NAME + " varchar(255), " + MODE +" integer)";
    public static final int PHONE = 1;
    public static final int SMS = 2;
    public static final int PHONE_AND_SMS = 3;

}
