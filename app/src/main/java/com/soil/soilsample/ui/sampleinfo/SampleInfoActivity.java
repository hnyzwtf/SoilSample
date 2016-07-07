package com.soil.soilsample.ui.sampleinfo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.soil.soilsample.R;
import com.soil.soilsample.base.BaseActivity;
import com.soil.soilsample.model.Coordinate;
import com.soil.soilsample.model.CoordinateAlterSample;
import com.soil.soilsample.support.kml.KmlSharedPrefHelper;
import com.soil.soilsample.support.kml.WriteKml;
import com.soil.soilsample.support.util.ToastUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by GIS on 2016/6/13 0013.
 */
public class SampleInfoActivity extends BaseActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private LinearLayout exportAlterSamples;
    private LinearLayout calcAlterSample;
    private LinearLayout sampleHtmlCommentBtn;
    private ImageButton samplePicBtn;

    private EditText sampleNameText;
    private EditText sampleLatitudeText;
    private EditText sampleLongitudeText;

    private String markerParentKml;//当前操作的marker所属的kml文件名
    private Boolean isSampleImageChanged = false;//设置一个标识，判断该marker的图标是否被改变
    private Bundle intentBundle;
    private String markerName = "";//声明样点marker的name属性，从intent中获取
    private String markerHtmlComment = "";//声明样点marker的html属性，从intent中获取
    private String markerLat;
    private String markerLng;
    private int imageSelected = 0;//选中的marker图片，从SamplePicSelectActivity传入
    private int markerIconId = 0;//从intentFromMainActivity传入的marker的图标

    public static int altersampleModel = 0;
    private static final String TAG = "SampleInfoActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_info);
        initView();
        initIntentParams();
        initEvents();
        initEditText();
        initSamplePicBtnImage();
        altersampleModel = getSetInfoFromShared();//从shared中读取配置信息，在本类中是读取用户选择了什么采样方法
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_white_24dp);
        getSupportActionBar().setTitle("样点详情");
        exportAlterSamples = (LinearLayout) findViewById(R.id.rl_export_altersamples);
        calcAlterSample = (LinearLayout) findViewById(R.id.rl_calc_alterSample);
        samplePicBtn = (ImageButton) findViewById(R.id.imgBtn_sample_pic);
        sampleHtmlCommentBtn = (LinearLayout) findViewById(R.id.rl_sample_htmlComment);
        sampleNameText = (EditText) findViewById(R.id.edit_sample_name);
        sampleLatitudeText = (EditText) findViewById(R.id.edit_sample_lat);
        sampleLongitudeText = (EditText) findViewById(R.id.edit_sample_lng);
    }

    /**
     * 获取从MainActivity中通过intent传递的参数
     */
    private void initIntentParams()
    {
        Intent intentFromMainActivity = getIntent();
        intentBundle = intentFromMainActivity.getBundleExtra("markerInfoBundle");
        markerParentKml = intentBundle.getString("markerParentKml");
        Coordinate markerCoor = intentBundle.getParcelable("marker_bundle");
        markerName = markerCoor.getName();
        double latitude = markerCoor.getX();
        double longitude = markerCoor.getY();
        markerHtmlComment = markerCoor.getHtmlContent();
        markerLat = String.valueOf(latitude);
        markerLng = String.valueOf(longitude);
        //markerIconId = intentFromMainActivity.getIntExtra("markerIconId", -1);//当前正在操作的marker的icon
        markerIconId = markerCoor.getIconId();

    }
    private void initEvents()
    {
        sampleHtmlCommentBtn.setOnClickListener(this);
        samplePicBtn.setOnClickListener(this);
        calcAlterSample.setOnClickListener(this);
        exportAlterSamples.setOnClickListener(this);
    }
    private void initEditText()
    {
        sampleNameText.setText(markerName);
        sampleLatitudeText.setText(markerLat);
        sampleLongitudeText.setText(markerLng);

    }
    private void initSamplePicBtnImage()
    {
       /* if (isSampleImageChanged) {
            samplePicBtn.setImageResource(imageSelected);
        }*/
        if ((markerIconId != 0) && (markerIconId != -1))
        {
            samplePicBtn.setImageResource(markerIconId);
        }
        else {
            samplePicBtn.setImageResource(R.drawable.default_marker);//设置默认的显示图标
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.rl_sample_htmlComment:
                Intent intentToHtmlComment = new Intent(SampleInfoActivity.this, HtmlCommentActivity.class);
                intentToHtmlComment.putExtra("htmlComment", markerHtmlComment);
                startActivity(intentToHtmlComment);
                break;
            case R.id.imgBtn_sample_pic:
                //启动带有返回参数的SamplePicSelectActivity
                Intent intentToSamplePicSelect = new Intent(SampleInfoActivity.this, SamplePicSelectActivity.class);
                startActivityForResult(intentToSamplePicSelect, 1);//使用这个方法可以接收SamplePicSelectActivity返回的数据
                break;
            case R.id.rl_calc_alterSample://计算可替代样点
                if (altersampleModel == 0)
                {
                    String markerLongLat = markerLng + "," + markerLat;
                    Intent intentToAlterOptions = new Intent(SampleInfoActivity.this, AlternativeParamsActivity.class);
                    intentToAlterOptions.putExtra("markerLongLat", markerLongLat);
                    intentToAlterOptions.putExtra("markerName", markerName);
                    startActivity(intentToAlterOptions);

                }
                if (altersampleModel == 1)
                {
                    String markerLongLat = markerLng + "," + markerLat;
                    Intent intentToAlterFCMOptions = new Intent(SampleInfoActivity.this, AlterParamsFCMActivity.class);
                    intentToAlterFCMOptions.putExtra("markerLongLat", markerLongLat);
                    intentToAlterFCMOptions.putExtra("markerName", markerName);
                    startActivity(intentToAlterFCMOptions);
                }
                break;
            case R.id.rl_export_altersamples:
                exportAlterSamples();
                break;
            default:
                break;
        }

    }
    /*
	 * 我们是使用的startActivityForResult方法启动SamplePicSelectActivity的，在它销毁之后会返回数据，因此我们用
	 * onActivityResult接收返回的数据
	 * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1://我们启动SamplePicSelectActivity时设置的requestCode就是1
                if (resultCode == RESULT_OK) {
                    imageSelected = data.getIntExtra("selectedPic", -1);
                    samplePicBtn.setImageResource(imageSelected);
                    isSampleImageChanged = true;
                }
                break;

            default:
                break;
        }

    }
    private int getSetInfoFromShared()
    {
        SharedPreferences preferences = getSharedPreferences("SettingInfo", MODE_PRIVATE);
        int sampleModel = preferences.getInt("altersampleModel", 0);
        return sampleModel;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sample_info, menu);
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
                if (isSampleImageChanged) {//如果改变了当前样点的图标
                    Intent intentForResult = new Intent();
                    //将imageSelected，markerName这两个参数返回给MainActivity
                    intentForResult.putExtra("SelectedImage", imageSelected);
                    intentForResult.putExtra("currentMarkerName", markerName);
                    //将改变的图标及对应的marker写入本地
                    KmlSharedPrefHelper kmlPrefHelper = KmlSharedPrefHelper.getInstance(SampleInfoActivity.this);
                    List<Coordinate> coorList = null;
                    coorList = kmlPrefHelper.getAddedKmlFromShared(markerParentKml);

                   // Log.d(TAG, "onOptionsItemSelected: markerParentKml is " + markerParentKml + ", markerName is " + markerName);
                    for (int i = 0; i < coorList.size(); i++) {
                        if (coorList.get(i).getName().equals(markerName))
                        {
                            //Log.d(TAG, "onOptionsItemSelected: coorList.get(i).getName() is " + coorList.get(i).getName());
                            coorList.get(i).setIconId(imageSelected);//存入改变的图片的id
                        }
                    }

                    //将更新后的coorList保存下来
                   // kmlPrefHelper.clearAddedKmlFromShared(markerParentKml);
                    kmlPrefHelper.saveAddedKmlToShared(markerParentKml, coorList);

                    setResult(RESULT_OK, intentForResult);
                    finish();
                }
                break;
            case R.id.action_click_me:
                ToastUtil.show(this, "谁让你点我的！！！");
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private void exportAlterSamples()
    {

        // AlterSamplesList此pref文件对应AlternativeParamsActivity的saveAlterSamplesToSharedPrefer方法
        // 以及AlterParamsFCMActivity中的saveAlterSamplesToSharedPrefer方法
        SharedPreferences preferences = getSharedPreferences("AlterSamplesList", MODE_PRIVATE);
        String json = preferences.getString(markerName, null);
        // AlterFCMSamplesList对应AlterParamsFCMActivity中的saveAlterSamplesToSharedPrefer方法
        /*SharedPreferences preferencesFCM = getSharedPreferences("AlterFCMSamplesList", MODE_PRIVATE);
        String jsonFCM = preferencesFCM.getString(markerName, null);*/

        if (json != null)
        {
            Gson gson = new Gson();
            Type type = new TypeToken<List<CoordinateAlterSample>>(){}.getType();
            //先从本地取出可替代样点
            List<CoordinateAlterSample> alterSamples = null;
            alterSamples = new ArrayList<CoordinateAlterSample>();
            alterSamples = gson.fromJson(json, type);
            //调用WriteKml中的方法，写出Kml
            WriteKml writeKml = new WriteKml();
            try {
                writeKml.createKml( markerName, alterSamples);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        /*else if (jsonFCM != null)
        {
            Gson gson = new Gson();
            Type type = new TypeToken<List<CoordinateAlterSample>>(){}.getType();
            //先从本地取出可替代样点
            List<CoordinateAlterSample> alterSamples = null;
            alterSamples = new ArrayList<CoordinateAlterSample>();
            alterSamples = gson.fromJson(jsonFCM, type);
            //调用WriteKml中的方法，写出Kml
            WriteKml writeKml = new WriteKml();
            try {
                writeKml.createKml( markerName + "_FCM", alterSamples);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
        else
        {
            ToastUtil.show(SampleInfoActivity.this, "亲，你还没有计算可替代样点呢~~");
        }
    }
}
