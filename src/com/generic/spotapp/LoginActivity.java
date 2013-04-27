package com.generic.spotapp;




import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.provider.CalendarContract.Events;
import android.provider.Settings.Secure; //for the androidID unique identifier

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.facebook.Request;
import com.facebook.Response;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.google.android.gcm.*;
import com.google.android.gms.maps.GoogleMap;


public class LoginActivity extends Activity {

	
	String APP_ID = "522690977788329";
	
	@SuppressWarnings("deprecation")
	Facebook fb = new Facebook(APP_ID);
	
	final String permisos[] = {"publish_stream"};
	
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);

		enviarMensaje();
		
		//No funciona esta parte, pero se logea solo
//		if(!fb.isSessionValid())
//		{
//			Log.i("Facebook", "Conectando a FB");
//			
//			fb.authorize(this, this.permisos, new Facebook.DialogListener() {
//				
//				@Override
//				public void onFacebookError(FacebookError e) {
//					// TODO Auto-generated method stub
//					Log.i("Facebook", "Error de FB");
//				}
//				
//				@Override
//				public void onError(DialogError e) {
//					Log.i("Facebook", "Error");
//					
//			    }
//							
//				@Override
//				public void onComplete(Bundle values) {
//					// TODO Auto-generated method stub
//					Log.i("Facebook", "Completo");
//					//enviarMensaje();
//				}
//				
//				@Override
//				public void onCancel() {
//					// TODO Auto-generated method stub
//					
//				}
//			});
//		
//		}else{
//			Log.i("Facebook", "Session valida");
//			//enviarMensaje();
//		}
	}
	
	
	@SuppressWarnings("deprecation")
	private void enviarMensaje()
	{
		Log.i("Facebook", "Enviando mensaje");
		
		
		Log.i("Facebook", "Token: " + fb.getAccessToken());
		
		Bundle params = new Bundle();
		
		params.putString("name", "SpotApp");
		params.putString("description", "With SpotApp you can add alerts on when the ISS will pass over your position");
		params.putString("link", "http://spaceappschallenge.org/project/spotapp/");
		params.putString("message", "I can see the ISS right now");
		
		
		this.fb.dialog(this, "feed", new DialogListener() {
			
			@Override
			public void onFacebookError(FacebookError e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onError(DialogError e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onComplete(Bundle values) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onCancel() {
				// TODO Auto-generated method stub
				
			}
		});
			
	}
}
	
