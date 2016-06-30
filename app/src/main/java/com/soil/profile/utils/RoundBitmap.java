package com.soil.profile.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

public class RoundBitmap {
//	 public static Bitmap toRoundBitmap(Bitmap bitmap) {
//	        //圆形图片宽高
//	        int width = bitmap.getWidth();
//	        int height = bitmap.getHeight();
//	        //正方形的边长
//	        int r = 0;
//	        //取最短边做边长
//	        if(width > height) {
//	            r = height;
//	        } else {
//	            r = width;
//	        }
//	        //构建一个bitmap
//	        Bitmap backgroundBmp = Bitmap.createBitmap(width,
//	                 height, Config.ARGB_8888);
//	        //new一个Canvas，在backgroundBmp上画图
//	        Canvas canvas = new Canvas(backgroundBmp);
//	        Paint paint = new Paint();
//	        //设置边缘光滑，去掉锯齿
//	        paint.setAntiAlias(true);
//	        //宽高相等，即正方形
//	        RectF rect = new RectF(0, 0, r, r);
//	        //通过制定的rect画一个圆角矩形，当圆角X轴方向的半径等于Y轴方向的半径时，
//	        //且都等于r/2时，画出来的圆角矩形就是圆形
//	        canvas.drawRoundRect(rect, r/2, r/2, paint);
//	        //设置当两个图形相交时的模式，SRC_IN为取SRC图形相交的部分，多余的将被去掉
//	        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
//	        //canvas将bitmap画在backgroundBmp上
//	        canvas.drawBitmap(bitmap, null, rect, paint);
//	        //返回已经绘画好的backgroundBmp
//	        return backgroundBmp;
//	    }

    /**
     * 转换图片成圆形
     * @param bitmap 传入Bitmap对象
     * @return
     */
    public static Bitmap toRoundBitmap(Bitmap bitmap)
    {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left,top,right,bottom,dst_left,dst_top,dst_right,dst_bottom;
        if (width <= height) {
            roundPx = width / 2 -5;
            top = 0;
            bottom = width;
            left = 0;
            right = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            roundPx = height / 2 -5;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }

        Bitmap output = Bitmap.createBitmap(width,
                height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect src = new Rect((int)left, (int)top, (int)right, (int)bottom);
        final Rect dst = new Rect((int)dst_left, (int)dst_top, (int)dst_right, (int)dst_bottom);
        final RectF rectF = new RectF(dst_left+15, dst_top+15, dst_right-20, dst_bottom-20);

        paint.setAntiAlias(true);

        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);

        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, src, dst, paint);
        return output;
    }
}

