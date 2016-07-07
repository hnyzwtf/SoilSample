package com.soil.soilsample.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by GIS on 2016/7/3 0003.
 */
public class KmlDbHelper extends SQLiteOpenHelper {
    private Context mContext;
    private String tableName;
    public KmlDbHelper(Context context, String name, String kmlName, SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, name, factory, version);
        mContext = context;
        tableName = kmlName;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        createTable(db, tableName);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public void createTable(SQLiteDatabase db, String tableName)
    {
        String create_table = "create table" + tableName + "(name text, x real, y real, htmlContent text)";
        db.execSQL(create_table);
    }
}
