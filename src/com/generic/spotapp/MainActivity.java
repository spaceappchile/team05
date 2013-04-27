package com.generic.spotapp;


import java.net.URI;
import java.util.ArrayList;
import java.util.Date;

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
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private static final String INFO="I:MainActivity";
	private static final int DIALOGO_CONFIRMACION = 0;
	private static final int DIALOGO_REPORTE = 1;
	private static final int DOS_MIN = 1000 * 60 * 2;

	// el tiempo en segundos
	private ArrayList<Long> longs;
	// la duracion en segundos
	private ArrayList<Integer> duracion;
	// string con la fecha, en ingles
	private ArrayList<String> cadenas;	
	private ListView listView;
	private Location locNetwork;
	private Location locGps;	
	private LocationListener listener;
	private LocationManager locationManager;
	private String providerNameNetwork;
	private String providerNameGps;
	
    
	double lat = -33.4496866030000035;
	double lng = -70.687233315499995;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// obtiene posicion
		gpsOn();
		getPosicion();		
		
		duracion = new ArrayList<Integer>();
		longs = new ArrayList<Long>();
		cadenas = new ArrayList<String>();
		
		/*
		if(loc!=null){
			Log.i("GPS", "onCreate: posicion prox: lat: " + loc.getLatitude() + " long: " + loc.getLongitude() );
			new Proximos(10, loc.getLatitude(), loc.getLongitude(), this).execute();
		}
		else{
			Log.i(INFO, "onCreate: loc es null");
		}*/
		
		
		listView = (ListView) findViewById(R.id.lista_avistamientos);
		registerForContextMenu(listView);
		
		// escucha toques en los elementos de la lista
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		      @Override
		      public void onItemClick(AdapterView<?> parent, final View view, int position, long id){
		    	  nuevoEventoCalendario(duracion.get(position), longs.get(position), view.getContext());		    	  
		      }
		});	
		
		// referencia a la coleccion de preferencias, para guardar preferencias en el dispositivo
		SharedPreferences prefs = getSharedPreferences(getString(R.string.preference_file_key),Context.MODE_PRIVATE);
		/*
		 * Se obtiene el valor de firstTime del archivo de preferencias
		 * Si firstTime no esta en el archivo, getBoolean devuelve el 
		 * valor de defecto que en este caso esta dado por el segundo
		 * parametro de la funcion.
		 * En este caso devuelve true		 	
		 */
		
		// DEBUG
		//prefs.edit().clear().commit();
		
		boolean firstTime = prefs.getBoolean("firstTime", true);		
		
		// muestra pantallan de bienvenida
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
		crearAdapter();
		
		
		//Intent intent=new Intent(this, LoginActivity.class);
		
		//startActivity(intent);
		
		
	}
	protected void onPause(Bundle savedInstanceState){
		super.onPause();
		Log.i(INFO,"on pause");
		if(listener != null){
			locationManager.removeUpdates(listener);
		}
	}
	protected void onStop(){
		super.onStop();		
		Log.i(INFO,"on stop");
		if(listener != null){
			locationManager.removeUpdates(listener);
		}		
	}
	
	private void crearAdapter(){
		// adaptador personalizado :D
		try{
			MiAdapter miAdapter = new MiAdapter(this, R.layout.single_view, cadenas);
			listView.setAdapter(miAdapter);
		}catch(Exception e){
			Log.e("AD", "Exception - " + e);
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
            case R.id.action_report:
            	showDialog(DIALOGO_REPORTE); 
            	Log.i("info", "action_report");
            	return true;
            case R.id.action_settings:            	    	
            	return true;
        }
        return super.onOptionsItemSelected(item);
    }
	
	// crea los dialogos, para el gps
	protected Dialog onCreateDialog(int id) {
		Dialog dialogo = null;
			 
		switch(id){
			case DIALOGO_CONFIRMACION:
				dialogo = crearDialogoConfirmacion();
			    break;
			    
			case DIALOGO_REPORTE:
				dialogo = crearDialogoReporte();
				break;
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
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);		
		
		/*
		// criterios
		Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE); // localizacion exacta
        criteria.setCostAllowed(false); 			  // sin costo monetario
        // pregunta si hay un proveedor con estas caracteristicas
         * 
         */
        providerNameNetwork = LocationManager.NETWORK_PROVIDER;        
        providerNameGps = LocationManager.GPS_PROVIDER;        
        
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
	
	// decide cual de las dos fuentes es mejor, network o gps
	public void getPosicion(){
		getKnownPosNetwork();
		getKnownPosGps();
		
		if(locNetwork==null){
			Log.i("GPS", "getPosicion:locNetwork == null");
			return;
		}
		if(isBetterLocation(locNetwork,locGps)){
			Log.i("GPS", "onCreate-else: posicion prox: lat: " + locNetwork.getLatitude() + " long: " + locNetwork.getLongitude() );
			new Proximos(10, locNetwork.getLatitude(), locNetwork.getLongitude(), this).execute();
		}
		else{			
			Log.i("GPS", "onCreate-if: posicion prox: lat: " + locGps.getLatitude() + " long: " + locGps.getLongitude() );
			new Proximos(10, locGps.getLatitude(), locGps.getLongitude(), this).execute();
		}
		getActualPosGps();
		getActualPosNetwork();
	}
	
	private void getKnownPosNetwork(){		
		locNetwork = locationManager.getLastKnownLocation(providerNameNetwork);	
		if(locNetwork!=null){
			Log.i("GPS", "NW:getKnownPos: lat: " + locNetwork.getLatitude() + " long: " + locNetwork.getLongitude());
		}
	}	
	
	private void getActualPosNetwork(){ 	    
	    	    
	    listener = new LocationListener() {			
			//Lanzado cada vez que se recibe una actualización de la posición.
		    @Override
		    public void onLocationChanged(Location location) {
		    	Log.i(INFO, "pos Lat: " + location.getLatitude() + " lon: " + location.getLongitude() );	
		    	locNetwork=location;
		    	Log.i("GPS", "cambio la localizacion");		    			    		    	
		    }		    
			//Lanzado cuando el proveedor se deshabilita.
			@Override
			public void onProviderDisabled(String provider) {		

			}			
			//Lanzado cuando el proveedor se habilita.
			@Override
			public void onProviderEnabled(String provider) {
			
			}			
			// Lanzado cada vez que el proveedor cambia 
			// su estado, que puede variar entre OUT_OF_SERVICE, TEMPORARILY_UNAVAILABLE, AVAILABLE.
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				Log.i("LocAndroid", "Provider Status: " + status);				
			}		    
		};	
		if(locationManager !=null){
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60*1000*10, 30, listener);
		}else{
			Log.i("GPS", "location Manager es null");
		}
	}
	
	// obtiene la posicion conocida
	private void getKnownPosGps(){		
		locGps = locationManager.getLastKnownLocation(providerNameGps);	
		if(locGps!=null){
			Log.i("GPS", "GPS:getKnownPos: lat: " + locGps.getLatitude() + " long: " + locGps.getLongitude());
		}
	}	
	
	private void getActualPosGps(){ 	    
	    	    
	    listener = new LocationListener() {			
			//Lanzado cada vez que se recibe una actualización de la posición.
		    @Override
		    public void onLocationChanged(Location location) {
		    	Log.i(INFO, "pos Lat: " + location.getLatitude() + " lon: " + location.getLongitude() );	
		    	locGps=location;
		    	Log.i("GPS", "cambio la localizacion");		    			    		    	
		    }		    
			//Lanzado cuando el proveedor se deshabilita.
			@Override
			public void onProviderDisabled(String provider) {		

			}			
			//Lanzado cuando el proveedor se habilita.
			@Override
			public void onProviderEnabled(String provider) {
			
			}			
			// Lanzado cada vez que el proveedor cambia 
			// su estado, que puede variar entre OUT_OF_SERVICE, TEMPORARILY_UNAVAILABLE, AVAILABLE.
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				Log.i("LocAndroid", "Provider Status: " + status);				
			}		    
		};	
		if(locationManager !=null){
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60*1000*10, 30, listener);
		}else{
			Log.i("GPS", "location Manager es null");
		}
	}
	// devuelve true cuando la nueva location es mejor que currentBestLocation
	protected boolean isBetterLocation(Location location, Location currentBestLocation) {
	    if (currentBestLocation == null) {	        
	        return true;
	    }
	    
	    long timeDelta = location.getTime() - currentBestLocation.getTime();
	    boolean isSignificantlyNewer = timeDelta > DOS_MIN;
	    boolean isSignificantlyOlder = timeDelta < -DOS_MIN;
	    boolean isNewer = timeDelta > 0;
	    
	    if (isSignificantlyNewer) {
	        return true;
	    
	    } else if (isSignificantlyOlder) {
	        return false;
	    }

	    // Check whether the new location fix is more or less accurate
	    int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
	    boolean isLessAccurate = accuracyDelta > 0;
	    boolean isMoreAccurate = accuracyDelta < 0;
	    boolean isSignificantlyLessAccurate = accuracyDelta > 200;

	    // Check if the old and new location are from the same provider
	    boolean isFromSameProvider = esIgual(location.getProvider(),
	            currentBestLocation.getProvider());

	    // Determine location quality using a combination of timeliness and accuracy
	    if (isMoreAccurate) {
	        return true;
	    } else if (isNewer && !isLessAccurate) {
	        return true;
	    } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
	        return true;
	    }
	    return false;
	}
	
	private boolean esIgual(String provider1, String provider2) {
	    if (provider1 == null) {
	      return provider2 == null;
	    }
	    return provider1.equals(provider2);
	}

	
	private Dialog crearDialogoConfirmacion(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		String title = getResources().getString(R.string.dialog_gps_title);
		String messege = getResources().getString(R.string.dialog_gps_message);
		String accept = getResources().getString(R.string.dialog_gps_accept);
		String cancel = getResources().getString(R.string.dialog_gps_cancel);
		
		 
		builder.setTitle(title);
		builder.setMessage(messege);
		  
		builder.setPositiveButton(accept, new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Log.i(INFO, "Confirmacion Aceptada.");
				activarGps();
				dialog.cancel();
			}
		});
		builder.setNegativeButton(cancel, new OnClickListener() {
		    public void onClick(DialogInterface dialog, int which) {
		        Log.i(INFO, "Confirmacion Cancelada.");
		        dialog.cancel();
		    }
		});
		 
	    return builder.create();
	}
	
	private Dialog crearDialogoReporte(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		String title = getResources().getString(R.string.dialog_rpt_title);
		String message = getResources().getString(R.string.dialog_rpt_message);
		String yes = getResources().getString(R.string.dialog_rpt_yes);
		String no = getResources().getString(R.string.dialog_rpt_no);
				
		 
		builder.setTitle(title);
		builder.setMessage(message);
		  
                
		
		final Context cont = this;
		
		builder.setPositiveButton(yes, new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Log.i(INFO, "Confirmacion Aceptada.");
				String iSee = getResources().getString(R.string.i_see);
				
				Intent intent= new Intent(cont, LoginActivity.class);
				
				startActivityForResult(intent, 0);
				
				
				new Report("androidID", lat, lng).execute();
				//push notification to the server
				
				dialog.cancel();
			}
		});
		builder.setNegativeButton(no, new OnClickListener() {
		    public void onClick(DialogInterface dialog, int which) {
		        Log.i(INFO, "Confirmacion Cancelada.");
		        dialog.cancel();
		    }
		});
		 
	    return builder.create();
	}
	
	private void nuevoEventoCalendario(int duracion, long tiempo, Context ctx){
		   Intent l_intent = new Intent(Intent.ACTION_EDIT);
		   String title = getResources().getString(R.string.event_calendar_title);
		   String description = getResources().getString(R.string.event_calendar_description);
		   String error = getResources().getString(R.string.event_calendar_error);
		   
		   
		   
		   l_intent.setType("vnd.android.cursor.item/event");

		   //l_intent.putExtra("calendar_id", m_selectedCalendarId);  //this doesn't work

		   l_intent.putExtra("title", title);

		   l_intent.putExtra("description", description);

		   l_intent.putExtra("eventLocation", "@home");

		   l_intent.putExtra("beginTime", tiempo);

		   l_intent.putExtra("endTime", duracion*1000 + tiempo);

		   l_intent.putExtra("allDay", 1);

		   //status: 0~ tentative; 1~ confirmed; 2~ canceled

		   l_intent.putExtra("eventStatus", 1);

		   //0~ default; 1~ confidential; 2~ private; 3~ public

		   l_intent.putExtra("visibility", 0);

		   //0~ opaque, no timing conflict is allowed; 1~ transparency, allow overlap of scheduling

		   l_intent.putExtra("transparency", 1);

		   //0~ false; 1~ true

		   l_intent.putExtra("hasAlarm", 1);

		   try {
		       startActivity(l_intent);

		   } catch (Exception e) {

		       Toast.makeText(ctx.getApplicationContext(), error, Toast.LENGTH_LONG).show();
		   }	
	}
	
	public class MiAdapter extends ArrayAdapter<String>{
		private Context ctx;
		private ArrayList<String> dates;
		
		public MiAdapter(Context ctx, int textVievResourcedId, ArrayList<String> obj){
			super(ctx, textVievResourcedId, obj);
			this.ctx = ctx;
			this.dates = obj;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			
			if(convertView==null){
				LayoutInflater infalInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = infalInflater.inflate(R.layout.single_view, null);
			}
			TextView fecha = (TextView)convertView.findViewById(R.id.single_date);
			TextView duration = (TextView)convertView.findViewById(R.id.single_duration);
		
			fecha.setText(dates.get(position));
			String sDuracion = getResources().getString(R.string.txt_duracion);
			duration.setText(sDuracion+duracion.get(position)/60 + "min" );
			
			return convertView;			
		}		
	}

	private class Proximos extends AsyncTask<String, Integer, Boolean>{
		
		private final String PASS_TIME_URL = "http://api.open-notify.org/iss/?n=%s&lat=%s&lon=%s";
		ArrayList<Pass> pass;
				
		private boolean internet = true;
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
			
			if(this.internet){
			
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
					
					this.mensaje = getResources().getString(R.string.msg_error);
					return false;
				}
				
				try{
					JSONObject json = new JSONObject(responseStr);
		
					String message = json.getString("message");
					if(message.equals(message))	{
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
					this.mensaje = getResources().getString(R.string.msg_error1);
					this.pass = null;
					return false;
				}
				
				return true;
				
			}else{
				
				Log.i("INFO", "NO hay conexion");
				
				this.pass = new ArrayList<Pass>();
				
				this.pass.add(new Pass(601, 1366524444));
				this.pass.add(new Pass(602, 1366530237));
				this.pass.add(new Pass(420, 1366536183));
						
				return true;				
			}			
		}


	    @SuppressLint("NewApi")
		protected void onPostExecute(Boolean response) {    	

			Log.i("INFO", "POST EXECUTE PASS TIME");

			if(response)
			{
				Log.i("INFO", "Tenemos todos los datos, YEAH!" + (Long.toString(this.pass.get(0).risetime)));
				
				int NewID = 0 + 1;
								
				Log.i("PROX", "size pass" + pass.size());				
				for(int i = 0; i < this.pass.size(); i++)
				{
					// para poder obtener la fecha en string
					Date fecha = new Date(pass.get(i).risetime*1000);
					
					// se agregan el tiempo en segundos
					longs.add(pass.get(i).risetime*1000);
					// se agregan la duracion en segundos
					duracion.add(pass.get(i).duration);
					// se agrega la fecha en string
					cadenas.add(fecha.toString());
				}
				
				Log.i("INFO", "terminado de poner los eventos");
				
				crearAdapter();

			}else{
				Utils.showError(this.mensaje, this.context);
			}
		}	
	}
	
	
	
	class Report extends AsyncTask<String, Integer, Boolean>{

		
		
		private final static String URL_REPORT = "http://spotissserver.alwaysdata.net/notify/%s/%s/%s/";
		
		
		String formated;
		String mensaje;
		
		
		Report(String android, double lat, double lng)
		{
			
			this.formated = String.format(URL_REPORT, android, Double.toString(lat), Double.toString(lng));
		}
		
		
		@Override
		protected Boolean doInBackground(String... params) {
			
			
			Log.i("REPORT", "Enviando reporte al servidor");
			HttpClient client = new DefaultHttpClient();
			HttpPut request = new HttpPut();
			
			
			
			String responseStr = null;

			try {
				request.setURI(new URI(this.formated));
				HttpResponse response = client.execute(request);
				
				Log.i("REPORT", "post fetch");

				HttpEntity entity = response.getEntity();
				responseStr = EntityUtils.toString(entity);
				
				Log.i("REPORT", responseStr);
				
			} catch (Exception e) {
				
				Log.i("REPORT", "No se puede enviar el reporte al servidor");
				
				return false;
			}
			return true;
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



