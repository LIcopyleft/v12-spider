package org.v11.baidu.http;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.WebClient;
/**
 * WebClient管理类
 * 单例
 * @author v11
 * @date 2014年8月28日
 * @version 1.0
 */
public class WebClientManager {
	private WebClient webClient;
	private static WebClientManager unique;
	private WebClientManager(){
		initClient();
	}
	public static WebClientManager getUnique(){
		if(unique == null){
			unique = new WebClientManager();
		}
		return unique;
	}
	public WebClient getWebClient(){
		return webClient;
	}
	/**
	 * 初始化一个webClient
	 */
	private void initClient() {
		// 模拟一个浏览器
		webClient = new WebClient(BrowserVersion.FIREFOX_24);
		// 设置webClient的相关参数
		webClient.getOptions().setJavaScriptEnabled(false);
		webClient.getOptions().setCssEnabled(false);
		webClient.getOptions().setTimeout(35000);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		CookieManager cookieMan = webClient.getCookieManager();
		cookieMan.setCookiesEnabled(true);
		webClient.getOptions().setJavaScriptEnabled(false);
	}
}
