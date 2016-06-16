package com.soil.soilsample.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by GIS on 2016/5/2 0002.
 */
public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
