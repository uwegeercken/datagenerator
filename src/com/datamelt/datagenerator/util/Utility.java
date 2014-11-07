package com.datamelt.datagenerator.util;

import java.util.Calendar;

public class Utility
{
	public  static final int DEFAULT_MAXDATE_YEAR   = 2199;
	private static final int DEFAULT_MAXDATE_MONTH  = 11;
	private static final int DEFAULT_MAXDATE_DAY    = 31;
	private static final int DEFAULT_MAXDATE_HOUR   = 23;
	private static final int DEFAULT_MAXDATE_MINUTE = 59;
	private static final int DEFAULT_MAXDATE_SECOND = 59;
	
	public  static final int DEFAULT_MINDATE_YEAR   = 1970;
	private static final int DEFAULT_MINDATE_MONTH  = 0;
	private static final int DEFAULT_MINDATE_DAY    = 1;
	private static final int DEFAULT_MINDATE_HOUR   = 0;
	private static final int DEFAULT_MINDATE_MINUTE = 0;
	private static final int DEFAULT_MINDATE_SECOND = 0;
	
	public static long getMaxDate(int maximumYear)
	{
		// set the desired maximum date
		Calendar calMax = Calendar.getInstance();
		calMax.set(Calendar.YEAR, maximumYear);
		calMax.set(Calendar.MONTH, DEFAULT_MAXDATE_MONTH);
		calMax.set(Calendar.DAY_OF_MONTH,DEFAULT_MAXDATE_DAY);
		calMax.set(Calendar.HOUR_OF_DAY, DEFAULT_MAXDATE_HOUR);
		calMax.set(Calendar.MINUTE, DEFAULT_MAXDATE_MINUTE);
		calMax.set(Calendar.SECOND, DEFAULT_MAXDATE_SECOND);
		// get the milliseconds value for the max date
		return calMax.getTimeInMillis();
	}
	
	public static long getMinDate(int minimumYear)
	{
		// set the desired maximum date
		Calendar calMin = Calendar.getInstance();
		calMin.set(Calendar.YEAR, minimumYear);
		calMin.set(Calendar.MONTH, DEFAULT_MINDATE_MONTH);
		calMin.set(Calendar.DAY_OF_MONTH,DEFAULT_MINDATE_DAY);
		calMin.set(Calendar.HOUR_OF_DAY, DEFAULT_MINDATE_HOUR);
		calMin.set(Calendar.MINUTE, DEFAULT_MINDATE_MINUTE);
		calMin.set(Calendar.SECOND, DEFAULT_MINDATE_SECOND);
		// get the milliseconds value for the max date
		return calMin.getTimeInMillis();
	}
}
