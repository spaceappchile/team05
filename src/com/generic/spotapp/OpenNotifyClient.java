package com.generic.spotapp;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OpenNotifyClient {

	private final String PASS_TIME_URL = "http://api.open-notify.org/iss/?n=%i&lat=%f&lon=%f";
	private final String CURRENT_URL = "http://api.open-notify.org/iss-now/";
	private final String PEOPLE_SPACE = "http://api.open-notify.org/astros/v1/";
	
	
	
	public OpenNotifyClient()
	{
		
	
	}

	
	public ArrayList<Pass> getPass(int n, float lat, float lng) throws ClientProtocolException, URISyntaxException, IOException, Error404, JSONException, ErrorOpenNofify
	{
		ArrayList<Pass> passArray = new ArrayList();
		
		String url = String.format(PASS_TIME_URL, n, lat, lng);
		
		
		RestClient client = new RestClient(url);
		String response = client.performGet();
		JSONObject json = new JSONObject(response);
		
		String message = json.getString("message");
		if(message.equals(message))
		{
			JSONArray positions = json.getJSONArray("response");
			
			for(int i = 0; i < positions.length(); i++)
			{
				JSONObject pos = positions.getJSONObject(i);
				
				int duration = Integer.parseInt(pos.getString("duration"));
				int risetime = Integer.parseInt(pos.getString("risetime"));
				
				passArray.add(new Pass(duration, risetime));
				
			}
			
			return passArray;
			
		}else{
			throw new ErrorOpenNofify(message);
		}
		
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
	
	/**Calculate the distance between in kilometers using the haversine function */
	public double distance(Coordinate other)
	{
	                                                                                                                          
		     
		     double lon1 = toRadians(this.lng);
		     double lat1 = toRadians(this.lat);
		     double lon2 = toRadians(other.lng);
		     double lat2 = toRadians(other.lat);
		    		 
		    		 
		     // haversine formula                                                                                                                                                  
		     double dlon = lon2 - lon1;                                                                                                                                                  
		     double dlat = lat2 - lat1;                                                                                                                                              
		     double a = Math.pow(Math.sin(dlat/2), 2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(dlon/2), 2);                                                                                                      
		     double c = 2 * Math.asin(Math.sqrt(a));                                                                                                                                           
		     double km = 6367 * c;                                                                                                                                                        
		     return km*1000;
	}
	
	
	private double toRadians(float decimal)
	{
		return decimal * Math.PI / 180;
	}
}

class Pass{
	final int duration, risetime;
	
	Pass(int duration, int risetime)
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