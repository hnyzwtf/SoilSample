package com.soil.profile.model.DatabaseMdl;

public class InfoAttrRec {
    private String filePath;//剖面的存储路径，以此作为剖面图与属性的连接字段

    private String NO;//剖面编号
    private String date;
    private String weather;
    private String investigator; //调查人
    private String position;
    private String sheet_NO;//地形图幅/航（卫）片号
    private String common_name;//俗名
    private String formal_name;//正式定名
    private String terrain;    //地形
    private String altitude;   //海拔
    private String prnt_mat_type; //母质类型
    private String nat_veg;    //自然植被
    private String erode_stat; //侵蚀情况
    private String phreatic_level; //潜水位
    private String water_quality; //水质
    private String landuse;  //土地利用
    private String irrig_drainage; //排灌条件
    private String ferti_stat; //施肥情况
    private String human_effect; //人为影响
    private String crop_rotat_stat;//轮作状况
    private String yield;      //一般产量
    private String review;     //综合评述
    public String getFilePath() {
        return filePath;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    public String getErode_stat() {
        return erode_stat;
    }
    public void setErode_stat(String erode_stat) {
        this.erode_stat = erode_stat;
    }
    public String getNO() {
        return NO;
    }
    public void setNO(String nO) {
        NO = nO;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getWeather() {
        return weather;
    }
    public void setWeather(String weather) {
        this.weather = weather;
    }
    public String getInvestigator() {
        return investigator;
    }
    public void setInvestigator(String investigator) {
        this.investigator = investigator;
    }
    public String getPosition() {
        return position;
    }
    public void setPosition(String position) {
        this.position = position;
    }
    public String getSheet_NO() {
        return sheet_NO;
    }
    public void setSheet_NO(String sheet_NO) {
        this.sheet_NO = sheet_NO;
    }
    public String getCommon_name() {
        return common_name;
    }
    public void setCommon_name(String common_name) {
        this.common_name = common_name;
    }
    public String getFormal_name() {
        return formal_name;
    }
    public void setFormal_name(String formal_name) {
        this.formal_name = formal_name;
    }
    public String getTerrain() {
        return terrain;
    }
    public void setTerrain(String terrain) {
        this.terrain = terrain;
    }
    public String getAltitude() {
        return altitude;
    }
    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }
    public String getPrnt_mat_type() {
        return prnt_mat_type;
    }
    public void setPrnt_mat_type(String prnt_mat_type) {
        this.prnt_mat_type = prnt_mat_type;
    }
    public String getNat_veg() {
        return nat_veg;
    }
    public void setNat_veg(String nat_veg) {
        this.nat_veg = nat_veg;
    }
    public String getErode_sitn() {
        return erode_stat;
    }
    public void setErode_sitn(String erode_sitn) {
        this.erode_stat = erode_sitn;
    }
    public String getPhreatic_level() {
        return phreatic_level;
    }
    public void setPhreatic_level(String phreatic_level) {
        this.phreatic_level = phreatic_level;
    }
    public String getWater_quality() {
        return water_quality;
    }
    public void setWater_quality(String water_quality) {
        this.water_quality = water_quality;
    }
    public String getLanduse() {
        return landuse;
    }
    public void setLanduse(String landuse) {
        this.landuse = landuse;
    }
    public String getIrrig_drainage() {
        return irrig_drainage;
    }
    public void setIrrig_drainage(String irrig_drainage) {
        this.irrig_drainage = irrig_drainage;
    }
    public String getFerti_stat() {
        return ferti_stat;
    }
    public void setFerti_stat(String ferti_stat) {
        this.ferti_stat = ferti_stat;
    }
    public String getHuman_effect() {
        return human_effect;
    }
    public void setHuman_effect(String human_effect) {
        this.human_effect = human_effect;
    }
    public String getCrop_rotat_stat() {
        return crop_rotat_stat;
    }
    public void setCrop_rotat_stat(String crop_rotat_stat) {
        this.crop_rotat_stat = crop_rotat_stat;
    }
    public String getYield() {
        return yield;
    }
    public void setYield(String yield) {
        this.yield = yield;
    }
    public String getReview() {
        return review;
    }
    public void setReview(String review) {
        this.review = review;
    }
}

