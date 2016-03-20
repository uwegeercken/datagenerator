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
