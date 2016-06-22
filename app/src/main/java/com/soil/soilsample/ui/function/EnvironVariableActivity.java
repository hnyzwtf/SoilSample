package com.soil.soilsample.ui.function;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.soil.soilsample.R;
import com.soil.soilsample.base.BaseActivity;
import com.soil.soilsample.base.BaseApplication;
import com.soil.soilsample.model.AsyncResponse;
import com.soil.soilsample.support.adapter.MyEnvironListViewAdapter;
import com.soil.soilsample.support.util.ToastUtil;
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
 * 环境因子选择，从服务器获取环境因子列表，填充到listview中，每个listview有一个checkbox和一个spinner
 * 当用户选择完毕之后，返回时即将每个list item的选择状态保存到SharedPreferences中EnvironDataInfo
 * 将环境因子列表即List<String> environmentList保存到SharedPreferences中EnvironDataSet
 */
public class EnvironVariableActivity extends BaseActivity implements OnClickListener{
	private Toolbar toolbar;

	private TextView environPathText;//一个隐藏控件，用来保存环境因子变量的路径
	private EmptyLayout mEmptyLayout;
	private ListView environListView;
	private List<String> environmentList = new ArrayList<String>();//listview上显示的数据源
	private MyEnvironListViewAdapter mAdapter;
	//当用户进入此Activity时有时可能什么都没有操作，此时isCheckBoxStateChanged仍然为0，就不用将当前的checkbox的状态保存到本地了
	//当用户进行了操作时，比如点击按钮，勾选checkbox，这时候就改变isCheckBoxStateChanged的数值
	private int isCheckBoxStateChanged = 0;
	private static final String TAG = "EnvironVariableActivity";
	private String listDataRequest = "{\"environData\":\"environData\"}";//请求服务器发送环境因子列表

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.environ_listview);
		initView();
		sendRequestForListData();
		initEvents();
	}

	private void initView() {
		environListView = (ListView) findViewById(R.id.lv_environment);
		environPathText = (TextView) findViewById(R.id.tv_environ_path);
		mEmptyLayout = (EmptyLayout) findViewById(R.id.error_layout);
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_white_24dp);
		getSupportActionBar().setTitle("环境因子选择");

	}
	private void initEvents()
	{
		mEmptyLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
				sendRequestForListData();
			}
		});
	}

	/**
	 * 显示listview上的复选框和spinner
	 */
	public void showListViewCheckSpin()
	{
		mAdapter = new MyEnvironListViewAdapter(this, environmentList, R.layout.environ_listview_item);
		environListView.setAdapter(mAdapter);
		getEnvironDataInfo();//先从本地取出保存的复选框和spinner的状态
		environListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					MyEnvironListViewAdapter.ViewHolder holder = (MyEnvironListViewAdapter.ViewHolder) view.getTag();
					holder.cb.toggle();// 在每次获取点击的item时改变checkbox的状态
					MyEnvironListViewAdapter.getIsSelected().put(position, holder.cb.isChecked());// 将CheckBox的选中状况记录下来
					isCheckBoxStateChanged++;
				}
			});

	}


	/**
	 * 启动AsyncTask
	 */
	private void sendRequestForListData()
	{
		mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);

		try {
			final SocketConnAsync socketConn = new SocketConnAsync();
			socketConn.execute(listDataRequest);

			socketConn.setOnAsyncResponse(new AsyncResponse() {
				//通过自定义的接口回调获取AsyncTask中onPostExecute返回的结果变量
				@Override
				public void onDataReceivedSuccess(List<String> listData) {
					mEmptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
					environmentList = listData;
					//获取到environmentList之后，再调用下面的两个函数，这样适配器获取到的environmentList就不是空值了
					showListViewCheckSpin();
				}

				@Override
				public void onCoorXYReceivedSuccess(List<String> listDataX, List<String> listDataY) {
					// 专门用于处理服务器返回的可替代样点坐标
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
			if (connected)
			{
				if ((msg != null) && !(msg.equals("{\"tifFileNames\":[\"noData\"]}")))
				{
					List<String> listData = new ArrayList<String>();
					String environDataPath = getEnvironPathFromJsonResponse(msg);
					environPathText.setText(environDataPath);
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
	 * 例如：{"tifFileNames":["dem2.tif","geo2.tif","jiangshui2.tif","slope2.tif","E:/work/soilSample/similarity/environData"]}
	 */
	private List<String> parseJsonResponse(String msg)
	{
		List<String> listData = new ArrayList<String>();//listview显示的数据源
		try {
			JSONObject jsonObject = new JSONObject(msg);
			JSONArray jsonArray = jsonObject.getJSONArray("tifFileNames");
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
	 * 解析从服务器返回的json字符串，获得环境变量路径
	 */
	private String getEnvironPathFromJsonResponse(String msg)
	{
		String environDataPath = null;
		try {
			// 接收到的json数据，最后一个是服务器返回的环境因子变量路径

			JSONObject jsonObject = new JSONObject(msg);
			JSONArray jsonArray = jsonObject.getJSONArray("tifFileNames");
			for (int i = 0; i < jsonArray.length(); i++) {

				if (i >= jsonArray.length()-1)
				{
					environDataPath = jsonArray.getString(i);
				}

			}


		} catch (Exception e) {

			e.printStackTrace();
		}
		return environDataPath;
	}

	@Override
	public void onClick(View v) {


	}

	/**
	 * @param id
	 * @param isChecked
	 * 将checkbox的勾选状态和spinner的选择状态,环境因子数据路径保存到本地
	 */
	public void saveEnvironDataInfo(int id, boolean isChecked, int selectedSpinner, String path)
	{

		SharedPreferences.Editor editor = getSharedPreferences("EnvironDataInfo", MODE_PRIVATE).edit();
		editor.putBoolean(String.valueOf(id), isChecked);//0-->false,int --> bool
		String environItem = environmentList.get(id);//slope.tif-->0    String-->int
		editor.putInt(environItem, selectedSpinner);
		String environmentPath = "environmentPath";
		editor.putString(environmentPath, path);//保存环境变量路径到本地

		editor.commit();
	}
	/*
	* 将环境因子列表environmentList保存到本地
	* */
	public void saveEnvironDataList()
	{
		SharedPreferences.Editor editor = getSharedPreferences("EnvironDataList", MODE_PRIVATE).edit();
		editor.putInt("EnvironNums", environmentList.size());
		for (int i = 0; i < environmentList.size(); i++)
		{
			editor.putString("item_"+i, environmentList.get(i));
		}

		editor.commit();
	}
	/**
	 * ��SharedPreferences中读取保存的每一个环境因子的信息
	 */
	public void getEnvironDataInfo()
	{
		SharedPreferences prefer = getSharedPreferences("EnvironDataInfo", MODE_PRIVATE);

		for (int i = 0; i < environmentList.size(); i++) {
			String environItem = environmentList.get(i);
			boolean isChecked = prefer.getBoolean(String.valueOf(i), false);
			int selectedSpinnerValue = prefer.getInt(environItem, -1);
			MyEnvironListViewAdapter.getIsSelected().put(i, isChecked);
			MyEnvironListViewAdapter.getSpinnerValue().put(environItem, selectedSpinnerValue);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_environ_variable, menu);
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
			case R.id.action_save:
				saveListviewState();
				break;
			case R.id.action_select_all:
				selectAll();
				break;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	private void saveListviewState()
	{
		//退出之前先判断，若用户更改了checkbox的状态或spinnerVaule的数值就将状态保存到本地，若用户没有进行任何操作则不保存
		if (environmentList.size() > 0)
		{

			String path = environPathText.getText().toString();
			if ((isCheckBoxStateChanged != 0) ||
					((MyEnvironListViewAdapter.spinnerValueChanged % environmentList.size()) != 0)) {
				for (int i = 0; i < environmentList.size(); i++) {
					String environItem = environmentList.get(i);
					saveEnvironDataInfo(i, MyEnvironListViewAdapter.getIsSelected().get(i),
							MyEnvironListViewAdapter.getSpinnerValue().get(environItem), path);

				}

			}
			saveEnvironDataList();
		}
		ToastUtil.show(EnvironVariableActivity.this, "保存成功");
	}

	/**
	 * 全选
	 */
	private void selectAll()
	{
		if (environmentList.size() > 0)
		{
			for (int i = 0; i < environmentList.size(); i++) {
				MyEnvironListViewAdapter.getIsSelected().put(i, true);
			}
			mAdapter.notifyDataSetChanged();//注意这一句必须加上，否则checkbox无法正常更新状态
			isCheckBoxStateChanged++;
		}else
		{
			ToastUtil.show(EnvironVariableActivity.this, "环境因子列表为空");
		}
	}
}
