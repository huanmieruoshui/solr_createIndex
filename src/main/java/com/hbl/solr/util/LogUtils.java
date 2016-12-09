package com.hbl.solr.util;

/**
 * 工具类
 * 2016年6月2日18:25:12
 * @author Administrator
 *
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogUtils {

	public static void write(String content,String fileName){
		
		try {
        	File file = new File(fileName);
        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        	fileName=fileName+"."+sdf.format(new Date());        	
        	if(!file.exists())
        		file.createNewFile();
            BufferedWriter writer =new BufferedWriter(new FileWriter(fileName,true));
            if(content==null)
            	content="sfsdfsda";
        	writer.write(content);
            writer.newLine();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
		
	}
	
	public static void UpdateId(int id,String fileName){
		try {
        	File file = new File(fileName);
        	if(!file.exists())
        		file.createNewFile();
            BufferedWriter writer =new BufferedWriter(new FileWriter(fileName,false));
        	writer.write(String.valueOf(id));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	public static String readFiles(String filename)  {
		
		File file = new File(filename);
		BufferedReader reader = null;
		try {
			if(!file.exists())
				file.createNewFile();
			
			reader = new BufferedReader(new FileReader(file));
			String tempString = reader.readLine();
			if(tempString==null)
				tempString="0";
			reader.close();
			
			return tempString;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		
		return "0";
	}
	
}
