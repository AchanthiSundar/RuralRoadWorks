package com.example.ruralroadworks;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Factory;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rd.DB.DatabaseHelper;

public class DashBoard extends Activity implements OnClickListener
{
	DatabaseHelper dbHelper;
	TextView pendingWorks, pendingWorksCount, pendingUploadCount, pendingUploads, emp_name, emp_designation, emp_last_visit_date, service_provider;
	ImageView employee_photo;
	//public static PlaceDataSQL placeData;

	//Used to check internet Connection..
	ConnectivityManager cm;
	NetworkInfo networkInfo;


	static final String KEY_USER_NAME = "mdn";
	static final String KEY_PWD = "pwdtxt";
	static final String KEY_IMEI = "imei_no";
	static final String KEY_IMSI = "imsi_no";
	static final String KEY_PHONE_NAME = "phname";
	static final String KEY_PHONE_VERSION = "phversion";
	static final String KEY_PHONE_MODEL = "phmodel";


	void showToast(CharSequence msg) 
	{
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	String pendingWorkCoun;

	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.dash_board);

		employee_photo =(ImageView)findViewById(R.id.emp_photo);
		emp_name = (TextView)findViewById(R.id.emp_name);  
		emp_designation = (TextView)findViewById(R.id.designation);
		pendingWorks = (TextView)findViewById(R.id.pendingWorks);
		pendingUploads = (TextView)findViewById(R.id.pendingUploads);
		pendingUploadCount = (TextView)findViewById(R.id.pendingUploadCount);
		pendingWorksCount = (TextView)findViewById(R.id.pendingWorksCount);
		emp_last_visit_date = (TextView)findViewById(R.id.emp_last_visit_date);
		service_provider = (TextView)findViewById(R.id.footertxt);

		dbHelper=new DatabaseHelper(this);

		/*String photo_url = ServerConnection.xml_emp_photo.get(0).toString().trim();
        Drawable drawable = LoadImageFromWebOperations(photo_url);
        employee_photo.setImageDrawable(drawable);

        pendingUploadCount.setText(""+dbHelper.getTotalCount());
        pendingUploadCount.setOnClickListener(this);

        pendingWorksCount.setText(""+ServerConnection.xml_pendingWork.size());
        pendingWorksCount.setOnClickListener(this);

        String name = ServerConnection.xml_emp_name.get(0).toString().trim();       
        emp_name.setText(name);

        String designation = ServerConnection.xml_emp_designation.get(0).trim();       
        emp_designation.setText(designation);

        String previous_visit_date = ServerConnection.xml_emp_last_visit_date.get(0).toString().trim();       
        emp_last_visit_date.setText(previous_visit_date);

        String service = ServerConnection.xml_serviceProvider.get(0).toString().trim();      
        service_provider.setText(service);*/

		/*placeData = new PlaceDataSQL(this); */   
		SQLiteDatabase db = LoginScreen.placeData.getWritableDatabase();

		cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		networkInfo = cm.getActiveNetworkInfo();

