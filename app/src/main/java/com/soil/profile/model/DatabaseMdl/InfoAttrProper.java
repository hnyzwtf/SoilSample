package com.soil.profile.model.DatabaseMdl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InfoAttrProper implements Serializable {

    private static final long serialVersionUID = 1L;

    private String filePath;
    private String layer;
    private float depth;
    private String color_dry;//颜色干
    private String color_wet;//颜色润
    private String humidity; //干湿度
    private String texture; //质地
    private String structure; //结构
    private String compactness; //松紧度
    private String porosity;   //孔隙度
    private String newGrowth_class;   //新生体类别
    private String newGrowth_morphology; //新生体形态
    private String newGrowth_number;  //新生体数量
    private String intrusion;  //侵入体
    private String rootSys; //根系
    private String meas_PH;
    private String meas_limy_react; //石灰反应

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getMeas_PH() {
        return meas_PH;
    }

    public void setMeas_PH(String meas_PH) {
        this.meas_PH = meas_PH;
    }

    public String getMeas_limy_react() {
        return meas_limy_react;
    }

    public void setMeas_limy_react(String meas_limy_react) {
        this.meas_limy_react = meas_limy_react;
    }

    public List<String> getProperContents() {
        List<String> result = new ArrayList<String>();
        result.add(layer);
        result.add(depth+"");
        result.add(color_dry);
        result.add(color_wet);
        result.add(humidity);
        result.add(texture);
        result.add(structure);
        result.add(compactness);
        result.add(porosity);
        result.add(newGrowth_class);
        result.add(newGrowth_morphology);
        result.add(newGrowth_number);
        result.add(intrusion);
        result.add(rootSys);
        result.add(meas_PH);
        result.add(meas_limy_react);
        return result;
    }

    public InfoAttrProper setAllProperContents(List<Map<String, Object>> list) {
        InfoAttrProper proper = new InfoAttrProper();
        proper.setLayer(list.get(0).get("content")+"");
        proper.setDepth(Float.parseFloat(list.get(1).get("content")+""));
        proper.setColor_dry(list.get(2).get("content")+"");
        proper.setColor_wet(list.get(3).get("content")+"");
        proper.setHumidity(list.get(4).get("content")+"");
        proper.setTexture(list.get(5).get("content")+"");
        proper.setStructure(list.get(6).get("content")+"");
        proper.setCompactness(list.get(7).get("content")+"");
        proper.setPorosity(list.get(8).get("content")+"");
        proper.setNewGrowth_class(list.get(9).get("content")+"");
        proper.setNewGrowth_morphology(list.get(10).get("content")+"");
        proper.setNewGrowth_number(list.get(11).get("content")+"");
        proper.setIntrusion(list.get(12).get("content")+"");
        proper.setRootSys(list.get(13).get("content")+"");
        proper.setMeasure_PH(list.get(14).get("content")+"");
        proper.setMeasure_limy_reaction(list.get(15).get("content")+"");
        return proper;
    }

    public String getLayer() {
        return layer;
    }
    public void setLayer(String layer) {
        this.layer = layer;
    }
    public float getDepth() {
        return depth;
    }
    public void setDepth(float depth) {
        this.depth = depth;
    }
    public String getColor_dry() {
        return color_dry;
    }
    public void setColor_dry(String color_dry) {
        this.color_dry = color_dry;
    }
    public String getColor_wet() {
        return color_wet;
    }
    public void setColor_wet(String color_wet) {
        this.color_wet = color_wet;
    }
    public String getHumidity() {
        return humidity;
    }
    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }
    public String getTexture() {
        return texture;
    }
    public void setTexture(String texture) {
        this.texture = texture;
    }
    public String getStructure() {
        return structure;
    }
    public void setStructure(String structure) {
        this.structure = structure;
    }
    public String getCompactness() {
        return compactness;
    }
    public void setCompactness(String compactness) {
        this.compactness = compactness;
    }
    public String getPorosity() {
        return porosity;
    }
    public void setPorosity(String porosity) {
        this.porosity = porosity;
    }
    public String getNewGrowth_class() {
        return newGrowth_class;
    }
    public void setNewGrowth_class(String newGrowth_class) {
        this.newGrowth_class = newGrowth_class;
    }
    public String getNewGrowth_morphology() {
        return newGrowth_morphology;
    }
    public void setNewGrowth_morphology(String newGrowth_morphology) {
        this.newGrowth_morphology = newGrowth_morphology;
    }
    public String getNewGrowth_number() {
        return newGrowth_number;
    }
    public void setNewGrowth_number(String newGrowth_number) {
        this.newGrowth_number = newGrowth_number;
    }
    public String getIntrusion() {
        return intrusion;
    }
    public void setIntrusion(String intrusion) {
        this.intrusion = intrusion;
    }
    public String getRootSys() {
        return rootSys;
    }
    public void setRootSys(String rootSys) {
        this.rootSys = rootSys;
    }
    public String getMeasure_PH() {
        return meas_PH;
    }
    public void setMeasure_PH(String measure_PH) {
        this.meas_PH = measure_PH;
    }
    public String getMeasure_limy_reaction() {
        return meas_limy_react;
    }
    public void setMeasure_limy_reaction(String measure_limy_reaction) {
        this.meas_limy_react = measure_limy_reaction;
    }
}

