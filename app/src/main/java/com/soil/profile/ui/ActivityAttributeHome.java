package com.soil.profile.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

import com.soil.soilsample.R;
import com.soil.soilsample.base.BaseActivity;


public class ActivityAttributeHome extends BaseActivity implements OnClickListener{
    private Toolbar toolbar;
    private RelativeLayout properties, record;
    private String saveFilePath;
    private String modifyWay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attribute_home);

        Intent intent = getIntent();
        if ("edit_photo".equals(intent.getStringExtra("back_activity"))) {
            modifyWay = "insert";
        }else if ("image_detail".equals(intent.getStringExtra("back_activity"))) {
            modifyWay = "update";
        }
        saveFilePath = intent.getStringExtra("saveFilePath");

        initView();
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_white_24dp);
        getSupportActionBar().setTitle("属性编辑");
        properties = (RelativeLayout) findViewById(R.id.id_properties);
        record = (RelativeLayout) findViewById(R.id.id_record);

        properties.setOnClickListener(this);
        record.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_properties:
                if ("insert".equals(modifyWay)) {
                    Intent intent = new Intent(ActivityAttributeHome.this, TableAttrProperActivity.class);
                    intent.putExtra("saveFilePath", saveFilePath);
                    startActivity(intent);
                }else if ("update".equals(modifyWay)){
                    Intent intent = new Intent(ActivityAttributeHome.this, UpdateTableAttrProperActivity.class);
                    intent.putExtra("saveFilePath", saveFilePath);
                    startActivity(intent);
                }
                break;
            case R.id.id_record:
                Intent intent1 = new Intent(ActivityAttributeHome.this, TableAttrRecActivity.class);
                startActivity(intent1);
                break;
           default:
               break;
        }
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
