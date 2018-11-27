package com.example.ruralroadworks;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rd.DB.PlaceDataSQL;


public class PendingWorkDetailSpinner1 extends Activity implements OnClickListener
{
	String array2[];
	String[] array3;
	private static final int CAMERA_PIC_REQUEST = 2500;
	EditText etext;
	ImageView home, back;
	Spinner stage_group, stage,quality_parameters,quality_parameter_value;
	EditText chainage;
	ArrayAdapter<CharSequence> adapter, adapter1, adapter2,adapter3,adapter4;
	int selectedSpinnerIndex=-1, selectedSpinnerIndex1,selectedSpinnerIndex2;
	boolean spinner;
	static String remarksTextValue;
	TextView t, t1, t2, un, d, ll, pro;
	private InputStream inputStream;
	static String SpinnerTextValue="", SpinnerTextValue1, SpinnerTextValue2, SpinnerTextValue3, SpinnerTextValue4;  
	static String Workid;

	int position_spinner,position_spinner1,position_spinner2,position_spinner3,position_spinner4;

	private static PlaceDataSQL placeData;

	LocationManager mlocManager=null;
	LocationListener mlocListener;
	AlertDialog.Builder alert;
	
	String stage_group_id[];
	String stage_group_name[];
	String stage_id[];
	String stage_name[];
	String group_id[];
	String quality_parameter[];
	
	void showToast(CharSequence msg) 
	{
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.toast, (ViewGroup) findViewById(R.id.toast_layout_root));

		ImageView image = (ImageView) layout.findViewById(R.id.image);
		image.setImageResource(R.drawable.ic_launcher);
		TextView text = (TextView) layout.findViewById(R.id.text);
		text.setText(msg);

