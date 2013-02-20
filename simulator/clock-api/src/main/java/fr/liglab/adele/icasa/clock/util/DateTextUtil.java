package fr.liglab.adele.icasa.clock.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTextUtil {

	public static String SIMULATOR_DATE_FORMAT = "dd/MM/yyyy-HH:mm:ss";

	public static Date getDateFromText(String dateStr) {
		if (dateStr != null && (!dateStr.isEmpty())) {
			SimpleDateFormat formatter = new SimpleDateFormat(SIMULATOR_DATE_FORMAT);
			Date startDate = null;

			try {
				startDate = formatter.parse(dateStr);
			} catch (ParseException e) {
				e.printStackTrace();
			} finally {
				if (startDate == null)
					startDate = null;
			}
			return startDate;			
		}
		return null;
	}

	
	public static String getTextDate(long timeInMs) {
		return getTextDate((new Date(timeInMs)));
	}

	public static String getTextDate(Date date) {
		SimpleDateFormat format = new SimpleDateFormat(SIMULATOR_DATE_FORMAT);
		return format.format(date);
	}
	
}
