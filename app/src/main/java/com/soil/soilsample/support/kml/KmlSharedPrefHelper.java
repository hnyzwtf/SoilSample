package com.soil.soilsample.support.kml;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.soil.soilsample.model.Coordinate;
import com.soil.soilsample.model.CoordinateAlterSample;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by GIS on 2016/7/3 0003.
 * 辅助类，主要是对添加的kml文件进行相关的sharedpreference操作
 */
public class KmlSharedPrefHelper {

    private Set<String> kmlNamesSets = new HashSet<String>();
    private static KmlSharedPrefHelper kmlSharedPrefHelper;
    private Context mContext;
    private KmlSharedPrefHelper(Context context)
    {
        this.mContext = context;
    }
    public static KmlSharedPrefHelper getInstance(Context context)
    {
        if (kmlSharedPrefHelper == null)
        {
            synchronized (KmlSharedPrefHelper.class)
            {
                if (kmlSharedPrefHelper == null)
                {
                    kmlSharedPrefHelper = new KmlSharedPrefHelper(context);
                }
            }
        }
        return kmlSharedPrefHelper;
    }

    /**
     * @param kmlNames
     * @param coorList
     * 解析kml成功后，将所有的坐标保存下来，在MainActivity的parseKml中，只要解析成功，就调用此方法立即保存到本地
     */
    public void saveAddedKmlToShared(String kmlNames, List<Coordinate> coorList)
    {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(kmlNames, mContext.MODE_PRIVATE).edit();
        Gson gson = new Gson();
        String json = gson.toJson(coorList);
        editor.putString(kmlNames, json);
        editor.commit();
    }

    /**
     * @param kmlName
     * @return
     * 将saveAddedKmlToShared方法中保存到本地的Kml坐标取出来，存放到list中
     */
    public List<Coordinate> getAddedKmlFromShared(String kmlName)
    {
        List<Coordinate> coorList = null;
        SharedPreferences preferences = mContext.getSharedPreferences(kmlName, mContext.MODE_PRIVATE);
        String json = preferences.getString(kmlName, null);
        if (json != null)
        {
            Gson gson = new Gson();
            Type type = new TypeToken<List<Coordinate>>(){}.getType();
            coorList = new ArrayList<Coordinate>();
            coorList = gson.fromJson(json, type);

        }

        return coorList;
    }

