package com.soil.soilsample.support.adapter;

import android.content.Context;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.soil.soilsample.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
* 环境因子选择listview的适配器
* */
public class MyEnvironListViewAdapter extends BaseAdapter {

	public static SparseBooleanArray isSelected;//使用SparseArray来代替HashMap<Integer, Boolean>
	public static Map<String, Integer> itemSpinnerVaule;//存储每一个listview的item对应的spinner值，如：地形因子，terrain?Gower
	private LayoutInflater mInflater;//用来导入布局
	private int mListviewItemResource;//布局文件
	private List<String> mList;//一个填充数据的list，内容是listview中每一项的环境因子名称
	private Context mContext;
	public static int spinnerValueChanged = 0;//static，会保留上次的数值
	private String TAG = "EnvironListView";

	public MyEnvironListViewAdapter(Context context, List<String> list, int resource) {
		if (list == null) {
			Log.d(TAG, "环境因子列表数值没有获取到");
		}
		this.mContext = context;
		this.mList = list;
		this.mListviewItemResource = resource;				
		mInflater = LayoutInflater.from(context);
		isSelected = new SparseBooleanArray();
		itemSpinnerVaule = new HashMap<String, Integer>();
		initCheckBox();
		initItemSpinnerVaules();
	}
	/**
	 * 初始化Map对象itemSpinnerVaule中的值
	 */
	private void initItemSpinnerVaules() {
		for (String str : mList) {
			itemSpinnerVaule.put(str, 0);
		}
		
	}
	/**
	 * 初始化所有的checkbox为未选中状态
	 */
	private void initCheckBox() {				
		for (int i = 0; i < mList.size(); i++) {
			getIsSelected().put(i, false);
		}
		
	}

	@Override
	public int getCount() {

		return mList.size();
	}

	@Override
	public Object getItem(int position) {

		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (holder == null) {
			holder = new ViewHolder();
			if (convertView == null) {
				convertView = mInflater.inflate(mListviewItemResource, null);
			}
			holder.tv = (TextView) convertView.findViewById(R.id.tv_environitem);
			holder.spin = (Spinner) convertView.findViewById(R.id.spin_environitem);
			holder.cb = (CheckBox) convertView.findViewById(R.id.cb_environitem);
			//设置spinner的外观
			//String[] mSpinnerItems = mContext.getResources().getStringArray(R.array.spinner_gower_value);
			//ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, mSpinnerItems);
			//	arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			//	holder.spin.setAdapter(arrayAdapter);
			//设置spinner的适配器
			MyEnvironSpinnerAdapter spinnerAdapter = new MyEnvironSpinnerAdapter(mContext);

			holder.spin.setAdapter(spinnerAdapter);
			//给spinner设置自定义的item点击事件
			holder.spin.setOnItemSelectedListener(new SpinnerItemSelectedListener(holder.spin));
			convertView.setTag(holder);
		}
		holder = (ViewHolder) convertView.getTag();
		// 设置list中TextView的显示
		holder.tv.setText(mList.get(position));
		// 根据isSelected来设置checkbox的选中状况
		holder.cb.setChecked(getIsSelected().get(position));
		//设置spinner的setPrompt
		holder.spin.setPrompt(mList.get(position));
		int defaultSpinnerPosition = itemSpinnerVaule.get(mList.get(position));
		holder.spin.setSelection(defaultSpinnerPosition);//设置listview初始化时每个spinner显示第一项的值
		
		return convertView;
	}
	public class ViewHolder
	{
		public TextView tv = null;
		public Spinner spin = null;
		public CheckBox cb = null;
	}
	public static Map<String, Integer> getSpinnerValue()
	{
		return itemSpinnerVaule;
	}
	public static SparseBooleanArray getIsSelected()
	{
		return isSelected;
	}
	public static void setSpinnerValue(Map<String, Integer> itemSpinnerValue)
	{
		MyEnvironListViewAdapter.itemSpinnerVaule = itemSpinnerValue;
	}
	public static void setIsSelected(SparseBooleanArray isSelected)
	{
		MyEnvironListViewAdapter.isSelected = isSelected;
	}
	/**
	 * 点击spinner下拉菜单中某个选项的响应事件
	 *
	 */
	public class SpinnerItemSelectedListener implements OnItemSelectedListener
	{
		Spinner mSpinner;
		public SpinnerItemSelectedListener(Spinner spinner) {
			this.mSpinner = spinner;
		}

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
								   int position, long id) {
			// 当选择spinner的某一个具体值时，将这个值和spinner所在的listview的item值绑定在一起
			itemSpinnerVaule.put(mSpinner.getPrompt().toString(), position);
			//String str = (String) mSpinner.getSelectedItem();
			//此listener是在getView中调用的，只要listview一初始化，就会调用getView，即调用此listener，那么
			//spinnerValueChanged的数值就是listview中item的个数或个数的整数倍，因为spinnerValueChanged是static的，每次都保留
			//上次的数值
			spinnerValueChanged++;

		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub
			
		}
		
	}

}
