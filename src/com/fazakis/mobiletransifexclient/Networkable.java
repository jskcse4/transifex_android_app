package com.fazakis.mobiletransifexclient;

public class Networkable {
	Mtx mtx;
	public Networkable(Mtx mtx) {
		this.mtx = mtx;
	}
	
	public NetworkResponse netRequest(int method,String methodUrl,Boolean authRequestFlag){		
		mtx.nt.setRequestParameters(method, methodUrl, authRequestFlag);
		new Thread(mtx.nt).start();
		while(mtx.nt.response==NetworkThread.RESPONSE_UNDEFINED);
		return mtx.nt.nr;
	}
	
	public NetworkResponse netRequest(int method,String methodUrl,String jsonData,Boolean authRequestFlag){
		//mtx.startActivity(mtx.activityLoadingProgressbar);
		mtx.nt.setRequestParameters(method, methodUrl,jsonData, authRequestFlag);
		new Thread(mtx.nt).start();
		while(mtx.nt.response==NetworkThread.RESPONSE_UNDEFINED);
		//LoadingProgressbar.alive=false;
		return mtx.nt.nr;
	}

}
