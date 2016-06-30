package com.soil.profile.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class SoilNoteOpenHelper extends SQLiteOpenHelper {

    //回头把字段名称改一下
	/*
	 * GeoInfo表建表语句
	 */
    public static final String CREATE_GEO_INFO = "create table InfoGeo ("
//			+ "id integer primary key autoincrement, "
            + "imagePath text, "
            + "latitude real, "
            + "longtitude real, " + "position text)";
    /*
     * InfoAttrProper表建表语句
     */
    public static final String 	CREATE_FEAT_ATTR_INFO = "create table InfoAttrProper ("
//			+"id integer primary key autoincrement, "
            + "filePath text, "+ "layer text, "
            +"depth real, " + "color_dry text, " + "color_wet text, "
            +"humidity text, " + "texture text, " + "structure text, "
            +"compactness text, " + "porosity text, " + "newGrowth_class text, "
            +"newGrowth_morphology text, " + "newGrowth_number text, " + "intrusion text, "
            +"rootSys text, " + "meas_PH text, " + "meas_limy_react text)";
    /*
     * InfoAttrRec表建表语句,千万千万要注意语句中的逗号不要丢了！！！哭死了
     */
    public static final String 	CREATE_REC_ATTR_INFO = "create table InfoAttrRec ("
//			+ "id integer primary key autoincrement, "
            + "filePath text, "+"NO text, " + "date text, "
            + "weather text, " +"investigator text, " + "position text, " + "sheet_NO text, "
            +"common_name text, " + "formal_name text, " + "terrain text, "
            +"altitude text, " + "prnt_mat_type text, " + "nat_veg text, "
            +"erode_stat text, " + "phreatic_level text, " + "water_quality text, "
            +"landuse text, " + "irrig_drainage text, " + "ferti_stat text, "
            +"human_effect text, " + "crop_rotat_stat text, " + "yield text, " + "review text)";

    /*
     * ProfilePhoto表建表语句
     */
    public static final String CREATE_PROFILE_PHOTO = "create table ProfilePhoto ("
//			+"id integer primary key autoincrement, "
            +"filePath text,"+"depth real,"
            +"layerName text, "+"proLegendCode integer)";

    public SoilNoteOpenHelper(Context context, String name,
                              CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_GEO_INFO);// 创建GeoInfo表
        db.execSQL(CREATE_FEAT_ATTR_INFO);//创建InfoAttrFeat表
        db.execSQL(CREATE_REC_ATTR_INFO);//创建InfoAttrRec表
        db.execSQL(CREATE_PROFILE_PHOTO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO 自动生成的方法存根

    }

}

