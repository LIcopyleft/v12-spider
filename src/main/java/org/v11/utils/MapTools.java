package org.v11.utils;

import java.text.DecimalFormat;
import java.util.HashMap;

public class MapTools {

	/**
	 * 将信息累加保存至Map
	 * word 为null时，忽略本次插入
	 * @param map 
	 * @param word 关键字
	 * @param count 需累加值
	 */
	public static void setCountToMap(HashMap<String, Integer> map,String word,Integer count){
		//System.out.println(word+" "+type);
		if(word == null) return ;
		int cc = 0;
		if(map.containsKey(word)){
			cc = map.get(word);
		}
		map.put(word, cc+count);
	}
	/**
	 * 追加
	 * value:1,2,23,54
	 * @param map
	 * @param word
	 * @param value
	 */
	public static void appendString(HashMap<String, String> map,String word,String value){
		if(word == null) return ;
		if(map.containsKey(word)){
			String old = map.get(word);
			map.put(word, old+","+value);
		}
		else {
			map.put(word, value);
		}
	}
	/**
	 * 获取结果均值
	 * Map：{value:"1,23,58,44"}
	 * @param map
	 * @param word
	 * @return
	 */
	public static String getAvgFromStringMap(HashMap<String, String> map,String word){
		
		Integer res = 0;
		Integer count = 0;
		if(map.containsKey(word)){
			String v = map.get(word);
			String ls[] = v.split(",");
			for(String snum:ls){
				Integer num = Integer.parseInt(snum);
				res += num;
				count ++;
			}
			DecimalFormat df = new DecimalFormat("0.00");
			return df.format(res*1.0 / count);
		}
		return "0";
	}
}