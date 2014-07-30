/*
 * Diese Datei kann gelöscht werden!
 */
package com.ipv.sensetrace.delphincsvservice;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import com.ipv.sensetrace.delphincsvservice.internal.TimeFormat;

public class DelphinCSVService implements IDelphinCSVService {

	java.io.BufferedReader FileReader = null;
	String line = null;

	TimeFormat timeformat = new TimeFormat();
	String file_str_lowest_date = "";

	// String actualfolder = "";

	// String rootdownloadfolder;

	/*
	 * public DataloggerService(String downloadfolder_ref) { rootdownloadfolder
	 * = downloadfolder_ref; }
	 */
	public void Init(String memfiles_ref, String memconvert_ref) {

	}

	public boolean FetchData(String ftplink, String dlid, String datefrom,
			String folder_str, String stringinfile) {
		// actualfolder = folder_str;
		System.out.println("Jump to dlid in file: " + dlid);
		System.out.println("Jump to date in file: " + datefrom);

		// find out the correct filename. therefore find the oldest file in
		// given folder that contains stringinfile
		// At first the program searches the folder content
		File Folder = new File(folder_str);
		if (!Folder.exists()) {
			System.out.println("Could not find folder! Exist System!");
			System.exit(0);

		}
		// Print out all folder content...
		/*
		 * String[] FilesInFolder = Folder.list(); for (int i = 0; i <
		 * FilesInFolder.length; i++) { System.out.println(FilesInFolder[i]); }
		 */
		String[] FilesInFolder = Folder.list();
		// Add files to list that contain string in file

		ArrayList<String> SearchedFiles = new ArrayList<String>();
		for (int i = 0; i < FilesInFolder.length; i++) {
			if (FilesInFolder[i].contains(stringinfile)) {
				SearchedFiles.add(FilesInFolder[i]);
			}

		}

		// Search for the oldest file in list
		String date_str = "";
		long date_ts = 0;
		file_str_lowest_date = "";
		long date_ts_lowest = 0;

		for (int i = 0; i < SearchedFiles.size(); i++) {
			date_str = SearchedFiles.get(i).split("_" + stringinfile + "_")[1]
					.split(".asc")[0];
			// System.out.println("date_str: " + date_str);
			date_ts = timeformat.ConvertTimeInFilenameToTimestamp(date_str);
			//System.out.println("date_ts: " + date_ts);
			if (i == 0) {
				file_str_lowest_date = SearchedFiles.get(0);
				date_ts_lowest = date_ts;
			} else if (date_ts < date_ts_lowest) {
				file_str_lowest_date = SearchedFiles.get(i);
				date_ts_lowest = date_ts;
			}
			// System.out.println("date_ts_lowest: " + date_ts_lowest);

		}
		System.out.println("date_str_lowest: " + file_str_lowest_date);

		try {
			FileReader = // ein Reader um die Datei Zeilenweise auszulesen

			new java.io.BufferedReader(new java.io.FileReader(new java.io.File(
					folder_str + "/" + file_str_lowest_date)));

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			// Jump back because file is not existing (e.g. datalogger
			// connection is down)
			System.err
					.println("Go to next sensor. Cause file not found.");
			return false;
		}
		/*try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
*/
		// int n = 0;
		line = "";

		// System.out.println("dlid: " + dlid);
		// Jump to tagid in file!
		try {
			while (line != null && !line.contains(";" + dlid + ";")) {

				line = FileReader.readLine();
			}
			//line = FileReader.readLine();
			// n++;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Just go on, if line not null

		 System.out.println("Line: "+line);
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
					&& timeformat.ConvertDLTimeToTimestamp(line,";") != 0
					&& timeformat.ConvertDLTimeToTimestamp(line,";") <= sqltime_l) {
				try {
					line = FileReader.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// n++;
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

	public String RemoveOldestFilesToBatch(String folder_str) {
		// actualfolder = folder_str;

		// find out the correct filename. therefore find the oldest file in
		// given folder that contains stringinfile
		// At first the program searches the folder content
		File Folder = new File(folder_str);
		if (!Folder.exists()) {
			System.out.println("Could not find folder! Exist System!");
			System.exit(0);

		}

		String[] FilesInFolder = Folder.list();
		ArrayList<String> SearchedFiles = new ArrayList<String>();
		for (int i = 0; i < FilesInFolder.length; i++) {
			if (FilesInFolder[i].contains("_"))
				SearchedFiles.add(FilesInFolder[i]);
		}

		// Search for the oldest file in list
		String date_str = "";
		long date_ts = 0;
		file_str_lowest_date = "";
		long date_ts_lowest = 0;

		for (int i = 0; i < SearchedFiles.size(); i++) {
			//System.out.println("SearchedFiles.get(i): " + SearchedFiles.get(i));
			date_str = SearchedFiles.get(i).split("_")[4] + "_"
					+ SearchedFiles.get(i).split("_")[5];
			// System.out.println("date_str: " + date_str);
			date_ts = timeformat.ConvertTimeInFilenameToTimestamp(date_str);
			//System.out.println("date_ts: " + date_ts);
			if (i == 0) {
				file_str_lowest_date = date_str;
				date_ts_lowest = date_ts;
			} else if (date_ts < date_ts_lowest) {
				file_str_lowest_date = date_str;
				date_ts_lowest = date_ts;
			}
			// System.out.println("date_ts_lowest: " + date_ts_lowest);

		}
		//System.out.println("file_str_lowest_date: " + file_str_lowest_date);
		//System.out.println("date_str: " + date_str);

		for (int i = 0; i < SearchedFiles.size(); i++) {
			if (SearchedFiles.get(i).contains(file_str_lowest_date)) {
				File quellDatei = new File(folder_str + "/"
						+ SearchedFiles.get(i));
				File zielDatei = new File(folder_str + "/batched/"
						+ SearchedFiles.get(i));
				quellDatei.renameTo(zielDatei);
			}
		}
		return timeformat.ConvertMillisecondsToSQLTime(date_ts_lowest);
		/*try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}

	public boolean IsAllBatched(String folder_str) {
		// actualfolder = folder_str;

		// At first the program searches the folder content
		File Folder = new File(folder_str);
		if (!Folder.exists()) {
			System.out.println("Could not find folder! Exit System!");
			System.exit(0);

		}

		String[] FilesInFolder = Folder.list();
		ArrayList<String> SearchedFiles = new ArrayList<String>();
		for (int i = 0; i < FilesInFolder.length; i++) {
			if (FilesInFolder[i].contains("_"))
				SearchedFiles.add(FilesInFolder[i]);
		}

		// Return true if folder contains less then one file
		if (SearchedFiles.size() < 1) {
			//System.out.println("return true")
			return true;
		} else {

			return false;
		}
	}

	/*
	 * public void DeleteOldestFiles() { File quellDatei = new File(actualfolder
	 * + "/" + file_str_lowest_date); File zielDatei = new File(actualfolder +
	 * "/batched/" + file_str_lowest_date); quellDatei.renameTo(zielDatei); }
	 */

	public String GetElement(String column) {
		// System.out.println("line: " + line);
		if (!line.isEmpty()) {
			// System.out.println("line: " + line);
			StringTokenizer st = new StringTokenizer(line, ";");
			String date = st.nextToken() + ";" + st.nextToken();
			String value = st.nextToken();
			if (column == "timestamp") {
				return date;
			} else if (column == "value") {
				return value.replace(",", ".");
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
