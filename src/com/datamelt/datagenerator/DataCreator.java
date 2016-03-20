package com.datamelt.datagenerator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import org.xml.sax.SAXException;

import com.datamelt.datagenerator.output.Field;
import com.datamelt.datagenerator.output.Row;
import com.datamelt.datagenerator.parser.xml.Parser;
import com.datamelt.datagenerator.util.Utility;

/**
 * application to generate random ASCII rows of data, based on predefined categories,
 * using a regular expression pattern or by generating random strings of text.<br />
 * <br />
 * categories are text files, located together in one folder and contain one word per row
 * of words applicable to this category. all files in the given folder are read during
 * processing.<br />
 * <br />
 * output may be to system.out or a file. the layout of the output is defined in an xml file
 * indicating the type of output - fixed ascii or delimited. the format of the output maybe
 * in regular (mixed) case, lowercase or uppercase.<br />
 * <br />
 * for each field in an output row, the type, length of the value and from which category its
 * taken, can be specified. alternatively the value of a field can be generated randomly,
 * containing defined characters or by specifying a regular expression pattern.<br />
 * <br />
 * the name of the category file has to correspond to the category name used in the xml file
 * for defining a field.<br />
 * <br />
 *  for example:<br />
 *  the file named: trees.category contains a list of names of trees - one per row. then
 *  in the rowlayout xml file you can define a field like:<br />
 *  <br />
 *  field type="category" category="trees" length="30"<br />
 *  <br />
 *  ...indicating that a word should be randomly selected from this category file, named
 *  trees.category, with a maximum length of 30 characters.<br />
 *  <br />
 *  if you want to have the value of a field generated randomly then specify:<br />
 *  <br />
 *  field type="random" length="10"<br />
 *  <br />
 *  ...indicating that the value should be generated randomly with a maximum length of 10
 *  characters.<br />
 * <br />
 *  field type="regex" pattern="[A-Za-k]{2,6}.xyz[2-7]{4}" length="10"<br />
 *  <br />
 *  ...indicating that the value should be generated according to the given regular expression
 *   pattern with a maximum length of 10 characters.<br />
 * <br />
 *  <br />
 *  field type="datetime" pattern="yyMMdd HH:mm:ss" length="15"<br />
 *  <br />
 *  ...indicating that the value should be generated according to the given date and time format
 *   pattern with a maximum length of 8 characters. Formatting is based on the java class SimpleDateFormat.<br />
 *  <br />
 *  field type="datetime" id="mydate" pattern="yyMMdd HH:mm" length="12"<br />
 *  field type="datetime" reference="mydate" pattern="EEEE" length="12"<br />
 *  <br />
 *  ...indicating that the generated datetime value has the id "mydate". the other field references this id by
 *  use of the "reference" keyword. the fields will be based on the same exact datetime but with a different
 *  format/pattern.<br />
 * <br />
 * the maximum length of the field is interesting in this case, when the value  - generated
 * or picked from a category file - is longer than the maximum length specified in the xml file.
 * in this case the value will be shortened.<br />
 * <br />
 * if the field is shorter than the length specified - only in cases of fixed length output files - 
 * it will be filled with spaces up to the specified length.<br />
 * <br />
 * all arguments can be directly passed to the program or specified in the properties file<br />
 * <br />
 * last update: 2016-03-20, copyright: uwe geercken<br />
 * 
 * @author uwe geercken - uwe.geercken@web.de
 *
 */
public class DataCreator 
{
	private static final String APPLICATION_VERSION_NUMBER = "version 1.0";
	
	public static final String CATEGORY_FILE_EXTENSION = ".category";
	
	private CategoryCollection collection = new CategoryCollection();

	private PrintStream out 				    = System.out;
	private String outputfile;
	private long numberOfOutputLines 	     	= 10; //default=10
	private boolean verbose                   	= false;
	private int dataFormat				     	= 0; // default=0
	private long processedLinesOutputInterval;
	private int maximumYear				     	= Utility.DEFAULT_MAXDATE_YEAR;
	private int minimumYear				     	= Utility.DEFAULT_MINDATE_YEAR;
	
	long maxMilliSeconds;
	long minMilliSeconds;
	
	private String categoryFilesFolder;
	private String rowlayoutFile;
	
