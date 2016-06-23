package com.soil.soilsample.support.util;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by GIS on 2016/6/22 0022.
 */
public class SDFileHelper {
    private Context mContext;
    private String mSDCardPath = null;
    public static final String APP_FOLDER_NAME = "SoilSample";
    public SDFileHelper(Context context)
    {
        super();
        this.mContext = context;
    }
    public void saveFileToSD(String fileName, String content) throws Exception
    {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            // Environment.getExternalStorageDirectory()函数获得Sdcard的根目录
            fileName = Environment.getExternalStorageDirectory().getCanonicalPath() + "/" + "SoilSample" + "/" + fileName;
            FileOutputStream outputStream = new FileOutputStream(fileName);
            outputStream.write(content.getBytes());
            outputStream.close();
        }
        else
        {
            Toast.makeText(mContext, "you have no sdcard ", Toast.LENGTH_SHORT).show();
        }
    }
    /*
   * 从SD卡读取文件，目前只能读取文本文件中的字符串
   * */
    public String readFileContentFromSD(String fileName) throws IOException
    {
        StringBuilder sb = new StringBuilder("");
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            fileName = Environment.getExternalStorageDirectory().getCanonicalPath() + "/" + fileName;
            FileInputStream inputStream = new FileInputStream(fileName);
            byte[] temp = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(temp)) > 0)
            {
                sb.append(new String(temp, 0, len));
            }
            inputStream.close();
        }
        return sb.toString();
    }
    /*
   * 在SD卡中创建SoilSample目录
   * */
    public boolean createDirOnSD()
    {
        mSDCardPath = getSdcardDir();
        if (mSDCardPath == null) {
            return false;
        }
        File f = new File(mSDCardPath, APP_FOLDER_NAME);
        if (!f.exists()) {
            try {
                f.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;

    }

    private String getSdcardDir() {
        if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().toString();
        }
        return null;
    }
    public void createDirOnSDcard()
    {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            try {
                String dirPath = Environment.getExternalStorageDirectory().getCanonicalPath() + "/SoilSample";
                File file = new File(dirPath);
                if (!file.exists())
                {
                    file.mkdir();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
