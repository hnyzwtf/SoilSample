package com.soil.soilsample.ui.function;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import com.soil.soilsample.R;
import com.soil.soilsample.base.BaseActivity;
import com.soil.soilsample.model.AsyncResponse;
import com.soil.soilsample.support.util.ToastUtil;
import com.soil.soilsample.ui.dialog.CustomWaitDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by GIS on 2016/6/20 0020.
 */
public class FCMActivity extends BaseActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private EditText mValueEdit;
    private EditText iterationNumEdit;
    private EditText maxErrorEdit;
    private EditText clusterNumEdit;

    private String mValue = null;//从edittext中获取用户输入的文本
    private String iterationNum = null;
    private String maxError = null;
    private String clusterNum = null;
    private StringBuffer multiEnvironPaths =  new StringBuffer();//发送给服务器的环境因子路径
    private String jsonRequestForFCM = null;//发送给服务器的json字符串请求
    private Dialog waitDialog = null;

    private String TAG = "FCMActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.fcmcluster);

        initView();
        initEvents();
        getEnvironParamsFromShared();
        initEditText();
    }
    private void initView() {
        mValueEdit = (EditText) findViewById(R.id.edit_fcm_m);
        iterationNumEdit = (EditText) findViewById(R.id.edit_fcm_iteration_num);
        maxErrorEdit = (EditText) findViewById(R.id.edit_fcm_max_error);
        clusterNumEdit = (EditText) findViewById(R.id.edit_fcm_cluster_num);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_white_24dp);
        getSupportActionBar().setTitle("FCM");
    }
    private void initEvents() {


    }
    /**
     * 从SharedPreferences中读取保存的每一个环境因子的信息
     */
    public void getEnvironParamsFromShared()
    {
        int selectedEnviron = 0;//计数器，若选择一个环境因子，则计数器加1
        try
        {
            SharedPreferences preferDataList = getSharedPreferences("EnvironDataList", MODE_PRIVATE);//取出环境因子列表的list
            SharedPreferences preferDataInfo = getSharedPreferences("EnvironDataInfo", MODE_PRIVATE);

            int environNums = preferDataList.getInt("EnvironNums", 0);//环境因子数量
            String environmentPath = preferDataInfo.getString("environmentPath", null);//环境因子路径
            StringBuffer multiEnvironPathsTemp = null;
            multiEnvironPathsTemp = new StringBuffer();
            for (int i = 0; i < environNums; i++) {
                String environItem = preferDataList.getString("item_"+i, null);
                boolean isChecked = preferDataInfo.getBoolean(String.valueOf(i), false);
                if (isChecked)
                {
                    selectedEnviron++;
                    multiEnvironPathsTemp.append(environmentPath).append("/").append(environItem).append("#");
                }

            }// end of for (int i = 0; i < environNums; i++)

            if (selectedEnviron == 0)
            {
                showDialog();
            }else
            {
                //去掉最后的#号
                multiEnvironPaths = multiEnvironPathsTemp.deleteCharAt(multiEnvironPathsTemp.length()-1);
            }
        }catch (Exception e)
        {

            e.printStackTrace();
            showDialog();
        }

    }// end of getEnvironParamsFromShared

    private void initEditText()
    {
        mValueEdit.setText("2");
        iterationNumEdit.setText("50");
        maxErrorEdit.setText("0.01");
        clusterNumEdit.setText("8");
    }
    private void getEditText()
    {
        mValue = mValueEdit.getText().toString();
        iterationNum = iterationNumEdit.getText().toString();
        maxError = maxErrorEdit.getText().toString();
        clusterNum = clusterNumEdit.getText().toString();
    }
    private void generateJson()
    {
        getEditText();
        String jsonResult = "";
        JSONObject object = new JSONObject();
        try{
            object.put("environLyrsPath", multiEnvironPaths.toString());
            object.put("mValue", mValue);
            object.put("iterationNum", iterationNum);
            object.put("maxError", maxError);
            object.put("clusterNum", clusterNum);
            jsonResult = object.toString();
            jsonRequestForFCM = jsonResult;

        }catch (JSONException e)
        {
            e.printStackTrace();
        }

    }
    public void sendRequest()
    {
        generateJson();
        try {
            final SocketConnAsync socketConn = new SocketConnAsync();
            socketConn.execute(jsonRequestForFCM);
            socketConn.setOnAsyncResponse(new AsyncResponse() {
                @Override
                public void onDataReceivedSuccess(List<String> listData) {
                    ToastUtil.show(FCMActivity.this,"FCM聚类计算完成");
                    waitDialog.dismiss();
                }

                @Override
                public void onDataReceivedFailed() {
                    ToastUtil.show(FCMActivity.this,"计算失败！");
                    waitDialog.dismiss();
                }

                @Override
                public void onConnectServerFailed() {
                    ToastUtil.show(FCMActivity.this,"连接服务器失败，请检查网络后重试");
                    waitDialog.dismiss();
                }
            });


        } catch (Exception e) {

            e.printStackTrace();
        }
    }
    /**
     * @author GIS
     * AsyncTask连接服务器
     */
    class SocketConnAsync extends AsyncTask<String, Void, String>
    {
        public AsyncResponse asyncResponse;
        String serverIP = "222.192.7.122";//223.2.36.52
        String listenPort = "8181";
        Socket socketConn = null;
        BufferedOutputStream outputStream = null;
        BufferedReader bufferedReader = null;
        String dataReceived = null;
        public boolean connected = false;
        public boolean listening = true;

        public void setOnAsyncResponse(AsyncResponse asyncResponse)
        {
            this.asyncResponse = asyncResponse;
        }

        @Override
        protected String doInBackground(String... params) {
            try {

                socketConn = new Socket();
                SocketAddress address = new InetSocketAddress(serverIP, 8181);
                socketConn.connect(address, 6000);
                socketConn.setTcpNoDelay(true);
                connected = true;

                String sendData = params[0];
                outputStream = new BufferedOutputStream(socketConn.getOutputStream());
                outputStream.write(sendData.getBytes());
                outputStream.flush();
                // start to receive data from the server
                bufferedReader = new BufferedReader(new InputStreamReader(socketConn.getInputStream()));
                dataReceived = bufferedReader.readLine();

                outputStream.close();
                bufferedReader.close();
                socketConn.close();
            }

            catch (Exception e) {
                connected = false;
                listening = false;
                e.printStackTrace();
            }

            return dataReceived;

        }

        @Override
        protected void onPostExecute(String msg) {

            super.onPostExecute(msg);
            if (connected)
            {
                if (msg != null && msg.equals("{\"fcmClusterCompleted\":[\"calcFCMCompleted\"]}"))
                {
                    List<String> listData = new ArrayList<String>();

                    asyncResponse.onDataReceivedSuccess(listData);//将结果传给回调接口中的函数
                }
                else {
                    asyncResponse.onDataReceivedFailed();
                }
            }else {
                asyncResponse.onConnectServerFailed();
            }
        }
    }//end of class asynctask

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_fcmcluster, menu);
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
            case R.id.action_ok:
                waitDialog = CustomWaitDialog.show(FCMActivity.this, "计算中，请稍后...", true, null);
                sendRequest();

                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

    }
    private void showDialog()
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(FCMActivity.this);
        dialog.setTitle("警告");
        dialog.setMessage("你还没有选择环境因子数据");
        dialog.setCancelable(false);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        dialog.show();
    }

}
