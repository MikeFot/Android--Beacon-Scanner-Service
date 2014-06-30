package com.michaelfotiadis.ibeaconscanner.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeFormatter {
	private final static String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS zzz";
	private final static SimpleDateFormat ISO_FORMATTER = new UtcDateFormatter(ISO_FORMAT, Locale.US);

	public static String getIsoDateTime(Date date){
		return ISO_FORMATTER.format(date);
	}

	public static String getIsoDateTime(long millis){
		return getIsoDateTime(new Date(millis));
	}
}
