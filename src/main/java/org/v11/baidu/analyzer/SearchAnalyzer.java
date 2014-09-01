package org.v11.baidu.analyzer;

import java.util.*;

public interface SearchAnalyzer {
	/**
	 * 网页快照url统计信息
	 * @return
	 */
	public List<String> getUrlInfo(String searchWord);
	/**
	 * 关键字统计信息
	 * @return
	 */
	public List<String> getKeywordInfo(String searchWord);
	/**
	 * 获取网页快照文本内容
	 * @return
	 */
	public List<String> getContentInfo(String searchWord);
	/**
	 * 获取百度中Url映射
	 * @return
	 */
	public List<String> getBaiduUrlInfo(String searchWord);
	public String getRealUrl(String baiduUrl);

}
