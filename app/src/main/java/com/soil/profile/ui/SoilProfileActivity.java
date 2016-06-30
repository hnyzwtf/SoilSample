package com.soil.profile.ui;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.soil.soilsample.support.util.GetFilePathFromDevice;
import com.soil.soilsample.R;
import com.soil.soilsample.base.BaseActivity;
import com.soil.soilsample.base.BaseApplication;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by GIS on 2016/6/28 0028.
 */
public class SoilProfileActivity extends BaseActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private LinearLayout takePhoto;
    private LinearLayout localProfile;
    private final String[] photoSelect_item={"拍摄照片","从文件中选择"};

    private static final int TAKE_PHOTO = 1;
    private static final int CHOOSE_PHOTO = 2;
    private static final int CROP_PHOTO = 3;

    private Uri imageUri;
    private String imageFilePath;
    private static final String TAG = "SoilProfileActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.soil_profile);
        initView();
        initEvents();
    }
    private void initView() {

        takePhoto = (LinearLayout) findViewById(R.id.rl_take_photo);
        localProfile = (LinearLayout) findViewById(R.id.rl_local_profile);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_white_24dp);
        getSupportActionBar().setTitle("土壤剖面");
    }

    private void initEvents() {
        takePhoto.setOnClickListener(this);
        localProfile.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.rl_take_photo:
                doTakePhoto();
                /*final AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setItems(photoSelect_item, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which==0)
                        {
                            doTakePhoto();
                        }
                        else {
                            doSelectImageFromLocal();
                        }
                    }
                });
                AlertDialog adDialog=builder.create();
                adDialog.show();*/
                break;
            case R.id.rl_local_profile:
                Intent intent = new Intent(SoilProfileActivity.this, LoadProfileExistedActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }

    }
    // 接收回传结果（第二个页面中用的是setResult方法）
    // requestCode 请求码
    // resultCode 结果码（在第二个页面中也有）
    // data 回传的数据
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        Uri uri = data.getData();
                        String picPath = GetFilePathFromDevice.getPath(SoilProfileActivity.this, uri);
                        //Log.d(TAG, "onActivityResult: " + picPath);
                        Uri selectedImageUri = Uri.fromFile(new File(picPath));

                        //调用系统裁剪
                        Intent intent2 = new Intent("com.android.camera.action.CROP");
                        intent2.setDataAndType(selectedImageUri, "image");
                        intent2.putExtra("scale", true);//裁剪时是否保留图片的比例，这里的比例是1:1
                        intent2.putExtra(MediaStore.EXTRA_OUTPUT, selectedImageUri);
                        startActivityForResult(intent2, CROP_PHOTO);
                       /* String[] filePathColumns={MediaStore.Images.Media.DATA};
                        Cursor c = this.getContentResolver().query(selectedImageUri, filePathColumns, null,null, null);
                        //将光标移至开头 ，这个很重要，不小心很容易引起越界
                        c.moveToFirst();
                        int columnIndex = c.getColumnIndex(filePathColumns[0]);
                        //最后根据索引值获取图片路径
                        String selectedImagePath= c.getString(columnIndex);
                        c.close();
                        Intent intent = new Intent(SoilProfileActivity.this, ActivityEditPhoto.class);
                        intent.putExtra("imageFilePath", selectedImagePath);*/

                       // startActivity(intent);
                        //刷新一下系统环境，为了能在相册中找到
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                        {
                            MediaScannerConnection.scanFile(this, new String[] { Environment.getExternalStorageDirectory().toString() }, null, new MediaScannerConnection.OnScanCompletedListener() {
                                public void onScanCompleted(String path, Uri uri)
                                {
                                }
                            });
                        }else
                        {
                            sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                                    Uri.parse("file://"+ Environment.getExternalStorageDirectory())));
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
//				saveInfoGeo();
//				Intent intent = new Intent(ActivityHome.this, ActivityEditPhoto.class);
//				intent.putExtra("imageFilePath", imageFilePath);
//				startActivity(intent);
                    //调用系统裁剪
                    Intent intent1 = new Intent("com.android.camera.action.CROP");
                    intent1.setDataAndType(imageUri, "image/*");
                    intent1.putExtra("scale", true);//裁剪时是否保留图片的比例，这里的比例是1:1
                    intent1.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent1, CROP_PHOTO);
                }
                break;
            case CROP_PHOTO:
                if (resultCode == RESULT_OK || resultCode == RESULT_CANCELED) {
                    Intent intent = new Intent(SoilProfileActivity.this, ActivityEditPhoto.class);
                    intent.putExtra("imageFilePath", imageFilePath);
                    startActivity(intent);
                    //刷新一下系统环境，为了能在相册中找到
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                    {
                        MediaScannerConnection.scanFile(this, new String[] { Environment.getExternalStorageDirectory().toString() }, null, new MediaScannerConnection.OnScanCompletedListener() {
                            /*
                             *   (non-Javadoc)
                             * @see android.media.MediaScannerConnection.OnScanCompletedListener#onScanCompleted(java.lang.String, android.net.Uri)
                             */
                            public void onScanCompleted(String path, Uri uri)
                            {
                            }
                        });
                    }else
                    {
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                                Uri.parse("file://"+ Environment.getExternalStorageDirectory())));
                    }
                }
                break;
            default:
                break;
        }
    }
    // 拍照获取图片
    protected void doTakePhoto() {
        try {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // 调用系统相机
            // 获得文件保存路径
            imageFilePath = getPhotoPath();
            //Log.d(TAG, "doTakePhoto: " + imageFilePath);
            // 加载路径
            imageUri = Uri.fromFile(new File(imageFilePath));
            // 指定存储路径，这样就可以保存原图了
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(cameraIntent, TAKE_PHOTO);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    // 从本地选择图片
    private void doSelectImageFromLocal() {
        Intent localIntent = new Intent();
        localIntent.setType("image/*");
        localIntent.setAction("android.intent.action.GET_CONTENT");
//		Intent localIntent2 = Intent.createChooser(localIntent, "选择图片");
        startActivityForResult(localIntent, CHOOSE_PHOTO);
    }

    @SuppressLint("SimpleDateFormat")
    public static String getPhotoPath() {
        String mFilePath = "";
        // 获得最终图片保存的路径
        String pathStorage = null;
        try {
            pathStorage = BaseApplication.getAppPath() + "/SoilNote/";
            // 创建文件夹
            File file = new File(pathStorage);
            if (!file.exists()) {
                file.mkdirs();
            }
            // 文件名以日期命名
            String name = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
            // 返回文件路径
            mFilePath = pathStorage + "/" + name + ".jpg";
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mFilePath;
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
