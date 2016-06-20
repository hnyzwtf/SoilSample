package com.soil.soilsample.support.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.soil.soilsample.R;


public class MyEnvironSpinnerAdapter extends BaseAdapter {
	private Context mContext;
	private String spinnerValues[];
	public MyEnvironSpinnerAdapter(Context context) {
		this.mContext = context;
		Resources resources = mContext.getResources();
		this.spinnerValues = resources.getStringArray(R.array.spinner_gower_value);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return spinnerValues.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return spinnerValues[position];
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (holder == null) {
			holder = new ViewHolder();
			if (convertView == null) {
				convertView = View.inflate(mContext, R.layout.environ_spinner_value, null);
			}
			holder.tvSpinnerVaule = (TextView) convertView.findViewById(R.id.tv_environspinner_value);
			convertView.setTag(holder);
		}
		holder = (ViewHolder) convertView.getTag();
		String value = spinnerValues[position];
		holder.tvSpinnerVaule.setText(value);
		return convertView;
	}
	public class ViewHolder
	{
		TextView tvSpinnerVaule;
	}

}
