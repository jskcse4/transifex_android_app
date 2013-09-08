package com.fazakis.mobiletransifexclient;

import org.json.JSONArray;

import android.widget.LinearLayout;

public class StringsLazyLoader {
	Boolean lazyLoadingEnabledFlag = true;
	int lastStringLoaded=0;
	LinearLayout stringsList;
	JSONArray Strings;	
	
	public StringsLazyLoader(LinearLayout stringsList, JSONArray Strings,int lastStringLoaded) {
		this.stringsList = stringsList;
		this.Strings = Strings;
		this.lastStringLoaded = lastStringLoaded;
	}

}
