package com.example.ruralroadworks;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class PendingWorkDetail  extends Activity implements OnClickListener{
	
	TextView pro;
	ImageView home, back;
	// XML node keys
	static final String KEY_WORKID = "workid";
	static final String KEY_VILLAGE = "village";
	static final String KEY_BLOCK = "block";
	static final String KEY_SCHEME = "scheme";
	static final String KEY_FINYR = "financialyear";
	static final String KEY_AGENCY = "agencyname";
	static final String KEY_WORK = "workname";
    static String WorkidText;

	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.pending_work_details);
        initialize();
        
        
       /* pro = (TextView)findViewById(R.id.footertxt);
        pro.setText(""+ServerConnection.xml_serviceProvider.get(0).toString());
        pro.setOnClickListener(this);*/
        
        
        Button clickButton = (Button) findViewById(R.id.button1);
        clickButton.setOnClickListener(new OnClickListener()
        {
        	public void onClick(View v) 
        	{
        		startActivity(new Intent(PendingWorkDetail.this, PendingWorkDetailSpinner1.class));    
        		finish();
        	} 
        });
        
        
        // getting intent dataimageButton2
        Intent in = getIntent();       
        // Get XML values from previous intent
        String workid = in.getStringExtra(KEY_WORKID);
        String village = in.getStringExtra(KEY_VILLAGE);
        String block = in.getStringExtra(KEY_BLOCK);
        String scheme = in.getStringExtra(KEY_SCHEME);
        String financialyear = in.getStringExtra(KEY_FINYR);
        String agencyname = in.getStringExtra(KEY_AGENCY);
        String workname = in.getStringExtra(KEY_WORK);
        String serv = in.getStringExtra("service");
        WorkidText= workid.toString();
        

        Log.i("Debug", "KEY_WORKID :++++++++++++++++++++: "+workid);
        Log.i("Debug", "KEY_VILLAGE :++++++++++++++++++: "+village);
        Log.i("Debug", "KEY_BLOCK :+++++++++++++++++++++: "+block);   
        Log.i("Debug", "KEY_WORKID :++++++++++++1++++++++: "+scheme);
        Log.i("Debug", "KEY_VILLAGE :++++++++2++++++++++: "+financialyear);
        Log.i("Debug", "KEY_BLOCK :+++++++++++++3++++++++: "+agencyname);
        Log.i("Debug", "KEY_WORKID :+++++++++++++4+++++++: "+workname);
        
        // Displaying all values on the screen
        TextView lblworkid = (TextView) findViewById(R.id.textView1);
        TextView lblvillage = (TextView) findViewById(R.id.textView16);
        TextView lblblock = (TextView) findViewById(R.id.textView15);
        TextView lblscheme = (TextView) findViewById(R.id.textView11);
        TextView lblfinancialyear = (TextView) findViewById(R.id.textView12);
        TextView lblagencyname = (TextView) findViewById(R.id.textView13);
        TextView lblworkname = (TextView) findViewById(R.id.textView14);
        pro = (TextView)findViewById(R.id.footertxt);
        
        lblworkid.setText("WorkID :"+workid);
        lblvillage.setText(village);
        lblblock.setText(block);
        lblscheme.setText(scheme);
        lblfinancialyear.setText(financialyear);
        lblagencyname.setText(agencyname);
        lblworkname.setText(workname);
        pro.setText(serv);
        

    }
	
	private void initialize() 
	{
		home = (ImageView) findViewById(R.id.homeimg);
		home.setOnClickListener(this); 
		
		back = (ImageView) findViewById(R.id.backimg);
		back.setOnClickListener(this);
		
	}

	public void onClick(View v)
	{
		if (v.equals(home))
		{			
			Intent Pendingwork = new Intent(PendingWorkDetail.this,DashBoard.class);
			startActivity(Pendingwork);		
			finish();
		}
				
		if (v.equals(back))
		{	
			Intent Pendingwork = new Intent(PendingWorkDetail.this,PendingWork.class);
			startActivity(Pendingwork);
			finish();
		}
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
	    if (keyCode == KeyEvent.KEYCODE_BACK) 
	    {
	    	Intent Pendingwork = new Intent(PendingWorkDetail.this,PendingWork.class);
			startActivity(Pendingwork);
			finish();
	    }
	    return super.onKeyDown(keyCode, event);
	}
}
