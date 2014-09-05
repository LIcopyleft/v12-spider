package org.v11.weibo.mobile.https;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.v11.weibo.utils.SpiderLog;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class TopicWorker extends BasicWorker{
	private final Logger log = Logger.getLogger(this.getClass().getName());
	private HttpClient client;
	private WeiboLoginer loginer;
	public TopicWorker(){
		loginer = new WeiboLoginer();
		client = loginer.getCliet();
	}
	
	/**
	 * 获取话题wids列表
	 * <ol>
	 * <li>pageNum = 100</li>
	 * <li>end time = current time</li>
	 * </ol>
	 * @param word 关键字
	 * @return
	 */
	public Set<String> getIds(String word){
		return getIds(word,100,"","");
	}
	/**
	 * 获取话题wids列表
	 * <ol>
	 * <li>单独设置开始时间可以</li>
	 * <li>单独设置结束时间就无法识别</li>
	 * </ol>
	 * @param word 关键字
	 * @param pageNum 爬取页数
	 * <br /> 最大为100
	 * @param starttime 开始时间
	 * <br />Example ： 20140806
	 * @param endtime 结束时间
	 * <br /> Example 20140901
	 * @return
	 */
	public Set<String> getIds(String word,Integer pageNum,String starttime,String endtime){
		Set<String> ls = new HashSet<String>();
		String wordUrlEncode = URLEncoder.encode(word);
		String searchUrl = "http://weibo.cn/search/mblog?hideSearchFrame=&keyword="
				+ wordUrlEncode
				+ "&advancedfilter=1&hasori=1&starttime="+starttime
				+"endtime="+endtime
				+"&sort=time&page=";
		int count = 0;
		Document doc = null;
		if(pageNum > 100) pageNum = 100;
		for (int i = 1; i <= pageNum; i++) {
			try {
				searchUrl = searchUrl + i;
				log.info(searchUrl);
				HttpGet getSearch = addHttpGetWithHeader(searchUrl);
				HttpResponse res;
				res = client.execute(getSearch);
				doc = Jsoup.parse(showResponseBody(res));
				release(res);
				Elements es = doc.getElementsByTag("div");
				for(Element e : es){
					String idName = e.attr("id");
					if(idName.startsWith("M_")){
						count++;
						log.info(idName.substring(idName.indexOf("_") + 1));
					}
				}
			} catch (ClientProtocolException e) {
				log.error(e.getMessage());
				log.error(doc.text());
			} catch (IOException e) {
				log.error(e.getMessage());
			}
			log.info(word + "关键词获得的搜索结果数为：" + count);
		}
		return ls;
	}
	public static void main(String[] args) {
		TopicWorker t = new TopicWorker();
		t.getIds("林治波");
	}
}
