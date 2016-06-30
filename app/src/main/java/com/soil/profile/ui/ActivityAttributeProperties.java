package com.soil.profile.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.soil.profile.model.DatabaseMdl.InfoAttrProper;
import com.soil.soilsample.R;
import com.soil.soilsample.base.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityAttributeProperties extends BaseActivity{
    private Toolbar toolbar;

    private final String[] arr_data = {"层次","深度","颜色（干）","颜色（润）","干湿度","质地","结构",
            "松紧度","空隙","新生体种类","新生体形态","新生体数量","侵入体","根系"
            ,"野外测定-PH","野外测定-石灰反应"};

    public static List<Map<String, Object>> dataItemList;
    private ListView listView;
    private SimpleAdapter simp_adapter;

    private int clickItem;
    private InfoAttrProper proper;
    private String fromString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attribute_properties);
        //这个getIntent得到的intent就是跳转过来的那个Activity里面声明的intent
        Intent intent = getIntent();
        fromString = intent.getStringExtra("tag");
        if ("adapter".equals(fromString)) {
            proper = (InfoAttrProper) getIntent().getSerializableExtra("proper");
        }

        initView();

        dataItemList = new ArrayList<Map<String,Object>>();
        simp_adapter = new SimpleAdapter(this, getData(), R.layout.item_attr,
                new String[]{"label","content", "pic"}, new int[]{R.id.label, R.id.content, R.id.arrow_right});
        listView.setAdapter(simp_adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                clickItem = position;
                Intent intent = new Intent(ActivityAttributeProperties.this, TableAttrProperActivity.class);
                intent.putExtra("content", dataItemList.get(position).get("content")+"");
                startActivityForResult(intent, 1);
            }
        });
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_white_24dp);

        listView = (ListView) findViewById(R.id.list_properties);

    }

    private List<Map<String, Object>> getData() {
        //Java中比较字符串是否相等不要直接写==，要写equals
        if ("adapter".equals(fromString)) {
            List<String> tmp = new ArrayList<String>();
            tmp = proper.getProperContents();
            int n = arr_data.length;
            for(int i=0;i<n;i++)
            {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("label", arr_data[i]);
                if (null == tmp.get(i)) {
                    map.put("content", "");
                }else {
                    map.put("content", tmp.get(i));
                }
                map.put("pic", R.drawable.arrow_right);
                dataItemList.add(map);
            }
        }else {
            int n = arr_data.length;
            for(int i=0;i<n;i++)
            {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("label", arr_data[i]);
                map.put("content", "");
                map.put("pic", R.drawable.arrow_right);
                dataItemList.add(map);
            }
        }
        return dataItemList;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("label", arr_data[clickItem]);
                    map.put("content", data.getStringExtra("content_return")+"");
                    map.put("pic", R.drawable.arrow_right);
                    dataItemList.set(clickItem, map);
                    simp_adapter.notifyDataSetChanged();
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_attribute_properties, menu);
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
            case R.id.action_save:
                //	Intent intent = new Intent();
//			Bundle bundle = new Bundle();
//			bundle.putSerializable("dateItemList", dataItemList);
//			intent.putExtras(bundle);
//			intent.putExtra("haha", "试试看吧");
                setResult(RESULT_OK);
                finish();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

