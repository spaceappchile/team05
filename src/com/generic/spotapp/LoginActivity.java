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


import com.google.android.gcm.*;
import com.google.android.gms.maps.GoogleMap;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity {
	/**
	 * A dummy authentication store containing known user names and passwords.
	 * TODO: remove after connecting to a real authentication system.
	 */
	private static final String[] DUMMY_CREDENTIALS = new String[] {
			"pato@generic.cl", "felipe@generic.cl" };

	/**
	 * The default email to populate the email field with.
	 */
	public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private CreateUser mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;

	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;
	
	
	
	
	private float lat, lng;
	private static final String PROYECT_ID = "211948616229";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);

		// Set up the login form.
		mEmail = getIntent().getStringExtra(EXTRA_EMAIL);
		mEmailView = (EditText) findViewById(R.id.email);
		mEmailView.setText(mEmail);

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {

				showProgress(true);
		
                String usr = mEmailView.getText().toString();
                String password = mPasswordView.getText().toString();
                
                //check if the user provied an usr and password

                if(usr.equals(""))
                {
                	String error = getResources().getString(R.string.missing_name);
                	mEmailView.setError(error);
                	return;
                }
                
                if(password.equals(""))
                {
                	String error = getResources().getString(R.string.missing_name);
                	mPasswordView.setError(error);
                	return;
                	
                }

                //send a request to the database
                
                String android_id = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID); 
           
        		
        		
        		
               new CreateUser(usr, password, android_id, this).execute();
                
               
               
                
                
                
                
                

                //if (mAuthTask != null) {
                        //return;
                //}

                //// Reset errors.
                //mEmailView.setError(null);
                //mPasswordView.setError(null);

                //// Store values at the time of the login attempt.
                //mEmail = mEmailView.getText().toString();
                //mPassword = mPasswordView.getText().toString();

                //boolean cancel = false;
                //View focusView = null;

                //// Check for a valid password.
                //if (TextUtils.isEmpty(mPassword)) {
                        //mPasswordView.setError(getString(R.string.error_field_required));
                        //focusView = mPasswordView;
                        //cancel = true;
                //} else if (mPassword.length() < 4) {
                        //mPasswordView.setError(getString(R.string.error_invalid_password));
                        //focusView = mPasswordView;
                        //cancel = true;
                //}

                //// Check for a valid email address.
                //if (TextUtils.isEmpty(mEmail)) {
                        //mEmailView.setError(getString(R.string.error_field_required));
                        //focusView = mEmailView;
                        //cancel = true;
                //} else if (!mEmail.contains("@")) {
                        //mEmailView.setError(getString(R.string.error_invalid_email));
                        //focusView = mEmailView;
                        //cancel = true;
                //}

                //if (cancel) {
                        //// There was an error; don't attempt login and focus the first
                        //// form field with an error.
                        //focusView.requestFocus();
                //} else {
                        //// Show a progress spinner, and kick off a background task to
                        //// perform the user login attempt.
                        //mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
                        //showProgress(true);
                        //mAuthTask = new UserLoginTask();
                        //mAuthTask.execute((Void) null);
                //}
	}
	
	
	public void setLat(float lat)
	{
		this.lat = lat;
	}
	
	public void setLng(float lng)
	{
		this.lng = lng;
	}
	
	
	class Login extends AsyncTask<String, Integer, Boolean>{

		
		
		
		@Override
		protected Boolean doInBackground(String... params) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	
	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	
	
	
	// async task

	
	private class CreateUser extends AsyncTask<String, Integer, String> {

		private static final String CREATE_URL = "http://10.0.2.2:8000/usuario/%s/%s/%s/";

		String formated;
		String usuario, clave, android;;

		private Context context;

		public CreateUser(String user, String password, String android, Context context) {
			
			
			this.formated = String.format(CREATE_URL, user, password, android);
			this.context = context;
			this.usuario = user;
			this.clave = password;
			this.android = android;
			
			
		}

		@Override
		protected String doInBackground(String... params) {

			HttpClient client = new DefaultHttpClient();
			HttpPost request = new HttpPost();

			try {
				request.setURI(new URI(this.formated));
				HttpResponse response = client.execute(request);

				HttpEntity entity = response.getEntity();
				return EntityUtils.toString(entity);

			} catch (Exception e) {
				
				return null;
			}
		}
		
		

		// realizamos las operaciones como actualizar la UI dependiendo si hay
		// error o no
		protected void onPostExecute(String response) {
			
			
			//ocurrio un error :(
			if(response == null)
			{
				String error = getResources().getString(R.string.error_conexion); 
				Utils.showError(error, context);
				return;
			}

			JSONObject json;

			try {
				json = new JSONObject(response);
			} catch (JSONException e) {
				String error = getResources().getString(R.string.error_json);
				Utils.showError(error,
						this.context);
				return;
			}
			
			
			String msgError = getResources().getString(R.string.error_parsing);
			String responseStr;
			try {
				responseStr = json.getString("response");
			} catch (JSONException e) {

				Utils.showError(msgError, this.context);
				return;
			}
			
			

			if (responseStr.equals("OK")) {
				// everything is ok and we have register in the server
				// we also need to login in the server
				// FUCKING TODO
				new LoginUser(this.usuario, this.clave, this.android, this.context).execute();
				return;
			} else {
				// error, let's get the error

				String error = null;
				try {
					error = json.getString("cause");
					if (error.equals("User exists")) {
						//el usuario ya existia en la base de datos por lo que debemos hacer el login en vez
						//de crear un nuevo usuario
						//FUCKING TODO
						new LoginUser(this.usuario, this.clave, this.android, this.context).execute();
						return;
						
					}

				} catch (JSONException e) {

					Utils.showError(msgError, this.context);
					return;
				}

			}
		}

	}
	
	
	
	
	
	
	
	private class LoginUser extends AsyncTask<String, Integer, Boolean> {

		Context context;
		
		private GoogleMap mMap;
		
		String mensaje;		
		
		//usuario/clave/android/gcm_id/lat/lng/
		private static final String LOGIN_URL = "http://10.0.2.2:8000/login/%s/%s/%s/%s/%s/%s/";
		
		String formated; 
		
		String usuario, clave, android;
		
		LoginUser(String usuario, String clave, String android, Context context)
		{
			this.context = context;
		}
		
		@Override
		protected Boolean doInBackground(String... arg0) {
			// se logea en el server enviando las coordenadas actuales y el gcm_id
			
			//primero debemos obtener los datos de la gcm
			
			//nos registramos gcm como cliente
			String id = GCMRegistrar.getRegistrationId(this.context);
			
			if(id.equals(""))
			{
				GCMRegistrar.register(this.context, PROYECT_ID);
				id = GCMRegistrar.getRegistrationId(this.context);
				if(id.equals(""))
				{
					this.mensaje = "No se pudo registrar el gcm";
					return false;
				}
			}
			
			//deberiamos tener ahora la id con la cual podemos identificar a este usuario en el server
			
			
			//obtenemos la direccion mediante gps
			
			//this.mMap.setMyLocationEnabled(true);
			//Location l = this.mMap.getMyLocation();
			
			//double lat = l.getLatitude();
			//double lng = l.getLongitude();
			
			
			id = "23451213421";
			double lat = -33.4496866030000035;
			double lng = -70.687233315499995;
			
			String formated = String.format(LOGIN_URL, this.usuario, this.clave, this.android, id, Double.toString(lat), Double.toString(lng));			
			
			//realizamos la peticion al servidor
			HttpClient client = new DefaultHttpClient();
			HttpPut request = new HttpPut();

			try {
				request.setURI(new URI(this.formated));
				HttpResponse response = client.execute(request);

				HttpEntity entity = response.getEntity();
				this.mensaje = EntityUtils.toString(entity);
				return true;

			} catch (Exception e) {
				
				this.mensaje = "No se puede contactar con el servidor";
				return false;
			}
		}
		
		
		protected void onPostExecute(Boolean response) {
			if(response)
			{
				
				Utils.showError("Todo ok", context);
				
//				JSONObject json;
//
//				try {
//					json = new JSONObject(response);
//				} catch (JSONException e) {
//					Utils.showError("Server response is not a valid json",	this.context);
//					return;
//				}
//				
//				
//				String responseStr;
//				try {
//					responseStr = json.getString("response");
//				} catch (JSONException e) {
//					Utils.showError("Error parsing json", this.context);
//					return;
//				}
//				
//				if(responseStr.equals("OK"))
//				{
//					Utils.showError("Todo ok", this.context);
//					
//				}else{
//					Utils.showError(responseStr, this.context);
//					return;
//				}
//				
//				
//				
			}else{
				
				Utils.showError(this.mensaje, this.context);
			}
//			
		}
		
	}
}

