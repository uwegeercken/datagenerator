package com.datamelt.datagenerator.parser.xml;

import java.util.HashMap;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import com.datamelt.datagenerator.output.Field;
import com.datamelt.datagenerator.output.Row;

/**
 * class is used to parse an xml file containing the definition
 * of a row and its fields.<br />
 * 
 * @author uwe geercken - uwe.geercken@web.de
 */
public class Parser extends DefaultHandler implements ContentHandler
{
    private Row row;
   
    private boolean rowTagActive;
    private boolean fieldTagActive;
    private boolean referencesTagActive;
    
    private static final String TAG_ROW                   		= "row";
    private static final String TAG_REFERENCES             		= "references";
    private static final String ATTRIBUTE_ROW_TYPE        		= "type";
    private static final String ATTRIBUTE_ROW_FIELDSEPERATOR	= "seperator";
    private static final String TAG_FIELD			      		= "field";
    private static final String ATTRIBUTE_FIELD_TYPE    		= "type";
    private static final String ATTRIBUTE_FIELD_CATEGORY  		= "category";
    private static final String ATTRIBUTE_FIELD_LENGTH    		= "length";
    private static final String ATTRIBUTE_FIELD_PATTERN    		= "pattern";
    private static final String ATTRIBUTE_FIELD_ID		  		= "id";
    private static final String ATTRIBUTE_FIELD_REFERENCE_ID	= "reference";
    private static final String ATTRIBUTE_FIELD_OUTPUT  		= "output";
    
    public static final String TAG_ROW_TYPE_FIXED_LENGTH  		= "fixed";
    public static final String TAG_ROW_TYPE_DELIMITED     		= "delimited";
    
    public static final String ATTRIBUTE_FIELD_TYPE_CATEGORY    = "category";
    public static final String ATTRIBUTE_FIELD_TYPE_RANDOM      = "random";
    public static final String ATTRIBUTE_FIELD_TYPE_REGEX       = "regex";
    public static final String ATTRIBUTE_FIELD_TYPE_DATETIME    = "datetime";
    public static final String ATTRIBUTE_FIELD_TYPE_REFERENCE   = "reference";
    
    private HashMap <String,Field> referencedFields;
    
    /**
     * pass a filename of an xml row layout file to this method, which will
     * in turn be parsed using SAX. 
     */
    public void parse(String filename) throws Exception
    {
        SAXParserFactory factory = SAXParserFactory.newInstance();
	    SAXParser saxParser = factory.newSAXParser();
	    saxParser.parse(filename,this);
    }
    
