package com.fazakis.mobiletransifexclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpMessage;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.json.JSONStringer;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

public class NetworkOperator{
	String apiUrl;
	ConnectivityManager cm;
	
	DefaultHttpClient httpClient;
	
	HttpGet httpGet;
	HttpPost httpPost;
	HttpPut httpPut;
	
	HttpResponse response;
	int responseCode;
	HttpEntity entity;
	String responseString;
	
	public NetworkOperator(String apiUrl){
		this.apiUrl = apiUrl;
		httpClient = new DefaultHttpClient();
		cm = (ConnectivityManager)Mtx.context.getSystemService(Context.CONNECTIVITY_SERVICE);
	}
	
	NetworkResponse getData(String method){		
		if(cm.getActiveNetworkInfo()!=null && cm.getActiveNetworkInfo().isConnected()){
			// Creating HTTP client
			// Creating HTTP Post
			httpGet = new HttpGet(apiUrl+method);	
			httpGet.setHeader("Content-Type", "application/json");
			// Making HTTP Request
			try {
			    response = httpClient.execute(httpGet);		 
			    // writing response to log
			    responseCode = response.getStatusLine().getStatusCode();
			    //Log.d("ME","Response Code:"+responseCode);
			    entity = response.getEntity();
			    responseString = new String(EntityUtils.toString(entity));
			    //Log.d("ME", "Response String:"+responseString);
			    return new NetworkResponse(responseCode,responseString);
			} catch (ClientProtocolException e) {			
			    // writing exception to log
			    e.printStackTrace();		         
			} catch (IOException e) {
			    // writing exception to log
			    e.printStackTrace();
			}
		}
		return null;		
	}
	
	NetworkResponse postData(String method,List<NameValuePair> nameValuePair){
		if(cm.getActiveNetworkInfo()!=null && cm.getActiveNetworkInfo().isConnected()){
			// Creating HTTP client
			httpClient = new DefaultHttpClient();		 
			// Creating HTTP Post
			httpPost = new HttpPost(apiUrl+method);
			httpPost.setHeader("Content-Type", "application/json");
			// Url Encoding the POST parameters
			try {
			    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
			    // Making HTTP Request
				try {
				    response = httpClient.execute(httpPost);		 
				    // writing response to log
				    responseCode = response.getStatusLine().getStatusCode();
				    //Log.d("ME","Response Code:"+responseCode);
				    entity = response.getEntity();
				    responseString = new String(EntityUtils.toString(entity));
				    //Log.d("ME", "Response String:"+responseString);
				    return new NetworkResponse(responseCode,responseString);
				} catch (ClientProtocolException e) {			
				    // writing exception to log
				    e.printStackTrace();		         
				} catch (IOException e) {
				    // writing exception to log
				    e.printStackTrace();
				}
			}
			catch (UnsupportedEncodingException e) {
			    // writing error to Log
			    e.printStackTrace();
			}
		}
		return null;		
	}
	
	//methods with basic auth
	NetworkResponse getData(String method,String user,String password){		
		if(cm.getActiveNetworkInfo()!=null && cm.getActiveNetworkInfo().isConnected()){
			// Creating HTTP client
			// Creating HTTP Post
			httpGet = new HttpGet(apiUrl+method);	
			httpGet.addHeader(BasicScheme.authenticate(
					 new UsernamePasswordCredentials(user, password),
					 "UTF-8", false));
			httpGet.setHeader("Content-Type", "application/json");
			// Making HTTP Request
			try {
			    response = httpClient.execute(httpGet);		 
			    // writing response to log
			    responseCode = response.getStatusLine().getStatusCode();
			    //Log.d("ME","Response Code:"+responseCode);
			    entity = response.getEntity();
			    responseString = new String(EntityUtils.toString(entity));
			    //Log.d("ME", "Response String:"+responseString);
			    return new NetworkResponse(responseCode,responseString);
			} catch (ClientProtocolException e) {			
			    // writing exception to log
			    e.printStackTrace();		         
			} catch (IOException e) {
			    // writing exception to log
			    e.printStackTrace();
			}
		}
		return null;		
	}
	
	NetworkResponse putData(String method,String jsonData,String user,String password){		
		if(cm.getActiveNetworkInfo()!=null && cm.getActiveNetworkInfo().isConnected()){
			// Creating HTTP client
			// Creating HTTP Post
			httpPut = new HttpPut(apiUrl+method);	
			httpPut.addHeader(BasicScheme.authenticate(
					 new UsernamePasswordCredentials(user, password),
					 "UTF-8", false));
			try {
			    entity = new StringEntity(jsonData,"UTF-8");
				httpPut.setEntity(entity);
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			httpPut.setHeader("Content-Type", "application/json");
			// Making HTTP Request
			try {
			    response = httpClient.execute(httpPut);		 
			    // writing response to log
			    responseCode = response.getStatusLine().getStatusCode();
			    //Log.d("ME","Response Code:"+responseCode);
			    entity = response.getEntity();
			    responseString = new String(EntityUtils.toString(entity));
			    //Log.d("ME", "Response String:"+responseString);
			    return new NetworkResponse(responseCode,responseString);
			} catch (ClientProtocolException e) {			
			    // writing exception to log
			    e.printStackTrace();		         
			} catch (IOException e) {
			    // writing exception to log
			    e.printStackTrace();
			}
		}
		return null;		
	}
	
	
}
