package com.soil.soilsample.ui.sampleinfo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.soil.soilsample.R;
import com.soil.soilsample.base.BaseActivity;

/*
 * 可以任意选择设置marker的图标
 * */
public class SamplePicSelectActivity extends BaseActivity {
	private Toolbar toolbar;
	private GridView samplePicSelect;
	private static final String TAG = "SamplePicSelect";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sample_image_select);
		initView();
		initEvents();
	}
	
	private void initView() {
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_white_24dp);
		getSupportActionBar().setTitle("选择样点图标");
		samplePicSelect = (GridView) findViewById(R.id.grid_picSelect);
	}
	private void initEvents() {
		samplePicSelect.setAdapter(new ImageAdapter(this));//gridview适配器设置 
		samplePicSelect.setOnItemClickListener(new OnItemClickListener() {//设置gridview中元素的点击事件

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				
				Intent intentToSampleInfo = new Intent();
				intentToSampleInfo.putExtra("selectedPic", mImagesIds[position]);
				setResult(RESULT_OK, intentToSampleInfo);//向上一个活动返回数据
				finish();
			}
		});
		
	}
	//定义适配器
	private class ImageAdapter extends BaseAdapter
	{
		private Context mContext;
		public ImageAdapter(Context context)
		{
			this.mContext = context;
		}
		@Override
		public int getCount() {
			return mImagesIds.length;
		}

		@Override
		public Object getItem(int position) {			
			return position;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//定义一个ImageView,显示在GridView里
			ImageView mImageView;
			if (convertView == null) {
				mImageView = new ImageView(mContext);
				// 设置View的height和width：这样保证每个图像将重新适合这个指定的尺寸
				mImageView.setLayoutParams(new GridView.LayoutParams(90, 90));
				/* ImageView.ScaleType.CENTER 但不执行缩放比例
                 * ImageView.ScaleType.CENTER_CROP 按比例统一缩放图片（保持图片的尺寸比例）便于图片的两维（宽度和高度）等于或大于相应的视图维度
                 * ImageView.ScaleType.CENTER_INSIDE 按比例统一缩放图片（保持图片的尺寸比例）便于图片的两维（宽度和高度）等于或小于相应的视图维度 */
				mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
				mImageView.setPadding(6, 6, 6, 6);
			}else {
				mImageView = (ImageView) convertView;
			}
			mImageView.setImageResource(mImagesIds[position]);
			return mImageView;
		}
		
	}
	//在gridview中展示的图片，即数据源
	//第一个图标是默认的图标，他的id是2130837601
	private Integer[] mImagesIds = {R.drawable.default_marker, R.drawable.marker1, R.drawable.marker2, 
			R.drawable.marker3, R.drawable.marker4, R.drawable.marker5, R.drawable.marker6, R.drawable.marker7, 
			R.drawable.marker8, R.drawable.marker9, R.drawable.marker10, R.drawable.marker11, R.drawable.marker12,
			R.drawable.marker13, R.drawable.marker14, R.drawable.marker15, R.drawable.marker16};
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id)
		{
			case android.R.id.home:
				finish();
				break;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}


}
