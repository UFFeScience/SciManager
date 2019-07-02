package com.uff.scimanager.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Throwables;

public class CalendarDateUtils {
	
	private static final Logger log = LoggerFactory.getLogger(CalendarDateUtils.class);
	
	public static final String DATE_LOCALE = "America/Sao_Paulo";
	private static final int DAY_IN_MILLISECONDS = 86400000;
	
	public static String formatDate(Calendar calendarDate) {
		if (calendarDate == null) {
			log.info("Data informada está nula");
			return null;
		}
		
		Date date = calendarDate.getTime();
		SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/YYYY - HH:mm:ss");
		return dateFormatter.format(date);
	}
	
	public static String formatDate(Date date) {
		if (date == null) {
			log.info("Data informada está nula");
			return null;
		}
		
		SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/YYYY - HH:mm:ss");
		return dateFormatter.format(date);
	}
	
	public static String formatDateWithoutHours(Calendar calendarDate) {
		if (calendarDate == null) {
			log.info("Data informada está nula");
			return null;
		}
		
		Date date = calendarDate.getTime();
		SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/YYYY");
		return dateFormatter.format(date);
	}
	
	public static String formatDateString(String dateText) {
		if (dateText == null || "".equals(dateText)) {
			log.info("String de data informada está nula");
			return null;
		}
		
		return dateText.replace('-', '/');
	}
	
	public static String parseStringDateCorrectFormat(String dateText) {
		if (dateText == null || "".equals(dateText)) {
			log.info("Data informada está nula");
			return null;
		}
		
		String[] dateTextDates = dateText.split("-");
		
		if (dateTextDates.length < 3) {
			return null;
		}
		
		return dateTextDates[2] + "-" + dateTextDates[1] + "-" + dateTextDates[0] + " 00:00:00.000";
	}
	
	public static Calendar createCalendarFromString(String dateText) {
		if (dateText == null || "".equals(dateText)) {
			log.info("String de data informada está nula");
			return null;
		}
		
		dateText = dateText.replace('-', '/'); 
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(DATE_LOCALE));
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		try {
			cal.setTime(sdf.parse(dateText));
		} 
		catch (ParseException e) {
			log.error("Erro ao fazer o parse da data {} para calendar\n{}", dateText, Throwables.getStackTraceAsString(e));
			return null;
		}
		
		return cal;
	}
	
	public static Calendar getCalendarFromDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
	}
	
	public static Date getDateWithoutTime(String dateText) {
		try {
			if (dateText == null || "".equals(dateText)) {
				log.info("String de data informada está nula");
				return null;
			}
			
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date date = format.parse(dateText);
			
		    Calendar cal = Calendar.getInstance();
		    
		    cal.setTime(date);
		    cal.set(Calendar.HOUR_OF_DAY, 0);
		    cal.set(Calendar.MINUTE, 0);
		    cal.set(Calendar.SECOND, 0);
		    cal.set(Calendar.MILLISECOND, 0);
		    
		    return cal.getTime();
		    
		} 
		catch (ParseException e) {
			log.error("Erro ao fazer o parse da data {} para calendar\n{}", dateText, Throwables.getStackTraceAsString(e));
			return null;
		}
	}
	
	public static Boolean isSameDay(Calendar initialDate, Calendar finalDate) {
        if (initialDate == null || finalDate == null) {
        	log.error("Erro ao fazer o parse das datas, nao podem ser nulas");
			return false;
        }
        
        return (initialDate.get(Calendar.ERA) == finalDate.get(Calendar.ERA) &&
                initialDate.get(Calendar.YEAR) == finalDate.get(Calendar.YEAR) &&
                initialDate.get(Calendar.DAY_OF_YEAR) == finalDate.get(Calendar.DAY_OF_YEAR));
    }
	
	public static Date getDayAfter(Date date) {
		Long dayAfter = date.getTime() + DAY_IN_MILLISECONDS;
		return new Date(dayAfter);
	}
	
	public static Date getDayBefore(Date date) {
		Long dayBefore = date.getTime() - DAY_IN_MILLISECONDS;
		return new Date(dayBefore);
	}

	public static String formatExecutionDate(Date date) {
		if (date == null) {
			return "--";
		}
		
		return formatDate(getCalendarFromDate(date));
	}
	
}