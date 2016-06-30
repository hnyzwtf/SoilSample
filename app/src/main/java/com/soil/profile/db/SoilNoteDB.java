package com.soil.profile.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.soil.profile.model.DatabaseMdl.InfoAttrProper;
import com.soil.profile.model.DatabaseMdl.InfoAttrRec;
import com.soil.profile.model.DatabaseMdl.InfoGeo;
import com.soil.profile.model.DatabaseMdl.ProfilePhoto;

import java.util.ArrayList;
import java.util.List;

/*
 * SoilNoteDB类用于封装一些常用的数据库操作，如
 * 将InfoGeo实例存入数据库
 * 从数据库读取所有InfoGeo信息
 */

public class SoilNoteDB {
    /*
     * 数据库名
     */
    public static final String DB_NAME = "soil_note";

    /*
     * 数据库版本
     */
    public static final int VERSION = 1;

    /*
     * 声明SoilNoteDB对象
     */
    private static SoilNoteDB soilNoteDB;

    private SQLiteDatabase db;

    /*
     * 将构造方法私有化 在实例化这个类的时候，也同时实例化SoilNoteOpenHelper类，并且创建好数据库
     */
    private SoilNoteDB(Context context) {
        SoilNoteOpenHelper dbHelper = new SoilNoteOpenHelper(context, DB_NAME,
                null, VERSION);// 实例化
        db = dbHelper.getWritableDatabase();// 创建数据库中的表
    }

    /*
     * 获取SoilNoteDB的实例
     */
    public synchronized static SoilNoteDB getInstance(Context context) {
        if (soilNoteDB == null) {
            soilNoteDB = new SoilNoteDB(context);
        }
        return soilNoteDB;
    }

    /*
     * 将InfoGeo实例存储到数据库
     */
    public void saveInfoGeo(InfoGeo infoGeo) {
        if (infoGeo != null) {
            ContentValues values = new ContentValues();
            values.put("imagePath", infoGeo.getImagePath());
            values.put("latitude", infoGeo.getLatitude());
            values.put("longtitude", infoGeo.getLongtitude());
            values.put("position", infoGeo.getPosition());
            db.insert("InfoGeo", null, values);
        }
    }

