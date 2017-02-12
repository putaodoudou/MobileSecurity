package practice.mobilesecurity.chapter03.entity;

import practice.mobilesecurity.chapter03.utils.Constant;

/**
 * 用于存储黑名单信息的实体类.
 */
public class BlackContactInfo {
    private String  phoneNumber;
    private String  contactName;
    private int     mode;
    public String getModeString(int mode) {
        switch (mode) {
            case Constant.PHONE:
                return "电话拦截";
            case Constant.SMS:
                return "短信拦截";
            case Constant.PHONE_AND_SMS:
                return "电话、短信拦截";
        }
        return "";
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getContactName() {
        return contactName;
    }

    public int getMode() {
        return mode;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
}
