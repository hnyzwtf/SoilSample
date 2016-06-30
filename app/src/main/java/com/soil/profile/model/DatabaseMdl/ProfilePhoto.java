package com.soil.profile.model.DatabaseMdl;

/**
 * Created by GIS on 2016/6/28 0028.
 */
public class ProfilePhoto {
    private String filePath;
    private float[] depths;
    private String[] layerNames;
    private int[] proLegendCodes;
    public String getFilePath() {
        return filePath;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    public float[] getDepths() {
        return depths;
    }
    public void setDepths(float[] depths) {
        this.depths = depths;
    }
    public String[] getLayerNames() {
        return layerNames;
    }
    public void setLayerNames(String[] layerNames) {
        this.layerNames = layerNames;
    }
    public int[] getProLegendCodes() {
        return proLegendCodes;
    }
    public void setProLegendCodes(int[] proLegendCodes) {
        this.proLegendCodes = proLegendCodes;
    }
}
