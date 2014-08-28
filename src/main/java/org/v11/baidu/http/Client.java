package org.v11.baidu.http;
/**
 *  百度搜索页面抓取器
 * @author v11
 * @date 2014年8月28日
 * @version 1.0
 */
public interface Client {
	/**
	 * 获取页面搜索结果
	 * @param search 搜索关键词
	 * @return 结果
	 */
	public String getPage(String search);
	
}
