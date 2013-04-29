package com.generic.spotapp;

/* TODO crear funcion que tome la base de datos con los avistamientos y
 * 		crea las marcas
 * TODO mostrar info en los marcadores creados, la fecha, 
 *
 */
		


import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

public class MapActivity extends FragmentActivity {
	
	private static final String INFO = "INFO";
	private static final int DIALOGO_CONFIRMACION = 0;
	private ImageView imagen;
	
	private GoogleMap mMap;
	private LocationListener listener;
	private LocationManager locationManager;
	private Location loc;
	private Criteria criteria;
	
	private MenuItem modeNormal;
	private MenuItem modeHybrid;
	private MenuItem modeSatellite;
	private MenuItem modeTerrain;	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		
		imagen=(ImageView)findViewById(R.id.imagen_clima); 		
		
		gpsOn();
		
		setUpMapIfNeeded();
		
				
		//new Trayectoria().execute();
		new GetOthers().execute();
		
	}
	
	@Override
    protected void onResume() {
		super.onResume();
        setUpMapIfNeeded();
        /*
        getKnownPos();
        getActualPos();
        */
    }
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map, menu);
		
		return true;
	}
	
	@Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if(mMap!=null){
	        int mode = mMap.getMapType();
	        
	        switch(mode){
	       		case 1:
	       			modeNormal = menu.findItem(R.id.change_normal);
	       			modeNormal.setChecked(true);
	       			unCheckOther(1);
	       			Log.i(INFO,"is checked");
	       			break;
				case 2:
					modeSatellite = menu.findItem(R.id.change_satellite);
					modeSatellite.setChecked(true);
		            break;
				case 3:
					modeTerrain = menu.findItem(R.id.change_terrain);
					modeTerrain.setChecked(true);				
		            break;
				case 4:
					modeHybrid = menu.findItem(R.id.change_hybrid);
					modeHybrid.setChecked(true);				
		            break;	        
        	}               
	        return true;
        }
        return false;
    }
	
	 private void unCheckOther(int i) {
		
		
	}

	public boolean onMenuItemSelected(int id, MenuItem item){
		 
		 switch (item.getItemId()) {
		 case R.id.change_normal:    	
	    		mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);	    		
	            return true;   
	            
	    	case R.id.change_satellite:
	    		mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
	    		return true;	
	    		
	    	case R.id.change_terrain:
	    		mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
	    		return true;
	    		
	    	case R.id.change_hybrid:
	    		mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
	    		return true;
		 }
		 
		 return true;		 
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
		
	// configuracion del mensaje/dialogo
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
	
	
	
	private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
        	
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
                            	
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
            	// MyLocation es para obtener la posicion
            	mMap.setMyLocationEnabled(true);
           
            	//getKnownPos();
        		//getActualPos();
                setUpMap();
            }
        }
    }
	
	private void setUpMap() {
		
		addmarkers();
	
		
		//buscamos la informacion sobre el clima
		
			// TODO problema
		new Clima(MainActivity.latNow, MainActivity.lngNow).execute();
		
		
		//new Clima(-33.44989201,-70.68687829).execute();
		
    }
	
	private void changeModeMap(int mode){
		
		switch(mode){
			case 1:
				
	            break;
			case 2:
				mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
	            break;
			case 3:
				mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
	            break;
			case 4:
				mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
	            break;					
		}	
	}
	
	private void changePosCam(double lat, double lon){
		CameraUpdate camUpd1 = CameraUpdateFactory.newLatLng(new LatLng(lat, lon));
		mMap.moveCamera(camUpd1);    
	}
	
	private void changePosCam2(double lat, double lon){		
		CameraPosition camPos = new CameraPosition.Builder()
        .target( new LatLng(lat, lon))   
        .zoom(16)         
        .bearing(0)      
        .tilt(0)         
        .build();
		
		CameraUpdate camUpd =
			    CameraUpdateFactory.newCameraPosition(camPos);
		
		mMap.moveCamera(camUpd);
		Log.i(INFO, "Camara movida");
	}
	
	/*
	 * Cambia la vista en el mapa, con zoom
	 */
	private void changePosCam2(Location loc){		
		CameraPosition camPos = new CameraPosition.Builder()
        .target( new LatLng(loc.getLatitude(), loc.getLongitude()))   
        .zoom(16)         
        .bearing(0)      
        .tilt(0)         
        .build();
		
		CameraUpdate camUpd =
			    CameraUpdateFactory.newCameraPosition(camPos);
		
		mMap.moveCamera(camUpd);
		Log.i(INFO, "Camara movida");
	}
	
	
	public void activarGps(){
		Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	    startActivity(settingsIntent);		
	}
	
	
	// aacede a los servicios de localizacion y sale un mensaje para activar el gps
	private void gpsOn(){
		// para acceder a los servicios de localizacion
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		
		// criterios
		criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE); // localizacion exacta
        criteria.setCostAllowed(true); 			  // sin costo monetario
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
	
	// obtiene la posicion conocida
	private void getKnownPos(){
		String locationProvider = LocationManager.GPS_PROVIDER;
		Log.i(INFO, "locationProvider: "+ locationProvider);
		
		loc = locationManager.getLastKnownLocation(locationProvider);
		//showMarket(loc, "Tu posicion");
	}
	
	private void getActualPos(){

		//Obtenemos una referencia al LocationManager
		locationManager =
	        (LocationManager)getSystemService(Context.LOCATION_SERVICE); 
	    	    
	    //showMarket(loc, "Tu posicion");
	    
	    listener = new LocationListener() {			
			//Lanzado cada vez que se recibe una actualización de la posición.
		    @Override
		    public void onLocationChanged(Location location) {
		    	Log.i(INFO, "pos Lat: " + location.getLatitude() + " lon: " + location.getLongitude() );		   	
		    			    		    	
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
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60*1000*10, 20, listener);	
	}
	
	/*
	 * Para agregar varios avistamientos
	 */	
	private void addmarkers(){		

	}
	
	/* agrega un avistamiento
	 * usa la imagen del ojo
	 */
	private void addmarker(double lat, double lng, String mensaje, String detalle){
		LatLng ln = new LatLng(lat,lng);
		mMap.addMarker(new MarkerOptions()
	    .position(ln)
	    .title(mensaje)
	    .snippet(detalle)
	    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ojo)));		
	}
	
	private void dibujaEstacion(List<LatLng> coors){		
		
		Polyline line = mMap.addPolyline((new PolylineOptions())
                .width(5)
                .color(Color.BLUE)
                .geodesic(true));
		
		line.setPoints(coors);
		
	}
	
	
	
	class Trayectoria extends AsyncTask<String, Integer, Boolean>{
		
		final static String DATA_SERVER_URL = "http://spotissserver.alwaysdata.net/data/";

		String respuesta;
		
		@Override
		protected Boolean doInBackground(String... params) {
				
			Log.i("ISS","info pre-fecth trayectoria");
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet();

			try {
				request.setURI(new URI(DATA_SERVER_URL));
				HttpResponse response = client.execute(request);

				HttpEntity entity = response.getEntity();
				this.respuesta = EntityUtils.toString(entity);

			} catch (Exception e) {
				Log.e("ISS", "error exception " + e );
				return false;
			}
			
				return true;
		}
		
		
		protected void onPostExecute(Boolean response) {
			
			Log.i("ISS", "algo");
			
			try {
				
				JSONObject json = new JSONObject(this.respuesta);
				
				JSONArray array = json.getJSONArray("lista");
				
				List<LatLng> coor = new ArrayList<LatLng>();
				
				for(int i = 0; i < array.length(); i++)
				{
					JSONObject datos = array.getJSONObject(i);
					
					double lat = Double.parseDouble(datos.getString("lat"));
					double lng = Double.parseDouble(datos.getString("lng"));
					
					
					coor.add(new LatLng(lat, lng));
				}
				
				Log.i("ISS", "coor.size() = " + coor.size());
				Log.i("ISS", "coor 0 lat" + coor.get(0).latitude);
				Log.i("ISS", "coor 0 lng" + coor.get(0).longitude);
				dibujaEstacion(coor);
				
				
				
			} catch (JSONException e) {
				e.printStackTrace();
			}	
		}		
	}
	
	
	class Clima extends AsyncTask<String, Integer, Boolean>{

		double lat, lng;
		
		String mensaje;
		
		String clima, icon, descripcion;
		
		
		boolean internet = true;
		
		private final static String URL_WEATHER = "http://api.openweathermap.org/data/2.1/find/city?lat=%s&lon=%s&cnt=1&APPID=6ac9778157d222a99cba080476645453"; 
		
		Clima(double lat, double lng)
		{
			this.lat = lat;
			this.lng = lng;
			
		}
		
		
		@Override
		protected Boolean doInBackground(String... params) {
			
			
			Log.i(INFO, "prefecht clima");
			String formated = String.format(URL_WEATHER, Double.toString(this.lat), Double.toString(this.lng));
			Log.i(INFO, "formated: " + formated);
			
			String responseStr = null;

			
			if(this.internet)
			{
			

				//realizamos la peticion al servidor
				HttpClient client = new DefaultHttpClient();
				HttpGet request = new HttpGet();



				Log.i("WEATHER", "c");
				try {
					request.setURI(new URI(formated));
					HttpResponse response = client.execute(request);

					Log.i("INFO", "post fetch wheather");

					HttpEntity entity = response.getEntity();
					Log.i(INFO,"response = " + response);
					Log.i(INFO,"enttity = " + entity);
					responseStr = EntityUtils.toString(entity);					

				} catch (Exception e) {
					Log.e("CLIMA", "exception " + e);
					this.mensaje = "No se puede contactar con el servidor del clima";
					return false;
				}


				//parse json
				try{
					Log.i(INFO, "ResponseStr: "+ responseStr);
					JSONObject json = new JSONObject(responseStr);
					
					String message = json.getString("message");
					JSONArray list = json.getJSONArray("list");
					
					//Log.i("clima", "main: "+main);
					JSONObject elemList = list.getJSONObject(0);
					JSONArray listWeather = elemList.getJSONArray("weather");
					JSONObject weather = listWeather.getJSONObject(0);
					
										
					/*
					JSONArray list = json.getJSONArray("list");
					JSONArray wheatherList = list.getJSONObject(0).getJSONArray("wheather");
					
					JSONObject wheather = list.getJSONObject(0);*/
					
					this.clima = weather.getString("main");
					this.descripcion = weather.getString("description");
					this.icon = "i"+weather.getString("icon");
					Log.i("clima", "clima: "+ this.icon);

					return true;

				}catch(Exception e){

					Log.i(INFO,"Exception: "+e);
					return false;
				}
			
			}else{				
				this.clima = "Clear";
				this.descripcion = "Sky is Clear";
				this.icon = "i09d";			
				
				return true;
			}
		}
		
		
		
		@SuppressLint("NewApi")
		protected void onPostExecute(Boolean response) {
			
			if(response)
			{				
				if(this.icon.equals("i01d")){
					imagen.setImageResource(R.drawable.i01d);
				}
				else if(this.icon.equals("i02d")){
					imagen.setImageResource(R.drawable.i02d);
				}
				else if(this.icon.equals("i03d")){
					imagen.setImageResource(R.drawable.i03d);
				}
				else if(this.icon.equals("i04d")){
					imagen.setImageResource(R.drawable.i04d);
				}
				else if(this.icon.equals("i05d")){
					imagen.setImageResource(R.drawable.i04d);
				}
				else if(this.icon.equals("i10d")){
					imagen.setImageResource(R.drawable.i10d);
				}
				else if(this.icon.equals("i11d")){
					imagen.setImageResource(R.drawable.i11d);
				}
				else if(this.icon.equals("i13d")){
					imagen.setImageResource(R.drawable.i13d);
				}
				else if(this.icon.equals("i50d")){
					imagen.setImageResource(R.drawable.i50d);
				}
				else
					Log.i("CLIMA", "else");
				
			}else{
				Log.i("INFO", "Error obteniendo el clima");
			}
		}
		
	}
	
	
	
	class GetOthers extends AsyncTask<String, Integer, Boolean>{

		static final String OTHERS_URL = "http://spotissserver.alwaysdata.net/otros/";
		
		String responseStr;
		
		String mensaje;
		
		
		@Override
		protected Boolean doInBackground(String... params) {
			//realizamos la peticion al servidor
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet();




			try {
				request.setURI(new URI(OTHERS_URL));
				HttpResponse response = client.execute(request);

				

				HttpEntity entity = response.getEntity();
				
				this.responseStr = EntityUtils.toString(entity);
				return true;
			} catch (Exception e) {

				this.mensaje = "No se puede contactar con el servidor del clima";
				return false;
			}

		}
		
		protected void onPostExecute(Boolean response) {
			if(response)
			{
				//parse
				
				try {
					
					JSONObject json = new JSONObject(this.responseStr);
					
					JSONArray array = json.getJSONArray("list");
					
					for(int i = 0; i < array.length(); i++)
					{
						double lat = array.getJSONObject(i).getDouble("lat");
						double lng = array.getJSONObject(i).getDouble("lng");
						
						addmarker(lat, lng, "hola", "mundo");
					}
					
					
				} catch (Exception e) {					
					e.printStackTrace();
				}
				
				
			}else{
				Log.i("OTHERS", this.mensaje);
			}
			
		}		
	}	
}
	





