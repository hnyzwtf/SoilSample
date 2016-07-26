package com.soil.soilsample.support.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by GIS on 2016/6/7 0007.
 */
public class ToastUtil {
    private static Toast toast;
    public static void show(Context context, String info) {
        if (toast == null)
        {

            toast = Toast.makeText(context, info, Toast.LENGTH_SHORT);
        }else {
            toast.setText(info);
        }
        toast.show();
    }
    public static void showLongTime(Context context, String info) {
        if (toast == null)
        {

            toast = Toast.makeText(context, info, Toast.LENGTH_LONG);
        }else {
            toast.setText(info);
        }
        toast.show();
    }

    public static void show(Context context, int info) {
        Toast.makeText(context, info, Toast.LENGTH_SHORT).show();
    }
}
