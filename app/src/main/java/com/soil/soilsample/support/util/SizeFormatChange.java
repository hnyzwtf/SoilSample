package com.soil.soilsample.support.util;

/**
 * Created by GIS on 2016/7/8 0008.
 */
public class SizeFormatChange {
    /**
     * @param size
     * @return
     * change byte to kb or M
     */
    public static String formatDataSize(int size) {
        String ret = "";
        if (size < (1024 * 1024)) {
            ret = String.format("%dK", size / 1024);
        } else {
            ret = String.format("%.1fM", size / (1024 * 1024.0));
        }
        return ret;
    }
}