		Toast toast = new Toast(this);
		toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_VERTICAL, 0, 0);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(layout);
		toast.show();
		//Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	
	
	private class offTextWatcher implements TextWatcher {
		public offTextWatcher(EditText dl) {
		}

		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}

		public void afterTextChanged(Editable s) {
			try {
				String dob = chainage.getText().toString();
				int val = Integer.parseInt(dob);
				Log.i("VAL", val + "");

				if (val > 3000) {
					chainage.getText().delete(dob.length() - 1, dob.length());
					showToast("Invalid Chainage....");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
			
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.pending_work_details_spinner1);


		placeData = new PlaceDataSQL(this);    
		SQLiteDatabase db = placeData.getWritableDatabase();

		Cursor cursor = getRawEvents("select * from details");
		while (cursor.moveToNext())
		{
			String servicePro = cursor.getString(5);
			pro = (TextView)findViewById(R.id.footertxt);
			pro.setText(servicePro);
			System.out.println("SR "+servicePro);
		}
		
		TextView tv_workid = (TextView)findViewById(R.id.tv_workid);
		tv_workid.setText("Work ID :"+Workid);

		initialize();
		etext = (EditText)findViewById(R.id.editText1);
		etext.setVisibility(View.GONE);
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		Button takePhoto = (Button) findViewById(R.id.Button02);
		takePhoto.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v) 
			{
				SpinnerTextValue = chainage.getText().toString(); 
				remarksTextValue = etext.getText().toString();
				
				alert  = new AlertDialog.Builder(PendingWorkDetailSpinner1.this);
				alert.setCancelable(true);

				if(!SpinnerTextValue.equals("") && position_spinner1 != 0 && position_spinner2 !=0 
						&& position_spinner3 != 0 && position_spinner4 !=0 )
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
//								if(MyLocationListener.accuracy <= 5.0 ) {
									Intent cameraIntent = new Intent(
											android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
									startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
//								}else {
//									alert.setTitle("GPS");
//									alert.setMessage("Accuracy is "+MyLocationListener.accuracy+"m");
//									alert.setPositiveButton("OK", null);
//									alert.show();
//								}
							}
							else
							{
								alert.setTitle("GPS");
								alert.setMessage("Satellite communication not available to get GPS Co-ordination Please Capture Photo in Open Area..");
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
					Toast.makeText(PendingWorkDetailSpinner1.this, "Some Field are missing...\nplease choose Required field and try again..", Toast.LENGTH_LONG).show();
				}
			} 
		});

		chainage = (EditText) findViewById(R.id.ed_chainage);
		chainage.addTextChangedListener(new offTextWatcher(chainage));

		stage_group = (Spinner) findViewById(R.id.spinner1);
		stage = (Spinner) findViewById(R.id.spinner2);
		quality_parameters = (Spinner) findViewById(R.id.spinner3);
		quality_parameter_value = (Spinner) findViewById(R.id.spinner4);
				
		Cursor cursors = getRawEvents("SELECT stageGroupId,stageGroupName FROM stageGroup");
		cursors.moveToFirst();
		
		stage_group_name = new String[cursors.getCount()+1];
		stage_group_id =  new String[cursors.getCount()+1];
				
		stage_group_name[0] = "--Select--";
		stage_group_id[0] = "--Select--";
		
		for(int i=1;i<cursors.getCount()+1;i++)
		{
			stage_group_id[i] = cursors.getString(0);	
			stage_group_name[i] = cursors.getString(1);
			cursors.moveToNext();
		}
		stage_group.setAdapter(new MyAdapter(PendingWorkDetailSpinner1.this, R.layout.spinner_value,stage_group_name));
		stage_group.setOnItemSelectedListener(new OnItemSelectedListener() 
		{
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) 
			{
				position_spinner1 = position;
				if (position>=1)
				{
					SpinnerTextValue1 = stage_group_name[position];
					Log.i("Debug", "Stage Group ::: "+SpinnerTextValue1);
					
					String indx = stage_group_id[position];
					String wher[] = {indx};
					
					Cursor cursors = getRawEvent1("SELECT stageGroupId,stageId,stageName from stage WHERE stageGroupId=?",wher);
					cursors.moveToFirst();
					
					stage_id =  new String[cursors.getCount()+1];
					group_id = new String[cursors.getCount()+1];
					stage_name = new String[cursors.getCount()+1];
					
					stage_id[0] = "--Select--";
					group_id[0] = "--Select--";
					stage_name[0] = "--Select--";
					
					for(int i=1;i<cursors.getCount()+1;i++)
					{
						stage_id[i] = cursors.getString(0);
						group_id[i] = cursors.getString(1);
						stage_name[i] = cursors.getString(2);
						cursors.moveToNext();
					}
					stage.setAdapter(new MyAdapter(PendingWorkDetailSpinner1.this, R.layout.spinner_value,stage_name));
				}		
			}
			public void onNothingSelected(AdapterView<?> parent) 
			{
				showToast("Spinner: unselected");
			}
		});
		
		stage.setOnItemSelectedListener(new OnItemSelectedListener() 
		{
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) 
			{
				position_spinner2 = position;
				if (position>=1)
				{
					SpinnerTextValue2 = stage_name[position];
					Log.i("Debug", "Stage ::: "+SpinnerTextValue2);
					
					String indx = stage_id[position];
					String indx1 = group_id[position];
					
					String wher[] = {indx,indx1};
					
					Cursor cursors = getRawEvent1("SELECT qualityParams FROM qualityParameters WHERE stageGroupId=? AND stageId=?",wher);
					cursors.moveToFirst();
					
					quality_parameter = new String[cursors.getCount()+1];
					quality_parameter[0] = "--Select--";
					
					for(int i=1;i<cursors.getCount()+1;i++)
					{
						quality_parameter[i] = cursors.getString(0);
						cursors.moveToNext();
					}
					quality_parameters.setAdapter(new MyAdapter(PendingWorkDetailSpinner1.this, R.layout.spinner_value,quality_parameter));
				}		
			}
			public void onNothingSelected(AdapterView<?> parent) 
			{
				showToast("Spinner: unselected");
			}
		});
		
		quality_parameters.setOnItemSelectedListener(new OnItemSelectedListener() 
		{
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) 
			{
				position_spinner3 = position;

				if(position >= 1)
				{
					SpinnerTextValue3 = quality_parameter[position];
					Log.i("Debug", "Quality Parameter ::: "+SpinnerTextValue3);
				}
				
			}
			public void onNothingSelected(AdapterView<?> parent) 
			{
				showToast("Spinner: unselected");
			}
		});
		
		final String array1[] =  getResources().getStringArray(R.array.qpv_array);
		quality_parameter_value.setAdapter(new MyAdapter(PendingWorkDetailSpinner1.this, R.layout.spinner_value,array1));

		quality_parameter_value.setOnItemSelectedListener(new OnItemSelectedListener() 
		{
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) 
			{
				position_spinner4 = position;

				if(position == 1)
				{
					SpinnerTextValue4 = array1[position];
					Log.i("Debug", "Quality Parameter Value ::: "+SpinnerTextValue4);
					etext.setVisibility(View.GONE);
				}
				else if (position==2)
				{
					SpinnerTextValue4 = array1[position];
					Log.i("Debug", "Quality Parameter Value ::: "+SpinnerTextValue4);

					showToast("Enter Remarks");
					etext.setVisibility(View.VISIBLE);
					etext.setFocusable(true);
				}	
			}
			public void onNothingSelected(AdapterView<?> parent) 
			{
				showToast("Spinner: unselected");
			}
		});
	}

	private Cursor getRawEvent1(String sql, String[] wher) 
	{
		//String[] id ={wher};
		SQLiteDatabase db = (placeData).getReadableDatabase();
		Cursor cursor = db.rawQuery(sql, wher);

		startManagingCursor(cursor);
		return cursor;
	}

	private Cursor getRawEvents(String sql) 
	{
		SQLiteDatabase db = (placeData).getReadableDatabase();
		Cursor cursor = db.rawQuery(sql, null);

		startManagingCursor(cursor);
		return cursor;
	}


	private void initialize() 
	{
		home = (ImageView) findViewById(R.id.homeimg);
		home.setOnClickListener(this); 

		back = (ImageView) findViewById(R.id.backimg);
		back.setOnClickListener(this);
	}

	public static Bitmap cameraImage ;
	private void setCamaraImage(Bitmap bitmap)
	{
		cameraImage = bitmap;
	}

	public static Bitmap getCameraBitmap()
	{
		return cameraImage;
	}

	public static byte[] getCameraImage()
	{
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		cameraImage.compress(Bitmap.CompressFormat.PNG, 90, stream); //compress to which format you want.
		byte [] byte_arr = stream.toByteArray();
		return byte_arr;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		if (requestCode == CAMERA_PIC_REQUEST) 
		{      	
			try
			{
				Bitmap bitmap = (Bitmap) data.getExtras().get("data");
				setCamaraImage(bitmap);

				Intent upload = new Intent(PendingWorkDetailSpinner1.this,UploadPendingWorks.class);
				startActivity(upload);
				finish();
			}

			catch (Exception e) 
			{
				System.out.println("ooooooooooooooooooooooon "+e.toString());
			}		
		}      	
	}

	public String convertResponseToString(HttpResponse response) throws IllegalStateException, IOException
	{

		String res = "";
		StringBuffer buffer = new StringBuffer();
		inputStream = response.getEntity().getContent();
		int contentLength = (int) response.getEntity().getContentLength(); //getting content length…..

		if (contentLength < 0)
		{
		}
		else
		{
			byte[] data = new byte[512];
			int len = 0;
			try
			{
				while (-1 != (len = inputStream.read(data)) )
				{
					buffer.append(new String(data, 0, len)); //converting to string and appending  to stringbuffer…..
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			try
			{
				inputStream.close(); // closing the stream…..
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			res = buffer.toString();     // converting stringbuffer to string…..

		}
		return res;
	}

	
	
	public void onClick(View v) 
	{
		if (v.equals(home))
		{			
			Intent Pendingwork = new Intent(PendingWorkDetailSpinner1.this,DashBoard.class);
			startActivity(Pendingwork);	
			finish();
		}
		if (v.equals(back))
		{	
			Intent Pendingwork = new Intent(PendingWorkDetailSpinner1.this,PendingWork.class);
			startActivity(Pendingwork);
			finish();
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		if (keyCode == KeyEvent.KEYCODE_BACK) 
		{
			Intent Pendingwork = new Intent(PendingWorkDetailSpinner1.this,PendingWork.class);
			startActivity(Pendingwork);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}

