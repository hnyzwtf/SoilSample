package com.soil.profile.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.Toast;

import com.soil.profile.db.SoilNoteDB;
import com.soil.profile.model.DatabaseMdl.InfoAttrRec;
import com.soil.profile.utils.SaveViewAsImage;
import com.soil.soilsample.R;
import com.soil.soilsample.base.BaseActivity;

import java.text.SimpleDateFormat;
import java.util.Date;



@SuppressLint("SimpleDateFormat") public class TableAttrRecActivity extends BaseActivity {
    private Toolbar toolbar;

    private Button saveBn;
    private TableLayout tLayout;
    private EditText number, date, weather, investigator, position, sheetNum, commonName,
            formalName, terrain, altitude, prntMatType, naVeg, erodeStat, phreaticLevelQuality,
            landuse, irrigDrainage, fertiStat, humanEffect, cropRotatStat, yield, review;

    private String saveFilePath;
    // 数据库相关
    private SoilNoteDB soilNoteDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_attr_rec);
        soilNoteDB = SoilNoteDB.getInstance(this);

        initView();
    }

    private void initView() {
        number = (EditText) findViewById(R.id.id_tbl_num);
        date = (EditText) findViewById(R.id.id_tbl_date);
        weather = (EditText) findViewById(R.id.id_tbl_weather);
        investigator = (EditText) findViewById(R.id.id_tbl_investigator);
        position = (EditText) findViewById(R.id.id_tbl_position);
        sheetNum = (EditText) findViewById(R.id.id_tbl_sheet_num);
        commonName = (EditText) findViewById(R.id.id_tbl_common_name);
        formalName = (EditText) findViewById(R.id.id_tbl_formal_name);
        terrain = (EditText) findViewById(R.id.id_tbl_terrain);
        altitude = (EditText) findViewById(R.id.id_tbl_altitude);
        prntMatType = (EditText) findViewById(R.id.id_tbl_prnt_mat_type);
        naVeg = (EditText) findViewById(R.id.id_tbl_nat_veg);
        erodeStat = (EditText) findViewById(R.id.id_tbl_erode_stat);
        phreaticLevelQuality = (EditText) findViewById(R.id.id_tbl_phreatic_level_quality);
        landuse = (EditText) findViewById(R.id.id_tbl_landuse);
        irrigDrainage = (EditText) findViewById(R.id.id_tbl_irrig_drainage);
        fertiStat = (EditText) findViewById(R.id.id_tbl_ferti_stat);
        humanEffect = (EditText) findViewById(R.id.id_tbl_human_effect);
        cropRotatStat = (EditText) findViewById(R.id.id_tbl_crop_rotat_stat);
        yield = (EditText) findViewById(R.id.id_tbl_yield);
        review = (EditText) findViewById(R.id.id_tbl_review);

        //自动获取系统日期
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        date.setText(str);
        //自动获取地点信息
        String humanPosition = ActivityEditPhoto.humanPosition;
        position.setText(humanPosition);

        saveBn = (Button) findViewById(R.id.id_tbl_save);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tLayout = (TableLayout) findViewById(R.id.id_tbl_tbly);

        saveBn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

//				saveInfoAttrRec();
                Toast.makeText(TableAttrRecActivity.this, SaveViewAsImage.saveAsImg(tLayout)?"保存成功":"保存失败", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void saveInfoAttrRec() {
        InfoAttrRec rec = new InfoAttrRec();
        rec.setFilePath(saveFilePath);
        rec.setNO(number.getText()+"");
        rec.setDate(date.getText()+"");
        rec.setWeather(weather.getText()+"");
        rec.setInvestigator(weather.getText()+"");
        rec.setPosition(weather.getText()+"");
        rec.setSheet_NO(weather.getText()+"");
        rec.setCommon_name(weather.getText()+"");
        rec.setFormal_name(weather.getText()+"");
        rec.setTerrain(weather.getText()+"");
        rec.setAltitude(weather.getText()+"");
        rec.setPrnt_mat_type(weather.getText()+"");
        rec.setNat_veg(weather.getText()+"");
        rec.setErode_sitn(weather.getText()+"");
        rec.setPhreatic_level(weather.getText()+"");
        rec.setWater_quality(weather.getText()+"");
        rec.setLanduse(weather.getText()+"");
        rec.setIrrig_drainage(weather.getText()+"");
        rec.setFerti_stat(weather.getText()+"");
        rec.setHuman_effect(weather.getText()+"");
        rec.setCrop_rotat_stat(weather.getText()+"");
        rec.setYield(weather.getText()+"");
        rec.setReview(weather.getText()+"");
        soilNoteDB.saveInfoAttrRec(rec);
        Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
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

