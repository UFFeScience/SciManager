package com.uff.workflow.invoker.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CalendarDateUtils {
	
	private static final Logger log = LoggerFactory.getLogger(CalendarDateUtils.class);
	
	public static String formatDate(Calendar calendarDate) {
		if (calendarDate == null) {
			log.info("Data informada está nula");
			return null;
		}
		
		Date date = calendarDate.getTime();
		SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-YYYY-HH-mm-ss");
		
		return dateFormatter.format(date);
	}
	
}