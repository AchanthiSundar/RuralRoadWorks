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


public class PendingWorkDetailSpinner extends Activity implements OnClickListener
{
	String array2[];
	String[] array3;
	private static final int CAMERA_PIC_REQUEST = 2500;
	EditText etext;
	ImageView home, back;
	Spinner s, s1,s2,s3,s4;
	ArrayAdapter<CharSequence> adapter, adapter1, adapter2,adapter3,adapter4;
	int selectedSpinnerIndex=-1, selectedSpinnerIndex1,selectedSpinnerIndex2;
	boolean spinner;
	static String remarksTextValue;
	TextView t, t1, t2, un, d, ll, pro;
	private InputStream inputStream;
	static String SpinnerTextValue, SpinnerTextValue1, SpinnerTextValue2, SpinnerTextValue3, SpinnerTextValue4;  
	static String Workid;

	int position_spinner,position_spinner1,position_spinner2,position_spinner3,position_spinner4;

	private static PlaceDataSQL placeData;

	LocationManager mlocManager=null;
	LocationListener mlocListener;
	AlertDialog.Builder alert;


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


	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
		//                      WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.pending_work_details_spinner);


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
		//cursor.close();
		//db.close();

		/*pro = (TextView)findViewById(R.id.footertxt);
        pro.setText(""+ServerConnection.xml_serviceProvider.get(0).toString());
        pro.setOnClickListener(this);*/

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
				alert  = new AlertDialog.Builder(PendingWorkDetailSpinner.this);
				alert.setCancelable(true);

