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

        

        
        private String execute(HttpRequestBase request) throws URISyntaxException, ClientProtocolException, IOException
        {
        	  request.setURI(new URI(this.url));
              HttpResponse response = client.execute(request);
              HttpEntity entity = response.getEntity();
              return EntityUtils.getContentCharSet(entity);
                
                
        }
        
        String performGet() throws ClientProtocolException, URISyntaxException, IOException
        {
        	HttpGet request = new HttpGet();
        	return this.execute(request);
        }
        
        String performPut() throws ClientProtocolException, URISyntaxException, IOException
        {
        	HttpPut request = new HttpPut();
        	return this.execute(request);
        
        }
        
        String performDelete() throws ClientProtocolException, URISyntaxException, IOException
        {
        	HttpDelete request = new HttpDelete();
        	return this.execute(request);
        }
        
        String performPost() throws ClientProtocolException, URISyntaxException, IOException
        {
        	HttpPost request = new HttpPost();
        	return this.execute(request);
        }
        
}