		if (networkInfo != null && networkInfo.isConnected()) 
		{  
			db.delete("details", null,null);
			db.delete("authentication", null,null);
			db.delete("vill_details", null,null);
			

			byte[] photo_url = ServerConnection.xml_emp_photo.get(0);
			//System.out.println("Photo URRRRR "+photo_url);

			pendingUploadCount.setText(""+dbHelper.getTotalCount());
			pendingUploadCount.setOnClickListener(this);

			pendingWorkCoun = ""+ServerConnection.xml_pendingWork.size();
			String name = ServerConnection.xml_emp_name.get(0).toString().trim();       
			String designation = ServerConnection.xml_emp_designation.get(0).trim();       
			String previous_visit_date = ServerConnection.xml_emp_last_visit_date.get(0).toString().trim();       
			String service = ServerConnection.xml_serviceProvider.get(0).toString().trim();


			Intent in = getIntent();       
			// Get XML values from previous intent
			String user_name = in.getStringExtra(KEY_USER_NAME);
			String pwd = in.getStringExtra(KEY_PWD);
			String imei_no = in.getStringExtra(KEY_IMEI);
			String imsi_no = in.getStringExtra(KEY_IMSI);
			String phname = in.getStringExtra(KEY_PHONE_NAME);
			String phversion = in.getStringExtra(KEY_PHONE_VERSION);
			String phmodel = in.getStringExtra(KEY_PHONE_MODEL);


			try 
			{
				callInsertion(photo_url,name,designation,pendingWorkCoun,previous_visit_date,service);
				callAuthenticationTableInsertion(user_name,pwd,imei_no,imsi_no,phname,phversion,phmodel);
			}
			catch (ClientProtocolException e) 
			{
				e.printStackTrace();
			}
			catch (IOException e) 
			{
				e.printStackTrace();
			}

			for (int i = 0; i < ServerConnection.work_id.size(); i++) 
			{
				String work_id = ServerConnection.work_id.get(i).toString();
				String vill = ServerConnection.village.get(i).toString();
				String block = ServerConnection.block.get(i).toString();
				String scheme = ServerConnection.scheme.get(i).toString();
				String fin_year = ServerConnection.financialyear.get(i).toString();
				String agency = ServerConnection.agencyname.get(i).toString();
				String workName = ServerConnection.workname.get(i).toString();

				InsertVillageDetails(work_id,vill,block,scheme,fin_year,agency,workName);

			}

			viewEmployeeDetails();    
			db.close();
		}
		else
		{
			Cursor cursors = getRawEvents("select * from details");
			pendingWorkCoun = ""+cursors.getCount(); 
					
			pendingUploadCount.setText(""+dbHelper.getTotalCount());
			pendingUploadCount.setOnClickListener(this);

			viewEmployeeDetails();
			db.close();
		}

