package org.v11.baidu.analyzer;

import java.util.HashMap;

import org.apache.log4j.Logger;
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
	private final Integer MaxNum = 100;
	private final String Error = null;
	private HashMap<String, Integer> urlMap = new HashMap<String, Integer>();
	private HashMap<String, Integer> keywordMap = new HashMap<String, Integer>();
	private HashMap<String, Integer> typeMap = new HashMap<String, Integer>();
	private String searchHasDone = null;
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
	public synchronized void work(String search){
		searchHasDone = search;
		PageFetcher pFetcher = new PageFetcherImpl();
		Document doc = Jsoup.parse(pFetcher.getPage(search).asXml());
		for(int i = 1;i<=100;i++){
			Element e = doc.getElementById(i+"");
			if(e == null) continue;
			String url = getUrl(e);
			MapTools.setCountToMap(urlMap, url, 1);
			MapTools.setCountToMap(typeMap, getUrlType(url), 1);
			String content = getMainContent(e);
			if (content != null) {
				/*
				 * space
				 */
				String words[] = content.split(" ");
				if(words.length ==1){
					words = content.split(" "); // tag
				}
				for (String word : words) {
					MapTools.setCountToMap(keywordMap, word, 1);
				}
			}
		}
	}
	private String getUrlType(String url){
		if(url == null) return null;
		if(url.contains(Contants.NEWS)) return "新闻";
		else if(url.contains(Contants.BBS)) return "BBS";
		else if(url.contains(Contants.BLOG)) return "博客";
		else if(url.contains(Contants._zhidao)) return "百度知道";
		else if(url.contains(Contants._tieba)) return "百度贴吧";
		return "其他类型网站";
	}
	private void cleanUp(){
		urlMap.clear();
		keywordMap.clear();
		typeMap.clear();
	}
	public HashMap<String, Integer> getUrlInfo(String searchWord) {
		if(!searchWord.equals(searchHasDone)){
			cleanUp();
			work(searchWord);
		}
		return urlMap;
	}
	public HashMap<String, Integer> getKeywordInfo(String searchWord) {
		if(!searchWord.equals(searchHasDone))
			work(searchWord);
		return keywordMap;
	}
	public HashMap<String, Integer> getTypeInfo(String searchWord) {
		if(!searchWord.equals(searchHasDone))
			work(searchWord);
		return typeMap;
	}
	public HashMap<String, Integer> getCompanyInfo(String searchWord) {
		// TODO Auto-generated method stub
		return null;
	}
}
