package com.soil.profile.utils;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class DrawLegend {
    public static int[] proLegengCode = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};

    private static Paint textpaint = new Paint();
    private static List<String> resultText = new ArrayList<String>();
    private static int marginLeft = 90;
    private static int lineGap = 50;

    public static List<String> modifyTextString(String text){
        List<String> result = new ArrayList<String>();
        int length = text.length();
        if (length>4) {
            marginLeft = 145;
        }
        String tmp = "";
        for (int i = 0; i < length; ) {
            if (i+5>length) {
                tmp = text.substring(i, length);
            }else {
                tmp = text.substring(i, i+5);
            }
            result.add(tmp);
            i += 5;
        }
        return result;
    }

    private static void setPaint(){
        textpaint.setTextSize(25);
        textpaint.setStrokeWidth(1);
        textpaint.setColor(Color.WHITE);
    }


    public static void drawProfileYuandian(Canvas canvas, Paint paint, Paint linePaint, float width,
                                           float height1, float height2, String text) {
        //小圆+小点
        int count = 0;
        int j;
        for(int i = (int) (height1);i<height2-30;){
            j = (count%2==0)?50:0;
            for(;j<width-marginLeft;){
                canvas.drawCircle(j+5, i+25, 5, paint);
                canvas.drawText("`", j+20, i+25, paint);
                j+=100;
            }
            count++;
            i+=40;
        }
        setPaint();
        resultText = modifyTextString(text);
        if (height2-height1>lineGap) {
            for (int i = 0; i < resultText.size(); i++) {
                canvas.drawText(resultText.get(i), width-marginLeft+15, (height1+height2)/2+i*30+20, textpaint);
            }
        }
        canvas.drawLine(0, height2, width, height2, linePaint);
    }

    public static void drawProfileXiaocha(Canvas canvas, Paint paint, Paint linePaint, float width,
                                          float height1, float height2, String text) {
        //小叉
        for(int i = (int) (height1 + 25); i<height2;){
            for(int j = 1;j<width-marginLeft;){
                canvas.drawText("×", j, i, paint);
                j += 70;
            }
            i += 60;
        }
        setPaint();
        resultText = modifyTextString(text);
        if (height2-height1>lineGap) {
            for (int i = 0; i < resultText.size(); i++) {
                canvas.drawText(resultText.get(i), width-marginLeft+15, (height1+height2)/2+i*30+20, textpaint);
            }
        }
        canvas.drawLine(0, height2, width, height2, linePaint);
    }

    public static void drawProfileSanzhexian(Canvas canvas, Paint paint, Paint linePaint, float width,
                                             float height1, float height2, String text) {
        //三折线
        for(int i = (int) (height1 + 25); i<height2-15;){
            for(int j = 15;j<width-marginLeft-60;){
                canvas.drawLine(j, i-10, j+15, i, paint);
                canvas.drawLine(j+15, i, j+45, i, paint);
                canvas.drawLine(j+45, i, j+60, i+10, paint);
                j += 90;
            }
            i += 60;
        }
        setPaint();
        resultText = modifyTextString(text);
        if (height2-height1>lineGap) {
            for (int i = 0; i < resultText.size(); i++) {
                canvas.drawText(resultText.get(i), width-marginLeft+15, (height1+height2)/2+i*30+20, textpaint);
            }
        }
        canvas.drawLine(0, height2, width, height2, linePaint);
    }

    public static void drawProfileXiaodengzi(Canvas canvas, Paint paint, Paint linePaint, float width,
                                             float height1, float height2, String text){
        //小凳子
        for(int i = (int) (height1+25); i<height2;){
            for(int j = 40;j<width-marginLeft-25;){
                canvas.drawLine(j-10, i-10, j-10, i, paint);
                canvas.drawLine(j-20, i, j+20, i, paint);
                canvas.drawLine(j+10, i, j+10, i+10, paint);
                j += 90;
            }
            i += 60;
        }
        setPaint();
        resultText = modifyTextString(text);
        if (height2-height1>lineGap) {
            for (int i = 0; i < resultText.size(); i++) {
                canvas.drawText(resultText.get(i), width-marginLeft+15, (height1+height2)/2+i*30+20, textpaint);
            }
        }
        canvas.drawLine(0, height2, width, height2, linePaint);
    }

    public static void drawProfileXiaoyuanhu(Canvas canvas, Paint paint, Paint linePaint, float width,
                                             float height1, float height2, String text) {
        //小圆弧
        RectF rect = new RectF(0, 0, 0, 0);
        for(int i = (int) (height1);i<height2-50;){
            for(int j = 1;j<width-marginLeft-35;){
                rect.set(j, i, j+50, i+50);
                canvas.drawArc(rect, 45, 90, false, paint);
                j += 100;
            }
            i += 50;
        }
        setPaint();
        resultText = modifyTextString(text);
        if (height2-height1>lineGap) {
            for (int i = 0; i < resultText.size(); i++) {
                canvas.drawText(resultText.get(i), width-marginLeft+15, (height1+height2)/2+i*30+20, textpaint);
            }
        }
        canvas.drawLine(0, height2, width, height2, linePaint);
    }

    public static void drawProfileJiantou(Canvas canvas, Paint paint, Paint linePaint, float width,
                                          float height1, float height2, String text) {
        //向下箭头
        Path path = new Path(); //定义一条路径
        float len = (height2 - height1 - 60)/4;
        for (int i = (int) height1; i < height2; ) {
            for(int j = 40;j<width-marginLeft;){
                canvas.drawLine(j, i, j, i+len, paint);
                path.moveTo(j-10, i+len-10); //移动到 坐标
                path.lineTo(j, i+len);
                path.lineTo(j+10, i+len-10);
                canvas.drawPath(path, paint);
                path.reset();
                j += 50;
            }
            i += len+20;
        }
        setPaint();
        resultText = modifyTextString(text);
        if (height2-height1>lineGap) {
            for (int i = 0; i < resultText.size(); i++) {
                canvas.drawText(resultText.get(i), width-marginLeft+15, (height1+height2)/2+i*30+20, textpaint);
            }
        }
        canvas.drawLine(0, height2, width, height2, linePaint);
    }

    public static void drawProfileZhuankuai(Canvas canvas, Paint paint, Paint linePaint, float width,
                                            float height1, float height2, String text){
        //砖块
        int count = 0;
        int j;
        for(int i = (int) (height1);i<height2-20;){
            canvas.drawLine(0,i, width-marginLeft, i, paint);
            j = (count%2==0)?50:0;
            for(;j<width-marginLeft;){
                canvas.drawLine(j, i, j, i+20, paint);
                j+=100;
            }
            count++;
            i+=40;
        }
        setPaint();
        resultText = modifyTextString(text);
        if (height2-height1>lineGap) {
            for (int i = 0; i < resultText.size(); i++) {
                canvas.drawText(resultText.get(i), width-marginLeft+15, (height1+height2)/2+i*30+20, textpaint);
            }
        }
        canvas.drawLine(0, height2, width, height2, linePaint);
    }

    public static void drawProfileXiexian(Canvas canvas, Paint paint, Paint linePaint, float width,
                                          float height1, float height2, String text){
        //斜线
        int gap = (int) ((width-marginLeft)/10);
        for(int i = 0;i<=width-marginLeft-gap;){
            canvas.drawLine(i+gap, height1, i, height2, paint);
            i += gap;
        }
        setPaint();
        resultText = modifyTextString(text);
        if (height2-height1>gap) {
            for (int i = 0; i < resultText.size(); i++) {
                canvas.drawText(resultText.get(i), width-marginLeft+15, (height1+height2)/2+i*30+20, textpaint);
            }
        }
        canvas.drawLine(0, height2, width, height2, linePaint);
    }

    public static void drawProfileHengxian(Canvas canvas, Paint paint, Paint linePaint, float width,
                                           float height1, float height2, String text){
        //横线
        for (int i = (int) height1; i < height2; ) {
            canvas.drawLine(0f, i, width-marginLeft, i, paint);
            i += 30;
        }
        setPaint();
        resultText = modifyTextString(text);
        if (height2-height1>lineGap) {
            for (int i = 0; i < resultText.size(); i++) {
                canvas.drawText(resultText.get(i), width-marginLeft+15, (height1+height2)/2+i*30+20, textpaint);
            }
        }
        canvas.drawLine(0, height2, width, height2, linePaint);
    }

    public static void drawProfileShuxian(Canvas canvas, Paint paint, Paint linePaint, float width,
                                          float height1, float height2, String text) {
        //竖线
        int n = 0;
        int k;
        for(int i = (int) (height1);i<height2-40;){
            k = (n%2==0)?50:0;
            for(;k<width-marginLeft;){
                canvas.drawLine(k, i, k, i+40, paint);
                k+=100;
            }
            n++;
            i+=40;
        }
        setPaint();
        resultText = modifyTextString(text);
        if (height2-height1>lineGap) {
            for (int i = 0; i < resultText.size(); i++) {
                canvas.drawText(resultText.get(i), width-marginLeft+15, (height1+height2)/2+i*30+20, textpaint);
            }
        }
        canvas.drawLine(0, height2, width, height2, linePaint);
    }

    public static void drawProfileXiaocao(Canvas canvas, Paint paint, Paint linePaint, float width,
                                          float height1, float height2, String text){
        //小草
        for(int i = (int) (height1);i<height2-40;){
            for(int j = 40;j<width-marginLeft-50;){
                canvas.drawLine(j, i, j+20, i+30, paint);
                canvas.drawLine(j+50, i, j+30, i+30, paint);
                j+=100;
            }
            i+=40;
        }
        setPaint();
        resultText = modifyTextString(text);
        if (height2-height1>lineGap) {
            for (int i = 0; i < resultText.size(); i++) {
                canvas.drawText(resultText.get(i), width-marginLeft+15, (height1+height2)/2+i*30+20, textpaint);
            }
        }
        canvas.drawLine(0, height2, width, height2, linePaint);
    }

    public static void drawProfileKongbai(Canvas canvas, Paint paint, Paint linePaint, float width,
                                          float height1, float height2, String text) {
        //空白
        setPaint();
        resultText = modifyTextString(text);
        if (height2-height1>lineGap) {
            for (int i = 0; i < resultText.size(); i++) {
                canvas.drawText(resultText.get(i), width-marginLeft+15, (height1+height2)/2+i*30+20, textpaint);
            }
        }
        canvas.drawLine(0, height2, width, height2, linePaint);
    }

    public static void drawSpilitLine(Canvas canvas, Paint paint, float width, float height){
        canvas.drawLine(width-marginLeft, 0, width-marginLeft, height, paint);
    }

    public static void drawDepth(Canvas canvas, Paint paint, Paint linePaint, float width,
                                 float height1, float height2, float depth) {
        setPaint();
        DecimalFormat df = new DecimalFormat("0.0");
        if (height2-height1>lineGap) {
            canvas.drawText(df.format(depth)+"cm", width-marginLeft+7, (height1+height2)/2-10, textpaint);
        }else {
            canvas.drawText(df.format(depth)+"cm", width-marginLeft+7, (height1+height2)/2, textpaint);
        }
    }

    public static void drawCustomChoose(int code, Canvas canvas, Paint paint, Paint linePaint,
                                        float width, float height1, float height2, String text) {
        switch (code) {
            case 1:
                drawProfileYuandian(canvas,paint, linePaint, width, height1, height2, text);
                break;
            case 2:
                drawProfileXiaocha(canvas, paint, linePaint, width, height1, height2, text);
                break;
            case 3:
                drawProfileSanzhexian(canvas, paint, linePaint, width, height1, height2, text);
                break;
            case 4:
                drawProfileShuxian(canvas, paint, linePaint, width, height1, height2, text);
                break;
            case 5:
                drawProfileXiaodengzi(canvas, paint, linePaint, width, height1, height2, text);
                break;
            case 6:
                drawProfileXiaoyuanhu(canvas, paint, linePaint, width, height1, height2, text);
                break;
            case 7:
                drawProfileXiexian(canvas, paint, linePaint, width, height1, height2, text);
                break;
            case 8:
                drawProfileXiaocao(canvas, paint, linePaint, width, height1, height2, text);
                break;
            case 9:
                drawProfileJiantou(canvas, paint, linePaint, width, height1, height2, text);
                break;
            case 10:
                drawProfileHengxian(canvas, paint, linePaint, width, height1, height2, text);
                break;
            case 11:
                drawProfileZhuankuai(canvas, paint, linePaint, width, height1, height2, text);
                break;
            case 12:
                drawProfileKongbai(canvas, paint, linePaint, width, height1, height2, text);
                break;
        }
    }
}