    /**
     * when the start of an xml element is found, this method is called.
     * we set certain boolean values to know what tags are open and which
     * are not.<br />
     * we build up the field object here which will be used for further pro-
     * cessing.<br />
     */
    public void startElement( String namespaceURI, String localName, String qName, Attributes atts ) throws SAXException
	  {
		// new row object starts here
        if(qName.equals(TAG_ROW) && !rowTagActive)
        {
            rowTagActive=true;
            if(row==null)
            {
            	row = new Row();
            }
            if(atts.getValue(ATTRIBUTE_ROW_TYPE)!=null)
            {
            	if(atts.getValue(ATTRIBUTE_ROW_TYPE).equals(TAG_ROW_TYPE_FIXED_LENGTH))
            	{
            		row.setType(Row.TYPE_FIXED_LENGTH);
            	}
            	else if(atts.getValue(ATTRIBUTE_ROW_TYPE).equals(TAG_ROW_TYPE_DELIMITED))
            	{
            		row.setType(Row.TYPE_DELIMITED);
            		row.setSeperator(atts.getValue(ATTRIBUTE_ROW_FIELDSEPERATOR));
            	}
            }
            
        }
        
        if(qName.equals(TAG_REFERENCES) && !referencesTagActive)
        {
            if(row==null)
            {
            	row = new Row();
            }
        	referencedFields = new HashMap<String,Field>();
        	referencesTagActive=true;
        }
        
        // new field starts here
        else if(qName.equals(TAG_FIELD) && (rowTagActive || referencesTagActive)  && !fieldTagActive)
        {
            fieldTagActive=true;
            Field field = new Field();
            if(atts.getValue(ATTRIBUTE_FIELD_TYPE).equals(ATTRIBUTE_FIELD_TYPE_CATEGORY))
          	{
            	field.setType(Field.TYPE_CATEGORY);
            	field.setCategory(atts.getValue(ATTRIBUTE_FIELD_CATEGORY));
          	}
          	else if(atts.getValue(ATTRIBUTE_FIELD_TYPE).equals(ATTRIBUTE_FIELD_TYPE_RANDOM))
          	{
          		field.setType(Field.TYPE_RANDOM);
          	}
          	else if(atts.getValue(ATTRIBUTE_FIELD_TYPE).equals(ATTRIBUTE_FIELD_TYPE_REGEX))
          	{
          		field.setType(Field.TYPE_REGEX);
          		field.setPattern(atts.getValue(ATTRIBUTE_FIELD_PATTERN));
          	}
          	else if(atts.getValue(ATTRIBUTE_FIELD_TYPE).equals(ATTRIBUTE_FIELD_TYPE_DATETIME))
          	{
          		field.setType(Field.TYPE_DATETIME);
          		field.setPattern(atts.getValue(ATTRIBUTE_FIELD_PATTERN));
          	}
          	else if(atts.getValue(ATTRIBUTE_FIELD_TYPE).equals(ATTRIBUTE_FIELD_TYPE_REFERENCE))
          	{
          		field.setType(Field.TYPE_REFERENCE); 
          	}
            if(atts.getValue(ATTRIBUTE_FIELD_LENGTH)!=null)
            {
            	field.setLength(Integer.parseInt(atts.getValue(ATTRIBUTE_FIELD_LENGTH)));
            }
            if(atts.getValue(ATTRIBUTE_FIELD_OUTPUT)!=null)
            {
            	if(atts.getValue(ATTRIBUTE_FIELD_OUTPUT).toLowerCase().equals("false"))
            	{
            		field.setOutput(false);
            	}
            }
            if(atts.getValue(ATTRIBUTE_FIELD_REFERENCE_ID)!=null)
            {
            	field.setReference(atts.getValue(ATTRIBUTE_FIELD_REFERENCE_ID));
            }
            if(referencesTagActive && atts.getValue(ATTRIBUTE_FIELD_ID)==null)
            {
            	throw new SAXException("id attribute must be specified for reference fields");
            }
            // in this case we have a reference field. the field will be cached for re-use so
            // that other fields in the process may reference it.
            if(atts.getValue(ATTRIBUTE_FIELD_ID)!=null)
            {
            	Field cachedField = new Field();
            	cachedField.setReferenceField(true);
            	cachedField.setOutput(false);
				cachedField.setId(atts.getValue(ATTRIBUTE_FIELD_ID));
				cachedField.setType(field.getType());
				cachedField.setPattern(field.getPattern());
				cachedField.setLength(field.getLength());
				cachedField.setFillWithSpaces(field.isFillWithSpaces());
				cachedField.setOutput(field.getOutput());
				cachedField.setReference(field.getReference());
				cachedField.setCategory(field.getCategory());
				// put the field into a hashmap
            	referencedFields.put(cachedField.getId(), cachedField);
            }
            // if not a reference field then it will be added to the row object
            else
            {
            	row.addField(field);
            }
        }
        
	  }

    /**
     * when the end of an xml element is reached, this method is called.
     * we set certain boolean values to track what is open or not
     */
    public void endElement(String namespaceURI, String localName, String qName)
    {
        // end of row object
    	if(qName.equals(TAG_ROW))
        {
            rowTagActive=false;
        }
    	 // end of references object
    	if(qName.equals(TAG_REFERENCES))
        {
    		referencesTagActive=false;
        }
    	// end of field object
        else if(qName.equals(TAG_FIELD))
        {
            fieldTagActive=false;
        }
    }

    /**
     * returns the row that has been constructed by parsing
     * a xml row layout file 
     */
    public Row getRow()
    {
    	return row;
    }

    /**
     * returns the hashmap of reference fields that has been constructed by parsing
     * a xml row layout file 
     */
	public HashMap<String, Field> getReferencedFields()
	{
		return referencedFields;
	}

}
