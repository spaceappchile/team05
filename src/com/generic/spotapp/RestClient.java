package com.generic.spotapp;


import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class RestClient {


        String url;
        HttpClient client;

        RestClient(String url)
        {
                this.url = url;
                this.client = new DefaultHttpClient();
        }

        

        
        private String execute(HttpRequestBase request) throws URISyntaxException, ClientProtocolException, IOException, Error404
        {
        	  request.setURI(new URI(this.url));
        	  URI url = request.getURI();
              HttpResponse response = client.execute(request);
              
              switch(response.getStatusLine().getStatusCode())
              {
              	case 404:
              		throw new Error404("Error 404");
              		
              
              }
              
              HttpEntity entity = response.getEntity();
              return EntityUtils.toString(entity);
              
              

                
        }
        
        String performGet() throws ClientProtocolException, URISyntaxException, IOException, Error404
        {
        	HttpGet request = new HttpGet();
        	return this.execute(request);
        }
        
        String performPut() throws ClientProtocolException, URISyntaxException, IOException, Error404
        {
        	HttpPut request = new HttpPut();
        	return this.execute(request);
        	
        	
        
        }
        
        String performDelete() throws ClientProtocolException, URISyntaxException, IOException, Error404
        {
        	HttpDelete request = new HttpDelete();
        	return this.execute(request);
        }
        
        String performPost() throws ClientProtocolException, URISyntaxException, IOException, Error404
        {
        	HttpPost request = new HttpPost();
        	return this.execute(request);
        }
        
}


class Error404 extends Exception {
    public Error404(String message) {
        super(message);
    }
}

