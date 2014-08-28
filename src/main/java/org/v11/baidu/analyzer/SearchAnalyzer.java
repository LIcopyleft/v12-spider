package org.v11.baidu.analyzer;

import java.util.HashMap;

public interface SearchAnalyzer {
	/**
	 * url统计信息
	 * @return
	 */
	public HashMap<String, Integer> getUrlInfo(String searchWord);
	/**
	 * 关键字统计信息
	 * @return
	 */
	public HashMap<String, Integer> getKeywordInfo(String searchWord);
	/**
	 * 获取分类后的信息
	 * blog
	 * bbs
	 * news
	 * other
	 * @return
	 */
	public HashMap<String, Integer> getTypeInfo(String searchWord);
	/**
	 * 获取分类后的公司
	 * blog
	 * bbs
	 * news
	 * other
	 * @return
	 */
	public HashMap<String, Integer> getCompanyInfo(String searchWord);


}
