package com.soil.profile.ui;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.soil.profile.db.SoilNoteDB;
import com.soil.profile.model.BaseProfileModel;
import com.soil.profile.model.CustomChooseMdl.FiveLayerCusMdl;
import com.soil.profile.model.CustomChooseMdl.FourLayerCusMdl;
import com.soil.profile.model.CustomChooseMdl.SevenLayerCusMdl;
import com.soil.profile.model.CustomChooseMdl.SixLayerCusMdl;
import com.soil.profile.model.CustomChooseMdl.ThreeLayerCusMdl;
import com.soil.profile.model.CustomDrawMdl.CustomDrawMdl;
import com.soil.profile.model.DatabaseMdl.InfoGeo;
import com.soil.profile.model.DatabaseMdl.ProfilePhoto;
import com.soil.profile.model.InitProfileModel.MontainBrownSoil;
import com.soil.profile.model.InitProfileModel.MontainYelloBrownSoil;
import com.soil.profile.model.InitProfileModel.MontainYelloSoilOne;
import com.soil.profile.model.InitProfileModel.MontainYelloSoilThree;
import com.soil.profile.model.InitProfileModel.MontainYelloSoilTwo;
import com.soil.profile.utils.BitmapUtils;
import com.soil.profile.utils.DrawLegend;
import com.soil.profile.utils.UrlAndFile;
import com.soil.soilsample.R;
import com.soil.soilsample.base.BaseActivity;
import com.soil.soilsample.support.util.ToastUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressLint({ "NewApi", "ShowToast" })
public class ActivityEditPhoto extends BaseActivity implements OnClickListener, OnItemSelectedListener{

    // 定位相关（使用百度地图定位功能，不使用Android自带的locationManager）
    private LocationClient mLocationClient;
    private MyLocationListener myLocationListener;
    private double mLatitude, mLongtitude;
    public static String humanPosition;

    // 数据库相关
    private SoilNoteDB soilNoteDB;

    //主页的一些控件
    private Toolbar toolbar;
    private LinearLayout bottomToolbar;
    private ImageView img;
    private FrameLayout frameLayout;
    //选择现有剖面图，自定义剖面图，手绘土壤剖面图
    private Button chsMdlBn, cusChsBn, cusDrwBn;

    //模板部分的控件
    //点击选择现有剖面图之后出现的底部工具栏，选择山地黄壤1，2，3等5个图片的布局，点击自定义剖面图之后的底部工具栏
    private LinearLayout tmpChsMdlbar, tmpMdlsbar, tmpCusChsMdlTool;
    private RelativeLayout tmpCusChsMdlsbar;

    //选择模板部分的菜单按钮
    //山地黄壤1，山地黄壤2，山地黄壤3，山地黄棕壤，山地棕壤，
    private Button mdl1, mdl2, mdl3, mdl4, mdl5, cancelBn, okBn, chsBn;

    //自定义模板部分的控件
    //7个spinner
    private Spinner oneSp, twoSp, threeSp, fourSp, fiveSp, sixSp, sevenSp;
    // 自定义剖面, 自定义剖面图左侧spinner工具栏输入层数的确定，自定义剖面图底部工具栏的确定和取消，
    private Button cusChsMdlBn, cfmChsBn, okCusBn, cancelCusBn;
    private ImageView leftBn;//自定义剖面图输入层数的向左收起按钮
    private LinearLayout cusmdlSpinnerLy;//自定义剖面图层数选择的spinner下拉选择框布局
    private RelativeLayout cusmdlInputDepth, cusmdlInputName;//自定义剖面图，请输入高度和名称的布局
    private boolean stat = true;
    //请输入高度，第一层--第7层；请输入层名，第一层---第7层；
    private EditText depthOne, depthTwo, depthThree, depthFour, depthFive, depthSix, depthSeven,
            nameOne, nameTwo, nameThree, nameFour, nameFive, nameSix, nameSeven;

    //手绘部分的按钮
    private Button undoBn, clearBn, okdrawBn, canceldrawBn;
    private LinearLayout drawbar;
    private CustomDrawMdl cusDrawMdl;

    private Bitmap tmpBm;
    private Bitmap initBm;
    private int type;
    private BaseProfileModel basePro;
    private MontainYelloSoilOne one;
    private MontainYelloSoilTwo two;
    private MontainYelloSoilThree three;
    private MontainYelloBrownSoil four;
    private MontainBrownSoil five;
    private ThreeLayerCusMdl threeLyr;
    private FourLayerCusMdl fourLyr;
    private FiveLayerCusMdl fiveLyr;
    private SixLayerCusMdl sixLyr;
    private SevenLayerCusMdl sevenLyr;

    private final int NO_EDIT = 0;
    private final int CHOOSE_MODEL = 1;
    private final int CUSCHOOSE_MODEL = 2;
    private final int CUSTOM_DRAW = 3;
    public static int saveFlag;

    private SimpleAdapter adapter;

    private int[] proLegend = {0, 0, 0, 0, 0, 0, 0};
    private float[] depthLines = {0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f};
    private float sumDepth;
    private String[] layerName = {"", "", "", "", "", "", ""};
    public static ArrayList<Map<String, Object>> itemList;

    private String imageFilePath;//加载时的图片路径
    private String imageSaveFilePath; //保存时的图片路径

