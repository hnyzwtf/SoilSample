package com.soil.soilsample.ui.main;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.soil.soilsample.R;
import com.soil.soilsample.base.BaseActivity;
import com.soil.soilsample.base.BaseApplication;
import com.soil.soilsample.support.adapter.UploadListViewAdapter;
import com.soil.soilsample.support.util.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Request;

/**
 * 上传页面，更多请见https://github.com/hongyangAndroid/okhttp-utils
 */
public class UploadActivity extends BaseActivity {
    private Toolbar toolbar;
    private RelativeLayout listviewLayout;
    private LinearLayout textHintLayout;
    private ListView uploadListView;
    private TextView emptyHintText;
    private List<String> uploadFileList = new ArrayList<String>();//数据源即文件名list，他保存了SoilSample/upload文件夹下所有的tif文件名
    private UploadListViewAdapter uploadAdapter;
    // upload
    private String mUploadUrl = "http://222.192.7.122:8181/FileUpload/smartupload.jsp";
    private ProgressBar mProgressbar;

    private String uploadFilePath = BaseApplication.getAppUploadPath();
    private static final String TAG = "UploadActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        initView();
        initData();
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_white_24dp);
        getSupportActionBar().setTitle("环境因子上传");
        listviewLayout = (RelativeLayout) findViewById(R.id.rl_upload_listview);
        textHintLayout = (LinearLayout) findViewById(R.id.rl_empty_hint);
        uploadListView = (ListView) findViewById(R.id.lv_upload_files);
        emptyHintText = (TextView) findViewById(R.id.tv_empty_hint);
        mProgressbar = (ProgressBar) findViewById(R.id.upload_progressbar);
        mProgressbar.setMax(100);
    }
    private void initData()
    {

        uploadFileList = getUploadFileNameList(uploadFilePath);
        if (uploadFileList == null || uploadFileList.size() == 0)
        {
            textHintLayout.setVisibility(View.VISIBLE);
            listviewLayout.setVisibility(View.GONE);
            emptyHintText.setText("请将要上传的环境因子置于/storage/emulated/0/SoilSample/upload文件夹下");
        }else {
            showListViewCheckSpin();
        }
    }
    /**
     * 显示listview上的复选框和spinner
     */
    public void showListViewCheckSpin()
    {
        uploadAdapter = new UploadListViewAdapter(this, uploadFileList, R.layout.activity_upload_listview_item);
        uploadListView.setAdapter(uploadAdapter);
        getEnvironDataInfo();//先从本地取出保存的复选框和spinner的状态
        uploadListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                UploadListViewAdapter.ViewHolder holder = (UploadListViewAdapter.ViewHolder) view.getTag();
                holder.cb.toggle();// 在每次获取点击的item时改变checkbox的状态
                UploadListViewAdapter.getIsSelected().put(position, holder.cb.isChecked());// 将CheckBox的选中状况记录下来
            }
        });

    }
    public class MyStringCallBack extends StringCallback
    {
        @Override
        public void onBefore(Request request, int id) {
            mProgressbar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onError(Call call, Exception e, int id) {
            Log.d(TAG, "onError: " + e.getMessage());
            ToastUtil.show(UploadActivity.this, "上传失败   "+ e.getMessage());
        }

        @Override
        public void onResponse(String response, int id) {
            Log.d(TAG, "onResponse: " + response);
            ToastUtil.show(UploadActivity.this, "上传成功");
            mProgressbar.setVisibility(View.GONE);
        }

        @Override
        public void inProgress(float progress, long total, int id) {
            mProgressbar.setProgress((int) (100 * progress));
        }
    }

    public void multiFileUpload()
    {
        saveListviewState();//将用户的勾选状态和spinner的选择状态保存到本地
        SharedPreferences prefer = getSharedPreferences("EnvironDataInfo", MODE_PRIVATE);
        if (uploadFileList != null && uploadFileList.size() > 0)
        {
            //headers参数没有什么用
            Map<String, String> headers = new HashMap<>();
            headers.put("APP-Key", "APP-Secret222");
            headers.put("APP-Secret", "APP-Secret111");

            for (int i = 0; i < uploadFileList.size(); i++) {

                boolean isChecked = prefer.getBoolean(String.valueOf(i), false);
                if (isChecked)
                {
                    String fileItem = uploadFileList.get(i);
                    File file = new File(uploadFilePath, fileItem);
                    if (! file.exists())
                    {
                        ToastUtil.show(UploadActivity.this, "文件不存在");
                        return;
                    }

                    OkHttpUtils.post()
                            .addFile("mFile", fileItem, file)
                            .url(mUploadUrl)
                            .headers(headers)
                            .build()
                            .execute(new MyStringCallBack());

                }
            }

        }
    }
    /**
     * @param id
     * @param isChecked
     * 将checkbox的勾选状态和spinner的选择状态保存到本地
     */
    public void saveEnvironDataInfo(int id, boolean isChecked, int selectedSpinner)
    {

        SharedPreferences.Editor editor = getSharedPreferences("EnvironDataInfo", MODE_PRIVATE).edit();
        editor.putBoolean(String.valueOf(id), isChecked);//0-->false,int --> bool
        String environItem = uploadFileList.get(id);//slope.tif-->0    String-->int
        editor.putInt(environItem, selectedSpinner);
        editor.commit();
    }
    /*
    * 将上传文件列表uploadFileList保存到本地
    * */
    public void saveEnvironDataList()
    {
        SharedPreferences.Editor editor = getSharedPreferences("EnvironDataList", MODE_PRIVATE).edit();
        editor.putInt("EnvironNums", uploadFileList.size());
        for (int i = 0; i < uploadFileList.size(); i++)
        {
            editor.putString("item_"+i, uploadFileList.get(i));
        }

        editor.commit();
    }
    /**
     * ��SharedPreferences中读取保存的每一个环境因子的信息
     */
    public void getEnvironDataInfo()
    {
        SharedPreferences prefer = getSharedPreferences("EnvironDataInfo", MODE_PRIVATE);

        for (int i = 0; i < uploadFileList.size(); i++) {
            String environItem = uploadFileList.get(i);
            boolean isChecked = prefer.getBoolean(String.valueOf(i), false);
            int selectedSpinnerValue = prefer.getInt(environItem, -1);
            UploadListViewAdapter.getIsSelected().put(i, isChecked);
            UploadListViewAdapter.getSpinnerValue().put(environItem, selectedSpinnerValue);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_upload, menu);
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
            case R.id.action_upload:
                multiFileUpload();
                break;
            case R.id.action_select_all:
                selectAll();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 将listview的勾选状态保存到本地
     */
    private void saveListviewState()
    {
        for (int i = 0; i < uploadFileList.size(); i++) {
            String environItem = uploadFileList.get(i);
            saveEnvironDataInfo(i, UploadListViewAdapter.getIsSelected().get(i),
                    UploadListViewAdapter.getSpinnerValue().get(environItem));

        }
        saveEnvironDataList();

    }

    /**
     * 全选
     */
    private void selectAll()
    {
        if (uploadFileList.size() > 0)
        {
            for (int i = 0; i < uploadFileList.size(); i++) {
                UploadListViewAdapter.getIsSelected().put(i, true);
            }
            uploadAdapter.notifyDataSetChanged();//注意这一句必须加上，否则checkbox无法正常更新状态
        }else
        {
            ToastUtil.show(UploadActivity.this, "环境因子文件列表为空");
        }
    }

    /**
     * 获取soilsample/upload文件夹下，需要上传的环境因子名称列表
     *
     */
    private List<String> getUploadFileNameList(String filePath)
    {
        List<String> fileNames = new ArrayList<String>();

        File file = new File(filePath);
        if (! file.exists())
        {
            return null;
        }
        File[] subFiles = file.listFiles();
        for (File subFile : subFiles)
        {
            if (subFile.isFile())
            {
                if (subFile.getName().endsWith(".tif") || subFile.getName().endsWith(".asc"))
                {
                    fileNames.add(subFile.getName());
                }
            }else if (subFile.isDirectory()){
                getUploadFileNameList(subFile.getAbsolutePath());
            }
        }
        return fileNames;
    }

}
