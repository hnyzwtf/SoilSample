package com.soil.soilsample.ui.myinfo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;
import com.soil.soilsample.R;
import com.soil.soilsample.base.BaseActivity;
import com.soil.soilsample.model.OfflineCityRecord;
import com.soil.soilsample.support.adapter.DownloadedAdapter;
import com.soil.soilsample.support.adapter.DownloadingAdapter;
import com.soil.soilsample.support.adapter.HotCityListViewAdapter;
import com.soil.soilsample.support.adapter.NationCityAdapter;
import com.soil.soilsample.support.util.ToastUtil;

import java.util.ArrayList;

/**
 * Created by GIS on 2016/7/7 0007.
 */
public class OfflineMapActivity extends BaseActivity implements View.OnClickListener, MKOfflineMapListener {
    private MKOfflineMap mOfflineMap;
    private Toolbar toolbar;
    private LinearLayout downloadLayout;//下载管理布局
    private LinearLayout cityListLayout;//城市列表布局
    private Button downloadBtn;//下载管理
    private Button cityListBtn;//城市列表
    private AllCityExpandListView allCitiesExpandLv;//全国所有城市的列表
    private NationCityAdapter cityAdapter;
    private ArrayList<OfflineCityRecord> allCityDatas = new ArrayList<OfflineCityRecord>();//全国的所有城市（省份）列表
    private HotCityListView hotCitiesLv;//热门城市列表
    private HotCityListViewAdapter hotCityAdapter;
    private ArrayList<OfflineCityRecord> hotCitiesDatas = new ArrayList<OfflineCityRecord>();
    private HotCityListView downloadingLv;//正在下载列表
    private HotCityListView downloadedLv;//下载完成列表

