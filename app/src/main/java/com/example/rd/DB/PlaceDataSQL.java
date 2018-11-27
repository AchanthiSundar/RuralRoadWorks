package com.example.rd.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/** Helper to the database, manages versions and creation */
public class PlaceDataSQL extends SQLiteOpenHelper 
{
	private static final String DATABASE_NAME = "employeeDB";
	private static final int DATABASE_VERSION = 1;
	private Context context;
	
	public PlaceDataSQL(Context context) 
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
		
	}

	@Override
	public void onCreate(SQLiteDatabase db) 
	{	
		db.execSQL("CREATE TABLE details (employee_photo BLOB,emp_name varchar(160),emp_designation varchar(160),pendingWorksCount varchar(160),emp_last_visit_date varchar(160),service_provider varchar(160))");
		db.execSQL("CREATE TABLE vill_details (work_id varchar(64),scheme varchar(160),fin_year varchar(64),agencyName varchar(200),workName varchar(600),block varchar(250),village varchar(320))");	
		db.execSQL("CREATE TABLE authentication (mdn varchar(64),pwdtxt varchar(64),imei_no varchar(64),imsi_no varchar(64),phname varchar(128),phversion varchar(32),phmodel varchar(32))");
		db.execSQL("CREATE TABLE stageGroup (stageGroupId varchar(16),stageGroupName varchar(64))");
		db.execSQL("CREATE TABLE stage (stageGroupId varchar(16),stageId varchar(16),stageName varchar(64))");
		db.execSQL("CREATE TABLE qualityParameters (stageGroupId varchar(16),stageId varchar(16),qualityParams varchar(64))");
	}

	private void versionUpdation(SQLiteDatabase db) {

	}

	/**
	 * Check if the database already exist to avoid re-copying the file each
	 * time you open the application.
	 * 
	 * @return true if it exists, false if it doesn't
	 */
	public boolean checkDataBase(String db) {

		SQLiteDatabase checkDB = null;

		try {
			String myPath = "data/data/com.data.pack/databases/" + db;
			checkDB = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.OPEN_READONLY);

		} catch (SQLiteException e) {

			// database does't exist yet.

		} catch (Exception e) {

		}

		if (checkDB != null) {

			checkDB.close();

		}

		return checkDB != null ? true : false;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion >= newVersion)
			return;

		if (oldVersion == 1) {
			Log.d("New Version", "Datas can be upgraded");
		}

		Log.d("Sample Data", "onUpgrade	: " + newVersion);
	}

}
