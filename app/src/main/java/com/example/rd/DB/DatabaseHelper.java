package com.example.rd.DB;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
	
	public static Object getTotalCount = null; 
	
	public static Object deleteAll;

	static final String dbName="pendingDB";
	public static final String pendingTable="PendingUploads";
	static final String colID="workID";
	static final String colSpinner="spinnerChinage";
	static final String colSGroupName = "SGroupName";
	static final String colStageName="StageName";	
	static final String colQualityParam="QualityParam";
	static final String colQualiParamValue="QualiParamValue";
	static final String colRemarks="Remarks";
	static final String collat="latitude";
	static final String collan="longitude";
	static final String colImgData="Image";
		
	static final String deptTable="Dept";
	static final String colDeptID="DeptID";
	static final String colDeptName="DeptName";
	
	static final String viewPendingUploads ="ViewPUploads";
	
	public DatabaseHelper(Context context) 
	{
		super(context, dbName, null,33);		
	}

	@Override
	public void onCreate(SQLiteDatabase db) 
	{
		
		db.execSQL("CREATE TABLE " + pendingTable + " (id INTEGER PRIMARY KEY,"+colID +" TEXT,"+colSpinner+" TEXT, "+colSGroupName+" TEXT, " +
				""+colStageName+" TEXT, " +
						""+colQualityParam+" TEXT, " +
								""+colQualiParamValue+" TEXT, "+
								""+colRemarks+" TEXT, "+
								""+collat+" TEXT, "+
								""+collan+" TEXT, "+
								""+colImgData+" BLOB)");
		Log.i("Debug", "DB created >>>> "+db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{		
		db.execSQL("DROP TABLE IF EXISTS "+pendingTable);
		onCreate(db);
	}
	
	private SQLiteStatement insertStmt;
	private static final String INSERT = "insert into " + pendingTable + "" +
			"(colID,colSpinner, colSGroupName, colStageName,colQualityParam,colQualiParamValue ) values (?, ?,?, ?,?, ?)";	
	
	public void AddPendingUploads(String workid, String spin,String spin1,String spin2,String spin3,String spin4, String remarks,String lat, String lan, byte[] image)
	{
	 		 
		SQLiteDatabase db= this.getWritableDatabase();		 		
		ContentValues cv=new ContentValues();		
		cv.put(colID, workid);
		cv.put(colSpinner, spin);
		cv.put(colSGroupName,spin1 );		
		cv.put(colStageName,spin2 );
		cv.put(colQualityParam,spin3 );
		cv.put(colQualiParamValue,spin4);
		cv.put(colRemarks,remarks);
		cv.put(collat,lat);
		cv.put(collan,lan);
		cv.put(colImgData,image);		
		db.insert(pendingTable, null, cv);
		db.close();
		
		Log.i("Debug", "DB inserted "+cv.size()+" col id :: "+cv.getAsByteArray(colImgData));
	}
	 void insetData(){
		 SQLiteDatabase db= this.getWritableDatabase();		
		 this.insertStmt = db.compileStatement(INSERT);
		 
		 insertStmt.bindString(1, colID + colSpinner + colSGroupName + colStageName + colQualityParam + colQualityParam+colQualiParamValue);

	      insertStmt.executeInsert();
	      Log.i("Debug", "DB inserted Succccc"); 
	 }
	 
	 Cursor getAllEmployees()
	 {
		 SQLiteDatabase db=this.getWritableDatabase();
		
		 //Cursor cur= db.rawQuery("Select "+colID+" as _id , "+colSpinner+", "+colSGroupName+" from "+pendingTable, new String [] {});
		 Cursor cur= db.rawQuery("SELECT * FROM "+pendingTable,null);
		 return cur;
	 }
	 
	 public List<String> selectAll()
	 {
		 SQLiteDatabase db=this.getReadableDatabase();
	     List<String> list = new ArrayList<String>();
	     Cursor cursor = db.query(pendingTable, new String[] { colID, colSpinner,colSGroupName,colStageName,colQualityParam,colQualiParamValue  }, null, null, null, null, null, null);
	      
	      if (cursor.moveToFirst()) {
	         do {
	            list.add(cursor.getString(0));
	            list.add(cursor.getString(1)); 
	            list.add(cursor.getString(2));
	            list.add(cursor.getString(3));	            
	            list.add(cursor.getString(4));
	            list.add(cursor.getString(5));
	            
	         } while (cursor.moveToNext());
	      }
	      if (cursor != null && !cursor.isClosed()) {
	         cursor.close();
	      }
	      return list;
	   }
	 
	 Cursor getAllDepts()
	 {
		 SQLiteDatabase db=this.getReadableDatabase();
		 Cursor cur=db.rawQuery("SELECT "+colDeptID+" as _id, "+colDeptName+" from "+deptTable,new String [] {});
		 
		 return cur;
	 }
	 
	 void InsertDepts(SQLiteDatabase db)
	 {
		 ContentValues cv=new ContentValues();
			cv.put(colDeptID, 1);
			cv.put(colDeptName, "Sales");
			db.insert(deptTable, colDeptID, cv);
			cv.put(colDeptID, 2);
			cv.put(colDeptName, "IT");
			db.insert(deptTable, colDeptID, cv);
			cv.put(colDeptID, 3);
			cv.put(colDeptName, "HR");
			db.insert(deptTable, colDeptID, cv);
			db.insert(deptTable, colDeptID, cv);
			
	 }
	 
	 public String GetDept(int ID)
	 {
		 SQLiteDatabase db=this.getReadableDatabase();
		 
		 String[] params=new String[]{String.valueOf(ID)};
		 Cursor c=db.rawQuery("SELECT "+colDeptName+" FROM"+ deptTable+" WHERE "+colDeptID+"=?",params);
		 c.moveToFirst();
		 int index= c.getColumnIndex(colDeptName);
		 return c.getString(index);
	 }
	 
	 public Cursor getEmpByDept(String Dept)
	 {
		 SQLiteDatabase db=this.getReadableDatabase();
		 String [] columns=new String[]{"_id",colSpinner,colSGroupName,colDeptName};
		 Cursor c=db.query(viewPendingUploads, columns, colDeptName+"=?", new String[]{Dept}, null, null, null);
		 return c;
	 }
	 
	 public int GetDeptID(String Dept)
	 {
		 SQLiteDatabase db=this.getReadableDatabase();
		 Cursor c=db.query(deptTable, new String[]{colDeptID+" as _id",colDeptName},colDeptName+"=?", new String[]{Dept}, null, null, null);
		 //Cursor c=db.rawQuery("SELECT "+colDeptID+" as _id FROM "+deptTable+" WHERE "+colDeptName+"=?", new String []{Dept});
		 c.moveToFirst();
		 return c.getInt(c.getColumnIndex("_id"));
		 
		 }
	 
//	 public int UpdateEmp(Employee emp)
//	 {
//		 SQLiteDatabase db=this.getWritableDatabase();
//		 ContentValues cv=new ContentValues();
//		 cv.put(colSpinner, emp.getName());
//		 cv.put(colSGroupName, emp.getAge());
//		 cv.put(colStageName, emp.getDept());
//		 return db.update(pendingTable, cv, colID+"=?", new String []{String.valueOf(emp.getID())});
//		 
//	 }
	 
	 public void DeletePendingUploads()
	 {
		 SQLiteDatabase db=this.getWritableDatabase();
		 db.delete(pendingTable, null, null);
		 //db.execSQL("DROP TABLE IF EXISTS "+pendingTable);
	 }
	 
//	public static int deleteAll(){
//		 return SQLiteDatabase.delete(pendingTable, null, null);
//		
//		}
	 

	  public int getTotalCount()
	  {
		  SQLiteDatabase db=this.getWritableDatabase();
		  Cursor cur= db.rawQuery("Select * from "+pendingTable, null);
		  int x= cur.getCount();
		  cur.close();
		  db.close();
		  return x;
	  }

}
