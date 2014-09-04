package org.v11.weibo.mobile.https;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.v11.weibo.utils.SpiderLog;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class TopicWorker extends BasicWorker{
	private final Logger log = Logger.getLogger(this.getClass().getName());
	private HttpClient client;
	private WeiboLoginer loginer;
	public TopicWorker(){
		loginer = new WeiboLoginer();
		client = loginer.getCliet();
	}
	private Integer getMaxPageNum(Document doc){
		Integer res = 100;
		return 100;
	}
	public Set<String> getIds(String word){
		Set<String> ls = new HashSet<String>();
		String wordUrlEncode = URLEncoder.encode(word);
		
		System.out.println(word.length());
		String searchUrl = "http://weibo.cn/search/mblog?hideSearchFrame=&keyword="
				+ wordUrlEncode
				+ "&advancedfilter=1&hasori=1&endtime=20140904&sort=time&page=";
		int count = 0;
		for (int i = 1; i < 2; i++) {
			
			try {
				searchUrl = searchUrl + i;
				log.info(searchUrl);
				HttpGet getSearch = addHttpGetWithHeader(searchUrl);
				HttpResponse res;
				res = client.execute(getSearch);
				Document doc = Jsoup.parse(showResponseBody(res));
				release(res);
				Elements es = doc.getElementsByTag("div");
				for(Element e : es){
					String idName = e.attr("id");
					if(idName.startsWith("M_")){
						//System.out.println(idName);
						System.out.println(idName.substring(idName.indexOf("_") + 1));
					}
					
				}
			} catch (ClientProtocolException e) {
				log.error(e.getMessage());
			} catch (IOException e) {
				log.error(e.getMessage());
			}
			log.info(word + "关键词获得的搜索结果数为：" + count);
		}
		return ls;
	}
	public static void main(String[] args) {
		TopicWorker t = new TopicWorker();
		t.getIds("林治波");
	}
}