				if(position_spinner !=0 && position_spinner1 != 0 && position_spinner2 !=0 
						&& position_spinner3 != 0 && position_spinner4 !=0 )
				{
					mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
					mlocListener = new MyLocationListener();	        
					mlocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
					mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
					{   	
						//Toast.makeText(SqliteDBActivity.this, "Photo Captured Successfully", Toast.LENGTH_LONG).show();
						mlocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, mlocListener);

						if (mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) 
						{
							if(MyLocationListener.latitude>0)
							{

								Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
								startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST); 
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
					Toast.makeText(PendingWorkDetailSpinner.this, "Some Field are missing...\nplease choose Required field and try again..", Toast.LENGTH_LONG).show();
				}


				/*Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        		startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);*/

				//Intent upload = new Intent(workidspin.this,upload.class);
				//startActivity(upload);
			} 
		});

		s = (Spinner) findViewById(R.id.spinner);
		/* ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.chainage_array, android.R.layout.simple_spinner_item);     
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);*/

		final String array[] =  getResources().getStringArray(R.array.chainage_array);
		s.setAdapter(new MyAdapter(PendingWorkDetailSpinner.this, R.layout.spinner_value,array));

		s.setOnItemSelectedListener(new OnItemSelectedListener() 
		{
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) 
			{
				position_spinner = position;
				if (position>=1)
				{

					showToast("Select Stage Group");
					//SpinnerTextValue = s.getSelectedItem().toString();

					SpinnerTextValue = array[position];
					Log.i("Debug", "Select spinner index @@@ SpinnerTextValue ::: "+SpinnerTextValue);
				}		
			}
			public void onNothingSelected(AdapterView<?> parent) 
			{
				showToast("Spinner: unselected");
			}
		});


		final String array1[] =  getResources().getStringArray(R.array.stagegroup_array);
		s1 = (Spinner) findViewById(R.id.spinner1);
		Log.i("Debug", "Select spinner index spinner ::: "+spinner);
		/*adapter1 = ArrayAdapter.createFromResource(this, R.array.stagegroup_array, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);       
        s1.setAdapter(adapter1);*/

		s1.setAdapter(new MyAdapter(PendingWorkDetailSpinner.this, R.layout.spinner_value,array1));
		s1.setOnItemSelectedListener(new OnItemSelectedListener()
		{
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
			{
				position_spinner1 = position;
				if (position>=1)
					showToast("Select Stage");
				selectedSpinnerIndex = position;

				//SpinnerTextValue1 = s1.getSelectedItem().toString();
				SpinnerTextValue1 = array1[position];

				Log.i("Debug", "Select spinner index @@@ SpinnerTextValue 11 ::: "+SpinnerTextValue1);
				spinner = true;
				setContent(position);
			}

			public void onNothingSelected(AdapterView<?> parent) 
			{
				//                  showToast("Spinner1: unselected");
			}
		});
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

				remarksTextValue = etext.getText().toString();

				Intent upload = new Intent(PendingWorkDetailSpinner.this,UploadPendingWorks.class);
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

	private void setContent(int selectedSpinnerIndex) 
	{                
		Log.i("Debug", "Select spinner index ##### "+selectedSpinnerIndex);

		s2 = (Spinner) findViewById(R.id.spinner2);
		Log.i("Debug", "Select spinner index spinner ::: "+spinner);
		if(spinner)
		{
			//final String array2[];
			switch (selectedSpinnerIndex)
			{
			case 0: //earth work
				//    			adapter1 = ArrayAdapter.createFromResource(this, R.array.entrys2_array,
				//    	                android.R.layout.simple_spinner_item);
				//    	        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				//    			adapter2 = ArrayAdapter.createFromResource(
				//    	                this, R.array.entrys1_array, android.R.layout.simple_spinner_item);
				//    	        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				break;
			case 1://cd works
				/*adapter2 = ArrayAdapter.createFromResource(this, R.array.stagen1_array,
    	                android.R.layout.simple_spinner_item);
    	        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);*/

				array2 =  getResources().getStringArray(R.array.stagen1_array);
				s2.setAdapter(new MyAdapter(PendingWorkDetailSpinner.this, R.layout.spinner_value,array2));

				break;
			case 2:
				/*adapter2 = ArrayAdapter.createFromResource(
    	                this, R.array.stagen2_array, android.R.layout.simple_spinner_item);
    	        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);*/

				array2 =  getResources().getStringArray(R.array.stagen2_array);
				s2.setAdapter(new MyAdapter(PendingWorkDetailSpinner.this, R.layout.spinner_value,array2));

				break;
			case 3:
				/*adapter2 = ArrayAdapter.createFromResource(this, R.array.stagen3_array,
    	                android.R.layout.simple_spinner_item);
    	        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);*/

				array2 =  getResources().getStringArray(R.array.stagen3_array);
				s2.setAdapter(new MyAdapter(PendingWorkDetailSpinner.this, R.layout.spinner_value,array2));
				break;
			case 4:
				/*adapter2 = ArrayAdapter.createFromResource(this, R.array.stagen4_array,
    	                android.R.layout.simple_spinner_item);
    	        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);*/

				array2 =  getResources().getStringArray(R.array.stagen4_array);
				s2.setAdapter(new MyAdapter(PendingWorkDetailSpinner.this, R.layout.spinner_value,array2));

				break;
			case 5:
				/*adapter2 = ArrayAdapter.createFromResource(this, R.array.stagen5_array,
    	                android.R.layout.simple_spinner_item);
    	        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);*/

				array2 =  getResources().getStringArray(R.array.stagen5_array);
				s2.setAdapter(new MyAdapter(PendingWorkDetailSpinner.this, R.layout.spinner_value,array2));
				break;
			case 6:
				/*adapter2 = ArrayAdapter.createFromResource(this, R.array.stagen6_array,
    	                android.R.layout.simple_spinner_item);
    	        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);*/

				array2 =  getResources().getStringArray(R.array.stagen6_array);
				s2.setAdapter(new MyAdapter(PendingWorkDetailSpinner.this, R.layout.spinner_value,array2));

				break;
			case 7:
				/*adapter2 = ArrayAdapter.createFromResource(this, R.array.stagen7_array,
    	                android.R.layout.simple_spinner_item);
    	        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);*/

				array2 =  getResources().getStringArray(R.array.stagen7_array);
				s2.setAdapter(new MyAdapter(PendingWorkDetailSpinner.this, R.layout.spinner_value,array2));

				break;
			default:
				break;

			}
		}
		//        setContent(selectedSpinnerIndex);

		//s2.setAdapter(adapter2);
		s2.setOnItemSelectedListener(new OnItemSelectedListener() 
		{
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) 
			{
				position_spinner2 = position;
				if (position>=1)
					showToast("Select Quality Parameter");
				//                  showToast("Spinner2: position=" + position + " id=" + id);
				setContent2(position);
				//SpinnerTextValue2 = s2.getSelectedItem().toString();
				SpinnerTextValue2 = array2[position];
				Log.i("Debug", "Select spinner index @@@ SpinnerTextValue2222 ::: "+SpinnerTextValue2);
			}
			public void onNothingSelected(AdapterView<?> parent) 
			{
				//              showToast("Spinner2: unselected");
			}

		});
	}
	private void setContent2(int selectedSpinnerIndex) 
	{              
		Log.i("Debug", "Select spinner index ##### "+selectedSpinnerIndex);

		s3 = (Spinner) findViewById(R.id.spinner3);
		Log.i("Debug", "Select spinner index spinner ::: "+spinner);



		if(spinner){
			switch (selectedSpinnerIndex) {
			case 0:
				//    			adapter1 = ArrayAdapter.createFromResource(this, R.array.entrys2_array,
				//    	                android.R.layout.simple_spinner_item);
				//    	        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				//    			adapter3 = ArrayAdapter.createFromResource(
				//    	                this, R.array.chainage_array, android.R.layout.simple_spinner_item);
				//    	        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				break;
			case 1:
				/*adapter3 = ArrayAdapter.createFromResource(this, R.array.qp1_array,
    	                android.R.layout.simple_spinner_item);
    	        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);*/

				array3 =  getResources().getStringArray(R.array.qp1_array);
				s3.setAdapter(new MyAdapter(PendingWorkDetailSpinner.this, R.layout.spinner_value,array3));

				break;
			case 2:
				/*adapter3 = ArrayAdapter.createFromResource(
    	                this, R.array.qp2_array, android.R.layout.simple_spinner_item);
    	        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);*/

				array3 =  getResources().getStringArray(R.array.qp2_array);
				s3.setAdapter(new MyAdapter(PendingWorkDetailSpinner.this, R.layout.spinner_value,array3));
				break;
			case 3:
				/*adapter3 = ArrayAdapter.createFromResource(this, R.array.qp3_array,
    	                android.R.layout.simple_spinner_item);
    	        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);*/

				array3 =  getResources().getStringArray(R.array.qp3_array);
				s3.setAdapter(new MyAdapter(PendingWorkDetailSpinner.this, R.layout.spinner_value,array3));
				break;
			case 4:
				/*adapter3 = ArrayAdapter.createFromResource(this, R.array.qp4_array,
    	                android.R.layout.simple_spinner_item);
    	        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);*/

				array3 =  getResources().getStringArray(R.array.qp4_array);
				s3.setAdapter(new MyAdapter(PendingWorkDetailSpinner.this, R.layout.spinner_value,array3));

				break;
			case 5:
				/*adapter3 = ArrayAdapter.createFromResource(this, R.array.qp5_array,
    	                android.R.layout.simple_spinner_item);
    	        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);*/

				array3 =  getResources().getStringArray(R.array.qp5_array);
				s3.setAdapter(new MyAdapter(PendingWorkDetailSpinner.this, R.layout.spinner_value,array3));

				break;
			case 6:
				/*adapter3 = ArrayAdapter.createFromResource(this, R.array.qp6_array,
    	                android.R.layout.simple_spinner_item);
    	        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);*/

				array3 =  getResources().getStringArray(R.array.qp6_array);
				s3.setAdapter(new MyAdapter(PendingWorkDetailSpinner.this, R.layout.spinner_value,array3));
				break;
			case 7:
				/*adapter3 = ArrayAdapter.createFromResource(this, R.array.qp7_array,
    	                android.R.layout.simple_spinner_item);
    	        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);*/

				array3 =  getResources().getStringArray(R.array.qp7_array);
				s3.setAdapter(new MyAdapter(PendingWorkDetailSpinner.this, R.layout.spinner_value,array3));

				break;
			case 8:
				/*adapter3 = ArrayAdapter.createFromResource(this, R.array.qp8_array,
    	                android.R.layout.simple_spinner_item);
    	        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);*/

				array3 =  getResources().getStringArray(R.array.qp8_array);
				s3.setAdapter(new MyAdapter(PendingWorkDetailSpinner.this, R.layout.spinner_value,array3));
				break;
			case 9:
				/*adapter3 = ArrayAdapter.createFromResource(this, R.array.qp9_array,
    	                android.R.layout.simple_spinner_item);
    	        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);*/

				array3 =  getResources().getStringArray(R.array.qp9_array);
				s3.setAdapter(new MyAdapter(PendingWorkDetailSpinner.this, R.layout.spinner_value,array3));
				break;
			case 10:
				/*adapter3 = ArrayAdapter.createFromResource(this, R.array.qp10_array,
    	                android.R.layout.simple_spinner_item);
    	        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);*/

				array3 =  getResources().getStringArray(R.array.qp10_array);
				s3.setAdapter(new MyAdapter(PendingWorkDetailSpinner.this, R.layout.spinner_value,array3));
				break;
			case 11:
				/*adapter3 = ArrayAdapter.createFromResource(this, R.array.qp11_array,
    	                android.R.layout.simple_spinner_item);
    	        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);*/

				array3 =  getResources().getStringArray(R.array.qp11_array);
				s3.setAdapter(new MyAdapter(PendingWorkDetailSpinner.this, R.layout.spinner_value,array3));

				break;
			case 12:
				/*adapter3 = ArrayAdapter.createFromResource(this, R.array.qp12_array,
    	                android.R.layout.simple_spinner_item);
    	        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);*/

				array3 =  getResources().getStringArray(R.array.qp12_array);
				s3.setAdapter(new MyAdapter(PendingWorkDetailSpinner.this, R.layout.spinner_value,array3));

				break;
			case 13:
				/*adapter3 = ArrayAdapter.createFromResource(this, R.array.qp13_array,
    	                android.R.layout.simple_spinner_item);
    	        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);*/

				array3 =  getResources().getStringArray(R.array.qp13_array);
				s3.setAdapter(new MyAdapter(PendingWorkDetailSpinner.this, R.layout.spinner_value,array3));
				break;
			case 14:
				/*adapter3 = ArrayAdapter.createFromResource(this, R.array.qp14_array,
    	                android.R.layout.simple_spinner_item);
    	        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);*/

				array3 =  getResources().getStringArray(R.array.qp14_array);
				s3.setAdapter(new MyAdapter(PendingWorkDetailSpinner.this, R.layout.spinner_value,array3));
				break;
			case 15:
				/*adapter3 = ArrayAdapter.createFromResource(this, R.array.qp15_array,
    	                android.R.layout.simple_spinner_item);
    	        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);*/

				array3 =  getResources().getStringArray(R.array.qp15_array);
				s3.setAdapter(new MyAdapter(PendingWorkDetailSpinner.this, R.layout.spinner_value,array3));

				break;
			case 16:
				/*adapter3 = ArrayAdapter.createFromResource(this, R.array.qp16_array,
    	                android.R.layout.simple_spinner_item);
    	        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);*/

				array3 =  getResources().getStringArray(R.array.qp16_array);
				s3.setAdapter(new MyAdapter(PendingWorkDetailSpinner.this, R.layout.spinner_value,array3));

				break;
			case 17:
				/*adapter3 = ArrayAdapter.createFromResource(this, R.array.qp17_array,
    	                android.R.layout.simple_spinner_item);
    	        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);*/

				array3 =  getResources().getStringArray(R.array.qp17_array);
				s3.setAdapter(new MyAdapter(PendingWorkDetailSpinner.this, R.layout.spinner_value,array3));
				break;
			default:
				break;
			}
		}

		//s3.setAdapter(adapter3);
		s3.setOnItemSelectedListener(new OnItemSelectedListener() 
		{
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) 
			{
				position_spinner3 = position;
				if (position>=1)
					showToast("Select Quality Parameter Value");                    
				//SpinnerTextValue3 = s3.getSelectedItem().toString();
				SpinnerTextValue3 = array3[position];
				Log.i("Debug", "Select spinner index @@@ SpinnerTextValue ::: "+SpinnerTextValue3);
			}
			public void onNothingSelected(AdapterView<?> parent) 
			{
				//                  showToast("Spinner3: unselected");
			}
		});

		s4 = (Spinner) findViewById(R.id.spinner4);
		/*ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.qpv_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s4.setAdapter(adapter);*/
		final String array4[] =  getResources().getStringArray(R.array.qpv_array);
		s4.setAdapter(new MyAdapter(PendingWorkDetailSpinner.this, R.layout.spinner_value,array4));
		s4.setOnItemSelectedListener(new OnItemSelectedListener() 
		{
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) 
			{
				position_spinner4 = position;

				if(position == 1)
				{
					//SpinnerTextValue4 = s4.getSelectedItem().toString();
					SpinnerTextValue4 = array4[position];
					Log.i("Debug", "Select spinner index @@@ SpinnerTextValue 44 ::: "+SpinnerTextValue4);
					etext.setVisibility(View.GONE);
				}
				else if (position==2)
				{
					//SpinnerTextValue4 = s4.getSelectedItem().toString();
					SpinnerTextValue4 = array4[position];
					Log.i("Debug", "Select spinner index @@@ SpinnerTextValue 44 ::: "+SpinnerTextValue4);

					showToast("Enter Remarks");
					etext.setVisibility(View.VISIBLE);
					etext.setFocusable(true);
				}

				/*else
                {
                	etext.setVisibility(View.INVISIBLE);
                    if (position>=1)
//                  	showToast("Enter Remarks");                    
                    	SpinnerTextValue4 = s4.getSelectedItem().toString();
                    	Log.i("Debug", "Select spinner index @@@ SpinnerTextValue 44 ::: "+SpinnerTextValue4);
//                		etext.setVisibility(View.VISIBLE);
                 }*/


			}

			public void onNothingSelected(AdapterView<?> parent) 
			{
				//           	showToast("Spinner3: unselected");
				//              etext.setVisibility(View.INVISIBLE);
			}
		});

	}

	public void onClick(View v) 
	{
		if (v.equals(home))
		{			
			Intent Pendingwork = new Intent(PendingWorkDetailSpinner.this,DashBoard.class);
			startActivity(Pendingwork);	
			finish();
		}
		if (v.equals(back))
		{	
			Intent Pendingwork = new Intent(PendingWorkDetailSpinner.this,PendingWork.class);
			//Intent Pendingwork = new Intent(workidspin.this,SingleMenuItemActivity.class);
			startActivity(Pendingwork);
			finish();
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		if (keyCode == KeyEvent.KEYCODE_BACK) 
		{
			Intent Pendingwork = new Intent(PendingWorkDetailSpinner.this,PendingWork.class);
			startActivity(Pendingwork);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}

