package com.soil.soilsample.ui.function;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.soil.soilsample.R;
import com.soil.soilsample.base.BaseActivity;
import com.soil.soilsample.model.AsyncResponse;
import com.soil.soilsample.support.adapter.FuzzyMembershipAdapter;
import com.soil.soilsample.ui.empty.EmptyLayout;

import org.json.JSONArray;
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
public class FuzzyMembershipActivity extends BaseActivity {
    private Toolbar toolbar;
    private TextView membershipPathText;
    private EmptyLayout mEmptyLayout;
    private ListView membershipListView;
    private List<String> membershipData;
    private FuzzyMembershipAdapter mAdapter;
    private String TAG = "FuzzyMembership";
    private String membershipRequest = "{\"membershipLayers\":\"membershipLayers\"}";//请求服务器发送环境因子列表

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview_fuzzy_member);
        initView();
        sendRequestForMembership();
        initEvents();

    }
    private void initView()
    {
        membershipPathText = (TextView) findViewById(R.id.tv_membership_path);
        mEmptyLayout = (EmptyLayout) findViewById(R.id.membership_error_layout);
        membershipListView = (ListView) findViewById(R.id.lv_fuzzy_membership);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("模糊隶属度图层选择");
    }
    private void initEvents()
    {
       mEmptyLayout.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               sendRequestForMembership();
           }
       });
    }
    private void showListView()
    {
       /*
       // 直接使用ArrayAdapter也可实现带单选的listview，此时便不再需要FuzzyMembershipAdapter
       ListAdapter adapter = new ArrayAdapter<String>(this, R.layout.item_single_choice, membershipData) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                final ChoiceView view;
                if(convertView == null) {
                    view = new ChoiceView(FuzzyMembershipActivity.this);
                } else {
                    view = (ChoiceView)convertView;
                }
                view.setText(getItem(position));
                return view;
            }
        };
         membershipListView.setAdapter(adapter);*/
        membershipListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mAdapter = new FuzzyMembershipAdapter(this, membershipData);
        membershipListView.setAdapter(mAdapter);
        // 初始化时即默认第一项是选中的，并将第一项的选中信息保存到本地
        int selectItemId = getSelectedItemId();
        saveChoiceView(membershipData.get(0), 0, membershipPathText.getText().toString());
        membershipListView.setItemChecked(selectItemId, true);
        membershipListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // ToastUtil.show(FuzzyMembershipActivity.this, "you clicked " + membershipData.get(position) + ", id is " + String.valueOf(position));
                saveChoiceView(membershipData.get(position), position, membershipPathText.getText().toString());
            }
        });
    }
    /**
     * 启动AsyncTask
     */
    private void sendRequestForMembership()
    {
        mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);

        try {
            final SocketConnAsync socketConn = new SocketConnAsync();
            socketConn.execute(membershipRequest);

            socketConn.setOnAsyncResponse(new AsyncResponse() {
                //通过自定义的接口回调获取AsyncTask中onPostExecute返回的结果变量
                @Override
                public void onDataReceivedSuccess(List<String> listData) {
                    mEmptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                    membershipData = new ArrayList<String>();
                    membershipData = listData;
                    showListView();

                }

                @Override
                public void onDataReceivedFailed() {
                    mEmptyLayout.setErrorType(EmptyLayout.NODATA);

                }

                @Override
                public void onConnectServerFailed() {
                    mEmptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
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
                if ((msg != null) && !(msg.equals("{\"membershipLayers\":[\"noData\"]}")))
                {
                    List<String> listData = new ArrayList<String>();
                    String membershipPath = getMembershipPathFromJsonResponse(msg);
                    membershipPathText.setText(membershipPath);
                    listData = parseJsonResponse(msg);
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
    /**
     * @param msg
     * 解析从服务器返回的json字符串
     * 例如：{"membershipLayers":["class4.tif","class5.tif","class6.tif","E:/work/soilSample/similarity/membershipData"]}
     */
    private List<String> parseJsonResponse(String msg)
    {
        List<String> listData = new ArrayList<String>();//listview显示的数据源
        try {
            JSONObject jsonObject = new JSONObject(msg);
            JSONArray jsonArray = jsonObject.getJSONArray("membershipLayers");
            for (int i = 0; i < jsonArray.length()-1; i++) {
                listData.add(jsonArray.getString(i));
            }


        } catch (Exception e) {

            e.printStackTrace();
        }
        return listData;
    }
    /**
     * @param msg
     * 解析从服务器返回的json字符串，获得模糊隶属度图层路径
     */
    private String getMembershipPathFromJsonResponse(String msg)
    {
        String membershipDataPath = null;
        try {
            // 接收到的json数据，最后一个是服务器返回的环境因子变量路径

            JSONObject jsonObject = new JSONObject(msg);
            JSONArray jsonArray = jsonObject.getJSONArray("membershipLayers");
            for (int i = 0; i < jsonArray.length(); i++) {

                if (i >= jsonArray.length()-1)
                {
                    membershipDataPath = jsonArray.getString(i);
                }

            }


        } catch (Exception e) {

            e.printStackTrace();
        }
        return membershipDataPath;
    }

    /*
    * save the selected item and membership layer path into shared
    * */
    private void saveChoiceView(String itemName, int itemId, String path)
    {
        SharedPreferences.Editor editor = getSharedPreferences("MembershipSelectInfo", MODE_PRIVATE).edit();
        editor.putString("selectName", itemName);
        editor.putInt("selectId", itemId);
        editor.putString("membershipPath", path);
        editor.commit();
    }
    private int getSelectedItemId()
    {
        SharedPreferences preferences = getSharedPreferences("MembershipSelectInfo", MODE_PRIVATE);
        //String itemName = preferences.getString("selectName", null);
        int itemId = preferences.getInt("selectId", 0);
        //String membershipPath = preferences.getString("membershipPath", null);
        return itemId;
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
