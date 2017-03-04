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

/**
 * class to collect categories and the words assigned to them
 * in a collection.<br />
 * 
 * @author uwe geercken - uwe.geercken@web.de
 *
 */
public class CategoryCollection
{
	private ArrayList <Category> categories;
	
	/**
	 * adds a category object to the collection 
	 */
	public void add(Category category)
	{
		if(categories==null)
		{
			categories = new ArrayList <Category>();
		}
		categories.add(category);
	}
	
	/**
	 * removes a category object from the collection
	 * based on its index 
	 */
	public void remove(int index)
	{
		categories.remove(index);
	}
	
	/**
	 * retrieves a category object from the collection
	 * based on its index 
	 */
	public Category get(int index)
	{
		return (Category)categories.get(index);
	}
	
	/**
	 * retrieves a category object from the collection
	 * based on its name.
	 */
	public Category get(String categoryType)
	{
		int found=-1;
		if (categories!=null)
		{
			for(int i=0;i<categories.size();i++)
			{
				Category cat = categories.get(i);
				if(cat.getType().equals(categoryType))
				{
					found=i;
					break;
				}
			}
			if(found>=0) // we have found the category
			{
				return categories.get(found);
			}
			else // no category was found
			{
				return null;
			}
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * returns the number of categories in this collection 
	 */
	public int count()
	{
		return categories.size();
	}
	
}
