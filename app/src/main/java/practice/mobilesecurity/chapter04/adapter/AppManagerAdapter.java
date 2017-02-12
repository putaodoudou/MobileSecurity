package practice.mobilesecurity.chapter04.adapter;

import android.content.Context;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import practice.mobilesecurity.R;
import practice.mobilesecurity.chapter04.entity.AppInfo;
import practice.mobilesecurity.chapter04.utils.DensityUtil;
import practice.mobilesecurity.chapter04.utils.EngineUtils;


public class AppManagerAdapter extends BaseAdapter {
    private List<AppInfo> UserAppInfos;
    private List<AppInfo> SystemAppInfos;
    private Context context;
    public AppManagerAdapter(List<AppInfo> userAppInfos,
                             List<AppInfo> systemAppInfos, Context context) {
        UserAppInfos = userAppInfos;
        SystemAppInfos = systemAppInfos;
        this.context = context;
    }

    @Override
    public int getCount() {
        return UserAppInfos.size() + SystemAppInfos.size() + 2;
    }

    @Override
    public Object getItem(int position) {
        if (0 == position) {
            // 显示用户程序个数的标签
            return null;
        } else if (position == (UserAppInfos.size() + 1)) {
            return null;
        }
        AppInfo appInfo;
        if (position < (UserAppInfos.size() + 1)) {
            appInfo = UserAppInfos.get(position - 1);
        } else {
            int location = position - UserAppInfos.size() - 2;
            appInfo = SystemAppInfos.get(location);
        }
        return appInfo;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 如果position为0，则为TextView
        if (0 == position) {
            TextView tv = getTextView();
            tv.setText("用户程序:" + UserAppInfos.size() + "个");
            return tv;
        } else if ((UserAppInfos.size() + 1) == position) {
            TextView tv = getTextView();
            tv.setText("系统程序:" + SystemAppInfos.size() + "个");
            return tv;
        }

        AppInfo appInfo;
        if (position < (UserAppInfos.size() + 1)) {
            appInfo = UserAppInfos.get(position-1);
        } else {
            appInfo = SystemAppInfos.get(position - UserAppInfos.size() - 2);
        }
        ViewHolder viewHolder = null;
        if (convertView != null && convertView instanceof LinearLayout) {
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            viewHolder = new ViewHolder();
            convertView = View.inflate(context,
                    R.layout.item_appmanager_list, null);
            viewHolder.mAppIconImgv = (ImageView) convertView.
                    findViewById(R.id.imgv_appicon);
            viewHolder.mAppLocationTV = (TextView) convertView.
                    findViewById(R.id.tv_appisroom);
            viewHolder.mAppSizeTV = (TextView) convertView.
                    findViewById(R.id.tv_appsize);
            viewHolder.mAppNameTV = (TextView) convertView.
                    findViewById(R.id.tv_appname);
            viewHolder.mLaunchAppTV = (TextView) convertView.
                    findViewById(R.id.tv_launch_app);
            viewHolder.mSettingAppTV = (TextView) convertView.
                    findViewById(R.id.tv_setting_app);
            viewHolder.mShareAppTV = (TextView) convertView.
                    findViewById(R.id.tv_share_app);
            viewHolder.mUninstallAppTV = (TextView) convertView.
                    findViewById(R.id.tv_uninstall_app);
            viewHolder.mApppOptionLL = (LinearLayout) convertView.
                    findViewById(R.id.ll_option_app);
            convertView.setTag(viewHolder);
        }
        if (null != appInfo) {
            viewHolder.mAppLocationTV.setText(appInfo.
                    getAppLocation(appInfo.isInRoom));
            viewHolder.mAppIconImgv.setImageDrawable(appInfo.icon);
            viewHolder.mAppSizeTV.setText(Formatter.formatFileSize(context,
                    appInfo.appSize));
            viewHolder.mAppNameTV.setText(appInfo.appName);
            if (appInfo.isSelected) {
                viewHolder.mApppOptionLL.setVisibility(View.VISIBLE);
            } else {
                viewHolder.mApppOptionLL.setVisibility(View.GONE);
            }
        }
        MyClickListener listener = new MyClickListener(appInfo);
        viewHolder.mLaunchAppTV.setOnClickListener(listener);
        viewHolder.mSettingAppTV.setOnClickListener(listener);
        viewHolder.mShareAppTV.setOnClickListener(listener);
        viewHolder.mUninstallAppTV.setOnClickListener(listener);
        return convertView;
    }

    private TextView getTextView() {
        TextView tv = new TextView(context);
        tv.setBackgroundColor(context.getResources().getColor(R.color.graye5));
        int padding = DensityUtil.dip2px(context, 5);
        tv.setPadding(padding, padding, padding, padding);
        tv.setTextColor(context.getResources().getColor(R.color.black));
        return tv;
    }

    static class ViewHolder {
        // launch app
        TextView mLaunchAppTV;
        // uninstall app
        TextView mUninstallAppTV;
        // share app
        TextView mShareAppTV;
        // set app
        TextView mSettingAppTV;
        // app icon
        ImageView mAppIconImgv;
        // app location
        TextView mAppLocationTV;
        // app size
        TextView mAppSizeTV;
        // app name
        TextView mAppNameTV;
        // linear layout control app
        LinearLayout mApppOptionLL;

    }

    class MyClickListener implements View.OnClickListener {
        private AppInfo appInfo;
        public MyClickListener(AppInfo appInfo) {
            super();
            this.appInfo = appInfo;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_launch_app:
                    EngineUtils.startApplication(context, appInfo);
                    break;
                case R.id.tv_share_app:
                    EngineUtils.shareApplication(context, appInfo);
                    break;
                case R.id.tv_setting_app:
                    EngineUtils.SettingAppDetail(context, appInfo);
                    break;
                case R.id.tv_uninstall_app:
                    if (appInfo.packageName.equals(context.getPackageName())) {
                        Toast.makeText(context, "你没有权限卸载此应用",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    EngineUtils.uninstallApplication(context, appInfo);
                    break;
            }
        }
    }
}
