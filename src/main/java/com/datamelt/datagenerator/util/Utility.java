/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */ 
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
