package com.soil.profile.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.widget.ImageView;

public class BaseProfileModel extends ImageView {

    //protected：可以被子类访问
    protected Paint paint;
    protected Paint linePaint;
    protected Bitmap bitmap;   //用于存储模板
    protected Canvas canvasBitmap;

    protected int width, height;

    protected float[] fmlLines;
    protected boolean[] isDraw;

    protected int flag;
    protected int flagClick;

    protected static int HEIGHT = 950;

    public BaseProfileModel(Context context, float[] lines){
        super(context);
        fmlLines = new float[lines.length];
        isDraw = new boolean[lines.length-1];
        for (int i = 0; i < lines.length; i++) {
            fmlLines[i] = lines[i]*HEIGHT;
        }
        for (int i = 0; i < lines.length-1; i++) {
            isDraw[i] = true;
        }
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);//设置空心
        paint.setStrokeWidth(3);
        paint.setTextSize(30);

        linePaint = new Paint();
        linePaint.setColor(Color.WHITE);
        linePaint.setStyle(Paint.Style.STROKE);//设置空心
        linePaint.setStrokeWidth(6);
    }

    public Bitmap getProModelBitmap() {
        if (bitmap != null) {
            return bitmap;
        }else {
            return null;
        }
    }

    protected int marginLeft(String[] texts) {
        int marginLeft = 120;
        for (int i = 0; i < texts.length; i++) {
            if (texts[i].length()>7) {
                marginLeft = 145;
            }
        }
        return marginLeft;
    }

    int num =  0;
    public void deleteProfile(int n) {
        isDraw[n-1+num] = false;
        if (num<isDraw.length-1) {
            num++;
        }
        for(int i = fmlLines.length-1; i>=n;i--){
            fmlLines[i] = fmlLines[i-1];
        }
        invalidate();
    }

    public String getLayerName(int code){
        String name = "";
        switch (code) {
            case 10:
                name = "枯枝落叶层(O)";
                break;
            case 20:
                name = "腐殖质层(A)";
                break;
            case 201:
                name = "AE过渡层(AE)";
                break;
            case 202:
                name = "AB过渡层(AB)";
                break;
            case 21:
                name = "腐殖质层1(A1)";
                break;
            case 22:
                name = "腐殖质层2(A2)";
                break;
            case 30:
                name = "淋溶层(E)";
                break;
            case 301:
                name = "EB过渡层(EB)";
                break;
            case 40:
                name = "沉淀层(B)";
                break;
            case 401:
                name = "BC过渡层(BC)";
                break;
            case 50:
                name = "母质质层(C)";
                break;
            case 51:
                name = "母质质层1(C1)";
                break;
            case 52:
                name = "母质质层2(C2)";
                break;
            case 60:
                name = "母岩层(R)";
                break;
        }
        return name;
    }

}

