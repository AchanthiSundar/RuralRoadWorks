package com.example.ruralroadworks;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rd.DB.DatabaseHelper;
import com.example.rd.DB.PlaceDataSQL;

public class PendingUpload extends ListActivity implements OnClickListener
{
	
	String xmlString;
	
	//progress bar id and declaration
	private ProgressDialog mProgressDialog;
	public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
	    	
	DatabaseHelper dbHelper;
	static String oflatTextValue = "";
	static String oflanTextValue = "";
	EditText ofet_gpslat, ofet_gpslan;
	LocationManager mlocManager=null;
	LocationListener mlocListener;
	AlertDialog.Builder alert;
	Button uploadButton;
	
	//Used to check internet Connection..
    ConnectivityManager cm;
    NetworkInfo networkInfo;
   
    byte[] data;
    HttpPost httppost;
    StringBuffer buffer;
    HttpResponse response;
    HttpClient httpclient;
    InputStream inputStream;
    List<NameValuePair> nameValuePairs;
    
    
    static final String KEY_CHAINAGE = "chainage";
	static final String KEY_STAGE_GROUP = "stage_group";
	static final String KEY_STAGE = "stage";
	static final String KEY_QUALITY_PARAMATER = "quality_paramater";
	static final String KEY_QUALITY_PARAMATER_VALUE = "quality_paramater_value";
	static final String KEY_REMARKS = "remarks";
	
	AlertDialog alertDialog; 
	private static PlaceDataSQL placeData;
	
