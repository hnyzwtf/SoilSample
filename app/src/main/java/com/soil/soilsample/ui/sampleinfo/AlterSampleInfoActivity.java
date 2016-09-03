package com.soil.soilsample.ui.sampleinfo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.EditText;

import com.soil.soilsample.R;
import com.soil.soilsample.base.BaseActivity;
import com.soil.soilsample.model.CoordinateAlterSample;

/**
 * Created by GIS on 2016/6/21 0021.
 */
public class AlterSampleInfoActivity extends BaseActivity {
    private Toolbar toolbar;

    private EditText sampleNameText;
    private EditText sampleLatitudeText;
    private EditText sampleLongitudeText;
    private EditText sampleCostText;

    private Bundle intentBundle;
    private String markerName = "";//声明样点marker的name属性，从intent中获取
    private String markerCost = "";//声明样点marker的cost属性，从intent中获取
    private String markerLat;
    private String markerLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alter_sample_info);
        initView();
        initIntentParams();
        initEditText();
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_white_24dp);
        getSupportActionBar().setTitle("样点详情");

        sampleNameText = (EditText) findViewById(R.id.edit_sample_name);
        sampleLatitudeText = (EditText) findViewById(R.id.edit_sample_lat);
        sampleLongitudeText = (EditText) findViewById(R.id.edit_sample_lng);
        sampleCostText = (EditText) findViewById(R.id.edit_sample_cost);
    }

    /**
     * 获取从MainActivity中通过intent传递的参数
     */
    private void initIntentParams()
    {
        Intent intentFromMainActivity = getIntent();
        intentBundle = intentFromMainActivity.getBundleExtra("alterMarkerInfoBundle");
        CoordinateAlterSample markerCoor = intentBundle.getParcelable("alterMarker_bundle");
        int nameTxt = 0;
        try {//从服务器返回的可替代样点的编号是从0开始的，为了显示，让每个编号加1，从1开始显示
            nameTxt = Integer.parseInt(markerCoor.getName()) + 1;
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        markerName = String.valueOf(nameTxt);
        double latitude = markerCoor.getY();
        double longitude = markerCoor.getX();
        markerCost = markerCoor.getCostValue();
        markerLat = String.valueOf(latitude);
        markerLng = String.valueOf(longitude);

    }

    private void initEditText()
    {
        sampleNameText.setText(markerName);
        sampleLatitudeText.setText(markerLat);
        sampleLongitudeText.setText(markerLng);
        sampleCostText.setText(markerCost);

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
}
