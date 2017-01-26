package ptactice.mobilesecurity.chapter02;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import ptactice.mobilesecurity.R;
import ptactice.mobilesecurity.chapter02.utils.Constant;

@SuppressLint("ShowToast")
public class SetUp3Activity extends BaseSetUpActivity
        implements OnClickListener{
    private EditText mInputPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup3);
        initView();
    }

    private void initView() {
        ((RadioButton) findViewById(R.id.rb_third)).setChecked(true);
        findViewById(R.id.btn_add_contact).setOnClickListener(this);
        mInputPhone = (EditText) findViewById(R.id.et_input_phone);
        String safePhone = sp.getString("safePhone", null);
        if (!TextUtils.isEmpty(safePhone)) {
            mInputPhone.setText(safePhone);
        }
    }

    @Override
    public void showNext() {
        String safePhone = mInputPhone.getText().toString().trim();
        if (TextUtils.isEmpty(safePhone)) {
            Toast.makeText(this, "请输入安全号码",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        Editor edit = sp.edit();
        edit.putString(Constant.SAFE_PHONE, safePhone);
        edit.apply();
        startActivityAndFinishSelf(SetUp4Activity.class);
    }

    @Override
    public void showPre() {
        startActivityAndFinishSelf(SetUp2Activity.class);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_contact:
                startActivityForResult(
                        new Intent(this, ContactSelectActivity.class), 0);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            String phone = data.getStringExtra("phone");
            mInputPhone.setText(phone);
        }
    }
}