	public static final String PROPERTY_NUMBER_OF_OUTPUT_LINES	        = "numberofoutputlines";
	public static final String PROPERTY_PROCESSED_LINES_OUTPUT_INTERVAL	= "outputinterval";
	public static final String PROPERTY_CATEGORY_FILES_FOLDER 	        = "categoryfilesfolder";
	public static final String PROPERTY_ROW_LAYOUT_FILE 		        = "rowlayoutfile";
	public static final String PROPERTY_OUTPUTFILE 				        = "outputfile";
	public static final String PROPERTY_MAXIMUMYEAR				        = "maximumyear";
	public static final String PROPERTY_MINIMUMYEAR				        = "minimumyear";
	public static final String PROPERTY_VERBOSE					        = "verbose";
	public static final String PROPERTY_DATAFORMAT				        = "format";
	public static final String PROPERTY_POSSIBLE_CHARACTERS		        = "possiblecharacters";
	
	public static final String PROPERTIES_FILE					        = "datagenerator.properties";

	// HashMap contains fields that are referenced by other fields
	private HashMap <String,Field> referencedFields;

	public static void main(String[] args) throws Exception
	{
		DataCreator creator = new DataCreator();
		
		
		// if help was requested
		if (args !=null && args.length>0)
		{
			if(args[0].equals("-h") || args[0].equals("--help"))
			{
				help();
				
			}
			else
			{
				// load properties from file if there is such a file
				creator.loadProperties(PROPERTIES_FILE);
				
				//parse any arguments passed to the program
				creator.parseArguments(args);
		        
		        creator.generate();
			}
		}
		else
		{
			// load properties from file if there is such a file
			creator.loadProperties(PROPERTIES_FILE);
			
	        creator.generate();

		}
	}
	
	/**
	 * returns the version of the application
	 */
	public static String getVersion()
	{
		return APPLICATION_VERSION_NUMBER;
	}
	
	public void generate() throws Exception
	{
        // set the output destination if one was specified
		if(outputfile!=null && !outputfile.trim().equals(""))
		{
			out = new PrintStream(new FileOutputStream(new File(outputfile)));
		}

		// create a parser object instance
		Parser parser = new Parser();
		// parse the row layout file, containing the structure of the row and its fields
		try
		{
			parser.parse(rowlayoutFile);
			// get the reference fields from the parser
			referencedFields = parser.getReferencedFields();
			// get iterator to loop over keyset
			Iterator <String>it = referencedFields.keySet().iterator();
			while(it.hasNext())
					{
						String key = (String)it.next();
						// get reference field
						Field field = referencedFields.get(key);
						// the following checks are to check the completeness of the field attributes
						// which is important for further processing
						if(field.getId()==null)
						{
							throw new Exception("reference field of type " + field.getType() + " without id: ");
						}
						if(field.getLength()==-1 && field.getType()==Field.TYPE_RANDOM)
		            	{
		            		throw new Exception("missing length attribute for reference field: " + field.getId());
		            	}
		            	if(field.getType()==Field.TYPE_CATEGORY && field.getCategory()==null)
		            	{
		            		throw new Exception("missing category attribute for reference field: " + field.getId());
		            	}
		            	if((field.getType()==Field.TYPE_DATETIME || field.getType()==Field.TYPE_REGEX) && field.getPattern()==null)
		            	{
		            		throw new Exception("missing pattern attribute for reference field: " + field.getId());
		            	}

					}
		}
		// if there is a parsing exception from the sax parser
		catch(SAXException se)
		{
			System.out.println("error parsing rowlayout file: " + rowlayoutFile + " - " + se.getMessage());
		}
		// any other exception
		catch(Exception e)
		{
			throw new Exception("error parsing rowlayout file: " + rowlayoutFile + " - " + e.getMessage());
		}
		
		// loop over all fields of the row that are not reference fields
		// and check them
		for(int j=0;j<parser.getRow().getFields().size();j++)
		{
			// get the field from the parser
			Field field = (Field)parser.getRow().getFields().get(j);
			//check the fields if all required attributes have bween specified
			if(field.getLength()==-1 && field.getType()==Field.TYPE_RANDOM)
        	{
        		throw new Exception("missing length attribute for field: " + field.getId());
        	}
        	if(field.getType()==Field.TYPE_CATEGORY && field.getCategory()==null)
        	{
        		throw new Exception("missing category attribute for field: " + field.getId());
        	}
        	if((field.getType()==Field.TYPE_DATETIME || field.getType()==Field.TYPE_REGEX) && field.getPattern()==null)
        	{
        		throw new Exception("missing pattern attribute for field: " + field.getId());
        	}
        	if(field.getType()==Field.TYPE_REFERENCE && field.getReference()==null)
        	{
        		throw new Exception("missing reference attribute for field: " + field.getId());
        	}
		
		}
		
		// make sure that the max year is greater or equal the min year
		if(maximumYear<minimumYear)
		{
			maximumYear = minimumYear;
		}
		// get the long value for the largest allowable date value
		if(maximumYear>0)
		{
			maxMilliSeconds = Utility.getMaxDate(maximumYear); 
		}
		// get the long value for the smallest allowable date value
		if(minimumYear>0)
		{
			minMilliSeconds = Utility.getMinDate(minimumYear); 
		}
		
		if(processedLinesOutputInterval<=0)
		{
			processedLinesOutputInterval = numberOfOutputLines / 100;
		}

		// get the row object that has been constructed during parsing
		Row row = parser.getRow();
		
		// loop over all fields of the row that do not have an id
		for(int j=0;j<row.getFields().size();j++)
		{
			// get the field
			Field field = (Field)row.getFields().get(j);
			generateFieldValue(field);
		}

		long counter=0;
		
		// generate the specified number of rows
		for (long i=0;i<numberOfOutputLines;i++)
		{
			// get the row from the parser object created earlier
			//Row row = parser.getRow();
			
			// loop over all fields of the row that do not have an id
			for(int j=0;j<row.getFields().size();j++)
			{
				// get the field
				Field field = (Field)row.getFields().get(j);
				generateFieldValue(field);
			}

			// we loop over the map of reference fields and set valuegenerated to false
			// so that for the next row that will be generated, the values of the reference fields will be 
			// newly calculated
			Iterator <String>iter = referencedFields.keySet().iterator();
			while(iter.hasNext())
			{
				// get the field key				
				
				String key = (String)iter.next();
				// get the field
				Field field = referencedFields.get(key);
				if(field!=null)
				{
					field.setValueGenerated(false);
				}
			}
			// all fields have been processed. the output will go to console or file
			out.println(format(row.getValue()));
			
			// advance the counter by one
			counter++;
			
			if(verbose)
			{
				if(counter==processedLinesOutputInterval)
				{
					System.out.println("generated lines: " + (i+1));
					counter=0;
				}
			}

		}
		// close the output stream
		out.close();
	}
	
