package com.soil.soilsample.ui.sampleinfo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.soil.soilsample.R;
import com.soil.soilsample.base.BaseActivity;
import com.soil.soilsample.base.BaseApplication;
import com.soil.soilsample.model.AsyncResponse;
import com.soil.soilsample.model.CoordinateAlterSample;
import com.soil.soilsample.support.util.ToastUtil;
import com.soil.soilsample.ui.dialog.CustomWaitDialog;
import com.soil.soilsample.ui.function.EnvironVariableActivity;
import com.soil.soilsample.ui.function.FCMActivity;
import com.soil.soilsample.ui.function.FuzzyMembershipActivity;
import com.soil.soilsample.ui.main.MainActivity;

import org.json.JSONArray;
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
 * Created by GIS on 2016/6/21 0021.
 */
public class AlterParamsFCMActivity extends BaseActivity implements View.OnClickListener{
    private Toolbar toolbar;
    private Button selectEnvironBtn;
    private Button fcmBtn;
    private Button selectMembershipBtn;
    private String markerLongLat;//注意，发送给服务器的坐标，经度在前，纬度在后
    private String markerLongLatShow;//默认显示在coorUnaccessEdit中的坐标，保留了小数位

    private EditText coorUnaccessFCMEdit;
    private EditText membershipThresholdEdit;
    private EditText candidateFCMRadiusEdit;

    private String coorUnaccessFCM;//发送给服务器的不可采点坐标
    private String membershipThreshold;//发送给服务器的确定候选样点时不可采点与其他栅格点隶属度差值
    private String candidateFCMRadius;//发送给服务器的可替代样点距离不可采点的距离

    private String membershipPath =  null;//发送给服务器的模糊隶属度图的路径

    //可替代样点的集合,每个样点包含name,x,y
    private ArrayList<CoordinateAlterSample> alterFCMSamples = new ArrayList<CoordinateAlterSample>();

    private String jsonRequestForAlterFCMSample = null;//发送给服务器的json字符串请求
    Dialog waitDialog = null;
    private String markerName;
    private Boolean hasSelectEnviron = true;//用户是否已经选择了环境因子，默认选中

