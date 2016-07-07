package com.soil.soilsample.support.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.soil.soilsample.R;

import java.util.List;

/**
 * Created by GIS on 2016/7/3 0003.
 */
public class FavoriteListAdapter extends BaseAdapter {
    private List<String> listData = null;
    private List<String> listTag = null;
    private Context mContext;
    private int resourceItemTagId;
    private int resourceItemId;
    public FavoriteListAdapter(Context context, int itemTagIdRes, int itemIdRes, List<String> dataTag, List<String> data) {
        this.mContext = context;
        this.listTag = dataTag;
        this.listData = data;
        this.resourceItemTagId = itemTagIdRes;
        this.resourceItemId = itemIdRes;
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean isEnabled(int position) {
        if (listTag.contains(listData.get(position)))
        {
            return false;
        }
        return super.isEnabled(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder viewHolder;

        if (convertView == null)
        {
            viewHolder = new ViewHolder();
            if (listTag.contains(listData.get(position)))
            {
                view = LayoutInflater.from(mContext).inflate(resourceItemTagId, null);
                viewHolder.itemText = (TextView) view.findViewById(R.id.favo_item_text);
            }else {
                view = LayoutInflater.from(mContext).inflate(resourceItemId, null);
                viewHolder.itemText = (TextView) view.findViewById(R.id.favo_item_text);
            }
            view.setTag(viewHolder);
        }else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.itemText.setText(listData.get(position));
        return view;
    }
    class ViewHolder
    {
        TextView itemText;
    }
}
