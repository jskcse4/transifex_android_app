package com.fazakis.mobiletransifexclient;

import android.widget.Toast;

public class User extends Networkable{
	Mtx mtx;
	Boolean loggedInFlag = false;
	String user=new String("");
	String password=new String("");
	Project project;
	
	public User(Mtx mtx) {
		super(mtx);
		this.mtx = mtx;
	}
	
	public Boolean login(){
		//Log.d("ME","user.login() called");
		
		//do the request
		mtx.nt.setRequestParameters(NetworkThread.METHOD_GET, "project/foo", true);
		new Thread(mtx.nt).start();
		while(mtx.nt.response==NetworkThread.RESPONSE_UNDEFINED);
		
		//get the response
		if(mtx.nt.response==NetworkThread.RESPONSE_SUCCESS){ //request succeed
			//Log.d("ME","login response:"+mtx.nt.nr.responseString);
			if(!mtx.nt.nr.responseString.equals(Transifex.RESPONSE_BAD_AUTH)){				
				loggedInFlag = true;
				return true;
			}		
		}//else
			//Toast.makeText(mtx, "Request failed!", Toast.LENGTH_LONG).show();
		
		return false;
	}
	
	public void saveUser(){
		//Log.d("ME","user.saveUser() called");
	}
	
	public void loadUser(){
		
	}
	
	public void deleteUser(){
		
	}

}
