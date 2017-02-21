package practice.mobilesecurity.chapter07;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import practice.mobilesecurity.R;
import practice.mobilesecurity.chapter07.adapter.ProcessManagerAdapter;
import practice.mobilesecurity.chapter07.entity.TaskInfo;
import practice.mobilesecurity.chapter07.utils.SystemInfoUtils;
import practice.mobilesecurity.chapter07.utils.TaskInfoParser;

public class ProcessManagerActivity extends Activity implements View.OnClickListener{
    private TextView mRunProcessNum;
    private TextView mMemoryTV;
    private TextView mProcessNumTV;
    private ListView mListView;
    ProcessManagerAdapter adapter;
    private List<TaskInfo> runningTaskInfos;
    private List<TaskInfo> userTaskInfos = new ArrayList<>();
    private List<TaskInfo> systemTaskInfos = new ArrayList<>();
    private ActivityManager manager;
    private int runningProcessCount;
    private long totalMen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_processmanager);
        initView();
        fillData();
    }

    private void initView() {
        findViewById(R.id.rl_titlebar).setBackgroundColor(getResources().getColor(R.color.bright_green));
        ImageView mLeftImgv = (ImageView) findViewById(R.id.imgv_leftbtn);
        mLeftImgv.setOnClickListener(this);
        mLeftImgv.setImageResource(R.drawable.back);
        ImageView mRightImgv = (ImageView) findViewById(R.id.imgv_rightbtn);
        mRightImgv.setOnClickListener(this);
        mRightImgv.setImageResource(R.drawable.processmanager_setting_icon);
        ((TextView) findViewById(R.id.tv_title)).setText("进程管理");
        mRunProcessNum = (TextView) findViewById(R.id.tv_runningprocess_num);
        mMemoryTV = (TextView) findViewById(R.id.tv_memory_processmanager);
        mProcessNumTV = (TextView) findViewById(R.id.tv_user_runningprocess);
        runningProcessCount = SystemInfoUtils.getRunningProcessCount(ProcessManagerActivity.this);
        mRunProcessNum.setText("运行中的进程运行中的进程" + runningProcessCount + "个");

        long totalAvailMem= SystemInfoUtils.getAvailMen(this);
        totalMen = SystemInfoUtils.getTotalMem();
        mMemoryTV.setText("可用/总内存" + Formatter.formatFileSize(this, totalAvailMem) + "/" + Formatter.formatFileSize(this,totalMen));
        mListView = (ListView) findViewById(R.id.lv_runningapps);
        initListener();
    }

    private void initListener() {
        findViewById(R.id.btn_select_all).setOnClickListener(this);
        findViewById(R.id.btn_select_inverse).setOnClickListener(this);
        findViewById(R.id.btn_clean_process).setOnClickListener(this);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object object = mListView.getItemAtPosition(position);
                if (object != null) {
                    TaskInfo taskInfo = (TaskInfo) object;
                    if (taskInfo.packageName.equals(getPackageName())) {
                        return;
                    }
                    taskInfo.isChecked = !taskInfo.isChecked;
                    adapter.notifyDataSetChanged();
                }
            }
        });
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem >= userTaskInfos.size() + 1) {
                    mProcessNumTV.setText("系统进程: " + systemTaskInfos.size() + "个");
                } else {
                    mProcessNumTV.setText("用户进程: " + userTaskInfos.size() + "个");
                }
            }
        });
    }

    private void fillData() {
        userTaskInfos.clear();
        systemTaskInfos.clear();
        new Thread() {
            @Override
            public void run() {
                runningTaskInfos = TaskInfoParser.getRunningTaskInfos(getApplicationContext());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (TaskInfo taskInfo : runningTaskInfos) {
                            Log.i("taskInfo.appName", taskInfo.appName);
                            if (taskInfo.isUserApp) {
                                userTaskInfos.add(taskInfo);
                            } else {
                                systemTaskInfos.add(taskInfo);
                            }
                        }
                        if (adapter == null) {
                            adapter = new ProcessManagerAdapter(getApplicationContext(), userTaskInfos, systemTaskInfos);
                            mListView.setAdapter(adapter);
                        } else {
                            adapter.notifyDataSetChanged();
                        }

                        if (userTaskInfos.size() > 0) {
                            mProcessNumTV.setText("用户进程:" + userTaskInfos.size() + "个");
                        } else {
                            mProcessNumTV.setText("系统进程:" + systemTaskInfos.size() + "个");
                        }
                    }
                });
            }
        }.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgv_leftbtn:
                finish();
                break;
            case R.id.imgv_rightbtn:
                startActivity(new Intent(this, ProcessManagerSettingActivity.class));
                break;
            case R.id.btn_select_all:
                selectAll();
                break;
            case R.id.btn_select_inverse:
                inverse();
                break;
            case R.id.btn_clean_process:
                cleanProcess();
                break;
        }
    }

    private void cleanProcess() {
        manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        int count = 0;
        long saveMemory = 0;

        List<TaskInfo> killedTaskInfos = new ArrayList<>();
        for (TaskInfo info : userTaskInfos) {
            if (info.isChecked) {
                count++;
                saveMemory += info.appMemory;
                manager.killBackgroundProcesses(info.packageName);
                killedTaskInfos.add(info);
            }
        }
        for (TaskInfo info : systemTaskInfos) {
            if (info.isChecked) {
                count++;
                saveMemory += info.appMemory;
                manager.killBackgroundProcesses(info.packageName);
                killedTaskInfos.add(info);
            }
        }
        for (TaskInfo info : killedTaskInfos) {
            if (info.isUserApp) {
                userTaskInfos.remove(info);
            } else {
                systemTaskInfos.remove(info);
            }
        }
        runningProcessCount -= count;
        mRunProcessNum.setText("运行中的进程:" + runningProcessCount + "个");
        mMemoryTV.setText("可用/总内存:" + Formatter.formatFileSize(this, SystemInfoUtils.getAvailMen(this))+ "/" + Formatter.formatFileSize(this, totalMen));
        Toast.makeText(this, "清理了"+count+"个进程,释放了"+Formatter.formatFileSize(this, saveMemory) + "内存", Toast.LENGTH_SHORT).show();
        mProcessNumTV.setText("用户进程:" + userTaskInfos.size()+"个");
        adapter.notifyDataSetChanged();
    }

    private void inverse() {
        for (TaskInfo taskInfo : userTaskInfos) {
            if (taskInfo.packageName.equals(getPackageName())) {
                continue;
            }
            taskInfo.isChecked = !taskInfo.isChecked;
        }
        for (TaskInfo taskInfo : systemTaskInfos) {
            taskInfo.isChecked = !taskInfo.isChecked;
        }
        adapter.notifyDataSetChanged();
    }

    private void selectAll() {
        for (TaskInfo taskInfo : userTaskInfos) {
            if (taskInfo.packageName.equals(getPackageName())) {
                continue;
            }
            taskInfo.isChecked = true;
        }

        for (TaskInfo taskInfo : systemTaskInfos) {
            taskInfo.isChecked = true;
        }
        adapter.notifyDataSetChanged();
    }
}
