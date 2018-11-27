package com.example.ruralroadworks;

import android.app.Activity;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class SplashScreen extends Activity 
{
	//Used to check internet Connection..
    ConnectivityManager cm;
    NetworkInfo networkInfo;
    
    /* Location Manager for GPS */
    LocationManager mlocManager=null;
	LocationListener mlocListener;
	
	String conneted;
	
	public void onCreate(Bundle savedInstanceState) 
    {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_screen);
       
        
        final int welcome = 5000;			
	    final Thread thread1 = new Thread()
	    {
				int wait = 0;
				public void run() 
				{
					try 
					{
					    super.run();								
						while (wait < welcome) 
						{
							sleep(100);
							wait += 100;
					    }
					}
					
					catch (Exception e) 
				    {
						System.out.println("EXc=" + e);
					}
					
					finally
					{	
						Intent myIntent = new Intent(SplashScreen.this, LoginScreen.class);
						myIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
						startActivity(myIntent);				
						SplashScreen.this.finish();
						overridePendingTransition(R.anim.fade_in, R.anim.fade_out);	
														  
					}
				}
			};	
			thread1.start();
			
			
        /*cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    networkInfo = cm.getActiveNetworkInfo();
	    
	    
	    mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
	    mlocListener = new MyLocationListener();	        
	    mlocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
	    
	 	if (networkInfo != null && networkInfo.isConnected() && mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) 
	    { 
	 		setContentView(R.layout.splash_screen);
			
			final int welcome = 5000;			
		       final Thread thread1 = new Thread()
		       {
					int wait = 0;
					public void run() 
					{
						try 
						{
						    super.run();								
							while (wait < welcome) 
							{
								sleep(100);
								wait += 100;
						    }
						}
						
						catch (Exception e) 
					    {
							System.out.println("EXc=" + e);
						}
						
						finally
						{	
							Intent myIntent = new Intent(SplashScreen.this, NicsdpActivity2.class);
							myIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
							startActivity(myIntent);				
							SplashScreen.this.finish();
							overridePendingTransition(R.anim.fade_in, R.anim.fade_out);	
															  
						}
					}
				};	
				thread1.start();
	    }
	 	
	 	        	     			   
	 	else if (networkInfo == null && mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER) == false) 
	    {
	 		setContentView(R.layout.network_gps_setting1);
	 		
	 		RelativeLayout rl = (RelativeLayout)findViewById(R.id.setting_layout);
	 		//rl.setGravity(Gravity.CENTER_VERTICAL);
	 		
	 		

	 		TextView tv_msg = new TextView(getApplicationContext());
	 		TextView tv_network = new TextView(getApplicationContext());
	 		TextView tv_gps = new TextView(getApplicationContext());
	 		TextView tv_retry = new TextView(getApplicationContext());
	 		Button but_offline = new Button(getApplicationContext());
	 		
	 		
	 		
	 		TextView tv_msg = (TextView)findViewById(R.id.tv_messageForSetting);
	 		TextView tv_network = (TextView)findViewById(R.id.tv_netWorkSetting);
	 		TextView tv_gps = (TextView)findViewById(R.id.tv_GPS_Setting);
	 		TextView tv_retry = (TextView)findViewById(R.id.tv_reload);
	 		Button but_offline = (Button)findViewById(R.id.but_offlineMode);
	 		
	 		tv_msg.setText("Network and GPS are not available.Please Turn ON Network and GPS");
	 		tv_msg.setTextColor(Color.BLACK);
	 		
	 		tv_network.setText("Network Setting");
	 		tv_network.setTextColor(Color.BLACK);
	 		
	 		
	 		tv_gps.setText("GPS Setting");
	 		tv_gps.setTextColor(Color.BLACK);
	 		
	 		
	 		
	 		tv_retry.setText("Retry");
	 		tv_retry.setTextColor(Color.BLACK);
	 		
	 		
	 		
	 		rl.addView(tv_msg, new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	 		rl.addView(tv_network, new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	 		rl.addView(tv_gps, new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	 		rl.addView(tv_retry, new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	 		
	 		tv_network.setOnClickListener(new OnClickListener() 
	 		{
				
				public void onClick(View v) 
				{
					Intent I = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
	     	        startActivity(I);
				}
			});
	 		
	 		tv_gps.setOnClickListener(new OnClickListener() 
	 		{
				
				public void onClick(View v) 
				{
					Intent I = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
   	     	        startActivity(I);
				}
			});
	 		
	 		tv_retry.setOnClickListener(new OnClickListener() 
	 		{
				
				public void onClick(View v) 
				{
					
				}
			});
	    }
	    
	 	else if (networkInfo == null && mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) 
	    {
	 		setContentView(R.layout.network_gps_setting);
	 		
	 		TextView tv_msg = (TextView)findViewById(R.id.tv_messageForSetting);
	 		TextView tv_network = (TextView)findViewById(R.id.tv_netWorkSetting);
	 		TextView tv_gps = (TextView)findViewById(R.id.tv_GPS_Setting);
	 		TextView tv_retry = (TextView)findViewById(R.id.tv_reload);
	 		Button but_offline = (Button)findViewById(R.id.but_offlineMode);
	 		
	 		tv_msg.setText("Network is not available.Please Turn ON Network");
	 		tv_gps.setVisibility(View.INVISIBLE);
	 		
	 		tv_network.setOnClickListener(new OnClickListener() 
	 		{
				
				public void onClick(View v) 
				{
					Intent I = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
	     	        startActivity(I);
				}
			});
	 		
	 		tv_retry.setOnClickListener(new OnClickListener() 
	 		{
				public void onClick(View v) 
				{
					
				}
			});
	 		
	    }
	 	
	 	else if (networkInfo != null && networkInfo.isConnected() && mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER) == false) 
	    {
	 		setContentView(R.layout.network_gps_setting);
	 		
	 		TextView tv_msg = (TextView)findViewById(R.id.tv_messageForSetting);
	 		TextView tv_network = (TextView)findViewById(R.id.tv_netWorkSetting);
	 		TextView tv_gps = (TextView)findViewById(R.id.tv_GPS_Setting);
	 		TextView tv_retry = (TextView)findViewById(R.id.tv_reload);
	 		Button but_offline = (Button)findViewById(R.id.but_offlineMode);
	 		
	 		tv_msg.setText("Network is not available.Please Turn ON Network");
	 		tv_network.setVisibility(View.INVISIBLE);
	 		
	 		tv_gps.setOnClickListener(new OnClickListener() 
	 		{
				
				public void onClick(View v) 
				{
					Intent I = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	     	        startActivity(I);
				}
			});
	 		
	 		tv_retry.setOnClickListener(new OnClickListener() 
	 		{
				public void onClick(View v) 
				{
					
				}
			});
	    }
	    
		setContentView(R.layout.splash_screen);
		
		final int welcome = 5000;			
	       final Thread thread1 = new Thread()
	       {
				int wait = 0;
				public void run() 
				{
					try 
					{
					    super.run();								
						while (wait < welcome) 
						{
							sleep(100);
							wait += 100;
					    }
					}
					
					catch (Exception e) 
				    {
						System.out.println("EXc=" + e);
					}
					
					finally
					{	
						Intent myIntent = new Intent(SplashScreen.this, NicsdpActivity2.class);
						myIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
						startActivity(myIntent);				
						SplashScreen.this.finish();
						overridePendingTransition(R.anim.fade_in, R.anim.fade_out);	
														  
					}
				}
			};	
			thread1.start();*/
	}
}
