package practice.mobilesecurity;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import android.content.SharedPreferences.Editor;

import practice.mobilesecurity.chapter01.adapter.HomeAdapter;
import practice.mobilesecurity.chapter02.LostFindActivity;
import practice.mobilesecurity.chapter02.dialog.EnterPasswordDialog;
import practice.mobilesecurity.chapter02.dialog.SetUpPasswordDialog;
import practice.mobilesecurity.chapter02.receiver.MyDeviceAdminReceiver;
import practice.mobilesecurity.chapter02.utils.Constant;
import practice.mobilesecurity.chapter02.utils.MD5Utils;
import practice.mobilesecurity.chapter03.SecurityPhoneActivity;
import practice.mobilesecurity.chapter04.AppManagerActivity;
import practice.mobilesecurity.chapter05.VirusScanActivity;
import practice.mobilesecurity.chapter06.CacheClearListActivity;
import practice.mobilesecurity.chapter07.ProcessManagerActivity;

public class HomeActivity extends Activity {
    private GridView gv_home;
    // store password
    private SharedPreferences mSharedPreferences;
    private DevicePolicyManager policyManager;
    private ComponentName componentName;
    private long mExitTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_home);

        mSharedPreferences = getSharedPreferences(
                Constant.CONFIG, MODE_PRIVATE);

        gv_home = (GridView) findViewById(R.id.gv_home);
        gv_home.setAdapter(new HomeAdapter(HomeActivity.this));
        gv_home.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                switch (position) {
                    case 0:
                        if (isSetUpPassword()) {
                            showEnterPswdDialog();
                        } else {
                            showSetUpPswDialog();
                        }
                        break;
                    case 1:
                        startActivity(SecurityPhoneActivity.class);
                        break;
                    case 2:
                        startActivity(AppManagerActivity.class);
                        break;
                    case 3:
                        startActivity(VirusScanActivity.class);
                        break;
                    case 4:
                        startActivity(CacheClearListActivity.class);
                        break;
                    case 5:
                        startActivity(ProcessManagerActivity.class);
                        break;

                }
            }
        });

        // 获取设备管理员
        policyManager = (DevicePolicyManager)
                getSystemService(DEVICE_POLICY_SERVICE);
        // 申请权限
        componentName = new ComponentName(this, MyDeviceAdminReceiver.class);
        // 判断，如果没有权限则申请权限
        boolean active = policyManager.isAdminActive(componentName);
        if (!active) {
            Intent intent = new Intent(
                    DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                    componentName);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    "获取超级管理员权限，用于远程锁屏和清除数据");
            startActivity(intent);
        }
    }

    /**
     * 弹出设置密码对话框
     */
    private void showSetUpPswDialog() {
        final SetUpPasswordDialog setUpPasswordDialog =
                new SetUpPasswordDialog(HomeActivity.this);
        setUpPasswordDialog.setCallBack(new SetUpPasswordDialog.MyCallBack() {
            @Override
            public void ok() {
                String firstPwsd = setUpPasswordDialog.mFirstPWDET.
                        getText().toString().trim();
                String affirmPwsd = setUpPasswordDialog.mAffirmET.
                        getText().toString().trim();
                if (!TextUtils.isEmpty(firstPwsd)
                        && !TextUtils.isEmpty(affirmPwsd)) {
                    if (firstPwsd.equals(affirmPwsd)) {
                        savePassword(affirmPwsd);
                        setUpPasswordDialog.dismiss();
                        showEnterPswdDialog();
                    } else {
                        Toast.makeText(HomeActivity.this, "两次密码不一致",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "密码不能为空",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void cancle() {
                setUpPasswordDialog.dismiss();
            }
        });
        setUpPasswordDialog.setCancelable(true);
        setUpPasswordDialog.show();
    }

    /**
     * 弹出输入密码对话框
     */
    private void showEnterPswdDialog() {
        final String password = getPassword();
        final EnterPasswordDialog enterPasswordDialog =
                new EnterPasswordDialog(HomeActivity.this);
        enterPasswordDialog.setCallBack(new EnterPasswordDialog.MyCallBack() {
            @Override
            public void confirm() {
                if (TextUtils.isEmpty(enterPasswordDialog.getPassword())) {
                    Toast.makeText(HomeActivity.this, "密码不能为空",
                            Toast.LENGTH_SHORT).show();
                } else if (password.equals(MD5Utils.encode(
                        enterPasswordDialog.getPassword()))) {
                    enterPasswordDialog.dismiss();
                    startActivity(LostFindActivity.class);
                } else {
                    enterPasswordDialog.dismiss();
                    Toast.makeText(HomeActivity.this, "密码有误，请重新输入",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void cancle() {
                enterPasswordDialog.dismiss();
            }
        });
        enterPasswordDialog.setCancelable(true);
        enterPasswordDialog.show();
    }

    /**
     * 保存密码
     */
    private void savePassword(String affirmPwsd) {
        Editor editor = mSharedPreferences.edit();
        editor.putString("PhoneAntiTheftPWD", MD5Utils.encode(affirmPwsd));
        editor.commit();
    }

    /**
     * 获取密码
     */
    private String getPassword() {
        String password = mSharedPreferences.getString(
                "PhoneAntiTheftPWD", null);
        if (TextUtils.isEmpty(password)) {
            return "";
        }
        return password;
    }

    /**
     * 判断用户是否已经设置过密码
     */
    private boolean isSetUpPassword() {
        String password = mSharedPreferences.getString(
                "PhoneAntiTheftPWD", null);
        if (TextUtils.isEmpty(password)) {
            return false;
        }
        return true;
    }

    /**
     *
     * @param cls,新的Activity的字节码
     */
    public void startActivity(Class<?> cls) {
        Intent intent = new Intent(HomeActivity.this, cls);
        startActivity(intent);
    }

    // double click back button-->exit
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis()-mExitTime) > 2000) {
                Toast.makeText(this, "再按一次退出程序",
                        Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
