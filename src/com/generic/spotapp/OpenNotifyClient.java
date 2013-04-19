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

	/**Return a list of Pass objects with the future ISS pass at the given coordinates
	 * 
	 * @param n Number of pass objects to return. 1 <= n <= 100
	 * @param lat Latitude of the current position of the device in decimal degrees
	 * @param lng Longitude of the current position of the device in decimal degrees
	 * 
	 * @return A ArrayList of Pass objects in order of time.
	 * */
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
				long risetime = Long.parseLong(pos.getString("risetime"));
				
				passArray.add(new Pass(duration, risetime));
				
			}
			
			return passArray;
			
		}else{
			throw new ErrorOpenNofify(message);
		}
		
	}
	
	/**Get the current coordinates of the ISS
	 * */
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



/**A class of coordinates*/
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