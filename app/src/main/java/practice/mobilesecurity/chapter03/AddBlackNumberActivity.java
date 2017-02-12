package practice.mobilesecurity.chapter03;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import practice.mobilesecurity.R;
import practice.mobilesecurity.chapter03.db.dao.BlackNumberDao;
import practice.mobilesecurity.chapter03.entity.BlackContactInfo;
import practice.mobilesecurity.chapter03.utils.Constant;

/**
 * 添加黑名单界面
 */
public class AddBlackNumberActivity extends Activity
        implements OnClickListener {
    private CheckBox mSmsCB;
    private CheckBox mTelCB;
    private EditText mNumET;
    private EditText mNameET;
    private BlackNumberDao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_black_number);
        dao = new BlackNumberDao(this);
        initView();
    }

    private void initView() {
        findViewById(R.id.rl_titlebar).setBackgroundColor(
                getResources().getColor(R.color.bright_purple));
        ((TextView) findViewById(R.id.tv_title)).setText("添加黑名单");
        ImageView mLeftImgv = (ImageView) findViewById(R.id.imgv_leftbtn);
        mLeftImgv.setOnClickListener(this);
        mLeftImgv.setImageResource(R.drawable.back);
        mSmsCB = (CheckBox) findViewById(R.id.cb_black_number_sms);
        mTelCB = (CheckBox) findViewById(R.id.cb_black_number_phone);
        mNumET = (EditText) findViewById(R.id.et_black_number);
        mNameET = (EditText) findViewById(R.id.et_black_name);

        findViewById(R.id.add_black_num_btn).setOnClickListener(this);
        findViewById(R.id.add_from_contact_btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgv_leftbtn:
                finish();
                break;
            case R.id.add_black_num_btn:
                String number = mNumET.getText().toString().trim();
                String name = mNameET.getText().toString().trim();
                if (TextUtils.isEmpty(number) || TextUtils.isEmpty(name)) {
                    Toast.makeText(this, "电话号码和手机号不能为空",
                            Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    BlackContactInfo info = new BlackContactInfo();
                    info.setContactName(name);
                    info.setPhoneNumber(number);
                    // ????? --->  &
                    if (mSmsCB.isChecked() & mTelCB.isChecked()) {
                        info.setMode(Constant.PHONE_AND_SMS);
                    } else if (mSmsCB.isChecked() & !mTelCB.isChecked()) {
                        info.setMode(Constant.SMS);
                    } else if (!mSmsCB.isChecked() & mTelCB.isChecked()) {
                        info.setMode(Constant.PHONE);
                    } else {
                        Toast.makeText(this, "请选择拦截模式",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!dao.IsNumberExist(info.getPhoneNumber())) {
                        dao.add(info);
                    } else {
                        Toast.makeText(this, "该号码已添加至黑名单",
                                Toast.LENGTH_SHORT).show();
                    }
                    finish();
                }
                break;
            case R.id.add_from_contact_btn:
                startActivityForResult(new Intent(this, ContactSelectActivity.class), 0);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            String phone = data.getStringExtra(Constant.NUMBER);
            String name = data.getStringExtra(Constant.NAME);
            Toast.makeText(this, "phone:"+phone+"name:"+name, Toast.LENGTH_SHORT).show();
            mNameET.setText(name);
            mNumET.setText(phone);
        }
    }
}
