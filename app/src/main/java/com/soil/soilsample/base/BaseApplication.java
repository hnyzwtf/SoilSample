package com.soil.soilsample.base;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import java.io.IOException;

import im.fir.sdk.FIR;

/**
 * obtain the app's context.
 */
public class BaseApplication extends Application {
    private static Context context;
    private static String serverIP;
    private static int port;
    private static int connTimeOut;

    private static String appPath;

    @Override
    public void onCreate() {
        serverIP = "222.192.7.122";
        //serverIP = "223.2.40.14";// 223.2.40.14
        port = 8181;
        connTimeOut = 15*1000;
        super.onCreate();
        FIR.init(this);
        context = getApplicationContext();
        try {
            appPath = Environment.getExternalStorageDirectory().getCanonicalPath() + "/SoilSample";
        } catch (IOException e) {
            e.printStackTrace();
        }
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
}
