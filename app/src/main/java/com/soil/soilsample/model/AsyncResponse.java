package com.soil.soilsample.model;

import java.util.List;

/**
 * 接口函数，用于处理AsyncTask中的结果返回值
 *
 */
public interface AsyncResponse {
	
	void onDataReceivedSuccess(List<String> listData);
	// 专门用于处理服务器返回的可替代样点坐标
	void onCoorXYReceivedSuccess(List<String> listDataX, List<String> listDataY);
	void onDataReceivedFailed();
	void onConnectServerFailed();
}
