package org.v11.baidu.fetcher;

import java.util.List;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * 百度搜索页面抓取器 
 * @author v11
 * @date 2014年8月28日
 * @version 1.0
 */
public interface PageFetcher {
	public HtmlPage getPage(String searchWords);
}
