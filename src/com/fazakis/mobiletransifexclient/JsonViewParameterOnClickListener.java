package com.fazakis.mobiletransifexclient;

import org.json.JSONObject;

import android.view.View;
import android.view.View.OnClickListener;

public class JsonViewParameterOnClickListener implements OnClickListener {
	JSONObject parameter1;
	View parameter2,parameter3;
	public JsonViewParameterOnClickListener(JSONObject parameter1,View parameter2,View parameter3) {
		this.parameter1 = parameter1;
		this.parameter2 = parameter2;
		this.parameter3 = parameter3;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

}
