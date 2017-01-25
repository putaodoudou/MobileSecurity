package ptactice.mobilesecurity.chapter01.utils;


import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.io.File;

public class DownloadUtils {
    /**
     * 下载APK的方法
     * @param url
     * @param targetFile
     * @param myCallBack
     */
    public void downloadApk(String url, String targetFile,
                        final MyCallBack myCallBack) {
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.download(url, targetFile, new RequestCallBack<File>() {
            @Override
            public void onSuccess(ResponseInfo<File> responseInfo) {
                myCallBack.onSuccess(responseInfo);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                myCallBack.onFailure(e, s);
            }

            @Override
            public void onLoading(long total, long current,
                                  boolean isUploading) {
                super.onLoading(total, current, isUploading);
                myCallBack.onLoading(total, current, isUploading);
            }
        });
    }
}

interface MyCallBack {
    void onSuccess(ResponseInfo<File> arg0);

    void onFailure(HttpException arg0, String arg1);

    void onLoading(long total, long current, boolean isUploading);
}
