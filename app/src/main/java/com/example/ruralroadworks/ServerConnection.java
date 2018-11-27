package com.example.ruralroadworks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.content.Context;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Button;

public class ServerConnection 
{
    /** Called when the activity is first created. */
	WebView webcontent;
	
	/** Create Object For SiteList Class */
	SitesList sitesList = null;
	public static int arraySize;
	public static boolean scheme_fix = false;
	Button search;
	String finalUrl ;
	Context context;
	
	/** Variables */
	public static ArrayList<byte[]> xml_emp_photo = new ArrayList<byte[]>();
	public static ArrayList<String> xml_emp_name = new ArrayList<String>();
	public static ArrayList<String> xml_emp_designation = new ArrayList<String>();
	public static ArrayList<String> xml_pendingWork = new ArrayList<String>();
	public static ArrayList<String> xml_emp_last_visit_date = new ArrayList<String>();
	public static ArrayList<String> xml_serviceProvider = new ArrayList<String>();
	public static ArrayList<String> work_id = new ArrayList<String>();
	public static ArrayList<String> scheme = new ArrayList<String>();
	public static ArrayList<String> financialyear = new ArrayList<String>();
	public static ArrayList<String> agencyname = new ArrayList<String>();
	public static ArrayList<String> workname = new ArrayList<String>();
	public static ArrayList<String> block = new ArrayList<String>();
	public static ArrayList<String> village = new ArrayList<String>();
	public static ArrayList<String> data = new ArrayList<String>();
	
	public ServerConnection()
	{
		
	}	
	
    public void constructFinalURL(String currenturl,String username,String password)
    {	
    	finalUrl = currenturl+"?n="+username+"&p="+password ;
    	Log.i("Url", finalUrl);
    }
	public boolean makeRequest() throws ClientProtocolException, IOException
	{
		if(finalUrl == null || finalUrl == "")
		{ 
			return false;
		}
		
		try
		{
			xml_emp_photo.clear();
			xml_emp_name.clear();
			xml_emp_designation.clear();
			xml_pendingWork.clear();
			xml_emp_last_visit_date.clear();
			xml_serviceProvider.clear();
			work_id.clear();
			scheme.clear();
			financialyear.clear();
			agencyname.clear();
			workname.clear();
			block.clear();
			village.clear();
			data.clear();
			
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			XMLReader xr = sp.getXMLReader();
			URL sourceUrl = new URL(finalUrl);			
			String s = "";
		    String line = "";
		    BufferedReader rd = new BufferedReader(new InputStreamReader(sourceUrl.openStream())); 
			while ((line = rd.readLine()) != null) {
				s += line;
			}
		    Log.i("Debug", "Response >>>> "+s);
			MyXMLHandler myXMLHandler = new MyXMLHandler();
			xr.setContentHandler(myXMLHandler);
			xr.parse(new InputSource(sourceUrl.openStream()));

		}
		catch (Exception e) 
		{
			Log.i("Debug", "Connection Exception :: "+e);
			e.printStackTrace();
			return false;
		}
		
		/** Get result from MyXMLHandler SitlesList Object */
		sitesList = MyXMLHandler.sitesList;
		
		if(sitesList != null)
		{			
			for (int i = 0; i < sitesList.getPhoto().size(); i++) 
			{
				xml_emp_photo.add (LoadImageFromWebOperations(sitesList.getPhoto().get(i)));	
			}
			
			for (int i = 0; i < sitesList.getServiceProvider().size(); i++) 
			{
				xml_emp_name.add (sitesList.getEmpName().get(i));
				xml_emp_designation.add (sitesList.getEmpDesignation().get(i));
				xml_emp_last_visit_date.add (sitesList.getLastVisitDate().get(i));
				xml_serviceProvider.add (sitesList.getServiceProvider().get(i));	
			}
			for (int i = 0; i < sitesList.getPendingWork().size(); i++) 
			{			
				xml_pendingWork.add (sitesList.getPendingWork().get(i));
			}
			
			for(int i = 0;i<sitesList.getWorkId().size();i++)
			{
				work_id.add(sitesList.getWorkId().get(i));
				scheme.add(sitesList.getScheme().get(i));
				financialyear.add(sitesList.getFinancialyear().get(i));
				agencyname.add(sitesList.getAgencyName().get(i));
				workname.add(sitesList.getWorkName().get(i));
				block.add(sitesList.getBlock().get(i));
				village.add(sitesList.getVillage().get(i));
			}
			for(int i = 0;i<sitesList.getData().size();i++)
			{
				data.add(sitesList.getData().get(i));
			}		
		}
		else
		{
			return false;
		}
		
		return true;
	}
	private byte[] LoadImageFromWebOperations(String url) throws ClientProtocolException, IOException 
    {
		try
		{
			DefaultHttpClient mHttpClient = new DefaultHttpClient();
			HttpGet mHttpGet = new HttpGet(url);
			HttpResponse mHttpResponse = mHttpClient.execute(mHttpGet);
			
			if (mHttpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) 
			{
				HttpEntity entity = mHttpResponse.getEntity();
			    if ( entity != null)
			    {			    	
					return EntityUtils.toByteArray(entity);	
			    }
			}
		}
		catch (Exception e) 
    	{
    		System.out.println("Exc="+e);
    		return null;
    	}
		return null;
			
	}
	
}