package com.example.ruralroadworks;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.rd.DB.PlaceDataSQL;

public class PendingWork extends ListActivity 
{

	TextView pro;
	static final String KEY_ID = "id";
	static final String KEY_WORKID = "workid";
	static final String KEY_VILLAGE = "village";
	static final String KEY_BLOCK = "block";
	static final String KEY_SCHEME = "scheme";
	static final String KEY_FINYR = "financialyear";
	static final String KEY_AGENCY = "agencyname";
	static final String KEY_WORK = "workname";
	private static PlaceDataSQL placeData;

	//Used to check internet Connection..
	ConnectivityManager cm;
	NetworkInfo networkInfo;

	String servicePro;

	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.pending_work);


		placeData = new PlaceDataSQL(this);    
		SQLiteDatabase db = placeData.getWritableDatabase();

		viewVillageDetails();
		db.close();
	}

	

	private void viewVillageDetails() 
	{

		Cursor cursors = getRawEvents("select * from vill_details");
		ArrayList<HashMap<String, String>> menuItems = new ArrayList<HashMap<String, String>>();
		while (cursors.moveToNext())
		{

			String workid = cursors.getString(0);
			String scheme = cursors.getString(1);
			String fin_year = cursors.getString(2);
			String agency = cursors.getString(3);
			String workName = cursors.getString(4);
			String block = cursors.getString(5);
			String vill = cursors.getString(6);	

			HashMap<String, String> map = new HashMap<String, String>();

			// adding each child node to HashMap key => value
			map.put(KEY_WORKID, workid);
			map.put(KEY_VILLAGE, vill);
			map.put(KEY_BLOCK, block);
			map.put(KEY_SCHEME, scheme);
			map.put(KEY_FINYR, fin_year);
			map.put(KEY_AGENCY, agency);
			map.put(KEY_WORK, workName);

			// adding HashList to ArrayList
			menuItems.add(map);

		}	

		cursors.close();

		Cursor cursor = getRawEvents("select * from details");
		while (cursor.moveToNext())
		{
			servicePro = cursor.getString(5);
			pro = (TextView)findViewById(R.id.footertxt);
			pro.setText(servicePro);
			System.out.println("SR "+servicePro);
		}
		cursor.close();
		
		// Adding menuItems to ListView
		ListAdapter adapter = new SimpleAdapter(this, menuItems,R.layout.list_item,new String[] 
				{ 
				KEY_WORKID, 
				KEY_BLOCK, 
				KEY_VILLAGE, 
				KEY_SCHEME, 
				KEY_FINYR, 
				KEY_AGENCY, 
				KEY_WORK
				}, 
				new int[] {
				R.id.workid, 
				R.id.block, 
				R.id.village,
				R.id.scheme, 
				R.id.fyear, 
				R.id.agency, 
				R.id.workname
		});

		setListAdapter(adapter);
		ListView lv = getListView();
		lv.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) 
			{
				// getting values from selected ListItem
				String workid = ((TextView) view.findViewById(R.id.workid)).getText().toString();
				String village = ((TextView) view.findViewById(R.id.village)).getText().toString();
				String block = ((TextView) view.findViewById(R.id.block)).getText().toString();

				String scheme = ((TextView) view.findViewById(R.id.scheme)).getText().toString();
				String financialYear = ((TextView) view.findViewById(R.id.fyear)).getText().toString();
				String agency = ((TextView) view.findViewById(R.id.agency)).getText().toString();
				String WorkName = ((TextView) view.findViewById(R.id.workname)).getText().toString();

				PendingWorkDetailSpinner1.Workid = workid;

				Log.i("Debug", "name :: "+workid);
				Log.i("Debug", "cost :: "+village);
				Log.i("Debug", "description :: "+block);
				Log.i("Debug", "name :: "+scheme);
				Log.i("Debug", "cost :: "+financialYear);
				Log.i("Debug", "description :: "+agency);
				Log.i("Debug", "description :: "+WorkName);

				// 	Starting new intent
				Intent in = new Intent(getApplicationContext(), PendingWorkDetail.class);
				in.putExtra(KEY_WORKID, workid);
				in.putExtra(KEY_VILLAGE, village);
				in.putExtra(KEY_BLOCK, block);
				in.putExtra(KEY_SCHEME, scheme);
				in.putExtra(KEY_FINYR, financialYear);
				in.putExtra(KEY_AGENCY, agency);
				in.putExtra(KEY_WORK, WorkName);
				in.putExtra("service", servicePro);

				startActivity(in);
				finish();

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

	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		if (keyCode == KeyEvent.KEYCODE_BACK) 
		{
			Intent Pendingwork = new Intent(PendingWork.this,DashBoard.class);
			startActivity(Pendingwork);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}