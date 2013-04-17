package com.generic.spotapp;


import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;

public class MapActivity extends FragmentActivity {
	
	private GoogleMap mMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		setUpMapIfNeeded();
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
        mMap.addMarker(new MarkerOptions().position(new LatLng(30, 70)).title("Marker"));
        
    }
	
	private void showMarket(double lat, double lon, String mensaje){
				
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
	
}
