package com.soil.profile.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.soil.profile.adapter.ImageGridViewAdapter;
import com.soil.profile.db.SoilNoteDB;
import com.soil.profile.model.DatabaseMdl.InfoGeo;
import com.soil.profile.utils.UrlAndFile;
import com.soil.soilsample.R;
import com.soil.soilsample.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class LoadProfileExistedActivity extends BaseActivity implements OnItemClickListener{
    private Toolbar toolbar;
    private List<String> paths;
    private GridView gv;
    private ImageGridViewAdapter adapter;

    private SoilNoteDB soilNoteDB;
    private List<InfoGeo> list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_profile_existed);
        soilNoteDB = SoilNoteDB.getInstance(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        gv = (GridView) findViewById(R.id.gv);
        adapter = new ImageGridViewAdapter(this, getProfilesPaths());
        gv.setAdapter(adapter);
        gv.setOnItemClickListener(this);
    }

    private List<String> getProfilesPaths() {
        list = new ArrayList<InfoGeo>();
        list = soilNoteDB.loadInfoGeo();
        int n = list.size();
        paths = new ArrayList<String>();
        for (int i = 0; i < n; i++) {
            String tmpString = list.get(i).getImagePath();
            //如果文件不存在了，就删除数据库中的相关
            if (!UrlAndFile.fileIsExists(tmpString)) {
                soilNoteDB.deleteInfoGeo(tmpString);
                soilNoteDB.deleteInfoAttrProper(tmpString);
                soilNoteDB.deleteInfoAttrRec(tmpString);
            }else {
                paths.add(tmpString);
            }
        }
        return paths;
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
        Intent intent = new Intent(LoadProfileExistedActivity.this, ActivityImageDetail.class);
        intent.putExtra("imagePath", paths.get(position));
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_load_profile_existed, menu);
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
            case R.id.action_baidu_map:
                Intent intent = new Intent(LoadProfileExistedActivity.this, ActivityBaiduMap.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}

