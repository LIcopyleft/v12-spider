package org.v11.spider;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.Set;

import org.bson.types.ObjectId;


import org.cyberneko.html.HTMLElements;
import org.eclipse.jetty.util.UrlEncoded;
import org.eclipse.jetty.util.log.Log;
import org.v11.utils.SpiderLog;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * 话题监测的线程
 * @author Kontrol
 *
 */

public class TopicThread extends Thread {
	
	private WebClient webClient;
	public List<String> keyWords;
	private void search() {
		try {
			for (int i = 0; i < keyWords.size(); i++) {
				try {
					getSearchResult(keyWords.get(i));
				} catch (Exception e) {
					SpiderLog.error("获取关键词" + keyWords.get(i) + "的搜索结果失败！");
				}
				Thread.sleep(2200 + 1000 * (new Random()).nextInt(6));
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void showPage(HtmlPage page){
		SpiderLog.info(page.asText());
		SpiderLog.info(page.asXml());
	}
	public void run() {
		try {
			keyWords = getKeyWords();
			if (keyWords == null || keyWords.size() == 0) {
				return ;
			}
			webClient = LoginManager.getUniqueLogin().getWebClient();
			search();
		} catch (FailingHttpStatusCodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}


	/**
	 * 获取搜索结果
	 * @param word
	 * @throws IOException
	 */
	public void getSearchResult(String word) throws IOException {
		String wordUrlEncode = URLEncoder.encode(word);
		
		System.out.println(word.length());
		String searchUrl = "http://weibo.cn/search/mblog?hideSearchFrame=&keyword="
				+ wordUrlEncode
				+ "&advancedfilter=1&hasori=1&endtime=20140825&sort=time&page=";
		int count = 0;
		for (int i = 1; i < 2; i++) {
			searchUrl = searchUrl + i;
			SpiderLog.info(searchUrl);
			HtmlPage searchResult = webClient.getPage(searchUrl);
			showPage(searchResult);
			List<HtmlElement> resultWeibos = (List<HtmlElement>) searchResult
					.getByXPath("//div[@id]");

			for (HtmlElement element : resultWeibos) {
				String divId = element.getAttribute("id");
				if (!divId.equals("pagelist")) {
					SpiderLog.info(element.asText());
					count++;
				}
			}
			searchResult.cleanUp();
			searchResult = null;
			SpiderLog.info(word + "关键词获得的搜索结果数为：" + count);
		}
	}

	/**
	 * 退出
	 */
	private void exit() {
		if (webClient != null) {
			webClient.closeAllWindows();
		}
	}

	/**
	 * 获取关键词
	 * @return
	 */
	private List<String> getKeyWords() {
		// TODO Auto-generated method stub
		List<String> ls = new ArrayList<String>();
		ls.add("房祖名吸毒被抓");
		return ls;
	}

	
	public static void main(String[] args) {
		TopicThread topicTread = new TopicThread();
		topicTread.start();
	}
}
