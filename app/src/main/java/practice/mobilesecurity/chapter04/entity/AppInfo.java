package practice.mobilesecurity.chapter04.entity;

import android.graphics.drawable.Drawable;

public class AppInfo {
    public String packageName;
    public Drawable icon;
    public String appName;
    public String apkPath;
    public long appSize;

    // 是否是手机存储
    public boolean isInRoom;

    // 是否是用户应用
    public boolean isUserApp;

    // 是否选中
    public boolean isSelected = false;

    // 拿到APP位置字符串
    public String getAppLocation(boolean isInRoom) {
        if (isInRoom) {
            return "手机内存";
        } else {
            return "外部存储";
        }
    }
}
