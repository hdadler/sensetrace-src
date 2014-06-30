package com.ipv.sensetrace.delphinftpservice;


import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.StringTokenizer;


import com.ipv.sensetrace.delphinftpservice.internal.FTPDownloader;
import com.ipv.sensetrace.delphinftpservice.internal.MemConverter;
import com.ipv.sensetrace.delphinftpservice.internal.TimeFormat;

public class DelphinFTPService implements IDelphinFTPService {

	java.io.BufferedReader FileReader = null;
	String line = null;
	FTPDownloader ftp = new FTPDownloader();
	MemConverter convert = new MemConverter();
	TimeFormat timeformat = new TimeFormat();
	// String rootdownloadfolder;
	String memfiles = "/tmp";
	String memconvert = "/etc/sensetrace/com.ipv.sensetrace.DataloggerService";

	/*
	 * public DataloggerService(String downloadfolder_ref) { rootdownloadfolder
	 * = downloadfolder_ref; }
	 */
	public void Init(String memfiles_ref, String memconvert_ref) {
		memfiles = memfiles_ref;
		memconvert = memconvert_ref;
	}

	public boolean FetchData(String ftplink, String dlid, String datefrom, String user, String pwd) {
		System.out.println("ftplink: " + ftplink);
		StringTokenizer st = new StringTokenizer(ftplink, "/");
		String dlfolder = "";
		String filename = "";
		while (st.hasMoreTokens()) {
			dlfolder = filename;
			filename = st.nextToken();
		}
		String addressfolder = ftplink.substring(0, ftplink.indexOf(filename));
		String downloadfolder = memfiles + "/" + dlfolder;
		// Filename without ending
		st = new StringTokenizer(filename, ".");
		filename = st.nextToken();
		if (ftp.download(filename + ".mem", addressfolder, downloadfolder, user, pwd)) {
		
		//modified for import from disk
		//if (true) {
			// Converting process
			// Only if file was new downloaded
			System.out.println("File older 10 hours! Converting!");
			convert.convert(memconvert, memfiles + "/" + dlfolder + "/"
					+ filename + ".mem", memfiles + "/" + dlfolder + "/"
					+ filename + ".asc");

		} else {
			System.out.println("File not older 10 hours! Not converting!");
		}

		try {
			FileReader = // ein Reader um die Datei Zeilenweise auszulesen

			new java.io.BufferedReader(new java.io.FileReader(new java.io.File(
					memfiles + "/" + dlfolder + "/" + filename + ".asc")));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			//Jump back because file is not existing (e.g. datalogger connection is down)
			System.err.println("Go to next sensor. Cause datalogger not reachable.");
			return false;
		} 

		int n = 0;
		line = "";

		// System.out.println("dlid: " + dlid);
		// Jump to tagid in file!
		try {
			while (line != null && !line.contains("," + dlid)) {

				line = FileReader.readLine();
			}
			n++;
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
				n++;
			}
			// System.out.println("Line: "+line);
			if (line.isEmpty()) {
				System.err
						.println("No newer Date found!...import next sensor.");
				return false;
			}
		} else {
			System.err.println("No SensorID found!...import next sensor.");
			return false;
		}
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
				return date;
			} else if (column == "value") {
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
