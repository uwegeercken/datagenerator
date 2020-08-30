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
package com.datamelt.datagenerator;

import java.util.Random;

/**
 * class is used to generate random test data based on a given regular expression.<br />
 * <br />
 * as this is the opposite way regular expressions are normally used, there are certain limitations
 * of what expressions can be used in the pattern:<br />
 * <br />
 * - you can use character ranges to group characters, such as [A-Z] or [0-9] or combine them [A-Ka-z0-5]<br />
 * - besides using character ranges (e.g. [a-w]) you can add other characters: [A-LBCDF]<br />
 * - you can use multipliers behind groups: [AKMN]{1,4} or [abcdep]{18}<br />
 * - you can use escape sequences in the pattern: [AHGJ\\-po]<br />
 * <br />
 * - you can NOT use * or ? because using these the program would be unable to determine how much characters
 *   to generate.<br />
 * - you can not use a logical or (|) character<br />
 * <br />
 * example:<br />
 * the pattern is:        [A-Za-z0-9\\-_]{1,10}@[A-Za-z0-9\\-_]{1,10}.[A-Za-z]{3}<br />
 * the program generates: 16ibq@Z80FVg.tx<br />
 *                    or: BFPgm@w.hgw<br />
 *                    or: 2NF@MQng.Gct<br />
 * <br />
 * @author uwe geercken - uwe.geercken@web.de
 *
 */
public class RegularExpressionDataGenerator
{
	public static final String POSSIBLE_VALUES_UPPER_ALPHA     = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static final String POSSIBLE_VALUES_LOWER_ALPHA     = "abcdefghijklmnopqrstuvwxyz";
	public static final String POSSIBLE_VALUES_NUMERIC         = "0123456789";
	
	private static final int SEQUENCE_TYPE_UPPER_ALPHA = 0;
	private static final int SEQUENCE_TYPE_LOWER_ALPHA = 1;
	private static final int SEQUENCE_TYPE_NUMERIC     = 2;
	
	/**
	 *	method returns randomly generated test data according to a given
	 *  regular expression pattern.
	 *  
	 *  not the complete set of regular expressions can be used. see description at the top.
	 */
	public String generateData(String regularExpressionPattern)
	{
		return processPattern(regularExpressionPattern);
	}
	
	/**
	 *	pass a generated string of test data and the pattern that the data was generated for
	 *  - using the generateData() method of this class - to check if the test data fits to the
	 *  pattern. 
	 */
	public boolean checkTestdata(String testdata,String regularExpressionPattern)
	{
		return regularExpressionPattern.matches(testdata);
	}
	
	private String processPattern(String pattern)
	{
		final String startOfGroup = "[";
		final String endOfGroup   = "]";
		
		final String startOfMultiplier = "{";
		final String endOfMultiplier = "}";
		
		StringBuffer buffer = new StringBuffer();
		
		do
		{
			int posStart = pattern.indexOf(startOfGroup);
			int posEnd = pattern.indexOf(endOfGroup);
			String multiplierString="";
			// check if there is a group
			if(posStart>=0 && posEnd>posStart)
			{
				// all before the group is not a group and will be added to the buffer as is
				buffer.append(pattern.substring(0,posStart));
				
				String group = pattern.substring(posStart,posEnd+1);
				
				int posStartofMultiplier = posEnd+1;
				// is there anything right of the group we found?
				if(pattern.length()>posStartofMultiplier)
				{
					String restOfPattern = pattern.substring(posStartofMultiplier);
					if(restOfPattern.length()>=3 && restOfPattern.startsWith(startOfMultiplier))
					{
						int posEndOfMultiplier = restOfPattern.indexOf(endOfMultiplier);
						multiplierString = restOfPattern.substring(1,posEndOfMultiplier);
						pattern = restOfPattern.substring(posEndOfMultiplier+1);
					}
					else
					{
						pattern = restOfPattern;
					}
				}
				else // there is nothing left right of the pattern string
				{
					// set the remaining pattern, so no duplicate processing is done
					pattern = "";
				}
				String possibleValues = handleGroup(group);
				buffer.append(getRandomValue(possibleValues, multiplierString));
			}
			else //no group found, meaning there are only characters, but no groupings in the pattern
			{
				buffer.append(pattern);
				pattern="";
			}
		} while( pattern.length()>0);
		return buffer.toString();
	}
	
