package com.soil.soilsample.support.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.soil.soilsample.R;
import com.soil.soilsample.model.OfflineCityRecord;
import com.soil.soilsample.support.util.SizeFormatChange;

import java.util.ArrayList;

/**
 * Created by GIS on 2016/7/8 0008.
 */
public class HotCityListViewAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<OfflineCityRecord> datas = null;
    public HotCityListViewAdapter(Context context, ArrayList<OfflineCityRecord> datas)
    {
        this.mContext = context;
        this.datas = datas;
    }
    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return datas.get(position).getCityId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int dataSize = datas.get(position).getCitySize();
        String cityName = datas.get(position).getCityName();
        String downloadStatus = datas.get(position).getDownloadStatus();
        ViewHolder holder = null;
        if (convertView == null)
        {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.offline_hotcity_item, null);
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
        if (downloadStatus == null)
        {
            holder.downloadState.setText("");
        }else {

            holder.downloadState.setText(downloadStatus);
        }
        return convertView;
    }
    class ViewHolder
    {
        TextView cityName;
        TextView dataSize;
        TextView downloadState;
    }
}
