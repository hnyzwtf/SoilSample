package com.soil.soilsample.ui.myinfo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.soil.soilsample.R;
import com.soil.soilsample.base.ActivityCollector;
import com.soil.soilsample.base.BaseActivity;

/**
 * Created by GIS on 2016/6/16 0016.
 */
public class MyInfoActivity extends BaseActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private LinearLayout altersampleModel;
    private LinearLayout setLayout;
    private LinearLayout exitFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myinfo);
        initView();
        initEvents();
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_white_24dp);
        altersampleModel = (LinearLayout) findViewById(R.id.rl_altersample_model);
        setLayout = (LinearLayout) findViewById(R.id.rl_set);
        exitFinish = (LinearLayout) findViewById(R.id.rl_exit);
    }
    private void initEvents()
    {
        altersampleModel.setOnClickListener(this);
        setLayout.setOnClickListener(this);
        exitFinish.setOnClickListener(this);
    }
    private void saveSetInfoToShared(int sampleModelId)
    {
        SharedPreferences.Editor editor = getSharedPreferences("SettingInfo", MODE_PRIVATE).edit();
        editor.putInt("altersampleModel", sampleModelId);
        editor.commit();
    }
    private int getSetInfoFromShared()
    {
        SharedPreferences preferences = getSharedPreferences("SettingInfo", MODE_PRIVATE);
        int selectedItem = preferences.getInt("altersampleModel", 0);
        return selectedItem;
    }
    private void showSampleModelDialog()
    {
        final String[] items = new String[]{"地理环境相似度","FCM模糊隶属度"};
        int selectedItem = getSetInfoFromShared();
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle("替代样点计算方法选择").setSingleChoiceItems(
                items, selectedItem, new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        saveSetInfoToShared(which);
                        dialog.dismiss();
                    }
                }
        ).create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_myinfo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id)
        {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_help:
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.rl_altersample_model:
                showSampleModelDialog();
                break;
            case R.id.rl_set:
                startActivity(new Intent(MyInfoActivity.this, SettingActivity.class));
                break;
            case R.id.rl_exit:
                finish();
                ActivityCollector.finishAll();
                break;
            default:
                break;
        }

    }
}