    /*
     * 从数据库读取所有照片的地理信息
     */
    public List<InfoGeo> loadInfoGeo() {
        List<InfoGeo> list = new ArrayList<InfoGeo>();
        Cursor cursor = db.query("InfoGeo", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                InfoGeo infoGeo = new InfoGeo();
                infoGeo.setImagePath(cursor.getString(cursor
                        .getColumnIndex("imagePath")));
                infoGeo.setLatitude(cursor.getDouble(cursor
                        .getColumnIndex("latitude")));
                infoGeo.setLongtitude(cursor.getDouble(cursor
                        .getColumnIndex("longtitude")));
                infoGeo.setPosition(cursor.getString(cursor
                        .getColumnIndex("position")));
                list.add(infoGeo);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }

    /*
     * 从数据库中删除土壤剖面的地理信息
     */
    public void deleteInfoGeo(String deletedFile){
        db.delete("InfoGeo", "imagePath = ?", new String[]{ deletedFile });
    }

    /*
     * 将InfoAttrFeat实例存储到数据库
     */
    public void saveInfoAttrFeat(InfoAttrProper infoAttrProper) {
        if (infoAttrProper != null) {
            ContentValues values = new ContentValues();
            values.put("filePath", infoAttrProper.getFilePath());
            values.put("layer", infoAttrProper.getLayer());
            values.put("depth", infoAttrProper.getDepth());
            values.put("color_dry", infoAttrProper.getColor_dry());
            values.put("color_wet", infoAttrProper.getColor_wet());
            values.put("humidity", infoAttrProper.getHumidity());
            values.put("texture", infoAttrProper.getTexture());
            values.put("structure", infoAttrProper.getStructure());
            values.put("compactness", infoAttrProper.getCompactness());
            values.put("porosity", infoAttrProper.getPorosity());
            values.put("newGrowth_class", infoAttrProper.getNewGrowth_class());
            values.put("newGrowth_morphology", infoAttrProper.getNewGrowth_morphology());
            values.put("newGrowth_number", infoAttrProper.getNewGrowth_number());
            values.put("intrusion", infoAttrProper.getIntrusion());
            values.put("rootSys", infoAttrProper.getRootSys());
            values.put("meas_PH", infoAttrProper.getMeasure_PH());
            values.put("meas_limy_react", infoAttrProper.getMeasure_limy_reaction());
            db.insert("InfoAttrProper", null, values);
        }
    }

    /*
     * 从数据库读取所有土壤剖面的性状信息
     */
    public List<InfoAttrProper> loadInfoAttrProper() {
        List<InfoAttrProper> list = new ArrayList<InfoAttrProper>();
        Cursor cursor = db.query("InfoAttrProper", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                InfoAttrProper infoAttrProper = new InfoAttrProper();
                infoAttrProper.setFilePath(cursor.getString(cursor
                        .getColumnIndex("filePath")));
                infoAttrProper.setLayer(cursor.getString(cursor
                        .getColumnIndex("layer")));
                infoAttrProper.setDepth(cursor.getFloat(cursor
                        .getColumnIndex("depth")));
                infoAttrProper.setColor_dry(cursor.getString(cursor
                        .getColumnIndex("color_dry")));
                infoAttrProper.setColor_wet(cursor.getString(cursor
                        .getColumnIndex("color_wet")));
                infoAttrProper.setHumidity(cursor.getString(cursor
                        .getColumnIndex("humidity")));
                infoAttrProper.setTexture(cursor.getString(cursor
                        .getColumnIndex("texture")));
                infoAttrProper.setStructure(cursor.getString(cursor
                        .getColumnIndex("structure")));
                infoAttrProper.setCompactness(cursor.getString(cursor
                        .getColumnIndex("compactness")));
                infoAttrProper.setPorosity(cursor.getString(cursor
                        .getColumnIndex("porosity")));
                infoAttrProper.setNewGrowth_class(cursor.getString(cursor
                        .getColumnIndex("newGrowth_class")));
                infoAttrProper.setNewGrowth_morphology(cursor.getString(cursor
                        .getColumnIndex("newGrowth_morphology")));
                infoAttrProper.setNewGrowth_number(cursor.getString(cursor
                        .getColumnIndex("newGrowth_number")));
                infoAttrProper.setIntrusion(cursor.getString(cursor
                        .getColumnIndex("intrusion")));
                infoAttrProper.setRootSys(cursor.getString(cursor
                        .getColumnIndex("rootSys")));
                infoAttrProper.setMeasure_PH(cursor.getString(cursor
                        .getColumnIndex("meas_PH")));
                infoAttrProper.setMeasure_limy_reaction(cursor.getString(cursor
                        .getColumnIndex("meas_limy_react")));
                list.add(infoAttrProper);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }

	/*
	 * 根据条件选取剖面性状信息
	 */

    public List<InfoAttrProper> selectInfoProper(String filePath){
        List<InfoAttrProper> list = new ArrayList<InfoAttrProper>();
        String selectString = "select * from ProfilePhoto where filePath = ?";
        Cursor cursor = db.rawQuery(selectString, new String[]{filePath});
        if (cursor.moveToFirst()) {
            do {
                InfoAttrProper infoAttrProper = new InfoAttrProper();
                infoAttrProper.setFilePath(cursor.getString(cursor
                        .getColumnIndex("filePath")));
                infoAttrProper.setLayer(cursor.getString(cursor
                        .getColumnIndex("layer")));
                infoAttrProper.setDepth(cursor.getFloat(cursor
                        .getColumnIndex("depth")));
                infoAttrProper.setColor_dry(cursor.getString(cursor
                        .getColumnIndex("color_dry")));
                infoAttrProper.setColor_wet(cursor.getString(cursor
                        .getColumnIndex("color_wet")));
                infoAttrProper.setHumidity(cursor.getString(cursor
                        .getColumnIndex("humidity")));
                infoAttrProper.setTexture(cursor.getString(cursor
                        .getColumnIndex("texture")));
                infoAttrProper.setStructure(cursor.getString(cursor
                        .getColumnIndex("structure")));
                infoAttrProper.setCompactness(cursor.getString(cursor
                        .getColumnIndex("compactness")));
                infoAttrProper.setPorosity(cursor.getString(cursor
                        .getColumnIndex("porosity")));
                infoAttrProper.setNewGrowth_class(cursor.getString(cursor
                        .getColumnIndex("newGrowth_class")));
                infoAttrProper.setNewGrowth_morphology(cursor.getString(cursor
                        .getColumnIndex("newGrowth_morphology")));
                infoAttrProper.setNewGrowth_number(cursor.getString(cursor
                        .getColumnIndex("newGrowth_number")));
                infoAttrProper.setIntrusion(cursor.getString(cursor
                        .getColumnIndex("intrusion")));
                infoAttrProper.setRootSys(cursor.getString(cursor
                        .getColumnIndex("rootSys")));
                infoAttrProper.setMeasure_PH(cursor.getString(cursor
                        .getColumnIndex("meas_PH")));
                infoAttrProper.setMeasure_limy_reaction(cursor.getString(cursor
                        .getColumnIndex("meas_limy_react")));
                list.add(infoAttrProper);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }

    /*
     * 从数据库中删除土壤剖面的某条性状信息
     */
    public void deleteInfoAttrProper(String deletedFile){
        db.delete("InfoAttrProper", "filePath = ?", new String[]{deletedFile});
    }

    /*
     * 修改数据库中土壤剖面的性状信息
     */
    public void updateInfoAttrProper(InfoAttrProper infoAttrProper){
        if (infoAttrProper != null) {
            ContentValues values = new ContentValues();
            values.put("layer", infoAttrProper.getLayer());
            values.put("depth", infoAttrProper.getDepth());
            values.put("color_dry", infoAttrProper.getColor_dry());
            values.put("color_wet", infoAttrProper.getColor_wet());
            values.put("humidity", infoAttrProper.getHumidity());
            values.put("texture", infoAttrProper.getTexture());
            values.put("structure", infoAttrProper.getStructure());
            values.put("compactness", infoAttrProper.getCompactness());
            values.put("porosity", infoAttrProper.getPorosity());
            values.put("newGrowth_class", infoAttrProper.getNewGrowth_class());
            values.put("newGrowth_morphology", infoAttrProper.getNewGrowth_morphology());
            values.put("newGrowth_number", infoAttrProper.getNewGrowth_number());
            values.put("intrusion", infoAttrProper.getIntrusion());
            values.put("rootSys", infoAttrProper.getRootSys());
            values.put("meas_PH", infoAttrProper.getMeasure_PH());
            values.put("meas_limy_react", infoAttrProper.getMeasure_limy_reaction());
            db.update("InfoAttrProper", values, "filePath = ?", new String[]{infoAttrProper.getFilePath()});
        }
    }


    /*
     * 将InfoAttrRec实例存储到数据库
     */
    public void saveInfoAttrRec(InfoAttrRec infoAttrRec) {
        if (infoAttrRec != null) {
            ContentValues values = new ContentValues();
            values.put("filePath", infoAttrRec.getFilePath());
            values.put("NO", infoAttrRec.getNO());
            values.put("date", infoAttrRec.getDate());
            values.put("weather", infoAttrRec.getWeather());
            values.put("investigator", infoAttrRec.getInvestigator());
            values.put("position", infoAttrRec.getPosition());
            values.put("sheet_NO", infoAttrRec.getSheet_NO());
            values.put("common_name", infoAttrRec.getCommon_name());
            values.put("formal_name", infoAttrRec.getFormal_name());
            values.put("terrain", infoAttrRec.getTerrain());
            values.put("altitude", infoAttrRec.getAltitude());
            values.put("prnt_mat_type", infoAttrRec.getPrnt_mat_type());
            values.put("nat_veg", infoAttrRec.getNat_veg());
            values.put("erode_stat", infoAttrRec.getErode_sitn());
            values.put("phreatic_level", infoAttrRec.getPhreatic_level());
            values.put("water_quality", infoAttrRec.getWater_quality());
            values.put("landuse", infoAttrRec.getLanduse());
            values.put("irrig_drainage", infoAttrRec.getIrrig_drainage());
            values.put("ferti_stat", infoAttrRec.getFerti_stat());
            values.put("human_effect", infoAttrRec.getHuman_effect());
            values.put("crop_rotat_stat", infoAttrRec.getCrop_rotat_stat());
            values.put("yield", infoAttrRec.getYield());
            values.put("review", infoAttrRec.getReview());
            db.insert("InfoAttrRec", null, values);
            db.close();
        }
    }

    /*
     * 从数据库读取所有土壤剖面的记录信息
     */
    public List<InfoAttrRec> loadInfoAttrRec() {
        List<InfoAttrRec> list = new ArrayList<InfoAttrRec>();
        Cursor cursor = db.query("InfoAttrRec", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                InfoAttrRec infoAttrRec = new InfoAttrRec();
                infoAttrRec.setFilePath(cursor.getString(cursor
                        .getColumnIndex("filePath")));
                infoAttrRec.setNO(cursor.getString(cursor
                        .getColumnIndex("NO")));
                infoAttrRec.setDate(cursor.getString(cursor
                        .getColumnIndex("date")));
                infoAttrRec.setWeather(cursor.getString(cursor
                        .getColumnIndex("weather")));
                infoAttrRec.setInvestigator(cursor.getString(cursor
                        .getColumnIndex("investigator")));
                infoAttrRec.setPosition(cursor.getString(cursor
                        .getColumnIndex("position")));
                infoAttrRec.setSheet_NO(cursor.getString(cursor
                        .getColumnIndex("sheet_NO")));
                infoAttrRec.setCommon_name(cursor.getString(cursor
                        .getColumnIndex("common_name")));
                infoAttrRec.setFormal_name(cursor.getString(cursor
                        .getColumnIndex("formal_name")));
                infoAttrRec.setTerrain(cursor.getString(cursor
                        .getColumnIndex("terrain")));
                infoAttrRec.setAltitude(cursor.getString(cursor
                        .getColumnIndex("altitude")));
                infoAttrRec.setPrnt_mat_type(cursor.getString(cursor
                        .getColumnIndex("prnt_mat_type")));
                infoAttrRec.setNat_veg(cursor.getString(cursor
                        .getColumnIndex("nat_veg")));
                infoAttrRec.setErode_sitn(cursor.getString(cursor
                        .getColumnIndex("erode_stat")));
                infoAttrRec.setPhreatic_level(cursor.getString(cursor
                        .getColumnIndex("phreatic_level")));
                infoAttrRec.setWater_quality(cursor.getString(cursor
                        .getColumnIndex("water_quality")));
                infoAttrRec.setLanduse(cursor.getString(cursor
                        .getColumnIndex("landuse")));
                infoAttrRec.setIrrig_drainage(cursor.getString(cursor
                        .getColumnIndex("irrig_drainage")));
                infoAttrRec.setFerti_stat(cursor.getString(cursor
                        .getColumnIndex("ferti_stat")));
                infoAttrRec.setHuman_effect(cursor.getString(cursor
                        .getColumnIndex("human_effect")));
                infoAttrRec.setCrop_rotat_stat(cursor.getString(cursor
                        .getColumnIndex("crop_rotat_stat")));
                infoAttrRec.setYield(cursor.getString(cursor
                        .getColumnIndex("yield")));
                infoAttrRec.setReview(cursor.getString(cursor
                        .getColumnIndex("review")));
                list.add(infoAttrRec);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }
    /*
     * 从数据库中删除土壤剖面的记录信息
     */
    public void deleteInfoAttrRec(String deletedFile){
        db.delete("InfoAttrRec", "filePath = ?", new String[]{deletedFile});
    }

    /*
     * 修改数据库中土壤剖面的性状信息
     */
    public void updateInfoAttrRec(InfoAttrRec infoAttrRec){
        if (infoAttrRec != null) {
            ContentValues values = new ContentValues();
            values.put("NO", infoAttrRec.getNO());
            values.put("date", infoAttrRec.getDate());
            values.put("weather", infoAttrRec.getWeather());
            values.put("investigator", infoAttrRec.getInvestigator());
            values.put("position", infoAttrRec.getPosition());
            values.put("sheet_NO", infoAttrRec.getSheet_NO());
            values.put("common_name", infoAttrRec.getCommon_name());
            values.put("formal_name", infoAttrRec.getFormal_name());
            values.put("terrain", infoAttrRec.getTerrain());
            values.put("altitude", infoAttrRec.getAltitude());
            values.put("prnt_mat_type", infoAttrRec.getPrnt_mat_type());
            values.put("nat_veg", infoAttrRec.getNat_veg());
            values.put("erode_stat", infoAttrRec.getErode_sitn());
            values.put("phreatic_level", infoAttrRec.getPhreatic_level());
            values.put("water_quality", infoAttrRec.getWater_quality());
            values.put("landuse", infoAttrRec.getLanduse());
            values.put("irrig_drainage", infoAttrRec.getIrrig_drainage());
            values.put("ferti_stat", infoAttrRec.getFerti_stat());
            values.put("human_effect", infoAttrRec.getHuman_effect());
            values.put("crop_rotat_stat", infoAttrRec.getCrop_rotat_stat());
            values.put("yield", infoAttrRec.getYield());
            values.put("review", infoAttrRec.getReview());
            db.update("InfoAttrRec", values, "filePath = ?", new String[]{infoAttrRec.getFilePath()});
            db.close();
        }
    }

    /*
     * 保存剖面图对象
     */
    public void saveProfile(ProfilePhoto profile){
        if (profile != null) {
            for (int i = 0; i < profile.getLayerNames().length; i++) {
                ContentValues values = new ContentValues();
                values.put("filePath", profile.getFilePath());
                values.put("depth", profile.getDepths()[i]);
                values.put("layerName", profile.getLayerNames()[i]);
                values.put("proLegendCode", profile.getProLegendCodes()[i]);
                db.insert("ProfilePhoto", null, values);
            }
            db.close();
        }
    }

    /*
     *从数据库中读取所有剖面图信息
     */
    public List<ProfilePhoto> loadProfilePhotos() {
        List<ProfilePhoto> list = new ArrayList<ProfilePhoto>();
        String selectFilePath = "select distinct filePath from ProfilePhoto";
        String selectOthers = "select depth, layerName, proLegendCode from ProfilePhoto where filePath = ?";
        Cursor cursor = db.rawQuery(selectFilePath, null);
        if (cursor.moveToFirst()) {
            do{
                ProfilePhoto profilePhoto = new ProfilePhoto();
                String filepath = cursor.getString(cursor.getColumnIndex("filePath"));
                profilePhoto.setFilePath(filepath);
                Cursor cursor2 = db.rawQuery(selectOthers, new String[]{filepath});
                int num = cursor2.getCount();
                float[] depths = new float[num];
                String[] layerNames = new String[num];
                int[] proLegendCodes = new int[num];
                int i = 0;
                for (cursor2.moveToFirst(); cursor2.moveToNext(); i++) {
                    depths[i] = cursor2.getFloat(cursor2.getColumnIndex("depth"));
                    layerNames[i] = cursor2.getString(cursor2.getColumnIndex("layerName"));
                    proLegendCodes[i] = cursor2.getInt(cursor2.getColumnIndex("proLegendCode"));
                }
                profilePhoto.setDepths(depths);
                profilePhoto.setLayerNames(layerNames);
                profilePhoto.setProLegendCodes(proLegendCodes);
                list.add(profilePhoto);
            }while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }
    /*
     * 选择一个剖面图
     */
    public ProfilePhoto getProfile(String filePath) {
        ProfilePhoto profile = new ProfilePhoto();
        String selectString = "select depth, layerName, proLegendCode from ProfilePhoto where filePath = ?";
        Cursor cursor = db.rawQuery(selectString, new String[]{filePath});
        int num = cursor.getCount();
        float[] depths = new float[num];
        String[] layerNames = new String[num];
        int[] proLegendCodes = new int[num];
        int i = 0;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); i++) {
            depths[i] = cursor.getFloat(cursor.getColumnIndex("depth"));
            layerNames[i] = cursor.getString(cursor.getColumnIndex("layerName"));
            proLegendCodes[i] = cursor.getInt(cursor.getColumnIndex("proLegendCode"));
            cursor.moveToNext();
        }
        profile.setDepths(depths);
        profile.setLayerNames(layerNames);
        profile.setProLegendCodes(proLegendCodes);
        if (cursor != null) {
            cursor.close();
        }
        return profile;
    }
}

