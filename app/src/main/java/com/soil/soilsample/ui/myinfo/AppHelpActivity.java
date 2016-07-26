package com.soil.soilsample.ui.myinfo;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;

import com.soil.soilsample.R;
import com.soil.soilsample.base.BaseActivity;

/**
 * Created by GIS on 2016/7/20 0020.
 */
public class AppHelpActivity extends BaseActivity {
    private Toolbar toolbar;
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_help);
        initView();
        loadAppHelpHtml();
    }

    private void initView() {
        mWebView = (WebView) findViewById(R.id.web_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_white_24dp);
        getSupportActionBar().setTitle("帮助");
    }
    private void loadAppHelpHtml()
    {
        mWebView.loadUrl("file:///android_asset/app_help.html");
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
