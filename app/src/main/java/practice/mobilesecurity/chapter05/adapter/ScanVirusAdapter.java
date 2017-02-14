package practice.mobilesecurity.chapter05.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import practice.mobilesecurity.R;
import practice.mobilesecurity.chapter05.entity.ScanAppInfo;

public class ScanVirusAdapter extends BaseAdapter {
    private List<ScanAppInfo> mScanAppInfos;
    private Context context;

    public ScanVirusAdapter(List<ScanAppInfo> mScanAppInfos, Context context) {
        this.mScanAppInfos = mScanAppInfos;
        this.context = context;
    }

    @Override
    public int getCount() {
        return mScanAppInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return mScanAppInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(context,
                    R.layout.item_list_applock, null);
            holder = new ViewHolder();
            holder.mAppIconImgv = (ImageView)
                    convertView.findViewById(R.id.imgv_appicon);
            holder.mAppNameTV = (TextView)
                    convertView.findViewById(R.id.tv_appname);
            holder.mScanIconImgv = (ImageView)
                    convertView.findViewById(R.id.imgv_lock);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ScanAppInfo scanAppInfo = mScanAppInfos.get(position);
        if (!scanAppInfo.isVirus) {
            holder.mScanIconImgv.setBackgroundResource(
                    R.drawable.blue_right_icon);
            holder.mAppNameTV.setTextColor(context.getResources().
                    getColor(R.color.black));
            holder.mAppNameTV.setText(scanAppInfo.appName);
        } else {
            holder.mAppNameTV.setTextColor(context.getResources().
                    getColor(R.color.bright_red));
            holder.mAppNameTV.setText(scanAppInfo.appName +
                    "(" + scanAppInfo.description + ")");
        }
        holder.mAppIconImgv.setImageDrawable(scanAppInfo.appIcon);
        return convertView;
    }

    static class ViewHolder {
        ImageView mAppIconImgv;
        TextView mAppNameTV;
        ImageView mScanIconImgv;
    }
}
