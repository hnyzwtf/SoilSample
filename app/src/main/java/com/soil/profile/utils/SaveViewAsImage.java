package com.soil.profile.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import java.io.FileOutputStream;

/**
 * Created by GIS on 2016/6/28 0028.
 */
public class SaveViewAsImage {
    public static Bitmap loadBitmapFromView(View v) {
        if (v == null) {
            return null;
        }
        Bitmap screenshot;
        screenshot = Bitmap.createBitmap(v.getMeasuredWidth(), v.getMeasuredHeight(), Config.RGB_565);
        Canvas c = new Canvas(screenshot);
        v.draw(c);
        return screenshot;
    }

    public static boolean saveAsImg(View view) {
        try {
            FileOutputStream fos = new FileOutputStream(UrlAndFile.getSaveFilePath());
            Bitmap bitmap = loadBitmapFromView(view);
            boolean b = bitmap.compress(CompressFormat.PNG, 100, fos);
            bitmap.recycle();
            fos.close();
            return b;
        } catch (Exception e) {
            return false;
        }
    }
}
