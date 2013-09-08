package com.fazakis.mobiletransifexclient;

import org.json.JSONObject;

import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class ViewParameterOnCheckedChangeListener implements OnCheckedChangeListener{
	View parameter1,parameter2,parameter3;
	JSONObject parameter4;
	CheckBox parameter5;
	public ViewParameterOnCheckedChangeListener(View parameter1,View parameter2,View parameter3,JSONObject parameter4,CheckBox parameter5) {
		this.parameter1 = parameter1;
		this.parameter2 = parameter2;
		this.parameter3 = parameter3;
		this.parameter4 = parameter4;
		this.parameter5 = parameter5;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		
	}

}
