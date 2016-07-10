package com.soil.soilsample.support.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.soil.soilsample.R;
import com.soil.soilsample.support.util.SizeFormatChange;
import com.soil.soilsample.support.util.ToastUtil;

import java.util.ArrayList;

/**
 * Created by GIS on 2016/7/9 0009.
 */
public class DownloadingAdapter extends BaseAdapter{
    private Context mContext;
    private ArrayList<MKOLUpdateElement> downloadingMapList;
    private ArrayList<MKOLUpdateElement> isDoingUpdateMapList;
    private MKOfflineMap mOfflineMap;
    private DownloadedAdapter downloadedAdapter;

    public DownloadingAdapter(Context context, ArrayList<MKOLUpdateElement> downloadingMapList, ArrayList<MKOLUpdateElement> isDoingUpdateMapList,
                              MKOfflineMap mOfflineMap, DownloadedAdapter downloadedAdapter)
    {
        this.mContext = context;
        this.downloadingMapList = downloadingMapList;
        this.isDoingUpdateMapList = isDoingUpdateMapList;
        this.mOfflineMap = mOfflineMap;
        this.downloadedAdapter = downloadedAdapter;
    }
    @Override
    public int getCount() {
        return downloadingMapList.size();
    }

    @Override
    public Object getItem(int position) {
        return downloadingMapList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final MKOLUpdateElement element = downloadingMapList.get(position);
        ViewHolder holder = null;
        if (convertView == null)
        {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.offline_downloading_item, null);
            holder = new ViewHolder();
            holder.downloadingCityName = (TextView) convertView.findViewById(R.id.tv_downloading_city_name);
            holder.downloadingStatus = (TextView) convertView.findViewById(R.id.tv_downloading_status);
            holder.downloadingCitySize = (TextView) convertView.findViewById(R.id.tv_downloading_city_size);
            holder.downloadingProgress = (ProgressBar) convertView.findViewById(R.id.progress_downloading_city);
            holder.downloadingBtnLayout = (LinearLayout) convertView.findViewById(R.id.rl_downloaded_item_btn);
            holder.beginBtn = (Button) convertView.findViewById(R.id.btn_begin_download);
            holder.pauseBtn = (Button) convertView.findViewById(R.id.btn_pause_download);
            holder.deleteBtn = (Button) convertView.findViewById(R.id.btn_delete_download);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.downloadingCityName.setText(element.cityName);
        holder.downloadingCitySize.setText(SizeFormatChange.formatDataSize(element.serversize));
        String stateInfo = "";
        if (element.status == MKOLUpdateElement.DOWNLOADING)
        {
            stateInfo = "正在下载";
            holder.beginBtn.setEnabled(false);
            holder.pauseBtn.setEnabled(true);
            holder.downloadingStatus.setText(stateInfo + element.ratio + "%");
            holder.downloadingStatus.setTextColor(Color.BLUE);
        }
        if (element.status == MKOLUpdateElement.SUSPENDED)
        {
            stateInfo = "已暂停";
            holder.beginBtn.setEnabled(true);
            holder.pauseBtn.setEnabled(false);
            holder.downloadingStatus.setText(stateInfo + element.ratio + "%");
            holder.downloadingStatus.setTextColor(Color.RED);
        }
        holder.downloadingProgress.setProgress(element.ratio);
        // 暂停下载按钮的点击事件
        holder.pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOfflineMap.pause(element.cityID);
                ToastUtil.show(mContext, "暂停下载:" + element.cityName);
                isDoingUpdateMapList = mOfflineMap.getAllUpdateInfo();
                if (isDoingUpdateMapList == null)
                {
                    isDoingUpdateMapList = new ArrayList<MKOLUpdateElement>();
                }
                if (isDoingUpdateMapList.size() > 0)
                {
                    downloadingMapList.clear();
                    for (MKOLUpdateElement updateElement: isDoingUpdateMapList)
                    {
                        if (updateElement.ratio != 100)
                            downloadingMapList.add(updateElement);
                    }
                }
                notifyDataSetChanged();
            }
        });
        holder.beginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOfflineMap.start(element.cityID);
                ToastUtil.show(mContext, "开始下载:" + element.cityName);
                isDoingUpdateMapList = mOfflineMap.getAllUpdateInfo();
                if (isDoingUpdateMapList == null)
                {
                    isDoingUpdateMapList = new ArrayList<MKOLUpdateElement>();
                }
                if (isDoingUpdateMapList.size() > 0)
                {
                    downloadingMapList.clear();
                    for (MKOLUpdateElement updateElement: isDoingUpdateMapList)
                    {
                        if (updateElement.ratio != 100)
                            downloadingMapList.add(updateElement);
                    }
                }
                notifyDataSetChanged();
                downloadedAdapter.notifyDataSetChanged();
            }
        });
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOfflineMap.remove(element.cityID);
                ToastUtil.show(mContext, "删除地图：" + element.cityName);
                downloadingMapList.remove(position);
                notifyDataSetChanged();
            }
        });
        return convertView;
    }
    class ViewHolder
    {
        TextView downloadingCityName;
        TextView downloadingStatus;
        TextView downloadingCitySize;
        ProgressBar downloadingProgress;
        LinearLayout downloadingBtnLayout;
        Button beginBtn;
        Button pauseBtn;
        Button deleteBtn;
    }
}
