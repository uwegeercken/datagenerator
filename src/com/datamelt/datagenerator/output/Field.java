
package com.datamelt.datagenerator.output;

import com.datamelt.datagenerator.RegularExpressionDataGenerator;
import com.datamelt.datagenerator.util.Generator;

/**
 * a field object defines a field of a row in an output ASCII file.
 * all fields of a row together build a string of data to be output.
 * 
 * @author uwe geercken - uwe.geercken@web.de
 */
public class Field
{
	private int type;
	private int length = -1;
	private boolean fillWithSpaces;
	private String value;
	private String category;
	private String pattern;
	private String id;
	private String reference;
	private long dateTimeMilliseconds;
	private boolean output = true; // per default all fields will be output
	private boolean referenceField=false;
	private boolean valueGenerated=false; // steers if a value should be re-generated or not
	
	
	private static final String POSSIBLE_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
	private static String possibleCharacters        = POSSIBLE_CHARACTERS;
	private static final String spaceCharacter      = " ";
	
	public static final int TYPE_CATEGORY           = 0;
	public static final int TYPE_RANDOM             = 1;
	public static final int TYPE_REGEX              = 2;
	public static final int TYPE_DATETIME           = 3;
	public static final int TYPE_REFERENCE          = 4;
	
	/**
	 * default constructor
	 */
	public Field()
	{
	
	}
	
	/**
	 * constructur with type of field as parameter 
	 */
	public Field(int fieldType)
	{
		this.type = fieldType;
	}
	
	/**
	 * a field may belong to a category - in this case the value of the field will
	 * be taken from the specified category. length indicates the max length of the
	 * field. the argument fillWithSpaces indicates if, when a value is shorter than
	 * the specified length, if it should be filled up with spaces. 
	 */
	public Field(int fieldType,String category, int length, boolean fillWithSpaces)
	{
		this.type = fieldType;
		this.category = category;
		this.length = length;
		this.fillWithSpaces=fillWithSpaces;
	}
	
	/**
	 * used for category fields. in case of a fixed length file, if the 
	 * length of the value is shorter than the specified length of the field
	 * then spaces are added to the end.
	 * 
	 * if the value is longer than the specified length of the field, then
	 * it will cut then characters that are exceeding it.
	 */
	public void setValue(String value)
	{
		if(value.length()>length && length!=-1)
		{
			this.value = value.substring(0,length);
		}
		else if(value.length()<length)
		{
			StringBuffer buffer = new StringBuffer(value);
			if(fillWithSpaces)
			{
				int numberOfSpaces = length - value.length();
				for(int i=0;i<numberOfSpaces;i++)
				{
					buffer.append(spaceCharacter);
				}
			}
			this.value = buffer.toString();
		}
		else
		{
			this.value = value;	
		}
	}
	
	/**
	 * method generates a value based on the variable: POSSIBLE_VALUES,
	 * and up to the length of the field as specified.
	 */
	public void generateRandomValue()
	{
		value = Generator.generateRandomValue(possibleCharacters, length);
	}
	
	/**
	 * method generates a value based on the regular expression pattern provided
	 * and up to the length of the field as specified.
	 */
	public void generateRegularExpressionValue()
	{
		RegularExpressionDataGenerator generator = new RegularExpressionDataGenerator();
		value = generator.generateData(pattern);
	}
	
	/**
	 * method generates a value based on the date/time pattern provided
	 * The minimumMilliSeconds and the maximumMilliseconds are the minimum
	 * and maximum long values for the date/time.
	 */
	public void generateDateTimeValue(long minimumMilliSeconds, long maximumMilliSeconds)
	{
		dateTimeMilliseconds = Generator.getRandomMilliseconds(minimumMilliSeconds, maximumMilliSeconds);
	}
	
	public String formatDateTimeValue()
	{
		// format the random date with the given pattern
		return Generator.formatDateTimeValue(dateTimeMilliseconds, pattern);
	}
	
	/**
	 * returns the value of the field 
	 */
	public String getValue()
	{
		return value;
	}

	/**
	 * returns the name of the category that this field belongs to. 
	 */
	public String getCategory()
	{
		return category;
	}

	/**
	 * returns the type of the field
	 */
	public int getType()
	{
		return type;
	}
	
	/**
	 * returns if a field is filled with trailing spaces or not 
	 */
	public boolean isFillWithSpaces() 
	{
		return fillWithSpaces;
	}
	
	/**
	 * defines if a field should be filled with trailing spaces,
	 * for the output in a fixed length ASCII file 
	 */
	public void setFillWithSpaces(boolean fillWithSpaces) 
	{
		this.fillWithSpaces = fillWithSpaces;
	}
	
	/**
	 * returns the length of the field
	 */
	public int getLength() 
	{
		return length;
	}
	
	/**
	 * sets the length of the field 
	 */
	public void setLength(int length) 
	{
		this.length = length;
	}
	
	/**
	 * returns the pattern used for a regular expression type field 
	 */
	public String getPattern() 
	{
		return pattern;
	}
	
	/**
	 * sets the pattern for a regular expression type field 
	 */
	public void setPattern(String pattern) 
	{
		this.pattern = pattern;
	}
	
	/**
	 * sets the name of the category of the field
	 */
	public void setCategory(String category) 
	{
		this.category = category;
	}
	
	/**
	 * sets the type of the field 
	 */
	public void setType(int type) 
	{
		this.type = type;
	}
	
	/**
	 * sets the possible characters that shall be used when
	 * generating random data 
	 */
	public static void setPossibleCharacters(String characters)
	{
		possibleCharacters = characters;
	}
	
	/**
	 * gets the possible characters that shall be used when
	 * generating random data 
	 */
	public static String getPossibleCharacters()
	{
		return possibleCharacters;
	}

	/**
	 * returns the id of the field
	 * 
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * sets the id of the field. it will be used to uniquely 
	 * identify a field
	 * 
	 */
	public void setId(String id) 
	{
		this.id = id;
	}

	/**
	 * fields can reference other fields. this method returns
	 * the reference of this field to the id of another field
	 * 
	 */
	public String getReference() 
	{
		return reference;
	}

	/**
	 * sets the value of the reference of this field to the id
	 * of another field 
	 * 
	 */
	public void setReference(String reference) 
	{
		this.reference = reference;
	}

	public long getDateTimeMilliseconds() 
	{
		return dateTimeMilliseconds;
	}

	public void setDateTimeMilliseconds(long dateTimeMilliseconds)
	{
		this.dateTimeMilliseconds = dateTimeMilliseconds;
	}

	public boolean getOutput()
	{
		return output;
	}

	public void setOutput(boolean output)
	{
		this.output = output;
	}

	public boolean getReferenceField()
	{
		return referenceField;
	}

	public void setReferenceField(boolean referenceField)
	{
		this.referenceField = referenceField;
	}
	public boolean getValueGenerated()
	{
		return valueGenerated;
	}

	public void setValueGenerated(boolean valueGenerated)
	{
		this.valueGenerated = valueGenerated;
	}
}
