package com.soil.profile.utils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
/**
 * Created by GIS on 2016/6/28 0028.
 */
public class HumanPosition {
    public static String getHumanPosition(double latitude, double longtitude) {
        String humanPosition = "";
        try {
            // 组装反向地理编码的接口地址
            StringBuilder url = new StringBuilder();
            url.append("http://maps.googleapis.com/maps/api/geocode/json?latlng=");
            url.append(latitude).append(",");
            url.append(longtitude);
            url.append("&sensor=false");
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url.toString());
            // 在请求消息头中指定语言，保证服务器会返回中文数据
            httpGet.addHeader("Accept-Language", "zh-CN");
            HttpResponse httpResponse = httpClient.execute(httpGet);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = httpResponse.getEntity();
                String response = EntityUtils.toString(entity, "utf-8");
                JSONObject jsonObject = new JSONObject(response);
                // 获取results节点下的位置信息
                JSONArray resultArray = jsonObject.getJSONArray("results");
                if (resultArray.length() > 0) {
                    JSONObject subObject = resultArray.getJSONObject(0);// 取结果中的第一条
                    // 取出格式化后的位置信息
                    humanPosition = subObject.getString("formatted_address");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return humanPosition;
    }
}
