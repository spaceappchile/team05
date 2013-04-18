package com.generic.spotapp;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

public class OpenNotifyClient {

	private final String PASS_TIME_URL = "http://api.open-notify.org/iss/?n=%i&lat=%f&lon=%f";
	private final String CURRENT_URL = "http://api.open-notify.org/iss-now/";
	private final String PEOPLE_SPACE = "http://api.open-notify.org/astros/v1/";
	
	
	
	public OpenNotifyClient()
	{
		
	
	}
	
	
	public Coordinate getNow() throws ClientProtocolException, URISyntaxException, IOException, Error404, JSONException, ErrorOpenNofify
	{
		RestClient client = new RestClient(this.CURRENT_URL);
		
		String response = client.performGet();
		JSONObject json = new JSONObject(response);
		
		String message = json.getString("message");
		
		if(message.equals("success"))
		{
			JSONObject position = json.getJSONObject("iss_position");
			
			float lat, lng;
			
			lat = Float.parseFloat(position.getString("latitude"));
			lng = Float.parseFloat(position.getString("longitude"));
			
			return new Coordinate(lat, lng);
			
		}else{
			throw new ErrorOpenNofify(message);
		}
	}
}



class Coordinate{
	
	
	//coordinates in decimal degrees, like the coordinates in google maps
	final float lat, lng;
	
	
	Coordinate(float lat, float lng)
	{
		this.lat = lat;
		this.lng = lng;
	}
	
	

}


class ErrorOpenNofify extends Exception {
    public ErrorOpenNofify(String message) {
        super(message);
    }
}