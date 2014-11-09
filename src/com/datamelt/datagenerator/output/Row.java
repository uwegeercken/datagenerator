package com.datamelt.datagenerator.output;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * a row object defines a row in an output ASCII file and thus
 * contains 1 or many fields. the fields will be put together
 * to build either a delimited row or a fixed length row.
 * 
 * @author uwe geercken - uwe.geercken@datamelt.com
 */
public class Row
{
	private ArrayList <Field> fields = new ArrayList <Field>();
	HashMap <String,Field> referencedFields = new HashMap<String,Field>();
	private int type;
	private String seperator = SEPERATOR_SEMICOLON;
	
	public static final int TYPE_DELIMITED    = 0;
	public static final int TYPE_FIXED_LENGTH = 1;
	
	public static final String SEPERATOR_SEMICOLON = ";";
	
	
	public Row()
	{
	}

	/**
	 * instantiate a row object, indicating its type being either
	 * delimited (0) or fixed length (1) 
	 */
	public Row(int type)
	{
		this.type = type;
	}
	
	/**
	 * add a field object to this row 
	 */
	public void addField(Field field)
	{
		if(type== TYPE_FIXED_LENGTH)
		{
			field.setFillWithSpaces(true);
		}
		fields.add(field);
	}
	
	/**
	 * add a field object to this row by defining the category
	 * of the field and its maximum length
	 */
	public void addField(int fieldType, String category,int length)
	{
		boolean fillWithSpaces=false;
		if(type== TYPE_FIXED_LENGTH)
		{
			fillWithSpaces=true;
		}
		Field field = new Field(fieldType,category,length,fillWithSpaces);
		addField(field);
	}
	
	/**
	 * returns the value of the row as a whole, meaning all fields are
	 * put together and the resulting value is returned, being either a
	 * fixed length ASCII string or a string with appropriate delimiter.  
	 */
	public String getValue()
	{
		StringBuffer buffer = new StringBuffer();
		for (int i=0;i<fields.size();i++)
		{
			Field field = fields.get(i);
			if(field.getOutput())
			{
				buffer.append(field.getValue());
				if(type==TYPE_DELIMITED)
				{
					buffer.append(seperator);
				}
			}
		}
		String value = buffer.toString();
		if(type==TYPE_DELIMITED && value.endsWith(seperator))
		{
			value = value.substring(0,value.length()-seperator.length());
		}
		return value;
	}

	/**
	 * returns the separator used. only applicable if the type of the
	 * output row is delimited. 
	 */
	public String getSeperator() 
	{
		return seperator;
	}

	/**
	 * sets the separator to be used between the fields of a delimited ASCII
	 * file 
	 */
	public void setSeperator(String seperator) 
	{
		this.seperator = seperator;
	}

	/**
	 * returns an ArrayList of fields belonging to this row object 
	 */
	public ArrayList <Field> getFields() 
	{
		return fields;
	}

	/**
	 * returns the type of row object: either delimited (0) or fixed length (1) 
	 */
	public int getType() 
	{
		return type;
	}

	/**
	 * sets the type of row object: either delimited (0) or fixed length (1)
	 */
	public void setType(int type)
	{
		this.type = type;
	}
	
	/**
	 * returns a HashMap of Field objects that are used as referenced
	 * by other fields. these may be reused.
	 * 
	 */
	public HashMap <String,Field> getReferencedFields()
	{
		return referencedFields;
	}

	public void setReferencedFields(HashMap<String, Field> referencedFields)
	{
		this.referencedFields = referencedFields;
	}
}
