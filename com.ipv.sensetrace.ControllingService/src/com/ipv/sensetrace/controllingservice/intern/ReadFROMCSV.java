/*
 * Diese Datei kann gelöscht werden!
 */
package com.ipv.sensetrace.controllingservice.intern;


import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.StringTokenizer;

public class ReadFROMCSV {

	java.io.BufferedReader FileReader = null;
	String line = null;

	TimeFormat timeformat = new TimeFormat();

	// String rootdownloadfolder;

	/*
	 * public DataloggerService(String downloadfolder_ref) { rootdownloadfolder
	 * = downloadfolder_ref; }
	 */
	public void Init(String memfiles_ref, String memconvert_ref) {

	}

	public boolean FetchData(String ftplink, String dlid, String datefrom) {
		System.out.println("Jump to dlid in file: " + dlid);
		System.out.println("Jump to date in file: " + datefrom);
		try {
			FileReader = // ein Reader um die Datei Zeilenweise auszulesen

			new java.io.BufferedReader(new java.io.FileReader(new java.io.File(
					"/home/hendrik/Release1/export/2013-11-ucy.csv")));	
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			// Jump back because file is not existing (e.g. datalogger
			// connection is down)
			System.err
					.println("Go to next sensor. Cause datalogger not reachable.");
			return false;
		}
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//int n = 0;
		line = "";

		// System.out.println("dlid: " + dlid);
		// Jump to tagid in file!
		try {
			while (line != null && !line.contains(dlid+",")) {

				line = FileReader.readLine();
			}
			line = FileReader.readLine();
			//n++;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Just go on, if line not null

		// System.out.println("Line: "+line);
		// System.out.println("datefrom: "+datefrom);
		if (line != null) {
			// One Line further!
			try {
				line = FileReader.readLine();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			// Jump to right date in file!
			long sqltime_l = 0;
			if (datefrom != null) {
				sqltime_l = timeformat.ConvertSQLTimeToTimestamp(datefrom);
			} else
				sqltime_l = 0;
			while (!line.isEmpty()
					&& timeformat.ConvertDLTimeToTimestamp(line,",") != 0
					&& timeformat.ConvertDLTimeToTimestamp(line,",") <= sqltime_l) {
				try {
					line = FileReader.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//n++;
			}
			// System.out.println("Line: "+line);
			if (line.isEmpty()) {
				System.err
						.println("No newer Date found!...import next sensor.");
				System.out.println("return false");
				return false;
			}
		} else {
			System.err.println("No SensorID found!...import next sensor.");
			System.out.println("return false");
			return false;
		}
		System.out.println("return true");
		return true;
	}

	public String GetElement(String column) {
		// System.out.println("line: " + line);
		if (!line.isEmpty()) {
			// System.out.println("line: " + line);
			StringTokenizer st = new StringTokenizer(line, ",");
			String date = st.nextToken() + "," + st.nextToken();
			String value = st.nextToken();
			if (column == "timestamp") {
				// System.out.println("date: " + date);
				return date;
			} else if (column == "value") {
				// System.out.println("value: " + value);
				return value;
			}

		}
		return null;

	}

	public boolean GotoNextElement() {
		try {
			if ((line = FileReader.readLine()) != null) {
				return true;
			} else {
				return false;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
			return false;
		}
	}

}
