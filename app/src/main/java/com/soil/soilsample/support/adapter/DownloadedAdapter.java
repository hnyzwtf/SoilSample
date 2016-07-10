package com.soil.soilsample.support.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.soil.soilsample.R;
import com.soil.soilsample.support.util.SizeFormatChange;
import com.soil.soilsample.ui.main.MainActivity;

import java.util.ArrayList;

/**
 * Created by GIS on 2016/7/9 0009.
 */
public class DownloadedAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<MKOLUpdateElement> downloadedMapList;
    private MKOfflineMap mOfflineMap;

    public DownloadedAdapter(Context context, ArrayList<MKOLUpdateElement> downloadedMapList, MKOfflineMap mOfflineMap)
    {
        this.mContext = context;
        this.downloadedMapList = downloadedMapList;
        this.mOfflineMap = mOfflineMap;
    }
    @Override
    public int getCount() {
        return downloadedMapList.size();
    }

    @Override
    public Object getItem(int position) {
        return downloadedMapList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final MKOLUpdateElement element = downloadedMapList.get(position);
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.offline_downloaded_item, null);
            holder = new ViewHolder();
            holder.downloadedCityName = (TextView) convertView
                    .findViewById(R.id.tv_downloaded_city_name);
            holder.downloadedStatus = (TextView) convertView
                    .findViewById(R.id.tv_downloaded_status);
            holder.downloadedCitySize = (TextView) convertView
                    .findViewById(R.id.tv_downloaded_city_size);
            holder.downloadedDelete = (Button) convertView
                    .findViewById(R.id.btn_downloaded_delete);
            holder.downloadedUpdate = (Button) convertView.findViewById(R.id.btn_downloaded_update);
            holder.downloadedViewMap = (Button) convertView
                    .findViewById(R.id.btn_downloaded_view_map);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (element.update)
        {
            holder.downloadedStatus.setText("离线地图有更新");
            holder.downloadedStatus.setTextColor(Color.BLUE);
        }else {
            holder.downloadedStatus.setText("已下载");
            holder.downloadedUpdate.setEnabled(false);
        }
        holder.downloadedCityName.setText(element.cityName);
        holder.downloadedCitySize.setText(SizeFormatChange.formatDataSize(element.serversize));
        holder.downloadedDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOfflineMap.remove(element.cityID);
                downloadedMapList.remove(position);
                notifyDataSetChanged();
            }
        });
        holder.downloadedViewMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("x", element.geoPt.longitude);
                intent.putExtra("y", element.geoPt.latitude);
                intent.setClass(mContext, MainActivity.class);
                mContext.startActivity(intent);
            }
        });
        return convertView;
    }
    class ViewHolder
    {
        TextView downloadedCityName;
        TextView downloadedCitySize;
        TextView downloadedStatus;
        Button downloadedViewMap;
        Button downloadedUpdate;
        Button downloadedDelete;
    }
}