	/**
	 * sets the field value to the value of the field it references. the reference to another field is
	 * specified in the relevant row layout file. see the documentation for further details
	 * 
	 * if it references multiple fields the values of the fields are concatenated. multiple
	 * referenced fields have to be seperated using a slash character (/).
	 * if a reference specified is not existing, it will be treated as a fixed string and
	 * concatenated to the rest of the values.
	 * 
	 * for datetime fields the original datetime value of the reference field will be used. this
	 * means that seperate fields/columns can be based on the same date, which will result in consistent
	 * datetime values across the columns.
	 * 
	 * e.g. reference to one field:
	 * 
	 * 		reference="date1"
	 * 
	 * e.g. reference to two fields:
	 * 
	 * 		reference="date1/date2"
	 * 
	 * e.g. reference to two fields with a fixed string in between
	 * 
	 * 		reference="date1/-sometext-/date2"
	 * 
	 * the field it references to needs to be specified in the xml file as follows:
	 * 
	 * e.g.	id="date1"
	 */
	private String getReferencedFieldValue(Field field) throws Exception
	{
		// the reference can be made to multiple fields. to achieve this,
		// devide the references by a slash character (/).
		String [] references = field.getReference().split("/");
		StringBuffer referenceValueBuffer= new StringBuffer();
		// for each of the references, we try to get the value of the referenced
		// field
		for(int f=0;f<references.length;f++)
		{
			Field referencedField = referencedFields.get(references[f]);
			// if the reference does not exist, we take the name (value) of the reference
			// as the value for the field referencing the other field. this allows to define
			// a constant value that is in turn used as a devider.
			if(referencedField!=null)
			{
				// if the reference exists but the value and has not been generated yet
				if(referencedField.getValueGenerated()==false)
				{
					generateFieldValue(referencedField);
					referencedField.setValueGenerated(true);
				}
				// if the field is a datetime type field, we get the value from the
				// referenced field and apply the requested pattern to it. this way the
				// date basis the referenced field and this field are using is the same.
				if(referencedField.getType()== Field.TYPE_DATETIME && field.getType()!= Field.TYPE_REFERENCE) 
				{
					field.setDateTimeMilliseconds(referencedField.getDateTimeMilliseconds());
					if(field.getPattern()==null)
					{
						field.setPattern(referencedField.getPattern());
					}
					referenceValueBuffer.append(field.formatDateTimeValue());
				}
				else
				{
					referenceValueBuffer.append(referencedField.getValue());
				}
			}
			else
			{
				referenceValueBuffer.append(references[f]);
			}
		}
		// set the value of the field as generated above. if the reference is not existing
		// an empty string will be returned as the value
		return referenceValueBuffer.toString();
		
	}
	