		emp_last_visit_date.setOnClickListener(this);
		emp_designation.setOnClickListener(this);
		emp_name.setOnClickListener(this);
		pendingWorks.setOnClickListener(this);  
		pendingWorksCount.setOnClickListener(this);
		pendingUploads.setOnClickListener(this);       
		service_provider.setOnClickListener(this);
	}

	private void viewEmployeeDetails() 
	{
		Cursor cursors = getRawEvents("select * from details");
		while (cursors.moveToNext())
		{

			byte[] emp_image = cursors.getBlob(0);
			String emp_name1 = cursors.getString(1);
			String emp_designation1 = cursors.getString(2);
			String count = cursors.getString(3);
			String date = cursors.getString(4);
			String ser = cursors.getString(5);

			employee_photo.setImageBitmap(BitmapFactory.decodeByteArray(emp_image, 0, emp_image.length));
			emp_name.setText(emp_name1);
			emp_designation.setText(emp_designation1);
			pendingWorksCount.setText(count);
			emp_last_visit_date.setText(date);
			service_provider.setText(ser);
		}	

		cursors.close();

	}

	private void callInsertion(byte[] url, String name, String designation,
			String pendingWorkCoun, String previous_visit_date,String service) throws ClientProtocolException, IOException 
			{
		insertDataIntoEmployeeDetails(url,name,designation,pendingWorkCoun,previous_visit_date,service);
			}

	private void callAuthenticationTableInsertion(String user_name, String pwd,String imei_no, 
			String imsi_no, String phname, String phversion,String phmodel) throws ClientProtocolException, IOException
			{
		insertDataIntoAuthentication(user_name,pwd,imei_no,imsi_no,phname,phversion,phmodel);
			}

	private void InsertVillageDetails(String work_id, String vill,String block, String scheme, 
			String fin_year, String agency,String workName)
	{
		insertDataIntoVillageDetails(work_id,vill,block,scheme,fin_year,agency,workName);
	}


	private void insertDataIntoEmployeeDetails(byte[] emp_photo, String name, String designation,
			String pendingWorkCoun, String previous_visit_date, String service)
	{
		SQLiteDatabase db = LoginScreen.placeData.getWritableDatabase();
		ContentValues values;
		values = new ContentValues();

		values.put("employee_photo", emp_photo);
		values.put("emp_name", name);
		values.put("emp_designation", designation);
		values.put("pendingWorksCount", pendingWorkCoun);
		values.put("emp_last_visit_date", previous_visit_date);
		values.put("service_provider", service);

		db.insert("details", null, values);
	}

	private void insertDataIntoAuthentication(String user_name, String pwd,
			String imei_no, String imsi_no, String phname, String phversion,
			String phmodel)
	{
		SQLiteDatabase db = LoginScreen.placeData.getWritableDatabase();
		ContentValues values;
		values = new ContentValues();

		values.put("mdn", user_name);
		values.put("pwdtxt", pwd);
		values.put("imei_no", imei_no);
		values.put("imsi_no", imsi_no);
		values.put("phname", phname);
		values.put("phversion", phversion);
		values.put("phmodel", phmodel);

		db.insert("authentication", null, values);
	}

	private void insertDataIntoVillageDetails(String work_id, String vill,String block, String scheme,
			String fin_year, String agency,String workName) 
	{
		SQLiteDatabase db = LoginScreen.placeData.getWritableDatabase();
		ContentValues values;
		values = new ContentValues();

		values.put("work_id", work_id);
		values.put("scheme", scheme);
		values.put("fin_year", fin_year);
		values.put("agencyName", agency);
		values.put("workName", workName);
		values.put("block", block);
		values.put("village", vill);

		db.insert("vill_details", null, values);

	}

	/*File file = new File("infilename");

	// Get the number of bytes in the file
	long sizeInBytes = file.length();
	//transform in MB
	long sizeInMb = sizeInBytes / (1024 * 1024);*/

	/*private Drawable LoadImageFromWebOperations(String url) 
    {
    	try
    	{
    		InputStream is = (InputStream) new URL(url).getContent();
    		Drawable d = Drawable.createFromStream(is, "src name");
    		return d;
    	}
    	catch (Exception e) 
    	{
    		System.out.println("Exc="+e);
    		return null;
    	}	  

	}*/

	static ArrayList<String> workid = new ArrayList<String>();
	static ArrayList<String> spin = new ArrayList<String>();
	static ArrayList<String> spin1 = new ArrayList<String>();
	static ArrayList<String> spin2 = new ArrayList<String>();
	static ArrayList<String> spin3 = new ArrayList<String>();
	static ArrayList<String> spin4 = new ArrayList<String>();
	static ArrayList<String> remarks = new ArrayList<String>();
	static ArrayList<String> lat = new ArrayList<String>();
	static ArrayList<String> lan = new ArrayList<String>();
	static ArrayList<byte[]> image = new ArrayList<byte[]>();
	static String[] spinArray;

	private void getDataAndPopulate() 
	{
//		workid = new ArrayList<String>();	     
//		spin = new ArrayList<String>();
//		spin1 = new ArrayList<String>();
//		spin2 = new ArrayList<String>();
//		spin3 = new ArrayList<String>();
//		spin4 = new ArrayList<String>();
//		remarks = new ArrayList<String>();	     
//		lat = new ArrayList<String>();	     
//		lan = new ArrayList<String>();	     
//		image = new ArrayList<byte[]>();
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
			String temp_lat = cursor.getString(8);
			String temp_lan = cursor.getString(9);
			byte[] temp_image = cursor.getBlob(10);

			Log.i("Debug", "IMGAge ::1::: "+cursor.getString(0));
			Log.i("Debug", "IMGAge ::2::: "+cursor.getString(1));
			Log.i("Debug", "IMGAge ::3::: "+cursor.getString(2));
			Log.i("Debug", "IMGAge ::4::: "+cursor.getString(3));
			Log.i("Debug", "IMGAge ::5::: "+cursor.getString(4));
			Log.i("Debug", "IMGAge ::6::: "+cursor.getString(5));
			Log.i("Debug", "IMGAge ::7::: "+cursor.getString(6));
			Log.i("Debug", "IMGAge :::8:: "+cursor.getString(7));

			workid.add(temp_id);
			spin.add(temp_spin);
			spin1.add(temp_spin1);
			spin2.add(temp_spin2);
			spin3.add(temp_spin3);
			spin4.add(temp_spin4);
			remarks.add(temp_remarks);
			lat.add(temp_lat);
			lan.add(temp_lan);
			image.add(temp_image);
		}

		spinArray = (String[]) spin.toArray(new String[spin.size()]);
		Log.i("Debug", "Spin array "+workid.size());

		if(workid.size()>0)
		{
			try
			{	    		 
				startActivity(new Intent(DashBoard.this, PendingUpload.class));
				finish();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}			
		}
		else
		{
			Toast.makeText(DashBoard.this, "There is no data to Upload", Toast.LENGTH_LONG).show();
		}	  
	}  

	private Cursor getEvents(String table)
	{
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(table, null, null, null, null, null, null);
		startManagingCursor(cursor);
		return cursor;
	}

	public void onClick(View v) 
	{
		if(v.equals(pendingWorks))
		{
			try
			{
				System.out.println("Count "+pendingWorkCoun);
				int count = Integer.parseInt(pendingWorkCoun);
				if(count > 0)
				{
					startActivity(new Intent(DashBoard.this, PendingWork.class));
					finish();
				}
				else
				{
					Toast.makeText(DashBoard.this, "There is no Pending Works to View..", Toast.LENGTH_LONG).show();
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		else if(v.equals(pendingUploads)) 
		{
			getDataAndPopulate();
		}
	}

	@SuppressWarnings("deprecation")
	private Cursor getRawEvents(String sql) 
	{
		SQLiteDatabase db = (LoginScreen.placeData).getReadableDatabase();
		Cursor cursor = db.rawQuery(sql, null);

		startManagingCursor(cursor);
		return cursor;
	}



	private static final int exit = Menu.FIRST + 1;
	//private static final int Syncronize = Menu.FIRST + 2;

	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);

		menu.add(1, exit, 0, "EXIT Rural Works").setIcon(R.drawable.exit);
		//menu.add(2, Syncronize, 0, "Download Data").setIcon(R.drawable.exit);

		setMenuBackground();
		return (super.onCreateOptionsMenu(menu));

	}

	private void setMenuBackground()
	{
		getLayoutInflater().setFactory(new Factory()
		{
			public View onCreateView(String name, Context context, AttributeSet attrs)
			{
				System.out.println("The menu value is: " + name);
				if (name.equalsIgnoreCase("com.android.internal.view.menu.IconMenuItemView"))
				{
					System.out.println("Inside condition: ");
					try
					{
						LayoutInflater f = getLayoutInflater();
						final View view = f.createView(name, null, attrs);

						new Handler().post(new Runnable()
						{
							public void run()
							{

								view.setBackgroundResource(R.drawable.personal_bg_bar);
								((TextView) view).setTextColor(Color.WHITE);
								((TextView) view).setTextSize(16);
							}
						});
						return view;
					} catch (InflateException e)
					{
						System.out.println("the error in menu: " + e);

					} catch (ClassNotFoundException e)
					{
						System.out.println("the error in menu: " + e);
					}
				}
				return null;
			}
		});
	}

	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle item selection
		switch (item.getItemId()) 
		{
		case exit:
		{
			System.exit(0);
			return true;
		}

		default:
			return super.onOptionsItemSelected(item);
		}
	}



	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		if (keyCode == KeyEvent.KEYCODE_BACK) 
		{
			AlertDialog.Builder ab = new AlertDialog.Builder(DashBoard.this);
			ab.setMessage("Do you want to exit?");
			ab.setPositiveButton("Yes", new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog, int whichButton) 
				{				            	
					System.exit(0);
				}
			});   


			ab.setNegativeButton("No", new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog, int whichButton) 
				{				            	

				}
			});   
			ab.show();

		}
		return super.onKeyDown(keyCode, event);
	}


}