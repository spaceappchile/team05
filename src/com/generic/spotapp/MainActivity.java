package com.generic.spotapp;


import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
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

}
