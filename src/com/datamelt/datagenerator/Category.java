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

import java.util.ArrayList;
import java.util.Random;

/**
 * class used to store the words/values that are applicable
 * to a specific type of category.<br />
 * <br />
 * e.g. a category of [fruits] could contain a list
 * of all fruits. or a category of [names] could contain
 * a list of valid first names.<br />
 * <br />
 * @author uwe geercken - uwe.geercken@web.de
 *
 */
public class Category
{
	private String type;
	private ArrayList <String> words;
	private int numberOfUsage; 
	
	/** constructor to instantiate a category. pass the name of the
	 *  category, so it can be identified by its name. 
	 */
	public Category(String type)
	{
		this.type=type;
	}
	
	/**
	 * method add a chosen word to this category 
	 */
	public void addWord(String word)
	{
		if(words==null)
		{
			words = new ArrayList <String>();
		}
		words.add(word);
	}
	
	/**
	 * method retrieves a word from this category
	 * based on its index 
	 */
	public String getWord(int index)
	{
		return words.get(index);
	}
	
	/**
	 * method retrieves a random word from this category.
	 * a random number is generated between zero and the 
	 * number of words in this category minus 1. that word
	 * is then returned.
	 */
	public String getRandomWord() throws Exception
	{
		Random rand = new Random();
		if(words!=null && words.size()>0)
		{
			// choose a number between 0 (inclusive) and the number of words (exclusive)
			int number = rand.nextInt(words.size());
			return words.get(number);
		}
		else
		{
			throw new Exception("no words found in category: " + getType());
		}
	}
	
	/**
	 * method returns the type (its name) of the category
	 */
	public String getType()
	{
		return type;
	}

	/**
	 * defined how many times the category is used, as defined in the
	 * rowlayout file.
	 * 
	 * its purpose is to unload the category when it is no more used. 
	 */
	public int getNumberOfUsage()
	{
		return numberOfUsage;
	}

	/**
	 * set the number of usage of this category. 
	 */
	public void setNumberOfUsage(int numberOfUsage)
	{
		this.numberOfUsage = numberOfUsage;
	}
}
