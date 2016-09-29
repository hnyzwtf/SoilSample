package com.soil.soilsample.ui.sampleinfo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
 * 点击“计算此点的候选样点”之后弹出的页面，用于进行以下参数设置：
 * 1 输入不可采的样点的投影坐标；2 后台程序计算相似度和推理不确定性的阈值，即不确定性阈值;
 * 3 筛选生成一系列候选样点时的阈值，即将所有栅格点的相似度与不可采点的相似度之差必须小于这个阈值才能当做候选样点；
 * 4 候选样点位于不可采点某个半径范围内；5 attributeRules
 */
public class AlterParamsActivity extends BaseActivity implements View.OnClickListener{
    private Toolbar toolbar;
    private Button selectEnvironBtn;//点击这里，选择环境因子按钮
    private String markerLongLat;//注意，发送给服务器的坐标，经度在前，纬度在后
    private String markerLongLatShow;//默认显示在coorUnaccessEdit中的坐标，保留了小数位

    private EditText coorUnaccessEdit;
    private EditText simiThresholdEdit;//相似度计算的阈值，应该改为simiThresholdEdit
    private EditText candidateThresholdEdit;
    private EditText candidateRadiusEdit;

    private String coorUnaccess;//发送给服务器的不可采点坐标
    private String simiThreshold;//发送给服务器的计算相似度时的阈值
    private String candidateThreshold;//发送给服务器的筛选出候选样点时的阈值
    private String candidateRadius;//发送给服务器的候选样点距离不可采点的距离
    private StringBuffer attributeRules = new StringBuffer();//发送给服务器的attributeRules
    private StringBuffer multiEnvironPaths =  new StringBuffer();//发送给服务器的环境因子路径

    //private List<String> resultXs = new ArrayList<String>();//从服务器返回的可替代样点的坐标
    //private List<String> resultYs = new ArrayList<String>();
    //可替代样点的集合,每个样点包含name,x,y
    private ArrayList<CoordinateAlterSample> alterSamples = new ArrayList<CoordinateAlterSample>();

    private String jsonRequestForAlterSample = null;//发送给服务器的json字符串请求
    Dialog waitDialog = null;
    private String markerName;
    private Boolean hasSelectEnviron = true;//用户是否已经选择了环境因子，默认选中

