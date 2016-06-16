package com.soil.soilsample.base;

import android.app.Application;
import android.content.Context;

/**
 * obtain the app's context.
 */
public class BaseApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
    public static Context getContext()
    {
        return context;
    }
}
