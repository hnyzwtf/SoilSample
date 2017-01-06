package com.soil.soilsample.support.util;

/**
 * Created by GIS on 2017/1/6 0006.
 */

public class BaiduToWGS84 {
    public static double x_PI = 3.14159265358979324 * 3000.0 / 180.0;
    public static double PI = 3.1415926535897932384626;
    public static double a = 6378245.0;
    public static double ee = 0.00669342162296594323;

    /**
     * 百度坐标---纬度转wgs84的纬度
     * @param bd_lng
     * @param bd_lat
     * @return
     */
    public static double baiduLat2wgs(double bd_lng, double bd_lat)
    {
        // convert baidu to gcj
        bd_lng = +bd_lng;
        bd_lat = +bd_lat;
        double x = bd_lng - 0.0065;
        double y = bd_lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_PI);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_PI);
        double gg_lng = z * Math.cos(theta);
        double gg_lat = z * Math.sin(theta);
        // convert gcj to wgs84
        gg_lat = +gg_lat;
        gg_lng = +gg_lng;

        double dlat = transformlat(gg_lng - 105.0, gg_lat - 35.0);
        double radlat = gg_lat / 180.0 * PI;
        double magic = Math.sin(radlat);
        magic = 1 - ee * magic * magic;
        double sqrtmagic = Math.sqrt(magic);
        dlat = (dlat * 180.0) / ((a * (1 - ee)) / (magic * sqrtmagic) * PI);
        double mglat = gg_lat + dlat;
        double wgs_lat = gg_lat* 2 - mglat;
        return  wgs_lat;

    }
    public static double transformlat(double lng, double lat)
    {
        lat = +lat;
        lng = +lng;
        double ret = -100.0 + 2.0 * lng + 3.0 * lat + 0.2 * lat * lat + 0.1 * lng * lat + 0.2 * Math.sqrt(Math.abs(lng));
        ret += (20.0 * Math.sin(6.0 * lng * PI) + 20.0 * Math.sin(2.0 * lng * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(lat * PI) + 40.0 * Math.sin(lat / 3.0 * PI)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(lat / 12.0 * PI) + 320 * Math.sin(lat * PI / 30.0)) * 2.0 / 3.0;
        return ret;
    }
    /**
     * 百度坐标---经度转wgs84的经度
     * @param bd_lng
     * @param bd_lat
     * @return
     */
    public static double baiduLng2wgs(double bd_lng, double bd_lat)
    {
        // convert baidu to gcj
        bd_lng = +bd_lng;
        bd_lat = +bd_lat;
        double x = bd_lng - 0.0065;
        double y = bd_lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_PI);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_PI);
        double gg_lng = z * Math.cos(theta);
        double gg_lat = z * Math.sin(theta);
        // convert gcj to wgs84
        gg_lat = +gg_lat;
        gg_lng = +gg_lng;
        double dlng = transformlng(gg_lng - 105.0, gg_lat - 35.0);
        double radlat = gg_lat / 180.0 * PI;
        double magic = Math.sin(radlat);
        magic = 1 - ee * magic * magic;
        double sqrtmagic = Math.sqrt(magic);
        dlng = (dlng * 180.0) / (a / sqrtmagic * Math.cos(radlat) * PI);

        double mglng = gg_lng + dlng;
        double wgs_lng = gg_lng * 2 - mglng;
        return wgs_lng;
    }
    public static double transformlng(double lng, double lat)
    {
        lat = +lat;
        lng = +lng;
        double ret = 300.0 + lng + 2.0 * lat + 0.1 * lng * lng + 0.1 * lng * lat + 0.1 * Math.sqrt(Math.abs(lng));
        ret += (20.0 * Math.sin(6.0 * lng * PI) + 20.0 * Math.sin(2.0 * lng * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(lng * PI) + 40.0 * Math.sin(lng / 3.0 * PI)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(lng / 12.0 * PI) + 300.0 * Math.sin(lng / 30.0 * PI)) * 2.0 / 3.0;
        return ret;
    }
}
