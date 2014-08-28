package org.v11.baidu.analyzer;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.v11.baidu.fetcher.PageFetcher;
import org.v11.baidu.fetcher.PageFetcherImpl;
import org.v11.utils.MapTools;
/**
 * 解析抓取结果
 * @author v11
 * @date 2014年8月28日
 * @version 1.0
 */
public class SearchAnalyzer {
	private static Logger log = Logger.getLogger(SearchAnalyzer.class.getName());
	private final Integer MaxNum = 100;
	private final String Error = null;
	private HashMap<String, Integer> urlMap = new HashMap<String, Integer>();
	private HashMap<String, Integer> keywordMap = new HashMap<String, Integer>();
	/**
	 * 解析来源地址
	 * @param e
	 * @return
	 */
	private String getUrl(Element e){
		Elements es = e.getElementsByClass("f13");
		if(es == null || es.size() == 0)
			return Error;
		Element d = es.get(0).getElementsByClass("g").get(0);
		return d.text().split("/")[0];
		
	}
	/**
	 * 解析主要内容
	 * @param e
	 * @return
	 */
	private String getMainContent(Element e){
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
	public void work(String search){
		PageFetcher pFetcher = new PageFetcherImpl();
		
		Document doc = Jsoup.parse(pFetcher.getPage(search).asXml());
		for(int i = 1;i<=2;i++){
			Element e = doc.getElementById(i+"");
			if(e == null) continue;
			MapTools.setCountToMap(urlMap, getUrl(e), 1);
			String content = getMainContent(e);
			String words[] = content.split(" ");
			for(String word : words){
				MapTools.setCountToMap(keywordMap, word, 1);
			}
		}
	}
	
	public HashMap<String, Integer> getUrlMap() {
		return urlMap;
	}
	public void setUrlMap(HashMap<String, Integer> urlMap) {
		this.urlMap = urlMap;
	}
	public HashMap<String, Integer> getKeywordMap() {
		return keywordMap;
	}
	public void setKeywordMap(HashMap<String, Integer> keywordMap) {
		this.keywordMap = keywordMap;
	}
	public static void main(String[] args) {
		String search = "海南航空 投诉";
		SearchAnalyzer sa = new SearchAnalyzer();
		sa.work(search);
	}
}
