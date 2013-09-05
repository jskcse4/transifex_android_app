package com.fazakis.mobiletransifexclient;

import android.os.Bundle;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ScrollView;

public class Mtx extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);		
		setContentView(R.layout.activity_mtx);	
		
		ui_test();
        
		
	}
	
	public void ui_test(){
		switch(0){
		case 0:			
			ScrollView sv = (ScrollView)findViewById(R.id.content);
			sv.addView(LayoutInflater.from(this).inflate(R.layout.login_screen, sv,false));
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mtx, menu);
		return true;
	}

}
