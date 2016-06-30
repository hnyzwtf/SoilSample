package com.soil.profile.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.soil.profile.db.SoilNoteDB;
import com.soil.profile.model.DatabaseMdl.InfoAttrProper;
import com.soil.profile.utils.SaveViewAsImage;
import com.soil.soilsample.R;
import com.soil.soilsample.base.BaseActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;



/*
 * 该类用于插入属性数据
 */

public class TableAttrProperActivity extends BaseActivity implements OnClickListener{
    private Toolbar toolbar;

    private TableLayout tableLayout;
    public List<Map<String, Object>> dataList;
    private Button saveBn;
    private EditText layerName, length, colorDry, color_wet, humidity, texture,
            structure, compactness, porosity, newGrowth_class, newGrowth_morphology,
            newGrowth_number, intrusion, rootSys, meas_PH, meas_limy_react;
    private String saveFilePath;
    private SoilNoteDB soilNoteDB;
    private int layerCount;
    private List<List<String>> properInfoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_attr_proper);
        soilNoteDB = SoilNoteDB.getInstance(this);
        initEditText();

        Intent intent = getIntent();
        saveFilePath = intent.getStringExtra("saveFilePath");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_white_24dp);
        tableLayout = (TableLayout) findViewById(R.id.id_prop_tbl_tbly);
        saveBn = (Button) findViewById(R.id.id_prop_tbl_save);
        layerName = (EditText) findViewById(R.id.id_tbl_layer);
        length = (EditText) findViewById(R.id.id_tbl_depth);

        saveBn.setOnClickListener(this);

        switch (ActivityEditPhoto.saveFlag) {
            case 0:
            case 3:
                layerCount = ActivityEditPhoto.layerNum;
                for (int i = 1; i < layerCount; i++) {
                    TableRow tableRow = new TableRow(this);
                    tableRow.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT ));
                    tableRow.setFocusable(false);
                    tableRow.setFocusableInTouchMode(true);
                    for (int j = 0; j < 16; j++) {
                        EditText text = new EditText(this);
                        text.setLayoutParams(new TableRow.LayoutParams(80, LayoutParams.MATCH_PARENT));
                        text.setTextSize(14);
                        text.setMaxLines(5);
                        text.setGravity(Gravity.CENTER);
                        text.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
                        if (i < layerCount-1 && j < 15) {
                            text.setBackgroundResource(R.drawable.table_frame);
                        }else if (i < layerCount-1 && j == 15) {
                            text.setBackgroundResource(R.drawable.table_frame_rt);
                        }else if (i == layerCount-1 && j < 15) {
                            text.setBackgroundResource(R.drawable.table_frame_btm);
                        }else {
                            text.setBackgroundResource(R.drawable.table_frame_last);
                        }
                        tableRow.addView(text);
                    }
                    tableLayout.addView(tableRow);
                }
                break;
            case 1:
            case 2:
                dataList = ActivityEditPhoto.infoList;
                //深度保留小数点后一位
                DecimalFormat decimalFormat = new DecimalFormat("0.0");
                layerName.setText(dataList.get(0).get("name")+"");
                length.setText(decimalFormat.format(Float.parseFloat(dataList.get(0).get("length")+"")));

                layerCount = dataList.size();
                for (int i = 1; i < layerCount; i++) {
                    TableRow tableRow = new TableRow(this);
                    tableRow.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT ));
                    tableRow.setFocusable(false);
                    tableRow.setFocusableInTouchMode(true);
                    for (int j = 0; j < 16; j++) {
                        EditText text = new EditText(this);
                        text.setLayoutParams(new TableRow.LayoutParams(80, LayoutParams.MATCH_PARENT));
                        text.setTextSize(14);
                        text.setMaxLines(5);
                        text.setGravity(Gravity.CENTER);
                        text.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
                        if (j == 0) {
                            text.setText(dataList.get(i).get("name")+"");
                        }
                        if (j == 1) {
                            text.setText(decimalFormat.format(Float.parseFloat(dataList.get(i).get("length")+"")));
                        }
                        if (i < layerCount-1 && j < 15) {
                            text.setBackgroundResource(R.drawable.table_frame);
                        }else if (i < layerCount-1 && j == 15) {
                            text.setBackgroundResource(R.drawable.table_frame_rt);
                        }else if (i == layerCount-1 && j < 15) {
                            text.setBackgroundResource(R.drawable.table_frame_btm);
                        }else {
                            text.setBackgroundResource(R.drawable.table_frame_last);
                        }
                        tableRow.addView(text);
                    }
                    tableLayout.addView(tableRow);
                }
                break;
        }

    }

    private void initEditText() {
        colorDry = (EditText) findViewById(R.id.id_tbl_color_dry);
        color_wet = (EditText) findViewById(R.id.id_tbl_color_wet);
        humidity = (EditText) findViewById(R.id.id_tbl_humidity);
        texture = (EditText) findViewById(R.id.id_tbl_texture);
        structure = (EditText) findViewById(R.id.id_tbl_structure);
        compactness = (EditText) findViewById(R.id.id_tbl_compactness);
        porosity = (EditText) findViewById(R.id.id_tbl_porosity);
        newGrowth_class = (EditText) findViewById(R.id.id_tbl_newGrowth_class);
        newGrowth_morphology = (EditText) findViewById(R.id.id_tbl_newGrowth_morphology);
        newGrowth_number = (EditText) findViewById(R.id.id_tbl_newGrowth_number);
        intrusion = (EditText) findViewById(R.id.id_tbl_intrusion);
        rootSys = (EditText) findViewById(R.id.id_tbl_rootSys);
        meas_PH = (EditText) findViewById(R.id.id_tbl_meas_PH);
        meas_limy_react = (EditText) findViewById(R.id.id_tbl_meas_limy_react);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.id_prop_tbl_save:
                //先让edittext失去焦点，再保存图片
                tableLayout.clearFocus();
//			//保存属性数据
//			saveInfoAttrProper();
                Toast.makeText(this, SaveViewAsImage.saveAsImg(tableLayout)?"保存成功":"保存失败", Toast.LENGTH_LONG).show();
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
    private void saveInfoAttrProper() {
        properInfoList = new ArrayList<List<String>>();
        for (int i = 0; i < layerCount; i++) {
            TableRow tRow = (TableRow) tableLayout.getChildAt(2+i);
            int colnumber = tRow.getChildCount();
            List<String> tmpList = new ArrayList<String>();
            for (int j = 0; j < colnumber; j++) {
                EditText et = (EditText) tRow.getChildAt(j);
                tmpList.add(et.getText()+"");
            }
            properInfoList.add(tmpList);
        }
        for (int i = 0; i < properInfoList.size(); i++) {
            InfoAttrProper proper = new InfoAttrProper();
            proper.setFilePath(saveFilePath);
            proper.setLayer(properInfoList.get(i).get(0));
            proper.setDepth(Float.parseFloat(properInfoList.get(i).get(1)));
            proper.setColor_dry(properInfoList.get(i).get(2));
            proper.setColor_wet(properInfoList.get(i).get(3));
            proper.setHumidity(properInfoList.get(i).get(4));
            proper.setTexture(properInfoList.get(i).get(5));
            proper.setStructure(properInfoList.get(i).get(6));
            proper.setPorosity(properInfoList.get(i).get(7));
            proper.setNewGrowth_class(properInfoList.get(i).get(8));
            proper.setNewGrowth_morphology(properInfoList.get(i).get(9));
            proper.setNewGrowth_number(properInfoList.get(i).get(10));
            proper.setIntrusion(properInfoList.get(i).get(11));
            proper.setRootSys(properInfoList.get(i).get(12));
            proper.setMeas_PH(properInfoList.get(i).get(13));
            proper.setMeasure_limy_reaction(properInfoList.get(i).get(14));
            soilNoteDB.saveInfoAttrFeat(proper);
        }
    }


}

