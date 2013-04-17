package com.generic.spotapp;


import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.location.Criteria;
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
	private LatLng position;
	private LocationListener listener;
	private LocationManager locationManager;
	private Criteria criteria;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		setUpMapIfNeeded();
		gpsOn();
	}
	
	@Override
    protected void onResume() {
		super.onResume();
        setUpMapIfNeeded();
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
		 
		    switch(id)
		    {
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
		private Dialog crearDialogoConfirmacion()
		{
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
                setUpMap();
            }
        }
    }
	
	private void setUpMap() {
        showMarket(0,0,"aaaa");
        
    }
	
	private void showMarket(double lat, double lon, String mensaje){
		mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title(mensaje));				
	}

	private void changeView(int mode){
		
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
	
	private void changePos(float lat, float lon){
		CameraUpdate camUpd1 = CameraUpdateFactory.newLatLng(new LatLng(lat, lon));
		mMap.moveCamera(camUpd1);
	}
	public void activarGps(){
		Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	    startActivity(settingsIntent);		
	}
	
	private void gpsOn(){
		// para acceder a los servicios de localizacion
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		
		// criterios
		Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE); // localizacion exacta
        criteria.setCostAllowed(false); 			  // sin costo monetario
        // pregunta si hay un proveedor con estas caracteristicas
        String providerName = locationManager.getBestProvider(criteria, true);
        
        // retorna true, si el gps esta activado		
        final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	
        if(!gpsEnabled){
			// muestra, inicia, crea, (no se que mierda) 
			//el dialogo con el id: DIALOGO_CONFIRMACION
			showDialog(DIALOGO_CONFIRMACION);
			Log.d("PRUEBA GPS", "GPS apagado");
		}
	}
	private void getPos(){
	
	}
}
