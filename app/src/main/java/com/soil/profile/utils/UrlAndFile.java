package com.soil.profile.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.soil.soilsample.base.BaseApplication;

import java.io.File;

public class UrlAndFile {
    // 根据uri获得文件路径
    public static String getRealPathFromURI(Uri contentUri, Context context) {
        // TODO 自动生成的方法存根
        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null,
                null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    //判断文件是否存在
    public static boolean fileIsExists(String strFile){
        try{
            File f=new File(strFile);
            if(!f.exists()){
                return false;
            }else {
                return true;
            }
        }catch (Exception e){
            return false;
        }
    }

    //获得保存的路径
    public static String getSaveFilePath() {
        String mFilePath = "";
        // 获得最终图片保存的路径
        String pathStorage = BaseApplication.getAppPath() + "/SoilNoteProfile/";
        // 创建文件夹
        File file = new File(pathStorage);
        if (!file.exists()) {
            file.mkdirs();
        }
        String name = System.currentTimeMillis()+ ".jpg";
        // 返回文件路径
        mFilePath = pathStorage + "/" + name;
        return mFilePath;
    }


}
