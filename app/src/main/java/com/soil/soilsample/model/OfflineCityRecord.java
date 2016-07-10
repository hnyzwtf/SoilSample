package com.soil.soilsample.model;

import java.util.ArrayList;

/**
 * Created by GIS on 2016/7/7 0007.
 * 城市记录结构类，包括，城市id，城市名称，城市数据包大小
 * 此类类似于百度sdk中自带的MKOLSearchRecord类
 */
public class OfflineCityRecord {
    private int cityId;
    private String cityName;
    private int citySize;
    private String downloadStatus;
    private ArrayList<OfflineCityRecord> childCities;//如果这个城市为省份的话，那么childCities就是他下面所有地级城市的列表

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getCitySize() {
        return citySize;
    }

    public void setCitySize(int citySize) {
        this.citySize = citySize;
    }

    public ArrayList<OfflineCityRecord> getChildCities() {
        return childCities;
    }

    public void setChildCities(ArrayList<OfflineCityRecord> childCities) {
        this.childCities = childCities;
    }

    public String getDownloadStatus() {
        return downloadStatus;
    }

    public void setDownloadStatus(String downloadStatus) {
        this.downloadStatus = downloadStatus;
    }
}
