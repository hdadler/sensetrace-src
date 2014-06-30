package com.ipv.sensetrace.delphincsvservice.internal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimeFormat {

	public String ConvertMillisecondsToSQLTime(long milli) {
		// long timestamp_int = 0;
		java.util.Date dtDate = new Date();
		DateFormat sql = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sql.setTimeZone(TimeZone.getTimeZone("UTC"));
		dtDate.setTime(milli);

		return sql.format(dtDate);

	}

	public int timediff(String value1, String value2) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		df.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date date1 = null, date2 = null;
		try {
			date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
					.parse(value1);
			date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
					.parse(value2);
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println("Return diff: "
		// + (int) (date1.getTime() - date2.getTime()) / 1000); // Sat Jan
		// 02
		// 00:00:00
		// BOT
		// 2010
		return (int) (date1.getTime() - date2.getTime()) / 1000;

	}

	String ConvertEtalisTimeToSQLTime(String date) {
		// long timestamp_int = 0;
		java.util.Date dtDate = new Date();
		DateFormat etalis = new SimpleDateFormat("yyyy,MM,dd,HH,mm,ss");
		DateFormat sql = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sql.setTimeZone(TimeZone.getTimeZone("UTC"));
		etalis.setTimeZone(TimeZone.getTimeZone("UTC"));
		try {
			dtDate = etalis.parse(date);
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return sql.format(dtDate);

	}

	public String ConvertSQLTimeToEtalisTime(String date) {
		// long timestamp_int = 0;
		java.util.Date dtDate = new Date();
		DateFormat etalis = new SimpleDateFormat("yyyy,MM,dd,HH,mm,ss");
		DateFormat sql = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sql.setTimeZone(TimeZone.getTimeZone("UTC"));
		etalis.setTimeZone(TimeZone.getTimeZone("UTC"));
		try {
			dtDate = sql.parse(date);
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return etalis.format(dtDate);

	}

	public long ConvertSQLTimeToTimestamp(String date) {
		// System.out.println("Convertdate: " + date);
		// long timestamp_int = 0;
		java.util.Date dtDate = new Date();
		// DateFormat etalis = new SimpleDateFormat("yyyy,MM,dd,HH,mm,ss");
		DateFormat sql = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sql.setTimeZone(TimeZone.getTimeZone("UTC"));
		try {
			dtDate = sql.parse(date);
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return dtDate.getTime();

	}

	public long ConvertMySQLTimeToTimestamp(String date) {
		//if (date != null) {

			// System.out.println("Convertdate: " + date);
			// String tokens=tokens.
			// long timestamp_int = 0;
			// System.out.println("Convertdate: " + date);
			// date=date.substring(0, date.length()-2);
			// System.out.println("Convertdate: " + date);
			java.util.Date dtDate = new Date();
			// DateFormat etalis = new SimpleDateFormat("yyyy,MM,dd,HH,mm,ss");
			DateFormat sql = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
			sql.setTimeZone(TimeZone.getTimeZone("UTC"));
				try {
				dtDate = sql.parse(date);
			} catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
				System.err.println("Convertdate: " + date);
				e.printStackTrace();
				// System.err.println("Convertdate: " + date);
			}

			return dtDate.getTime();
		//}
		//return -1;

	}
	
	public long ConvertTimeInFilenameToTimestamp(String date) {
		//if (date != null) {

			// System.out.println("Convertdate: " + date);
			// String tokens=tokens.
			// long timestamp_int = 0;
			// System.out.println("Convertdate: " + date);
			// date=date.substring(0, date.length()-2);
			// System.out.println("Convertdate: " + date);
			java.util.Date dtDate = new Date();
			// DateFormat etalis = new SimpleDateFormat("yyyy,MM,dd,HH,mm,ss");
			DateFormat sql = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
			sql.setTimeZone(TimeZone.getTimeZone("UTC"));
				try {
				dtDate = sql.parse(date);
			} catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
				System.err.println("Convertdate: " + date);
				e.printStackTrace();
				// System.err.println("Convertdate: " + date);
			}

			return dtDate.getTime();
		//}
		//return -1;

	}

	public long ConvertDLTimeToTimestamp(String date,String sign) {
		// System.out.println("Convertdate: " + date);
		// long timestamp_int = 0;
		java.util.Date dtDate = new Date();
		// DateFormat etalis = new SimpleDateFormat("yyyy,MM,dd,HH,mm,ss");
		DateFormat sql = new SimpleDateFormat("dd.MM.yyyy"+sign+"HH:mm:ss");
		sql.setTimeZone(TimeZone.getTimeZone("UTC"));
		try {
			dtDate = sql.parse(date);
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dtDate.getTime();
	}

	public String ConvertSQLTimeToDLTime(String date) {
		// long timestamp_int = 0;
		java.util.Date dtDate = new Date();
		DateFormat dl = new SimpleDateFormat("dd.MM.yyyy,HH:mm:ss");
		DateFormat sql = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dl.setTimeZone(TimeZone.getTimeZone("UTC"));
		sql.setTimeZone(TimeZone.getTimeZone("UTC"));
		try {
			dtDate = sql.parse(date);
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return dl.format(dtDate);
	}

}
