package org.v11.weibo.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class ChangeCharset {
	private static final String US_ASCII = "US-ASCII";
	private static final String ISO_8859_1 = "ISO-8859-1";
	public static String toISO_8859_1(String str){
		return changeCharset(str, ISO_8859_1);
	}
	public static String toASCII(String str) {
		return changeCharset(str, US_ASCII);
	}

	private static String changeCharset(String str, String newCharset) {
		if (str != null) {
			byte[] bs = str.getBytes();
			try {
				return new String(bs, newCharset);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
}