    private static final String TAG = "AlternativeParams";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.alternative_params);
        getIntentParams();
        initView();
        initEvents();
        //getEnvironParamsFromShared();
        initCoorEditText();
    }
    /*
    * 取出从MainActivity发送来的数据
    * */
    private void getIntentParams()
    {
        Intent intent = getIntent();
        markerLongLat = intent.getStringExtra("markerLongLat");
        markerLongLatShow = intent.getStringExtra("markerLongLatShow");
        markerName = intent.getStringExtra("markerName");
    }
    private void initView() {
        coorUnaccessEdit = (EditText) findViewById(R.id.edit_unaccess_coor);
        simiThresholdEdit = (EditText) findViewById(R.id.edit_simi_threshold);
        candidateThresholdEdit = (EditText) findViewById(R.id.edit_candidate_threshold);
        candidateRadiusEdit = (EditText) findViewById(R.id.edit_candidate_radius);
        selectEnvironBtn = (Button) findViewById(R.id.btn_select_environ);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_white_24dp);
        getSupportActionBar().setTitle("参数设置");
    }
    private void initEvents()
    {
        selectEnvironBtn.setOnClickListener(this);
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
            StringBuffer attributeRulesTemp = null;
            attributeRulesTemp = new StringBuffer();

            for (int i = 0; i < environNums; i++) {
                String environItem = preferDataList.getString("item_"+i, null);

                boolean isChecked = preferDataInfo.getBoolean(String.valueOf(i), false);
                int selectedSpinnerValue = preferDataInfo.getInt(environItem, -1);
                if (isChecked)
                {
                    selectedEnviron++;
                    multiEnvironPathsTemp.append(environmentPath).append("/").append(environItem).append("#");
                    switch (selectedSpinnerValue)
                    {
                        //这里的顺序必须和arrays里面的顺序保持一致，分别是climate,geology,terrain,vegetation,other
                        case 0:
                            attributeRulesTemp.append("Climate?Gower").append("#");
                            break;
                        case 1:
                            attributeRulesTemp.append("Geology?Boolean").append("#");
                            break;
                        case 2:
                            attributeRulesTemp.append("Terrain?Gower").append("#");
                            break;
                        case 3:
                            attributeRulesTemp.append("Vegetation?Gower").append("#");
                            break;
                        case 4:
                            attributeRulesTemp.append("Other?Gower").append("#");
                            break;
                        default:
                            break;
                    }
                }

            }// end of for (int i = 0; i < environNums; i++)


            if (selectedEnviron == 0)
            {
                showDialog();
            }else
            {
                //去掉最后的#号
                multiEnvironPaths = multiEnvironPathsTemp.deleteCharAt(multiEnvironPathsTemp.length()-1);
                attributeRules = attributeRulesTemp.deleteCharAt(attributeRulesTemp.length()-1);
            }
        }catch (Exception e)
        {

            e.printStackTrace();
            showDialog();
        }

        //Log.d(TAG, "attributeRules in getEnvironParamsFromShared:"+attributeRules.toString());
		/*getEditText();
		jsonRequestForAlterSample = generateJson(multiEnvironPaths, coorUnaccess, attributeRules, uncertainThreshold,
				candidateThreshold, candidateRadius);*/


    }// end of getEnvironParamsFromShared
    private void initCoorEditText()
    {
        coorUnaccessEdit.setText(markerLongLatShow);//默认显示当前操作样点的坐标
        simiThresholdEdit.setText("0.5");
        candidateThresholdEdit.setText("0.9");
        candidateRadiusEdit.setText("500");

    }

    /**
     * 获取最终发送至服务器的参数
     */
    public void getEditText()
    {
        coorUnaccess = markerLongLatShow;
        simiThreshold = simiThresholdEdit.getText().toString();
        candidateThreshold = candidateThresholdEdit.getText().toString();
        candidateRadius = candidateRadiusEdit.getText().toString();
    }
    private void generateJson()
    {
        //getEnvironParamsFromShared();
        getEditText();

        String jsonResult = "";
        JSONObject object = new JSONObject();
        try{
            object.put("environLyrsPath", multiEnvironPaths.toString());
            object.put("coordinate", coorUnaccess);
            object.put("attributeRules", attributeRules.toString());
            object.put("threshold", simiThreshold);
            object.put("candidateThreshold", candidateThreshold);
            object.put("candidateRadius", candidateRadius);
            jsonResult = object.toString();

        }catch (JSONException e)
        {
            e.printStackTrace();
        }
        jsonRequestForAlterSample = jsonResult;
    }
    public void sendRequest()
    {
        generateJson();
        //Log.d(TAG, "jsonRequestForAlterSample is :"+jsonRequestForAlterSample);
        try {
            final SocketConnAsync socketConn = new SocketConnAsync();
            socketConn.execute(jsonRequestForAlterSample);

            socketConn.setOnAsyncResponse(new AsyncResponse() {

                @Override
                public void onDataReceivedSuccess(List<String> listData) {

                }

                @Override
                public void onCoorXYReceivedSuccess(List<String> listX, List<String> listY) {
                    if (alterSamples != null)
                    {

                        ToastUtil.show(AlterParamsActivity.this,"可替代样点计算成功，即将返回至地图界面");
                        waitDialog.dismiss();
                        Intent intentToMain = new Intent(AlterParamsActivity.this, MainActivity.class);
                        intentToMain.putParcelableArrayListExtra("alterSampleList", alterSamples);
                        startActivity(intentToMain);
                    }
                    else {
                        ToastUtil.show(AlterParamsActivity.this,"计算失败！");
                        waitDialog.dismiss();
                    }

                }

                @Override
                public void onDataReceivedFailed() {
                    ToastUtil.show(AlterParamsActivity.this,"计算失败！");
                    waitDialog.dismiss();
                }

                @Override
                public void onConnectServerFailed() {
                    ToastUtil.show(AlterParamsActivity.this,"连接服务器失败，请检查网络后重试");
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
                Intent intentToEnvironVariableActivity = new Intent(AlterParamsActivity.this, EnvironVariableActivity.class);
                startActivity(intentToEnvironVariableActivity);
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
           // Log.d(TAG, "onPostExecute: msg is " + msg);
            if (connected)
            {
                if (msg != null)
                {

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
            JSONArray arrayXs = jsonObject.getJSONArray("candidateXCoors");

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

            JSONArray arrayYs = jsonObject.getJSONArray("candidateYCoors");
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
            sample.setCostValue(costValues.get(i));//设置可达性数值
            samplesList.add(sample);
        }
        this.alterSamples = samplesList;
    }
    private ArrayList<CoordinateAlterSample> getAlterSample()
    {
        return alterSamples;
    }
    /*
    * save the List<CoordinateAlterSample> alterSamples to SharedPreferences
    * */
    private void saveAlterSamplesToSharedPrefer()
    {
        clearAlterSamplesListInShared();
        SharedPreferences.Editor editor = getSharedPreferences("AlterSamplesList", MODE_PRIVATE).edit();
        //editor.clear();
        Gson gson = new Gson();
        String json = gson.toJson(alterSamples);
        //Log.d(TAG, "saved json is "+ json);
        editor.putString(markerName, json);
        editor.putString("alterMarkerName", markerName);
        editor.commit();

    }
    /*
    * 如果AlterSamplesList中保存着上次生成的可替代样点结果，就清除掉
    * */
    private void clearAlterSamplesListInShared()
    {
        /*SharedPreferences preferences = getSharedPreferences("AlterSamplesList", MODE_PRIVATE);
        String json = preferences.getString(markerName, null);
        if (json != null)
        {
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.commit();
        }*/
        SharedPreferences.Editor editor = getSharedPreferences("AlterSamplesList", MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();

    }
    private void showDialog()
    {
        hasSelectEnviron = false;//表示用户没有选择环境因子
        AlertDialog.Builder dialog = new AlertDialog.Builder(AlterParamsActivity.this);
        dialog.setTitle("警告");
        dialog.setMessage("你还没有选择环境因子数据");
        dialog.setCancelable(false);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent intentToEnvironVariableActivity = new Intent(AlterParamsActivity.this, EnvironVariableActivity.class);
                startActivity(intentToEnvironVariableActivity);
            }
        });
        dialog.show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_alternative_params, menu);
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
                getEnvironParamsFromShared();
                if (hasSelectEnviron)
                {

                    waitDialog = CustomWaitDialog.show(AlterParamsActivity.this, "计算中，请稍后...", true, null);
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

