package edu.yonsei.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

/**
 * facade object for ApaheHttpClient
 * 
 * @version 1.0
 * @date 2015.10.3.
 * @author speechless
 */
public class HttpClient {
	
	private CloseableHttpClient httpclient;
	private CloseableHttpResponse response;
	private HttpPost httpPost;
	private HttpGet httpGet;
	private int status;
	private HttpEntity entity;
	
	public HttpClient(int timeout) {
		RequestConfig config = RequestConfig.custom().setConnectTimeout(timeout * 1000).setConnectionRequestTimeout(timeout * 1000).setSocketTimeout(timeout * 1000).build();
		httpclient = HttpClients.custom().setDefaultRequestConfig(config).build();
	}
	
	public HttpClient() {
		this(30);
	}
	
	public boolean connectGET(String url) throws ClientProtocolException, IOException {
		httpGet = new HttpGet(url);
		response = httpclient.execute(httpGet);
		status = response.getStatusLine().getStatusCode();
		if (status < 200 || status >= 400) {return false;}
		entity = response.getEntity();
		return true;
	}
	
	public boolean connectGET(URI url) throws ClientProtocolException, IOException {
		httpGet = new HttpGet(url);
		response = httpclient.execute(httpGet);
		status = response.getStatusLine().getStatusCode();
		if (status < 200 || status >= 400) {return false;}
		entity = response.getEntity();
		return true;
	}
	
	public boolean connectGET(String url, HashMap<String, String> header) throws ClientProtocolException, IOException {
		httpGet = new HttpGet(url);
		for (String key : header.keySet()) {httpGet.addHeader(key, header.get(key));}
		response = httpclient.execute(httpGet);
		status = response.getStatusLine().getStatusCode();
		if (status < 200 || status >= 400) {return false;}
		entity = response.getEntity();
		return true;
	}
	
	public boolean connectGET(URI url, HashMap<String, String> header) throws ClientProtocolException, IOException {
		httpGet = new HttpGet(url);
		for (String key : header.keySet()) {httpGet.addHeader(key, header.get(key));}
		response = httpclient.execute(httpGet);
		status = response.getStatusLine().getStatusCode();
		if (status < 200 || status >= 400) {return false;}
		entity = response.getEntity();
		return true;
	}

	public boolean connectPOST(String url, List<NameValuePair> nvps) throws UnsupportedEncodingException {
		httpPost = new HttpPost(url);
		httpPost.setEntity(new UrlEncodedFormEntity(nvps));
		status = response.getStatusLine().getStatusCode();
		if (status < 200 || status >= 400) {return false;}
		entity = response.getEntity();
		return true;
	}
	
	public boolean connectPOST(URI url, List<NameValuePair> nvps) throws UnsupportedEncodingException {
		httpPost = new HttpPost(url);
		httpPost.setEntity(new UrlEncodedFormEntity(nvps));
		status = response.getStatusLine().getStatusCode();
		if (status < 200 || status >= 400) {return false;}
		entity = response.getEntity();
		return true;
	}

	public boolean connectPOST(String url, List<NameValuePair> nvps, HashMap<String, String> header) throws ClientProtocolException, IOException {		
		httpPost = new HttpPost(url);
		for (String key : header.keySet()) {httpPost.addHeader(key, header.get(key));}
		httpPost.setEntity(new UrlEncodedFormEntity(nvps));
		response = httpclient.execute(httpPost);
		status = response.getStatusLine().getStatusCode();
		if (status < 200 || status >= 400) {return false;}
		entity = response.getEntity();
		return true;
	}
	
	public boolean connectPOST(URI url, List<NameValuePair> nvps, HashMap<String, String> header) throws ClientProtocolException, IOException {		
		httpPost = new HttpPost(url);
		for (String key : header.keySet()) {httpPost.addHeader(key, header.get(key));}
		httpPost.setEntity(new UrlEncodedFormEntity(nvps));
		response = httpclient.execute(httpPost);
		status = response.getStatusLine().getStatusCode();
		if (status < 200 || status >= 400) {return false;}
		entity = response.getEntity();
		return true;
	}

	public static ArrayList<NameValuePair> convertNameValuePair(HashMap<String, String> nvp) {
		ArrayList<NameValuePair> nvps = new ArrayList <NameValuePair>();
		for (String key : nvp.keySet()) {nvps.add(new BasicNameValuePair(key, nvp.get(key)));}
		return nvps;
	}
	
	public void close() throws IOException {
		response.close();
		httpclient.close();
	}
	
	public int getStatusCode() {
		return status;
	}
	
	public HttpEntity getHttpEntity() {
		return entity;
	}
}
