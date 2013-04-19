package com.generic.spotapp;

import android.app.AlertDialog;
import android.content.Context;

public class Utils {

	static public void showError(String msg, Context context)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		 
	    builder.setTitle("Error");
	    builder.setMessage(msg);
	    
	    
	    builder.setPositiveButton("OK", null);
	    
	 
	    builder.create();
	    
	    
	    builder.show();
	}
	
}
