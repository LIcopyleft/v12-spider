package org.v11.baidu;

import java.util.List;

import org.v11.baidu.analyzer.SearchAnalyzerImpl;
import org.v11.baidu.info.DoPageInfo;
import org.v11.utils.ReadFile;
import org.v11.utils.WriteFile;

/**
 * 工作类
 * 生成结果
 * @author v11
 * @date 2014年8月28日
 * @version 1.0
 */
public class Worker {
	public static void main(String[] args) {
		List<String> searchs = ReadFile.readFileToList("in");
		for(String search : searchs){
			String pre = "./target/";
			DoPageInfo pInfo = new DoPageInfo();

			WriteFile.write(pInfo.createUrlInfo(search), pre+search+"_url.txt");
			WriteFile.write(pInfo.createKeywordInfo(search), pre+search+"_content.txt");
			//WriteFile.write(pInfo.createUrlInfo(search), pre+search+"_type.txt");
		}		
	}
}
