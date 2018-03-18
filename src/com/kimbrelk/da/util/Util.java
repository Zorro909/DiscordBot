package com.kimbrelk.da.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class Util {
	
	public final static Date stringToDate(String str) {
		try {
			final DateFormat format = new SimpleDateFormat("yyyy-MM-dd\'T\'HH:mm:ss-SSSS");
			return format.parse(str);
		}
		catch (ParseException e) {
			return null;
		}
	}

	public final static String base10To36(long base10) {
		return Long.toString(base10, 36);
	}

	public final static String getStashItemUrl(long itemId) {
		return "http://sta.sh/1" + itemId;
	}
	public final static String getStashItemShortUrl(long itemId) {
		return "http://sta.sh/0" + base10To36(itemId);
	}
	public final static String getStashStackUrl(long stackId) {
		return "http://sta.sh/2" + base10To36(stackId);
	}
}