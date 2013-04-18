package com.generic.spotapp;




import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import android.provider.Settings.Secure; //for the androidID unique identifier

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface.OnClickListener;
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
	private UserLoginTask mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;

	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;

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
                	mEmailView.setError("You have to give a user name");
                	return;
                }
                
                if(password.equals(""))
                {
                	mPasswordView.setError("You have to give a password");
                	return;
                	
                }

                //send a request to the database
                
                String LOGIN_URL = "http://10.0.2.2:8000/usuario/%s/%s/%s/";
                
                String android_id = Secure.getString(getBaseContext().getContentResolver(), Secure.ANDROID_ID); 
                
                String formated = String.format(LOGIN_URL, usr, password, android_id);
                
                RestClient rest = new RestClient(formated);
                
                String response = null;
                
                try {
					 response = rest.performPost();
					 
					
					 if(response == null)
					 {
						 showError("Can't get response from server");
						 return;
					 
					 }
					 
				} catch (ClientProtocolException e) {
					showError("Client protocol error");
					return;
				} catch (URISyntaxException e) {
					showError("Bad URL");
					return;
				} catch (IOException e) {
					showError("Conection Error");
					return;
				} catch (Error404 e) {
					showError(e.getMessage());
				}
               
                JSONObject json;
                
                try {
					json = new JSONObject(response);
				} catch (JSONException e) {
					showError("Server response is not a valid json");
					return;
				}
                
                
                String responseStr;
                try {
					responseStr = json.getString("response");
				} catch (JSONException e) {
					showError("Error parsing json");
					return;
				}
                
                
                if(responseStr.equals("OK"))
                {
                	//everything is ok and we have register in the server
                	showProgress(false);
                
                }else{
                	//error, let's get the error
                	
                	String error = null;
                	try{
                		error = json.getString("cause");
                		showError(error);
                	
                	} catch (JSONException e){
    					showError("JSON parse error");
    					return;
                	}
                	
                	               	
                }
                
                

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
	
	
	private void showError(String msg)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		 
	    builder.setTitle("Error");
	    builder.setMessage(msg);
	    
	    
	    builder.setPositiveButton("OK", null);
	    
	 
	    builder.create();
	    
	    showProgress(false);
	    
	    builder.show();
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

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.

			try {
				// Simulate network access.
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				return false;
			}

			for (String credential : DUMMY_CREDENTIALS) {
				String[] pieces = credential.split(":");
				if (pieces[0].equals(mEmail)) {
					// Account exists, return true if the password matches.
					return pieces[1].equals(mPassword);
				}
			}

			// TODO: register the new account here.
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			showProgress(false);

			if (success) {
				finish();
			} else {
				mPasswordView
						.setError(getString(R.string.error_incorrect_password));
				mPasswordView.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
}
