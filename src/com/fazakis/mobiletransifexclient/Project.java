package com.fazakis.mobiletransifexclient;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import android.widget.Toast;

public class Project extends Networkable{
	Mtx mtx;
	
	String slug = new String("");
	JSONObject projectDetailsJson;
	Map<String,JSONObject> languages;
	//ArrayList Strings;
	
	String selectedLanguageCode;
	String selectedResourceSlug;	
	
	public Project(Mtx mtx) {
		super(mtx);
		this.mtx = mtx;
	}
	
	public Boolean getProject(String slug){
		//Log.d("ME","project.getProject() called");
		this.slug = slug;
		
		NetworkResponse nr = netRequest(NetworkThread.METHOD_GET,"project/"+slug+"/?details",true);
		if(nr!=null){ //request succeed
			//Log.d("ME","getProject response:"+mtx.nt.nr.responseString);
			if(/*!nr.responseString.equals(Transifex.RESPONSE_NOT_FOUND) &&*/ nr.responseJson!=null){	
				projectDetailsJson = nr.responseJson;
				//get
				languages = new HashMap<String,JSONObject>();
				for(int i=0;i<projectDetailsJson.getJSONArray("teams").length();i++){
					String lang = (String)projectDetailsJson.getJSONArray("teams").get(i);
					//Log.d("ME","lang:"+lang);					
					nr = netRequest(NetworkThread.METHOD_GET,"project/"+slug+"/language/"+lang+"/?details",true);
					if(nr!=null){
						//Log.d("ME","getProject response:"+nr.responseString);
						if(!nr.responseString.equals(Transifex.RESPONSE_FORBIDEN))
							languages.put(lang,nr.responseJson);
						else
							languages.put(lang,null);
					}
				}
				return true;
			}		
		}else
			Toast.makeText(mtx, "Request failed!", Toast.LENGTH_LONG).show();
		return false;
	}
	
		
	public JSONObject getResourceStatsByLanguage(String slug,String resource,String languageCode){
		//Log.d("ME","project.getResourceStatsByLanguage() called");
		
		NetworkResponse nr = netRequest(NetworkThread.METHOD_GET
				,"project/"+slug+"/resource/"+resource+"/stats/"+languageCode+"/",true);
		if(nr!=null){//request succeed
			if(!nr.responseString.equals(Transifex.RESPONSE_NOT_FOUND)){
				//Log.d("ME","getResourceStatsByLanguage:"+nr.responseString);
				return nr.responseJson;
			}
		}else
			Toast.makeText(mtx, "Request failed!", Toast.LENGTH_LONG).show();	
		return null;
	}
	
	public JSONArray getResourceStringsByLanguage(String slug,String resource,String languageCode){
		//Log.d("ME","project.getResourceStringsByLanguage() called");
		
		NetworkResponse nr = netRequest(NetworkThread.METHOD_GET
				,"project/"+slug+"/resource/"+resource+"/translation/"+languageCode+"/strings/",true);
		if(nr!=null){//request succeed
			if(!nr.responseString.equals(Transifex.RESPONSE_NOT_FOUND)){
				//Log.d("ME","getResourceStringsByLanguage:"+nr.responseString);
				return nr.responseJsonArray;
			}
		}else
			Toast.makeText(mtx, "Request failed!", Toast.LENGTH_LONG).show();	
		return null;
	}
	
