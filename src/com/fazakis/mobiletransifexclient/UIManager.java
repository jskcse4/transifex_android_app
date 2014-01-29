package com.fazakis.mobiletransifexclient;



import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class UIManager implements ScrollViewListener{
		
	static final int SCREEN_START = 0;
	static final int SCREEN_LOGIN = 1;
	static final int SCREEN_DASHBOARD = 2;
	static final int SCREEN_PROJECT = 3;
	static final int SCREEN_LANGUAGE = 4;
	static final int SCREEN_TRANSLATE = 5;
	
	static final int UI_STATUS_LOCKED = -1;
	static final int UI_STATUS_IDLE = 0;	
	static final int UI_STATUS_REQUEST_SUCCEED = 1;
	static final int UI_STATUS_REQUEST_FAILED = 2;
	
	int uiStatus = UI_STATUS_IDLE;
		
	Mtx mtx;
	int currentScreen=0;
	
	InputMethodManager keyboard;    
	
	RelativeLayout drawer;
	ObservableScrollView content;
	ImageView drawerButton,gravatar,logo;
	TextView email;
	static ProgressBar loading;	
	
	//drawer loggedout
	Button drawerSignupButton;
	
	//drawer loggedin
	Button drawerDashboardButton,drawerLogoutButton;	
		
	//screen_login
	EditText loginEmail,loginPassword;
	Button loginButton;
	CheckBox rememberMeCheckBox;
	
	//screen_dashboard
	EditText slug;
	Button go;
	
	//screen_project
	TextView projectName,projectDescription,projectDetails;
	LinearLayout languagesList;
	RadioButton projectResourcesButton;
	
	//screen_language
	TextView languageCode,languageName;
	LinearLayout resourcesList;
	RadioButton languageMembersButton;
	
	//screen_translate
	TextView resourceName;
	LinearLayout stringsList;
	Button translateTranslationSettingsButton;
	StringsLazyLoader lazyLoader;
	
	//uiHandler for Async ui updating
	int uiUpdateTime = 1; //seconds
	final Handler uiHandler = new Handler();
	final Runnable uiRunnable = new Runnable() {
	   @Override
	   public void run() {		   
		   updateUI();
		   uiHandler.postDelayed(this, uiUpdateTime*1000);
	   }
	};
	//uiHandler end
			
	public UIManager(Mtx mtx) {
		this.mtx = mtx;
	}
	
	public void initUI(){
		keyboard = (InputMethodManager)mtx.getSystemService(Mtx.INPUT_METHOD_SERVICE);
		drawer = (RelativeLayout)mtx.findViewById(R.id.drawer);
		email = (TextView)mtx.findViewById(R.id.email);
		content = (ObservableScrollView)mtx.findViewById(R.id.content);
		content.setScrollViewListener(this);		
		content.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
		content.setFocusable(true);
		content.setFocusableInTouchMode(true);
		content.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				arg0.requestFocusFromTouch();	
				arg0.setSelected(true);				
				return false;
			}
	    });
		drawerButton = (ImageView)mtx.findViewById(R.id.drawerButton);		
		gravatar = (ImageView)mtx.findViewById(R.id.gravatar);
		logo = (ImageView)mtx.findViewById(R.id.logo);
		loading = (ProgressBar)mtx.findViewById(R.id.loading);
		uiHandler.postDelayed(uiRunnable, uiUpdateTime*1000);
	}
	
	public void initDrawer(){		
		content.setOnTouchListener(new OnSwipeTouchListener() {
		    public void onSwipeTop() {
		        //Toast.makeText(Mtx.this, "top", Toast.LENGTH_SHORT).show();		        
		    }
		    public void onSwipeRight() {
		       // Toast.makeText(Mtx.this, "right", Toast.LENGTH_SHORT).show();
		        drawer.setVisibility(View.VISIBLE);
		    }
		    public void onSwipeLeft() {
		        //Toast.makeText(Mtx.this, "left", Toast.LENGTH_SHORT).show();
		        drawer.setVisibility(View.GONE);
		    }
		    public void onSwipeBottom() {
		        //Toast.makeText(Mtx.this, "bottom", Toast.LENGTH_SHORT).show();
		    }
		});		
		drawerButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				if(drawer.getVisibility()==View.GONE)
					drawer.setVisibility(View.VISIBLE);
				else
					drawer.setVisibility(View.GONE);
		}}); 
	}
	
	public void loadDrawerMenu(Boolean userLoggedinFlag){
		drawer.setVisibility(View.GONE);
		drawer.removeAllViews();
		if(!userLoggedinFlag){
			drawer.addView(LayoutInflater.from(mtx).inflate(R.layout.drawer_menu, content,false));
			Button drawerSignupButton = (Button)mtx.findViewById(R.id.drawerSignupButton);
			
			drawerSignupButton.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {					
					Intent browserIntent = new Intent(android.content.Intent.ACTION_VIEW,
					                        Uri.parse(Transifex.REGISTER_URL));	
					mtx.startActivity(browserIntent);
					keyboard.hideSoftInputFromWindow(arg0.getWindowToken(),0);
					drawer.setVisibility(View.GONE);
				}				
			});
		}else{
			drawer.addView(LayoutInflater.from(mtx).inflate(R.layout.drawer_menu_loggedin, content,false));
			Button drawerDashboardButton = (Button)mtx.findViewById(R.id.drawerDashboardButton);
			Button drawerLogoutButton = (Button)mtx.findViewById(R.id.drawerLogoutButton);
	
			drawerDashboardButton.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					keyboard.hideSoftInputFromWindow(arg0.getWindowToken(),0);
					loadUI(SCREEN_DASHBOARD);					
				}				
			});
			drawerLogoutButton.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					mtx.user = new User(mtx);
					keyboard.hideSoftInputFromWindow(arg0.getWindowToken(),0);
					loadUI(SCREEN_LOGIN);					
				}				
			});
		}
	}
	
	public void loadUI(int ui){
		switch(ui){
		case SCREEN_START:
			if(!mtx.user.loggedInFlag)
				loadUI(SCREEN_LOGIN);
			else
				loadUI(SCREEN_DASHBOARD);
			break;
		case SCREEN_LOGIN:
			currentScreen = SCREEN_LOGIN;
			
			logo.setVisibility(View.VISIBLE);
			email.setVisibility(View.GONE);
			gravatar.setVisibility(View.GONE);
			
			content.removeAllViews();
			content.addView(LayoutInflater.from(mtx).inflate(R.layout.screen_login, content,false));
			loadDrawerMenu(false);
			
			rememberMeCheckBox = (CheckBox)mtx.findViewById(R.id.rememberMeCheckBox);
			rememberMeCheckBox.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {					
					Toast.makeText(mtx, "Not implemented yet...", Toast.LENGTH_LONG).show();
				}				
			});
			
			//listeners here
			loginEmail = (EditText)mtx.findViewById(R.id.user);
			loginPassword = (EditText)mtx.findViewById(R.id.loginPassword);
			rememberMeCheckBox = (CheckBox)mtx.findViewById(R.id.rememberMeCheckBox);
			loginButton = (Button)mtx.findViewById(R.id.loginButton);
			loginButton.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {					
					//Log.d("ME","Login Button Clicked!");					
					mtx.user.user = loginEmail.getText().toString();
					mtx.user.password = loginPassword.getText().toString();
					new AsyncLogin().execute(null, null, null);
					keyboard.hideSoftInputFromWindow(arg0.getWindowToken(),0);					
				}			
			});	
			
						
			
			break;
		case SCREEN_DASHBOARD:
			currentScreen = SCREEN_DASHBOARD;
			
			//Log.d("ME","loading dashboard");
			logo.setVisibility(View.GONE);
			email.setText(mtx.user.user);
			email.setVisibility(View.VISIBLE);
			gravatar.setVisibility(View.VISIBLE);
			new DownloadImageTask((ImageView)mtx.findViewById(R.id.gravatar))
            .execute("http://www.gravatar.com/avatar/"+Transifex.md5(mtx.user.user.toLowerCase()));
			
			
			content.removeAllViews();
			content.addView(LayoutInflater.from(mtx).inflate(R.layout.screen_dashboard, content,false));			
			loadDrawerMenu(true);
			
			//listeners
			slug = (EditText)mtx.findViewById(R.id.slug);
			//slug.requestFocus(View.FOCUS_DOWN);
			go = (Button)mtx.findViewById(R.id.go);
			go.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {					
					mtx.user.project = new Project(mtx);					
					new AsyncGetProject().execute(null, null, null);
					Toast.makeText(mtx, "This might take a while...", Toast.LENGTH_SHORT).show();
					keyboard.hideSoftInputFromWindow(arg0.getWindowToken(),0);
				}				
			});
			
			break;
		case SCREEN_PROJECT:
			currentScreen = SCREEN_PROJECT;
			
			content.removeAllViews();
			content.addView(LayoutInflater.from(mtx).inflate(R.layout.screen_project_overview, content,false));
			
			projectName = (TextView)mtx.findViewById(R.id.projectProjectName);
			projectName.setText(mtx.user.project.projectDetailsJson.getString("name"));
			
			projectDescription = (TextView)mtx.findViewById(R.id.projectProjectDescription);
			projectDescription.setText(mtx.user.project.projectDetailsJson.getString("description"));
			
			projectDetails = (TextView)mtx.findViewById(R.id.projectProjectDetails);
			projectDetails.setText(mtx.user.project.projectDetailsJson.getString("long_description"));
			
			projectResourcesButton = (RadioButton)mtx.findViewById(R.id.projectResourcesButton);
			projectResourcesButton.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {					
					Toast.makeText(mtx, "Not implemented yet...", Toast.LENGTH_LONG).show();
				}				
			});
			
			languagesList = (LinearLayout)mtx.findViewById(R.id.languagesList);
			LinearLayout sourceLanguage = (LinearLayout)LayoutInflater.from(mtx).inflate(R.layout.element_list_item, languagesList,false);
			setLanguagesListItemProperties(sourceLanguage
					,mtx.user.project.projectDetailsJson.getString("source_language_code"),100);
			languagesList.addView(sourceLanguage);
			
			Iterator<Entry<String, JSONObject>> languagesIterator = mtx.user.project.languages.entrySet().iterator();
			while(languagesIterator.hasNext()){
				Map.Entry<String, JSONObject> mEntry = (Map.Entry<String, JSONObject>) languagesIterator.next();
				LinearLayout languageItem = (LinearLayout)LayoutInflater.from(mtx).inflate(R.layout.element_list_item, languagesList,false);
				int percent = -1;
				if(mEntry.getValue()!=null)
					percent = (int) (((double)mEntry.getValue().getInt("translated_segments")/mEntry.getValue().getInt("total_segments"))*100);
				setLanguagesListItemProperties(languageItem,mEntry.getKey(),percent);
				languagesList.addView(languageItem);
			}
			
			content.scrollTo(0, 0);
			break;
		case SCREEN_LANGUAGE:
			currentScreen = SCREEN_LANGUAGE;
			
			content.removeAllViews();			
			content.addView(LayoutInflater.from(mtx).inflate(R.layout.screen_language_overview, content,false));
			
			projectName = (TextView)mtx.findViewById(R.id.languageProjectName);
			projectName.setText(mtx.user.project.projectDetailsJson.getString("name"));
			
			languageName = (TextView)mtx.findViewById(R.id.languageLanguageName);
			languageName.setText(Transifex.languageCodeMap(mtx.user.project.selectedLanguageCode));
			
			projectDescription = (TextView)mtx.findViewById(R.id.languageProjectDescription);
			projectDescription.setText(mtx.user.project.projectDetailsJson.getString("description"));
			
			languageCode = (TextView)mtx.findViewById(R.id.languageLanguageCode);
			languageCode.setText(mtx.user.project.selectedLanguageCode);  
			
			languageMembersButton = (RadioButton)mtx.findViewById(R.id.languageMembersButton);
			languageMembersButton.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {					
					Toast.makeText(mtx, "Not implemented yet...", Toast.LENGTH_LONG).show();
				}				
			});
			
			resourcesList = (LinearLayout)mtx.findViewById(R.id.resourcesList);
			
			Iterator<Entry<JSONObject, JSONObject>> selectedLanguageResources = mtx.user.project.selectedLanguageResources.entrySet().iterator();
			//JSONArray resources = mtx.user.project.projectDetailsJson.getJSONArray("resources");
			//for(int i=0;i<resources.length();i++){
			while(selectedLanguageResources.hasNext()){
				Map.Entry<JSONObject, JSONObject> mEntry = (Map.Entry<JSONObject, JSONObject>) selectedLanguageResources.next();
				LinearLayout resourceItem = (LinearLayout)LayoutInflater.from(mtx).inflate(R.layout.element_list_item, resourcesList,false);
				setResourceListItemProperties(resourceItem,mEntry.getKey(),mEntry.getValue());				
				resourcesList.addView(resourceItem);
			}
			content.scrollTo(0, 0);
			break;
		case SCREEN_TRANSLATE:
			currentScreen = SCREEN_TRANSLATE;
			
			content.removeAllViews();			
			content.addView(LayoutInflater.from(mtx).inflate(R.layout.screen_translate_main, content,false));
			
			projectName = (TextView)mtx.findViewById(R.id.translateProjectName);
			projectName.setText(mtx.user.project.projectDetailsJson.getString("name"));
			
			resourceName = (TextView)mtx.findViewById(R.id.translateResourceName);
			resourceName.setText(mtx.user.project.selectedResourceSlug); //This Should Be name not slug fix it later
									
			languageName = (TextView)mtx.findViewById(R.id.translateLanguageName);
			languageName.setText(Transifex.languageCodeMap(mtx.user.project.selectedLanguageCode));
			
			languageCode = (TextView)mtx.findViewById(R.id.translateLanguageCode);
			languageCode.setText("("+mtx.user.project.selectedLanguageCode+")");
			
			translateTranslationSettingsButton = (Button)mtx.findViewById(R.id.translateTranslationSettingsButton);
			translateTranslationSettingsButton.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {					
					Toast.makeText(mtx, "Not implemented yet...", Toast.LENGTH_LONG).show();
				}				
			});
			
			
			stringsList = (LinearLayout)mtx.findViewById(R.id.stringsList);			
			JSONArray Strings = mtx.user.project.selectedResourceStrings;
			int i;						
			for(i=0;i<Strings.length();i++){
				RelativeLayout stringBox = (RelativeLayout)LayoutInflater.from(mtx).inflate(R.layout.tab_translate_box, stringsList,false);
				setTranslateBoxItemProperties(mtx,stringBox,Strings.getJSONObject(i),stringsList); // The all app is about this LITLE FUNCTION! lets give it full SCOPE!				
				stringsList.addView(stringBox);
				if(i>15) break; // lazyloading
			}
			lazyLoader = new StringsLazyLoader(stringsList,Strings,i-1);			
						
			//content.addView(LayoutInflater.from(mtx).inflate(R.layout.element_space, null));
			content.scrollTo(0, 0);
			break;
		}		
	}
	
	public void setTranslateBoxItemProperties(Mtx mtx,RelativeLayout stringBox,JSONObject string,LinearLayout stringsList){ //precious
		//source string at 1 1 2
		//translation string 1 1 5
		EditText sourceString = (EditText)((LinearLayout)(((LinearLayout)((RelativeLayout)stringBox).getChildAt(1)).getChildAt(1))).getChildAt(2);
		EditText translationString = (EditText)((LinearLayout)(((LinearLayout)((RelativeLayout)stringBox).getChildAt(1)).getChildAt(1))).getChildAt(5);
		//status line 1 0
		RelativeLayout statusLine = (RelativeLayout)   ((LinearLayout)(((RelativeLayout)stringBox).getChildAt(1))).getChildAt(0);
		//close 
		TextView closeButton = (TextView) ((RelativeLayout)stringBox).getChildAt(0);
		//auto 1 1 6 0
		Button autoTranslateButton = (Button) ((RelativeLayout)((LinearLayout)(((LinearLayout)((RelativeLayout)stringBox).getChildAt(1)).getChildAt(1))).getChildAt(6)).getChildAt(0);
		//reviewed 1 1 6 1 0
		CheckBox reviewedButton = (CheckBox) ((LinearLayout)((RelativeLayout)((LinearLayout)(((LinearLayout)((RelativeLayout)stringBox).getChildAt(1)).getChildAt(1))).getChildAt(6)).getChildAt(1)).getChildAt(0);
		//save 1 1 6 1 1
		Button saveButton = (Button) ((LinearLayout)((RelativeLayout)((LinearLayout)(((LinearLayout)((RelativeLayout)stringBox).getChildAt(1)).getChildAt(1))).getChildAt(6)).getChildAt(1)).getChildAt(1);

		if(string.getString("reviewed").equals("true")){
			statusLine.setBackgroundColor(mtx.getResources().getColor(R.color.reviewed));
			reviewedButton.setChecked(true);
		}else if(!string.getString("translation").equals(""))
			statusLine.setBackgroundColor(mtx.getResources().getColor(R.color.translated));
		else
			statusLine.setBackgroundColor(mtx.getResources().getColor(R.color.untranslated));
		
		sourceString.setText(string.getString("source_string"));
		try{ // IF ITS JSONOBJECT THEN ITS PLURAL! ELSE ITS STRING!
			new JSONObject(string.getString("source_string"));
			translationString.setText("mTx does not support plural forms yet!");
			translationString.setEnabled(false);
		}catch(JSONException e){
			translationString.setText(string.getString("translation"));			
		}
			
	
		//these and below need clearance cause i'm fucking tired!
		closeButton.setOnClickListener(new ViewParameterOnClickListener(stringsList,stringBox){
			@Override
			public void onClick(View arg0) {
				parameter2.requestFocus();
				Toast.makeText(Mtx.context, "StringBox closed!", Toast.LENGTH_LONG).show();
				((ViewGroup) parameter1).removeView(parameter2);
				//keyboard.hideSoftInputFromWindow(arg0.getWindowToken(),0);
			}
		});
		
		
		autoTranslateButton.setOnClickListener(new ViewParameterOnClickListener(sourceString,translationString){
			@Override
			public void onClick(View arg0) {				
				parameter2.requestFocus();
				String translation = Mtx.mtx.user.project.translateString(((EditText)parameter1).getText().toString());
				((EditText)parameter2).setText(translation);
				//keyboard.hideSoftInputFromWindow(arg0.getWindowToken(),0);
			}			
		});
		
		saveButton.setOnClickListener(new JsonViewParameterOnClickListener(string,translationString,reviewedButton){
			@Override
			public void onClick(View arg0) {				
				parameter2.requestFocus();
				String hash = Mtx.mtx.user.project.calculateHash(parameter1);
				if(Mtx.mtx.user.project.saveString(
							hash,
							((EditText)parameter2).getText().toString()
						))
					Toast.makeText(Mtx.mtx, "Translation Saved!", Toast.LENGTH_LONG).show();
				else
					Toast.makeText(Mtx.mtx, "Save Failed!", Toast.LENGTH_LONG).show();
				//keyboard.hideSoftInputFromWindow(arg0.getWindowToken(),0);
			}
		});
		
		reviewedButton.setOnCheckedChangeListener(new ViewParameterOnCheckedChangeListener(saveButton,translationString,statusLine,string,reviewedButton) {
			  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {				  
				  parameter2.requestFocus();
				  //((Button)parameter1).callOnClick(); android 2 crashes with this
				  //so we copy paste save onclicklistener
				  String hash = Mtx.mtx.user.project.calculateHash(parameter4);
				  if(Mtx.mtx.user.project.setReviewedString(
								hash,								
								((EditText)parameter2).getText().toString(),
								((CheckBox)parameter5).isChecked()								
							))
					  	if(isChecked)
					  		Toast.makeText(Mtx.mtx, "Translation approved!", Toast.LENGTH_LONG).show();
					  	else
					  		Toast.makeText(Mtx.mtx, "Translation disapproved!", Toast.LENGTH_LONG).show();
				  else{
						Toast.makeText(Mtx.mtx, "Review Failed!", Toast.LENGTH_LONG).show();
						buttonView.setChecked(!isChecked);
				  }
				  //end copy paste
				  if(isChecked)
					  parameter3.setBackgroundColor(Mtx.mtx.getResources().getColor(R.color.reviewed));
				  else if( !((EditText)parameter2).getText().toString().equals("") )
					  parameter3.setBackgroundColor(Mtx.mtx.getResources().getColor(R.color.translated));
				  else
					  parameter3.setBackgroundColor(Mtx.mtx.getResources().getColor(R.color.untranslated));
				  //keyboard.hideSoftInputFromWindow(buttonView.getWindowToken(),0);
			  }
		  });
		//end dirty
	}
	
	public void setResourceListItemProperties(LinearLayout item,JSONObject resource,JSONObject resourceStats){
		//JSONObject resourceStats = mtx.user.project.getResourceStatsByLanguage(mtx.user.project.slug, 
				//resource.getString("slug"), mtx.user.project.selectedLanguageCode);
		if(resourceStats==null)
			return;
		
		((TextView)((RelativeLayout)item.getChildAt(0)).getChildAt(0)).setText(resource.getString("name"));
		//Log.d("ME","percent_clean:"+resourceStats.getString("completed").substring(0, resourceStats.getString("completed").length()-2));
		((ProgressBar)((RelativeLayout)item.getChildAt(0)).getChildAt(1)).setProgress(Integer.parseInt(resourceStats.getString("completed").substring(0, resourceStats.getString("completed").length()-1)));
		((TextView)((LinearLayout)((RelativeLayout)item.getChildAt(0)).getChildAt(2)).getChildAt(0)).setText(resourceStats.getString("completed"));
		item.getChildAt(0).setOnClickListener(new JsonParameterOnClickListener(resource,resourceStats){
			@Override
			public void onClick(View arg0) {				
				mtx.user.project.selectedResourceSlug = parameter1.getString("slug");
				//Log.d("ME","selectedResourceSlug:"+mtx.user.project.selectedResourceSlug);
				resourceDialog(parameter1,parameter2);				
				Toast.makeText(mtx, "Resource Selected", Toast.LENGTH_LONG).show();
			}			
		});
	}
	
	public void resourceDialog(JSONObject resource,JSONObject resourceStats){		
		LayoutInflater factory = LayoutInflater.from(mtx);
	    final View dialogLayout = factory.inflate(R.layout.dialog_resource_translate, null);
	    //setupDailogContent	    
	    TextView dialogResourceTotal,dialogResourceTranslated,dialogResourceRemaining,dialogResourceReviewed;
	    dialogResourceTotal = (TextView)dialogLayout.findViewById(R.id.dialogResourceTotal);
	    dialogResourceTranslated = (TextView)dialogLayout.findViewById(R.id.dialogResourceTranslated);
	    dialogResourceRemaining = (TextView)dialogLayout.findViewById(R.id.dialogResourceRemaining);
	    dialogResourceReviewed = (TextView)dialogLayout.findViewById(R.id.dialogResourceReviewed);
	    
	    dialogResourceTotal.setText(
	    		(resourceStats.getInt("untranslated_entities")+resourceStats.getInt("translated_entities"))
	    		+" strings,"
	    		+(resourceStats.getInt("untranslated_words")+resourceStats.getInt("translated_words"))
	    		+" words"
	    		);	    
	    dialogResourceTranslated.setText(
	    		resourceStats.getInt("translated_entities")
	    		+" strings,"
	    		+resourceStats.getInt("translated_words")
	    		+" words"
	    		);
	    dialogResourceRemaining.setText(
	    		resourceStats.getInt("untranslated_entities")
	    		+" strings ("
	    		+resourceStats.getInt("untranslated_words")
	    		+" words)"
	    		);
	    dialogResourceReviewed.setText(
	    		resourceStats.getInt("reviewed")
	    		+" strings"
	    		);
	    
		AlertDialog.Builder builder = new AlertDialog.Builder(mtx);
		builder.setTitle("Resource: "+resource.getString("name"));		
		builder.setView(dialogLayout);
		builder.setPositiveButton("Translate", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {		        	   
		        	   new AsyncGetResourceStrings().execute(null, null, null);		        	   
		           }
		       });
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		               dialog.dismiss();
		           }
		       });
		// Create the AlertDialog
		AlertDialog dialog = builder.create();
		dialog.show();
	}
	
	public void setLanguagesListItemProperties(LinearLayout item,String itemLanguageCode,int itemPercent){		
		((TextView)((RelativeLayout)item.getChildAt(0)).getChildAt(0)).setText(Transifex.languageCodeMap(itemLanguageCode));
		((ProgressBar)((RelativeLayout)item.getChildAt(0)).getChildAt(1)).setProgress(itemPercent>=0?itemPercent:0);
		((TextView)((LinearLayout)((RelativeLayout)item.getChildAt(0)).getChildAt(2)).getChildAt(0)).setText((itemPercent>=0?itemPercent:"n/a")+"%");
		item.getChildAt(0).setOnClickListener(new StringParameterOnClickListener(itemLanguageCode){
			@Override
			public void onClick(View arg0) {
				mtx.user.project.selectedLanguageCode = parameter;
				new AsyncGetResourcesByLanguage().execute(null, null, null);
				//content.requestFocusFromTouch();
			}			
		});
	}
	
	public void addTeamToList(LinearLayout languagesList){
		LinearLayout item = (LinearLayout)LayoutInflater.from(mtx).inflate(R.layout.element_list_item, content,false);
		languagesList.addView(item);
	}
	
	public void setLoading(){
		loading.setVisibility(View.VISIBLE);
	}
	
	public void unsetLoading(){
		loading.setVisibility(View.GONE);
	}
	
	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
	    ImageView bmImage;

	    public DownloadImageTask(ImageView bmImage) {
	        this.bmImage = bmImage;
	    }

	    protected Bitmap doInBackground(String... urls) {
	        String urldisplay = urls[0];
	        Bitmap mIcon11 = null;
	        try {
	            InputStream in = new java.net.URL(urldisplay).openStream();
	            mIcon11 = BitmapFactory.decodeStream(in);
	        } catch (Exception e) {
	            Log.e("Error", e.getMessage());
	            e.printStackTrace();
	        }
	        return mIcon11;
	    }

	    protected void onPostExecute(Bitmap result) {
	        bmImage.setImageBitmap(result);
	    }
	}
	
	public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
		//if(Math.abs(x-oldx)>40){
			//content.requestFocus();
			//keyboard.hideSoftInputFromWindow(content.getWindowToken(),0);
		//}		
		//Log.d("ME","onScrollChanged() called");
		if(currentScreen==SCREEN_TRANSLATE && scrollView == content){ // we're gonna create lazyloading for stringBoxes
			//Log.d("ME","stringsList height:"+ lazyLoader.stringsList.getHeight());			
			if(y>lazyLoader.stringsList.getHeight()*0.8 && lazyLoader.lazyLoadingEnabledFlag){
				lazyLoader.lazyLoadingEnabledFlag = false;				
				if(lazyLoader.lastStringLoaded+1<lazyLoader.Strings.length()){
					//Log.d("ME","lazyloading running");
					content.requestFocus();
					//keyboard.hideSoftInputFromWindow(content.getWindowToken(),0);
					//Toast tLoading = Toast.makeText(mtx, "Loading Strings", Toast.LENGTH_SHORT);
					//tLoading.show();
					int i;						
					for(i=lazyLoader.lastStringLoaded+1;i<lazyLoader.Strings.length();i++){
						RelativeLayout stringBox = (RelativeLayout)LayoutInflater.from(mtx).inflate(R.layout.tab_translate_box, lazyLoader.stringsList,false);
						setTranslateBoxItemProperties(mtx,stringBox,lazyLoader.Strings.getJSONObject(i),lazyLoader.stringsList); // The all app is about this LITLE FUNCTION! lets give it full SCOPE!				
						lazyLoader.stringsList.addView(stringBox);
						if(i>5+lazyLoader.lastStringLoaded+1) break; // lazyloading
					}
					lazyLoader.lastStringLoaded=i-1;
					lazyLoader.lazyLoadingEnabledFlag = true;
					//tLoading.cancel();
					//Log.d("ME","lazyloaderLastItem:"+lazyLoader.lastStringLoaded);
				}
			} //end lazy code			
		}
    }
	
	
	// Async Tasks
		
	private class AsyncLogin extends AsyncTask<URL, Integer, Long> {
	     protected Long doInBackground(URL... urls) {
	    	 uiStatus=UI_STATUS_LOCKED;
	    	 publishProgress(0);
	         
	    	 if(mtx.user.login()){
					if(rememberMeCheckBox.isChecked())
						mtx.user.saveUser();
					uiStatus=UI_STATUS_REQUEST_SUCCEED;
				}else
					uiStatus=UI_STATUS_REQUEST_FAILED;	    	 
	         return null;
	     }

	     protected void onProgressUpdate(Integer... progress) {
	         loading.setVisibility(View.VISIBLE);
	     }

	     protected void onPostExecute(Long result) {
	    	 loading.setVisibility(View.GONE);
	     }
	 }	
	
	
	private class AsyncGetProject extends AsyncTask<URL, Integer, Long> {
	     protected Long doInBackground(URL... urls) {
	    	 uiStatus=UI_STATUS_LOCKED;
	    	 publishProgress(0);
	    	 if(mtx.user.project.getProject(slug.getText().toString())){
					uiStatus = UI_STATUS_REQUEST_SUCCEED;
				}else
					uiStatus = UI_STATUS_REQUEST_FAILED;	    		    	 
	         return null;
	     }

	     protected void onProgressUpdate(Integer... progress) {
	         loading.setVisibility(View.VISIBLE);
	     }

	     protected void onPostExecute(Long result) {
	    	 loading.setVisibility(View.GONE);
	     }
	 }
	
	private class AsyncGetResourcesByLanguage extends AsyncTask<URL, Integer, Long> {
	     protected Long doInBackground(URL... urls) {
	    	 uiStatus=UI_STATUS_LOCKED;
	    	 publishProgress(0);
	    	 mtx.user.project.getResourceStatsAllByLanguage(mtx.user.project.slug
	    			 		,mtx.user.project.projectDetailsJson.getJSONArray("resources")
	    			 		,mtx.user.project.selectedLanguageCode);
			uiStatus = UI_STATUS_REQUEST_SUCCEED;
					    	 
	         return null;
	     }

	     protected void onProgressUpdate(Integer... progress) {
	         loading.setVisibility(View.VISIBLE);
	     }

	     protected void onPostExecute(Long result) {
	    	 loading.setVisibility(View.GONE);
	     }
	 }
	
	private class AsyncGetResourceStrings extends AsyncTask<URL, Integer, Long> {
	     protected Long doInBackground(URL... urls) {
	    	 uiStatus=UI_STATUS_LOCKED;
	    	 publishProgress(0);
	    	 mtx.user.project.selectedResourceStrings = mtx.user.project.getResourceStringsByLanguage(
	 				mtx.user.project.slug, mtx.user.project.selectedResourceSlug, mtx.user.project.selectedLanguageCode);
	    	 if(mtx.user.project.selectedResourceStrings!=null)
	    		 uiStatus = UI_STATUS_REQUEST_SUCCEED;
	    	 else
	    		 uiStatus = UI_STATUS_REQUEST_FAILED;
	         return null;
	     }

	     protected void onProgressUpdate(Integer... progress) {
	         loading.setVisibility(View.VISIBLE);
	     }

	     protected void onPostExecute(Long result) {
	    	 loading.setVisibility(View.GONE);
	     }
	 }
	// Async Tasks End
	
	public void updateUI(){
		int uiStatusBuffer = uiStatus;
		uiStatus = UI_STATUS_IDLE;
		if(uiStatusBuffer!=UI_STATUS_IDLE)
			if(uiStatusBuffer!=UI_STATUS_LOCKED)
				switch(currentScreen){
				case SCREEN_LOGIN:
					if(uiStatusBuffer==UI_STATUS_REQUEST_SUCCEED){
						//keyboard.hideSoftInputFromWindow(arg0.getWindowToken(),0);
						loadUI(SCREEN_DASHBOARD);
					}else
						Toast.makeText(mtx, "Could authenticate! Try again please.", Toast.LENGTH_LONG).show();
					break;
				case SCREEN_DASHBOARD:
					if(uiStatusBuffer==UI_STATUS_REQUEST_SUCCEED){
						//keyboard.hideSoftInputFromWindow(arg0.getWindowToken(),0);
						loadUI(SCREEN_PROJECT);
					}else
						Toast.makeText(mtx, "Project could not be retrieved!", Toast.LENGTH_LONG).show();
					break;
				case SCREEN_PROJECT:
					loadUI(SCREEN_LANGUAGE);
					Toast.makeText(mtx, "Language Selected", Toast.LENGTH_LONG).show();
					break;
				case SCREEN_LANGUAGE:
					if(uiStatusBuffer==UI_STATUS_REQUEST_SUCCEED)
						loadUI(SCREEN_TRANSLATE);
					else
						Toast.makeText(mtx, "Strings could not be retrieved!", Toast.LENGTH_LONG).show();
					break;
				}
	}	
	
}