	/**
	 * the field passed to this method is a regular field not referencing
	 * another field.
	 * based on the type of field the value is generated
	 * 
	 */
	private void generateFieldValue(Field field) throws Exception
	{
		// if the category is not null, meaning it was specified in the xml file,
		// we get a random word from that category file that is specified in the
		// field tag
		if(field.getType()== Field.TYPE_CATEGORY)
		{
			// get the applicable category
			Category category = (Category)collection.get(field.getCategory());
			// if the category does not exist, try to load it
			if(category==null)
			{
				String path = checkTrailingSlash(categoryFilesFolder);
				String filename = field.getCategory() + CATEGORY_FILE_EXTENSION; 
				try
				{
					File file = new File(path + filename);
					// load category data
					loadCategoryFile(file);
					category = (Category)collection.get(field.getCategory());
					// generate how often the category will be used
					//generateCategoryUsage(row, category);
				}
				catch(FileNotFoundException ex)
				{
					throw new Exception ("category file not found: " + filename);
				}
				catch (Exception ex)
				{
					throw new Exception ("error loading file: " + filename);
				}
			}
			
			// get a random word from that category
			field.setValue(category.getRandomWord());
		}
		else if(field.getType()== Field.TYPE_REGEX)
		{
			field.generateRegularExpressionValue();
		}
		else if(field.getType()== Field.TYPE_RANDOM) 
		{
			// generate random value
			field.generateRandomValue();
		}
		else if(field.getType() == Field.TYPE_REFERENCE || (field.getReferenceField()==true && field.getReference()!=null)) 
		{
			// generate random value
			field.setValue(getReferencedFieldValue(field));
		}
		else if(field.getType()== Field.TYPE_DATETIME )
		{
			if(field.getReference()==null)
			{
				// generate random value
				field.generateDateTimeValue(minMilliSeconds,maxMilliSeconds);
				
			}
			field.setValue(field.formatDateTimeValue());
		}
		
		else // if no type was specified for the field in the xml file we genrate a exception
		{
			throw new Exception("undefined type of field: " + field.getType());
		}
	}		
		
	
	
	/**
	 * output can be in mixed, lowercase only or uppercase only format
	 * 
	 */
	private String format(String value)
	{
		if(dataFormat==1)
		{
			return value.toLowerCase();
		}
		else if (dataFormat==2)
		{
			return value.toUpperCase();
		}
		else
		{
			return value;
		}
	}
	
	/**
	 * method to load the words of a certain category based on the filename.
	 * one value per row. One row can contain multiple words seperated by
	 * spaces. Blank lines are ignored. Lines starting with a hash sign (#)
	 * are ignored.
	 * 
	 * example:
	 * 
	 * apple
	 * orange
	 * banana
	 * big coconut
	 * 
	 */
	public void loadCategoryFile(File file) throws Exception
	{
		int posExtension = file.getName().lastIndexOf(CATEGORY_FILE_EXTENSION);
		Category cat = new Category(file.getName().substring(0,posExtension));
		
	    BufferedReader reader = new BufferedReader(new FileReader(file.getPath()));
	    String line;
	    while ((line=reader.readLine())!=null)
	    {
	    	if(line!=null && line.trim().length()>0 && !line.trim().startsWith("#"))
	    	{
	    		cat.addWord(line.trim());
	    	}
	    }
	    reader.close();
	    collection.add(cat);
	}
	
