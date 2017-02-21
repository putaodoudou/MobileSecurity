package practice.mobilesecurity.chapter07.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import practice.mobilesecurity.R;
import practice.mobilesecurity.chapter04.utils.DensityUtil;
import practice.mobilesecurity.chapter07.entity.TaskInfo;

public class ProcessManagerAdapter extends BaseAdapter {
    private Context context;
    private List<TaskInfo> mUserTaskInfos;
    private List<TaskInfo> mSysTaskInfos;
    private SharedPreferences mSP;
    public ProcessManagerAdapter(Context context, List<TaskInfo> userTaskInfos, List<TaskInfo> sysTaskInfos) {
        super();
        this.context = context;
        this.mUserTaskInfos = userTaskInfos;
        this.mSysTaskInfos = sysTaskInfos;
        mSP = context.getSharedPreferences("config", Context.MODE_PRIVATE);
    }
    @Override
    public int getCount() {
        if (mSysTaskInfos.size() > 0 & mSP.getBoolean("showSystemProcess", true)) {
            return mUserTaskInfos.size() + mSysTaskInfos.size() + 2;
        } else {
            return mUserTaskInfos.size() + 1;
        }
    }

    @Override
    public Object getItem(int position) {
        if (position == 0 || position == mUserTaskInfos.size() + 1) {
            return null;
        } else if (position <= mUserTaskInfos.size()) {
            return mUserTaskInfos.get(position-1);
        } else {
            return mSysTaskInfos.get(position - mUserTaskInfos.size() - 2);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (position == 0) {
            TextView textView = getTextView();
            textView.setText("用户进程:" + mUserTaskInfos.size() + "个");
            return textView;
        } else if (position == mUserTaskInfos.size() + 1) {
            TextView textView = getTextView();
            if (mSysTaskInfos.size() > 0) {
                textView.setText("系统进程:" + mSysTaskInfos.size()+"个");
                return textView;
            }
        }
        TaskInfo taskInfo = null;
        if (position <= mUserTaskInfos.size()) {
            taskInfo = mUserTaskInfos.get(position-1);
        } else if (mSysTaskInfos.size() > 0) {
            taskInfo = mSysTaskInfos.get(position - mUserTaskInfos.size() - 2);
        }
        ViewHolder holder = null;
        if (convertView != null && convertView instanceof RelativeLayout) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = View.inflate(context, R.layout.item_processmanager, null);
            holder = new ViewHolder();
            holder.mAppIconImgv = (ImageView) convertView.findViewById(R.id.imgv_appicon_processmana);
            holder.mAppMemoryTV = (TextView) convertView.findViewById(R.id.tv_appmemory_processmana);
            holder.mAppNameTV = (TextView) convertView.findViewById(R.id.tv_appname_processmana);
            holder.mCheckBox = (CheckBox) convertView.findViewById(R.id.checkbox);
            convertView.setTag(holder);
        }
        if (taskInfo != null) {
            holder.mAppNameTV.setText(taskInfo.appName);
            holder.mAppMemoryTV.setText("内存占用:" + Formatter.formatFileSize(context, taskInfo.appMemory));
            holder.mAppIconImgv.setImageDrawable(taskInfo.appIcon);
            if (taskInfo.packageName.equals(context.getPackageName())) {
                holder.mCheckBox.setVisibility(View.GONE);
            } else {
                holder.mCheckBox.setVisibility(View.VISIBLE);
            }
            holder.mCheckBox.setChecked(taskInfo.isChecked);
         }
        return convertView;
    }

    private TextView getTextView() {
        TextView textView = new TextView(context);
        textView.setBackgroundColor(context.getResources().getColor(R.color.graye5));
        textView.setPadding(DensityUtil.dip2px(context, 5), DensityUtil.dip2px(context, 5),
                DensityUtil.dip2px(context, 5),DensityUtil.dip2px(context, 5));
        textView.setTextColor(context.getResources().getColor(R.color.black));
        return textView;
    }

    static class ViewHolder {
        ImageView mAppIconImgv;
        TextView mAppNameTV;
        TextView mAppMemoryTV;
        CheckBox mCheckBox;
    }
}
