package com.example.ruralroadworks;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;


import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.Manifest;
import android.R.string;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Base64;

import com.example.rd.DB.PlaceDataSQL;

public class LoginScreen extends Activity {

	String EncryptedUserName, EncryptedPasseord;
	//progress bar id and declaration
	ProgressBar pb;
	Dialog dialog1;
	int downloadedSize = 0;
	int totalSize = 0;
	TextView cur_val;
	TextView dialog_text;

	//Used to check internet Connection..
	ConnectivityManager cm;
	NetworkInfo networkInfo;

	/* Location Manager for GPS */
	LocationManager mlocManager = null;
	LocationListener mlocListener;
	AlertDialog.Builder alert;

	/* To get Mobile & Subscriber Details */
	String imei;
	String imsi;
	String PhoneModel;
	String AndroidVersion;
	String encrypted_name;
	String encrypted_pwd;

	private String pageUrl = "https://www.tnrd.gov.in/project/default_webservice.php";
	static string imsii, versioni, phonemodel;

	Button button2;
	Button login;
	String name = "", pass = "";
	EditText username, password;

	TelephonyManager telephonyManager;
	PhoneStateListener listener;

	byte[] data;
	HttpPost httppost;
	StringBuffer buffer;
	HttpResponse response;
	HttpClient httpclient;
	InputStream inputStream;
	List<NameValuePair> nameValuePairs;

	static final String KEY_USER_NAME = "mdn";
	static final String KEY_PWD = "pwdtxt";
	static final String KEY_IMEI = "imei_no";
	static final String KEY_IMSI = "imsi_no";
	static final String KEY_PHONE_NAME = "phname";
	static final String KEY_PHONE_VERSION = "phversion";
	static final String KEY_PHONE_MODEL = "phmodel";
	public static PlaceDataSQL placeData;
	SQLiteDatabase db;
	String main_url;

	static SharedPreferences sharedpreferences;
	static String pref_username;
	static String pref_password;

	void showToast(CharSequence msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login_screen);
           
          /*  //Show keyboard
            EditText editText = (EditText) findViewById(R.id.username);
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            // only will trigger it if no physical keyboard is open
            mgr.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);

           // And to hide:
            InputMethodManager mgr1 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr1.hideSoftInputFromWindow(editText.getWindowToken(), 0);*/


		// Get the telephony manager
		telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

