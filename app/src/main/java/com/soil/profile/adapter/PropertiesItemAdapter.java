package com.soil.profile.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.soil.profile.model.DatabaseMdl.InfoAttrProper;
import com.soil.profile.ui.ActivityAttributeProperties;
import com.soil.soilsample.R;

import java.util.List;

public class PropertiesItemAdapter extends ArrayAdapter<InfoAttrProper>{

    private int resourceId;
    private int position;

    public int getPosition() {
        return position;
    }

    public PropertiesItemAdapter(Context context, int textViewResourceId,
                                 List<InfoAttrProper> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        this.position = position;
        //先获得对应的单项的数据
        final InfoAttrProper proper = getItem(position);
        //初始化单项的布局
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.edit = (TextView) view.findViewById(R.id.proper_layer_edit);
            viewHolder.layer = (TextView) view.findViewById(R.id.proper_layer);
            viewHolder.depth = (TextView) view.findViewById(R.id.proper_depth);
            viewHolder.color_dry = (TextView) view.findViewById(R.id.proper_color_dry);
            viewHolder.color_wet = (TextView) view.findViewById(R.id.proper_color_wet);
            viewHolder.compactness = (TextView) view.findViewById(R.id.proper_compactness);
            viewHolder.humidity = (TextView) view.findViewById(R.id.proper_humidity);
            viewHolder.intrusion = (TextView) view.findViewById(R.id.proper_intrusion);
            viewHolder.meas_limy_react = (TextView) view.findViewById(R.id.proper_meas_limy_react);
            viewHolder.meas_PH = (TextView) view.findViewById(R.id.proper_meas_PH);
            viewHolder.newGrowth_class = (TextView) view.findViewById(R.id.proper_newgrowth_class);
            viewHolder.newGrowth_morphology = (TextView) view.findViewById(R.id.proper_newgrowth_morphology);
            viewHolder.newGrowth_number = (TextView) view.findViewById(R.id.proper_newgrowth_number);
            viewHolder.porosity = (TextView) view.findViewById(R.id.proper_porosity);
            viewHolder.rootSys = (TextView) view.findViewById(R.id.proper_rootsys);
            viewHolder.structure = (TextView) view.findViewById(R.id.proper_structure);
            viewHolder.texture = (TextView) view.findViewById(R.id.proper_texture);
            view.setTag(viewHolder);//将ViewHolder存储在view中
        }else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();//重新获取ViewHolder
        }
        //将数据与布局连接起来
        viewHolder.color_dry.setText(proper.getColor_dry());
        viewHolder.color_wet.setText(proper.getColor_wet());
        viewHolder.compactness.setText(proper.getCompactness());
        viewHolder.depth.setText(proper.getDepth()+" m");
        viewHolder.humidity.setText(proper.getHumidity());
        viewHolder.intrusion.setText(proper.getIntrusion());
        viewHolder.layer.setText(proper.getLayer());
        viewHolder.meas_limy_react.setText(proper.getMeasure_limy_reaction());
        viewHolder.meas_PH.setText(proper.getMeasure_PH());
        viewHolder.newGrowth_class.setText(proper.getNewGrowth_class());
        viewHolder.newGrowth_morphology.setText(proper.getNewGrowth_morphology());
        viewHolder.newGrowth_number.setText(proper.getNewGrowth_number());
        viewHolder.porosity.setText(proper.getPorosity());
        viewHolder.rootSys.setText(proper.getRootSys());
        viewHolder.structure.setText(proper.getStructure());
        viewHolder.texture.setText(proper.getTexture());

        viewHolder.edit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ActivityAttributeProperties.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("proper", proper);
                intent.putExtras(bundle);
                intent.putExtra("tag", "adapter");
                //一定要将getContext强转为Activity，否则不能使用startActivityForResult，
                //然后在adapter所在的Activity中写onActivityResult的逻辑
                ((Activity) getContext()).startActivityForResult(intent, 2);
            }
        });

        return view;
    }

    class ViewHolder{
        TextView edit;
        TextView layer;
        TextView depth;
        TextView color_dry;
        TextView color_wet;//颜色润
        TextView humidity; //干湿度
        TextView texture; //质地
        TextView structure; //结构
        TextView compactness; //松紧度
        TextView porosity;   //孔隙度
        TextView newGrowth_class;   //新生体类别
        TextView newGrowth_morphology; //新生体形态
        TextView newGrowth_number;  //新生体数量
        TextView intrusion;  //侵入体
        TextView rootSys; //根系
        TextView meas_PH;
        TextView meas_limy_react; //石灰反应
    }
}

