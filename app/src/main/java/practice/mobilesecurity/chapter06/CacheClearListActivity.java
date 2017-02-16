package practice.mobilesecurity.chapter06;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import practice.mobilesecurity.R;
import practice.mobilesecurity.chapter06.adapter.CacheCleanAdapter;
import practice.mobilesecurity.chapter06.entity.CacheInfo;
import practice.mobilesecurity.chapter06.utils.Constant;

public class CacheClearListActivity extends Activity
        implements OnClickListener{
    protected static final int SCANNING = 100;
    protected static final int FINISH = 101;

    private AnimationDrawable animation;
    private TextView mRecomandTV;
    private TextView mCanClearTV;
    private long cacheMemory;
    private List<CacheInfo> cacheInfos = new ArrayList<>();
    private List<CacheInfo> mCacheInfos = new ArrayList<>();
    private PackageManager packageManager;
    private CacheCleanAdapter adapter;
    private ListView mCacheLV;
    private Button mCacheBtn;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SCANNING:
                    PackageInfo info = (PackageInfo) msg.obj;
                    mRecomandTV.setText("正在扫描" + info.packageName);
                    mCanClearTV.setText("已扫描缓存" + Formatter.formatFileSize(
                            CacheClearListActivity.this, cacheMemory));
                    mCacheInfos.clear();
                    mCacheInfos.addAll(cacheInfos);
                    adapter.notifyDataSetChanged();
                    mCacheLV.setSelection(mCacheInfos.size());
                    break;
                case FINISH:
                    animation.stop();
                    if (cacheMemory > 0) {
                        mCacheBtn.setEnabled(true);
                    } else {
                        mCacheBtn.setEnabled(false);
                        Toast.makeText(CacheClearListActivity.this,
                                "您的手机洁净如新", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };
    private Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_cache_clear_list);
        packageManager = getPackageManager();
        initView();
    }

    private void initView() {
        findViewById(R.id.rl_titlebar).setBackgroundColor(
                getResources().getColor(R.color.rose_red));
        ImageView mLeftImgv = (ImageView) findViewById(R.id.imgv_leftbtn);
        mLeftImgv.setOnClickListener(this);
        mLeftImgv.setImageResource(R.drawable.back);
        ((TextView) findViewById(R.id.tv_title)).setText("扫描缓存");
        mRecomandTV = (TextView) findViewById(R.id.tv_recommend_clean);
        mCanClearTV = (TextView) findViewById(R.id.tv_can_clean);
        mCacheLV = (ListView) findViewById(R.id.lv_scan_cache);
        mCacheBtn = (Button) findViewById(R.id.btn_clean_all);
        mCacheBtn.setOnClickListener(this);
        animation = (AnimationDrawable)
                findViewById(R.id.imgv_broom).getBackground();
        animation.setOneShot(false);
        animation.start();
        adapter = new CacheCleanAdapter(this, mCacheInfos);
        mCacheLV.setAdapter(adapter);
        fillData();
    }

    private void fillData() {
        thread = new Thread() {
            @Override
            public void run() {
                cacheInfos.clear();
                List<PackageInfo> infos = packageManager.
                        getInstalledPackages(0);
                for (PackageInfo info : infos) {
                    getCacheSize(info);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Message msg = Message.obtain();
                    msg.obj = info;
                    msg.what = SCANNING;
                    handler.sendMessage(msg);
                }
                Message msg = Message.obtain();
                msg.what = FINISH;
                handler.sendMessage(msg);
            }
        };
        thread.start();
    }

    public void getCacheSize(PackageInfo info) {
        try {
            Method method = PackageManager.class.getDeclaredMethod(
                    "getPackageSizeInfo", String.class,
                    IPackageStatsObserver.class);
            method.invoke(packageManager, info.packageName, new MyPackObserver(info));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        animation.stop();
        if (thread != null) {
            thread.interrupt();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case  R.id.imgv_leftbtn:
                finish();
                break;
            case R.id.btn_clean_all:
                if (cacheMemory > 0) {
                    Intent intent = new Intent(this, CleanCacheActivity.class);
                    intent.putExtra(Constant.CACHE_SIZE, cacheMemory);
                    startActivity(intent);
                    finish();
                }
                break;
        }
    }

    private class MyPackObserver extends IPackageStatsObserver.Stub {
        private PackageInfo info;
        public MyPackObserver(PackageInfo info) {
            this.info = info;
        }
        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
                throws RemoteException {
            long cacheSize = pStats.cacheSize;
            if (cacheSize >= 0) {
                CacheInfo cacheInfo = new CacheInfo();
                cacheInfo.cacheSize = cacheSize;
                cacheInfo.packageName = info.packageName;
                cacheInfo.appName = info.applicationInfo.loadLabel(
                        packageManager).toString();
                cacheInfo.appIcon = info.applicationInfo.
                        loadIcon(packageManager);
                cacheInfos.add(cacheInfo);
                cacheMemory += cacheSize;
            }
        }
    }
}