    public static int layerNum = 0;
    public static List<Map<String, Object>> infoList;
    private float length;
    private static final String TAG = "ActivityEditPhoto";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_photo);
        Intent intent = getIntent();
        imageFilePath = intent.getStringExtra("imageFilePath");
        imageSaveFilePath = UrlAndFile.getSaveFilePath();
        soilNoteDB = SoilNoteDB.getInstance(this);
        initView();
        initModelView();
        initCusModelView();
        initDrawView();
        initLocation();
    }

    private void initCusModelView() {
        cusChsMdlBn = (Button) findViewById(R.id.tmp_id_menu_cuschs_mdl);
        cfmChsBn = (Button) findViewById(R.id.tmp_id_cfm_chs);
        okCusBn = (Button) findViewById(R.id.tmp_id_cus_ok_mdl);
        cancelCusBn = (Button) findViewById(R.id.tmp_id_cus_cancel_mdl);
        leftBn = (ImageView) findViewById(R.id.id_left);
        cusmdlSpinnerLy = (LinearLayout) findViewById(R.id.tmp_id_lyt_cusmdl_spinner);
        cusmdlInputName = (RelativeLayout) findViewById(R.id.tmp_id_input_name_ly);
        cusmdlInputDepth = (RelativeLayout) findViewById(R.id.tmp_id_input_depth_ly);

        nameOne = (EditText) findViewById(R.id.tmp_id_input_name_one);
        nameTwo = (EditText) findViewById(R.id.tmp_id_input_name_two);
        nameThree = (EditText) findViewById(R.id.tmp_id_input_name_three);
        nameFour = (EditText) findViewById(R.id.tmp_id_input_name_four);
        nameFive = (EditText) findViewById(R.id.tmp_id_input_name_five);
        nameSix = (EditText) findViewById(R.id.tmp_id_input_name_six);
        nameSeven = (EditText) findViewById(R.id.tmp_id_input_name_seven);

        depthOne = (EditText) findViewById(R.id.tmp_id_input_depth_one);
        depthTwo = (EditText) findViewById(R.id.tmp_id_input_depth_two);
        depthThree = (EditText) findViewById(R.id.tmp_id_input_depth_three);
        depthFour = (EditText) findViewById(R.id.tmp_id_input_depth_four);
        depthFive = (EditText) findViewById(R.id.tmp_id_input_depth_five);
        depthSix = (EditText) findViewById(R.id.tmp_id_input_depth_six);
        depthSeven = (EditText) findViewById(R.id.tmp_id_input_depth_seven);

        oneSp = (Spinner) findViewById(R.id.tmp_id_one_lyr);
        twoSp = (Spinner) findViewById(R.id.tmp_id_two_lyr);
        threeSp = (Spinner) findViewById(R.id.tmp_id_three_lyr);
        fourSp = (Spinner) findViewById(R.id.tmp_id_four_lyr);
        fiveSp = (Spinner) findViewById(R.id.tmp_id_five_lyr);
        sixSp = (Spinner) findViewById(R.id.tmp_id_six_lyr);
        sevenSp = (Spinner) findViewById(R.id.tmp_id_seven_lyr);

        cfmChsBn.setOnClickListener(this);
        cusChsMdlBn.setOnClickListener(this);
        okCusBn.setOnClickListener(this);
        cancelCusBn.setOnClickListener(this);
        leftBn.setOnClickListener(this);

//		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getData());
        adapter = new SimpleAdapter(this, getData(), R.layout.item_cus_chs_mdl,
                new String[]{"pic"}, new int[]{R.id.id_cus_item_pic});
        //3、adapter设置一个下拉列表样式(点击spinner后的下拉菜单的样式)
        adapter.setDropDownViewResource(R.layout.item_cus_chs_mdl);
        //4、spinner加载适配器
        oneSp.setAdapter(adapter);
        twoSp.setAdapter(adapter);
        threeSp.setAdapter(adapter);
        fourSp.setAdapter(adapter);
        fiveSp.setAdapter(adapter);
        sixSp.setAdapter(adapter);
        sevenSp.setAdapter(adapter);

        //5、spinner设置监听器
        oneSp.setOnItemSelectedListener(this);
        twoSp.setOnItemSelectedListener(this);
        threeSp.setOnItemSelectedListener(this);
        fourSp.setOnItemSelectedListener(this);
        fiveSp.setOnItemSelectedListener(this);
        sixSp.setOnItemSelectedListener(this);
        sevenSp.setOnItemSelectedListener(this);
    }

    private void initModelView() {
        mdl1 = (Button) findViewById(R.id.tmp_id_mdl_1);
        mdl2 = (Button) findViewById(R.id.tmp_id_mdl_2);
        mdl3 = (Button) findViewById(R.id.tmp_id_mdl_3);
        mdl4 = (Button) findViewById(R.id.tmp_id_mdl_4);
        mdl5 = (Button) findViewById(R.id.tmp_id_mdl_5);
        cancelBn = (Button) findViewById(R.id.tmp_id_cancel_mdl);
        okBn = (Button) findViewById(R.id.tmp_id_ok_mdl);
        chsBn = (Button) findViewById(R.id.tmp_id_menu_chs_mdl);

        mdl1.setOnClickListener(this);
        mdl2.setOnClickListener(this);
        mdl3.setOnClickListener(this);
        mdl4.setOnClickListener(this);
        mdl5.setOnClickListener(this);
        cancelBn.setOnClickListener(this);
        okBn.setOnClickListener(this);
    }

    private void initDrawView(){
        drawbar = (LinearLayout) findViewById(R.id.tmp_cusdr_bar);
        undoBn = (Button) findViewById(R.id.tmp_id_undo_cus_draw);
        clearBn = (Button) findViewById(R.id.tmp_id_clear_cus_draw);
        okdrawBn = (Button) findViewById(R.id.tmp_id_ok_cus_draw);
        canceldrawBn = (Button) findViewById(R.id.tmp_id_cancel_cus_draw);

        undoBn.setOnClickListener(this);
        clearBn.setOnClickListener(this);
        okdrawBn.setOnClickListener(this);
        canceldrawBn.setOnClickListener(this);
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_white_24dp);
        getSupportActionBar().setTitle("剖面分层");
        bottomToolbar = (LinearLayout) findViewById(R.id.toolbar_layout);

        frameLayout = (FrameLayout) findViewById(R.id.frame_layout);
        chsMdlBn = (Button) findViewById(R.id.id_chs_mdl);
        cusChsBn = (Button) findViewById(R.id.id_cus_chs_mdl);
        cusDrwBn = (Button) findViewById(R.id.id_custom_draw);

        tmpChsMdlbar = (LinearLayout) findViewById(R.id.tmp_chsmdl_tool);
        tmpCusChsMdlsbar = (RelativeLayout) findViewById(R.id.tmp_cuschsmdl_part);
        tmpCusChsMdlTool = (LinearLayout) findViewById(R.id.tmp_cuschsmdl_tool);
        tmpMdlsbar = (LinearLayout) findViewById(R.id.tmp_mdls_part);

        img = (ImageView) findViewById(R.id.id_img_edit_photo);
        showImageView(img);
        saveFlag = NO_EDIT;

        chsMdlBn.setOnClickListener(this);
        cusChsBn.setOnClickListener(this);
        cusDrwBn.setOnClickListener(this);

    }

    private void showImageView(final ImageView image) {
        ViewTreeObserver vto = image.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                image.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                //TODO 判断id是否相同
                initBm = BitmapUtils.decodeSampledBitmapFromFile(imageFilePath, image.getWidth(),image.getHeight());
                //把图片设为背景
                Drawable drawable = new BitmapDrawable(initBm);
                image.setBackground(drawable);
            }
        });
    }

    @SuppressLint("NewApi")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_chs_mdl:// 选择现有剖面图
                bottomToolbar.setVisibility(View.GONE);
                toolbar.setVisibility(View.GONE);
                img.setVisibility(View.VISIBLE);
                frameLayout.setVisibility(View.GONE);
                tmpChsMdlbar.setVisibility(View.VISIBLE);
                tmpMdlsbar.setVisibility(View.VISIBLE);
                saveFlag = NO_EDIT;
                break;
            case R.id.id_cus_chs_mdl:// 自定义剖面图
                bottomToolbar.setVisibility(View.GONE);
                toolbar.setVisibility(View.GONE);
                img.setVisibility(View.VISIBLE);
                frameLayout.setVisibility(View.GONE);
                tmpCusChsMdlTool.setVisibility(View.VISIBLE);
                tmpCusChsMdlsbar.setVisibility(View.VISIBLE);
                AnimationSet animationSet = new AnimationSet(true);
                TranslateAnimation translateAnimation = new TranslateAnimation( Animation.RELATIVE_TO_PARENT, -140/720.0f, Animation.RELATIVE_TO_PARENT,
                        0f, Animation.RELATIVE_TO_PARENT, 0f, Animation.RELATIVE_TO_PARENT, 0f);
                translateAnimation.setDuration(300);
                animationSet.addAnimation(translateAnimation);
                tmpCusChsMdlsbar.startAnimation(animationSet);
                saveFlag = CHOOSE_MODEL;
                stat = true;
                break;
            case R.id.id_custom_draw:// 手绘土壤剖面图
                img.setVisibility(View.GONE);
                frameLayout.setVisibility(View.VISIBLE);
                frameLayout.removeAllViews();
                cusDrawMdl = new CustomDrawMdl(this);
                cusDrawMdl.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                cusDrawMdl.setImageResource(R.drawable.transparent_backgroud_frame);
                cusDrawMdl.setTag("cusDraw");
                showImageView(cusDrawMdl);
                frameLayout.addView(cusDrawMdl);
                bottomToolbar.setVisibility(View.GONE);
                toolbar.setVisibility(View.GONE);
                drawbar.setVisibility(View.VISIBLE);
                break;

            case R.id.tmp_id_mdl_1://山地黄壤1，
                img.setVisibility(View.GONE);
                frameLayout.setVisibility(View.VISIBLE);
                frameLayout.removeAllViews();
                one = new MontainYelloSoilOne(this);
                one.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                one.setTag("profile");
                showProfileModel(one);
                frameLayout.addView(one);
                type = 1;
                layerNum = 5;
                break;
            case R.id.tmp_id_mdl_2:// 山地黄壤2，
                img.setVisibility(View.GONE);
                frameLayout.setVisibility(View.VISIBLE);
                frameLayout.removeAllViews();
                two = new MontainYelloSoilTwo(this);
                two.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                two.setTag("profile");
                showProfileModel(two);
                frameLayout.addView(two);
                type = 2;
                layerNum = 6;
                break;
            case R.id.tmp_id_mdl_3://山地黄壤3，
                img.setVisibility(View.GONE);
                frameLayout.setVisibility(View.VISIBLE);
                frameLayout.removeAllViews();
                three = new MontainYelloSoilThree(this);
                three.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                three.setTag("profile");
                showProfileModel(three);
                frameLayout.addView(three);
                type = 3;
                layerNum = 5;
                break;
            case R.id.tmp_id_mdl_4://山地黄棕壤，
                img.setVisibility(View.GONE);
                frameLayout.setVisibility(View.VISIBLE);
                frameLayout.removeAllViews();
                four = new MontainYelloBrownSoil(this);
                four.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                four.setTag("profile");
                showProfileModel(four);
                frameLayout.addView(four);
                type = 4;
                layerNum = 4;
                break;
            case R.id.tmp_id_mdl_5://山地棕壤
                img.setVisibility(View.GONE);
                frameLayout.setVisibility(View.VISIBLE);
                frameLayout.removeAllViews();
                five = new MontainBrownSoil(this);
                five.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                five.setTag("profile");
                showProfileModel(five);
                frameLayout.addView(five);
                type = 5;
                layerNum = 5;
                break;
            case R.id.tmp_id_cancel_mdl://山地黄壤1，2，3等对应下的确定与取消
                toolbar.setVisibility(View.VISIBLE);
                tmpChsMdlbar.setVisibility(View.GONE);
                tmpCusChsMdlsbar.setVisibility(View.GONE);
                tmpMdlsbar.setVisibility(View.GONE);
                frameLayout.setVisibility(View.GONE);
                bottomToolbar.setVisibility(View.VISIBLE);
                img.setVisibility(View.VISIBLE);
                saveFlag = NO_EDIT;
                break;
            case R.id.tmp_id_ok_mdl:
                toolbar.setVisibility(View.VISIBLE);
                tmpChsMdlbar.setVisibility(View.GONE);
                tmpCusChsMdlsbar.setVisibility(View.GONE);
                tmpMdlsbar.setVisibility(View.GONE);
                bottomToolbar.setVisibility(View.VISIBLE);
                saveFlag = CHOOSE_MODEL;
                break;
            case R.id.tmp_id_cus_cancel_mdl:// 自定义剖面图底部工具栏的确定和取消
                toolbar.setVisibility(View.VISIBLE);
                tmpChsMdlbar.setVisibility(View.GONE);
                tmpCusChsMdlsbar.setVisibility(View.GONE);
                tmpCusChsMdlTool.setVisibility(View.GONE);
                tmpMdlsbar.setVisibility(View.GONE);
                frameLayout.setVisibility(View.GONE);
                bottomToolbar.setVisibility(View.VISIBLE);
                img.setVisibility(View.VISIBLE);
                saveFlag = NO_EDIT;
                break;
            case R.id.tmp_id_cus_ok_mdl:
                toolbar.setVisibility(View.VISIBLE);
                tmpChsMdlbar.setVisibility(View.GONE);
                tmpCusChsMdlsbar.setVisibility(View.GONE);
                tmpCusChsMdlTool.setVisibility(View.GONE);
                tmpMdlsbar.setVisibility(View.GONE);
                bottomToolbar.setVisibility(View.VISIBLE);
                saveFlag = CUSCHOOSE_MODEL;
                break;
            case R.id.tmp_id_menu_cuschs_mdl:

                break;
            case R.id.id_left:
                if (stat) {
                    stat = false;
                    leftBn.setImageResource(R.drawable.gallery_button_right);
                    cusmdlSpinnerLy.setVisibility(View.GONE);
                    cusmdlInputDepth.setVisibility(View.GONE);
                    cusmdlInputName.setVisibility(View.GONE);
                }else {
                    stat = true;
                    leftBn.setImageResource(R.drawable.gallery_button_left);
                    cusmdlSpinnerLy.setVisibility(View.VISIBLE);
                    cusmdlInputDepth.setVisibility(View.VISIBLE);
                    cusmdlInputName.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.tmp_id_cfm_chs:// 自定义剖面图左侧spinner工具栏输入层数的确定
                switch (getLytNum()) {
                    case 99:
                        Toast.makeText(this, "1~3层为必选哦", Toast.LENGTH_LONG).show();
                        break;
                    case 999:
                        Toast.makeText(this, "请连续选择土层图例", Toast.LENGTH_LONG).show();
                        break;
                    case 3:
                        if (isInputed(3)) {
                            updateDepthLines(3);
                            updatelayerName(3);
                            img.setVisibility(View.GONE);
                            frameLayout.setVisibility(View.VISIBLE);
                            frameLayout.removeAllViews();
                            threeLyr = new ThreeLayerCusMdl(this, depthLines, layerName, sumDepth);
                            threeLyr.setPro_name(proLegend);
                            threeLyr.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                            threeLyr.setTag("profile");
                            showProfileModel(threeLyr);
                            cusmdlSpinnerLy.setVisibility(View.GONE);
                            cusmdlInputDepth.setVisibility(View.GONE);
                            cusmdlInputName.setVisibility(View.GONE);
                            stat = true;
                            frameLayout.addView(threeLyr);
                            type = 13;
                            layerNum = 3;
                        }else {
                            Toast.makeText(this, "高度输入为空或不为数字", Toast.LENGTH_LONG).show();
                        }
                        break;
                    case 4:
                        if (isInputed(4)) {
                            updateDepthLines(4);
                            updatelayerName(4);
                            img.setVisibility(View.GONE);
                            frameLayout.setVisibility(View.VISIBLE);
                            frameLayout.removeAllViews();
                            fourLyr = new FourLayerCusMdl(this, depthLines, layerName, sumDepth);
                            fourLyr.setPro_name(proLegend);
                            fourLyr.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                            fourLyr.setTag("profile");
                            showProfileModel(fourLyr);
                            cusmdlSpinnerLy.setVisibility(View.GONE);
                            cusmdlInputDepth.setVisibility(View.GONE);
                            cusmdlInputName.setVisibility(View.GONE);
                            stat = true;
                            frameLayout.addView(fourLyr);
                            type = 14;
                            layerNum = 4;
                        }else {
                            Toast.makeText(this, "高度输入为空或不为数字", Toast.LENGTH_LONG).show();
                        }
                        break;
                    case 5:
                        if (isInputed(5)) {
                            updateDepthLines(5);
                            updatelayerName(5);
                            img.setVisibility(View.GONE);
                            frameLayout.setVisibility(View.VISIBLE);
                            frameLayout.removeAllViews();
                            fiveLyr = new FiveLayerCusMdl(this, depthLines, layerName, sumDepth);
                            fiveLyr.setPro_name(proLegend);
                            fiveLyr.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                            fiveLyr.setTag("profile");
                            showProfileModel(fiveLyr);
                            cusmdlSpinnerLy.setVisibility(View.GONE);
                            cusmdlInputDepth.setVisibility(View.GONE);
                            cusmdlInputName.setVisibility(View.GONE);
                            stat = true;
                            frameLayout.addView(fiveLyr);
                            type = 15;
                            layerNum = 5;
                        }else {
                            Toast.makeText(this, "高度输入为空或不为数字", Toast.LENGTH_LONG).show();
                        }
                        break;
                    case 6:
                        if (isInputed(6)) {
                            updateDepthLines(6);
                            updatelayerName(6);
                            img.setVisibility(View.GONE);
                            frameLayout.setVisibility(View.VISIBLE);
                            frameLayout.removeAllViews();
                            sixLyr = new SixLayerCusMdl(this, depthLines, layerName, sumDepth);
                            sixLyr.setPro_name(proLegend);
                            sixLyr.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                            sixLyr.setTag("profile");
                            showProfileModel(sixLyr);
                            cusmdlSpinnerLy.setVisibility(View.GONE);
                            cusmdlInputDepth.setVisibility(View.GONE);
                            cusmdlInputName.setVisibility(View.GONE);
                            stat = true;
                            frameLayout.addView(sixLyr);
                            type = 16;
                            layerNum = 6;
                        }else {
                            Toast.makeText(this, "高度输入为空或不为数字", Toast.LENGTH_LONG).show();
                        }
                        break;
                    case 7:
                        if (isInputed(7)) {
                            updateDepthLines(7);
                            updatelayerName(7);
                            img.setVisibility(View.GONE);
                            frameLayout.setVisibility(View.VISIBLE);
                            frameLayout.removeAllViews();
                            sevenLyr = new SevenLayerCusMdl(this, depthLines, layerName, sumDepth);
                            sevenLyr.setPro_name(proLegend);
                            sevenLyr.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                            sevenLyr.setTag("profile");
                            showProfileModel(sevenLyr);
                            cusmdlSpinnerLy.setVisibility(View.GONE);
                            cusmdlInputDepth.setVisibility(View.GONE);
                            cusmdlInputName.setVisibility(View.GONE);
                            stat = true;
                            frameLayout.addView(sevenLyr);
                            type = 17;
                            layerNum = 7;
                        }else {
                            Toast.makeText(this, "高度输入为空或不为数字", Toast.LENGTH_LONG).show();
                        }
                        break;
                }
                break;
            case R.id.tmp_id_undo_cus_draw:
                cusDrawMdl.unDo();
                break;
            case R.id.tmp_id_clear_cus_draw:
                cusDrawMdl.clearDrawMdl();
                break;
            case R.id.tmp_id_cancel_cus_draw:
                toolbar.setVisibility(View.VISIBLE);
                drawbar.setVisibility(View.GONE);
                frameLayout.setVisibility(View.GONE);
                bottomToolbar.setVisibility(View.VISIBLE);
                img.setVisibility(View.VISIBLE);
                saveFlag = NO_EDIT;
                stat = true;
                break;
            case R.id.tmp_id_ok_cus_draw:
                toolbar.setVisibility(View.VISIBLE);
                drawbar.setVisibility(View.GONE);
                bottomToolbar.setVisibility(View.VISIBLE);
                saveFlag = CUSTOM_DRAW;
                stat = false;
                break;
        }
    }

    private float updateDepthLines(int n) {
        float depth1 = Float.parseFloat(depthOne.getText()+"");
        float depth2 = Float.parseFloat(depthTwo.getText()+"");
        float depth3 = Float.parseFloat(depthThree.getText()+"");
        float depth4, depth5, depth6, depth7;
        depthLines[0] = 0f;
        switch (n) {
            case 3:
                sumDepth = depth1+depth2+depth3;
                depthLines[1] = depth1/sumDepth;
                depthLines[2] = (depth1+depth2)/sumDepth;
                depthLines[3] = 1;
                break;
            case 4:
                depth4 = Float.parseFloat(depthFour.getText()+"");
                sumDepth = depth1+depth2+depth3+depth4;
                depthLines[1] = depth1/sumDepth;
                depthLines[2] = (depth1+depth2)/sumDepth;
                depthLines[3] = (depth1+depth2+depth3)/sumDepth;
                depthLines[4] = 1;
                break;
            case 5:
                depth4 = Float.parseFloat(depthFour.getText()+"");
                depth5 = Float.parseFloat(depthFive.getText()+"");
                sumDepth = depth1+depth2+depth3+depth4+depth5;
                depthLines[1] = depth1/sumDepth;
                depthLines[2] = (depth1+depth2)/sumDepth;
                depthLines[3] = (depth1+depth2+depth3)/sumDepth;
                depthLines[4] = (depth1+depth2+depth3+depth4)/sumDepth;
                depthLines[5] = 1;
                break;
            case 6:
                depth4 = Float.parseFloat(depthFour.getText()+"");
                depth5 = Float.parseFloat(depthFive.getText()+"");
                depth6 = Float.parseFloat(depthSix.getText()+"");
                sumDepth = depth1+depth2+depth3+depth4+depth5+depth6;
                depthLines[1] = depth1/sumDepth;
                depthLines[2] = (depth1+depth2)/sumDepth;
                depthLines[3] = (depth1+depth2+depth3)/sumDepth;
                depthLines[4] = (depth1+depth2+depth3+depth4)/sumDepth;
                depthLines[5] = (depth1+depth2+depth3+depth4+depth5)/sumDepth;
                depthLines[6] = 1;
                break;
            case 7:
                depth4 = Float.parseFloat(depthFour.getText()+"");
                depth5 = Float.parseFloat(depthFive.getText()+"");
                depth6 = Float.parseFloat(depthSix.getText()+"");
                depth7 = Float.parseFloat(depthSeven.getText()+"");
                sumDepth = depth1+depth2+depth3+depth4+depth5+depth6+depth7;
                depthLines[1] = depth1/sumDepth;
                depthLines[2] = (depth1+depth2)/sumDepth;
                depthLines[3] = (depth1+depth2+depth3)/sumDepth;
                depthLines[4] = (depth1+depth2+depth3+depth4)/sumDepth;
                depthLines[5] = (depth1+depth2+depth3+depth4+depth5)/sumDepth;
                depthLines[6] = (depth1+depth2+depth3+depth4+depth5+depth6)/sumDepth;
                depthLines[7] = 1;
                break;
        }
        return depthLines[4];
    }

    private void updatelayerName(int n) {
        layerName[0] = nameOne.getText()+"";
        layerName[1] = nameTwo.getText()+"";
        layerName[2] = nameThree.getText()+"";
        switch (n) {
            case 3:
                break;
            case 4:
                layerName[3] = nameFour.getText()+"";
                break;
            case 5:
                layerName[3] = nameFour.getText()+"";
                layerName[4] = nameFive.getText()+"";
                break;
            case 6:
                layerName[3] = nameFour.getText()+"";
                layerName[4] = nameFive.getText()+"";
                layerName[5] = nameSix.getText()+"";
                break;
            case 7:
                layerName[3] = nameFour.getText()+"";
                layerName[4] = nameFive.getText()+"";
                layerName[5] = nameSix.getText()+"";
                layerName[6] = nameSeven.getText()+"";
                break;
        }
    }

    private boolean isInputed(int n) {
        String inputOne = depthOne.getText()+"";
        String inputTwo = depthTwo.getText()+"";
        String inputThree = depthThree.getText()+"";
        String inputFour = depthFour.getText()+"";
        String inputFive = depthFive.getText()+"";
        String inputSix = depthSix.getText()+"";
        String inputSeven = depthSeven.getText()+"";

        boolean result = false;
        switch (n) {
            case 3:
                if (isInputRight(inputOne) && isInputRight(inputTwo) && isInputRight(inputThree)) {
                    result = true;
                }else {
                    result = false;
                }
                break;
            case 4:
                if (isInputRight(inputOne) && isInputRight(inputTwo) && isInputRight(inputThree) && isInputRight(inputFour)) {
                    result = true;
                }else {
                    result = false;
                }
                break;
            case 5:
                if (isInputRight(inputOne) && isInputRight(inputTwo) && isInputRight(inputThree) &&
                        isInputRight(inputFour) && isInputRight(inputFive)) {
                    result = true;
                }else {
                    result = false;
                }
                break;
            case 6:
                if (isInputRight(inputOne) && isInputRight(inputTwo) && isInputRight(inputThree) &&
                        isInputRight(inputFour) && isInputRight(inputFive)&&isInputRight(inputSix)) {
                    result = true;
                }else {
                    result = false;
                }
                break;
            case 7:
                if (isInputRight(inputOne) && isInputRight(inputTwo) && isInputRight(inputThree) &&
                        isInputRight(inputFour) && isInputRight(inputFive)&&isInputRight(inputSix) &&isInputRight(inputSeven)) {
                    result = true;
                }else {
                    result = false;
                }
                break;
        }
        return result;
    }

    private boolean isInputRight(String input) {
        Pattern p = Pattern.compile("[0-9.]*");
        Matcher m = p.matcher(input);
        if ("".equals(input) || !m.matches()) {
            return false;
        }else {
            return true;
        }
    }

    private ArrayList<Map<String, Object>> getData() {
        itemList = new ArrayList<Map<String, Object>>();
        Map<String, Object> Map = new HashMap<String, Object>();
        Map.put("pic", R.drawable.choose_plea);
        itemList.add(Map);
        Map<String, Object> oMap = new HashMap<String, Object>();
        oMap.put("pic", R.drawable.yuandian);
        itemList.add(oMap);
        Map<String, Object> aMap = new HashMap<String, Object>();
        aMap.put("pic", R.drawable.xiaocha);
        itemList.add(aMap);
        Map<String, Object> a1Map = new HashMap<String, Object>();
        a1Map.put("pic", R.drawable.zhexian);
        itemList.add(a1Map);
        Map<String, Object> aeMap = new HashMap<String, Object>();
        aeMap.put("pic", R.drawable.shuxian);
        itemList.add(aeMap);
        Map<String, Object> abMap = new HashMap<String, Object>();
        abMap.put("pic", R.drawable.xiaodengzi);
        itemList.add(abMap);
        Map<String, Object> eMap = new HashMap<String, Object>();
        eMap.put("pic", R.drawable.yuanhu);
        itemList.add(eMap);
        Map<String, Object> ebMap = new HashMap<String, Object>();
        ebMap.put("pic", R.drawable.xiexian);
        itemList.add(ebMap);
        Map<String, Object> bMap = new HashMap<String, Object>();
        bMap.put("pic", R.drawable.xiaocao);
        itemList.add(bMap);
        Map<String, Object> bcMap = new HashMap<String, Object>();
        bcMap.put("pic", R.drawable.jiantou);
        itemList.add(bcMap);
        Map<String, Object> cMap = new HashMap<String, Object>();
        cMap.put("pic", R.drawable.hengxian);
        itemList.add(cMap);
        Map<String, Object> c1Map = new HashMap<String, Object>();
        c1Map.put("pic", R.drawable.zhuankuai);
        itemList.add(c1Map);
        Map<String, Object> rMap = new HashMap<String, Object>();
        rMap.put("pic", R.drawable.kongbai);
        itemList.add(rMap);
        return itemList;
    }

    private void showProfileModel(final BaseProfileModel base) {
        ViewTreeObserver vto = base.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
            public void onGlobalLayout() {
                base.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                //TODO 判断id是否相同
                tmpBm = BitmapUtils.decodeSampledBitmapFromFile(imageFilePath, base.getWidth(),base.getHeight());
                //把图片设为背景
                Drawable drawable = new BitmapDrawable(tmpBm);
                base.setBackground(drawable);
            }
        });
    }

    private int getLytNum(){
        int count = 0;
        if (proLegend[0] == 0 || proLegend[1] == 0 || proLegend[2] == 0) {
            return 99;
        }else {
            for (int i = 0; i < proLegend.length; i++) {
                if (0 != proLegend[i]) {
                    count++;
                }else if(i < proLegend.length-1 && proLegend[i+1] != 0){
                    return 999;
                }
            }
        }
        return count;
    }

    private void saveProfile() {
        try {
            float[] depths = null;
            String[] layerNames = null;
            int[] proLegendCodes = null;

            // 保存图片到SD卡上
            File file = new File(imageSaveFilePath);
            FileOutputStream stream = new FileOutputStream(file);
            //将ImageView中的图片转换成Bitmap
            Bitmap bitmapDraw = null;
            Bitmap bitmapPic = null;
            Bitmap bitmapResult = null;
            switch (saveFlag) {
                case NO_EDIT:
                    img.buildDrawingCache();
                    bitmapResult = img.getDrawingCache();
                    break;
                case CUSCHOOSE_MODEL:
                case CHOOSE_MODEL:
                    basePro = (BaseProfileModel) frameLayout.findViewWithTag("profile");
                    basePro.buildDrawingCache();
//    				bitmapPic = ((BitmapDrawable) basePro.getBackground()).getBitmap();
                    bitmapPic = BitmapUtils.decodeSampledBitmapFromFile(imageFilePath, basePro.getWidth(),basePro.getHeight());
                    switch (type) {
                        case 1:
                            bitmapDraw = one.getProModelBitmap();
                            break;
                        case 2:
                            bitmapDraw = two.getProModelBitmap();
                            break;
                        case 3:
                            bitmapDraw = three.getProModelBitmap();
                            break;
                        case 4:
                            bitmapDraw = four.getProModelBitmap();
                            break;
                        case 5:
                            bitmapDraw = five.getProModelBitmap();
                            break;
                        case 13:
                            bitmapDraw = threeLyr.getProModelBitmap();
                            depths = threeLyr.getDepths();
                            layerNames = threeLyr.getLayerName();
                            proLegendCodes = threeLyr.getProLegendCode();
                            break;
                        case 14:
                            bitmapDraw = fourLyr.getProModelBitmap();
                            depths = threeLyr.getDepths();
                            layerNames = threeLyr.getLayerName();
                            proLegendCodes = threeLyr.getProLegendCode();
                            break;
                        case 15:
                            bitmapDraw = fiveLyr.getProModelBitmap();
                            depths = threeLyr.getDepths();
                            layerNames = threeLyr.getLayerName();
                            proLegendCodes = threeLyr.getProLegendCode();
                            break;
                        case 16:
                            bitmapDraw = sixLyr.getProModelBitmap();
                            depths = threeLyr.getDepths();
                            layerNames = threeLyr.getLayerName();
                            proLegendCodes = threeLyr.getProLegendCode();
                            break;
                        case 17:
                            bitmapDraw = sevenLyr.getProModelBitmap();
                            depths = threeLyr.getDepths();
                            layerNames = threeLyr.getLayerName();
                            proLegendCodes = threeLyr.getProLegendCode();
                            break;
                    }
                    bitmapResult = BitmapUtils.add2Bitmap(bitmapPic, bitmapDraw);
                    break;
                case CUSTOM_DRAW:
                    cusDrawMdl = (CustomDrawMdl) frameLayout.findViewWithTag("cusDraw");
                    cusDrawMdl.buildDrawingCache();
                    bitmapPic = ((BitmapDrawable) cusDrawMdl.getBackground()).getBitmap();
                    bitmapDraw = cusDrawMdl.getProModelBitmap();
                    bitmapResult = BitmapUtils.add2Bitmap(bitmapPic, bitmapDraw);
                    break;
            }
            bitmapResult.compress(Bitmap.CompressFormat.PNG, 100, stream);
            ToastUtil.show(this,"保存图片成功");
           // Toast.makeText(this, "保存图片成功").show();
            //保存位置信息
            saveInfoGeo();
           /* if (depths == null)
            {
                Log.d(TAG, "saveProfile: depths is null");
            }
            if (layerNames == null)
            {
                Log.d(TAG, "saveProfile: layernames is null");
            }
            if (proLegendCodes == null)
            {
                Log.d(TAG, "saveProfile: proLegendCodes is null");
            }*/
            
            //保存剖面图信息
            saveProfilePhoto(depths, layerNames, proLegendCodes);

        } catch (Exception e) {
            ToastUtil.show(this,"保存图片成功");
            //Toast.makeText(this, "保存图片成功", 0).show();
            e.printStackTrace();
        }
    }

    private void saveProfilePhoto(float[] depths, String[] layerNames, int[] proLegendCodes) {
        //Log.d(TAG, "saveProfilePhoto: " + layerNames.length);
        ProfilePhoto pro = new ProfilePhoto();
        pro.setFilePath(imageSaveFilePath);
        pro.setDepths(depths);
        pro.setLayerNames(layerNames);
        pro.setProLegendCodes(proLegendCodes);
        soilNoteDB.saveProfile(pro);
    }

    public static int getLayerNum() {
        return layerNum;
    }

    public static void setLayerNum(int layerNum) {
        ActivityEditPhoto.layerNum = layerNum;
    }

    int[] empty = {0,0,0,0,0,0,0};

    public int addArray(int[] a){
        int sum = 0;
        for (int i = 0; i < a.length-1; i++) {
            sum += a[i];
        }
        return sum;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position,
                               long id) {
        switch (parent.getId()) {//注意这里是parent.getId,不是v.getId
            case R.id.tmp_id_one_lyr://spinner的每一个item
                proLegend[0] = ( position==0 )?0: DrawLegend.proLegengCode[position-1];
                if (position != 0) {
                    cusmdlInputDepth.setVisibility(View.VISIBLE);
                    cusmdlInputName.setVisibility(View.VISIBLE);
                    nameOne.setVisibility(View.VISIBLE);
                    depthOne.setVisibility(View.VISIBLE);
                    empty[0] = 1;
                }else {
                    empty[0] = 0;
                    nameOne.setVisibility(View.INVISIBLE);
                    depthOne.setVisibility(View.INVISIBLE);
                    depthLines[1] = 0f;
                    layerName[0] = "";
                    if (addArray(empty) == 0) {
                        cusmdlInputDepth.setVisibility(View.GONE);
                        cusmdlInputName.setVisibility(View.GONE);
                    }
                }
                break;
            case R.id.tmp_id_two_lyr:
                proLegend[1] = ( position==0 )?0:DrawLegend.proLegengCode[position-1];
                if (position != 0) {
                    cusmdlInputDepth.setVisibility(View.VISIBLE);
                    cusmdlInputName.setVisibility(View.VISIBLE);
                    nameTwo.setVisibility(View.VISIBLE);
                    depthTwo.setVisibility(View.VISIBLE);
                    empty[1] = 1;
                }else {
                    empty[1] = 0;
                    nameTwo.setVisibility(View.INVISIBLE);
                    depthTwo.setVisibility(View.INVISIBLE);
                    depthLines[2] = 0f;
                    layerName[1] = "";
                    if (addArray(empty) == 0) {
                        cusmdlInputDepth.setVisibility(View.GONE);
                        cusmdlInputName.setVisibility(View.GONE);
                    }
                }
                break;
            case R.id.tmp_id_three_lyr:
                proLegend[2] = ( position==0 )?0:DrawLegend.proLegengCode[position-1];
                if (position != 0) {
                    cusmdlInputDepth.setVisibility(View.VISIBLE);
                    cusmdlInputName.setVisibility(View.VISIBLE);
                    nameThree.setVisibility(View.VISIBLE);
                    depthThree.setVisibility(View.VISIBLE);
                    empty[2] = 1;
                }else {
                    empty[2] = 0;
                    nameThree.setVisibility(View.INVISIBLE);
                    depthThree.setVisibility(View.INVISIBLE);
                    depthLines[3] = 0f;
                    layerName[2] = "";
                    if (addArray(empty) == 0) {
                        cusmdlInputDepth.setVisibility(View.GONE);
                        cusmdlInputName.setVisibility(View.GONE);
                    }
                }
                break;
            case R.id.tmp_id_four_lyr:
                proLegend[3] = ( position==0 )?0:DrawLegend.proLegengCode[position-1];
                if (position != 0) {
                    cusmdlInputDepth.setVisibility(View.VISIBLE);
                    cusmdlInputName.setVisibility(View.VISIBLE);
                    nameFour.setVisibility(View.VISIBLE);
                    depthFour.setVisibility(View.VISIBLE);
                    empty[3] = 1;
                }else {
                    empty[3] = 0;
                    nameFour.setVisibility(View.INVISIBLE);
                    depthFour.setVisibility(View.INVISIBLE);
                    depthLines[4] = 0f;
                    layerName[3] = "";
                    if (addArray(empty) == 0) {
                        cusmdlInputDepth.setVisibility(View.GONE);
                        cusmdlInputName.setVisibility(View.GONE);
                    }
                }
                break;
            case R.id.tmp_id_five_lyr:
                proLegend[4] = ( position==0 )?0:DrawLegend.proLegengCode[position-1];
                if (position != 0) {
                    cusmdlInputDepth.setVisibility(View.VISIBLE);
                    cusmdlInputName.setVisibility(View.VISIBLE);
                    nameFive.setVisibility(View.VISIBLE);
                    depthFive.setVisibility(View.VISIBLE);
                    empty[4] = 1;
                }else {
                    empty[4] = 0;
                    nameFive.setVisibility(View.INVISIBLE);
                    depthFive.setVisibility(View.INVISIBLE);
                    depthLines[5] = 0f;
                    layerName[4] = "";
                    if (addArray(empty) == 0) {
                        cusmdlInputDepth.setVisibility(View.GONE);
                        cusmdlInputName.setVisibility(View.GONE);
                    }
                }
                break;
            case R.id.tmp_id_six_lyr:
                proLegend[5] = ( position==0 )?0:DrawLegend.proLegengCode[position-1];
                if (position != 0) {
                    cusmdlInputDepth.setVisibility(View.VISIBLE);
                    cusmdlInputName.setVisibility(View.VISIBLE);
                    nameSix.setVisibility(View.VISIBLE);
                    depthSix.setVisibility(View.VISIBLE);
                    empty[5] = 1;
                }else {
                    empty[5] = 0;
                    nameSix.setVisibility(View.INVISIBLE);
                    depthSix.setVisibility(View.INVISIBLE);
                    depthLines[6] = 0f;
                    layerName[5] = "";
                    if (addArray(empty) == 0) {
                        cusmdlInputDepth.setVisibility(View.GONE);
                        cusmdlInputName.setVisibility(View.GONE);
                    }
                }
                break;
            case R.id.tmp_id_seven_lyr:
                proLegend[6] = ( position==0 )?0:DrawLegend.proLegengCode[position-1];
                if (position != 0) {
                    cusmdlInputDepth.setVisibility(View.VISIBLE);
                    cusmdlInputName.setVisibility(View.VISIBLE);
                    nameSeven.setVisibility(View.VISIBLE);
                    depthSeven.setVisibility(View.VISIBLE);
                    empty[6] = 1;
                }else {
                    empty[6] = 0;
                    nameSeven.setVisibility(View.INVISIBLE);
                    depthSeven.setVisibility(View.INVISIBLE);
                    depthLines[7] = 0f;
                    layerName[6] = "";
                    if (addArray(empty) == 0) {
                        cusmdlInputDepth.setVisibility(View.GONE);
                        cusmdlInputName.setVisibility(View.GONE);
                    }
                }
                break;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_photo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id)
        {
            case android.R.id.home:
                if (null != infoList) {
                    infoList.clear();
                }
                finish();
                break;
            case R.id.action_save:

                saveProfile();
                break;
            case R.id.action_add_attr:
                if (saveFlag == NO_EDIT || saveFlag == CUSTOM_DRAW) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);  //先得到构造器
                    builder.setCancelable(false);
                    final EditText eText = new EditText(this);
                    builder.setTitle("请定义土壤剖面总层数：");
                    builder.setView(eText);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() { //设置确定按钮
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String text = eText.getText()+"";
                            Pattern p = Pattern.compile("[0-9]*");
                            Matcher m = p.matcher(text);
                            if ("".equals(text)) {
                                Toast.makeText(getApplication(), "层数不能为空", Toast.LENGTH_SHORT).show();
                            }else if (!m.matches()) {
                                Toast.makeText(getApplication(), "请输入数字", Toast.LENGTH_SHORT).show();
                            }else {
                                layerNum = Integer.parseInt(eText.getText()+"");
                                Intent intent = new Intent(ActivityEditPhoto.this, ActivityAttributeHome.class);
                                intent.putExtra("back_activity", "edit_photo");
                                intent.putExtra("saveFilePath", imageSaveFilePath);
                                startActivity(intent);
                            }
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() { //设置取消按钮
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    //参数都设置完成了，创建并显示出来
                    builder.create().show();
                }else if (saveFlag == CUSCHOOSE_MODEL) {
                    infoList = new ArrayList<Map<String,Object>>();
                    switch (type) {
                        case 13:
                            infoList = threeLyr.getLayerInfo();
                            break;
                        case 14:
                            infoList = fourLyr.getLayerInfo();
                            break;
                        case 15:
                            infoList = fiveLyr.getLayerInfo();
                            break;
                        case 16:
                            infoList = sixLyr.getLayerInfo();
                            break;
                        case 17:
                            infoList = sevenLyr.getLayerInfo();
                            break;
                    }
                    Intent intent = new Intent(ActivityEditPhoto.this, ActivityAttributeHome.class);
                    startActivity(intent);
                }else {
                    infoList = new ArrayList<Map<String,Object>>();
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);  //先得到构造器
                    builder.setCancelable(false);
                    final EditText eText = new EditText(this);
                    builder.setTitle("请输入土壤剖面总深度(cm)：");
                    builder.setView(eText);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() { //设置确定按钮
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String text = eText.getText()+"";
                            Pattern p = Pattern.compile("[0-9.]*");
                            Matcher m = p.matcher(text);
                            if ("".equals(text)) {
                                Toast.makeText(getApplication(), "深度不能为空", Toast.LENGTH_SHORT).show();
                            }else if (!m.matches()) {
                                Toast.makeText(getApplication(), "请输入数字", Toast.LENGTH_SHORT).show();
                            }else {
                                length = Float.parseFloat(eText.getText()+"");
                                switch (type) {
                                    case 1:
                                        infoList = one.getLayerInfo(length);
                                        break;
                                    case 2:
                                        infoList = two.getLayerInfo(length);
                                        break;
                                    case 3:
                                        infoList = three.getLayerInfo(length);
                                        break;
                                    case 4:
                                        infoList = four.getLayerInfo(length);
                                        break;
                                    case 5:
                                        infoList = five.getLayerInfo(length);
                                        break;
                                }
                                dialog.dismiss(); //关闭dialog
                                Intent intent = new Intent(ActivityEditPhoto.this, ActivityAttributeHome.class);
                                intent.putExtra("back_activity", "edit_photo");
                                intent.putExtra("saveFilePath", imageSaveFilePath);
                                startActivity(intent);
                            }
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() { //设置取消按钮
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.setNeutralButton("深度未知", new DialogInterface.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent intent = new Intent(ActivityEditPhoto.this, ActivityAttributeHome.class);
                            startActivity(intent);
                        }
                    });
                    //参数都设置完成了，创建并显示出来
                    builder.create().show();
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO 自动生成的方法存根

    }

    //好像如果把saveInfoGeo内部代码直接放在点击事件下面，经纬度等信息都是空的，这样写就能有数据
    private void saveInfoGeo() {
        InfoGeo infoGeo = new InfoGeo();
        infoGeo.setImagePath(imageSaveFilePath);
        infoGeo.setLatitude(mLatitude);
        infoGeo.setLongtitude(mLongtitude);
        infoGeo.setPosition(humanPosition);
        soilNoteDB.saveInfoGeo(infoGeo);
    }

    private class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            mLatitude = location.getLatitude();
            mLongtitude = location.getLongitude();
            humanPosition = location.getAddrStr();
        }
    }

    /*
     * 定位相关
     */
    private void initLocation() {
        mLocationClient = new LocationClient(this);
        myLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(myLocationListener);// 注册定位监听器
        // 设置定位的一些属性
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");// 坐标类型
        option.setIsNeedAddress(true);// 返回位置
        option.setOpenGps(true);// 打开GPS
        option.setScanSpan(1000);// 每隔1000秒进行一次请求
        mLocationClient.setLocOption(option);
    }

    @Override
    protected void onStart() {
        // TODO 自动生成的方法存根
        super.onStart();
        if (!mLocationClient.isStarted()) {
            mLocationClient.start();
        }
    }

    @Override
    protected void onStop() {
        // TODO 自动生成的方法存根
        super.onStop();
        mLocationClient.stop();
    }
}

