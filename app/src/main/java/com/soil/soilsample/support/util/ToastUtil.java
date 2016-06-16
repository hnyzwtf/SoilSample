package com.soil.soilsample.support.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by GIS on 2016/6/7 0007.
 */
public class ToastUtil {
    public static void show(Context context, String info) {
        Toast.makeText(context, info, Toast.LENGTH_SHORT).show();
    }

    public static void show(Context context, int info) {
        Toast.makeText(context, info, Toast.LENGTH_SHORT).show();
    }
}