	private String getRandomValue(String possibleValues, String multiplierString)
	{
		int multiplierLow=1;
		int multiplierHigh=1;
		int posDevider= multiplierString.indexOf(",");
		// default multiplier is one
		int multiplier;
		if (posDevider>=0)
		{
			try
			{
				multiplierLow = Integer.parseInt(multiplierString.substring(0,posDevider));
				multiplierHigh = Integer.parseInt(multiplierString.substring(posDevider+1));
			}
			catch(Exception ex)
			{
				// error with the numbers
				multiplier=1;
			}
			int differenzHighLow = multiplierHigh - multiplierLow;
			Random randomMultiplier = new Random();
			multiplier = multiplierLow + randomMultiplier.nextInt(differenzHighLow+1);
		}
		else
		{
			try
			{
				multiplier = Integer.parseInt(multiplierString);
			}
			catch(Exception ex)
			{
				// error with the number
				multiplier=1;
			}
		}
		StringBuffer buffer = new StringBuffer(multiplier);
		
		for (int i=0;i<(multiplier);i++)
		{
			Random random = new Random();
			int randomNumber = random.nextInt(possibleValues.length());
			buffer.append(possibleValues.substring(randomNumber,randomNumber+1));
		}
		return buffer.toString();
		
	}
	
	private String handleGroup(String group)
	{
		String pattern = group.substring(1,group.length()-1);
		StringBuffer buffer = new StringBuffer();
		String sequenceStartCharacter ="";
		boolean sequenceStarted=false;
		boolean escapeStarted=false;
		
		for(int i=0;i<pattern.length();i++)
		{
			String character = pattern.substring(i,i+1);
			if(character.equals("-") && !escapeStarted)
			{
				sequenceStarted = true;
				sequenceStartCharacter = buffer.substring(buffer.length()-1);
			}
			else if(character.equals("\\") && !escapeStarted)
			{
				escapeStarted = true;
			}
			else
			{
				if(!sequenceStarted)
				{
					buffer.append(character);
				}
				else
				{
					sequenceStarted = false;
					buffer.append(getSequenceOfCharacters(sequenceStartCharacter,character));
				}
				escapeStarted = false;
			}
			
		}
		
		return buffer.toString();
	}
	
	private int checkCharacterType(String character)
	{
		boolean isNumeric=false;
		try
		{
			Integer.parseInt(character);
			isNumeric = true;
		}
		catch(Exception ex)
		{
			// not numeric
		}
		if(isNumeric)
		{
			return SEQUENCE_TYPE_NUMERIC;
		}
		else if (character.equals(character.toUpperCase()))
		{
			return SEQUENCE_TYPE_UPPER_ALPHA;
		}
		else
		{
			return SEQUENCE_TYPE_LOWER_ALPHA;
		}
	}
	
	private String getSequenceOfCharacters(String sequenceStartCharacter, String sequenceEndCharacter)
	{
		final int sequenceStartCharacterType = checkCharacterType(sequenceStartCharacter);
		int posStart;
		int posEnd;
		String sequenceString;
		
		if(sequenceStartCharacterType==SEQUENCE_TYPE_UPPER_ALPHA)
		{
			posStart = POSSIBLE_VALUES_UPPER_ALPHA.indexOf(sequenceStartCharacter);
			posEnd = POSSIBLE_VALUES_UPPER_ALPHA.indexOf(sequenceEndCharacter);
			sequenceString = POSSIBLE_VALUES_UPPER_ALPHA.substring(posStart+1,posEnd+1);
		}
		else if(sequenceStartCharacterType==SEQUENCE_TYPE_LOWER_ALPHA)
		{
			posStart = POSSIBLE_VALUES_LOWER_ALPHA.indexOf(sequenceStartCharacter);
			posEnd = POSSIBLE_VALUES_LOWER_ALPHA.indexOf(sequenceEndCharacter);
			sequenceString = POSSIBLE_VALUES_LOWER_ALPHA.substring(posStart+1,posEnd+1);
		}
		else
		{
			posStart = POSSIBLE_VALUES_NUMERIC.indexOf(sequenceStartCharacter);
			posEnd = POSSIBLE_VALUES_NUMERIC.indexOf(sequenceEndCharacter);
			sequenceString = POSSIBLE_VALUES_NUMERIC.substring(posStart+1,posEnd+1);
		}
		return sequenceString;
	}
}
