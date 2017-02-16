package practice.mobilesecurity.chapter06;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.IPackageDataObserver;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.Random;

import practice.mobilesecurity.R;
import practice.mobilesecurity.chapter06.utils.Constant;

public class CleanCacheActivity extends Activity
        implements OnClickListener {
    protected static final int CLEANNING = 100;
    private AnimationDrawable animation;
    private long cacheMemory;
    private TextView mMemoryTV;
    private TextView mMemoryUnitTV;
    private PackageManager packageManager;
    private FrameLayout mCleanCacheFL;
    private FrameLayout mFinishCleanFL;
    private TextView mSizeTV;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CLEANNING:
                    long memory = (Long) msg.obj;
                    formatMemory(memory);
                    if (memory == cacheMemory) {
                        animation.stop();
                        mCleanCacheFL.setVisibility(View.GONE);
                        mFinishCleanFL.setVisibility(View.VISIBLE);
                        mSizeTV.setText("成功清理" + Formatter.formatFileSize(
                                CleanCacheActivity.this, cacheMemory));
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_cleancache);
        initView();
        packageManager = getPackageManager();
        Intent intent = getIntent();
        cacheMemory = intent.getLongExtra(Constant.CACHE_SIZE, 0);
        initData();
    }

    private void initData() {
        clearAll();
        new Thread() {
            @Override
            public void run() {
                long memory = 0;
                while (memory < cacheMemory) {
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();;
                    }
                    Random random = new Random();
                    int i = random.nextInt();
                    i = random.nextInt(1024);
                    memory += 1024*i;
                    if (memory > cacheMemory) {
                        memory = cacheMemory;
                    }
                    Message msg = Message.obtain();
                    msg.what = CLEANNING;
                    msg.obj = memory;
                    handler.sendMessageDelayed(msg, 200);
                }
            }
        }.start();
    }

    private void initView() {
        findViewById(R.id.rl_titlebar).setBackgroundColor(
                getResources().getColor(R.color.rose_red));
        ((TextView) findViewById(R.id.tv_title)).setText("缓存清理");
        ImageView mLeftImgv = (ImageView) findViewById(R.id.imgv_leftbtn);
        mLeftImgv.setImageResource(R.drawable.back);
        mLeftImgv.setOnClickListener(this);
        animation = (AnimationDrawable)
                findViewById(R.id.imgv_trash_bin).getBackground();
        animation.setOneShot(false);
        animation.start();

        mMemoryTV = (TextView) findViewById(R.id.tv_cleancache_memory);
        mMemoryUnitTV = (TextView) findViewById(R.id.tv_cleancache_memoryunit);
        mCleanCacheFL = (FrameLayout) findViewById(R.id.fl_clean_cache);
        mFinishCleanFL = (FrameLayout) findViewById(R.id.fl_finish_clean);
        mSizeTV = (TextView) findViewById(R.id.tv_clean_memorysize);
        findViewById(R.id.btn_finish_cleancache).setOnClickListener(this);
    }

    private void formatMemory(long memory) {
        String cacheMemoryStr = Formatter.formatFileSize(this, memory);
        String memoryStr;
        String memoryUnit;
        if (memory > 900) {
            memoryStr = cacheMemoryStr.substring(0, cacheMemoryStr.length()-2);
            memoryUnit = cacheMemoryStr.substring(cacheMemoryStr.length()-2,
                    cacheMemoryStr.length());
        } else {
            memoryStr = cacheMemoryStr.substring(0, cacheMemoryStr.length()-1);
            memoryUnit = cacheMemoryStr.substring(cacheMemoryStr.length()-1,
                    cacheMemoryStr.length());
        }
        mMemoryTV.setText(memoryStr);
        mMemoryUnitTV.setText(memoryUnit);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgv_leftbtn:
                finish();
                break;
            case R.id.btn_finish_cleancache:
                finish();
                break;
        }
    }
    
    class ClearCacheObserver extends IPackageDataObserver.Stub {
        @Override
        public void onRemoveCompleted(String packageName, boolean succeeded)
                throws RemoteException { }
    }
    
    private void clearAll() {
        Method[] methods = PackageManager.class.getMethods();
        for (Method method : methods) {
            if ("freeStorageAndNotify".equals(method.getName())) {
                try {
                    method.invoke(packageManager, Long.MAX_VALUE, new ClearCacheObserver());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
        }
        Toast.makeText(this, "清理完毕", Toast.LENGTH_SHORT).show();
    }
}
