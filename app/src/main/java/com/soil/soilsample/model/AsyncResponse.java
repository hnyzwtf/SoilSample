package com.soil.soilsample.model;

import java.util.List;

/**
 * 接口函数，用于处理AsyncTask中的结果返回值
 *
 */
public interface AsyncResponse {
	
	void onDataReceivedSuccess(List<String> listData);
	void onDataReceivedFailed();
	void onConnectServerFailed();
}
