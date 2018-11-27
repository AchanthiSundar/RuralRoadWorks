
package com.example.ruralroadworks;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
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
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rd.DB.DatabaseHelper;
import com.example.rd.DB.PlaceDataSQL;


public class UploadPendingWorks extends Activity implements OnClickListener 
{
	/** Called when the activity is first created. */

	//progress bar id and declaration
	private ProgressDialog mProgressDialog;
	public static final int DIALOG_DOWNLOAD_PROGRESS = 0;

	String res;
	byte[] data;
	DatabaseHelper dbHelper;
	HttpPost httppost;
	StringBuffer buffer;
	HttpResponse response;
	HttpClient httpclient;
	InputStream inputStream;
	Button save,upload;
	SharedPreferences app_preferences;
	List<NameValuePair> nameValuePairs;
	boolean host = false;

	/* Location Manager for GPS */
	LocationManager mlocManager=null;
	LocationListener mlocListener;

	AlertDialog.Builder alert;
	static String latTextValue = "";
	static String lanTextValue = "";
	EditText et_gpslat, et_gpslan;
	ImageView taken_photo,home, back;
	TextView chainAge, stageGroup, stage, qualityParam, qualityParamValue, remark, workID, service_provided;

	//Used to check internet Connection..
	ConnectivityManager cm;
	NetworkInfo networkInfo;
	
	String userName;

	ArrayList<Double> lat = new ArrayList<Double>();
	ArrayList<Double> lan = new ArrayList<Double>();
	
	
	private static PlaceDataSQL placeData;

	void showToast(CharSequence msg)
	{
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	int count = 0;

	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.workidspin1);
		placeData = new PlaceDataSQL(this);    
		SQLiteDatabase db = placeData.getWritableDatabase();

		Cursor cursor = getRawEvents("select * from details");
		while (cursor.moveToNext())
		{
			String servicePro = cursor.getString(5);
			service_provided = (TextView)findViewById(R.id.footertxt);
			service_provided.setText(servicePro);
			System.out.println("SR "+servicePro);
		}
		cursor.close();
		
		Cursor cursor1 = getRawEvents("SELECT mdn FROM authentication");
		while (cursor1.moveToNext()){
			userName = cursor1.getString(0);
		}
		cursor1.close();	
		db.close();

		save = (Button)findViewById(R.id.but_workidspin_save);
		save.setOnClickListener(this);

		chainAge = (TextView)findViewById(R.id.tv_workidspin_chainage);
		chainAge.setText(""+PendingWorkDetailSpinner1.SpinnerTextValue.toString());
		chainAge.setOnClickListener(this);

		stageGroup = (TextView)findViewById(R.id.tv_workidspin_stageGroup);
		stageGroup.setText(""+PendingWorkDetailSpinner1.SpinnerTextValue1.toString());
		stageGroup.setOnClickListener(this);

		stage = (TextView)findViewById(R.id.tv_workidspin_stage);
		stage.setText(""+PendingWorkDetailSpinner1.SpinnerTextValue2.toString());
		stage.setOnClickListener(this);

		qualityParam = (TextView)findViewById(R.id.tv_workidspin_qualityParamater);
		qualityParam.setText(""+PendingWorkDetailSpinner1.SpinnerTextValue3.toString());
		qualityParam.setOnClickListener(this);

		qualityParamValue = (TextView)findViewById(R.id.tv_workidspin_qualityParamaterValue);
		qualityParamValue.setText(""+PendingWorkDetailSpinner1.SpinnerTextValue4.toString());
		qualityParamValue.setOnClickListener(this);

		remark = (TextView)findViewById(R.id.tv_workidspin_ramark);
		String str_remark = PendingWorkDetailSpinner1.remarksTextValue.toString();
		if(str_remark.equals("")||str_remark.equals(null)){
			remark.setVisibility(View.GONE);
		}
		remark.setText(""+str_remark);
		remark.setOnClickListener(this);

		workID = (TextView)findViewById(R.id.tv_workidspin_workId);
		workID.setText("WorkID :"+PendingWorkDetail.WorkidText.toString());
		workID.setOnClickListener(this);

		taken_photo = (ImageView)findViewById(R.id.taken_photo);
		Bitmap bm = BitmapFactory.decodeByteArray(PendingWorkDetailSpinner1.getCameraImage(), 0, PendingWorkDetailSpinner1.getCameraImage().length);
		taken_photo.setImageBitmap(bm);

		dbHelper=new DatabaseHelper(this);

		initialize();
		et_gpslat=(EditText)findViewById(R.id.editText1);
		et_gpslan=(EditText)findViewById(R.id.editText2);