    public String TAG = "AlterParamsFCM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.alternative_params_fcm);
        getIntentParams();
        initView();
        initEvents();
        //getMembershipParamsFromShared();
        initCoorEditText();

    }
    /*
    * 取出从SampleInfoActivity发送来的数据
    * */
    private void getIntentParams()
    {
        Intent intent = getIntent();
        markerLongLat = intent.getStringExtra("markerLongLat");
        markerLongLatShow = intent.getStringExtra("markerLongLatShow");
        markerName = intent.getStringExtra("markerName");
    }
    private void initView() {
        coorUnaccessFCMEdit = (EditText) findViewById(R.id.edit_unaccess_coor_fcm);
        membershipThresholdEdit = (EditText) findViewById(R.id.edit_membership_threshold);
        candidateFCMRadiusEdit = (EditText) findViewById(R.id.edit_candidate_radius_fcm);
        selectEnvironBtn = (Button) findViewById(R.id.btn_select_environ_fcm);
        fcmBtn = (Button) findViewById(R.id.btn_fcm);
        selectMembershipBtn = (Button) findViewById(R.id.btn_fuzzy_membership_map);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_white_24dp);
        getSupportActionBar().setTitle("参数设置");
    }
    private void initEvents()
    {
        selectEnvironBtn.setOnClickListener(this);
        fcmBtn.setOnClickListener(this);
        selectMembershipBtn.setOnClickListener(this);
    }

    /**
     * 从SharedPreferences中读取保存的每一个环境因子的信息
     */
    public void getMembershipParamsFromShared()
    {
        try
        {
            SharedPreferences preferDataInfo = getSharedPreferences("MembershipSelectInfo", MODE_PRIVATE);

            String environmentPath = preferDataInfo.getString("membershipPath", null);//隶属度图路径
            String environmentName = preferDataInfo.getString("selectName", null);
            if (environmentName != null)
            {
                membershipPath = environmentPath + "/" + environmentName;
            }
            else
            {
                showDialog();
            }

        }catch (Exception e)
        {

            e.printStackTrace();
            showDialog();
        }

    }// end of getEnvironParamsFromShared
    private void initCoorEditText()
    {
        coorUnaccessFCMEdit.setText(markerLongLatShow);//默认显示当前操作样点的坐标
        membershipThresholdEdit.setText("0.02");
        candidateFCMRadiusEdit.setText("1000");
    }
    public void getEditText()
    {
        coorUnaccessFCM = markerLongLatShow;
        membershipThreshold = membershipThresholdEdit.getText().toString();
        candidateFCMRadius = candidateFCMRadiusEdit.getText().toString();
    }
    private void generateJson()
    {
        getEditText();

        String jsonResult = "";
        JSONObject object = new JSONObject();
        try{
            object.put("membershipLyrsPath", membershipPath);
            object.put("coordinateFCM", coorUnaccessFCM);
            object.put("memberThreshold", membershipThreshold);
            object.put("candidateFCMRadius", candidateFCMRadius);
            jsonResult = object.toString();

        }catch (JSONException e)
        {
            e.printStackTrace();
        }
        jsonRequestForAlterFCMSample = jsonResult;
    }
    public void sendRequest()
    {
        generateJson();
        //Log.d(TAG, "jsonRequestForAlterSample:"+jsonRequestForAlterSample);
        try {
            final SocketConnAsync socketConn = new SocketConnAsync();
            socketConn.execute(jsonRequestForAlterFCMSample);

            socketConn.setOnAsyncResponse(new AsyncResponse() {

                @Override
                public void onDataReceivedSuccess(List<String> listData) {

                }

                @Override
                public void onCoorXYReceivedSuccess(List<String> listX, List<String> listY) {

                    if (alterFCMSamples != null)
                    {
                        ToastUtil.show(AlterParamsFCMActivity.this,"可替代样点计算成功，即将返回至地图界面");
                        waitDialog.dismiss();
                        Intent intentToMain = new Intent(AlterParamsFCMActivity.this, MainActivity.class);
                        intentToMain.putParcelableArrayListExtra("alterFCMSampleList", alterFCMSamples);
                        startActivity(intentToMain);
                    }
                    else {
                        ToastUtil.show(AlterParamsFCMActivity.this,"计算失败！");
                        waitDialog.dismiss();
                    }

                }

                @Override
                public void onDataReceivedFailed() {
                    ToastUtil.show(AlterParamsFCMActivity.this,"服务器没有返回任何数据");
                    waitDialog.dismiss();
                }

                @Override
                public void onConnectServerFailed() {
                    ToastUtil.show(AlterParamsFCMActivity.this,"连接服务器失败，请检查网络后重试");
                    waitDialog.dismiss();
                }
            });

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_select_environ:
                Intent intentToEnvironVariableActivity = new Intent(AlterParamsFCMActivity.this, EnvironVariableActivity.class);
                startActivity(intentToEnvironVariableActivity);
                break;
            case R.id.btn_fcm:
                Intent intentToFCMActivity = new Intent(AlterParamsFCMActivity.this, FCMActivity.class);
                startActivity(intentToFCMActivity);
                break;
            case R.id.btn_fuzzy_membership_map:
                Intent intentToFuzzyMemberActivity = new Intent(AlterParamsFCMActivity.this, FuzzyMembershipActivity.class);
                startActivity(intentToFuzzyMemberActivity);
                break;
            default:
                break;
        }
    }

    /**
     * @author GIS
     * AsyncTask连接服务器
     */
    class SocketConnAsync extends AsyncTask<String, Void, String>
    {
        public AsyncResponse asyncResponse;
        String serverIP = BaseApplication.getServerIP();//223.2.36.52
        int listenPort = BaseApplication.getPort();
        int connTimeOut = BaseApplication.getConnTimeOut();
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
                SocketAddress address = new InetSocketAddress(serverIP, listenPort);
                socketConn.connect(address, connTimeOut);
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
            //Log.d(TAG, "onPostExecute----msg is "+msg);
            if (connected)
            {
                if (msg != null)
                {
                    //先暂时不做处理
                    List<String> coorXs = new ArrayList<String>();
                    List<String> coorYs = new ArrayList<String>();
                    List<String> costValues = new ArrayList<String>();
                    coorXs = parseJsonCoorXsFromServer(msg);
                    coorYs = parseJsonCoorYsFromServer(msg);
                    costValues = parseJsonCostValueFromServer(msg);

                    setAlterSamples(coorXs, coorYs, costValues);
                    saveAlterSamplesToSharedPrefer();
                    asyncResponse.onCoorXYReceivedSuccess(coorXs, coorYs);//将结果传给回调接口中的函数

                }
                else {
                    asyncResponse.onDataReceivedFailed();
                }
            }else {
                asyncResponse.onConnectServerFailed();
            }



        }


    }//end of class asynctask

    /*
    * 解析从服务器返回的json结果，例如：
    * {"candidateXCoors":[118.6865897871420,118.6874806529536,118.6883715187652],"candidateYCoors":[30.80287335533615,30.79930989208982,30.80020075790141]}
    * */
    private List<String> parseJsonCoorXsFromServer(String jsonData)
    {
        List<String> coorXs = new ArrayList<String>();

        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray arrayXs = jsonObject.getJSONArray("candidateFCMX");

            for (int i = 0; i < arrayXs.length(); i++)
            {
                coorXs.add(arrayXs.getString(i));

            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return coorXs;

    }
    private List<String> parseJsonCoorYsFromServer(String jsonData)
    {

        List<String> coorYs = new ArrayList<String>();
        try {
            JSONObject jsonObject = new JSONObject(jsonData);

            JSONArray arrayYs = jsonObject.getJSONArray("candidateFCMY");
            for (int i = 0; i < arrayYs.length(); i++)
            {

                coorYs.add(arrayYs.getString(i));
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return coorYs;

    }
    /*
    * 从返回的字符串中取出每个样点的可达性数值
    * */
    private List<String> parseJsonCostValueFromServer(String jsonData)
    {

        List<String> costValue = new ArrayList<String>();
        try {
            JSONObject jsonObject = new JSONObject(jsonData);

            JSONArray arrayYs = jsonObject.getJSONArray("walkCostValue");
            for (int i = 0; i < arrayYs.length(); i++)
            {
                costValue.add(arrayYs.getString(i));
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return costValue;

    }
    /*
    * 每个可替代样点包括，一个ID，坐标x，坐标Y
    * 获得所有可替代样点的集合即List<CoordinateAlterSample> alterSamples，每一个元素就是一个可替代样点
    * */
    private void setAlterSamples(List<String> coorXs, List<String> coorYs, List<String> costValues)
    {
        ArrayList<CoordinateAlterSample> samplesList = new ArrayList<CoordinateAlterSample>();
        int sampleNum = coorXs.size();
        for (int i = 0; i < sampleNum; i++)
        {
            CoordinateAlterSample sample = new CoordinateAlterSample();
            sample.setName(String.valueOf(i));
            sample.setX(Double.valueOf(coorXs.get(i)));//坐标是string，将其转为double
            sample.setY(Double.valueOf(coorYs.get(i)));
            sample.setCostValue(costValues.get(i));
            samplesList.add(sample);
        }
        this.alterFCMSamples = samplesList;
    }
    private ArrayList<CoordinateAlterSample> getAlterSample()
    {
        return alterFCMSamples;
    }
    /*
    * save the List<CoordinateAlterSample> alterSamples to SharedPreferences
    * */
    private void saveAlterSamplesToSharedPrefer()
    {
        clearAlterSamplesListInShared();
        //SharedPreferences.Editor editor = getSharedPreferences("AlterFCMSamplesList", MODE_PRIVATE).edit();
        SharedPreferences.Editor editor = getSharedPreferences("AlterSamplesList", MODE_PRIVATE).edit();
        Gson gson = new Gson();
        String json = gson.toJson(alterFCMSamples);
        Log.d(TAG, "saved json is "+ json);
        editor.putString(markerName, json);
        editor.putString("alterMarkerName", markerName);
        editor.commit();

    }
    /*
	* 如果AlterSamplesList中保存着上次生成的可替代样点结果，就清除掉
	* */
    private void clearAlterSamplesListInShared()
    {
        //SharedPreferences preferences = getSharedPreferences("AlterFCMSamplesList", MODE_PRIVATE);
        SharedPreferences.Editor editor = getSharedPreferences("AlterSamplesList", MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();

    }
    private void showDialog()
    {
        hasSelectEnviron = false;//表示用户没有选择环境因子
        AlertDialog.Builder dialog = new AlertDialog.Builder(AlterParamsFCMActivity.this);
        dialog.setTitle("警告");
        dialog.setMessage("你还没有选择隶属度图层");
        dialog.setCancelable(false);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent intentToEnvironVariableActivity = new Intent(AlterParamsFCMActivity.this, EnvironVariableActivity.class);
                startActivity(intentToEnvironVariableActivity);
            }
        });
        dialog.show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_alter_params_fcm, menu);
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
                getMembershipParamsFromShared();
                if (hasSelectEnviron)
                {

                    waitDialog = CustomWaitDialog.show(AlterParamsFCMActivity.this, "计算中，请稍后...", true, null);
                    sendRequest();
                }

                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onResume() {
        super.onResume();
        hasSelectEnviron = true;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

