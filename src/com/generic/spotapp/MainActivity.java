package com.generic.spotapp;


import java.net.URI;
import java.util.ArrayList;

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
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
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
	
	
	
	
}
	class Proximos extends AsyncTask<String, Integer, Boolean>{
		
		private final String PASS_TIME_URL = "http://api.open-notify.org/iss/?n=%s&lat=%s&lon=%s";
		ArrayList<Pass> pass;
		
		int n;
		double lat, lng;
		
		String mensaje;
		Context context;
		
		
		Proximos(int n, double lat, double lng, Context context)
		{
			this.n = n;
			this.lat = lat;
			this.lng = lng;
			this.context = context;
		}

		@Override
		protected Boolean doInBackground(String... params) {
			
			Log.i("INFO", "Ejecutando task");
		
		
			ArrayList<Pass> passArray = new ArrayList<Pass>();

			
			
			
			String formated = String.format(PASS_TIME_URL, Integer.toString(this.n), Double.toString(this.lat), Double.toString(this.lng));
			
			
			Log.i("INFO", "Pre fetch");

			//realizamos la peticion al servidor
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet();
			
			String responseStr = null;

			try {
				request.setURI(new URI(formated));
				HttpResponse response = client.execute(request);
				
				Log.i("INFO", "post fetch");

				HttpEntity entity = response.getEntity();
				responseStr = EntityUtils.toString(entity);
				
			} catch (Exception e) {
				
				this.mensaje = "No se puede contactar con el servidor";
				return false;
			}

			
			try{


			JSONObject json = new JSONObject(responseStr);

			String message = json.getString("message");
			if(message.equals(message))
			{
				JSONArray positions = json.getJSONArray("response");

				Log.i("INFO", "parsing JSON");
				
				for(int i = 0; i < positions.length(); i++)
				{
					JSONObject pos = positions.getJSONObject(i);

					int duration = Integer.parseInt(pos.getString("duration"));
					long risetime = Long.parseLong(pos.getString("risetime"));

					passArray.add(new Pass(duration, risetime));

				}

				Log.i("INFO", "fin parse json");
				
				this.pass =  passArray;

				return true;

			}
			
			}catch (Exception e){
				this.mensaje = "Error al hacer el parse";
				this.pass = null;
				return false;
			}
			
			return true;
			
		}

	
	    protected void onPostExecute(Boolean response) {
	    	

			Log.i("INFO", "POST EXECUTE PASS TIME");


			

			if(response)
			{

				Log.i("INFO", "Tenemos todos los datos, YEAH!");
				
				for(int i = 0; i < this.pass.size(); i++)
				{
					ContentValues event = new ContentValues();
					
				}
			

			}else{
				Utils.showError(this.mensaje, this.context);
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