	public void generateCategoryUsage(Row row, Category category)
	{
		for(int i=0;i<row.getFields().size();i++)
		{
			Field field = (Field)row.getFields().get(i);
			if(field.getType()==Field.TYPE_CATEGORY)
			{
				if(field.getCategory().equals(category.getType()))
				{
					category.setNumberOfUsage(category.getNumberOfUsage()+1);
				}
			}
		}
	}

	
	/**
	 *	parses the arguments that where passed to this program 
	 */
	private void parseArguments(String args[]) throws Exception
	{
		
		for(int i=0;i<args.length;i++)
		{
			if (args[i].startsWith("-n"))
			{
				numberOfOutputLines = Long.parseLong(args[i].substring(3));
			}
			else if (args[i].startsWith("-c"))
			{
				categoryFilesFolder = args[i].substring(3);
			}
			else if (args[i].startsWith("-m"))
			{
				maximumYear = Integer.parseInt(args[i].substring(3));
			}
			else if (args[i].startsWith("-i"))
			{
				minimumYear = Integer.parseInt(args[i].substring(3));
			}
			else if (args[i].startsWith("-l"))
			{
				rowlayoutFile = args[i].substring(3);
			}
			else if (args[i].startsWith("-o"))
			{
				outputfile = args[i].substring(3);
			}
			else if (args[i].equals("-v"))
			{
				verbose = true;
			}
			else if(args[i].startsWith("-f"))
			{
				dataFormat = Integer.parseInt(args[i].substring(3));
			}
			else if(args[i].startsWith("-p"))
			{
				Field.setPossibleCharacters(args[i].substring(3));
			}
			else if(args[i].startsWith("-e"))
			{
				processedLinesOutputInterval = Long.parseLong(args[i].substring(3));
			}
			
		}
		
		if(categoryFilesFolder==null)
		{
			throw new Exception("argument [-c] (category files folder) must be specified as an argument or in the properties file");
		}
		if(rowlayoutFile==null)
		{
			throw new Exception("argument [-l] (row layout file) must be specified as an argument or in the properties file");
		}

	}
	
	/**
     * load the properties from the file datagenerator.properties
     * 
     * a check is done if the file exists. in case the file does not exist, nothing
     * is done.
     */
    private void loadProperties(String filename) throws Exception
	{
		//System.out.println("loading properties: " + filename);
    	File f = new File(filename);
		if(f.exists() && f.isFile())
		{
	    	InputStream inputStream = DataCreator.class.getResourceAsStream("/" + PROPERTIES_FILE);
			
			Properties props = new Properties();
			props.load(inputStream);
			
			numberOfOutputLines = Long.parseLong(props.getProperty(PROPERTY_NUMBER_OF_OUTPUT_LINES));
			categoryFilesFolder = props.getProperty(PROPERTY_CATEGORY_FILES_FOLDER);
			rowlayoutFile = props.getProperty(PROPERTY_ROW_LAYOUT_FILE);
			outputfile = props.getProperty(PROPERTY_OUTPUTFILE);
			if(props.getProperty(PROPERTY_MAXIMUMYEAR)!=null)
			{
				maximumYear = Integer.parseInt(props.getProperty(PROPERTY_MAXIMUMYEAR));
			}
			if(props.getProperty(PROPERTY_MINIMUMYEAR)!=null)
			{
				minimumYear = Integer.parseInt(props.getProperty(PROPERTY_MINIMUMYEAR));
			}
			if(props.getProperty(PROPERTY_DATAFORMAT)!=null)
			{
				dataFormat = Integer.parseInt(props.getProperty(PROPERTY_DATAFORMAT));
			}
			if(props.getProperty(PROPERTY_VERBOSE)!=null)
			{
				verbose = Boolean.parseBoolean(props.getProperty(PROPERTY_VERBOSE));
			}
		    if(props.getProperty(PROPERTY_POSSIBLE_CHARACTERS)!=null)
		    {
		    	Field.setPossibleCharacters(props.getProperty(PROPERTY_POSSIBLE_CHARACTERS));
		    }
			if(categoryFilesFolder==null)
			{
				throw new Exception("[categoryfilesfolder] unspecified in " + PROPERTIES_FILE);
			}
			if(rowlayoutFile==null)
			{
				throw new Exception("[rowlayoutfile] unspecified in " + PROPERTIES_FILE);
			}
		}
		
	}
    
