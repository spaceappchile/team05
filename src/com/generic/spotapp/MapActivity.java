package com.generic.spotapp;

/* TODO crear funcion que tome la base de datos con los avistamientos y
 * 		crea las marcas
 * TODO mostrar info en los marcadores creados, la fecha, 
 *
 */
		

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

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
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;

public class MapActivity extends FragmentActivity {
	
	private static final String INFO = "INFO";
	private static final int DIALOGO_CONFIRMACION = 0;
	
	private GoogleMap mMap;
	private LocationListener listener;
	private LocationManager locationManager;
	private Location loc;
	private Criteria criteria;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);	
		
		gpsOn();
		setUpMapIfNeeded();	
	}
	
	@Override
    protected void onResume() {
		super.onResume();
        setUpMapIfNeeded();
        
        getKnownPos();
        getActualPos();
    }
	
	@Override
	protected void onStop(){
		super.onStop();
		// Remove the listener you previously added
		locationManager.removeUpdates(listener);
		Log.i(INFO, "no obtener posicion");
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map, menu);
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
		Location loc = mMap.getMyLocation();
		
		if(loc!=null)
			changePosCam2(loc);
		else
			Log.i(INFO,"Mylocation is null");
		
		addmarkers();
		dibujaEstacion();
		
    }

	private void changeModeMap(int mode){
		
		switch(mode){
			case 0:
				mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
	            break;
			case 1:
				mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
	            break;
			case 2:
				mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
	            break;
			case 3:
				mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
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
		    	//showMarket(location,"Tu posicion");			    		    	
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
		addmarker(-33.44989201, -70.68687829, "Avistamiento", "el dia ...");	
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
	
	private void dibujaEstacion(){		
		LatLng l1 = new LatLng(0,0);
		LatLng l2 = new LatLng(45,5);
		mMap.addPolyline((new PolylineOptions())
               .add(l1, l2)
                .width(5)
                .color(Color.BLUE)
                .geodesic(true));
	}	
}
	