    /**
     * @param kmlName
     * 清空指定的shared文件
     */
    public void clearAddedKmlFromShared(String kmlName)
    {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(kmlName, mContext.MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();
    }

    /**
     * @param kmlName
     * 删除指定的shared文件
     */
    public void deleteCertainKmlShared(String kmlName)
    {
        String path = "/data/data/" + mContext.getPackageName().toString() + "/shared_prefs/";
        String fileName = kmlName + ".xml";
        try {
            File file = new File(path, fileName);
            if (file.exists())
            {
                file.delete();
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    /**
     * @param kmlName
     * 将每次添加的kml文件名保存下来,在MainActivity的parseKml中调用
     */
    public void saveAddedKmlNames(String kmlName)
    {
        try {

            kmlNamesSets.add(kmlName);
            SharedPreferences.Editor editor = mContext.getSharedPreferences("AddedKmlNames", mContext.MODE_PRIVATE).edit();
            editor.putStringSet("KmlNamesSet", kmlNamesSets);
            editor.commit();
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    /**
     * @param kmlName
     * 删除指定的kmlName
     */
    public void updateAddedKmlNames(String kmlName)
    {
        try {
            //先读取set集合中的数值
            Set<String> kmlNamesSet = new HashSet<String>();
            SharedPreferences preferences = mContext.getSharedPreferences("AddedKmlNames", mContext.MODE_PRIVATE);
            kmlNamesSet = preferences.getStringSet("KmlNamesSet", null);
            //删除相应的元素
            kmlNamesSet.remove(kmlName);
            //再将更新后的set集合保存到本地
            SharedPreferences.Editor editor = mContext.getSharedPreferences("AddedKmlNames", mContext.MODE_PRIVATE).edit();
            editor.clear();
            editor.apply();
            editor.putStringSet("KmlNamesSet", kmlNamesSet);
            editor.commit();
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    /**
     * @return
     * 取出保存的文件名
     */
    public Set<String> getAddedKmlNames()
    {
        Set<String> kmlNamesSet = new HashSet<String>();
        SharedPreferences preferences = mContext.getSharedPreferences("AddedKmlNames", mContext.MODE_PRIVATE);
        kmlNamesSet = preferences.getStringSet("KmlNamesSet", null);
        return kmlNamesSet;
    }

    /**
     * @return
     * 对应于AlternativeParamsActivity和AlterParamsFCMActivity中的saveAlterSamplesToSharedPrefer方法，
     * 取出某个不可采点的名称
     * 服务器生成的某个不可采点的10个可替代样点marker，就以此不可采点的名称命名
     */
    public String getAlterMarkerNameFromPref()
    {
        SharedPreferences preferences = mContext.getSharedPreferences("AlterSamplesList", mContext.MODE_PRIVATE);
        String unaccessName = preferences.getString("alterMarkerName", null);
        return unaccessName;
    }

    /**
     * @param markerName
     * @return alterSamples
     * 我们在AlternativeParamsActivity的saveAlterSamplesToSharedPrefer方法以及
     * AlterParamsFCMActivity中的saveAlterSamplesToSharedPrefer方法
     * 中将服务器返回的可替代样点的坐标json化，并保存到了本地
     */
    public ArrayList<CoordinateAlterSample> getAlterCoorsFromPref(String markerName)
    {
        SharedPreferences preferences = mContext.getSharedPreferences("AlterSamplesList", mContext.MODE_PRIVATE);
        String json = preferences.getString(markerName, null);
        ArrayList<CoordinateAlterSample> alterSamples = null;
        if (json != null)
        {
            Gson gson = new Gson();
            Type type = new TypeToken<List<CoordinateAlterSample>>(){}.getType();
            //先从本地取出可替代样点
            alterSamples = new ArrayList<CoordinateAlterSample>();
            alterSamples = gson.fromJson(json, type);
        }
        return alterSamples;
    }

    /**
     * remove the alterMarkerName field in AlterSamplesList pref so that it will not show up in FavoriteActivity
     */
    public void removeMarkerNameFromPref()
    {
        SharedPreferences.Editor editor = mContext.getSharedPreferences("AlterSamplesList", mContext.MODE_PRIVATE).edit();
        editor.remove("alterMarkerName");
        editor.commit();
    }
    /**
     * 清空我们在AlternativeParamsActivity的saveAlterSamplesToSharedPrefer方法以及
     * AlterParamsFCMActivity中的saveAlterSamplesToSharedPrefer方法
     * 中保存的shared数据
     */
    public void clearAlterCoorsFromPref()
    {
        SharedPreferences.Editor editor = mContext.getSharedPreferences("AlterSamplesList", mContext.MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();
    }

    /**
     * @param kmlName
     * @param flag
     * 将传入的kmlName的marker显示状态存入本地，例如，我们添加了一个kml，在MainActivity中的addMarkers调用此方法，那么传入的就是：example.kml,true
     */
    public void saveKmlShownState(String kmlName, boolean flag)
    {
        SharedPreferences.Editor editor = mContext.getSharedPreferences("kmlShownState_pref", mContext.MODE_PRIVATE).edit();
        editor.putBoolean(kmlName, flag);
        editor.commit();
    }
    public boolean getKmlShownState(String kmlName)
    {
        SharedPreferences preferences = mContext.getSharedPreferences("kmlShownState_pref", mContext.MODE_PRIVATE);
        boolean flag = preferences.getBoolean(kmlName, false);
        return flag;
    }
}
