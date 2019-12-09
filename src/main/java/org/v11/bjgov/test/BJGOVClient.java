package org.v11.bjgov.test;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.v11.baidu.http.Client;
import org.v11.bjgov.test.entity.Persion;
import org.v11.mapper.FlowMapper;
import org.v11.utils.WriteFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * BaiduClient
 *
 * @author v11
 * @version 1.0
 * @date 2014年8月28日
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/applicationContext.xml"})
public class BJGOVClient {
    private static final Logger log = Logger.getLogger(BJGOVClient.class.getName());

    private static final String URL = "http://rsj.beijing.gov.cn/integralpublic/settlePerson/tablePage";

    private String RefererString = null;
    private static HttpClient client = new DefaultHttpClient();
    private final String LOGIN_URL = "http://www.baidu.com/";

    public BJGOVClient() {
        client = new DefaultHttpClient();
    }

    private ApplicationContext applicationContext;

    /*@Autowired
    SqlSessionFactory factory;
    @Before
    public void setUp() throws Exception {

        applicationContext = new ClassPathXmlApplicationContext(new String[] {
                "applicationContext.xml",
        });
    }
*/
    @Autowired
    private FlowMapper mapper;

    public HttpClient getClient() {
        return client;
    }

    /**
     * 释放掉res的资源，在每次连接后
     *
     * @param res
     */
    private void release(HttpResponse res) {
        try {
            EntityUtils.consume(res.getEntity());
        } catch (IOException e) {
            log.info(e.toString());
        }
    }

    /**
     * 为HttpGet添加报文头信息
     *
     * @param url
     * @return
     */
    private HttpGet addHttpGetWithHeader(String url) {
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:16.0) Gecko/20100101 Firefox/16.0");
		/*if(RefererString == null){
			httpGet.setHeader("Referer", LOGIN_URL);
		}
		else {
			httpGet.setHeader("Referer", RefererString);
		}*/
        httpGet.setHeader("Content-Type", "text/html;charset=GBK");
        RefererString = url;
        return httpGet;
    }

    /**
     * 添加post请求报文头信息
     *
     * @param url
     * @return
     */
    private HttpPost addHttpPostWithHeader(String url, Integer page, Integer pageSize) {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:16.0) Gecko/20100101 Firefox/16.0");
        if (RefererString == null) {
            httpPost.setHeader("Referer", URL);
        } else {
            httpPost.setHeader("Referer", URL);
        }
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
        RefererString = url;

        if (pageSize == null) {
            pageSize = 100;
        }

        if (page == null) {
            page = 1;
        }
        int pageInfo = pageSize * (page - 1);
        StringEntity postingString = new StringEntity("name=&rows=" + pageSize + "&page=" + pageInfo,
                "utf-8");
        httpPost.setEntity(postingString);
        return httpPost;
    }

    /**
     * 打印出报文头+html信息
     *
     * @param res
     */
    private void showResponse(HttpResponse res) {
        showHeaders(res);
        showResponseBody(res);
    }

    /**
     * 打印出response的页面信息
     *
     * @param res
     */
    private String showResponseBody(HttpResponse res) {
        HttpEntity entity = res.getEntity();
        if (entity != null) {
            String content;
            try {
                content = EntityUtils.toString(entity, "UTF-8");
                log.info(content);
                return content;
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 打印response出报文头信息
     *
     * @param response
     */
    private void showHeaders(HttpResponse response) {
        log.debug("---------------------------------");
        Header[] headers = response.getAllHeaders();
        for (Header header : headers) {
            log.debug("key; " + header.getName()
                    + " value:" + header.getValue());
        }
    }

    /**
     * 转化为搜索信息
     *
     * @param
     * @return
     */
    private String getSearchString(String search) {
        String[] strs = search.split(" ");
        String resString = "%20";
        for (String str : strs) {
            resString += URLEncoder.encode(str) + "%20";
        }
        return resString;
    }

    /**
     * 获取页面搜索结果
     *
     * @param search 搜索关键词
     * @return 结果
     */
    public String getPage(String search) {
        // TODO Auto-generated method stub
        String url = "http://www.baidu.com/s?wd=" + getSearchString(search) + "&rsv_spt=1"
                + "&issp=1&rsv_bp=0&ie=utf-8&tn=baiduhome_pg&inputT=1305&rn=100";
        log.info(url);
        try {
            HttpGet get = addHttpGetWithHeader(url);
            HttpResponse res = client.execute(get);
            String resString = showResponseBody(res);
            release(res);
            return resString;
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }


    public static void main(String[] args) throws IOException {


    }

    @Test
    public void getAllUserInfo() throws IOException {

		int page = 1;
		while (true){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(page > 61){
				break;
			}

		}
    	//testPost(page,100);
	}

    @Test
    public void testGet() throws IOException {
        BJGOVClient bjgovClient = new BJGOVClient();
        HttpGet get = bjgovClient.addHttpGetWithHeader(URL);
        HttpResponse res = client.execute(get);
        String html = bjgovClient.showResponseBody(res).trim();
        Document parse = Jsoup.parse(html);
        Element body = parse.body();
        Elements children = body.children();
        Element element = children.get(0);
        Element tbody = element.getElementsByTag("tbody").get(0);
        Elements trs = tbody.getElementsByTag("tr");

        final List<Persion> list = new LinkedList<Persion>();
        for (Element tr : trs) {
            String pid = tr.child(0).text();
            String name = tr.child(1).text();
            String birthday = tr.child(2).text();
            String company = tr.child(3).text();
            String score = tr.child(4).text();

            Persion persion = new Persion();
            persion.setBirthday(birthday);
            persion.setCompany(company);
            persion.setId(pid);
            persion.setName(name);
            persion.setScore(score);
            list.add(persion);
        }
        mapper.insertList(list);
        bjgovClient.release(res);
    }

    @Test
    public void testPost() throws IOException {
        AtomicInteger page = new AtomicInteger(1);;
        int pageSize = 100;
        BJGOVClient bjgovClient = new BJGOVClient();
        while (true){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            int andIncrement = page.getAndIncrement();
            if(andIncrement > 61){
                break;
            }

            HttpPost httpPost = bjgovClient.addHttpPostWithHeader(URL, andIncrement, pageSize);
            HttpResponse res = client.execute(httpPost);
            String html = bjgovClient.showResponseBody(res).trim();

            Document parse = Jsoup.parse(html);
            Element body = parse.body();
            Elements children = body.children();
            Element element = children.get(0);
            Element tbody = element.getElementsByTag("tbody").get(0);
            Elements trs = tbody.getElementsByTag("tr");

            final List<Persion> list = new LinkedList<Persion>();
            for (Element tr : trs) {
                String pid = tr.child(0).text();
                String name = tr.child(1).text();
                String birthday = tr.child(2).text();
                String company = tr.child(3).text();
                String score = tr.child(4).text();

                Persion persion = new Persion();
                persion.setBirthday(birthday);
                persion.setCompany(company);
                persion.setId(pid);
                persion.setName(name);
                persion.setScore(score);
                list.add(persion);
            }
            bjgovClient.release(res);

            Thread t = new Thread(new Runnable() {
                public void run() {
                    mapper.insertList(list);
                    log.info(Thread.currentThread().getName()+"{"+list.get(0).getId()+"---"+list.get(list.size()-1).getId()+"}");
                }
            });
            t.start();

        }


    }


}
