package org.v11.baidu.analyzer;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.v11.baidu.fetcher.PageFetcher;
import org.v11.baidu.fetcher.PageFetcherImpl;
import org.v11.baidu.utils.Contants;
import org.v11.utils.MapTools;
/**
 * 解析抓取结果
 * @author v11
 * @date 2014年8月28日
 * @version 1.0
 */
public class SearchAnalyzerImpl implements SearchAnalyzer{
	private static Logger log = Logger.getLogger(SearchAnalyzerImpl.class.getName());
	private final Integer MaxNum = 50;
	private final String Error = null;
	private List<String> urls = new ArrayList<String>();
	private List<String> baiduUrls = new ArrayList<String>();
	/*
	 * 搜索内部hidden的推荐值
	 */
	private List<String> keywords = new ArrayList<String>();
	/*
	 * 主要内容提要
	 */
	private List<String> contents = new ArrayList<String>();
	private String searchHasDone = null;
	/**
	 * 转化Url
	 * 将//www.baidu.com/link?url=0Fnlt86uqBcGsWpOaj58-d2rheFK6cbVeoE6oDF1tMYm-B5z1Q6h5RkVWGjYFk5M"
	 * 变为正常的Url
	 * 速度太慢，要去请求百度
	 * @param baiduUrl
	 * @return
	 */
	public String getRealUrl(String baiduUrl){
		try {  
	        String str="http:"+baiduUrl;  
	        URL url = new URL(str);  
	        HttpURLConnection conn=(HttpURLConnection)url.openConnection();  
	        conn.getResponseCode();  
	        String realUrl=conn.getURL().toString();  
	        conn.disconnect();  
	        return realUrl;
	    } catch (MalformedURLException e) {  
	        e.printStackTrace();  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    }
		catch(Exception e){
			return null;
		}
		return null;
	}
	/**
	 * 不好用
	 * 速度太慢，要去请求百度
	 * @param e
	 * @return
	 */
	private String getBaiduUrl(Element e){
		Elements es = e.getElementsByClass("t");
		if(es == null || es.size() == 0)
			return Error;
		try {
			String d = es.get(0).select("a").get(0).attr("href");
			//log.info(getRealUrl(d));
			return getRealUrl(d);
		} catch (Exception ex) {
			log.error(es.size()+" "+e.toString());
		}
		return null;
	}
	/**
	 * 解析来源地址
	 * @param e
	 * @return
	 */
	private String getUrl(Element e){
		Elements es = e.getElementsByClass("f13");
		if(es == null || es.size() == 0)
			return Error;
		try {
			Element d = es.get(es.size()-1).getElementsByClass("g").get(0);
			return d.text().split("/")[0];
		} catch (Exception ex) {
			log.error(es.size()+" "+e.toString());
		}
		return null;
		
		
	}
	/**
	 * 解析hidden推荐词
	 * @param e
	 * @return
	 */
	private String getKeyword(Element e){
		/*
		 * c-gap-top c-recommend
		 * 空格处理方法，抓前面的
		 */
		Elements es = e.getElementsByClass("c-gap-top");
		if(es == null || es.size() == 0)
			return Error;
		Element div = es.get(0);
		return div.attr("data-extquery");
	}
	/**
	 * 获取内容提要
	 * @param e
	 * @return
	 */
	public String getContent(Element e){
		Elements es = e.getElementsByClass("c-abstract");
		if(es == null || es.size() == 0)
			return Error;
		Element div = es.get(0);
		return div.text();
	}
	public synchronized void work(String search){
		searchHasDone = search;
		PageFetcher pFetcher = new PageFetcherImpl();
		Document doc = Jsoup.parse(pFetcher.getPage(search).asXml());
		for(int i = 1;i<=MaxNum;i++){
			Element e = doc.getElementById(i+"");
			if(e == null) continue;
			String url = getUrl(e);
			String keyword = getKeyword(e);
			String content = getContent(e);
			String baiduUrl = getBaiduUrl(e);
			if(baiduUrl != null) baiduUrls.add(i+Contants._split+baiduUrl);
			if(url != null) urls.add(i+Contants._split+url);
			if(keyword != null) keywords.add(keyword);
			if(content != null) contents.add(content);
			
			
		}
	}

	
	private void cleanUp(){
		urls.clear();
		keywords.clear();
		contents.clear();
		baiduUrls.clear();
	}
	public List<String> getUrlInfo(String searchWord) {
		if(!searchWord.equals(searchHasDone)){
			cleanUp();
			work(searchWord);
		}
		return urls;
	}
	public List<String> getKeywordInfo(String searchWord) {
		if(!searchWord.equals(searchHasDone)){
			cleanUp();
			work(searchWord);
		}
		return keywords;
	}
	public List<String> getContentInfo(String searchWord) {
		if(!searchWord.equals(searchHasDone)){
			cleanUp();
			work(searchWord);
		}
		return contents;
	}
	
	public List<String> getBaiduUrlInfo(String searchWord) {
		if(!searchWord.equals(searchHasDone)){
			cleanUp();
			work(searchWord);
		}
		return baiduUrls;
	}
	public static void main(String[] args) {
		SearchAnalyzerImpl sa = new SearchAnalyzerImpl();
		sa.work("海航实业 垃圾");
	}
}
