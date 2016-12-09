package com.hbl.solr.config;


import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

public class Config{  
	
	private static Logger log = Logger.getLogger(Config.class);
    
    /** 国际化资源 */
	public static ResourceBundle resourceBundle;
	private static BufferedInputStream inputStream;  

	static {
		//resourceBundle = ResourceBundle.getBundle("config/app");
        String proFilePath = System.getProperty("user.dir") + "/config/app.properties";  
        try {  
            inputStream = new BufferedInputStream(new FileInputStream(proFilePath));  
            resourceBundle = new PropertyResourceBundle(inputStream);  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
	}
	  
	  
  /**
     * 从资源文件中返回字符串 我们不希望程序崩溃，所以如果没有找到Key，就直接返回Key
     */
    public static String getString(String key, Object[] args) {
        try {
            return MessageFormat.format(getString(key), args);
        } catch (MissingResourceException e) {
        	log.error(e);
        	e.printStackTrace();
            return "";
        } catch (NullPointerException e) {
        	log.error(e);
            return "";
        }
    }
	   
    /**
     * 从资源文件中返回字符串 我们不希望程序崩溃，所以如果没有找到Key，就直接返回Key
     */
    public static String getString(String key) {
        try {
            if(!resourceBundle.containsKey(key)) {
                return "";
            }
            return resourceBundle.getString(key);
        } catch (MissingResourceException e) {
        	log.error(e);
        	e.printStackTrace();
            return "";
        } catch (NullPointerException e) {
        	log.error(e);
            return "";
        }
    }
    
    /**
     * 从资源文件中返回字符串 我们不希望程序崩溃，所以如果没有找到Key，就直接返回Key
     */
    public static int getInt(String key) {
        try {
            if(!resourceBundle.containsKey(key)) {
                return 0;
            }
            return Integer.parseInt(resourceBundle.getString(key));
        } catch (MissingResourceException e) {
        	log.error(e);
        	e.printStackTrace();
            return 0;
        } catch (NullPointerException e) {
        	log.error(e);
            return 0;
        }
    }
    
    /**
     * 从资源文件中返回字符串 我们不希望程序崩溃，所以如果没有找到Key，就直接返回defaultValue
     */
    public static int getInt(String key, int defaultValue) {
        try {
            if(!resourceBundle.containsKey(key)) {
                return defaultValue;
            }
            return Integer.parseInt(resourceBundle.getString(key));
        } catch (MissingResourceException e) {
        	log.error(e);
        	e.printStackTrace();
            return defaultValue;
        } catch (NullPointerException e) {
        	log.error(e);
            return defaultValue;
        }
    }
   
}  