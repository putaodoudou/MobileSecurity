package practice.mobilesecurity.chapter08;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import practice.mobilesecurity.R;

public class OperatorSetActivity extends Activity implements OnClickListener{
    private Spinner mSelectSP;
    private String[] operators = {"中国移动", "中国联通", "中国电信"};
    private ArrayAdapter mSelectAdapter;
    private SharedPreferences mSP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_operatorset);
        mSP = getSharedPreferences("config", MODE_PRIVATE);
        initView();
    }

    @SuppressWarnings("unchecked")
    private void initView() {
        findViewById(R.id.rl_titlebar).setBackgroundColor(
                getResources().getColor(R.color.light_green));
        ImageView mLeftImgv = (ImageView) findViewById(R.id.imgv_leftbtn);
        ((TextView) findViewById(R.id.tv_title)).setText("运营商设置");
        mLeftImgv.setOnClickListener(this);
        mLeftImgv.setImageResource(R.drawable.back);
        mSelectSP = (Spinner) findViewById(R.id.spinner_operator_select);
        mSelectAdapter = new ArrayAdapter(this,
                R.layout.item_spinner_operatorset, R.id.tv_provice, operators);
        mSelectSP.setAdapter(mSelectAdapter);
        findViewById(R.id.btn_operator_finish).setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        SharedPreferences.Editor edit = mSP.edit();
        switch (v.getId()) {
            case R.id.imgv_leftbtn:
                edit.putBoolean("isset_operator", false);
                finish();
                break;
            case R.id.btn_operator_finish:
                edit.putInt("operator", mSelectSP.getSelectedItemPosition() + 1);
                edit.putBoolean("isset_operator", true);
                edit.commit();
                startActivity(new Intent(this, TrafficMonitoringActivity.class));
                finish();
                break;
        }
    }
}