		username = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.password);
		login = (Button) findViewById(R.id.login);

		TextView txt_footer = (TextView) findViewById(R.id.headtext1);
		txt_footer.setText("");

		placeData = new PlaceDataSQL(this);
		db = placeData.getWritableDatabase();
		sharedpreferences = getSharedPreferences("mypreferences", Context.MODE_PRIVATE);
		login.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				name = username.getText().toString();
				pass = password.getText().toString();

				if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
					// TODO: Consider calling
					//    ActivityCompat#requestPermissions
					// here to request the missing permissions, and then overriding
					//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
					//                                          int[] grantResults)
					// to handle the case where the user grants the permission. See the documentation
					// for ActivityCompat#requestPermissions for more details.
					return;
				}
				imei = telephonyManager.getDeviceId();
				if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
					// TODO: Consider calling
					//    ActivityCompat#requestPermissions
					// here to request the missing permissions, and then overriding
					//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
					//                                          int[] grantResults)
					// to handle the case where the user grants the permission. See the documentation
					// for ActivityCompat#requestPermissions for more details.
					return;
				}
				imsi = telephonyManager.getSubscriberId();
				PhoneModel = android.os.Build.MODEL;
				AndroidVersion = android.os.Build.VERSION.RELEASE;
				loginMethod();
			}
		});

	}

	@Override
	public void onResume() {
		super.onResume();
		pref_username = sharedpreferences.getString("username", null);
		pref_password = sharedpreferences.getString("password", null);
		if (pref_username != null && pref_password != null) {
			try {
				name = pref_username;
				pass = pref_password;

				if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
					// TODO: Consider calling
					//    ActivityCompat#requestPermissions
					// here to request the missing permissions, and then overriding
					//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
					//                                          int[] grantResults)
					// to handle the case where the user grants the permission. See the documentation
					// for ActivityCompat#requestPermissions for more details.
					return;
				}
				imei = telephonyManager.getDeviceId();
				imsi = telephonyManager.getSubscriberId();
				PhoneModel = android.os.Build.MODEL;
				AndroidVersion = android.os.Build.VERSION.RELEASE;
				loginMethod();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void loginMethod() {
        	/*name= username.getText().toString();
	 			pass=password.getText().toString();
	 		
	 			imei = telephonyManager.getDeviceId(); 
	 			imsi = telephonyManager.getSubscriberId();
	 			PhoneModel = android.os.Build.MODEL;
	 			AndroidVersion = android.os.Build.VERSION.RELEASE;*/

		if ((name.equals("") && pass.equals(""))) {
			AlertDialog.Builder ab = new AlertDialog.Builder(LoginScreen.this);
			ab.setMessage("Please Enter UserName and Password");
			ab.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

				}
			});
			ab.show();

		} else if (name.equals("")) {
			AlertDialog.Builder ab = new AlertDialog.Builder(LoginScreen.this);
			ab.setMessage("Please Enter UserName");
			ab.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

				}
			});
			ab.show();

		} else if (pass.equals("")) {
			AlertDialog.Builder ab = new AlertDialog.Builder(LoginScreen.this);
			ab.setMessage("Please Enter PassWord.");
			ab.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

				}
			});
			ab.show();

		} else if (!name.equals("") && !pass.equals("")) {
			cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			networkInfo = cm.getActiveNetworkInfo();

			if (networkInfo != null && networkInfo.isConnected()) {
				alert = new AlertDialog.Builder(LoginScreen.this);
				alert.setCancelable(true);

				mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
				mlocListener = new MyLocationListener();
				if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
					// TODO: Consider calling
					//    ActivityCompat#requestPermissions
					// here to request the missing permissions, and then overriding
					//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
					//                                          int[] grantResults)
					// to handle the case where the user grants the permission. See the documentation
					// for ActivityCompat#requestPermissions for more details.
					return;
				}
				mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);

				if (mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
					main_url = pageUrl + "?n=" + name + "&p=" + pass;
					showProgress(pageUrl);
					new Thread(new Runnable() {
						public void run() {
							db.delete("stageGroup", null, null);
							db.delete("stage", null, null);
							db.delete("qualityParameters", null, null);
							login();
						}
					}).start();
					//new DownloadFileAsync().execute();
				} else {
					alert.setTitle("GPS");
					alert.setMessage("GPS is turned OFF...\nDo U Want Turn On GPS..");
					alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							Intent I = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
							startActivity(I);
						}
					});
					alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {

						}
					});
					alert.show();
					showToast("GPS Turned ON..");
				}
			} else {
				AlertDialog.Builder ab = new AlertDialog.Builder(LoginScreen.this);
				ab.setMessage(Html.fromHtml("<font color=#ffffff>Internet Connection is not avaliable..Please Turn ON Network Connection OR Continue With Off-line Mode..\nTo Turn ON Network Connection Press Yes Button else To Continue With Off-Line Mode Press No Buttn..</font>"));
				ab.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						Intent I = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
						startActivity(I);
					}
				});
				ab.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						offline_mode();
					}


				});
				ab.show();
			}
		}
	}

	void showProgress(String file_path) {
		dialog1 = new Dialog(LoginScreen.this);
		dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog1.setContentView(R.layout.myprogressdialog);
		dialog1.setTitle("Download Progress");

		dialog_text = (TextView) dialog1.findViewById(R.id.tv1);
		dialog_text.setText("Validating Username and Password..");
		cur_val = (TextView) dialog1.findViewById(R.id.cur_pg_tv);
		cur_val.setText("Starting download...");
		dialog1.show();

		pb = (ProgressBar) dialog1.findViewById(R.id.progress_bar);
		pb.setProgress(0);
		pb.setProgressDrawable(getResources().getDrawable(R.drawable.green_progress));
	}

	void login() {
		int fileLength = 0;
		try {
			//encrypt(name.trim());

			URL url = new URL("https://www.tnrd.gov.in/project/android_app_auth2.php");
			URLConnection connection = url.openConnection();
			connection.connect();
			// 	this will be useful so that you can show a typical 0-100% progress bar
			fileLength = connection.getContentLength();
			InputStream inputStream1 = connection.getInputStream();


			URL url1 = new URL(main_url);
			HttpURLConnection urlConnection = (HttpURLConnection) url1.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setDoOutput(true);

			//connect
			urlConnection.connect();
			InputStream inputStream2 = urlConnection.getInputStream();

			byte[] buffer = new byte[1024];
			int bufferLength = 0;

			totalSize = fileLength + urlConnection.getContentLength();

			runOnUiThread(new Runnable() {
				public void run() {
					pb.setMax(totalSize);
					dialog_text.setText("Validating Username and Password..");
				}
			});


			while ((bufferLength = inputStream1.read(buffer)) > 0) {
				//fileOutput.write(buffer, 0, bufferLength);

				downloadedSize += bufferLength;
				// update the progressbar //
				runOnUiThread(new Runnable() {
					public void run() {
						pb.setProgress(downloadedSize);
						float per = ((float) downloadedSize / totalSize) * 100;
						cur_val.setText("Downloaded " + downloadedSize + "KB / " + totalSize + "KB (" + (int) per + "%)");
					}
				});
			}

			//encode by using Base64
			String txt_name = name;
			String txt_pwd = pass;
			byte[] data = txt_name.getBytes();
			byte[] data1 = txt_pwd.getBytes();
			encrypted_name = Base64.encodeToString(data, Base64.DEFAULT);
			encrypted_pwd = Base64.encodeToString(data1, Base64.DEFAULT);
//          	    Log.i("Base 64 ", encrypted_name);
//          	    Log.i("Base 64 ", encrypted_pwd);


			httpclient = new DefaultHttpClient();
			httppost = new HttpPost("https://www.tnrd.gov.in/project/android_app_auth2.php");
			//httppost = new HttpPost("http://10.163.14.137/rdwebtraining/project_new/webservices_forms/android_app_auth2.php");

			// 	Add your data
			nameValuePairs = new ArrayList<NameValuePair>(3);
//       			nameValuePairs.add(new BasicNameValuePair("mdn", name.trim()));
//       			nameValuePairs.add(new BasicNameValuePair("pwdtxt", pass.trim()));

			nameValuePairs.add(new BasicNameValuePair("mdn", encrypted_name));
			nameValuePairs.add(new BasicNameValuePair("pwdtxt", encrypted_pwd));
			nameValuePairs.add(new BasicNameValuePair("imei_no", imei.trim()));
       			/*nameValuePairs.add(new BasicNameValuePair("imsi_no", imsi.trim()));
       			nameValuePairs.add(new BasicNameValuePair("phname", PhoneModel.trim()));
       			nameValuePairs.add(new BasicNameValuePair("phversion", AndroidVersion.trim()));
       			nameValuePairs.add(new BasicNameValuePair("phmodel", AndroidVersion.trim()));
       			nameValuePairs.add(new BasicNameValuePair("command", "authentication"));*/

			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			// Execute HTTP Post Request
			response = httpclient.execute(httppost);


			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			final String response1 = httpclient.execute(httppost, responseHandler);
			System.out.println("Response is : " + response1);

			if (response1.equalsIgnoreCase("Y")) {
				ServerConnection sc = new ServerConnection();
				sc.constructFinalURL(pageUrl, encrypted_name.trim(), encrypted_pwd.trim());
				//sc.constructFinalURL(pageUrl, name.trim(), pass.trim());

				byte[] buffer1 = new byte[1024];
				int bufferLength1 = 0;

				while ((bufferLength1 = inputStream2.read(buffer1)) > 0) {

					downloadedSize += bufferLength1;
					// update the progressbar //
					runOnUiThread(new Runnable() {
						public void run() {
							dialog_text.setText("Your Data is getting downloaded..");
							pb.setProgress(downloadedSize);
							float per = ((float) downloadedSize / totalSize) * 100;
							cur_val.setText("Downloaded " + downloadedSize + "KB / " + totalSize + "KB (" + (int) per + "%)");
						}
					});
				}

				boolean searchResult = sc.makeRequest();

				if (searchResult) {
					if (pref_username == null && pref_password == null) {

						Editor edtr = sharedpreferences.edit();
						edtr.putString("username", username.getText().toString());
						edtr.putString("password", password.getText().toString());
						edtr.commit();
					}
					Intent Pendingwork = new Intent(LoginScreen.this, DashBoard.class);

					Pendingwork.putExtra(KEY_USER_NAME, name.trim());
					Pendingwork.putExtra(KEY_PWD, pass.trim());
					Pendingwork.putExtra(KEY_IMEI, imei.trim());
					Pendingwork.putExtra(KEY_IMSI, imsi.trim());
					Pendingwork.putExtra(KEY_PHONE_NAME, PhoneModel.trim());
					Pendingwork.putExtra(KEY_PHONE_VERSION, AndroidVersion.trim());
					Pendingwork.putExtra(KEY_PHONE_MODEL, AndroidVersion.trim());

					startActivity(Pendingwork);
					finish();
				}

			}

			runOnUiThread(new Runnable() {
				public void run() {
					//tv.setText("Response from PHP : " + response);
					dialog1.dismiss();


					if (response1.equalsIgnoreCase("N")) {

						AlertDialog.Builder ab = new AlertDialog.Builder(LoginScreen.this);
						ab.setMessage("Invalid UserName & Password ");
						if (pref_username != null && pref_password != null) {
							SharedPreferences settings = getSharedPreferences("mypreferences", Context.MODE_PRIVATE);
							settings.edit().remove("username").commit();
							settings.edit().remove("password").commit();
						}
						ab.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {

							}
						});
						ab.show();
						//showToast("Check Your Username & Password !");
					} else if (response1.equalsIgnoreCase("D")) {
						AlertDialog.Builder ab = new AlertDialog.Builder(LoginScreen.this);
						ab.setMessage("Authentication Failure(DB Error)..");
						ab.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {

							}
						});
						ab.show();
						//showToast("Check Your Username & Password !");
					}
				}
			});

		} catch (MalformedURLException e) {
			showError("Error : MalformedURLException " + e);
			e.printStackTrace();
		} catch (IOException e) {
			showError("Error : IOException " + e);
			e.printStackTrace();
		} catch (Exception e) {
			showError("Error : Please check your internet connection " + e);
		}

	}

	void showError(final String err) {
		runOnUiThread(new Runnable() {
			public void run() {
				Toast.makeText(LoginScreen.this, err, Toast.LENGTH_LONG).show();
			}
		});
	}

	protected void offline_mode() {

		alert = new AlertDialog.Builder(LoginScreen.this);
		alert.setCancelable(true);

		mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mlocListener = new MyLocationListener();
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return;
		}
		mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);

		if (mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
	 		{
	 			placeData = new PlaceDataSQL(LoginScreen.this);    
	 			SQLiteDatabase db = placeData.getWritableDatabase();
         
	 			Cursor cursors = getRawEvents("select * from authentication where mdn = ? AND pwdtxt = ?");
	 			if (cursors.moveToNext() == true)
	 			{
	 				Intent Pendingwork = new Intent(LoginScreen.this,DashBoard.class);
					startActivity(Pendingwork);	 	
					finish();
	 			}
	 			else
	 			{
	 				AlertDialog.Builder ab = new AlertDialog.Builder(LoginScreen.this);
	 				ab.setMessage(Html.fromHtml("<font color=#ffffff>There are No data available for you..you must login and download data from Server </font>"));
	   				ab.setNeutralButton("Ok", new DialogInterface.OnClickListener() 
	   				{
	   					public void onClick(DialogInterface dialog, int whichButton) 
	   					{				            	
	   						 
	   					}
	   				});
	   				ab.show();
	 			}
     	
	 			cursors.close();
	 			db.close();
	 		}
	 		else 
	 		{   	     		        
 	        alert.setTitle("GPS");
				alert.setMessage("GPS is turned OFF...\nDo U Want Turn On GPS..");
				
				alert.setNegativeButton("No", new DialogInterface.OnClickListener() 
	   			{
		           	public void onClick(DialogInterface dialog, int whichButton) 
		           	{				            	
		           		
		           	}
	   			});  
				
				alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() 
	   			{
					public void onClick(DialogInterface dialog, int whichButton) 
					{				            	
						Intent I = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	     	        	startActivity(I); 
					}
		        }); 
				alert.show();
				showToast("GPS Turned ON..");   	     			        
 		}
		
		
	}
    
	private Cursor getRawEvents(String sql) 
	{
			SQLiteDatabase db = (placeData).getReadableDatabase();
			String[] params=new String[]{String.valueOf(name.trim()),String.valueOf(pass.trim())};
			Cursor cursor = db.rawQuery(sql, params);
			startManagingCursor(cursor);
			return cursor;
	}

     
   }