    //需要更新的城市数据, 返回各城市离线地图更新信息
    private ArrayList<MKOLUpdateElement> isDoingUpdateMapList = null;
    //正在下载的城市
    private ArrayList<MKOLUpdateElement> downloadingMapList = new ArrayList<MKOLUpdateElement>();
    private DownloadingAdapter mDownloadingAdapter;
    //已经下载的城市
    private ArrayList<MKOLUpdateElement> downloadedMapList = new ArrayList<MKOLUpdateElement>();
    private DownloadedAdapter mDownloadedAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_map);
        mOfflineMap = new MKOfflineMap();
        mOfflineMap.init(this);
        initView();
        initEvents();
        initData();
        //initCityListEvents();
    }
    private void initView()
    {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_white_24dp);
        getSupportActionBar().setTitle("离线地图下载");
        downloadLayout = (LinearLayout) findViewById(R.id.llayout_download_manage);
        cityListLayout = (LinearLayout) findViewById(R.id.llayout_city_list);
        downloadBtn = (Button) findViewById(R.id.btn_download_manage);
        cityListBtn = (Button) findViewById(R.id.btn_citylist);
        allCitiesExpandLv = (AllCityExpandListView) findViewById(R.id.expand_lv_allcities);
        hotCitiesLv = (HotCityListView) findViewById(R.id.lv_hotcity);
        downloadingLv = (HotCityListView) findViewById(R.id.lv_downloading);
        downloadedLv = (HotCityListView) findViewById(R.id.lv_downloaded);
        // 初始状态下显示城市列表布局
        downloadLayout.setVisibility(View.GONE);
        cityListLayout.setVisibility(View.VISIBLE);
        cityListBtn.setBackgroundColor(Color.parseColor("#d7d7d7"));
        downloadBtn.setBackgroundColor(Color.WHITE);
    }
    private void initEvents()
    {
        downloadBtn.setOnClickListener(this);
        cityListBtn.setOnClickListener(this);
    }
    private void initData()
    {
        // hot city

        ArrayList<MKOLSearchRecord> hotCityList = mOfflineMap.getHotCityList();
        if (hotCityList != null)
        {
            for (MKOLSearchRecord cityRecord: hotCityList)
            {
                OfflineCityRecord hotCityRecord = new OfflineCityRecord();
                hotCityRecord.setCityId(cityRecord.cityID);
                hotCityRecord.setCityName(cityRecord.cityName);
                hotCityRecord.setCitySize(cityRecord.size);
                hotCitiesDatas.add(hotCityRecord);
            }
        }
        hotCityAdapter = new HotCityListViewAdapter(OfflineMapActivity.this, hotCitiesDatas);
        hotCitiesLv.setAdapter(hotCityAdapter);
        // all cities except hot city

        ArrayList<MKOLSearchRecord> allCityList = mOfflineMap.getOfflineCityList();//得到所有离线城市列表
        if (allCityList != null)
        {
            for (MKOLSearchRecord cityRecord: allCityList) {
                // 我们可以把OfflineCityRecord offlineCityBean理解为全国的每一个省份(也是城市)
                OfflineCityRecord offlineCityBean = new OfflineCityRecord();
                if (cityRecord.cityType == 1)//如果当前的城市为省份
                {
                    offlineCityBean.setCityId(cityRecord.cityID);
                    offlineCityBean.setCityName(cityRecord.cityName);
                    //获取这个省份下的所有地级城市
                    ArrayList<MKOLSearchRecord> childCities = cityRecord.childCities;
                    //cities用来保存全国所有的地级城市
                    ArrayList<OfflineCityRecord> cities = new ArrayList<OfflineCityRecord>();
                    for (MKOLSearchRecord city: childCities) {
                        OfflineCityRecord cityBean = new OfflineCityRecord();//每一个地级市
                        cityBean.setCityId(city.cityID);
                        cityBean.setCityName(city.cityName);
                        cityBean.setCitySize(city.size);
                        cities.add(cityBean);
                    }
                    offlineCityBean.setChildCities(cities);
                    allCityDatas.add(offlineCityBean);
                }
            }
        }
        cityAdapter = new NationCityAdapter(OfflineMapActivity.this, allCityDatas);
        allCitiesExpandLv.setAdapter(cityAdapter);
        //下载管理
        // 初始化已下载的城市列表，并根据已下载和下载中进行分类
        isDoingUpdateMapList = mOfflineMap.getAllUpdateInfo();
        if (isDoingUpdateMapList == null) {
            isDoingUpdateMapList = new ArrayList<MKOLUpdateElement>();
        }
        if (isDoingUpdateMapList.size() > 0) {
            for (MKOLUpdateElement element : isDoingUpdateMapList) {
                // 如果下载进度为100则应放入“已下载”的List
                if (element.ratio == 100) {
                    downloadedMapList.add(element);
                    //mDownloadedAdapter = new DownloadedAdapter(OfflineMapActivity.this, downloadedMapList, mOfflineMap);
                }
                // 如果下载进入不为100则应放入“正在下载”的List
                if (element.ratio != 100) {
                    downloadingMapList.add(element);
                    /*mDownloadingAdapter = new DownloadingAdapter(OfflineMapActivity.this, downloadingMapList, isDoingUpdateMapList, mOfflineMap,
                            mDownloadedAdapter);*/
                }
            }
        }
        mDownloadedAdapter = new DownloadedAdapter(OfflineMapActivity.this, downloadedMapList, mOfflineMap);
        mDownloadingAdapter = new DownloadingAdapter(OfflineMapActivity.this, downloadingMapList, isDoingUpdateMapList, mOfflineMap,
                mDownloadedAdapter);
        downloadingLv.setAdapter(mDownloadingAdapter);
        downloadedLv.setAdapter(mDownloadedAdapter);
        hotCitiesLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                OfflineCityRecord cityRecord = hotCitiesDatas.get(position);
                int cityId = cityRecord.getCityId();
                String cityName = cityRecord.getCityName();
                MKOLUpdateElement element = mOfflineMap.getUpdateInfo(cityId);
                if (element == null)
                {
                    mOfflineMap.start(cityId);
                    //cityRecord.setDownloadStatus("正在下载");
                    ToastUtil.show(OfflineMapActivity.this, "开始下载：" + cityName);
                    isDoingUpdateMapList = mOfflineMap.getAllUpdateInfo();
                    if (isDoingUpdateMapList == null)
                    {
                        isDoingUpdateMapList = new ArrayList<MKOLUpdateElement>();
                    }
                    element = mOfflineMap.getUpdateInfo(cityId);
                    downloadingMapList.add(element);
                    mDownloadingAdapter.notifyDataSetChanged();
                    hotCityAdapter.notifyDataSetChanged();
                }else {
                    //cityRecord.setDownloadStatus("下载完成");
                    hotCityAdapter.notifyDataSetChanged();
                    ToastUtil.show(OfflineMapActivity.this, "你已经下载过" + cityName + "的离线地图了");
                }
            }
        });
        allCitiesExpandLv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                OfflineCityRecord cityRecord = allCityDatas.get(groupPosition).getChildCities().get(childPosition);
                int cityId = cityRecord.getCityId();
                String cityName = cityRecord.getCityName();
                MKOLUpdateElement updateElement = mOfflineMap.getUpdateInfo(cityId);
                if (updateElement == null)
                {
                    mOfflineMap.start(cityId);
                    //cityRecord.setDownloadStatus("正在下载");
                    ToastUtil.show(OfflineMapActivity.this, "开始下载：" + cityName);
                    isDoingUpdateMapList = mOfflineMap.getAllUpdateInfo();
                    if (isDoingUpdateMapList == null)
                    {
                        isDoingUpdateMapList = new ArrayList<MKOLUpdateElement>();
                    }
                    updateElement = mOfflineMap.getUpdateInfo(cityId);
                    downloadingMapList.add(updateElement);
                    mDownloadingAdapter.notifyDataSetChanged();
                    cityAdapter.notifyDataSetChanged();
                }else {
                    ToastUtil.show(OfflineMapActivity.this, "你已经下载过" + cityName + "的离线地图了");
                }
                return false;
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_download_manage:
                downloadBtn.setBackgroundColor(Color.parseColor("#d7d7d7"));
                cityListBtn.setBackgroundColor(Color.WHITE);
                downloadLayout.setVisibility(View.VISIBLE);
                cityListLayout.setVisibility(View.GONE);
                break;
            case R.id.btn_citylist:
                cityListBtn.setBackgroundColor(Color.parseColor("#d7d7d7"));
                downloadBtn.setBackgroundColor(Color.WHITE);
                if (downloadLayout.getVisibility() == View.VISIBLE && cityListLayout.getVisibility() == View.GONE)
                {

                    downloadLayout.setVisibility(View.GONE);
                    cityListLayout.setVisibility(View.VISIBLE);
                }
                break;
            default:
                break;
        }

    }
    @Override
    public void onGetOfflineMapState(int type, int state) {
        switch (type)
        {
            // 得到当前正在下载的城市的具体更新信息
            case MKOfflineMap.TYPE_DOWNLOAD_UPDATE: // 离线地图下载更新事件类型
                MKOLUpdateElement updateElement = mOfflineMap.getUpdateInfo(state);
                if (updateElement != null)
                {
                    /*ArrayList<MKOLUpdateElement> elements = downloadingMapList;
                    for (MKOLUpdateElement element: elements)
                    {
                        if (updateElement.cityID == element.cityID)
                        {
                            element.ratio = updateElement.ratio;
                            if (updateElement.ratio == 100)
                            {
                                downloadingMapList.remove(element);
                                downloadedMapList.add(element);
                                mDownloadedAdapter.notifyDataSetChanged();
                            }
                        }
                    }*/
                    ArrayList<MKOLUpdateElement> deleteElements = new ArrayList<MKOLUpdateElement>();
                    for (MKOLUpdateElement element: downloadingMapList)
                    {
                        if (element.cityID == updateElement.cityID)
                        {
                            element.ratio = updateElement.ratio;
                            if (element.ratio == 100)
                            {
                                deleteElements.add(element);
                            }
                        }
                    }
                    downloadingMapList.removeAll(deleteElements);
                    downloadedMapList.addAll(deleteElements);
                    mDownloadedAdapter.notifyDataSetChanged();

                }

                mDownloadingAdapter.notifyDataSetChanged();
                break;
            case MKOfflineMap.TYPE_NEW_OFFLINE:
                break;
            case MKOfflineMap.TYPE_VER_UPDATE:
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
