package org.v11.utils;

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
}