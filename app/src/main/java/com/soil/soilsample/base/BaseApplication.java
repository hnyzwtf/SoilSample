package com.soil.soilsample.base;

import android.app.Application;
import android.content.Context;

/**
 * obtain the app's context.
 */
public class BaseApplication extends Application {
    private static Context context;
    private static String serverIP;
    private static int port;
    private static int connTimeOut;

    @Override
    public void onCreate() {
        serverIP = "222.192.7.122";
        port = 8181;
        connTimeOut = 15*1000;
        super.onCreate();
        context = getApplicationContext();
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
}
