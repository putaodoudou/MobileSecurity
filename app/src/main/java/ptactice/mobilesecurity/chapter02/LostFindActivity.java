package ptactice.mobilesecurity.chapter02;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.content.SharedPreferences.Editor;

import ptactice.mobilesecurity.R;

public class LostFindActivity extends Activity
        implements OnClickListener {
    private TextView mSafePhoneTV;
    private RelativeLayout mEnterSetupRL;
    private SharedPreferences mSharePreferences;
    private ToggleButton mToggleButton;
    private TextView mProtectStatusTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_lostfind);
        mSharePreferences = getSharedPreferences("config", MODE_PRIVATE);
        if (!isSetUp()) {
            startSetUp1Activity();
        }
        initView();
    }

    private void initView() {
        TextView mTitleTV = (TextView) findViewById(R.id.tv_title);
        mTitleTV.setText("手机防盗");
        ImageView mLeftImgv = (ImageView) findViewById(R.id.imgv_leftbtn);
        mLeftImgv.setOnClickListener(this);
        mLeftImgv.setImageResource(R.drawable.back);
        findViewById(R.id.rl_titlebar).setBackgroundColor(
                getResources().getColor(R.color.purple));
        mSafePhoneTV = (TextView) findViewById(R.id.tv_safe_phone);
        mSafePhoneTV.setText(mSharePreferences.getString("safePhone",
                "default"));
        mToggleButton = (ToggleButton) findViewById(R.id.togglebtn_lostfind);
        mEnterSetupRL = (RelativeLayout) findViewById(
                R.id.rl_enter_setup_wizard);
        mEnterSetupRL.setOnClickListener(this);
        mProtectStatusTV = (TextView) findViewById(
                R.id.tv_lostfind_protect_status);
        boolean protecting = mSharePreferences.getBoolean("protecting", true);
        if (protecting) {
            mProtectStatusTV.setText("防盗保护已经开启");
            mToggleButton.setChecked(true);
        } else {
            mProtectStatusTV.setText("防盗保护没有开启");
            mToggleButton.setChecked(false);
        }

        mToggleButton.setOnCheckedChangeListener(
                new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    mProtectStatusTV.setText("防盗保护已经开启");
                } else {
                    mProtectStatusTV.setText("防盗保护没有开启");
                }
                Editor editor = mSharePreferences.edit();
                editor.putBoolean("protecting", isChecked);
                editor.commit();
            }
        });
    }


    private boolean isSetUp() {
        return mSharePreferences.getBoolean("isSetUp", false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_enter_setup_wizard:
                startSetUp1Activity();
                break;
            case R.id.imgv_leftbtn:
                finish();
                break;
        }
    }

    private void startSetUp1Activity() {
        Intent intent = new Intent(LostFindActivity.this,
                SetUp1Activity.class);
        startActivity(intent);
        finish();
    }
}
