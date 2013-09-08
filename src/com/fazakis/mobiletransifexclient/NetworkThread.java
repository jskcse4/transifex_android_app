package com.fazakis.mobiletransifexclient;

public class NetworkThread implements Runnable{
	
	static final int RESPONSE_ERROR = 0;
	static final int RESPONSE_SUCCESS = 1;
	static final int RESPONSE_UNDEFINED = 3;
	
	int response = RESPONSE_UNDEFINED;
	NetworkOperator nOp;
	NetworkResponse nr;
	
	//request parameters
	String methodUrl = new String("");
	
	static final int METHOD_GET = 0;
	static final int METHOD_POST = 1;	
	static final int METHOD_PUT = 2;
	int method = METHOD_GET;
	
	Boolean authRequestFlag = true;
	String jsonData;
	//end request parameters
		
	Mtx mtx;
	
	public NetworkThread(String apiUrl,Mtx mtx) {
		nOp=new NetworkOperator(apiUrl);
		this.mtx = mtx;
	}

	@Override
	public void run() {		
		switch(method){
		case METHOD_GET:
			if(authRequestFlag)
				nr = nOp.getData(methodUrl,mtx.user.user,mtx.user.password);
			else
				nr = nOp.getData(methodUrl);
			break;
		case METHOD_PUT:
			nr = nOp.putData(methodUrl,jsonData,mtx.user.user,mtx.user.password);
			break;
		}
		if(nr!=null)
			response = RESPONSE_SUCCESS;
		else
			response = RESPONSE_ERROR;
	}
	
	public void setRequestParameters(int method,String methodUrl,Boolean authRequestFlag){
		resetResponse();
		this.method = method;
		this.methodUrl = methodUrl;
		this.authRequestFlag = authRequestFlag;
	}
	
	public void setRequestParameters(int method,String methodUrl,String jsonData,Boolean authRequestFlag){
		resetResponse();
		this.method = method;
		this.methodUrl = methodUrl;
		this.jsonData = new String(jsonData);
		this.authRequestFlag = authRequestFlag;
	}
	
	public void resetResponse(){
		response = RESPONSE_UNDEFINED;
		nr = null;
	}
	
	public void setApiUrl(String apiUrl){
		nOp.apiUrl = apiUrl;
	}

}
