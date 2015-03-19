package jb.smsmedia.iparliament.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JSONParser {

	public static JSONObject getJSONfromURL(String url) throws Exception{
		InputStream is = null;
		String result = "";
		JSONObject jArray = null;
	
	            HttpClient httpclient = new DefaultHttpClient();
	            HttpGet httpget = new HttpGet(url);
	            HttpResponse response = httpclient.execute(httpget);
	            HttpEntity entity = response.getEntity();
	            is = entity.getContent();

	            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
	            StringBuilder sb = new StringBuilder();
	            String line = null;
	            while ((line = reader.readLine()) != null) {
	                    sb.append(line + "\n");
	            }
	            is.close();
	            result=sb.toString();

	    	
            jArray = new JSONObject(result);            

	    return jArray;
	}
	
	public static String getJSONStringfromURL(String url) throws Exception{
		InputStream is = null;
		String result = "";
	
	            HttpClient httpclient = new DefaultHttpClient();
	            HttpGet httpget = new HttpGet(url);
	            HttpResponse response = httpclient.execute(httpget);
	            HttpEntity entity = response.getEntity();
	            is = entity.getContent();

	            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
	            StringBuilder sb = new StringBuilder();
	            String line = null;
	            while ((line = reader.readLine()) != null) {
	                    sb.append(line + "\n");
	            }
	            is.close();
	            result=sb.toString();
	    return result;
	}
		
	public static JSONObject postJSONtoURL(String url, String type, String json) throws Exception{
		InputStream is = null;
		String result = "";
	
	            HttpClient httpclient = new DefaultHttpClient();
	            HttpPost httppost = new HttpPost(url);
	           
	            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	            nameValuePairs.add(new BasicNameValuePair("type", type));
	            nameValuePairs.add(new BasicNameValuePair("data", json));
	            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	           
	            HttpResponse response = httpclient.execute(httppost);
	            HttpEntity entity = response.getEntity();
	            is = entity.getContent();

	            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
	            StringBuilder sb = new StringBuilder();
	            String line = null;
	            while ((line = reader.readLine()) != null) {
	                    sb.append(line + "\n");
	            }
	            is.close();
	            result=sb.toString();                      

	            JSONObject jsonObj = new JSONObject(result);
	    return jsonObj;
	}
	
	public static JSONObject uploadImage(String url, String type, String extras, String image) throws Exception{
		InputStream is = null;
		String result = "";
	
	            HttpClient httpclient = new DefaultHttpClient();
	            HttpPost httppost = new HttpPost(url);
	           
	            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	            nameValuePairs.add(new BasicNameValuePair("type", type));
	            nameValuePairs.add(new BasicNameValuePair("data", extras));
	            nameValuePairs.add(new BasicNameValuePair("image", image));
	            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	           
	            HttpResponse response = httpclient.execute(httppost);
	            HttpEntity entity = response.getEntity();
	            is = entity.getContent();

	            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
	            StringBuilder sb = new StringBuilder();
	            String line = null;
	            while ((line = reader.readLine()) != null) {
	                    sb.append(line + "\n");
	            }
	            is.close();
	            result=sb.toString();                      

	            JSONObject jsonObj = new JSONObject(result);
	    return jsonObj;
	}
	
	
}
