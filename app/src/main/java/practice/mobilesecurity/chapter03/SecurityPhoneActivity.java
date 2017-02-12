package practice.mobilesecurity.chapter03;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import practice.mobilesecurity.R;
import practice.mobilesecurity.chapter03.adapter.BlackContactAdapter;
import practice.mobilesecurity.chapter03.db.dao.BlackNumberDao;
import practice.mobilesecurity.chapter03.entity.BlackContactInfo;

/**
 * 显示黑名单信息界面
 */
public class SecurityPhoneActivity extends Activity
        implements OnClickListener {
    private FrameLayout mHaveBlackNumber;
    private FrameLayout mNoBlackNumber;
    private BlackNumberDao dao;
    private ListView mListView;
    private int pageNumber = 0;
    private int pageSize = 15;
    private int totalNumber;
    private List<BlackContactInfo> pageBlackNumber = new ArrayList<>();
    private BlackContactAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_securityphone);
        initView();
        fillData();
    }

    private void fillData() {
        dao = new BlackNumberDao(SecurityPhoneActivity.this);
        totalNumber = dao.getTotalNumber();
        if (totalNumber == 0) {
            mHaveBlackNumber.setVisibility(View.GONE);
            mNoBlackNumber.setVisibility(View.VISIBLE);
        } else if (totalNumber > 0) {
            mHaveBlackNumber.setVisibility(View.VISIBLE);
            mNoBlackNumber.setVisibility(View.GONE);
            pageNumber = 0;
            if (pageBlackNumber.size() > 0) {
                pageBlackNumber.clear();
            }
            pageBlackNumber.addAll(dao.getPageBlackNumber(
                    pageNumber, pageSize));

            if (adapter == null) {
                adapter = new BlackContactAdapter(pageBlackNumber,
                        SecurityPhoneActivity.this);
                adapter.setCallback(new BlackContactAdapter.BlackContactCallback() {
                    @Override
                    public void DataSizeChanged() {
                        fillData();
                    }
                });
                mListView.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void initView() {
        findViewById(R.id.rl_titlebar).setBackgroundColor(getResources().
                getColor(R.color.bright_purple));
        ImageView mLefteImgv = (ImageView) findViewById(R.id.imgv_leftbtn);
        ((TextView) findViewById(R.id.tv_title)).setText("通讯卫士");
        mLefteImgv.setOnClickListener(this);
        mLefteImgv.setImageResource(R.drawable.back);
        mHaveBlackNumber = (FrameLayout)
                findViewById(R.id.fl_have_black_number);
        mNoBlackNumber = (FrameLayout) findViewById(R.id.fl_no_black_number);
        findViewById(R.id.btn_add_black_number).setOnClickListener(this);
        mListView = (ListView) findViewById(R.id.lv_black_numbers);
        mListView.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case OnScrollListener.SCROLL_STATE_IDLE:
                        int lastVisiblePosition =
                                mListView.getLastVisiblePosition();
                        if (lastVisiblePosition == pageBlackNumber.size()-1) {
                            pageNumber++;
                            if (pageNumber * pageSize >= totalNumber) {
                                Toast.makeText(SecurityPhoneActivity.this,
                                        "没有更多数据了",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                pageBlackNumber.addAll(dao.getPageBlackNumber(
                                        pageNumber, pageSize));
                                adapter.notifyDataSetChanged();
                            }
                        }
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (totalNumber != dao.getTotalNumber()) {
            if (dao.getTotalNumber() > 0) {
                mHaveBlackNumber.setVisibility(View.VISIBLE);
                mNoBlackNumber.setVisibility(View.GONE);
            } else {
                mHaveBlackNumber.setVisibility(View.GONE);
                mNoBlackNumber.setVisibility(View.VISIBLE);
            }

            pageNumber = 0;
            pageBlackNumber.clear();
            pageBlackNumber.addAll(dao.getPageBlackNumber(
                    pageNumber, pageSize));
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgv_leftbtn:
                finish();
                break;
            case R.id.btn_add_black_number:
                startActivity(new Intent(this, AddBlackNumberActivity.class));
                break;
        }
    }
}
