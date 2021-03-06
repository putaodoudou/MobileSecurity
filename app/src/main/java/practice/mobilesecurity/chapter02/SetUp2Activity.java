package practice.mobilesecurity.chapter02;

import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import practice.mobilesecurity.R;
import practice.mobilesecurity.chapter02.utils.Constant;

public class SetUp2Activity extends BaseSetUpActivity
        implements OnClickListener{
    private TelephonyManager mTelephonyManager;
    private Button mBindSIMBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2);
        mTelephonyManager = (TelephonyManager) getSystemService(
                TELEPHONY_SERVICE);
        initView();
    }

    private void initView() {
        ((RadioButton) findViewById(R.id.rb_second)).setChecked(true);
        mBindSIMBtn = (Button)findViewById(R.id.btn_bind_sim);
        mBindSIMBtn.setOnClickListener(this);
        if (isBind()) {
            mBindSIMBtn.setEnabled(false);
        } else {
            mBindSIMBtn.setEnabled(true);
        }
    }

    private boolean isBind() {
        String simString = sp.getString(Constant.SIM, null);
        return !TextUtils.isEmpty(simString);
    }

    @Override
    public void showNext() {
        if (!isBind()) {
            Toast.makeText(this, "你还没有绑定SIM卡",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        startActivityAndFinishSelf(SetUp3Activity.class);
    }

    @Override
    public void showPre() {
        startActivityAndFinishSelf(SetUp1Activity.class);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_bind_sim:
                bindSIM();
                break;
        }
    }

    private void bindSIM() {
        if (!isBind()) {
            String simSerialNumber = mTelephonyManager.getSimSerialNumber();
            Editor edit = sp.edit();
            edit.putString(Constant.SIM, simSerialNumber);
            edit.apply();
            Toast.makeText(this, "SIM卡绑定成功", Toast.LENGTH_SHORT).show();
            mBindSIMBtn.setEnabled(false);
        } else {
            Toast.makeText(this, "SIM卡已经绑定!", Toast.LENGTH_SHORT).show();
            mBindSIMBtn.setEnabled(false);
        }
    }
}
