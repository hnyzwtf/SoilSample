package com.soil.soilsample.ui.sampleinfo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;

import com.soil.soilsample.R;
import com.soil.soilsample.base.BaseActivity;

/**
 * Created by GIS on 2016/6/16 0016.
 */
public class HtmlCommentActivity extends BaseActivity {
    private Toolbar toolbar;
    private WebView sampleHtmlComment;//显示html内容的webview

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.htmlcomment);
        initView();
        initEvents();
    }
    private void initView() {
        sampleHtmlComment = (WebView)findViewById(R.id.wv_html_comment);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_white_24dp);
        getSupportActionBar().setTitle("html备注");
    }

    private void initEvents() {
        Intent intentFromSampleInfo = getIntent();
        String htmlComment = intentFromSampleInfo.getStringExtra("htmlComment");
        //sampleHtmlComment.loadData(htmlComment, "text/html", "utf-8");
        //使用loadData方法无法显示中文，故使用下面这个方法来解析
        sampleHtmlComment.loadDataWithBaseURL(null, htmlComment, "text/html", "utf-8", null);
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
