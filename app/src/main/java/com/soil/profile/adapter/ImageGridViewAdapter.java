package com.soil.profile.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.soil.profile.utils.BitmapUtils;

import java.util.ArrayList;
import java.util.List;

public class ImageGridViewAdapter extends BaseAdapter{

    //	private int[] bitmaps;
    private List<String> paths;
    private Context context;

    public ImageGridViewAdapter(Context context, List<String> bitmaps) {
        this.paths = new ArrayList<String>();
        this.paths = bitmaps;
        this.context = context;
    }
    @Override
    public int getCount() {
        return paths.size();
    }

    @Override
    public Object getItem(int position) {
        return paths.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO 自动生成的方法存根
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(LayoutParams.MATCH_PARENT, 300));//设置ImageView对象布局
            imageView.setAdjustViewBounds(false);//设置边界对齐
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);//设置刻度的类型
            imageView.setPadding(8, 8, 8, 8);//设置间距
        }
        else {
            imageView = (ImageView) convertView;
        }
//        imageView.setImageResource(bitmaps[position]);//为ImageView设置图片资源
        imageView.setImageBitmap(BitmapUtils.decodeSampledBitmapFromFile(paths.get(position), imageView.getWidth(),300));
        return imageView;
    }

}

