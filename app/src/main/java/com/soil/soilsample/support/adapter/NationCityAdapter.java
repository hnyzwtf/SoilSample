package com.soil.soilsample.support.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.soil.soilsample.R;
import com.soil.soilsample.model.OfflineCityRecord;
import com.soil.soilsample.support.util.SizeFormatChange;

import java.util.ArrayList;

/**
 * Created by GIS on 2016/7/7 0007.
 */
public class NationCityAdapter extends BaseExpandableListAdapter {
    private Context mContext;
    private ArrayList<OfflineCityRecord> allCityDatas;
    public NationCityAdapter(Context context, ArrayList<OfflineCityRecord> allCityDatas)
    {
        this.mContext = context;
        this.allCityDatas = allCityDatas;
    }
    @Override
    public int getGroupCount() {
        return allCityDatas.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return allCityDatas.get(groupPosition).getChildCities().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return allCityDatas.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return allCityDatas.get(groupPosition).getChildCities().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String provinceName = allCityDatas.get(groupPosition).getCityName();
        ViewHolder holder = null;
        if (convertView == null)
        {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.offline_map_group_item, null);
            holder = new ViewHolder();
            holder.provinceName = (TextView) convertView.findViewById(R.id.tv_province_name);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.provinceName.setText(provinceName);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String cityName = allCityDatas.get(groupPosition).getChildCities().get(childPosition).getCityName();
        String downloadState = allCityDatas.get(groupPosition).getChildCities().get(childPosition).getDownloadStatus();
        int dataSize = allCityDatas.get(groupPosition).getChildCities().get(childPosition).getCitySize();
        ViewHolder holder = null;
        if (convertView == null)
        {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.offline_map_item, null);
            holder = new ViewHolder();
            holder.cityName = (TextView) convertView.findViewById(R.id.tv_cityname);
            holder.dataSize = (TextView) convertView.findViewById(R.id.tv_data_size);
            holder.downloadState = (TextView) convertView.findViewById(R.id.tv_download_state);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.cityName.setText(cityName);
        holder.dataSize.setText(SizeFormatChange.formatDataSize(dataSize));
        if (downloadState == null)
        {
            holder.downloadState.setText("");
        }else {
            holder.downloadState.setText(downloadState);
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
    class ViewHolder
    {
        TextView provinceName;
        TextView cityName;
        TextView dataSize;
        TextView downloadState;
    }
}
