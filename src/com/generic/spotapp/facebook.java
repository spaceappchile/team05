package com.generic.spotapp;

import java.net.URI;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;



//llamar con: new facebook("I can see now the ISS", this).execute();
public class facebook extends AsyncTask<String, Integer, Boolean>{

	static private final String FACEBOOK = "https://graph.facebook.com/me/feed?access_token=%s&message=%s";
	
	static private final String ACCESS_TOKEN = "BAAHbYnoO8akBAGASFw1fIQq1nJ54etZCjBTGR2zAF74f8rZCmBAW3BHFPXXH4vIcKGCtVolOZBZA5zZBXxoXkYLtcHAohs1qoLDov5eu4ESC2Euuya4Mjh7oSK8UHCe0r4X1LeSxm264XZADVzWjnPylH0tqYg6C5EuzH7NTCjCfW3GaUQpEhlZBLe3bl2JqLxwlEvBDN4WBjGwn5b2QaZB0";
	
	
	
	
	
	
	String mensaje;

	String post;
	
	facebook(String post)
	{
		
		this.post = post;
	}
	
	
	@Override
	protected Boolean doInBackground(String... arg0) {
		
		//realizamos la peticion al servidor
		HttpClient client = new DefaultHttpClient();
		HttpPost request = new HttpPost();
		
		Log.i("INFO", "Posteando en FB");
		String formated = String.format(FACEBOOK, ACCESS_TOKEN, URLEncoder.encode(this.post));

		try {
			request.setURI(new URI(formated));
			HttpResponse response = client.execute(request);

			HttpEntity entity = response.getEntity();
			this.mensaje = EntityUtils.toString(entity);
			return true;

		} catch (Exception e) {
			
			this.mensaje = "No se puede contactar con el servidor de FB " + formated;
			return false;
		}
		
	
	}
	
	
	protected void onPostExecute(Boolean response) {
		
		if(response)
		{
			try {
				JSONObject json = new JSONObject(this.mensaje);
				JSONObject error = json.getJSONObject("error");
				Log.i("INFO", error.getString("message"));
				return;
			} catch (JSONException e) {
				
				Log.i("INFO", "TODO OK");
			}
			
			
			
			
		}else{
			Log.i("INFO", this.mensaje);
			
		}
		
	}
}
