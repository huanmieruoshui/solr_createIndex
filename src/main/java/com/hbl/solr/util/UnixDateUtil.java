package com.hbl.solr.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class UnixDateUtil {
	
	public final static String NEWFORMATSTRING = "yyyyMMdd";
	public final static String INFOUPDATE_FORMATSTRING = "yyyy年MM月dd日 HH:mm";
	public final static String UPDATE_FORMATSTRING = "yyyyMMddHH";
	public final static String NORMAL_FORMATSTRING = "yyyy-MM-dd HH:mm:ss";
	  /** 
	    * 时间unix转换 
	    * @param timestampString 
	    * 		long格式的时间，String类型
	    * @param format
	    * 		时间格式
	    * @return 
	    */ 
	   public static String TimeStampDate(String timestampString, String format) {  
	       Long timestamp = Long.parseLong(timestampString) * 1000;  
	       String date = new SimpleDateFormat(format).format(new Date(timestamp));  
	       return date;  
	   }  
	  
	   /** 
	    * 将时间unix转换为int类型 
	    * @param timeString
	    * 		有格式的String类型的时间，例如2016-01-01，该格式与format定义的格式要相同
	    * @param format 
	    * 		时间格式
	    * @return 
	    */  
	   public static int DateToInt(String timeString, String format) {  
	       int time = 0;  
	       try {  
	           SimpleDateFormat sdf = new SimpleDateFormat(format);  
	           Date date = sdf.parse(timeString);  
	           String strTime = date.getTime() + "";  
	           strTime = strTime.substring(0, 10);  
	           time = Integer.parseInt(strTime);  
	       }  
	       catch (ParseException e) {  
	           e.printStackTrace();  
	       }  
	       return time;  
	   }
	   
	   /**
	    * 获取int类型的时间，例如20160617
	    * @param longTime
	    * 		long类型的time
	    * @return
	    */
	   public static int getIntTime(long longTime) {
		   SimpleDateFormat sdfly = new SimpleDateFormat(NEWFORMATSTRING);
		   int intTime = Integer.parseInt(sdfly.format(longTime));
		   //System.out.println(intTime);
		   return intTime;
	   }
	   
	   /**
	    * 获取int类型的时间，例如2016061709，主要在信息推送中使用
	    * @param unixTime
	    * 		int类型的time，Unix时间
	    * @return
	    */
	   public static int getIntTime(int unixTime) {
		   Long timestamp = (long)unixTime * 1000;  
		   SimpleDateFormat sdfly = new SimpleDateFormat(UPDATE_FORMATSTRING);
		   int intTime = Integer.parseInt(sdfly.format(timestamp));
		   return intTime;
	   }
	   
	   /**
	    * long转int时间，即转成Unix时间
	    * @param time
	    * @return
	    */
	   public static int LongDateToInt(long time) {
		   String timeStr = time + "";
		   //System.out.println(timeStr);
		   timeStr = timeStr.substring(0, 10);  
		   int timeInt = Integer.parseInt(timeStr); 
		   //System.out.println(timeInt);
		   return timeInt;
	   }
	   
	   /**
	    * 获取今天
	    * @return yyyy-MM-dd
	    */
	   public static String getToday() {
		   String today = new SimpleDateFormat(NORMAL_FORMATSTRING).format(new Date());
		   return today;
	   }
	   
	   /**
	    * 获取昨天
	    * @return yyyy-MM-dd
	    */
	   public static String getYesterday() {
		   Calendar cal = Calendar.getInstance(); 
		   cal.add(Calendar.DATE,-1); 
		   String yesterday = new SimpleDateFormat(NORMAL_FORMATSTRING).format(cal.getTime());
		   return yesterday;
	   }
}
