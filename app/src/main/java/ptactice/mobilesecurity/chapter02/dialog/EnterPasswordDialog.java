package ptactice.mobilesecurity.chapter02.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyboardShortcutGroup;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import ptactice.mobilesecurity.R;

public class EnterPasswordDialog extends Dialog
        implements View.OnClickListener {
    private TextView mTitleTV;
    private EditText mEnterET;
    private Button mOKBtn;
    private Button mCancleBtn;
    private MyCallBack myCallBack;
    private Context context;

    public EnterPasswordDialog(Context context) {
        super(context, R.style.dialog_custom);
        this.context = context;
    }

    public void setCallBack(MyCallBack myCallBack) {
        this.myCallBack = myCallBack;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.enter_password_dialog);
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        mTitleTV = (TextView) findViewById(R.id.tv_enter_pwd_title);
        mEnterET = (EditText) findViewById(R.id.et_enter_password);
        mOKBtn = (Button) findViewById(R.id.btn_confirm);
        mCancleBtn = (Button) findViewById(R.id.btn_dismiss);
        mOKBtn.setOnClickListener(this);
        mCancleBtn.setOnClickListener(this);
    }

    public void setTile(String title) {
        if (!TextUtils.isEmpty(title)) {
            mTitleTV.setText(title);
        }
    }

    public String getPassword() {
        return mEnterET.getText().toString();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_confirm:
                myCallBack.confirm();
                break;
            case R.id.btn_dismiss:
                myCallBack.cancle();
                break;
        }
    }
    
    public interface MyCallBack {
        void confirm();
        void cancle();
    }
}
