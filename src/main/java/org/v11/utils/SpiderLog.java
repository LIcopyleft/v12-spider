package org.v11.utils;

import org.apache.log4j.*;


public class SpiderLog {
	private static Logger log = Logger.getLogger(SpiderLog.class.getName());
	public static void info(Object message){
		log.info(message);
	}
	public static void error(Object message){
		log.error(message);
	}
	public static void debug(Object message){
		log.debug(message);
	}
	
}
