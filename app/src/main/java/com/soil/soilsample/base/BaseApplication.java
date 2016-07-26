package com.soil.soilsample.base;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import com.zhy.http.okhttp.OkHttpUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import im.fir.sdk.FIR;
import okhttp3.OkHttpClient;

/**
 * obtain the app's context.
 */
public class BaseApplication extends Application {
    private static Context context;
    private static String serverIP;
    private static int port;
    private static int connTimeOut;

    private static String appPath;
    private static String appUploadPath;

    @Override
    public void onCreate() {
        super.onCreate();
        FIR.init(this);

        serverIP = "222.192.7.122";
        //serverIP = "223.2.40.14";// 223.2.40.14
        port = 8181;
        connTimeOut = 15*1000;
        context = getApplicationContext();
        try {
            appPath = Environment.getExternalStorageDirectory().getCanonicalPath() + "/SoilSample";
            appUploadPath = appPath + "/upload";
        } catch (IOException e) {
            e.printStackTrace();
        }
        initMyOKHttpClient();
    }
    public static Context getContext()
    {
        return context;
    }
    public static String getServerIP()
    {
        return serverIP;
    }
    public static int getPort()
    {
        return port;
    }
    public static int getConnTimeOut()
    {
        return connTimeOut;
    }
    public static String getAppPath() {
        return appPath;
    }
    public static String getAppUploadPath() {
        return appUploadPath;
    }
    public void initMyOKHttpClient()
    {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                // other config
                .build();
        OkHttpUtils.initClient(okHttpClient);
    }
}
