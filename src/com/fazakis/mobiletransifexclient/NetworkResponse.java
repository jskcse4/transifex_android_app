package com.fazakis.mobiletransifexclient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NetworkResponse {
	int responseCode;
	String responseString;
	JSONObject responseJson;
	JSONArray responseJsonArray;
	
	public NetworkResponse(int responseCode, String responseString){
		this.responseCode = responseCode;
		this.responseString = new String(responseString);
		try{
			this.responseJson = new JSONObject(responseString);
		}catch( JSONException e){
			this.responseJson = null;
			try{
				this.responseJsonArray = new JSONArray(responseString);
			}catch( JSONException f){
				this.responseJsonArray = null;
			}
		}
	}
}
