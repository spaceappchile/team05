package com.generic.spotapp;


import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
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



import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract.Events;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

public class MainActivity extends Activity {
	
	private static final String INFO="I:MainActivity";
	private static final int DIALOGO_CONFIRMACION = 0;
	private static final int DIALOGO_REPORTE = 1;
	// el tiempo en segundos
	private ArrayList<Long> longs;
	// la duracion en segundos
	private ArrayList<Integer> duracion;
	// string con la fecha, en ingles
	private ArrayList<String> cadenas;	
	private ListView listView;
	
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		duracion = new ArrayList<Integer>();
		longs = new ArrayList<Long>();
		cadenas = new ArrayList<String>();
		
		
		// DEBUG
		
		double lat = -33.4496866030000035;
		double lng = -70.687233315499995;
									
		//GCMRegistrar.checkDevice(this);
		
		new Proximos(10, lat, lng, this).execute();
		
		
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
		prefs.edit().clear().commit();
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
	
	private Dialog crearDialogoReporte(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		 
		builder.setTitle("Reporte");
		builder.setMessage("¿Vez la estacion espacial?");
		  
		builder.setPositiveButton("Sí", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Log.i(INFO, "Confirmacion Aceptada.");
				new facebook("I can see the International Space Station :)").execute();
				//push notification to the server
				
				dialog.cancel();
			}
		});
		builder.setNegativeButton("No", new OnClickListener() {
		    public void onClick(DialogInterface dialog, int which) {
		        Log.i(INFO, "Confirmacion Cancelada.");
		        dialog.cancel();
		    }
		});
		 
	    return builder.create();
	}
	
	private void nuevoEventoCalendario(int duracion, long tiempo, Context ctx){
		   Intent l_intent = new Intent(Intent.ACTION_EDIT);

		   l_intent.setType("vnd.android.cursor.item/event");

		   //l_intent.putExtra("calendar_id", m_selectedCalendarId);  //this doesn't work

		   l_intent.putExtra("title", "Next ISS pass");

		   l_intent.putExtra("description", "The ISS will pass over you");

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

		       Toast.makeText(ctx.getApplicationContext(), "Sorry, no compatible calendar is found!", Toast.LENGTH_LONG).show();
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
				HttpPut request = new HttpPut();
				
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
					this.mensaje = "Error al hacer el parse";
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

		
		
		private final String URL_REPORT = "http://spotissserver.alwaysdata.net/nofify/%s/%s/%s/";
		
		
		String formated;
		String mensaje;
		
		
		Report(String android, double lat, double lng)
		{
			String formated = String.format(URL_REPORT, android, Double.toString(lat), Double.toString(lng));
		}
		
		
		@Override
		protected Boolean doInBackground(String... params) {
			HttpClient client = new DefaultHttpClient();
			HttpPut request = new HttpPut();
			
			String responseStr = null;

			try {
				request.setURI(new URI(this.formated));
				HttpResponse response = client.execute(request);
				
				Log.i("REPORT", "post fetch");

				HttpEntity entity = response.getEntity();
				responseStr = EntityUtils.toString(entity);
				
			} catch (Exception e) {
				
				this.mensaje = "No se puede contactar con el servidor";
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



