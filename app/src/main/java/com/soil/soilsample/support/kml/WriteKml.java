package com.soil.soilsample.support.kml;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.soil.soilsample.R;
import com.soil.soilsample.base.BaseApplication;
import com.soil.soilsample.model.CoordinateAlterSample;
import com.soil.soilsample.support.util.ToastUtil;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by GIS on 2016/5/19 0019.
 * 服务器返回的坐标是x为经度，y为纬度
 */
public class WriteKml {
    private String TAG = "WriteKml";

    /*
    * 传入两个参数，一是kml的名称，第二个是坐标点的list
    * */
    public void createKml(String kmlName, List<CoordinateAlterSample> alterSamples) throws Exception
    {
        Element root = DocumentHelper.createElement("kml");  //根节点是kml
        Document document = DocumentHelper.createDocument(root);
        //给根节点kml添加属性
        root.addAttribute("xmlns", "http://www.opengis.net/kml/2.2")
                .addAttribute("xmlns:gx", "http://www.google.com/kml/ext/2.2")
                .addAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance")
                .addAttribute("xsi:schemaLocation",
                        "http://www.opengis.net/kml/2.2 http://schemas.opengis.net/kml/2.2.0/ogckml22.xsd http://www.google.com/kml/ext/2.2 http://code.google.com/apis/kml/schema/kml22gx.xsd");

        //给根节点kml添加子节点  Document
        Element documentElement = root.addElement("Document");

        documentElement.addElement("name").addText(kmlName); //添加name节点
        documentElement.addElement("Snippet").addText(""); //Snippet节点
        Element folderElement = documentElement.addElement("Folder");//Folder节点
        folderElement.addAttribute("id", "FeatureLayer0");
        //给Folder节点添加子节点
        folderElement.addElement("name").addText(kmlName);
        folderElement.addElement("Snippet").addText("");
        //循环添加每一个Placemark节点，有几个坐标点就有几个Placemark节点
        for (int i = 0; i < alterSamples.size(); i++) {
            Element placeMarkElement = folderElement.addElement("Placemark");
            placeMarkElement.addAttribute("id", alterSamples.get(i).getName());
            placeMarkElement.addElement("name").addText(alterSamples.get(i).getName());
            placeMarkElement.addElement("Snippet").addText("");
            placeMarkElement.addElement("description").addCDATA(getCdataContent(alterSamples.get(i).getName(),
                    alterSamples.get(i).getName(), String.valueOf(alterSamples.get(i).getX()), String.valueOf(alterSamples.get(i).getY()),
                    alterSamples.get(i).getCostValue()));
            placeMarkElement.addElement("styleUrl").addText("#IconStyle00");
            Element pointElement = placeMarkElement.addElement("Point");
            pointElement.addElement("altitudeMode").addText("clampToGround");
            //添加每一个坐标点的经纬度坐标
            //pointElement.addElement("coordinates").addText("119.39986000,31.13396700000143,0");
            pointElement.addElement("coordinates").addText(String.valueOf(alterSamples.get(i).getX()) + "," +
                    String.valueOf(alterSamples.get(i).getY()) + "," + "0");
        }
        Element styleElement = documentElement.addElement("Style");//Style节点
        styleElement.addAttribute("id", "IconStyle00");
        // IconStyle
        Element iconStyleElement = styleElement.addElement("IconStyle");
        Element iconElement = iconStyleElement.addElement("Icon");
        iconElement.addElement("href").addText("layer0_symbol.png");
        iconStyleElement.addElement("scale").addText("0.250000");
        // LabelStyle
        Element labelStyleElement = styleElement.addElement("LabelStyle");
        labelStyleElement.addElement("color").addText("00000000");
        labelStyleElement.addElement("scale").addText("0.000000");
        // PolyStyle
        Element polyStyleElement = styleElement.addElement("PolyStyle");
        polyStyleElement.addElement("color").addText("ff000000");
        polyStyleElement.addElement("outline").addText("0");

        //将生成的kml写出本地
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("utf-8");//设置编码格式
        //将doc.kml写入到/data/data/<package name>/files目录
        FileOutputStream outputStream = BaseApplication.getContext().openFileOutput("doc.kml", Context.MODE_PRIVATE);
        XMLWriter xmlWriter = new XMLWriter(outputStream,format);

        xmlWriter.write(document);

        xmlWriter.close();
        //开始对文件进行压缩，一个kml文件其实是一个压缩文件，里面包含一个kml文件和一个png图标

        String docKmlPath = BaseApplication.getContext().getFilesDir().getAbsolutePath() + "//doc.kml";

        zipWriteKml(docKmlPath, kmlName);
        ToastUtil.show(BaseApplication.getContext(), "导出kml成功，保存在sdcard0/SoilSample/kml目录下");

    }
    /*
    * 将生成的kml文件和drawable下的某个png图标进行压缩，生成最终的kml文件，并保存在sd/SoilSample目录
    * */
    public void zipWriteKml(String docKmlPath, String kmlName) throws IOException
    {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            // 最终生成的kml文件
            FileOutputStream fileOutput = new FileOutputStream(Environment.getExternalStorageDirectory().getCanonicalPath()
                    + "/" + "SoilSample" + "/" + "kml" + "/" + kmlName + ".kmz");
            OutputStream os = new BufferedOutputStream( fileOutput);
            ZipOutputStream zos = new ZipOutputStream(os);
            byte[] buf = new byte[8192];
            int len;

            //压缩data/data/package name/files目录下的doc.kml
            File file = new File(docKmlPath);
            if ( !file.isFile() )
                Log.d(TAG, "doc.kml is nonexist");
            ZipEntry ze = new ZipEntry( file.getName() );
            zos.putNextEntry( ze );
            BufferedInputStream bis = new BufferedInputStream( new FileInputStream( file ) );
            while ( ( len = bis.read( buf ) ) > 0 ) {
                zos.write( buf, 0, len );

            }
            zos.closeEntry();
            // 压缩drawable目录下的图片
            Resources r = BaseApplication.getContext().getResources();
            Bitmap bitmap = BitmapFactory.decodeResource(r, R.drawable.layer0_symbol);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            InputStream input = new ByteArrayInputStream(baos.toByteArray());
            int temp = 0;
            ZipEntry entry2 = new ZipEntry("layer0_symbol.png");
            zos.putNextEntry(entry2);
            while ((temp = input.read()) != -1)
            {
                zos.write(temp);
            }
            input.close();
            zos.closeEntry();

            // zos.closeEntry();
            zos.close();
        }
        else {
            ToastUtil.show(BaseApplication.getContext(), "错误：找不到SD卡");
        }
        //FileOutputStream fileOutput = BaseApplication.getContext().openFileOutput(kmlName + ".kmz", Context.MODE_PRIVATE);

