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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;
/**
 * class to generate several different types of values or to format
 * them appropriately.
 * 
 * 
 * @author uwe geercken - uwe.geercken@web.de
 *
 */
public class Generator
{
	/**
	 * method generates a value based on the date/time pattern provided
	 * and up to the length of the field as specified.
	 * Formatting must be according to the java SimpleDateFormat class.
	 * The minimumMilliSeconds and the maximumMilliseconds are the minimum
	 * and maximum long values for the date/time.
	 */
	public static long getRandomMilliseconds(long minimumMilliSeconds, long maximumMilliSeconds)
	{
		
		// generate a random long value
		Random rand = new Random();
			
		// calculate a new random long using the modulo operator and the
		// two given values
		long randomMilliseconds;
		do
		{
			randomMilliseconds = rand.nextLong() % maximumMilliSeconds;
			
		} while (randomMilliseconds<minimumMilliSeconds);

		return randomMilliseconds;
		
	}
	
	/**
	 * to format a datetime value with a given pattern.
	 * 
	 * the pattern must correspond to valid pattern as described in 
	 * the SimpleDateFormat class
	 * 
	 */
	public static String formatDateTimeValue(long dateTimeMilliseconds, String pattern)
	{
		// use the SimpleDateFormat class for formatting
		SimpleDateFormat sdf;
		
		// get the current date in milliseconds
		Calendar cal = Calendar.getInstance();
		// set the new random date
		cal.setTimeInMillis(dateTimeMilliseconds);
		
		if(pattern.equals("q"))
		{
			if(cal.get(Calendar.MONTH)<=2)
			{
				return "1";
			}
			else if(cal.get(Calendar.MONTH)>2 && cal.get(Calendar.MONTH)<=5)
			{
				return "2";
			}
			else if(cal.get(Calendar.MONTH)>5 && cal.get(Calendar.MONTH)<=8)
			{
				return "3";
			}
			else
			{
				return "4";
			}

		}
		else if(pattern.equals("Q"))
		{
			if(cal.get(Calendar.MONTH)<=2)
			{
				return "q1";
			}
			else if(cal.get(Calendar.MONTH)>2 && cal.get(Calendar.MONTH)<=5)
			{
				return "q2";
			}
			else if(cal.get(Calendar.MONTH)>5 && cal.get(Calendar.MONTH)<=8)
			{
				return "q3";
			}
			else
			{
				return "q4";
			}

		}
		else if(pattern.equals("h"))
		{
			if(cal.get(Calendar.MONTH)<=6)
			{
				return "1";
			}
			else
			{
				return "2";
			}
		}
		else if(pattern.equals("H"))
		{
			if(cal.get(Calendar.MONTH)<=6)
			{
				return "h1";
			}
			else
			{
				return "h2";
			}
		}
		else
		{
			// format the random date with the given pattern
			 sdf = new SimpleDateFormat(pattern);
			return sdf.format(cal.getTime());
		}

	}
	
	/**
	 * generates a random value based on the possible characters defined and up
	 * to the defined length
	 */
	public static String  generateRandomValue(String possibleCharacters, int length)
	{
		int possibilities = possibleCharacters.length();
		StringBuffer generatedString = new StringBuffer();
		for (int i=0;i<length;i++)
		{
			Random rand = new Random();
			int position = rand.nextInt(possibilities);
			generatedString.append(possibleCharacters.substring(position,position+1));
		}
		return generatedString.toString();
	}
}
