package com.soil.soilsample.support.util;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;

/**
 * Created by GIS on 2016/6/23 0023.
 */
public class CoorToBaidu {
    public static LatLng GPS2Baidu09ll(double latitude, double longitude)
    {
        LatLng sourceLatLng = new LatLng(latitude, longitude);
        LatLng destLatLng = null;
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.GPS);
        converter.coord(sourceLatLng);
        destLatLng = converter.convert();
        return destLatLng;
    }
}