       /* for (int i=0;i < files.length;i++) {
            File file = new File(files[i]);
            if ( !file.isFile() )
                continue;
            ZipEntry ze = new ZipEntry( file.getName() );
            zos.putNextEntry( ze );
            BufferedInputStream bis = new BufferedInputStream( new FileInputStream( file ) );
            while ( ( len = bis.read( buf ) ) > 0 ) {
                zos.write( buf, 0, len );
                Log.d(TAG, "we are zipping "+ file.getName());
            }
            zos.closeEntry();
        }*/

    }
    /*
    * 生成kml的html备注,在description节点下
    * */
    public String getCdataContent(String id, String placeMarkName, String x, String y, String costValue)
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<html xmlns:fo=\"http://www.w3.org/1999/XSL/Format\" xmlns:msxsl=\"urn:schemas-microsoft-com:xslt\">");
        buffer.append("<head>");
        buffer.append("<META http-equiv=\"Content-Type\" content=\"text/html\">");
        buffer.append("<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\">");
        buffer.append("</head>");
        buffer.append("<body style=\"margin:0px 0px 0px 0px;overflow:auto;background:#FFFFFF;\">");
        buffer.append("<table style=\"font-family:Arial,Verdana,Times;font-size:12px;text-align:left;width:100%;border-collapse:collapse;padding:3px 3px 3px 3px\">");
        buffer.append("<tr style=\"text-align:center;font-weight:bold;background:#9CBCE2\">");
        buffer.append("<td>").append(placeMarkName).append("</td>");
        buffer.append("</tr>");
        buffer.append("<tr>");
        buffer.append("<td>");
        buffer.append("<table style=\"font-family:Arial,Verdana,Times;font-size:12px;text-align:left;width:100%;border-spacing:0px; padding:3px 3px 3px 3px\">");
        buffer.append("<tr bgcolor=\"#D4E4F3\">");
        buffer.append("<td>ID</td>");
        buffer.append("<td>").append(id).append("</td>");
        buffer.append("</tr>");
        buffer.append("<tr>");
        buffer.append("<td>name</td>");
        buffer.append("<td>").append(placeMarkName).append("</td>");
        buffer.append("</tr>");
        buffer.append("<tr bgcolor=\"#D4E4F3\">");
        buffer.append("<td>X</td>");
        buffer.append("<td>").append(x).append("</td>");
        buffer.append("</tr>");
        buffer.append("<tr>");
        buffer.append("<td>Y</td>");
        buffer.append("<td>").append(y).append("</td>");
        buffer.append("</tr>");
        buffer.append("<tr bgcolor=\"#D4E4F3\">");
        buffer.append("<td>CostValue</td>");
        buffer.append("<td>").append(costValue).append("</td>");
        buffer.append("</tr>");
        buffer.append("</table>");
        buffer.append("</td>");
        buffer.append("</tr>");
        buffer.append("</table>");
        buffer.append("</body>");
        buffer.append("</html>");

        String cDataContent = buffer.toString();
        return cDataContent;
    }

}
