package com.fazakis.mobiletransifexclient;



import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;



public class Mtx extends Activity {
	
	static Context context;
	static Mtx mtx;
	
	Intent activityLoadingProgressbar;
		
	UIManager uiManager;
	User user;
	NetworkThread nt;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);		
		setContentView(R.layout.activity_mtx);	
		
		context = this;
		mtx = this;
								
		//ui_test();
		//network_test();
		
		user = new User(this);
		uiManager = new UIManager(this);		
		nt = new NetworkThread(Transifex.API_URL,this);
		
		user.loadUser();
		
		uiManager.initUI();
		uiManager.initDrawer();
		uiManager.loadUI(UIManager.SCREEN_START);
		//network_test();
				
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mtx, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {	        
	        case R.id.version:
	        	PackageManager manager = this.getPackageManager();
				PackageInfo info;
				try {
					info = manager.getPackageInfo(this.getPackageName(), 0);
					Toast.makeText(this,
			        	     "PackageName = " + info.packageName + "\nVersionCode = "
			        	       + info.versionCode + "\nVersionName = "
			        	       + info.versionName, Toast.LENGTH_LONG).show();
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}	        	
	        	return true;	       
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	public void network_test(){
		uiManager.setLoading();
		NetworkThread nt = new NetworkThread("https://www.transifex.com/api/2/",this);
		new Thread(nt).start();
		while(nt.response==NetworkThread.RESPONSE_UNDEFINED);
		if(nt.response==NetworkThread.RESPONSE_SUCCESS)
			Log.d("ME",nt.nr.responseString);
		else
			Log.d("ME","Network Error!");
		uiManager.unsetLoading();
	}	
	
	public void ui_test(){
		ScrollView sv;
		switch(0){
		case 0:			
			 sv = (ScrollView)findViewById(R.id.content);
			sv.addView(LayoutInflater.from(this).inflate(R.layout.screen_login, sv,false));
			break;
		case 1:			
			sv = (ScrollView)findViewById(R.id.content);
			sv.addView(LayoutInflater.from(this).inflate(R.layout.screen_dashboard, sv,false));
			break;
		case 2:			
			sv = (ScrollView)findViewById(R.id.content);
			sv.addView(LayoutInflater.from(this).inflate(R.layout.tab_project_header, sv,false));
			break;
		case 3:			
			sv = (ScrollView)findViewById(R.id.content);
			sv.addView(LayoutInflater.from(this).inflate(R.layout.screen_project_overview, sv,false));
			break;
		case 4: //language
			sv = (ScrollView)findViewById(R.id.content);
			sv.addView(LayoutInflater.from(this).inflate(R.layout.screen_language_overview, sv,false));
			break;		
		case 5: //language
			sv = (ScrollView)findViewById(R.id.content);
			sv.addView(LayoutInflater.from(this).inflate(R.layout.screen_translate_main, sv,false));
			break;
		}
	}
	
	/*
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev){
	    Boolean result = super.dispatchTouchEvent(ev);
	    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
	    	return uiManager.content.onTouchEvent(ev);
	    else
	    	return result;
	}
	*/
	
	@Override
	public void onBackPressed (){
		if(uiManager.drawer.getVisibility()==View.VISIBLE)
			uiManager.drawer.setVisibility(View.GONE);
		else if(uiManager.currentScreen>UIManager.SCREEN_DASHBOARD)
			uiManager.loadUI(--uiManager.currentScreen);
		else{
			super.onBackPressed();			
			finish();
		}
	}
	
	@Override
    protected void onDestroy() {		
        super.onDestroy();
        //Log.d("ME","ondestroy");
        System.exit(0);
    }
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        /*
        Log.d("ME","onConfigurationChanged called");
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
        	if(uiManager!=null)
        		uiManager.loadUI(uiManager.currentScreen);
        else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
        	if(uiManager!=null)
        		uiManager.loadUI(uiManager.currentScreen);
        		*/        
   }
		

}
