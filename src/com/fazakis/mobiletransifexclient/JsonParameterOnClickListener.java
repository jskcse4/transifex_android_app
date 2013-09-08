package com.fazakis.mobiletransifexclient;

import org.json.JSONObject;

import android.view.View;
import android.view.View.OnClickListener;

public class JsonParameterOnClickListener implements OnClickListener {
	JSONObject parameter1,parameter2;
	
	public JsonParameterOnClickListener(JSONObject parameter1,JSONObject parameter2) {
		this.parameter1 = parameter1;
		this.parameter2 = parameter2;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

}
