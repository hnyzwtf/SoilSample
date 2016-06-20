package com.soil.soilsample.ui.function;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.soil.soilsample.R;
import com.soil.soilsample.base.BaseActivity;


public class FunctionActivity extends BaseActivity implements OnClickListener{

	private Toolbar toolbar;
	private LinearLayout environmentSelect;//环境因子选择布局
	private LinearLayout membershipSelect;
	private LinearLayout fcmCluster;//fcm聚类布局


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.function);
		
		initView();
		initEvents();		
	}
	private void initView() {

		environmentSelect = (LinearLayout) findViewById(R.id.rl_environment);
		membershipSelect = (LinearLayout) findViewById(R.id.rl_fuzzy_membership_map);
		fcmCluster = (LinearLayout) findViewById(R.id.rl_fcm);
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_white_24dp);
		getSupportActionBar().setTitle("功能");
	}
	
	private void initEvents() {
		environmentSelect.setOnClickListener(this);
		membershipSelect.setOnClickListener(this);
		fcmCluster.setOnClickListener(this);
	}

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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_environment:
			Intent intentToEnvironVariableActivity = new Intent(FunctionActivity.this, EnvironVariableActivity.class);
			startActivity(intentToEnvironVariableActivity);
			break;
		case R.id.rl_fuzzy_membership_map:
			Intent intentToFuzzyMemberActivity = new Intent(FunctionActivity.this, FuzzyMembershipActivity.class);
			startActivity(intentToFuzzyMemberActivity);
			break;
		case R.id.rl_fcm:
			Intent intentToFCMActivity = new Intent(FunctionActivity.this, FCMActivity.class);
			startActivity(intentToFCMActivity);
			break;
		default:
			break;
		}
		
	}

}
