package org.v11.baidu.info;
import java.util.Map.Entry;
import java.util.*;

import org.v11.baidu.analyzer.SearchAnalyzer;
import org.v11.baidu.analyzer.SearchAnalyzerImpl;
import org.v11.baidu.utils.Contants;
import org.v11.utils.MapTools;
/**
 * 处理抓取来的String
 * 信息来源SearchAnalyzer中的List<String>
 * 生成有效的信息
 * @author v11
 * @date 2014年8月29日
 * @version 1.0
 */
public class DoPageInfo {

	
	SearchAnalyzer sa = new SearchAnalyzerImpl();
	
	/**
	 * 对Url分类
	 * 生成数据信息 
	 * "companyType" + "count" + "position" +"avg weight" 
	 * @return
	 */
	public List<String> createUrlInfo(String searchWord){
		List<String> outs = new ArrayList<String>();
		List<String> in = sa.getBaiduUrlInfo(searchWord);
		//List<String> burl = sa.getBaiduUrlInfo(searchWord);
		HashMap<String, Integer> countMap = new HashMap<String, Integer>();
		HashMap<String, String> linkMap = new HashMap<String, String>();
		int index = 0;
		for(String url : in){
			String type = getCompanyType(url);
			MapTools.setCountToMap(countMap, type, 1);
			MapTools.appendString(linkMap, type, ++index+"");
			//if(index < 10) outs.add(index+Contants._split+sa.getRealUrl(burl.get(index-1)));
			if(index < 10) outs.add(url);
		}
		Iterator<Entry<String,Integer>> iter = countMap.entrySet().iterator();
		while(iter.hasNext()){
			Entry<String,Integer> en = iter.next();
			String key = en.getKey();
			String res = key +"	"+en.getValue()+"	"+linkMap.get(key);
			outs.add(res);
		}
		return outs;
	}
	public HashMap<String, Integer> createKeywordInfo(String searchWord){
		List<String> outs = new ArrayList<String>();
		List<String> in = sa.getKeywordInfo(searchWord);
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		for (String content : in) {
			if (content != null) {
				/*
				 * space
				 */
				String words[] = content.split(" ");
				if (words.length == 1) {
					words = content.split(" "); // tag
				}
				for (String word : words) {
					MapTools.setCountToMap(map, word, 1);
				}
			}
		}
		return map;
	}
	/**
	 * 获取来源类型分类
	 * Example：bbs,blog
	 * @param url
	 * @return
	 */
	private String getUrlType(String url){
		if(url == null) return null;
		if(url.contains(Contants.NEWS)) return "新闻";
		else if(url.contains(Contants.BBS)) return "BBS";
		else if(url.contains(Contants.BLOG)) return "博客";
		else if(url.contains(Contants._zhidao)) return "百度知道";
		else if(url.contains(Contants._tieba)) return "百度贴吧";
		return "其他类型网站";
	}
	/**
	 * 获取来源主流媒体分类
	 * Example：sohu,tianya,douban
	 * @param url
	 * @return
	 */
	private String getCompanyType(String url){
		if(url == null) return null;
		if(url.contains(Contants._163)) return "网易";
		else if(url.contains(Contants._baidu)) return "百度";
		else if(url.contains(Contants._douban)) return "豆瓣";
		else if(url.contains(Contants._sina)) return "新浪";
		else if(url.contains(Contants._sohu)) return "搜狐";
		else if(url.contains(Contants._tianya)) return "天涯";
		else if(url.contains(Contants._qq)) return "腾讯";
		if(url.contains(Contants.NEWS)) return "传统新闻媒体";
		else if(url.contains(Contants.BBS)) return "其他BBS";
		else if(url.contains(Contants.BLOG)) return "其他博客";
		return "其他类型网站";
	}
}