		alert  = new AlertDialog.Builder(this);
		alert.setCancelable(true);

		Button toastnotify = (Button) findViewById(R.id.but_workidspin_upload);
		toastnotify.setOnClickListener(this);

		mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		mlocListener = new MyLocationListener();	        
		mlocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
		mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		{ 	
			Toast.makeText(UploadPendingWorks.this, "Photo Captured Successfully", Toast.LENGTH_LONG).show();
			mlocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, mlocListener);

			if (mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) 
			{
				if(MyLocationListener.latitude>0)
				{
					et_gpslat.setText(""+MyLocationListener.latitude);
					latTextValue = et_gpslat.getText().toString();

					et_gpslan.setText(""+ MyLocationListener.longitude);
					lanTextValue = et_gpslan.getText().toString(); 
				}
				else
				{
					alert.setTitle("GPS");
					alert.setMessage("GPS activation in progress,\n Please press back button to\n retake the photo");
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

	private Cursor getRawEvents(String sql) 
	{
		SQLiteDatabase db = (placeData).getReadableDatabase();
		Cursor cursor = db.rawQuery(sql, null);

		startManagingCursor(cursor);
		return cursor;
	}

	public String convertResponseToString(HttpResponse response) throws IllegalStateException, IOException{

		String res = "";
		StringBuffer buffer = new StringBuffer();
		inputStream = response.getEntity().getContent();
		int contentLength = (int) response.getEntity().getContentLength(); //getting content length�..
		if (contentLength < 0){
		}
		else{
			byte[] data = new byte[512];
			int len = 0;
			try
			{
				while (-1 != (len = inputStream.read(data)) )
				{
					buffer.append(new String(data, 0, len)); //converting to string and appending  to stringbuffer�..
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			try
			{
				inputStream.close(); // closing the stream�..
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			res = buffer.toString();     // converting stringbuffer to string�..

		}
		return res;
	}
	/*
	static ArrayList<String> workid = new ArrayList<String>();
	static ArrayList<String> spin = new ArrayList<String>();
	static ArrayList<String> spin1 = new ArrayList<String>();
	static ArrayList<String> spin2 = new ArrayList<String>();
	static ArrayList<String> spin3 = new ArrayList<String>();
	static ArrayList<String> spin4 = new ArrayList<String>();
	static ArrayList<String> remarks = new ArrayList<String>();
	static ArrayList<byte[]> image = new ArrayList<byte[]>();
	static String[] spinArray;

	private void getDataAndPopulate() 
	{
		workid = new ArrayList<String>();	     
		spin = new ArrayList<String>();
		spin1 = new ArrayList<String>();
		spin2 = new ArrayList<String>();
		spin3 = new ArrayList<String>();
		spin4 = new ArrayList<String>();
		remarks = new ArrayList<String>();	     
		image = new ArrayList<byte[]>();
		Cursor cursor = getEvents(DatabaseHelper.pendingTable);

		while (cursor.moveToNext()) 
		{
			String temp_id = cursor.getString(1);	   
			String temp_spin = cursor.getString(2);
			String temp_spin1 = cursor.getString(3);
			String temp_spin2 = cursor.getString(4);
			String temp_spin3 = cursor.getString(5);
			String temp_spin4 = cursor.getString(6);
			String temp_remarks = cursor.getString(7);
			byte[] temp_image = cursor.getBlob(8);
			Log.i("Debug", "IMGAge ::1::: "+cursor.getColumnName(0));
			Log.i("Debug", "IMGAge ::2::: "+cursor.getColumnName(1));
			Log.i("Debug", "IMGAge ::3::: "+cursor.getColumnName(2));
			Log.i("Debug", "IMGAge ::4::: "+cursor.getColumnName(3));
			Log.i("Debug", "IMGAge ::5::: "+cursor.getColumnName(4));
			Log.i("Debug", "IMGAge ::6::: "+cursor.getColumnName(5));
			Log.i("Debug", "IMGAge ::7::: "+cursor.getColumnName(6));
			Log.i("Debug", "IMGAge :::8:: "+cursor.getColumnName(7));
			Log.i("Debug", "IMGAge :::811:: "+cursor.getColumnName(8));


			workid.add(temp_id);
			spin.add(temp_spin);
			spin1.add(temp_spin1);
			spin2.add(temp_spin2);
			spin3.add(temp_spin3);
			spin4.add(temp_spin4);
			remarks.add(temp_remarks);
			image.add(temp_image);
		}
		spinArray = (String[]) spin.toArray(new String[spin.size()]);
		Log.i("Debug", "Spin array "+spinArray.length);
		startActivity(new Intent(UploadPendingWorks.this, DashBoard.class));

	}  
	private Cursor getEvents(String table) 
	{
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(table, null, null, null, null, null, null);
		startManagingCursor(cursor);
		//startManagingCursor(cursor);
		return cursor;
	}*/

	private void initialize()
	{
		home = (ImageView) findViewById(R.id.home_button);
		home.setOnClickListener(this); 
		back = (ImageView) findViewById(R.id.back_button);
		back.setOnClickListener(this);
	}

	public void onClick(View view) 
	{

		if (view.equals(home)){			
			Intent Pendingwork = new Intent(UploadPendingWorks.this,DashBoard.class);
			startActivity(Pendingwork);		
		}else if (view.equals(back)){			
			finish();
		}else if (view.equals(save)) {			
			try
			{
				dbHelper.AddPendingUploads(
						PendingWorkDetail.WorkidText, 
						PendingWorkDetailSpinner1.SpinnerTextValue, 
						PendingWorkDetailSpinner1.SpinnerTextValue1, 
						PendingWorkDetailSpinner1.SpinnerTextValue2, 
						PendingWorkDetailSpinner1.SpinnerTextValue3, 
						PendingWorkDetailSpinner1.SpinnerTextValue4,
						PendingWorkDetailSpinner1.remarksTextValue,
						latTextValue,lanTextValue,
						PendingWorkDetailSpinner1.getCameraImage()
						);

				Toast.makeText(UploadPendingWorks.this, "Data has been Saved Successfully..", Toast.LENGTH_LONG).show();
				Intent Pendingwork = new Intent(UploadPendingWorks.this,PendingWork.class);
				startActivity(Pendingwork);
				finish();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

		}else if (view == findViewById(R.id.but_workidspin_upload)){
			cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			networkInfo = cm.getActiveNetworkInfo();

			if (networkInfo != null && networkInfo.isConnected()) {
				if (!latTextValue.equals("") && !lanTextValue.equals("")) {
					UploadPhotoAsync task = new UploadPhotoAsync();
					task.execute();
				} else {
					Toast.makeText(
							UploadPendingWorks.this,
							"GPS is Not Activated..\nPlease activate GPS and Try Again..",
							Toast.LENGTH_LONG).show();
				}
			} else {
				AlertDialog.Builder ab = new AlertDialog.Builder(
						UploadPendingWorks.this);
				ab.setMessage(Html
						.fromHtml("<font color=#ffffff>Internet Connection is not avaliable...\n Please Save and Upload Later</font>"));
				ab.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {

							}
						});
				ab.show();
			}
		} else {
			Toast.makeText(
					this,
					"Uploading of data Failed due to non availability of Signal",
					Toast.LENGTH_SHORT).show();
		}
	}

	protected Dialog onCreateDialog(int id) 
	{
		switch (id) 
		{
		case DIALOG_DOWNLOAD_PROGRESS: 
			mProgressDialog = new ProgressDialog(this);
			mProgressDialog.setMessage("Please Wait..Your Data Is Getting Uploaded..");
			mProgressDialog.setIndeterminate(false);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mProgressDialog.setCancelable(false);
			mProgressDialog.show();
			return mProgressDialog;
		default:
			return null;
		}
	}

	private class UploadPhotoAsync extends AsyncTask<String, String, String> 
	{
		protected void onPreExecute()
		{
			super.onPreExecute();
			showDialog(DIALOG_DOWNLOAD_PROGRESS);
		}
		protected String doInBackground(String... urls) 
		{
			try {
				
				Bitmap bitmap = PendingWorkDetailSpinner1.getCameraBitmap();

				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream); //compress to which format you want.
				byte [] byte_arr = stream.toByteArray();
				String image_str = Base64.encodeBytes(byte_arr);
				
				HttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost;
				
				httpPost = new HttpPost("https://www.tnrd.gov.in/project/sample_photo.php");
				
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(12);
				nameValuePairs.add(new BasicNameValuePair("txtwkid", PendingWorkDetail.WorkidText.trim()));
				nameValuePairs.add(new BasicNameValuePair("txtchainage",PendingWorkDetailSpinner1.SpinnerTextValue.trim()));
				nameValuePairs.add(new BasicNameValuePair("lat", latTextValue));
				nameValuePairs.add(new BasicNameValuePair("lan",lanTextValue));
				nameValuePairs.add(new BasicNameValuePair("txtsgn", PendingWorkDetailSpinner1.SpinnerTextValue1.trim()));
				nameValuePairs.add(new BasicNameValuePair("txtsn",PendingWorkDetailSpinner1.SpinnerTextValue2.trim()));
				nameValuePairs.add(new BasicNameValuePair("txtqp", PendingWorkDetailSpinner1.SpinnerTextValue3.trim()));
				nameValuePairs.add(new BasicNameValuePair("txtqpv",PendingWorkDetailSpinner1.SpinnerTextValue4.trim()));
				nameValuePairs.add(new BasicNameValuePair("txtremarks", PendingWorkDetailSpinner1.remarksTextValue.trim()));
				nameValuePairs.add(new BasicNameValuePair("mode", "online".trim()));
				nameValuePairs.add(new BasicNameValuePair("image", image_str));
				nameValuePairs.add(new BasicNameValuePair("username", userName));

				
				try {
					httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				try {
					HttpResponse res1 = httpClient.execute(httpPost);
					HttpEntity entity = res1.getEntity();
					res = EntityUtils.toString(entity);
					
				} catch (UnknownHostException e) {
					Log.i("HOST", "Unknown");
					host = true;
				} catch (Exception e) {
					e.printStackTrace();
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
				if(res.equals("Saved"))
				{
					Toast.makeText(UploadPendingWorks.this, "Data Uploaded Successfully", Toast.LENGTH_LONG).show();

					startActivity(new Intent(UploadPendingWorks.this, DashBoard.class));
					finish();
				}
				if(res.equals("Failed"))
				{
					Toast.makeText(UploadPendingWorks.this, "Error In Data Upload", Toast.LENGTH_LONG).show();
				}

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
		}
		protected String doInBackground(String... urls) 
		{
			try
			{
				Bitmap bitmap = PendingWorkDetailSpinner1.getCameraBitmap();

				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream); //compress to which format you want.
				byte [] byte_arr = stream.toByteArray();
				String image_str = Base64.encodeBytes(byte_arr);

				HttpClient httpClient = new DefaultHttpClient();  
				HttpContext localContext = new BasicHttpContext();  

				// here, change it to your php;  
				//HttpPost httpPost = new HttpPost("http://tnrdweb1.tn.nic.in:8088/rdwebsite/project/sample_photo.php");
				HttpPost httpPost = new HttpPost("https://www.tnrd.gov.in/project/sample_photo.php");
				MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);  

				// sending a String param;      
				entity.addPart("txtwkid", new StringBody(PendingWorkDetail.WorkidText.trim())); 
				entity.addPart("txtchainage", new StringBody(PendingWorkDetailSpinner1.SpinnerTextValue.trim()));
				entity.addPart("lat", new StringBody(latTextValue));
				entity.addPart("lan", new StringBody(lanTextValue));
				entity.addPart("txtsgn", new StringBody(PendingWorkDetailSpinner1.SpinnerTextValue1.trim()));
				entity.addPart("txtsn", new StringBody(PendingWorkDetailSpinner1.SpinnerTextValue2.trim()));
				entity.addPart("txtqp", new StringBody(PendingWorkDetailSpinner1.SpinnerTextValue3.trim()));
				entity.addPart("txtqpv", new StringBody(PendingWorkDetailSpinner1.SpinnerTextValue4.trim()));
				entity.addPart("txtremarks", new StringBody(PendingWorkDetailSpinner1.remarksTextValue.trim()));
				entity.addPart("mode", new StringBody("online".trim()));

				entity.addPart("image", new StringBody(image_str));    		    		   
				//entity.addPart("myImage", new ByteArrayBody(byte_arr, "Sample.png"));  

				httpPost.setEntity(entity);  
				HttpResponse response = httpClient.execute(httpPost, localContext);  


				inputStream = response.getEntity().getContent();

			}
			catch(Exception e)
			{
				//Toast.makeText(upload.this, "error "+e.toString(), Toast.LENGTH_LONG).show();
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

				buffer = new StringBuffer();
				int len = 0;
				while (-1 != (len = inputStream.read(data)) )
				{
					buffer.append(new String(data, 0, len));
				}
				Log.i("Debug", "Response from upload :: "+buffer.toString()); 
				String res = buffer.toString().trim();

				if(res.equals("Saved"))
				{
					Toast.makeText(UploadPendingWorks.this, "Data Uploaded Successfully", Toast.LENGTH_LONG).show();

					startActivity(new Intent(UploadPendingWorks.this, DashBoard.class));
					finish();
				}
				if(res.equals("Failed"))
				{
					Toast.makeText(UploadPendingWorks.this, "Error In Data Upload", Toast.LENGTH_LONG).show();
				}

				inputStream.close();
			}
			catch(Exception e)
			{

			}
		} 
	}

}