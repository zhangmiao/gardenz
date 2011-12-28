package org.walkmanz.gardenz.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DateUtils {

	/**
	 * 按照指定的格式格式化时间
	 * @param str
	 * @param fmt
	 * @return
	 */
	public static Date convertStrToDate(String str, String pattern){
		Date date = null;
		DateFormat format = new SimpleDateFormat(pattern);
		try {
			date = format.parse(str);
		} catch (ParseException e) {
			throw new RuntimeException(e.getMessage());
		}
		return date;
	}
	
	/**
     * 按照指定的格式格式化当前时间
     * @param String pattern
     */
    public static String formatCurrentTime(String pattern) {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    /**
     * 按照指定的格式格式化时间
     * @param Date time
     * @param String patter
     */
    public static String convertDateToStr(Date time, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(time);
    }
    
    
    private final static String[] MONTHS = { "Jan", "Feb", "Mar", "Apr", "May",
        "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };

	/*
	 * Creates the DateFormat object used to parse/format
	 * dates in FTP format.
	 */
	private static final ThreadLocal<DateFormat> FTP_DATE_FORMAT = new ThreadLocal<DateFormat>() {
	
	    @Override
	    protected DateFormat initialValue() {
	        DateFormat df=new SimpleDateFormat("yyyyMMddHHmmss");
	        df.setLenient(false);
	        df.setTimeZone(TimeZone.getTimeZone("GMT"));
	        return df;
	    }
	    
	};
	
	/**
	 * Get unix style date string.
	 */
	public final static String getUnixDate(long millis) {
	    if (millis < 0) {
	        return "------------";
	    }
	
	    StringBuffer sb = new StringBuffer(16);
	    Calendar cal = new GregorianCalendar();
	    cal.setTimeInMillis(millis);
	
	    // month
	    sb.append(MONTHS[cal.get(Calendar.MONTH)]);
	    sb.append(' ');
	
	    // day
	    int day = cal.get(Calendar.DATE);
	    if (day < 10) {
	        sb.append(' ');
	    }
	    sb.append(day);
	    sb.append(' ');
	
	    long sixMonth = 15811200000L; // 183L * 24L * 60L * 60L * 1000L;
	    long nowTime = System.currentTimeMillis();
	    if (Math.abs(nowTime - millis) > sixMonth) {
	
	        // year
	        int year = cal.get(Calendar.YEAR);
	        sb.append(' ');
	        sb.append(year);
	    } else {
	
	        // hour
	        int hh = cal.get(Calendar.HOUR_OF_DAY);
	        if (hh < 10) {
	            sb.append('0');
	        }
	        sb.append(hh);
	        sb.append(':');
	
	        // minute
	        int mm = cal.get(Calendar.MINUTE);
	        if (mm < 10) {
	            sb.append('0');
	        }
	        sb.append(mm);
	    }
	    return sb.toString();
	}
	
	/**
	 * Get ISO 8601 timestamp.
	 */
	public final static String getISO8601Date(long millis) {
	    StringBuffer sb = new StringBuffer(19);
	    Calendar cal = new GregorianCalendar();
	    cal.setTimeInMillis(millis);
	
	    // year
	    sb.append(cal.get(Calendar.YEAR));
	
	    // month
	    sb.append('-');
	    int month = cal.get(Calendar.MONTH) + 1;
	    if (month < 10) {
	        sb.append('0');
	    }
	    sb.append(month);
	
	    // date
	    sb.append('-');
	    int date = cal.get(Calendar.DATE);
	    if (date < 10) {
	        sb.append('0');
	    }
	    sb.append(date);
	
	    // hour
	    sb.append('T');
	    int hour = cal.get(Calendar.HOUR_OF_DAY);
	    if (hour < 10) {
	        sb.append('0');
	    }
	    sb.append(hour);
	
	    // minute
	    sb.append(':');
	    int min = cal.get(Calendar.MINUTE);
	    if (min < 10) {
	        sb.append('0');
	    }
	    sb.append(min);
	
	    // second
	    sb.append(':');
	    int sec = cal.get(Calendar.SECOND);
	    if (sec < 10) {
	        sb.append('0');
	    }
	    sb.append(sec);
	
	    return sb.toString();
	}
	
	/**
	 * Get FTP date.
	 */
	public final static String getFtpDate(long millis) {
	    StringBuffer sb = new StringBuffer(20);
	    Calendar cal = new GregorianCalendar();
	    cal.setTimeInMillis(millis);
	
	    // year
	    sb.append(cal.get(Calendar.YEAR));
	
	    // month
	    int month = cal.get(Calendar.MONTH) + 1;
	    if (month < 10) {
	        sb.append('0');
	    }
	    sb.append(month);
	
	    // date
	    int date = cal.get(Calendar.DATE);
	    if (date < 10) {
	        sb.append('0');
	    }
	    sb.append(date);
	
	    // hour
	    int hour = cal.get(Calendar.HOUR_OF_DAY);
	    if (hour < 10) {
	        sb.append('0');
	    }
	    sb.append(hour);
	
	    // minute
	    int min = cal.get(Calendar.MINUTE);
	    if (min < 10) {
	        sb.append('0');
	    }
	    sb.append(min);
	
	    // second
	    int sec = cal.get(Calendar.SECOND);
	    if (sec < 10) {
	        sb.append('0');
	    }
	    sb.append(sec);
	
	    // millisecond
	    sb.append('.');
	    int milli = cal.get(Calendar.MILLISECOND);
	    if (milli < 100) {
	        sb.append('0');
	    }
	    if (milli < 10) {
	        sb.append('0');
	    }
	    sb.append(milli);
	    return sb.toString();
	}
	/*
	 *  Parses a date in the format used by the FTP commands 
	 *  involving dates(MFMT, MDTM)
	 */
	public final static Date parseFTPDate(String dateStr) throws ParseException{
	    return FTP_DATE_FORMAT.get().parse(dateStr);
	    
	}

}

