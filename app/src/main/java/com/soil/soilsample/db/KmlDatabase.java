package com.soil.soilsample.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by GIS on 2016/7/3 0003.
 */
public class KmlDatabase {
    //public static final String ADDED_KML_DB_NAME = "added_kmlDb";
    //public static final String ALTER_KML_DB_NAME = "alter_kmlDb";
    public static final int VERSION = 1;
    private SQLiteDatabase db;
    private static KmlDatabase kmlDatabase;
    private KmlDatabase(Context context, String dbName, String kmlName)
    {
        KmlDbHelper dbHelper = new KmlDbHelper(context, dbName, kmlName, null, VERSION);
        db = dbHelper.getWritableDatabase();
    }
    public static KmlDatabase getInstance(Context context, String dbName, String kmlName)
    {
        if (kmlDatabase == null)
        {
            synchronized (KmlDatabase.class)
            {
                if (kmlDatabase == null)
                {
                    kmlDatabase = new KmlDatabase(context, dbName, kmlName);
                }
            }
        }
        return kmlDatabase;
    }
}
