package com.datamelt.datagenerator;

public class Test1 {

	public static void main(String[] args) throws Exception
	{
	
		DataCreator dc = new DataCreator();
		
		dc.setCategoryFilesFolder("/home/uwe/development/git/datagenerator/categories/english");
		dc.setRowlayoutFile("/home/uwe/development/git/datagenerator/rowlayout.xml");
		dc.setNumberOfOutputLines(20);
		
		dc.generate();
		

	}

}
