package com.example.ruralroadworks;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class MyXMLHandler extends DefaultHandler {

	Boolean currentElement = false;
	String currentValue = null;
	public static SitesList sitesList = null;

	public static SitesList getSitesList() {
		return sitesList;
	}

	public static void setSitesList(SitesList sitesList) {
		MyXMLHandler.sitesList = sitesList;
	}

	/** Called when tag starts ( ex:- <name>AndroidPeople</name> 
	 * -- <name> )*/
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {

		currentElement = true;

		if (localName.equals("menu"))
		{
			/** Start */ 
			sitesList = new SitesList();		
		}
		else if (localName.equals("name")) {
			/** Get attribute value */
			String attr = attributes.getValue("designation");
			sitesList.setEmpDesignation(attr);
		}
		else if(localName.equals("data1"))
		{
			String id="";
			String value="";
			try
			{
				id = attributes.getValue("id");
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			try
			{
				value = attributes.getValue("value");
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			insertData1Value(id,value);
		}
		
		else if(localName.equals("data2"))
		{
			String id="";
			String sc="";
			String value="";
			try
			{
				id = attributes.getValue("id");
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			try
			{
				sc = attributes.getValue("sc");
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			try
			{
				value = attributes.getValue("value");
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			insertData2Value(id,sc,value);
		}

		else if(localName.equals("data3"))
		{
			String id="";
			String sc="";
			String value="";
			
			try
			{
				id = attributes.getValue("id");
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			try
			{
				sc = attributes.getValue("sc");
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			try
			{
				value = attributes.getValue("value");
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			insertData3Value(id,sc,value);
			
		}
	}

	/** Called when tag closing ( ex:- <name>AndroidPeople</name> 
	 * -- </name> )*/
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {

		currentElement = false;

		/** set value */ 
		if (localName.equalsIgnoreCase("name"))
			sitesList.setEmpName(currentValue);
		
		else if (localName.equalsIgnoreCase("provider"))
			sitesList.setServiceProvider(currentValue);
		
		else if (localName.equalsIgnoreCase("last_field_visitdate"))
			sitesList.setLastVisitDate(currentValue);
		
		else if (localName.equalsIgnoreCase("logo"))
			sitesList.setPhoto(currentValue);
		
		else if (localName.equalsIgnoreCase("id"))
			sitesList.setPendingWork(currentValue);
		
		
		
		else if (localName.equalsIgnoreCase("workid"))
			sitesList.setWorkId(currentValue);
		
		else if (localName.equalsIgnoreCase("scheme"))
			sitesList.setScheme(currentValue);
		
		else if (localName.equalsIgnoreCase("financialyear"))
			sitesList.setFinancialyear(currentValue);
		
		else if (localName.equalsIgnoreCase("agencyname"))
			sitesList.setAgencyName(currentValue);
		
		else if (localName.equalsIgnoreCase("workname"))
			sitesList.setWorkName(currentValue);
		
		else if (localName.equalsIgnoreCase("block"))
			sitesList.setBlock(currentValue);
		
		else if (localName.equalsIgnoreCase("village"))
			sitesList.setVillage(currentValue);
				
	}

	/** Called to get tag characters ( ex:- <name>AndroidPeople</name> 
	 * -- to get AndroidPeople Character ) */
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {

		if (currentElement) {
			currentValue = new String(ch, start, length);
			currentElement = false;
		}

	}

	private void insertData1Value(String id, String value)
	{
		SQLiteDatabase db = LoginScreen.placeData.getWritableDatabase();
		ContentValues values;
		values = new ContentValues();
		
		values.put("stageGroupId", id);
		values.put("stageGroupName", value);
		db.insert("stageGroup", null, values);
	}
	
	private void insertData2Value(String id,String sc, String value)
	{
		SQLiteDatabase db = LoginScreen.placeData.getWritableDatabase();
		ContentValues values;
		values = new ContentValues();

		values.put("stageGroupId", id);
		values.put("stageId", sc);
		values.put("stageName", value);
		db.insert("stage", null, values);
	}
	
	private void insertData3Value(String id,String sc, String value)
	{
		SQLiteDatabase db = LoginScreen.placeData.getWritableDatabase();
		ContentValues values;
		values = new ContentValues();

		values.put("stageGroupId", id);
		values.put("stageId", sc);
		values.put("qualityParams", value);
		db.insert("qualityParameters", null, values);
	}
}
