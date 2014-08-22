package org.v11.spider;

import java.io.IOException;
import java.net.MalformedURLException;
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

public class SinaTopicMonitorThread extends Thread {
	private final String loginUrl = "http://login.weibo.cn/login/";
	private WebClient webClient;
	private HtmlPage advanceSearch;
	public List<String> keyWords;
	/**
	 * 初始化一个webClient
	 */
	private void initClient() {
		// 模拟一个浏览器
		webClient = new WebClient(BrowserVersion.CHROME);
		// 设置webClient的相关参数
		webClient.getOptions().setJavaScriptEnabled(false);
		webClient.getOptions().setCssEnabled(false);
		webClient.getOptions().setTimeout(35000);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		CookieManager cookieMan = webClient.getCookieManager();
		cookieMan.setCookiesEnabled(true);
	}
	/**
	 * 登陆微博
	 * 初始化 advanceSearch 的值，该值用于后续的搜索中
	 */
	private void login(){
		try {
			HtmlPage loginPage = webClient.getPage(loginUrl);

			HtmlInput userName = loginPage.getElementByName("mobile");
			userName.setValueAttribute("442629928@qq.com");
			HtmlInput password = loginPage
					.getFirstByXPath("/html/body/div[2]/form/div/input[2]");
			password.setValueAttribute("wywywy1991");
			HtmlSubmitInput loginHref = loginPage.getElementByName("submit");
			HtmlPage resultPage = loginHref.click();
			HtmlElement searchHref = resultPage
					.getFirstByXPath("//a[text()='搜索']");
			HtmlPage searchPage = searchHref.click();
			HtmlElement advanceSearchElement = searchPage
					.getFirstByXPath("//a[text()='高级搜索>>']");
			advanceSearch = advanceSearchElement.click();
		} catch (FailingHttpStatusCodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void search() {
		try {
			HtmlElement origin = advanceSearch.getElementByName("hasori");
			origin.click();// 点击原创
			for (int i = 0; i < keyWords.size(); i++) {
				try {
					getSearchResult(keyWords.get(i));
				} catch (Exception e) {
					SpiderLog.error("获取关键词" + keyWords.get(i) + "的搜索结果失败！");
				}
				Thread.sleep(2200 + 1000 * (new Random()).nextInt(6));
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
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
			initClient();
			login();
			search();
			

//			
//			HtmlPage rootPage = (HtmlPage) webClient.getCurrentWindow()
//					.getEnclosedPage();
//			rootPage = webClient.getPage(loginUrl);
//			HtmlElement userName = rootPage.getElementByName("mobile");
//			userName.click();
//			userName.type("vmojing@163.com");
//			HtmlElement password = rootPage
//					.getFirstByXPath("/html/body/div[2]/form/div/input[2]");
//			password.click();
//			password.type("0744ict0744");
//			HtmlElement loginHref = rootPage.getElementByName("submit");
//			HtmlPage resultPage = loginHref.click();
//			HtmlElement searchHref = resultPage
//					.getFirstByXPath("//a[text()='搜索']");
//			HtmlPage searchPage = searchHref.click();
//			HtmlElement advanceSearchElement = searchPage
//					.getFirstByXPath("//a[text()='高级搜索>>']");
//			advanceSearch = advanceSearchElement.click();
//			HtmlElement origin = advanceSearch.getElementByName("hasori");
//			origin.click();	//点击原创
//			for (int i = 0; i < keyWords.size(); i++) {
//				try {
//					getSearchResult(keyWords.get(i));
//				} catch (Exception e) {
//					System.out.println("获取关键词"+keyWords.get(i)+"的搜索结果失败！");
//				}
//				Thread.sleep(2200+1000*(new Random()).nextInt(6));
//			}
//
//			exit();
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
		HtmlInput keyWordElement = advanceSearch.getElementByName("keyword");
		keyWordElement.setValueAttribute(word);
		HtmlElement keyWordSearchElement = advanceSearch.getFirstByXPath("//input[@value='搜索']");
		HtmlPage searchResult = keyWordSearchElement.click();
		List<HtmlElement> resultWeibos = (List<HtmlElement>) searchResult.getByXPath("//div[@id]");
		int count = 0;
		for (HtmlElement element : resultWeibos) {
			String divId = element.getAttribute("id");
			if (!divId.equals("pagelist")) {
				SpiderLog.info(element.asText());

				count++;
			}
		}
		searchResult.cleanUp();
		searchResult = null;
		SpiderLog.info(word+"关键词获得的搜索结果数为："+count);
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
		SinaTopicMonitorThread topicTread = new SinaTopicMonitorThread();
		topicTread.start();
	}
}
