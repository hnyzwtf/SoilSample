package com.soil.soilsample.support.kml;

import com.soil.soilsample.R;
import com.soil.soilsample.model.Coordinate;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class ReadKml {
	public static boolean parseKmlSuccess = false;	//判断读取KML是否成功
	private Coordinate coordinate = null; //存储从KML文件中读取出来的坐标值和name
	private static List<Coordinate> coordinateList = new ArrayList<Coordinate>();//存储每次实例化的Coordinate对象,每个Coordinate都保存着不同的x,y,name
	public void parseKml(String pathName) throws Exception
	{

		File file = new File(pathName);//pathName为KML文件的路径
		try {
			ZipFile zipFile = new ZipFile(file);
			ZipInputStream zipInputStream = null;
			InputStream inputStream = null;
			ZipEntry entry = null;
			zipInputStream = new ZipInputStream(new FileInputStream(file));
			while ((entry = zipInputStream.getNextEntry()) != null) {
				String zipEntryName = entry.getName();
				if (zipEntryName.endsWith("kml") || zipEntryName.endsWith("kmz")) {					
					inputStream = zipFile.getInputStream(entry);
					parseXmlWithDom4j(inputStream);
				}else if (zipEntryName.endsWith("png")) {
					
				}
			}			
			zipInputStream.close();
			inputStream.close();
		} catch (ZipException e) {			
			e.printStackTrace();
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}
	public Boolean parseXmlWithDom4j(InputStream input) throws Exception
	{
		SAXReader reader = new SAXReader();
		Document document = null;
		try {
			document = reader.read(input);
			Element root = document.getRootElement();//获取doc.kml文件的根结点
			listNodes(root);			
			parseKmlSuccess = true;
		} catch (DocumentException e) {
			// TODO: handle exception
			e.printStackTrace();
		}	
		return parseKmlSuccess;
	}
	//遍历当前节点下的所有节点  
    public void listNodes(Element node){  
    	String htmlContent = "";
    	String name = "";//Placemark节点中的name属性
    	String x = "";//坐标x
    	String y = "";//坐标y
    	double d_x = 0.0;//对x作string to double
    	double d_y = 0.0;
    	try {
    		if ("Placemark".equals(node.getName())) {//如果当前节点是Placemark就解析其子节点
        		List<Element> placemarkSons = node.elements();//得到Placemark节点所有的子节点
              	for (Element element : placemarkSons) { //遍历所有的子节点     		
        	  		if ("name".equals(element.getName())) {
        				name = element.getText();					
        			}
        	  		if ("description".equals(element.getName())) {
						htmlContent = element.getText();//获取KML文件中的HTML内容
					}
        		}
              	Element pointSon;//Point节点的子节点
        		Iterator i = node.elementIterator("Point");//遍历Point节点的所有子节点
        		while (i.hasNext()) {
        			pointSon = (Element)i.next();
        			String nodeContent = "";
        			nodeContent = pointSon.elementText("coordinates");//得到coordinates节点的节点内容
        			String nodeContentSplit[] = null;
        			nodeContentSplit = nodeContent.split(",");
                    x = nodeContentSplit[1];//纬度
                    y = nodeContentSplit[0];
                    d_x = Double.valueOf(x.trim());
                    d_y = Double.valueOf(y.trim());
        		}				
        		coordinate = new Coordinate();
				coordinate.setX(d_x);
				coordinate.setY(d_y);
				coordinate.setName(name);
				coordinate.setIconId(R.drawable.default_marker);
				coordinate.setHtmlContent(htmlContent);
            	coordinateList.add(coordinate);//将每一个实例化的对象存储在list中
    		}
    		   		

		} catch (Exception e) {
			e.printStackTrace();
		}
        //同时迭代当前节点下面的所有子节点  
        //使用递归  
        Iterator<Element> iterator = node.elementIterator();  
        while(iterator.hasNext()){  
            Element e = iterator.next();  
            listNodes(e);  
        }     
    }  

    public List<Coordinate> getCoordinateList()
    {       	
    	return this.coordinateList;
    }
}
