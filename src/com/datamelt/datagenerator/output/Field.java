
package com.datamelt.datagenerator.output;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

import com.datamelt.datagenerator.RegularExpressionDataGenerator;

/**
 * a field object defines a field of a row in an output ASCII file.
 * all fields of a row together build a string of data to be output.
 * 
 * @author uwe geercken - uwe.geercken@datamelt.com
 */
public class Field
{
	private int type;
	private int length;
	private boolean fillWithSpaces;
	private String value;
	private String category;
	private String pattern;
	private String id;
	private String reference;
	private long dateTimeMilliseconds;
	
	private static final String POSSIBLE_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
	private static String possibleCharacters        = POSSIBLE_CHARACTERS;
	
	public static final int TYPE_CATEGORY           = 0;
	public static final int TYPE_RANDOM             = 1;
	public static final int TYPE_REGEX              = 2;
	public static final int TYPE_DATETIME           = 3;
	
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
	 * sets the value of a field. in case of a fixed length file, if the 
	 * length of the value is shorter than the specified length of the field
	 * then spaces are added to the end.
	 * 
	 * if the value is longer than the specified length of the field, then
	 * it will cut then characters that are exceeding it.
	 */
	public void setValue(String value)
	{
		// no category means we do not take a word from the list of
		// available word, but generate a word randomly
		if(type==TYPE_RANDOM)
		{
			generateRandomValue();
		}
		else if(type==TYPE_REGEX)
		{
			generateRegularExpressionValue();
		}
		
		if(value.length()>length)
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
					buffer.append(" ");
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
		int possibilities = possibleCharacters.length();
		StringBuffer generatedString = new StringBuffer();
		for (int i=0;i<length;i++)
		{
			Random rand = new Random();
			int position = rand.nextInt(possibilities);
			generatedString.append(possibleCharacters.substring(position,position+1));
		}
		value=generatedString.toString();
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
	 * and up to the length of the field as specified.
	 * Formatting must be according to the java SimpleDateFormat class.
	 * The minimumMilliSeconds and the maximumMilliseconds are the minimum
	 * and maximum long values for the date/time.
	 */
	public void generateDateTimeValue(long minimumMilliSeconds, long maximumMilliSeconds)
	{
		
		
		// generate a random long value
		Random rand = new Random();
			
		// calculate a new random long using the modulo operator and the
		// two values
		long randomMilliseconds;
		do
		{
			randomMilliseconds = rand.nextLong() % maximumMilliSeconds;
			
		} while (randomMilliseconds<minimumMilliSeconds);

		dateTimeMilliseconds = randomMilliseconds;
		
	}
	
	public void formatDateTimeValue()
	{
		// use the SimpleDateFormat class for formatting
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		
		// get the current date in milliseconds
		Calendar cal = Calendar.getInstance();
		// set the new random date
		cal.setTimeInMillis(dateTimeMilliseconds);
		
		// format the random date with the given pattern
		value = sdf.format(cal.getTime());
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
	public String getId() {
		return id;
	}

	/**
	 * sets the id of the field. it will be used to uniquely 
	 * identify a field
	 * 
	 */
	public void setId(String id) {
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
	
}
