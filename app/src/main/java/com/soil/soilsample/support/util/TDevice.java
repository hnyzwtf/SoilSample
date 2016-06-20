package com.soil.soilsample.support.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.soil.soilsample.base.BaseApplication;

/**
 * Created by GIS on 2016/4/15 0015.
 */
public class TDevice {

    public static boolean hasInternet()
    {

        ConnectivityManager connectManager = (ConnectivityManager) BaseApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectManager != null)
        {
            NetworkInfo networkInfo = connectManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected())
            {
                if (networkInfo.getState() == NetworkInfo.State.CONNECTED)
                {
                    return true;
                }
            }
        }
        return false;
    }


}
