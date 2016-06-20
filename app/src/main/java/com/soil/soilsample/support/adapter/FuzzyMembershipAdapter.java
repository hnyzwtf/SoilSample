package com.soil.soilsample.support.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.soil.soilsample.ui.function.ChoiceView;

import java.util.List;

/**
 * 模糊隶属度图层选择的适配器，即带有单选功能的listview
 */
public class FuzzyMembershipAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> mData;
    private String TAG = "FuzzyMembershipAdapter";
    public FuzzyMembershipAdapter(Context context, List<String> data)
    {
        if (data == null) {
            Log.d(TAG, "模糊隶属度列表没有获取到");
        }
        this.mContext = context;
        this.mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ChoiceView view;
        if (convertView == null)
        {
            view = new ChoiceView(mContext);
        }else
        {
            view = (ChoiceView) convertView;
        }
        view.setText((String) getItem(position));
        return view;
    }
}
