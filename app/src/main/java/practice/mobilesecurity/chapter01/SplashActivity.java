package practice.mobilesecurity.chapter01;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import practice.mobilesecurity.R;
import practice.mobilesecurity.chapter01.utils.MyUtils;
import practice.mobilesecurity.chapter01.utils.VersionUpdateUtils;

public class SplashActivity extends Activity {
    private TextView mVersionTV;
    private String mVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set no-title style
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        mVersion = MyUtils.getVersion(getApplicationContext());
        initView();
        final VersionUpdateUtils updateUtils = new VersionUpdateUtils(
                mVersion, SplashActivity.this);
        new Thread() {
            public void run() {
                updateUtils.getServerVersion();
            }
        }.start();
    }

    private void initView() {
        mVersionTV = (TextView) findViewById(R.id.tv_splash_version);
        mVersionTV.setText("版本号"+mVersion);
    }
}
