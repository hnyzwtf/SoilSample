package com.soil.profile.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.soil.profile.db.SoilNoteDB;
import com.soil.profile.model.CustomChooseMdl.ThreeLayerCusMdl;
import com.soil.profile.model.DatabaseMdl.ProfilePhoto;
import com.soil.profile.utils.BitmapUtils;
import com.soil.soilsample.R;
import com.soil.soilsample.base.BaseActivity;


public class ActivityImageDetail extends BaseActivity{
    private Toolbar toolbar;
    private ImageView iv;
    Bitmap bm;
    String imagePath;

    private FrameLayout frameLayout;
    ThreeLayerCusMdl three;

    // 数据库相关
    private SoilNoteDB soilNoteDB;
    private ProfilePhoto profilePhoto;

    private float[] lines;
    private float sumDepth = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);
        soilNoteDB = SoilNoteDB.getInstance(this);

        iv = (ImageView) findViewById(R.id.id_map_img_detail);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_white_24dp);
        frameLayout = (FrameLayout) findViewById(R.id.frame_layout_iv_detail);

        Intent intent = getIntent();
        imagePath = intent.getStringExtra("imagePath");

        ViewTreeObserver vto = iv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @SuppressLint("NewApi") @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                iv.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                //TODO 判断id是否相同
                bm = BitmapUtils.decodeSampledBitmapFromFile(imagePath, iv.getWidth(),iv.getHeight());
                //把图片设为背景
                Drawable drawable = new BitmapDrawable(bm);
                iv.setBackground(drawable);
            }
        });
//		profilePhoto = soilNoteDB.getProfile(imagePath);
//
//
//		int lyNum = profilePhoto.getLayerNames().length;
//
//		lines = new float[lyNum+2];
//		lines[0] = 0f;
//		for (int i = 0; i < lyNum; i++) {
//			sumDepth += profilePhoto.getDepths()[i];
//		}
//		float tmp = 0;
//		for (int i = 0; i < lyNum; i++) {
//			tmp += profilePhoto.getDepths()[i];
//			lines[i+1] = tmp/sumDepth;
//		}
//		lines[4] = 1;
//		Toast.makeText(this, ""+sumDepth, Toast.LENGTH_LONG).show();
//
//		switch (lyNum) {
//		case 3:
//			frameLayout.removeAllViews();
//			three = new ThreeLayerCusMdl(this, lines, profilePhoto.getLayerNames(), sumDepth);
//			three.setPro_name(profilePhoto.getProLegendCodes());
//			three.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//			three.setTag("profile");
//		    ViewTreeObserver vto = three.getViewTreeObserver();
//	        vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
//	            @SuppressLint("NewApi") @SuppressWarnings("deprecation")
//				@Override
//	            public void onGlobalLayout() {
//	            	three.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//	                //TODO 判断id是否相同
//	                bm = BitmapUtils.decodeSampledBitmapFromFile(imagePath, three.getWidth(),three.getHeight());
//	              //把图片设为背景
//					Drawable drawable = new BitmapDrawable(bm);
//					three.setBackground(drawable);
//	            }
//	        });
//	        frameLayout.addView(three);
//			break;
//
//		default:
//			break;
//		}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_image_detail, menu);
        menu.findItem(R.id.action_modify_attr).setVisible(false);
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
            case R.id.action_modify_attr:

                Intent intent = new Intent(this, ActivityAttributeHome.class);
                intent.putExtra("back_activity", "image_detail");
                intent.putExtra("saveFilePath", imagePath);
                startActivity(intent);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}

