package org.v11.weibo.spider;

import java.io.IOException;
import java.net.MalformedURLException;

import org.v11.weibo.utils.SpiderLog;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;

/**
 * 微博登陆器
 * 单例模式
 * @author v11
 * @date 2014年8月25日
 * @version 1.0
 */
public class LoginManager {
	private final String loginUrl = "http://login.weibo.cn/login/";
	private WebClient webClient;
	private static LoginManager uniqueLogin;
	private LoginManager(){
		initClient();
		login();
	}
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
		webClient.getOptions().setJavaScriptEnabled(false);
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
			loginHref.click();

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
	/**
	 * 获取单例
	 * @return 登陆管理器
	 */
	public static LoginManager getUniqueLogin(){
		if(uniqueLogin == null){
			uniqueLogin = new LoginManager();
		}
		return uniqueLogin;
	}
	private void showPage(HtmlPage page){
		SpiderLog.info(page.asText());
		SpiderLog.info(page.asXml());
	}
	public WebClient getWebClient(){
		return this.webClient;
	}
}
