package com.fazakis.mobiletransifexclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

public class LoadingProgressbar extends Activity{
	static Boolean alive = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);		
		setContentView(R.layout.activity_loading_progressbar);	
		//while(alive);
		//finish();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	     if (resultCode == 1) 
	        finish();
	     
	  }

}
