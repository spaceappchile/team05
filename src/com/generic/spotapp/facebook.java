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
	
	static private final String ACCESS_TOKEN = "BAAHbYnoO8akBAIZBCuNCFPk0Q13nDxkyCWqdNhaxgZCEMyD6h48YBO6nqMqqZCdUKcZCOgOhoyASYHb4ANmrZCZC4XhyVSEvpGFzZCya7jrFqbYJEDRKQwBNkZCpQM0vMU2Kaen5ZAE1fEHRsdhD3ZBvE9HqVEDWlKoVA3vkZBfpORu9pSZCeGI7x03MSxdAeoZBZCRBZA4MZCBhXB7utwZDZD";
	
	
	
	
	Context context;
	
	String mensaje;

	String post;
	
	facebook(String post, Context context)
	{
		
		this.context = context;
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