	TextView service_provided;
	String userName;
	
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pending_upload);
        
        placeData = new PlaceDataSQL(this);    
        SQLiteDatabase db = placeData.getWritableDatabase();
        
        Cursor cursor = getRawEvents("SELECT * FROM details");
    	while (cursor.moveToNext()){
    		String servicePro = cursor.getString(5);
    		service_provided = (TextView)findViewById(R.id.footertxt);
    		service_provided.setText(servicePro);
 		}
    	cursor.close();
    	
    	Cursor cursor1 = getRawEvents("SELECT mdn FROM authentication");
		while (cursor1.moveToNext()){
			userName = cursor1.getString(0);
		}
		cursor1.close();
    	db.close();
    	
    	
        ItemsAdapter itemsAdapter = new ItemsAdapter(PendingUpload.this, R.layout.item,DashBoard.spinArray);
        setListAdapter(itemsAdapter);					

        ListView lv = getListView();
		lv.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) 
			{
				
				TextView tv_workID,
					tv_chainAge,
					tv_stageGroup,
					tv_stage,
					tv_qualityParamater,
					tv_qualityParamaterValue,
					tv_remarks;
				
				Button bt_upload;
				ImageView iv_photo;
				
				
				// getting values from selected ListItem
				String Workid = ((TextView)view.findViewById(R.id.workid)).getText().toString();
				String Chainage = ((TextView) view.findViewById(R.id.caption)).getText().toString();
				String stageGroup = ((TextView) view.findViewById(R.id.description)).getText().toString();
				String stage = ((TextView) view.findViewById(R.id.stage)).getText().toString();
				String qualityParamater = ((TextView) view.findViewById(R.id.qualityParamater)).getText().toString();
				String qualityParamaterValue = ((TextView) view.findViewById(R.id.qualityParamaterValue)).getText().toString();
				String remarks = ((TextView) view.findViewById(R.id.remarks)).getText().toString();
					
//				Log.i("Debug", "Workid :: "+Workid);
//				Log.i("Debug", "Chainage :: "+Chainage);
//				Log.i("Debug", "stageGroup :: "+stageGroup);
//				Log.i("Debug", "stage :: "+stage);
//				Log.i("Debug", "qualityParamater :: "+qualityParamater);
//				Log.i("Debug", "qualityParamaterValue :: "+qualityParamaterValue);
//				Log.i("Debug", "remarks :: "+remarks);
				
				
				AlertDialog.Builder builder;
				//AlertDialog alertDialog;

				LayoutInflater inflater = (LayoutInflater) PendingUpload.this.getSystemService(LAYOUT_INFLATER_SERVICE);
				View layout = inflater.inflate(R.layout.pending_uploads_preview, (ViewGroup) findViewById(R.id.root));
				
				tv_workID = (TextView)layout.findViewById(R.id.tv_pendingUploads_preview_workId);
				tv_chainAge = (TextView)layout.findViewById(R.id.tv_pendingUploads_preview__chainage);
				tv_stageGroup = (TextView)layout.findViewById(R.id.tv_pendingUploads_preview__stageGroup);
				tv_stage = (TextView)layout.findViewById(R.id.tv_pendingUploads_preview__stage);
				tv_qualityParamater = (TextView)layout.findViewById(R.id.tv_pendingUploads_preview__qualityParamater);
				tv_qualityParamaterValue = (TextView)layout.findViewById(R.id.tv_pendingUploads_preview_qualityParamaterValue);
				tv_remarks = (TextView)layout.findViewById(R.id.tv_pendingUploads_preview_ramark);
				
				bt_upload = (Button)layout.findViewById(R.id.but_pendingUploads_preview_upload);
				iv_photo = (ImageView)layout.findViewById(R.id.iv_pendingUploads_preview_taken_photo);
				
				iv_photo.setImageBitmap(BitmapFactory.decodeByteArray(DashBoard.image.get(position), 0, DashBoard.image.get(position).length));
				
				tv_workID.setText(Workid);
				tv_chainAge.setText(Chainage);
				tv_stageGroup.setText(stageGroup);
				tv_stage.setText(stage);
				tv_qualityParamater.setText(qualityParamater);
				tv_qualityParamaterValue.setText(qualityParamaterValue);
				tv_remarks.setText(remarks);
				
				bt_upload.setOnClickListener(new OnClickListener() 
				{					
					public void onClick(View v) 
					{
						alertDialog.dismiss();
					}
				});
				
				builder = new AlertDialog.Builder(PendingUpload.this);
				builder.setView(layout);
				alertDialog = builder.create();
				alertDialog.show();				
			}
		});
		
		
        uploadButton = (Button) findViewById(R.id.upload_db_data);
		uploadButton.setOnClickListener(this);
		
		dbHelper = new DatabaseHelper(this);
		ofet_gpslat=(EditText)findViewById(R.id.editText1);
	    ofet_gpslan=(EditText)findViewById(R.id.editText2);

	    alert  = new AlertDialog.Builder(this);
		alert.setCancelable(true);
		
		
    }
    
	private Cursor getRawEvents(String sql) {
		SQLiteDatabase db = (placeData).getReadableDatabase();
		Cursor cursor = db.rawQuery(sql, null);

		startManagingCursor(cursor);
		return cursor;
	}

    public void onClick(View v)
    {
    	Log.i("Debug", "onClick upload button :: ");
    	if(v==uploadButton)
    	{
    		cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		    networkInfo = cm.getActiveNetworkInfo();
		    
  	 		if (networkInfo != null && networkInfo.isConnected()) 
		    { 
  	 			mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
  	 	        mlocListener = new MyLocationListener();	        
  	 	        mlocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
  	 	        mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
  	 	 	    {   	
  	 	 			mlocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
  	 	 			        
  	 	 		if (mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) 
  	 	 			{
  	 	 				if(MyLocationListener.latitude>0)
  	 	 			    {
  	 	 					ofet_gpslat.setText(""+MyLocationListener.latitude);
	 	 			        oflatTextValue = ofet_gpslat.getText().toString();
	 	 			        
	 	 			       	ofet_gpslan.setText(""+ MyLocationListener.longitude);      	
	 	 			        oflanTextValue = ofet_gpslan.getText().toString();
	 	 			       
//	 	 			      oflatTextValue ="13.00103331";
//	 	 			      oflanTextValue = "80.27110324";
	 	 			        	 	 		  	 	 					
  	 	 					try
  	 	 					{ 	 	 						
  	 	 						UploadPhotoAsync task = new UploadPhotoAsync();
  	 	 						task.execute(); 						
  	 	 					}
  	 	 					catch(Exception e)
  	 	 					{
  	 	 						e.printStackTrace();
  	 	 					}
  	 	 			    }
  	 	 			    else
  	 	 			    {
  	 	 			       alert.setTitle("GPS");
  	 	 			       alert.setMessage("GPS activation in progress,\nPlease upload after getting Activated the GPS");
  	 	 			       alert.setPositiveButton("OK", null);
  	 	 			       alert.show();			
  	 	 			    }  	 			               
  	 	 			} 
  	 	 			else 
  	 	 			{
  	 	 			    alert.setTitle("GPS");
  	 	 				alert.setMessage("GPS is not turned on...");
  	 	 				alert.setPositiveButton("OK", null);
  	 	 				alert.show();
  	 	 			} 
  	 	 	    } 	 	 	      	 			
		    }
  	 		else
		    {
		    	AlertDialog.Builder ab = new AlertDialog.Builder(PendingUpload.this);
	   			ab.setMessage(Html.fromHtml("<font color=#ffffff>Internet Connection is not avaliable...\n Please Upload after Sometimen</font>"));
	   			ab.setPositiveButton("Ok", new DialogInterface.OnClickListener() 
	   			{
		            public void onClick(DialogInterface dialog, int whichButton) 
		            {				            	
		            	
		            }
		        });   
	   			ab.show();	
		    }
    	}
    	
    }
    
    private class UploadPhotoAsync extends AsyncTask<String, String, String> 
	{
		protected void onPreExecute()
		{
			super.onPreExecute();
			showDialog(DIALOG_DOWNLOAD_PROGRESS);
			//mProgressDialog.show();
		}
		protected String doInBackground(String... urls) 
		{
			try {
				int size = DashBoard.image.size();
   		 		for(int i=0;i<size;i++)
   		 		{
   		 			Bitmap bitmap  = BitmapFactory.decodeByteArray(DashBoard.image.get(i), 0, DashBoard.image.get(i).length);
   		 			ByteArrayOutputStream stream = new ByteArrayOutputStream();
   		 			bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream); //compress to which format you want.
   		 			byte [] byte_arr = stream.toByteArray();
   		 			String image_str = Base64.encodeBytes(byte_arr);
   		 			
   		 			
   		 			HttpClient httpClient = new DefaultHttpClient();
   		 			HttpContext localContext = new BasicHttpContext();  
   		 			HttpPost httpPost;
				
   		 			httpPost = new HttpPost("https://www.tnrd.gov.in/project/sample_photo.php");
				
   		 			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(12);
					nameValuePairs.add(new BasicNameValuePair("txtwkid", DashBoard.workid.get(i).trim()));
					nameValuePairs.add(new BasicNameValuePair("txtchainage",DashBoard.spin.get(i).trim()));
					nameValuePairs.add(new BasicNameValuePair("lat", DashBoard.lat.get(i).trim()));
					nameValuePairs.add(new BasicNameValuePair("lan",DashBoard.lan.get(i).trim()));
					nameValuePairs.add(new BasicNameValuePair("oflat", oflatTextValue.trim()));
					nameValuePairs.add(new BasicNameValuePair("oflan",oflanTextValue.trim()));
					nameValuePairs.add(new BasicNameValuePair("txtsgn", DashBoard.spin1.get(i).trim()));
					nameValuePairs.add(new BasicNameValuePair("txtsn",DashBoard.spin2.get(i).trim()));
					nameValuePairs.add(new BasicNameValuePair("txtqp", DashBoard.spin3.get(i).trim()));
					nameValuePairs.add(new BasicNameValuePair("txtqpv",DashBoard.spin4.get(i).trim()));
					nameValuePairs.add(new BasicNameValuePair("txtremarks", DashBoard.remarks.get(0).trim()));
					nameValuePairs.add(new BasicNameValuePair("mode", "offline".trim()));
					nameValuePairs.add(new BasicNameValuePair("image", image_str));
					nameValuePairs.add(new BasicNameValuePair("username", userName));
					
					httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));  
       		     	HttpResponse response = httpClient.execute(httpPost, localContext); 
       		        
       		     	inputStream = response.getEntity().getContent();
   	  	  	     	data = new byte[256];
   	  	  	     	buffer = new StringBuffer();
   	  	  	     	int len = 0;
   	  	  	         
   	  	  	     	while (-1 != (len = inputStream.read(data)) )
   	  	  	     	{
   	  	  	     		buffer.append(new String(data, 0, len));
   	  	  	     	}
   		 		}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onProgressUpdate(String... progress) 
		{
			Log.d("LOG_TAG",progress[0]);
			mProgressDialog.setProgress(Integer.parseInt(progress[0])); 	
		}
		protected void onPostExecute(String result) 
		{ 
			dismissDialog(DIALOG_DOWNLOAD_PROGRESS);
			try
       	 	{
       	 		data = new byte[256];
       	 		Log.i("Debug", "Response from upload :: "+buffer.toString()); 
    	     	String res = buffer.toString().trim();
    	           
    	     	if(res.equals("Saved"))
    	     	{
    	      	 	Toast.makeText(PendingUpload.this, "Data Uploaded Successfully", Toast.LENGTH_LONG).show();
    	    	 }
    	    	if(res.equals("Failed"))
    	    	{
    	     	  	 Toast.makeText(PendingUpload.this, "Error In Data Upload", Toast.LENGTH_LONG).show();
    	    	}
    	        
    	    	inputStream.close();
    	         
    	    	dbHelper.DeletePendingUploads();  
                 
             	startActivity(new Intent(PendingUpload.this, DashBoard.class));
             	finish();
       	 	}
       	 	catch(Exception e)
       	 	{
       			 e.printStackTrace();
       	 	}
		} 
	}
    
    private class DownloadWebPageTask extends AsyncTask<String, String, String> 
    {
    	protected void onPreExecute()
   	 	{
   	 		super.onPreExecute();
   	 		showDialog(DIALOG_DOWNLOAD_PROGRESS);
   	 		//mProgressDialog.show();
   	 	}
        protected String doInBackground(String... urls) 
        {
        	try
        	{
        		 int size = DashBoard.image.size();
        		 for(int i=0;i<size;i++)
                 {
        		   	 Bitmap bitmap  = BitmapFactory.decodeByteArray(DashBoard.image.get(i), 0, DashBoard.image.get(i).length);
            		 ByteArrayOutputStream stream = new ByteArrayOutputStream();
            		 bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream); //compress to which format you want.
            		 byte [] byte_arr = stream.toByteArray();
            		 String image_str = Base64.encodeBytes(byte_arr);
            		     
        		     HttpClient httpClient = new DefaultHttpClient();  
        		     HttpContext localContext = new BasicHttpContext();  
        		      
        		     //HttpPost httpPost = new HttpPost("http://tnrdweb1.tn.nic.in:8088/rdwebsite/project/sample_photo.php");
        		     HttpPost httpPost = new HttpPost("https://www.tnrd.gov.in/project/sample_photo.php");
        		     MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);  
        		      
        		     // sending a String param;      
        		     entity.addPart("txtwkid", new StringBody(DashBoard.workid.get(i).trim())); 
        		     entity.addPart("txtchainage", new StringBody( DashBoard.spin.get(i).trim()));
        		     entity.addPart("lat", new StringBody(DashBoard.lat.get(i).trim()));
        		     entity.addPart("lan", new StringBody(DashBoard.lan.get(i).trim()));
        		     entity.addPart("oflat", new StringBody( oflatTextValue.trim()));
        		     entity.addPart("oflan", new StringBody(oflanTextValue.trim()));
        		     entity.addPart("txtsgn", new StringBody(DashBoard.spin1.get(i).trim()));
        		     entity.addPart("txtsn", new StringBody(DashBoard.spin2.get(i).trim()));
        		     entity.addPart("txtqp", new StringBody(DashBoard.spin3.get(i).trim()));
        		     entity.addPart("txtqpv", new StringBody(DashBoard.spin4.get(i).trim()));
        		     entity.addPart("txtremarks", new StringBody(DashBoard.remarks.get(0).trim()));
        		     entity.addPart("mode", new StringBody("offline".trim()));
        		            
        		     entity.addPart("image", new StringBody(image_str));    		    		   
        		     //entity.addPart("myImage", new ByteArrayBody(byte_arr, "Sample.png"));  
        		         
        		     httpPost.setEntity(entity);  
        		     HttpResponse response = httpClient.execute(httpPost, localContext); 
        		        
        		     inputStream = response.getEntity().getContent();
    	  	  	     data = new byte[256];
    	  	  	     buffer = new StringBuffer();
    	  	  	     int len = 0;
    	  	  	         
    	  	  	     while (-1 != (len = inputStream.read(data)) )
    	  	  	     {
    	  	  	       	buffer.append(new String(data, 0, len));
    	  	  	     }
                 }
        	}
        	catch(Exception e)
        	{
        		e.printStackTrace();
        	}
        	return null;
        }

        protected void onProgressUpdate(String... progress) 
   	 	{
        	Log.d("LOG_TAG",progress[0]);
            mProgressDialog.setProgress(Integer.parseInt(progress[0])); 	
   	 	}
        
        protected void onPostExecute(String result) 
        { 
        	dismissDialog(DIALOG_DOWNLOAD_PROGRESS);
       	 	try
       	 	{
       	 		data = new byte[256];
       	 		Log.i("Debug", "Response from upload :: "+buffer.toString()); 
    	     	String res = buffer.toString().trim();
    	           
    	     	if(res.equals("Saved"))
    	     	{
    	      	 	Toast.makeText(PendingUpload.this, "Data Uploaded Successfully", Toast.LENGTH_LONG).show();
    	    	 }
    	    	if(res.equals("Failed"))
    	    	{
    	     	  	 Toast.makeText(PendingUpload.this, "Error In Data Upload", Toast.LENGTH_LONG).show();
    	    	}
    	        
    	    	inputStream.close();
    	         
    	    	dbHelper.DeletePendingUploads();  
                 
             	startActivity(new Intent(PendingUpload.this, DashBoard.class));
             	finish();
       	 	}
       	 	catch(Exception e)
       	 	{
       			 e.printStackTrace();
       	 	}
       } 
   }
    private class ItemsAdapter extends BaseAdapter {
		String[] items;

		public ItemsAdapter(Context context, int textViewResourceId,
				String[] items) {
			this.items = items;
		}

		public View getView(final int POSITION, View convertView,
				ViewGroup parent) {
			TextView Workid, Chainage, stageGroup, stage, qualityParamater, qualityParamaterValue, remarks;
			ImageView img;
			View view = convertView;

			if (view == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = vi.inflate(R.layout.item, null);
			}

			img = (ImageView) view.findViewById(R.id.image);

			Workid = (TextView) view.findViewById(R.id.workid);
			Chainage = (TextView) view.findViewById(R.id.caption);
			stageGroup = (TextView) view.findViewById(R.id.description);
			stage = (TextView) view.findViewById(R.id.stage);
			qualityParamater = (TextView) view
					.findViewById(R.id.qualityParamater);
			qualityParamaterValue = (TextView) view
					.findViewById(R.id.qualityParamaterValue);
			remarks = (TextView) view.findViewById(R.id.remarks);

			Workid.setText(DashBoard.workid.get(POSITION));
			Chainage.setText(DashBoard.spin.get(POSITION));
			stageGroup.setText(DashBoard.spin1.get(POSITION));
			stage.setText(DashBoard.spin2.get(POSITION));
			qualityParamater.setText(DashBoard.spin3.get(POSITION));
			qualityParamaterValue.setText(DashBoard.spin4.get(POSITION));
			remarks.setText(DashBoard.remarks.get(POSITION));

			// if(Pendingwork.image!=null)
			img.setImageBitmap(BitmapFactory.decodeByteArray(
					DashBoard.image.get(POSITION), 0,
					DashBoard.image.get(POSITION).length));

			return view;
		}

		public int getCount() {
			return items.length;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}
	}
    private void sendViaXml() 
    {
    	try 
        {    		
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("offline");
            doc.appendChild(rootElement);
		     
            int size = DashBoard.image.size();
            System.out.println("size "+size);
            
            for(int i=0;i<size;i++)
            {
            	Bitmap bitmap  = BitmapFactory.decodeByteArray(DashBoard.image.get(i), 0, DashBoard.image.get(i).length);
            	ByteArrayOutputStream stream = new ByteArrayOutputStream();
       		 	bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream); //compress to which format you want.
       		 	byte [] byte_arr = stream.toByteArray();
       		 	String image_str = Base64.encodeBytes(byte_arr);
       		 
            	// details elements
                Element detail = doc.createElement("details");
                rootElement.appendChild(detail);
                
            	// set attribute to details element
                Attr work_id = doc.createAttribute("workId");
                work_id.setValue(DashBoard.workid.get(i).trim());
                detail.setAttributeNode(work_id);

                // set chainage elements
                Element chainage = doc.createElement("chainage");
                chainage.appendChild(doc.createTextNode(DashBoard.spin.get(i).trim()));
                detail.appendChild(chainage);
                
                // latitude elements
                Element latitude = doc.createElement("lat");
                latitude.appendChild(doc.createTextNode(DashBoard.lat.get(i).trim()));
                detail.appendChild(latitude);

                // longitude elements
                Element longitude = doc.createElement("lan");
                longitude.appendChild(doc.createTextNode(DashBoard.lan.get(i).trim()));
                detail.appendChild(longitude);
                
                
                // offline latitude elements
                Element off_lat = doc.createElement("oflat");
                off_lat.appendChild(doc.createTextNode(oflatTextValue.trim()));
                detail.appendChild(off_lat);

                // offline longitude elements
                Element off_lan = doc.createElement("oflan");
                off_lan.appendChild(doc.createTextNode(oflanTextValue.trim()));
                detail.appendChild(off_lan);

                // stage group elements
                Element stageGroup = doc.createElement("stagegroup");
                stageGroup.appendChild(doc.createTextNode(DashBoard.spin1.get(i).trim()));
                detail.appendChild(stageGroup);

                // stage name elements
                Element stageName = doc.createElement("stagename");
                stageName.appendChild(doc.createTextNode(DashBoard.spin2.get(i).trim()));
                detail.appendChild(stageName);
                
                // quality Parameter elements
                Element qualityParameter = doc.createElement("quality");
                qualityParameter.appendChild(doc.createTextNode(DashBoard.spin3.get(i).trim()));
                detail.appendChild(qualityParameter);
                
                // quality Parameter value elements
                Element qlty_val = doc.createElement("qualityval");
                qlty_val.appendChild(doc.createTextNode(DashBoard.spin4.get(i).trim()));
                detail.appendChild(qlty_val);
                
                //remarks elements
                Element remark = doc.createElement("remark");
                remark.appendChild(doc.createTextNode(DashBoard.remarks.get(0).trim()));
                detail.appendChild(remark);
                
             	//image string elements
                Element image = doc.createElement("image");
                image.appendChild(doc.createTextNode(image_str));
                detail.appendChild(image);
            }
            

            TransformerFactory transfac = TransformerFactory.newInstance();
            Transformer trans = null;
            try 
            {
               trans = transfac.newTransformer();
            } 
            catch (TransformerConfigurationException e)
            {
               e.printStackTrace();
            }
            trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            trans.setOutputProperty(OutputKeys.INDENT, "yes");

            //create string from xml tree
            StringWriter sw = new StringWriter();
            StreamResult result = new StreamResult(sw);
            DOMSource source = new DOMSource(doc);
            try 
            {
               trans.transform(source, result);
            } 
            catch (TransformerException e) 
            {
               e.printStackTrace();
            }
            xmlString = sw.toString();
            
            System.out.println(xmlString);
            
        } 
        catch (ParserConfigurationException pce) 
        {
            pce.printStackTrace();
        }
    	
    	HttpClient httpClient = new DefaultHttpClient();  
      	HttpContext localContext = new BasicHttpContext();  

      	// here, change it to your php;  
      	HttpPost httpPost = new HttpPost("http://10.163.14.145/rdwebsite/project/sample_photo.php");  
 	 	MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);  

 	 	// sending a String param;      
 	 	try 
 	 	{
 	 		String s = "<?xml version='1.0'?>"+xmlString;
			entity.addPart("xml", new StringBody(s));
			entity.addPart("mode", new StringBody("offline".trim()));
			
			httpPost.setEntity(entity);  
	 	 	HttpResponse response = httpClient.execute(httpPost, localContext);  
	 	 	InputStream inputStream = response.getEntity().getContent();
	 	 	
	 	 	try
	    	 {
	    		 byte[] data = new byte[256];

	    		 StringBuffer buffer = new StringBuffer();
	 	         int len = 0;
	 	         while (-1 != (len = inputStream.read(data)) )
	 	         {
	 	           	buffer.append(new String(data, 0, len));
	 	         }
	 	         Log.i("Debug", "Response from upload :: "+buffer.toString()); 
	 	         String res = buffer.toString().trim();
	 	           
	 	         if(res.equals("Saved"))
	 	         {
	 	        	Toast.makeText(getApplicationContext(), "Data Uploaded Successfully", Toast.LENGTH_LONG).show();
	 	        	
	 	         }
	 	         if(res.equals("Failed"))
	 	         {
	 	        	 Toast.makeText(getApplicationContext(), "Error In Data Upload", Toast.LENGTH_LONG).show();
	 	         }
	 	        
	 	         inputStream.close();
	    	 }
	    	 catch(Exception e)
	    	 {
	    		 e.printStackTrace();
	    	 }
 	 	}
 	 	catch(Exception e)
 	 	{
 	 		e.printStackTrace();
 	 	}
	}
	
    protected Dialog onCreateDialog(int id) 
    {
         switch (id) 
         {
             case DIALOG_DOWNLOAD_PROGRESS: //we set this to 0
                 mProgressDialog = new ProgressDialog(this);
                 mProgressDialog.setMessage("Please Wait..Your Data Is Getting Uploaded..");
                 mProgressDialog.setIndeterminate(false);
                 //mProgressDialog.setMax(100);
                 mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                 mProgressDialog.setCancelable(true);
                 mProgressDialog.show();
                 return mProgressDialog;
             default:
                 return null;
         }
     }
    
    public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
	    if (keyCode == KeyEvent.KEYCODE_BACK) 
	    {
	    	Intent Pendingwork = new Intent(PendingUpload.this,DashBoard.class);
			startActivity(Pendingwork);
			finish();
	    }
	    return super.onKeyDown(keyCode, event);
	}
}