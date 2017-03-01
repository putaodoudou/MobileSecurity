package practice.mobilesecurity.chapter09.fragment;
import java.util.ArrayList;
import java.util.List;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import practice.mobilesecurity.R;
import practice.mobilesecurity.chapter09.adapter.AppLockAdapter;
import practice.mobilesecurity.chapter09.db.dao.AppLockDao;
import practice.mobilesecurity.chapter09.entity.AppInfo;
import practice.mobilesecurity.chapter09.utils.AppInfoParser;


public class AppLockFragment extends Fragment{

    private TextView mLockTV;
    private ListView mLockLV;
    private AppLockDao dao;
    List<AppInfo> mLockApps = new ArrayList<AppInfo>();
    private AppLockAdapter adapter;
    private Uri uri = Uri.parse("content://com.practice.mobilesecurity.applock");
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 10:
                    mLockApps.clear();
                    mLockApps.addAll((List<AppInfo>)msg.obj);
                    if(adapter == null){
                        adapter = new AppLockAdapter(mLockApps, getActivity());
                        mLockLV.setAdapter(adapter);
                    }else{
                        adapter.notifyDataSetChanged();
                    }
                    mLockTV.setText("加锁应用"+mLockApps.size()+"个");
                    break;
            }
        };
    };
    private List<AppInfo> appInfos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_applock, null);
        mLockTV = (TextView) view.findViewById(R.id.tv_lock);
        mLockLV = (ListView) view.findViewById(R.id.lv_lock);
        return view;
    }

    @Override
    public void onResume(){
        dao = new AppLockDao(getActivity());
        appInfos = AppInfoParser.getAppInfos(getActivity());

        fillData();
        initListener();
        getActivity().getContentResolver().registerContentObserver(uri, true, new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                fillData();
            }
        });
        super.onResume();
    }

    private void fillData() {
        final List<AppInfo> aInfos = new ArrayList<AppInfo>();
        new Thread(){
            public void run() {
                for (AppInfo appInfo : appInfos) {
                    if(dao.find(appInfo.packageName)){
                        appInfo.isLock = true;
                        aInfos.add(appInfo);
                    }
                }
                Message msg = new Message();
                msg.obj = aInfos;
                msg.what = 10;
                mHandler.sendMessage(msg);
            };
        }.start();
    }

    private void initListener() {
        mLockLV.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {
                TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -1.0f,
                        Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
                ta.setDuration(300);
                view.startAnimation(ta);
                new Thread(){
                    public void run() {
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dao.delete(mLockApps.get(position).packageName);
                                mLockApps.remove(position);
                                adapter.notifyDataSetChanged();
                            }
                        });
                    };
                }.start();
            }
        });
    }
}