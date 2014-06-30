package com.ipv.sensetrace.pgsqlservice;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeFormat {
	
	
	
	public String ConvertMillisecondsToSQLTime(long milli) {
		// long timestamp_int = 0;
		java.util.Date dtDate = new Date();
		DateFormat sql = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dtDate.setTime(milli);

		return sql.format(dtDate);

	}
	
	
	public int timediff(String value1, String value2) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
		try {
			dtDate = sql.parse(date);
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return etalis.format(dtDate);

	}

	public  long ConvertSQLTimeToTimestamp(String date) {
		//System.out.println("Convertdate: " + date);
		// long timestamp_int = 0;
		java.util.Date dtDate = new Date();
		// DateFormat etalis = new SimpleDateFormat("yyyy,MM,dd,HH,mm,ss");
		DateFormat sql = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			dtDate = sql.parse(date);
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return dtDate.getTime();

	}


	public long ConvertDLTimeToTimestamp(String date) {
		//System.out.println("Convertdate: " + date);
		// long timestamp_int = 0;
		java.util.Date dtDate = new Date();
		// DateFormat etalis = new SimpleDateFormat("yyyy,MM,dd,HH,mm,ss");
		DateFormat sql = new SimpleDateFormat("dd.MM.yyyy,HH:mm:ss");
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
		try {
			dtDate = sql.parse(date);
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return dl.format(dtDate);
	}

}
