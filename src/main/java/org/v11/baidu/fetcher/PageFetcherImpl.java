package org.v11.baidu.fetcher;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.List;

import org.apache.log4j.Logger;
import org.v11.baidu.http.*;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
/**
 * 抓取页面
 * @author v11
 * @date 2014年8月28日
 * @version 1.0
 */
public class PageFetcherImpl implements PageFetcher{
	private static Logger log = Logger.getLogger(PageFetcherImpl.class.getName());
	private WebClient client ;
	private final String URL = "http://www.baidu.com/";
	public PageFetcherImpl(){
		client = WebClientManager.getUnique().getWebClient();
	}
	private String getSearchString(String searchWords){
		String[] strs = searchWords.split(" ");
		String resString ="%20";
		for(String str:strs){
			resString += URLEncoder.encode(str)+"%20";
		}
		return resString;
	}
	public HtmlPage getPage(String searchWords) {
		String url = "http://www.baidu.com/s?wd="+getSearchString(searchWords)+"&rsv_spt=1"
				+ "&issp=1&rsv_bp=0&ie=utf-8&tn=baiduhome_pg&inputT=1305&rn=100";
		log.info(url);
		
		try {
			HtmlPage searchHtmlPage = client.getPage(url);
			
			return searchHtmlPage;
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
		return null;
	}
	public static void main(String[] args) {
		PageFetcher pageFetcher = new PageFetcherImpl();
		log.info(pageFetcher.getPage("海南航空 投诉").asXml());
	}

}
