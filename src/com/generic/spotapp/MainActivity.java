package com.generic.spotapp;



import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {
	
	private static final String INFO="I:MainActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// referencia a la coleccion de preferencias, para guardar preferencias en el dispositivo
		SharedPreferences prefs = getSharedPreferences(getString(R.string.preference_file_key),Context.MODE_PRIVATE);
		/*
		 * Se obtiene el valor de firstTime del archivo de preferencias
		 * Si firstTime no esta en el archivo, getBoolean devuelve el 
		 * valor de defecto que en este caso esta dado por el segundo
		 * parametro de la funcion.
		 * En este caso devuelve true
		 * 		 	
		 */		 
		boolean firstTime = prefs.getBoolean("firstTime", true);
		
		if(firstTime){
			
			Log.i(INFO,"primera vez");
			
			// Clase para poder realizar modificaciones en el archivo
			SharedPreferences.Editor editor = prefs.edit();
			// agregamos el dato, firstTime - false
			editor.putBoolean("firstTime", false);
			// para aplicar cambios
			editor.commit();
			// abrimos el layout (pantalla) de bienvenida 
			
			goWelcome();
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private void goWelcome(){
		Intent intent=new Intent(this, WelcomeActivity.class);
		Log.i(INFO,"pantalla welcome");
    	startActivity(intent);
	
	}

}
