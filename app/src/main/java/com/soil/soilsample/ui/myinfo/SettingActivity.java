package com.soil.soilsample.ui.myinfo;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.soil.soilsample.R;
import com.soil.soilsample.base.BaseActivity;


/**
 * Created by GIS on 2016/5/31 0031.
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private LinearLayout aboutMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
        initView();
        initEvents();
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_white_24dp);

        aboutMe = (LinearLayout) findViewById(R.id.rl_about_me);
    }
    private void initEvents() {

        aboutMe.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.rl_about_me:
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
