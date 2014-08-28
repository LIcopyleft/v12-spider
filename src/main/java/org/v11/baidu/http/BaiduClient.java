package org.v11.baidu.http;

import java.io.IOException;
import java.net.URLEncoder;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.WebClient;

/**
 * BaiduClient
 * @author v11
 * @date 2014年8月28日
 * @version 1.0
 */
public class BaiduClient implements Client{
	private final Logger log = Logger.getLogger(BaiduClient.class.getName());
	private String RefererString = null;
	private HttpClient client = new DefaultHttpClient();
	private final String LOGIN_URL = "http://www.baidu.com/";
	public BaiduClient(){
		client = new DefaultHttpClient();
	}
	
	public HttpClient getClient() {
		return client;
	}
	/**
	 * 释放掉res的资源，在每次连接后
	 * @param res
	 */
	private void release(HttpResponse res){
		try {
			EntityUtils.consume(res.getEntity());
		} catch (IOException e) {
			log.info(e.toString());
		}
	}
	/**
	 * 为HttpGet添加报文头信息
	 * @param url
	 * @return 
	 */
	private HttpGet addHttpGetWithHeader(String url){
		HttpGet httpGet = new HttpGet(url);
		httpGet.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; rv:16.0) Gecko/20100101 Firefox/16.0");
		if(RefererString == null){
			httpGet.setHeader("Referer", LOGIN_URL);
		}
		else {
			httpGet.setHeader("Referer", RefererString);
		}
		httpGet.setHeader("Content-Type", "application/x-www-form-urlencoded");
		RefererString = url ;
		return httpGet;
	}
	/**
	 * 添加post请求报文头信息
	 * @param url
	 * @return
	 */
	private HttpPost addHttpPostWithHeader(String url){
		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; rv:16.0) Gecko/20100101 Firefox/16.0");
		if(RefererString == null){
			httpPost.setHeader("Referer", LOGIN_URL);
		}
		else {
			httpPost.setHeader("Referer", RefererString);
		}
		httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
		RefererString = url ;
		return httpPost;
	}
	/**
	 * 打印出报文头+html信息
	 * @param res
	 */
	private void showResponse(HttpResponse res){
		showHeaders(res);
		showResponseBody(res);
	}
	/**
	 * 打印出response的页面信息
	 * @param res
	 */
	private String showResponseBody(HttpResponse res){
		HttpEntity entity = res.getEntity();
		if(entity != null){
			String content;
			try {
				content = EntityUtils.toString(entity,"UTF-8");
				log.info(content);
				return content;
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	/**
	 * 打印response出报文头信息
	 * @param response
	 */
	private void showHeaders(HttpResponse response){
		log.debug("---------------------------------");
		Header[] headers = response.getAllHeaders();
    	for(Header header:headers){
    		log.debug("key; "+header.getName()
    				+" value:"+header.getValue());
    	}
	}
	/**
	 * 转化为搜索信息
	 * @param searchWords
	 * @return
	 */
	private String getSearchString(String search){
		String[] strs = search.split(" ");
		String resString ="%20";
		for(String str:strs){
			resString += URLEncoder.encode(str)+"%20";
		}
		return resString;
	}
	/**
	 * 获取页面搜索结果
	 * @param search 搜索关键词
	 * @return 结果
	 */
	public String getPage(String search) {
		// TODO Auto-generated method stub
		String url = "http://www.baidu.com/s?wd="+getSearchString(search)+"&rsv_spt=1"
				+ "&issp=1&rsv_bp=0&ie=utf-8&tn=baiduhome_pg&inputT=1305&rn=100";
		log.info(url);
		try {
			HttpGet get = addHttpGetWithHeader(url);
			HttpResponse res = client.execute(get);
			String resString = showResponseBody(res); 
			release(res);
			return resString;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static void main(String[] args) {
		Client client = new BaiduClient();
		client.getPage("海南航空 投诉");
	}
}
