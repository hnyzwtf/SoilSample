package com.soil.soilsample.ui.main;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Text;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.navisdk.adapter.BNOuterTTSPlayerCallback;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BNaviSettingManager;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.google.gson.Gson;
import com.soil.profile.ui.SoilProfileActivity;
import com.soil.soilsample.BuildConfig;
import com.soil.soilsample.R;
import com.soil.soilsample.base.ActivityCollector;
import com.soil.soilsample.base.BaseActivity;
import com.soil.soilsample.model.Coordinate;
import com.soil.soilsample.model.CoordinateAlterSample;
import com.soil.soilsample.model.FirVersionJson;
import com.soil.soilsample.support.kml.KmlSharedPrefHelper;
import com.soil.soilsample.support.kml.ReadKml;
import com.soil.soilsample.support.util.CoorToBaidu;
import com.soil.soilsample.support.util.SDFileHelper;
import com.soil.soilsample.support.util.ToastUtil;
import com.soil.soilsample.ui.favorite.FavoriteActivity;
import com.soil.soilsample.ui.listener.MyOrientationListener;
import com.soil.soilsample.ui.myinfo.AppHelpActivity;
import com.soil.soilsample.ui.myinfo.MyInfoActivity;
import com.soil.soilsample.ui.myinfo.OfflineMapActivity;
import com.soil.soilsample.ui.sampleinfo.AlterParamsActivity;
import com.soil.soilsample.ui.sampleinfo.AlterParamsFCMActivity;
import com.soil.soilsample.ui.sampleinfo.AlterSampleInfoActivity;
import com.soil.soilsample.ui.sampleinfo.SampleInfoActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import im.fir.sdk.FIR;
import im.fir.sdk.VersionCheckCallback;