	public String translateString(String sourceString){
		//to simplify the flow we always translate for current source language and for selected language
		//and how we do this? well i'm tired that's why i'm commenting damit
		//Dear memory Translator we all love you thanks!
		//Log.d("ME","project.translateString() called");
		
		//this is also temporary solution this can mess many things!
		mtx.nt.setApiUrl("http://mymemory.translated.net/api/");
		try {
			NetworkResponse nr = netRequest(NetworkThread.METHOD_GET
					,"get?q="+URLEncoder.encode(sourceString,"UTF-8")+"&langpair="
				+URLEncoder.encode(projectDetailsJson.getString("source_language_code")+"|"+this.selectedLanguageCode,"UTF-8"),false);
			mtx.nt.setApiUrl(Transifex.API_URL);
			if(nr!=null){
				//Log.d("ME",nr.responseString);
				if(nr.responseJson!=null)
					return nr.responseJson.getJSONObject("responseData").getString("translatedText");
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mtx.nt.setApiUrl(Transifex.API_URL);
		return new String("Translation failed...");
	}
	
	public String calculateHash(JSONObject string){
		//Log.d("ME","calculatehash for string:"+string.toString());
		List<String> hashList = new ArrayList<String>();
		
		if(string.get("key") instanceof JSONArray ){
			for(int i=0; i<string.getJSONArray("key").length();i++)
				hashList.add(string.getJSONArray("key").getString(i));
		}else{
			hashList.add(string.getString("key"));
		}
		
		if(string.get("context") instanceof JSONArray ){
			for(int i=0; i<string.getJSONArray("context").length();i++)
				hashList.add(string.getJSONArray("context").getString(i));
		}else{
			if(string.getString("context").equals(""))
				hashList.add("");
			else
				hashList.add(string.getString("context"));
		}
		
		StringBuilder builder = new StringBuilder();
		for( String s : hashList) {
	        builder.append( ":");
	        builder.append(s);
	    }
		String concatenation = builder.toString().substring(1);
				
		
		String hash = Transifex.md5(concatenation);
		//Log.d("ME","hash_clean: "+concatenation+" hash: "+hash);		
		return hash;
	}
	
	public Boolean saveString(String hash,String translation){
		NetworkResponse nr = netRequest(NetworkThread.METHOD_PUT
				,"project/"+slug+"/resource/"+selectedResourceSlug+"/translation/"+selectedLanguageCode+"/string/"+hash+"/"
				,"{\"translation\":\""+translation.replace("\"", "\\\"")+"\"}"
				,true);
		/*Log.d("ME","saveString methodURL:"+"project/"+slug+"/resource/"+selectedResourceSlug+"/translation/"+selectedLanguageCode+"/strings/"+hash+"/"
				+" json: "+"{\"translation\":\""+translation+"\",\"reviewed\":"+reviewedFlag+"}"
				);*/
		if(nr!=null){//request succeed
			if(nr.responseString.equals(Transifex.RESPONSE_OK)){
				//Log.d("ME","saveTralsnation response: "+nr.responseString);
				return true;
			}
		}else
			Toast.makeText(mtx, "Request failed!", Toast.LENGTH_LONG).show();
		
		return false;
	}
	
	/*
	 public Boolean saveString(String hash,String translation, Boolean reviewedFlag){
		NetworkResponse nr = netRequest(NetworkThread.METHOD_PUT
				,"project/"+slug+"/resource/"+selectedResourceSlug+"/translation/"+selectedLanguageCode+"/string/"+hash+"/"
				,"{\"translation\":\""+translation.replace("\"", "\\\"")+"\",\"reviewed\":"+reviewedFlag+"}"
				,true);
		//Log.d("ME","saveString methodURL:"+"project/"+slug+"/resource/"+selectedResourceSlug+"/translation/"+selectedLanguageCode+"/strings/"+hash+"/"
		//		+" json: "+"{\"translation\":\""+translation+"\",\"reviewed\":"+reviewedFlag+"}"
		//		);
		if(nr!=null){//request succeed
			if(!nr.responseString.equals(Transifex.RESPONSE_NOT_FOUND)){
				//Log.d("ME","saveTralsnation response: "+nr.responseString);
				return true;
			}
		}else
			Toast.makeText(mtx, "Request failed!", Toast.LENGTH_LONG).show();
		
		return false;
	}*/
	 
	
	public Boolean setReviewedString(String hash,String translation,Boolean reviewedFlag){
		NetworkResponse nr = netRequest(NetworkThread.METHOD_PUT
				,"project/"+slug+"/resource/"+selectedResourceSlug+"/translation/"+selectedLanguageCode+"/string/"+hash+"/"
				,"{\"translation\":\""+translation.replace("\"", "\\\"")+"\",\"reviewed\":"+reviewedFlag+"}"
				,true);
		/*Log.d("ME","saveString methodURL:"+"project/"+slug+"/resource/"+selectedResourceSlug+"/translation/"+selectedLanguageCode+"/strings/"+hash+"/"
				+" json: "+"{\"translation\":\""+translation+"\",\"reviewed\":"+reviewedFlag+"}"
				);*/
		if(nr!=null){//request succeed
			if(nr.responseString.equals(Transifex.RESPONSE_OK)){
				//Log.d("ME","saveTralsnation response: "+nr.responseString);
				return true;
			}
		}else
			Toast.makeText(mtx, "Request failed!", Toast.LENGTH_LONG).show();
		
		return false;
	}
	
}
