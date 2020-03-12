package io.keikai.devref.jsf;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

	public static Calendar today(){
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal;
	}
	
	public static String today(String pattern){
		Calendar cal = today();
		return new SimpleDateFormat(pattern).format(cal.getTime());
	}
	
	public static String tomorrow(int dayafter,String pattern){
		Calendar cal = tomorrow(dayafter);
		return new SimpleDateFormat(pattern).format(cal.getTime());
	}
	
	public static Calendar tomorrow(int dayafter){
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, dayafter+1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal;
	}

	public static String yesterday(int daybefore,String pattern){
		Calendar cal = yesterday(daybefore);
		return new SimpleDateFormat(pattern).format(cal.getTime());
	}
	public static Calendar yesterday(int daybefore){
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -(daybefore+1));
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal;
	}
	
	public static Date todayDate(){
		return today().getTime();
	}
	public static Date tomorrowDate(int dayafter){
		return tomorrow(dayafter).getTime();
	}
	public static Date yesterdayDate(int dayafter){
		return yesterday(dayafter).getTime();
	}
}