public class MainActivity extends BaseActivity implements View.OnClickListener, FileBrowserFragment.OnFileAndFolderFinishListener,
        BaiduMap.OnMarkerClickListener, BaiduMap.OnMapClickListener, NavigationView.OnNavigationItemSelectedListener{
    private Toolbar toolbar;
    private MapView mMapView = null;
    private BaiduMap myBaiduMap;
    private View defaultBaiduMapLogo = null;
    private View defaultBaiduMapScaleUnit = null;
    private String[] types = {"普通地图","卫星地图"};
    private ImageView selectMapType, myLocation, selectLocationMode;
    // location
    private LocationClient myLocationClient;
    private MyLocationListener mLocationListener;
    private String[] LocationModeString = {"罗盘模式","普通模式","跟随模式"};
    private float myCurrentX;// 定位数据的方向信息
    private BitmapDescriptor myLocationBitmap;
    private Boolean isFirstIn = true;// 是否是第一次定位
    private double latitude,longtitude;//定位的经度和纬度
    private MyOrientationListener myOrientationListener;
    // other
    private KmlSharedPrefHelper kmlPrefHelper = KmlSharedPrefHelper.getInstance(MainActivity.this);
    private boolean doubleBackToExitOnce = false;
    DecimalFormat df = new DecimalFormat("0.000000");
    private String mInitDir = Environment.getExternalStorageDirectory()
            .getPath();
    private static final String TAG = "MainActivity";

    // navi
    private String authinfo = null;
    public static List<Activity> activityList = new LinkedList<Activity>();
    public static final String ROUTE_PLAN_NODE = "routePlanNode";
    private int initBaiduNaviSuccess = 0;//1表示初始化百度导航成功，0表示失败
    // ReadKml
    private ReadKml readKml = null;
    // 点击某个marker后，从底部弹出的布局
    private LinearLayout markOverviewLayout;
    private TextView calcAlterSample;//计算可替代样点
    private TextView markDetailLayout;//marker详情
    private TextView markNaviLayout;//marker导航
    public static int altersampleModel = 0;//用户设置的可替代样点计算方法
    // 侧边抽屉布局
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    // 当前正在操作的marker
    private Marker currentMarker;
    // sampleinfoActivity
    private String currentMarkerName;//接收从SampleInfoActivity返回的当前正在操作的markerName
    private int samplePicChanged = 0;//设置更改后的marker的图片
    // markers集合，kmlMarkers保存所有的从Kml文件中解析来的marker，alterMarkers保存所有的服务器返回的可替代样点坐标marker
    private List<Marker> kmlMarkers = new ArrayList<Marker>();
    private List<Text> kmlMarkersText = new ArrayList<Text>();//每个marker的textoptions
    private List<Marker> alterMarkers = new ArrayList<Marker>();
    //设置标志位，0表示此marker是从kml文件中得到了，1表示为可替代样点的marker
    private int markerFlag = -1;
    // 设置标志位，判断底部导航栏是否可以点击
    private Boolean isBottomClickable = true;
    // 显示，隐藏，删除marker，例如4thalter.kmz在地图上marker的状态，他是否在地图上显示（隐藏）了marker
    private int currentMarkerState = 0;
    private int currentAlterMarkerState = 0;
    private static final int MARKER_NO_EDIT = -1;
    private static final int SHOW_MARKERS = 1;
    private static final int HIDE_MARKERS = 2;
    private static final int DELETE_MARKERS = 3;
    private static final int SAVE_MARKERS = 4;// 保存按钮，主要作用是，当退出程序，再打开程序后，添加过的marker可以直接显示在地图上

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityList.add(this);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        //setOverflowButtonAlways();
        initView();
        initEvents();
        changeDefaultBaiduMapView();
        initMapLocation();

        if (createSoilSampleDir())
        {
            copySampleKmlToSD();
            initNavi();
        }
        showMarkersOnCreate();
        showAlterMarkersOnCreate();
        checkForUpdate();
        Log.d(TAG, "onCreate: ");
    }
    private void initView()
    {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.id_drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.id_navi_view);

        //Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //toolbar.setNavigationIcon(R.drawable.icon_navigation);
        toolbar.setOverflowIcon(getResources().getDrawable(R.drawable.toolbar_add_icon));
        mMapView = (MapView) findViewById(R.id.baidu_mapview);
        selectMapType = (ImageView) findViewById(R.id.map_type);
        myLocation = (ImageView) findViewById(R.id.my_location);
        selectLocationMode = (ImageView) findViewById(R.id.map_location);

        markOverviewLayout = (LinearLayout) findViewById(R.id.mark_overview_layout);
        calcAlterSample = (TextView) findViewById(R.id.tv_mark_calc_altersample);
        markDetailLayout = (TextView) findViewById(R.id.tv_mark_detail);
        markNaviLayout = (TextView) findViewById(R.id.tv_mark_navi);

    }
    private void initEvents()
    {
        selectMapType.setOnClickListener(this);
        myLocation.setOnClickListener(this);
        selectLocationMode.setOnClickListener(this);
        calcAlterSample.setOnClickListener(this);
        markDetailLayout.setOnClickListener(this);
        markNaviLayout.setOnClickListener(this);
        markOverviewLayout.setOnClickListener(this);
        /*
        * drawerLayout左侧菜单的展开与隐藏可以被DrawerLayout.DrawerListener的实现监听到，
        * 这样你就可以在菜单展开与隐藏的时刻做一些希望做的事情，比如更新actionbar菜单等。如果你的activity有actionbar的话，
        * 还是建议你用ActionBarDrawerToggle来监听，ActionBarDrawerToggle实现了DrawerListener，所以他能做DrawerListener可以做的任何事情，
        * 同时他还能将drawerLayout的展开和隐藏与actionbar的app 图标关联起来，当展开与隐藏的时候图标有一定的平移效果，点击图标的时候还能展开或者隐藏菜单。
        * */
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MainActivity.this, mDrawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();
        mNavigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * 获取从AlternativeParamsActivity中传递的altersamples list
     */
    private void initIntentParams()
    {
        // intent from AlterParamsActivity
        Intent intentFromAlterParams = getIntent();
        //ArrayList<CoordinateAlterSample> alterSamplesList = new ArrayList<CoordinateAlterSample>();
        ArrayList<CoordinateAlterSample> alterSamplesList  = intentFromAlterParams.getParcelableArrayListExtra("alterSampleList");

        if (alterSamplesList != null)
        {
            // 在添加新的marker之前，把以前的marker都删除掉
            if (alterMarkers != null)
            {
                for (int i = 0; i < alterMarkers.size(); i++) {
                    alterMarkers.get(i).remove();
                }
                alterMarkers.removeAll(alterMarkers);
            }
            addAlterMarkers(alterSamplesList);

        }
        // intent from AlterParamsFCMActivity
        Intent intentFromAlterFCM = getIntent();
        ArrayList<CoordinateAlterSample> alterFCMSamplesList  = intentFromAlterFCM.getParcelableArrayListExtra("alterFCMSampleList");

        if (alterFCMSamplesList != null)
        {
            // 在添加新的marker之前，把以前的marker都删除掉
            if (alterMarkers != null)
            {
                for (int i = 0; i < alterMarkers.size(); i++) {
                    alterMarkers.get(i).remove();
                }
                alterMarkers.removeAll(alterMarkers);
            }
            addAlterMarkers(alterFCMSamplesList);

        }
    }

    /**
     * @param intent
     * MainActivity设置的启动模式为singleTask，即程序中只会产生一个MainActivity的实例，如此带来的问题就是，无法及时的接收到新的intent传入的数据
     * 因此，在onNewIntent中通过setIntent来解决
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.map_type:
                changeMapType();
                break;
            case R.id.my_location:
                getMyLatestLocation(latitude, longtitude);
                break;
            case R.id.map_location:
                changeLocationMode();
                break;
            case R.id.tv_mark_calc_altersample://计算可替代样点
                altersampleModel = getSetInfoFromShared();//从shared中读取配置信息，在本类中是读取用户选择了什么采样方法
                if (markerFlag == 0) //表示是从Kml文件中得来的marker
                {
                    Bundle bundle = currentMarker.getExtraInfo();
                    Coordinate markerCoor = bundle.getParcelable("marker_bundle");
                    String markerName = markerCoor.getName();
                    double latitude = markerCoor.getX();
                    double longitude = markerCoor.getY();
                    String markerLat = String.valueOf(latitude);
                    String markerLng = String.valueOf(longitude);
                    String markerLongLat = markerLng + "," + markerLat;//用于发送至服务器的坐标
                    //显示在控件上的坐标，需要保留小数位
                    String markerLongLatShow = String.valueOf(df.format(longitude)) + "," + String.valueOf(df.format(latitude));
                    if (altersampleModel == 0)//表示采用相似度的计算方法
                    {
                        //String markerLongLat = markerLng + "," + markerLat;
                        Intent intentToAlterOptions = new Intent(MainActivity.this, AlterParamsActivity.class);
                        intentToAlterOptions.putExtra("markerLongLat", markerLongLat);//用于发送至服务器的坐标
                        intentToAlterOptions.putExtra("markerLongLatShow", markerLongLatShow);//显示在控件上的坐标，需要保留小数位
                        intentToAlterOptions.putExtra("markerName", markerName);
                        startActivity(intentToAlterOptions);

                    }
                    if (altersampleModel == 1)
                    {
                        //String markerLongLat = markerLng + "," + markerLat;
                        Intent intentToAlterFCMOptions = new Intent(MainActivity.this, AlterParamsFCMActivity.class);
                        intentToAlterFCMOptions.putExtra("markerLongLat", markerLongLat);
                        intentToAlterFCMOptions.putExtra("markerLongLatShow", markerLongLatShow);
                        intentToAlterFCMOptions.putExtra("markerName", markerName);
                        startActivity(intentToAlterFCMOptions);
                    }
                }
                break;
            case R.id.tv_mark_detail:
                intentToSampleInfoActivity();
                break;
            case R.id.tv_mark_navi:

                if (BaiduNaviManager.isNaviInited()) {
                    ToastUtil.show(MainActivity.this, "百度导航引擎初始化成功，即将为您导航");
                    routeplanToNavi();
                }
                else
                {
                    ToastUtil.show(MainActivity.this, "百度导航引擎初始化失败！");
                }
                break;
            case R.id.mark_overview_layout:
                /*
                * 两个view重叠堆放，view1在上，view2在下，view1中有几个按钮.由于view1中的按钮设置了点击响应函数，等于消费了在按钮区域的点击，
                * 底下view2便不会再响应点击事件了。在view1的非按钮区域点击，由于没有消费点击事件，点击事件会继续传到底下的view2
                * （事件是先传到他们的父view，也就是包含他们的layout，父view遍历所有的子view传递事件），
                * 如果需要view2任何区域都接收不到事件，应该设置整个view1的响应回调函数，以消费任何区域的点击事件。
                * 即我们给markOverviewLayout设置一个onClick点击事件，什么都不写。这样，点击空白处时，地图界面便不会有任何反应了
                * */
                break;
            default:
                break;
        }
    }

    /**
     *
     * 初始化定位
     * */
    private void initMapLocation() {
        myLocationClient = new LocationClient(this);//创建一个定位客户端对象
        mLocationListener = new MyLocationListener();//创建一个定位事件监听对象
        myLocationClient.registerLocationListener(mLocationListener);//并给该定位客户端对象注册监听事件
        //对LocaitonClient进行一些必要的设置
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");//设置坐标的类型
        option.setIsNeedAddress(true);//返回当前的位置信息，如果不设置为true，默认就为false，就无法获得位置的信息
        option.setOpenGps(true);//打开GPS
        option.setScanSpan(5000);//表示5s中进行一次定位请求
        myLocationClient.setLocOption(option);
        useLocationOrientationListener();//调用方向传感器

    }
    /**
     * 定位结合方向传感器，从而可以实时监测到X轴坐标的变化，从而就可以检测到
     * 定位图标方向变化，只需要将这个动态变化的X轴的坐标更新myCurrentX值，
     * 最后在MyLocationData data.driection(myCurrentX);
     * */
    private void useLocationOrientationListener() {
        myOrientationListener = new MyOrientationListener(MainActivity.this);
        myOrientationListener.setMyOrientationListener(new MyOrientationListener.onOrientationListener() {
            @Override
            public void onOrientationChanged(float x) {//监听方向的改变，方向改变时，需要得到地图上方向图标的位置
                myCurrentX = x;

            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.nav_soil:
                startActivity(new Intent(MainActivity.this, SoilProfileActivity.class));
                break;
            case R.id.nav_offline_map:
                startActivity(new Intent(MainActivity.this, OfflineMapActivity.class));
                break;
            case R.id.nav_set:
                startActivity(new Intent(MainActivity.this, MyInfoActivity.class));
                break;
            case R.id.nav_favorite:
                startActivity(new Intent(MainActivity.this, FavoriteActivity.class));
                break;
            case R.id.nav_help:
                startActivity(new Intent(MainActivity.this, AppHelpActivity.class));
                break;
            case R.id.nav_exit:
                finish();
                ActivityCollector.finishAll();
                break;
            default:
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.id_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * 定位监听器
     */
    private class MyLocationListener implements BDLocationListener
    {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation == null || mMapView == null)
            {
                return;
            }
            //得到一个MyLocationData对象，需要将BDLocation对象转换成MyLocationData对象
            MyLocationData data = new MyLocationData.Builder()
                    .accuracy(bdLocation.getRadius())//精度半径
                    .direction(myCurrentX)//方向
                    .latitude(bdLocation.getLatitude())
                    .longitude(bdLocation.getLongitude())
                    .build();
            myBaiduMap.setMyLocationData(data);
            changeLocationIcon();
            latitude = bdLocation.getLatitude();
            longtitude = bdLocation.getLongitude();
            if (isFirstIn)
            {
                getMyLatestLocation(latitude, longtitude);
                isFirstIn = false;
            }
        }
    }

    /**
     * 改变百度地图默认的定位图标
     */
    private void changeLocationIcon()
    {
        myLocationBitmap = BitmapDescriptorFactory.fromResource(R.drawable.location_marker);
        if (isFirstIn) {//表示第一次定位显示普通模式
            MyLocationConfiguration configuration = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL,
                    true, myLocationBitmap);// 配置定位图层显示方式
            myBaiduMap.setMyLocationConfigeration(configuration);
        }
    }

    /**
     * @param lat
     * @param lng
     * 获得最新定位的位置,并且地图的中心点设置为我的位置
     */
    private void getMyLatestLocation(double lat, double lng)
    {
        LatLng latLng = new LatLng(lat, lng);
        MapStatusUpdate mapStatus = MapStatusUpdateFactory.newLatLng(latLng);//创建一个地图最新更新的状态对象，需要传入一个最新经纬度对象
        //以动画方式更新地图状态，然后利用百度地图对象来展现地图更新状态，即此时的地图显示就为你现在的位置
        myBaiduMap.animateMapStatus(mapStatus);
//        MapStatus.Builder builder = new MapStatus.Builder();
//        builder.target(latLng).zoom(15.0f);
//        myBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
    }

    /**
     * 改变百度地图默认的view，因为百度地图自带的mapview包含logo和比例尺,要将他们去掉
     */
    private void changeDefaultBaiduMapView()
    {
        changeInitialScaleView();
        defaultBaiduMapLogo = mMapView.getChildAt(1);//该View是指百度地图中默认的百度地图的Logo,得到这个View
        defaultBaiduMapLogo.setPadding(300, -10, 100, 100);//设置该默认Logo View的位置，因为这个该View的位置会影响下面的刻度尺单位View显示的位置
        mMapView.removeViewAt(1);//最后移除默认百度地图的logo View
        defaultBaiduMapScaleUnit = mMapView.getChildAt(2);//得到百度地图的默认单位刻度的View
        defaultBaiduMapScaleUnit.setPadding(100, 0, 115,200);//最后设置调整百度地图的默认单位刻度View的位置
        mMapView.removeViewAt(2);

    }
    /**
     *
     * 修改百度地图默认开始初始化加载地图比例大小
     * */
    private void changeInitialScaleView() {
        myBaiduMap = mMapView.getMap();
        MapStatusUpdate factory = MapStatusUpdateFactory.zoomTo(15.0f);// 设置地图缩放级别
        myBaiduMap.animateMapStatus(factory);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * @param view
     * @param menu
     * @return
     * 反射用法，使得overflow的下拉菜单带图标显示
     */
    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(), "onMenuOpened...unable to set icons for overflow menu", e);
                }
            }
        }
        return super.onPrepareOptionsPanel(view, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.action_add_sample:
                openFileBrowser();
            break;
            case R.id.action_upload:
                startActivity(new Intent(MainActivity.this, UploadActivity.class));
                break;
        }
        //return super.onOptionsItemSelected(item);
        return false;//注意，这里必须是false，否则FileBrowserFragment中toolbar上的返回按钮不可点击
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if ((item.getItemId() == R.id.action_add_sample)) {
                item.setVisible(true);
            } else if ((item.getItemId() == R.id.menu_dir_up)
                    || (item.getItemId() == R.id.menu_dir_select)) {
                item.setVisible(false);
            }
        }
        return true;
    }
    @Override
    public void onFileFinish(String path) { // Handle the event when a file is tapped in the file browser
        // Dismiss file browser and refresh actionbar.
        this.getSupportFragmentManager().popBackStack();
        this.invalidateOptionsMenu();
        // Use the current path as the initial directory next time opening the
        // file bowser.
        File tempFile = new File(path);
        if (tempFile.isFile()) {
            mInitDir = tempFile.getParent();
        } else {
            mInitDir = tempFile.getPath();
        }
        //选中某个kml文件之后，开始解析此Kml
        parseKml(path);
    }

    @Override
    public void onDirectoryFinish(String path) {

    }
    private void openFileBrowser() {
        FileBrowserFragment fileFragment = FileBrowserFragment.newInstance();
        fileFragment.setInitialDirectory(mInitDir);
        getSupportFragmentManager().beginTransaction()
                .add(android.R.id.content, fileFragment).addToBackStack(null)
                .commit();
    }
    private void parseKml(String path)
    {
        // 从返回的路径中取出kml文件的文件名
        int lastIndex = path.lastIndexOf("/");
        final String fileName = path.substring(lastIndex + 1, path.length());
       // Log.d(TAG, "parseKml: " + fileName);
        if (path.endsWith(".kml") || path.endsWith(".kmz"))//判断用户选择的是否为kml文件
        {
            readKml = new ReadKml();
            try {
                readKml.parseKml(path);
                if (ReadKml.parseKmlSuccess)
                {
                    addMarkers(fileName, readKml.getCoordinateList());//将kml中的坐标信息转化为marker添加到地图上
                    
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //将本次添加的kml文件名和所有的坐标点保存
                            //saveKmlCoorsToShared(fileName, readKml.getCoordinateList());
                            kmlPrefHelper.saveAddedKmlToShared(fileName, readKml.getCoordinateList());
                            kmlPrefHelper.saveAddedKmlNames(fileName);
                        }
                    }).start();

                }else {
                    ToastUtil.show(MainActivity.this, "解析kml文件失败");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            ToastUtil.show(MainActivity.this, "不支持的文件类型");
        }

    }

    /**
     * 添加marker到地图
     */
    public void addMarkers(String kmlName, List<Coordinate> coorList)
    {
        if (kmlName != null)
        {
            List<Coordinate> marks = null;
            marks = coorList;
            myBaiduMap.setOnMapClickListener(this);
            myBaiduMap.setOnMarkerClickListener(this);
            //将marker添加到地图上
            LatLng desLatLng = null;
            Marker marker = null;
           // 每个marker的textoptions属性
            Text markerText = null;
            OverlayOptions overlayOptions;
            TextOptions textOptions;//文字覆盖物
            //默认的marker图标
            //BitmapDescriptor defaultBitmap = BitmapDescriptorFactory.fromResource(R.drawable.default_marker);
            BitmapDescriptor markerBitmap = null;
            //int changedIconId;
            //将地图视图转移到新添加的marker所在的范围
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (int i = 0; i < marks.size(); i++)
            {
                //将gps坐标转化为百度坐标，kml文件中的坐标为gps坐标
                desLatLng = CoorToBaidu.GPS2Baidu09ll(marks.get(i).getX(), marks.get(i).getY());//getX是纬度，getY是经度
                builder.include(desLatLng);
                //取出更改的iconId
                //changedIconId = marks.get(i).getIconId();
                /*if (changedIconId != 0 && changedIconId != R.drawable.default_marker)
                {
                    Log.d(TAG, "addMarkers: icon id is " + changedIconId);
                    defaultBitmap = BitmapDescriptorFactory.fromResource(changedIconId);
                    //设置marker属性
                    overlayOptions = new MarkerOptions().position(desLatLng).icon(defaultBitmap).zIndex(6);
                }else {
                    //设置marker属性
                    overlayOptions = new MarkerOptions().position(desLatLng).icon(defaultBitmap).zIndex(6);
                }*/
                markerBitmap = BitmapDescriptorFactory.fromResource(marks.get(i).getIconId());
                //设置marker属性
                overlayOptions = new MarkerOptions().position(desLatLng).icon(markerBitmap).zIndex(6);
                marker = (Marker) myBaiduMap.addOverlay(overlayOptions);


                //设置textoptions属性
                textOptions = new TextOptions().text(marks.get(i).getName())
                        .fontSize(26)
                        .position(desLatLng)
                        .align(TextOptions.ALIGN_RIGHT, TextOptions.ALIGN_TOP);
                markerText = (Text) myBaiduMap.addOverlay(textOptions);
                //long_lat要显示在AlternativeParamsActivity和AlterParamsFCMActivity参数设置页面不可采点坐标上，是需要发送给
                //服务器的坐标，故经度在前，纬度在后
                //String long_lat = String.valueOf(marks.get(i).getY()) + "," + String.valueOf(marks.get(i).getX());
                Bundle bundle = new Bundle();
                bundle.putString("markerParentKml", kmlName);//表示当前marker是哪个kml文件的内容
                bundle.putParcelable("marker_bundle", marks.get(i));
                marker.setExtraInfo(bundle);
                markerText.setExtraInfo(bundle);// text
                kmlMarkers.add(marker);
                kmlMarkersText.add(markerText);//text
            }
            //kmlPrefHelper.saveKmlShownState(kmlName, true);//当前样点已经成功添加了marker，将其保存下来,true表示已成功添加marker
            LatLngBounds bounds = builder.build();
            MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLngBounds(bounds);
            myBaiduMap.animateMapStatus(mapStatusUpdate);
        }


    }
    /**
     * 添加服务器返回的可替代样点marker到地图
     */
    public void addAlterMarkers(ArrayList<CoordinateAlterSample> coorList)
    {

        myBaiduMap.setOnMapClickListener(this);
        myBaiduMap.setOnMarkerClickListener(this);
        //将marker添加到地图上
        LatLng sourceLatLng = null;
        LatLng desLatLng = null;
        double latitude = 0.0;
        double longitude = 0.0;
        Marker marker = null;
        String alterSampleName = null;
        OverlayOptions overlayOptions;
        OverlayOptions textOptions;//文字覆盖物
        BitmapDescriptor defaultAlterSampleBitmap = BitmapDescriptorFactory.fromResource(R.drawable.alter_sample_default);
        ArrayList<BitmapDescriptor> bitmapList = new ArrayList<BitmapDescriptor>();
        bitmapList.add(BitmapDescriptorFactory.fromResource(R.drawable.b_poi_1));
        bitmapList.add(BitmapDescriptorFactory.fromResource(R.drawable.b_poi_2));
        bitmapList.add(BitmapDescriptorFactory.fromResource(R.drawable.b_poi_3));
        bitmapList.add(BitmapDescriptorFactory.fromResource(R.drawable.b_poi_4));
        bitmapList.add(BitmapDescriptorFactory.fromResource(R.drawable.b_poi_5));
        bitmapList.add(BitmapDescriptorFactory.fromResource(R.drawable.b_poi_6));
        bitmapList.add(BitmapDescriptorFactory.fromResource(R.drawable.b_poi_7));
        bitmapList.add(BitmapDescriptorFactory.fromResource(R.drawable.b_poi_8));
        bitmapList.add(BitmapDescriptorFactory.fromResource(R.drawable.b_poi_9));
        bitmapList.add(BitmapDescriptorFactory.fromResource(R.drawable.b_poi_10));

        for (int i = 0; i < coorList.size(); i++)
        {
            latitude = coorList.get(i).getY();
            longitude = coorList.get(i).getX();
            //将gps坐标转化为百度坐标,coorList是服务器返回的可替代坐标，getX是经度，getY是纬度
            desLatLng = CoorToBaidu.GPS2Baidu09ll(latitude, longitude);

            alterSampleName = coorList.get(i).getName();
            switch (Integer.parseInt(alterSampleName))
            {
                case 0:
                    //设置marker属性
                    overlayOptions = new MarkerOptions().position(desLatLng).icon(bitmapList.get(0)).zIndex(6);
                    break;
                case 1:
                    overlayOptions = new MarkerOptions().position(desLatLng).icon(bitmapList.get(1)).zIndex(6);
                    break;
                case 2:
                    overlayOptions = new MarkerOptions().position(desLatLng).icon(bitmapList.get(2)).zIndex(6);
                    break;
                case 3:
                    overlayOptions = new MarkerOptions().position(desLatLng).icon(bitmapList.get(3)).zIndex(6);
                    break;
                case 4:
                    overlayOptions = new MarkerOptions().position(desLatLng).icon(bitmapList.get(4)).zIndex(6);
                    break;
                case 5:
                    overlayOptions = new MarkerOptions().position(desLatLng).icon(bitmapList.get(5)).zIndex(6);
                    break;
                case 6:
                    overlayOptions = new MarkerOptions().position(desLatLng).icon(bitmapList.get(6)).zIndex(6);
                    break;
                case 7:
                    overlayOptions = new MarkerOptions().position(desLatLng).icon(bitmapList.get(7)).zIndex(6);
                    break;
                case 8:
                    overlayOptions = new MarkerOptions().position(desLatLng).icon(bitmapList.get(8)).zIndex(6);
                    break;
                case 9:
                    overlayOptions = new MarkerOptions().position(desLatLng).icon(bitmapList.get(9)).zIndex(6);

                    break;
                default:
                    overlayOptions = new MarkerOptions().position(desLatLng).icon(defaultAlterSampleBitmap).zIndex(6);
                    break;
            }

            marker = (Marker) myBaiduMap.addOverlay(overlayOptions);


           /* //设置textoptions属性
            textOptions = new TextOptions().text(alterSampleName)
                    .fontSize(26)
                    .position(desLatLng)
                    .align(TextOptions.ALIGN_RIGHT, TextOptions.ALIGN_TOP);
            myBaiduMap.addOverlay(textOptions);*/

            Bundle bundle = new Bundle();
            bundle.putParcelable("alterMarker_bundle", coorList.get(i));
            marker.setExtraInfo(bundle);
            alterMarkers.add(marker);
        }


        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(desLatLng, 16f);
        myBaiduMap.animateMapStatus(mapStatusUpdate);

    }


    @Override
    public void onMapClick(LatLng latLng) {

        if (markOverviewLayout.getVisibility() == View.VISIBLE)
        {
            markOverviewLayout.setVisibility(View.GONE);
        }
        //如果在SampleInfoActivity中更改了当前marker的图片，当前操作的marker对象不为空，并且，当前操作的marker对象
        //的名称和我们在SampleInfoActivity中操作的marker一致
        if (samplePicChanged != 0 && currentMarker != null) {
            Bundle bundle = currentMarker.getExtraInfo();
            Coordinate markerCoor = bundle.getParcelable("marker_bundle");
            if (currentMarkerName.equals(markerCoor.getName())) {

                BitmapDescriptor changedBitmap = BitmapDescriptorFactory.fromResource(samplePicChanged);
               /* double latitude = markerCoor.getX();
                double longitude = markerCoor.getY();
                LatLng sourceLatlng = new LatLng(latitude, longitude);
                changeMarkerIcon(sourceLatlng, changedBitmap);*/
                currentMarker.setIcon(changedBitmap);

            }

        }
    }

    @Override
    public boolean onMapPoiClick(MapPoi mapPoi) {
        return false;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        currentMarker = marker;
        TextView markerName = (TextView) markOverviewLayout.findViewById(R.id.mark_name);
        TextView markerLatlng = (TextView) markOverviewLayout.findViewById(R.id.mark_latlng);
        TextView markerCost = (TextView) markOverviewLayout.findViewById(R.id.mark_cost);
        LinearLayout markerCostLayout = (LinearLayout) markOverviewLayout.findViewById(R.id.rl_mark_cost);
        double latitude = 0.0;
        double longitude = 0.0;
        double costVaule = 0.0;
        if (kmlMarkers.contains(marker))
        {
            markerFlag = 0;
            Bundle bundle = marker.getExtraInfo();
            Coordinate markerCoor = bundle.getParcelable("marker_bundle");
            latitude = markerCoor.getX();
            longitude = markerCoor.getY();
            markerName.setText(markerCoor.getName());
            markerLatlng.setText(String.valueOf(df.format(latitude)) + "   " + String.valueOf(df.format(longitude)));
            markerCostLayout.setVisibility(View.GONE);
            calcAlterSample.setVisibility(View.VISIBLE);
            markOverviewLayout.setVisibility(View.VISIBLE);
        }else { //服务器返回的可替代样点
            markerFlag = 1;
            Bundle bundle = marker.getExtraInfo();
            CoordinateAlterSample markerCoor = bundle.getParcelable("alterMarker_bundle");
            latitude = markerCoor.getY();//可替代样点坐标，是从服务器返回的，因此getX是经度，getY是纬度
            longitude = markerCoor.getX();
            costVaule = Double.parseDouble(markerCoor.getCostValue());
            int nameTxt = 0;
            try {//从服务器返回的可替代样点的编号是从0开始的，为了显示，让每个编号加1，从1开始显示
                nameTxt = Integer.parseInt(markerCoor.getName()) + 1;
            }catch (Exception e)
            {
                e.printStackTrace();
            }
            markerName.setText(String.valueOf(nameTxt));
            markerLatlng.setText(String.valueOf(df.format(latitude)) + "   " + String.valueOf(df.format(longitude)));
            markerCost.setText(String.valueOf(df.format(costVaule)));

            markerCostLayout.setVisibility(View.VISIBLE);
            calcAlterSample.setVisibility(View.GONE);
            markOverviewLayout.setVisibility(View.VISIBLE);
        }


        return true;
    }
    private void intentToSampleInfoActivity()
    {
        if (currentMarker != null)
        {
            if (markerFlag == 0) //表示是从Kml文件中得来的marker
            {
                Bundle bundle = currentMarker.getExtraInfo();
                Intent intentToSampleInfo = new Intent(MainActivity.this, SampleInfoActivity.class);
                intentToSampleInfo.putExtra("markerInfoBundle", bundle);
                //intentToSampleInfo.putExtra("markerIconId",samplePicChanged);
                startActivityForResult(intentToSampleInfo, 0);//requestCode我们设置为0
            }
            if (markerFlag == 1)
            {
                Bundle bundle = currentMarker.getExtraInfo();
                Intent intentToAlterSampleInfo = new Intent(MainActivity.this, AlterSampleInfoActivity.class);
                intentToAlterSampleInfo.putExtra("alterMarkerInfoBundle", bundle);
                startActivity(intentToAlterSampleInfo);
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode)
        {
            case 0://接收从SampleInfoActivity返回的参数
                if (resultCode == RESULT_OK) {
                    samplePicChanged = data.getIntExtra("SelectedImage", -1);//在SampleInfoActivity中选择的图片id
                    currentMarkerName = data.getStringExtra("currentMarkerName");//在SampleInfoActivity中选择的图片名称
                }
                break;
            default:
                break;
        }
    }

    /**
     * 初始化导航
     */
    private void initNavi() {

        BNOuterTTSPlayerCallback ttsCallback = null;

        BaiduNaviManager.getInstance().init(this, Environment.getExternalStorageDirectory().toString(), SDFileHelper.APP_FOLDER_NAME,
                new BaiduNaviManager.NaviInitListener() {
            @Override
            public void onAuthResult(int status, String msg) {
                if (0 == status) {
                    authinfo = "key校验成功!";
                } else {
                    authinfo = "key校验失败, " + msg;
                }
                MainActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        //Toast.makeText(MainActivity.this, authinfo, Toast.LENGTH_LONG).show();
                    }
                });
            }

            public void initSuccess() {
                //Toast.makeText(MainActivity.this, "百度导航引擎初始化成功", Toast.LENGTH_SHORT).show();
                initSetting();
                initBaiduNaviSuccess = 1;
            }

            public void initStart() {
                //Toast.makeText(MainActivity.this, "百度导航引擎初始化开始", Toast.LENGTH_SHORT).show();
            }

            public void initFailed() {
                Toast.makeText(MainActivity.this, "百度导航引擎初始化失败", Toast.LENGTH_SHORT).show();
            }


        },  null, ttsHandler, null);

    }

    /**
     * 百度导航的相关设置
     */
    private void initSetting(){
        BNaviSettingManager.setDayNightMode(BNaviSettingManager.DayNightMode.DAY_NIGHT_MODE_DAY);
        BNaviSettingManager.setShowTotalRoadConditionBar(BNaviSettingManager.PreViewRoadCondition.ROAD_CONDITION_BAR_SHOW_ON);
        BNaviSettingManager.setVoiceMode(BNaviSettingManager.VoiceMode.Veteran);
        BNaviSettingManager.setPowerSaveMode(BNaviSettingManager.PowerSaveMode.DISABLE_MODE);
        BNaviSettingManager.setRealRoadCondition(BNaviSettingManager.RealRoadCondition.NAVI_ITS_ON);
    }
    /**
     * 内部TTS播报状态回传handler
     */
    private Handler ttsHandler = new Handler() {
        public void handleMessage(Message msg) {
            int type = msg.what;
            switch (type) {
                case BaiduNaviManager.TTSPlayMsgType.PLAY_START_MSG: {

                    //ToastUtil.show(MainActivity.this, "Handler : TTS play start");
                    break;
                }
                case BaiduNaviManager.TTSPlayMsgType.PLAY_END_MSG: {

                    //ToastUtil.show(MainActivity.this, "Handler : TTS play end");
                    break;
                }
                default :
                    break;
            }
        }
    };
    private void routeplanToNavi() {
        BNRoutePlanNode.CoordinateType coType = BNRoutePlanNode.CoordinateType.BD09LL;
        BNRoutePlanNode sNode = null;
        BNRoutePlanNode eNode = null;
        LatLng naviBeginLatLng = new LatLng(latitude, longtitude);//导航起点，即定位位置的坐标
        LatLng naviDestLatLng = null;
        if (currentMarker != null)
        {
            if (markerFlag == 0) //表示是从Kml文件中得来的marker
            {
                Bundle bundle = currentMarker.getExtraInfo();
                Coordinate markerCoor = bundle.getParcelable("marker_bundle");

                // kml文件中的坐标为导航目的地坐标，需要将其从gps转为百度坐标
                naviDestLatLng = CoorToBaidu.GPS2Baidu09ll(markerCoor.getX(), markerCoor.getY());
            }
            if (markerFlag == 1)
            {
                Bundle bundle = currentMarker.getExtraInfo();
                CoordinateAlterSample markerCoor = bundle.getParcelable("alterMarker_bundle");
                // 可替代样点的坐标为导航目的地坐标，需要将其从gps转为百度坐标，注意getY是纬度
                naviDestLatLng = CoorToBaidu.GPS2Baidu09ll(markerCoor.getY(), markerCoor.getX());//getY是纬度，getX是经度
            }
        }
        sNode = new BNRoutePlanNode(naviBeginLatLng.longitude, naviBeginLatLng.latitude, "百度大厦", null, coType);
        eNode = new BNRoutePlanNode(naviDestLatLng.longitude, naviDestLatLng.latitude, "北京天安门", null, coType);

        if (sNode != null && eNode != null) {
            List<BNRoutePlanNode> list = new ArrayList<BNRoutePlanNode>();
            list.add(sNode);
            list.add(eNode);
            BaiduNaviManager.getInstance().launchNavigator(this, list, 1, true, new DemoRoutePlanListener(sNode));
        }
    }
    public class DemoRoutePlanListener implements com.baidu.navisdk.adapter.BaiduNaviManager.RoutePlanListener {

        private BNRoutePlanNode mBNRoutePlanNode = null;

        public DemoRoutePlanListener(BNRoutePlanNode node) {
            mBNRoutePlanNode = node;
        }

        @Override
        public void onJumpToNavigator() {
			/*
			 * 设置途径点以及resetEndNode会回调该接口
			 */

            for (Activity ac : activityList) {

                if (ac.getClass().getName().endsWith("BNDemoGuideActivity")) {

                    return;
                }
            }
            Intent intent = new Intent(MainActivity.this, BNDemoGuideActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(ROUTE_PLAN_NODE, (BNRoutePlanNode) mBNRoutePlanNode);
            intent.putExtras(bundle);
            startActivity(intent);

        }

        @Override
        public void onRoutePlanFailed() {
            // TODO Auto-generated method stub
            Toast.makeText(MainActivity.this, "算路失败", Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * 改变地图类型，普通地图或卫星地图
     */
    private void changeMapType()
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.map_icon)
                .setTitle("请选择地图类型")
                .setItems(types, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String select = types[which];
                        if (select.equals("普通地图")) {
                            myBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                        }else if (select.equals("卫星地图")) {
                            myBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                        }
                    }
                }).show();
    }

    /**
     * 改变定位模式
     */
    private void changeLocationMode()
    {
        AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
        builder2.setIcon(R.drawable.track_collect_running)
                .setTitle("请选择定位模式")
                .setItems(LocationModeString, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String mode = LocationModeString[which];
                        if (mode.equals("罗盘模式")) {
                            MyLocationConfiguration config = new
                                    MyLocationConfiguration(MyLocationConfiguration.LocationMode.COMPASS, true, myLocationBitmap);
                            myBaiduMap.setMyLocationConfigeration(config);
                        }else if (mode.equals("跟随模式")) {
                            MyLocationConfiguration config = new
                                    MyLocationConfiguration(MyLocationConfiguration.LocationMode.FOLLOWING, true, myLocationBitmap);
                            myBaiduMap.setMyLocationConfigeration(config);
                        }else if (mode.equals("普通模式")) {
                            MyLocationConfiguration config = new
                                    MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, myLocationBitmap);
                            myBaiduMap.setMyLocationConfigeration(config);
                        }
                    }
                }).show();
    }

    @Override
    protected void onStart() {//当Activity调用onStart方法，开启定位,开启方向传感器，即将定位的服务、方向传感器和Activity生命周期绑定在一起
        myBaiduMap.setMyLocationEnabled(true);//开启允许定位
        if (!myLocationClient.isStarted()) {
            myLocationClient.start();//开启定位
        }
        //开启方向传感器
        myOrientationListener.start();
        super.onStart();
    }
    @Override
    protected void onStop() {//当Activity调用onStop方法，关闭定位以及关闭方向传感器
        myBaiduMap.setMyLocationEnabled(false);
        myLocationClient.stop();//关闭定位
        myOrientationListener.stop();//关闭方向传感器
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
        initIntentParams();
        showHideDelMarkers();//对加载的Kml文件的操作
        showHideDelAlterMarkers();//对可替代样点的操作
        Log.d(TAG, "onResume: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    /**
     * @param sourceLatlng
     * @param changedBitmap
     * 更改marker图标，暂时没有什么用
     */
    private void changeMarkerIcon(LatLng sourceLatlng, BitmapDescriptor changedBitmap)
    {
        LatLng desLatlng = null;
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.GPS);
        converter.coord(sourceLatlng);
        desLatlng = converter.convert();
        OverlayOptions overlayOptions = new MarkerOptions().position(desLatlng).icon(changedBitmap).zIndex(6);
        myBaiduMap.addOverlay(overlayOptions);
    }

    public boolean createSoilSampleDir()
    {
        SDFileHelper sdFileHelper = SDFileHelper.getInstance(MainActivity.this);
        sdFileHelper.createDirOnSD();
        return true;
    }


    /**
     * 在assets目录下存放着一个kml文件，将此kml文件复制到sd/soilsample目录下
     */
    private void copySampleKmlToSD()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File file = new File(Environment.getExternalStorageDirectory().getCanonicalPath()
                            + "/" + "SoilSample" + "/" + "fcmsamples.kmz");
                    if (! file.exists())
                    {
                        InputStream inputStream = getResources().getAssets().open("fcmsamples.kmz");
                        FileOutputStream outputStream = new FileOutputStream(Environment.getExternalStorageDirectory().getCanonicalPath()
                                + "/" + "SoilSample" + "/" + "fcmsamples.kmz");
                        int temp = 0;
                        byte[] buffer = new byte[1024];
                        while ((temp = inputStream.read(buffer)) != -1)
                        {
                            outputStream.write(buffer, 0, temp);
                        }
                        inputStream.close();
                        outputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void checkForUpdate()
    {

        new Thread(new Runnable() {
            @Override
            public void run() {
                final int versionCode = BuildConfig.VERSION_CODE;
                FIR.checkForUpdateInFIR("0af9a8d6b274efd326d0c5bec3f48bf3", new VersionCheckCallback() {
                    @Override
                    public void onSuccess(String versionJson) {
                        //Log.i("fir","check from fir.im success! " + "\n" + versionJson);
                        Gson gson = new Gson();
                        final FirVersionJson firJson = gson.fromJson(versionJson, FirVersionJson.class);
                        String versionCodeOnFir = firJson.getVersion();
                        //如果fir服务器上的版本号大于本程序的版本号，就提示升级
                        if (Integer.parseInt(versionCodeOnFir) > versionCode)
                        {

                            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                            dialog.setTitle("检测到新版本" +" "+ firJson.getVersionShort());
                            dialog.setMessage(firJson.getChangeLog());
                            dialog.setCancelable(true);
                            dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Uri uri = Uri.parse(firJson.getUpdataUrl());
                                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                    startActivity(intent);
                                }
                            });
                            dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            dialog.show();
                        }
                    }

                    @Override
                    public void onFail(Exception e) {
                        Log.i("fir", "check fir.im fail! " + "\n" + e.getMessage());

                    }

                    @Override
                    public void onStart() {
                        //Toast.makeText(getApplicationContext(), "正在获取", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFinish() {
                        //Toast.makeText(getApplicationContext(), "获取完成", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();

    }

    /**
     * MainActivity在第一次创建的时候，显示已经保存过的marker
     */
    private void showMarkersOnCreate()
    {
        SharedPreferences preferences = getSharedPreferences("showHideDelete_pref", MODE_PRIVATE);
        String selectKmlName = preferences.getString("selectKmlName", null);
        int dealFlag = preferences.getInt(selectKmlName, 0);
        currentMarkerState = getCurrentMarkerState(selectKmlName);//获取保存在本地的markerState
        if (currentMarkerState == SAVE_MARKERS || currentMarkerState == SHOW_MARKERS || currentMarkerState == HIDE_MARKERS)
        {
            int count = 0;//计数器，只要计数器大于0，说明当前要显示的kml文件已经在地图上显示了，不用再重新添加了
            for (int i = 0; i < kmlMarkers.size(); i++) {
                Marker marker = kmlMarkers.get(i);
                Bundle bundle = marker.getExtraInfo();
                String markerParentKml = bundle.getString("markerParentKml");
                if (markerParentKml.equals(selectKmlName))
                {
                    count++;
                }
            }

            //如果将要显示的kml没有在地图上显示，且此kml（包括本地的shared文件）没有被删除
            if (selectKmlName != null && count < 1)
            {
                //取出要显示的Kml保存在本地的坐标
                // 下面这两句话可以直接作为全局变量来使用，不必写在这里
                List<Coordinate> coorList = null;
                coorList = kmlPrefHelper.getAddedKmlFromShared(selectKmlName);
                if (coorList != null)
                {

                    addMarkers(selectKmlName, coorList);
                }
            }
        }
    }

    /**
     * MainActivity在第一次创建的时候，显示已经保存过的alter marker
     */
    private void showAlterMarkersOnCreate()
    {
        SharedPreferences preferences = getSharedPreferences("showHideDeleteAlter_pref", MODE_PRIVATE);
        String selectName = preferences.getString("selectName", null);
        int dealFlag = preferences.getInt(selectName, 0);
        currentAlterMarkerState = getCurrentAlterMarkerState(selectName);//获取保存在本地的markerState
        //获取可替代样点的名称，如样点554不可采，我们计算出554的一批可替代样点，并将这批可替代样点显示在地图上，这批可替代样点的名称就是554
        String alterName = kmlPrefHelper.getAlterMarkerNameFromPref();
        if (currentAlterMarkerState == SAVE_MARKERS || currentAlterMarkerState == SHOW_MARKERS || currentAlterMarkerState == HIDE_MARKERS)
        {
            //如果将要显示的kml没有在地图上显示，且此kml（包括本地的shared文件）没有被删除
            if (selectName.equals(alterName))
            {
                //取出要显示的可替代样点保存在本地的坐标
                // 下面这两句话可以直接作为全局变量来使用
                ArrayList<CoordinateAlterSample> coorList = null;
                coorList = kmlPrefHelper.getAlterCoorsFromPref(selectName);
                if (coorList != null)
                {
                    addAlterMarkers(coorList);
                }
            }
        }
    }
    /**
     * 隐藏，显示，删除kml及其marker，对应FavoriteActivity中的showHideDeleteKml方法
     * 目前只支持对一个kml文件进行操作，2016/07/05
     */
    private void showHideDelMarkers()
    {
        SharedPreferences preferences = getSharedPreferences("showHideDelete_pref", MODE_PRIVATE);
        String selectKmlName = preferences.getString("selectKmlName", null);
        int dealFlag = preferences.getInt(selectKmlName, 0);
        currentMarkerState = getCurrentMarkerState(selectKmlName);//获取保存在本地的markerState

        switch (dealFlag)
        {
            case HIDE_MARKERS://隐藏marker
                if (kmlMarkers != null )
                {
                    for (int i = 0; i < kmlMarkers.size(); i++) {
                        Marker marker = kmlMarkers.get(i);
                        Text markerText = kmlMarkersText.get(i);
                        Bundle bundle = marker.getExtraInfo();
                        String markerParentKml = bundle.getString("markerParentKml");
                        if (markerParentKml.equals(selectKmlName) && marker.isVisible() && markerText.isVisible())
                        //if (markerParentKml.equals(selectKmlName) && currentMarkerState != HIDE_MARKERS)
                        {
                            marker.setVisible(false);
                            markerText.setVisible(false);

                        }
                    }
                    currentMarkerState = HIDE_MARKERS;
                    saveCurrentMarkerState(selectKmlName, currentMarkerState);
                }
                break;
            case SHOW_MARKERS://显示
                if (currentMarkerState == HIDE_MARKERS)//如果当前marker的状态是被隐藏的状态，直接设置visible即可
                {
                    //如果用户将kml设为隐藏状态，然后退出程序，再打开程序，点击显示，此时的kmlMarkers可能为空，这是一个问题
                    if (kmlMarkers != null)
                    {
                        for (int i = 0; i < kmlMarkers.size(); i++) {
                            Marker marker = kmlMarkers.get(i);
                            Text markerText = kmlMarkersText.get(i);
                            Bundle bundle = marker.getExtraInfo();
                            String markerParentKml = bundle.getString("markerParentKml");
                            if (markerParentKml.equals(selectKmlName))
                            {
                                marker.setVisible(true);
                                markerText.setVisible(true);
                            }
                        }
                    }

                    currentMarkerState = SHOW_MARKERS;
                    saveCurrentMarkerState(selectKmlName, currentMarkerState);
                }
                break;
            case DELETE_MARKERS:
                deleteMarkersAndPref(selectKmlName);
                currentMarkerState = MARKER_NO_EDIT;
                saveCurrentMarkerState(selectKmlName, currentMarkerState);
                break;
            case SAVE_MARKERS:
                currentMarkerState = SAVE_MARKERS;
                saveCurrentMarkerState(selectKmlName, currentMarkerState);
                break;
            default:
                break;
        }
    }
    /**
     * 隐藏，显示，删除altermarker，对应FavoriteActivity中的showHideDeleteAlter方法
     * 目前只支持对一个kml文件进行操作，2016/07/05
     */
    private void showHideDelAlterMarkers()
    {
        SharedPreferences preferences = getSharedPreferences("showHideDeleteAlter_pref", MODE_PRIVATE);
        String selectName = preferences.getString("selectName", null);
        int dealFlag = preferences.getInt(selectName, 0);
        currentAlterMarkerState = getCurrentAlterMarkerState(selectName);//获取保存在本地的markerState
        //获取可替代样点的名称，如样点554不可采，我们计算出554的一批可替代样点，并将这批可替代样点显示在地图上，这批可替代样点的名称就是554
        String alterName = kmlPrefHelper.getAlterMarkerNameFromPref();
        switch (dealFlag)
        {
            case HIDE_MARKERS://隐藏marker
                // 隐藏可替代样点
                if (alterMarkers != null && selectName.equals(alterName))
                {
                    for (int i = 0; i < alterMarkers.size(); i++) {
                        Marker marker = alterMarkers.get(i);
                        if (marker.isVisible())
                        {
                            marker.setVisible(false);

                        }
                    }
                    currentAlterMarkerState = HIDE_MARKERS;
                    saveCurrentAlterMarkerState(selectName, currentAlterMarkerState);
                }

                break;
            case SHOW_MARKERS://显示
                if (currentAlterMarkerState == HIDE_MARKERS)//如果当前marker的状态是被隐藏的状态，直接设置visible即可
                {
                    //如果用户将kml设为隐藏状态，然后退出程序，再打开程序，点击显示，此时的kmlMarkers可能为空，这是一个问题
                    if (alterMarkers != null && selectName.equals(alterName))
                    {
                        for (int i = 0; i < alterMarkers.size(); i++) {
                            Marker marker = alterMarkers.get(i);
                            marker.setVisible(true);
                        }
                    }

                    currentAlterMarkerState = SHOW_MARKERS;
                    saveCurrentAlterMarkerState(selectName, currentAlterMarkerState);
                }
                break;
            case DELETE_MARKERS:
                if (selectName.equals(alterName))
                {

                    deleteAlterMarkersAndPref(selectName);
                    currentAlterMarkerState = MARKER_NO_EDIT;
                    saveCurrentAlterMarkerState(selectName, currentAlterMarkerState);
                }
                break;
            case SAVE_MARKERS:
                currentAlterMarkerState = SAVE_MARKERS;
                saveCurrentAlterMarkerState(selectName, currentAlterMarkerState);
                break;
            default:
                break;
        }
    }


    /**
     * @param deleteKml
     * 删除此kml保存在本地的pref数据，删除此kml的marker
     */
    private void deleteMarkersAndPref(String deleteKml)
    {
        kmlPrefHelper.updateAddedKmlNames(deleteKml);//删除此kml在AddedKmlNames中的记录
        kmlPrefHelper.clearAddedKmlFromShared(deleteKml);//删除此kml保存在本地的数据
        kmlPrefHelper.deleteCertainKmlShared(deleteKml);//删除保存在本地的shared文件
        if (kmlMarkers != null)
        {

            for (int i = 0; i < kmlMarkers.size(); i++) {
                Marker marker = kmlMarkers.get(i);
                Text markerText = kmlMarkersText.get(i);
                Bundle bundle = marker.getExtraInfo();
                String markerParentKml = bundle.getString("markerParentKml");
                if (markerParentKml.equals(deleteKml))
                {
                    marker.remove();
                    markerText.remove();
                }
            }
        }
        // 删除之后将FavoriteActivity中showHideDeleteKml方法中的showHideDelete_pref的flag改为MARKER_NO_EDIT状态，
        //因为若不这样做，如，在收藏页面删除了4thalter.kmz之后，将其保存为：4thalter.kmz---DELETE_MARKER,在MainActivity中，onResume方法会经常
        //执行的，当你再添加4thalter.kmz到地图上之后，在执行onResume方法时程序读取到他的状态是DELETE_MARKER，就会立即执行deleteMarkersAndPref
        //方法，将此kmz删除
        SharedPreferences.Editor editor = getSharedPreferences("showHideDelete_pref", MODE_PRIVATE).edit();
        editor.putString("selectKmlName", deleteKml);
        editor.putInt(deleteKml, MARKER_NO_EDIT);
        editor.commit();

    }
    /**
     * @param deleteKml
     * 删除alter marker其显示在地图上的marker
     */
    private void deleteAlterMarkersAndPref(String deleteKml)
    {
        kmlPrefHelper.clearAlterCoorsFromPref();//清空此kml保存在本地的数据
        //kmlPrefHelper.deleteCertainKmlShared("AlterSamplesList");//删除保存在本地的shared文件
        //kmlPrefHelper.removeMarkerNameFromPref();
        if (alterMarkers != null)
        {
            for (int i = 0; i < alterMarkers.size(); i++) {
                Marker marker = alterMarkers.get(i);
                marker.remove();
            }
            alterMarkers.clear();
        }
        //删除此marker的可替代样点之后，将其置为MARKER_NO_EDIT状态
        SharedPreferences.Editor editor = getSharedPreferences("showHideDeleteAlter_pref", MODE_PRIVATE).edit();
        editor.putString("selectName", deleteKml);
        editor.putInt(deleteKml, MARKER_NO_EDIT);
        editor.commit();

    }

    /**
     * @param markerState
     * 将marker的当前状态保存到本地，显示，隐藏，删除
     */
    private void saveCurrentMarkerState(String kmlName, int markerState)
    {
        SharedPreferences.Editor editor = getSharedPreferences("saveMarkerState_pref", MODE_PRIVATE).edit();
        editor.putInt(kmlName, markerState);

        editor.commit();
    }
    private int getCurrentMarkerState(String kmlName)
    {
        int markerState = 0;
        SharedPreferences preferences = getSharedPreferences("saveMarkerState_pref", MODE_PRIVATE);
        markerState = preferences.getInt(kmlName, 0);
        return markerState;
    }
    /**
     * @param markerState
     * 将altermarker的当前状态保存到本地，显示，隐藏，删除
     */
    private void saveCurrentAlterMarkerState(String markerName, int markerState)
    {
        SharedPreferences.Editor editor = getSharedPreferences("saveAlterMarkerState_pref", MODE_PRIVATE).edit();
        editor.putInt(markerName, markerState);

        editor.commit();
    }
    private int getCurrentAlterMarkerState(String markerName)
    {
        int markerState = 0;
        SharedPreferences preferences = getSharedPreferences("saveAlterMarkerState_pref", MODE_PRIVATE);
        markerState = preferences.getInt(markerName, 0);
        return markerState;
    }

    /**
     * @return
     * 从本地读取用户设置的可替代样点的计算方法：相似度或者FCM
     */
    private int getSetInfoFromShared()
    {
        SharedPreferences preferences = getSharedPreferences("SettingInfo", MODE_PRIVATE);
        int sampleModel = preferences.getInt("altersampleModel", 0);
        return sampleModel;
    }
    @Override
    public void onBackPressed() {
        //当drawerLayout处于展开状态时，点击返回键，关闭drawerlayout，而不是退出程序
        //GravityCompat.START, Push object to x-axis position at the start of its container, not changing its size
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.id_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
//    @Override
//    public void onBackPressed() {
//        if (doubleBackToExitOnce)
//        {
//            super.onBackPressed();
//            ActivityCollector.finishAll();
//            return;
//        }
//        this.doubleBackToExitOnce = true;
//        ToastUtil.show(MainActivity.this, "再按一次退出程序");
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                doubleBackToExitOnce = false;
//            }
//        }, 2000);
//    }
}
