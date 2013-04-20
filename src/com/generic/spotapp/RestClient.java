//package com.generic.spotapp;
//
//
//import java.io.IOException;
//import java.net.URI;
//import java.net.URISyntaxException;
//
//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.client.ClientProtocolException;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpDelete;
//import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.client.methods.HttpPut;
//import org.apache.http.client.methods.HttpRequestBase;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.util.EntityUtils;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import android.content.Context;
//import android.os.AsyncTask;
//
//
//
//
//
//
//class RestClient extends AsyncTask<String, Integer, String> {
//
//
//        String url;
//        HttpClient client;
//
//        RestClient(String url)
//        {
//                this.url = url;
//                this.client = new DefaultHttpClient();
//        }
//        
//        
//        
//        
//
//        
//
//        
//        private String execute(HttpRequestBase request) throws URISyntaxException, ClientProtocolException, IOException, Error404
//        {
//        	  request.setURI(new URI(this.url));
//        	  URI url = request.getURI();
//              HttpResponse response = this.client.execute(request);
//              
//              switch(response.getStatusLine().getStatusCode())
//              {
//              	case 404:
//              		throw new Error404("Error 404");
//              		
//              
//              }
//              
//              HttpEntity entity = response.getEntity();
//              return EntityUtils.toString(entity);
//              
//              
//
//                
//        }
//        
//        private String performGet() throws ClientProtocolException, URISyntaxException, IOException, Error404
//        {
//        	HttpGet request = new HttpGet();
//        	return this.execute(request);
//        }
//        
//        private String performPut() throws ClientProtocolException, URISyntaxException, IOException, Error404
//        {
//        	HttpPut request = new HttpPut();
//        	return this.execute(request);
//        	
//        	
//        
//        }
//        
//        private String performDelete() throws ClientProtocolException, URISyntaxException, IOException, Error404
//        {
//        	HttpDelete request = new HttpDelete();
//        	return this.execute(request);
//        }
//        
//        private String performPost() throws ClientProtocolException, URISyntaxException, IOException, Error404
//        {
//        	HttpPost request = new HttpPost();
//        	return this.execute(request);
//        }
//
//
//		@Override
//		protected String doInBackground(String... method) {
//			// TODO Auto-generated method stub
//			
//			if(method.length != 1)
//			{
//				return null;
//			}else{
//				String met = method[0];
//				
//				try{
//					if(met.equals("GET"))
//					{
//						return this.performGet();
//					}else if(met.equals("PUT"))
//					{
//						return this.performPut();
//					}else if(met.equals("POST"))
//					{
//						return this.performPost();
//					}else if(met.equals("DELETE"))
//					{
//						return this.performDelete();
//					}else{
//						return null;
//					}
//					
//				}catch(Exception e){
//					return null;
//				}
//		}
//				
//	}
//}
//        
//
//
//
//class Error404 extends Exception {
//    public Error404(String message) {
//        super(message);
//    }
//}
//
