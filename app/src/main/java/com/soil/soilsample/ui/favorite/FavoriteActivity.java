package com.soil.soilsample.ui.favorite;

import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.soil.soilsample.R;
import com.soil.soilsample.base.BaseActivity;
import com.soil.soilsample.support.adapter.FavoriteListAdapter;
import com.soil.soilsample.support.kml.KmlSharedPrefHelper;
import com.soil.soilsample.ui.empty.EmptyLayout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by GIS on 2016/7/3 0003.
 */
public class FavoriteActivity extends BaseActivity implements View.OnClickListener {
    private EmptyLayout mEmptyLayout;
    private Toolbar toolbar;
    // 数据源
    private List<String> data = null;
    //分组标识
    private List<String> dataTag = null;
    private ListView listView;
    private FavoriteListAdapter adapter = null;
    private String listviewItem;
    private PopupWindow popWindow = null;
    private KmlSharedPrefHelper kmlSharedPrefHelper = KmlSharedPrefHelper.getInstance(FavoriteActivity.this);
    private String alterName = kmlSharedPrefHelper.getAlterMarkerNameFromPref();
    private static final int SHOW_MARKERS = 1;
    private static final int HIDE_MARKERS = 2;
    private static final int DELETE_MARKERS = 3;
    private static final int SAVE_MARKERS = 4;// 保存按钮，主要作用是，当退出程序，再打开程序后，添加过的marker可以直接显示在地图上
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorite);
        initView();
        initData();
        adapter = new FavoriteListAdapter(FavoriteActivity.this,R.layout.favorite_item_tag, R.layout.favorite_item, dataTag, data);
        listView.setAdapter(adapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                listviewItem = data.get(position);
                showPopwindow();

                return true;
            }
        });
        initEvents();
    }

    private void initView() {
        listView = (ListView) findViewById(R.id.favorite_listview);
        mEmptyLayout = (EmptyLayout) findViewById(R.id.error_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_white_24dp);
        getSupportActionBar().setTitle("收藏");
    }
    private void initData()
    {
        data = new ArrayList<String>();
        dataTag = new ArrayList<String>();
        dataTag.add("已添加的kml文件");
        data.add("已添加的kml文件");
        Set<String> addedKmlNameSet = kmlSharedPrefHelper.getAddedKmlNames();

        if (addedKmlNameSet != null && addedKmlNameSet.size() > 0)
        {
            Iterator iterator = addedKmlNameSet.iterator();
            while (iterator.hasNext())
            {
                data.add((String)iterator.next());
            }
        }
        else
        {
            //mEmptyLayout.setErrorType(EmptyLayout.NODATA);
            data.add("加载成功的kml文件名将会出现在这里呢");
        }
        dataTag.add("可替代样点");
        data.add("可替代样点");

        if (alterName != null)
        {
            data.add(alterName);
        }else {
            data.add("这里什么都没有，快去计算可替代样点吧");
        }
    }
    private void initEvents()
    {
        mEmptyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
    private void showPopwindow(){
        LayoutInflater inflater = LayoutInflater.from(this);//获取一个填充器
        View view = inflater.inflate(R.layout.cus_popwindow, null);//填充我们自定义的布局
        Button buttonShow = (Button) view.findViewById(R.id.btn_show);
        Button buttonHidden = (Button) view.findViewById(R.id.btn_hidden);
        Button buttonDelete = (Button) view.findViewById(R.id.btn_delete);
        Button buttonSave = (Button) view.findViewById(R.id.btn_save);
        buttonShow.setOnClickListener(this);
        buttonHidden.setOnClickListener(this);
        buttonDelete.setOnClickListener(this);
        buttonSave.setOnClickListener(this);
        Display display = getWindowManager().getDefaultDisplay();//得到当前屏幕的显示器对象
        Point size = new Point();//创建一个Point点对象用来接收屏幕尺寸信息
        display.getSize(size);//Point点对象接收当前设备屏幕尺寸信息
        int width = size.x;//从Point点对象中获取屏幕的宽度(单位像素)
        int height = size.y;//从Point点对象中获取屏幕的高度(单位像素)
       // Log.v("zxy", "width="+width+",height="+height);//width=480,height=854可知手机的像素是480x854的
        //创建一个PopupWindow对象，第二个参数是设置宽度的，用刚刚获取到的屏幕宽度乘以2/3，取该屏幕的2/3宽度，从而在任何设备中都可以适配，高度则包裹内容即可，最后一个参数是设置得到焦点
        popWindow = new PopupWindow(view, 2*width/3, WindowManager.LayoutParams.WRAP_CONTENT, true);
        popWindow.setBackgroundDrawable(new BitmapDrawable());//设置PopupWindow的背景为一个空的Drawable对象，如果不设置这个，那么PopupWindow弹出后就无法退出了
        popWindow.setOutsideTouchable(true);//设置是否点击PopupWindow外退出PopupWindow

        //第一个参数为父View对象，即PopupWindow所在的父控件对象，第二个参数为它的重心，后面两个分别为x轴和y轴的偏移量
        popWindow.showAtLocation(inflater.inflate(R.layout.favorite, null), Gravity.CENTER, 0, 0);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_show://显示按钮
                if (listviewItem.equals(alterName))
                {
                    showHideDeleteAlter(listviewItem, SHOW_MARKERS);

                }else {
                    showHideDeleteKml(listviewItem, SHOW_MARKERS);
                }
                popWindow.dismiss();
                break;
            case R.id.btn_hidden:
                if (listviewItem.equals(alterName))
                {
                    showHideDeleteAlter(listviewItem, HIDE_MARKERS);

                }else {
                    showHideDeleteKml(listviewItem, HIDE_MARKERS);
                }
                popWindow.dismiss();
                break;
            case R.id.btn_delete:
                if (listviewItem.equals(alterName))
                {
                    data.remove(listviewItem);
                    adapter.notifyDataSetChanged();
                    showHideDeleteAlter(listviewItem, DELETE_MARKERS);

                }else {
                    data.remove(listviewItem);
                    adapter.notifyDataSetChanged();
                    showHideDeleteKml(listviewItem, DELETE_MARKERS);
                }
                popWindow.dismiss();
                break;
            case R.id.btn_save:
                if (listviewItem.equals(alterName))
                {
                    showHideDeleteAlter(listviewItem, SAVE_MARKERS);

                }else {
                    showHideDeleteKml(listviewItem, SAVE_MARKERS);
                }
                popWindow.dismiss();
                break;
            default:
                break;
        }

    }


    /**
     * @param kmlName
     * @param flag
     * 显示，隐藏，删除kml
     * 目前只支持对一个kml文件进行操作，2016/07/05
     * sharedpref只能保存一个kml的状态，对应MainActivity中的showHideDelMarkers
     */
    public void showHideDeleteKml(String kmlName, int flag)
    {
        SharedPreferences.Editor editor = getSharedPreferences("showHideDelete_pref", MODE_PRIVATE).edit();
        editor.putString("selectKmlName", kmlName);
        editor.putInt(kmlName, flag);
        editor.commit();
    }
    /**
     * @param markerName
     * @param flag
     * 显示，隐藏，删除kml
     * 目前只支持对一个kml文件进行操作，2016/07/05
     * sharedpref只能保存一个kml的状态, 对应MainActivity中的showHideDelAlterMarkers
     */
    public void showHideDeleteAlter(String markerName, int flag)
    {
        SharedPreferences.Editor editor = getSharedPreferences("showHideDeleteAlter_pref", MODE_PRIVATE).edit();
        editor.putString("selectName", markerName);
        editor.putInt(markerName, flag);
        editor.commit();
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