    /**
     * check if a given string ends with a trailing slash or backslash.
     * if not, then a trailing forward slash is added otherwise the
     * string is not changed. 
     */
    private static String checkTrailingSlash(String path)
    {
    	if(path.endsWith("/")|| path.endsWith("\\"))
    	{
    		return path;
    	}
    	else
    	{
    		return path + "/";
    	}
    }

    public String getOutputfile() 
    {
		return outputfile;
	}

	public void setOutputfile(String outputfile) 
	{
		this.outputfile = outputfile;
	}

	public long getNumberOfOutputLines() 
	{
		return numberOfOutputLines;
	}

	public void setNumberOfOutputLines(long numberOfOutputLines) 
	{
		this.numberOfOutputLines = numberOfOutputLines;
	}

	public int getDataFormat() 
	{
		return dataFormat;
	}

	public void setDataFormat(int dataFormat) 
	{
		this.dataFormat = dataFormat;
	}

	public long getProcessedLinesOutputInterval() 
	{
		return processedLinesOutputInterval;
	}

	public void setProcessedLinesOutputInterval(long processedLinesOutputInterval) 
	{
		this.processedLinesOutputInterval = processedLinesOutputInterval;
	}

	public int getMaximumYear() 
	{
		return maximumYear;
	}

	public void setMaximumYear(int maximumYear) 
	{
		this.maximumYear = maximumYear;
	}

	public int getMinimumYear() 
	{
		return minimumYear;
	}

	public void setMinimumYear(int minimumYear) 
	{
		this.minimumYear = minimumYear;
	}

	public String getCategoryFilesFolder() 
	{
		return categoryFilesFolder;
	}

	public void setCategoryFilesFolder(String categoryFilesFolder) 
	{
		this.categoryFilesFolder = categoryFilesFolder;
	}

	public String getRowlayoutFile() 
	{
		return rowlayoutFile;
	}

	public void setRowlayoutFile(String rowlayoutFile) 
	{
		this.rowlayoutFile = rowlayoutFile;
	}

	/**
	 * displayed in case no argument was specified or when either -h or --help
	 * was passed as an argument to the program.
	 *
	 */
	private static void help()
	{
		System.out.println("DataCreator. Program to generate rows of ASCII data, based on word categories in external files, ");
		System.out.println("a regular expression pattern or randomly generated data.");
		System.out.println();
		System.out.println("reference fields may be specified and fields can be constructed by concatenating reference fields.");
		System.out.println("this helps to define more flexible combinations of values. additionally this way datetime fields may be");
		System.out.println("specified which are based on the same datetime value, thus giving correct/real datetime values across columns.");
		System.out.println();
		System.out.println("usage: java com.datamelt.datagenerator.DataCreator -c=[categories folder] -l=[row layout file] -o=[output file] -n=[number of rows] -f=[dataformat] -p=[possible characters] -m=[maximum year] -i=[minimum year] -v");
		System.out.println("where: [categories folder] = the path to the folder where the category files area located");
		System.out.println("       [row layout file]   = name and path to the file that defines the layout of the rows to be genereted");
		System.out.println("       [output file]       = optional. name and path of the output file. if none specified, output goes to console");
		System.out.println("       [number of rows]    = number of rows to be generated");
		System.out.println("       [dataformat]        = optional. 0, 1 or 2. 0 (default) means output in regular case, 1 means output all lowercase, 2 means output all uppercase");
		System.out.println("       [possible values]   = optional. Specifies - for randomly generated values - the character set to be used");
		System.out.println("       [maximum year]      = optional. Specifies the maximum randomly generated year value");
		System.out.println("       [minimum year]      = optional. Specifies the minimum randomly generated year value");
		System.out.println("       -v                  = optional. verbose, create some output during processing");
		System.out.println("example: java com.datamelt.datagenerator.DataCreator -c=/home/dummy/categories -l=/home/dummy/rowlayout.xml -n=22500 -o=/home/dummy/output.txt -m=2020 -i=2007");
		System.out.println("         java com.datamelt.datagenerator.DataCreator -c=/home/dummy/categories -l=/home/dummy/rowlayout.xml -n=22500 -f=1 -v");
		System.out.println("         java com.datamelt.datagenerator.DataCreator -c=/home/dummy/categories -l=/home/dummy/rowlayout.xml -n=22500 -p=ABCDEFGHIJabcdefghij+*öäàé");
		System.out.println();
		System.out.println("copyright 2007-2016, uwe geercken - uwe.geercken@web.de");
		
	}
}
