package com.generic.spotapp;


import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;


import com.google.android.gcm.GCMRegistrar;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract.Events;
import android.provider.Settings;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.FragmentActivity;

public class MainActivity extends Activity {
	
	private static final String INFO="I:MainActivity";
	private static final int DIALOGO_CONFIRMACION = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		//GCMRegistrar.checkDevice(this);
		
		
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
		
		
		
		
		
		prefs.edit().clear().commit();
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
	
	// metodo que observa las pulsaciones del menu opciones
	public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_map:
            	goMaps();                	
            	return true;               
        }
        return super.onOptionsItemSelected(item);
    }
	
	// crea los dialogos
		protected Dialog onCreateDialog(int id) {
			   	Dialog dialogo = null;
			 
			    switch(id){
			        case DIALOGO_CONFIRMACION:
			            dialogo = crearDialogoConfirmacion();
			            break;
			        
			        //...
			        default:
			            dialogo = null;
			            break;
			    }
			 
			    return dialogo;
		}
	
	private void goWelcome(){
		Intent intent=new Intent(this, WelcomeActivity.class);
		Log.i(INFO,"pantalla welcome");
    	startActivity(intent);
	
	}
	
	private void goMaps(){
		Intent intent=new Intent(this, MapActivity.class);
		Log.i(INFO, "pantalla maps");
		startActivity(intent);
	}
	private void gpsOn(){
		// para acceder a los servicios de localizacion
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		
		// criterios
		Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE); // localizacion exacta
        criteria.setCostAllowed(false); 			  // sin costo monetario
        // pregunta si hay un proveedor con estas caracteristicas
        String providerName = locationManager.getBestProvider(criteria, true);
       
        Log.i(INFO,"bestProvider: " + providerName);
        
        // retorna true, si el gps esta activado		
        final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	
        if(!gpsEnabled){
			// muestra, inicia, crea, (no se que mierda) 
			//el dialogo con el id: DIALOGO_CONFIRMACION
			showDialog(DIALOGO_CONFIRMACION);
			Log.d("PRUEBA GPS", "GPS apagado");
		}
	}
	public void activarGps(){
		Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	    startActivity(settingsIntent);		
	}
	private Dialog crearDialogoConfirmacion(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		 
		builder.setTitle("Confirmacion");
		builder.setMessage("¿activar GPS?");
		  
		builder.setPositiveButton("Aceptar", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Log.i(INFO, "Confirmacion Aceptada.");
				activarGps();
				dialog.cancel();
			}
		});
		builder.setNegativeButton("Cancelar", new OnClickListener() {
		    public void onClick(DialogInterface dialog, int which) {
		        Log.i(INFO, "Confirmacion Cancelada.");
		        dialog.cancel();
		    }
		});
		 
	    return builder.create();
	}
	
	


private MyCalendar m_calendars[];

private void getCalendars() {

    String[] l_projection = new String[]{"_id", "displayName"};

    Uri l_calendars;

    if (Build.VERSION.SDK_INT >= 8 ) {

        l_calendars = Uri.parse("content://com.android.calendar/calendars");

    } else {

        l_calendars = Uri.parse("content://calendar/calendars");

    }

    Cursor l_managedCursor = this.managedQuery(l_calendars, l_projection, null, null, null);    //all calendars

    //Cursor l_managedCursor = this.managedQuery(l_calendars, l_projection, "selected=1", null, null);   //active calendars

    if (l_managedCursor.moveToFirst()) {

        m_calendars = new MyCalendar[l_managedCursor.getCount()];

        String l_calName;

        String l_calId;

        int l_cnt = 0;

        int l_nameCol = l_managedCursor.getColumnIndex(l_projection[1]);

        int l_idCol = l_managedCursor.getColumnIndex(l_projection[0]);

        do {

            l_calName = l_managedCursor.getString(l_nameCol);

            l_calId = l_managedCursor.getString(l_idCol);

            m_calendars[l_cnt] = new MyCalendar(l_calName, l_calId);

            ++l_cnt;

        } while (l_managedCursor.moveToNext());

    }

}




}



		/**Pass class with the duration and risetime
		 * */
		class Pass{
			
			//duration: Number of seconds the pass will last
			
			final int duration;
			
			//unix time stamp when the ISS will be above 10°
			final long risetime;
			
			
			Pass(int duration, long risetime)
			{
				this.duration = duration;
				this.risetime = risetime;
			}
		}
		
		
		class ErrorOpenNofify extends Exception {
		    public ErrorOpenNofify(String message) {
		        super(message);
		    }
	}



