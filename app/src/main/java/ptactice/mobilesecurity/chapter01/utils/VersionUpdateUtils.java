package ptactice.mobilesecurity.chapter01.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog.Builder;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;

import java.io.File;
import java.io.IOException;

import ptactice.mobilesecurity.HomeActivity;
import ptactice.mobilesecurity.R;
import ptactice.mobilesecurity.chapter01.entity.VersionEntity;

public class VersionUpdateUtils {
    private static final int MESSAGE_NET_EEOR = 101;
    private static final int MESSAGE_IO_EEOR = 102;
    private static final int MESSAGE_JSON_EEOR = 103;
    private static final int MESSAGE_SHOEW_DIALOG = 104;
    private static final int MESSAGE_ENTERHOME = 105;

    // update UI
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MESSAGE_IO_EEOR:
                    Toast.makeText(context, "IO Exception", 
                            Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case MESSAGE_JSON_EEOR:
                    Toast.makeText(context, "Error JSON Analysis",
                            Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case MESSAGE_NET_EEOR:
                    Toast.makeText(context, "Error Net Link",
                            Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case MESSAGE_SHOEW_DIALOG:
                    showUpdateDialog(versionEntity);
                    break;
                case MESSAGE_ENTERHOME:
                    Intent intent = new Intent(context, HomeActivity.class);
                    context.startActivity(intent);
                    context.finish();
                    break;
            }
        }
    };
    
    // local version
    private String mVersion;
    private Activity context;
    private ProgressDialog mProgresssDialog;
    private VersionEntity versionEntity;

    public VersionUpdateUtils(String veriosn, Activity activity) {
        this.mVersion = veriosn;
        this.context = activity;
    }

    // get version in server
    public void getServerVersion() {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(
                    client.getParams(), 5000);
            HttpConnectionParams.setSoTimeout(client.getParams(), 5000);
            HttpGet httpGet = new HttpGet(
                    "http://172.16.25.14:8080/updateinfo.html");
            HttpResponse excute = client.execute(httpGet);
            if (200 == excute.getStatusLine().getStatusCode()) {
                HttpEntity entity = excute.getEntity();
                String result = EntityUtils.toString(entity, "gbk");

                JSONObject jsonObject = new JSONObject(result);
                versionEntity = new VersionEntity();
                String des = jsonObject.getString("des");
                versionEntity.description = des;
                String apkUrl = jsonObject.getString("apkUrl");
                versionEntity.apkUrl = apkUrl;
                if (!mVersion.equals(versionEntity.versionCode)) {
                    handler.sendEmptyMessage(MESSAGE_SHOEW_DIALOG);
                }
            }
        } catch (ClientProtocolException e) {
            handler.sendEmptyMessage(MESSAGE_NET_EEOR);
            e.printStackTrace();
        } catch (IOException e) {
            handler.sendEmptyMessage(MESSAGE_IO_EEOR);
            e.printStackTrace();
        } catch (JSONException e) {
            handler.sendEmptyMessage(MESSAGE_JSON_EEOR);
            e.printStackTrace();
        }
    }

    /**
     * pop up update dialog
     * @param versionEntity
     */
    private void showUpdateDialog(final VersionEntity versionEntity) {
        // create dialog
        AlertDialog.Builder builder = new Builder(context);
        builder.setTitle("检查到新版本:" + versionEntity.versionCode);
        builder.setMessage(versionEntity.description);
        builder.setCancelable(false);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setPositiveButton("立即升级",
                new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        initProgressDialog();
                        downloadNewApk(versionEntity.apkUrl);
                    }
                });
        builder.setNegativeButton("暂不升级",
                new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        enterHome();
                    }
                });
        builder.show();
    }


    private void initProgressDialog() {
        mProgresssDialog = new ProgressDialog(context);
        mProgresssDialog.setMessage("准备下载");
        mProgresssDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgresssDialog.show();
    }

    protected void downloadNewApk(String apkUrl) {
        DownloadUtils downLoadUtils = new DownloadUtils();
        downLoadUtils.downloadApk(apkUrl, "mnt/sdcard/mobilesafe2.0.apk",
                new MyCallBack() {
                    @Override
                    public void onSuccess(ResponseInfo<File> arg0) {
                        mProgresssDialog.dismiss();
                        MyUtils.installApk(context);
                    }

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        mProgresssDialog.setMessage("下载失败");
                        mProgresssDialog.dismiss();
                        enterHome();
                    }

                    @Override
                    public void onLoading(long total, long current,
                                          boolean isUploading) {
                        mProgresssDialog.setMax((int)total);
                        mProgresssDialog.setMessage("正在下载...");
                        mProgresssDialog.setProgress((int)current);
                    }
                });
    }

    private void enterHome() {
        handler.sendEmptyMessageDelayed(MESSAGE_ENTERHOME, 2000);
    }
}
