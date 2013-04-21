package com.generic.spotapp;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class WelcomeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
	}

	
	public void goLogin(View view){		
    	Intent intent=new Intent(this, MainActivity.class);    	
    	startActivity(intent);
	}

}
