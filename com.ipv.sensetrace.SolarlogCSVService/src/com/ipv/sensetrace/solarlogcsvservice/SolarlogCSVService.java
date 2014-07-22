/*
 * Diese Datei kann gelöscht werden!
 */
package com.ipv.sensetrace.solarlogcsvservice;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.ipv.sensetrace.solarlogcsvservice.internal.TimeFormat;

public class SolarlogCSVService implements ISolarlogCSVService {

	java.io.BufferedReader FileReader = null;

	// For reversing the file
	List<String> buffer_list = new ArrayList<String>();
	int list_index = 0;

	int csvarray[] = null;
	String line = null;

	TimeFormat timeformat = new TimeFormat();
	String file_str_lowest_date = "";

	private void ReverseFileToBuffer() {

		buffer_list.clear();
		try {
			while ((line = FileReader.readLine()) != null) {
				buffer_list.add(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		list_index = buffer_list.size() - 1;

	}

	@Override
	public boolean FetchData(String datefrom, String folder_str,
			String stringinfile, int[] csvarray_ref) {
		//System.out.println("Datefrom: " + datefrom);

		csvarray = csvarray_ref;
		// actualfolder = folder_str;
		// System.out.println("Jump to dlid in file: " + dlid);
		//System.out.println("Jump to date in file: " + datefrom);

		// find out the correct filename. therefore find the oldest file in
		// given folder that contains stringinfile
		// At first the program searches the folder content
		File Folder = new File(folder_str);
		if (!Folder.exists()) {
			System.out.println("Could not find folder! Exist System!");
			System.exit(0);

		}
		// Check for batched folder
		File batched = new File(folder_str + "/batched");
		if (!batched.exists()) {
			// Create folder
			System.out.println("Create folder batched in: " + folder_str);
			boolean success = (new File(folder_str + "/batched")).mkdirs();
			if (!success) {
				System.out.println("Create folder batched in: " + folder_str
						+ "failed");
				System.out
						.println("Try to create folder \"batched\" manually.");
				System.exit(0);
			}

		}
		// Print out all folder content...
		/*
		 * String[] FilesInFolder = Folder.list(); for (int i = 0; i <
		 * FilesInFolder.length; i++) { System.out.println(FilesInFolder[i]); }
		 */
		String[] FilesInFolder = Folder.list();
		// Add files to list that contain string in file

		// System.out.println("stringinfile: "+stringinfile);
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
			// System.out.println("SearchedFiles.get(i): " +
			// SearchedFiles.get(i));
			date_str = SearchedFiles.get(i).split("min")[1].split(".js")[0];
			// System.out.println("date_str: " + date_str);
			date_ts = Long.parseLong(date_str);
			// System.out.println("date_ts: " + date_ts);
			if (i == 0) {
				file_str_lowest_date = SearchedFiles.get(0);
				date_ts_lowest = date_ts;
			} else if (date_ts < date_ts_lowest) {
				file_str_lowest_date = SearchedFiles.get(i);
				date_ts_lowest = date_ts;
			}
			// System.out.println("date_ts_lowest: " + date_ts_lowest);

		}
		System.out.println("file to import now: " + folder_str + "/"
				+ file_str_lowest_date);

		try {
			FileReader = // ein Reader um die Datei Zeilenweise auszulesen

			new java.io.BufferedReader(new java.io.FileReader(new java.io.File(
					folder_str + "/" + file_str_lowest_date)));

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			// Jump back because file is not existing (e.g. datalogger
			// connection is down)
			System.err.println("Go to next sensor. Cause file not found.");
			return false;
		}

		// reverse File!
		ReverseFileToBuffer();
		/*
		 * try { Thread.sleep(2000); } catch (InterruptedException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */
		/*
		 * // int n = 0; line = "";
		 * 
		 * // System.out.println("dlid: " + dlid); // Jump to tagid in file! try
		 * { while (line != null && !line.contains(";" + dlid + ";")) {
		 */

		//line = buffer_list.get(list_index--);

		// }
		// line = FileReader.readLine();
		// n++;
		/*
		 * } catch (IOException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */
		// Just go on, if line not null

		// System.out.println("list_index: "+list_index);
		if (list_index >= 0) {

			//If file contains line, read in
			line = buffer_list.get(list_index--);
			// Jump to right date in file!
			long sqltime_l = 0;
			if (datefrom != null) {
				sqltime_l = timeformat.ConvertSQLTimeToTimestamp(datefrom);
			} else
				sqltime_l = 0;

			/*
			 * try { Thread.sleep(10000); } catch (InterruptedException e1) { //
			 * TODO Auto-generated catch block e1.printStackTrace(); }
			 * 
			 * /* date_str=line.split("\"")[1].split("\\|")[0];
			 * date_str=date_str.split("\\|")[0];
			 * System.out.println("date_str:"+date_str);
			 */
			//System.out.println("list_index:" + list_index);
			while (list_index >= 0

					&& timeformat.ConvertDLTimeToTimestamp(
							line.split("\"")[1].split("\\|")[0], " ") != 0
					&& timeformat.ConvertDLTimeToTimestamp(
							line.split("\"")[1].split("\\|")[0], " ") <= sqltime_l) {
				/*System.out.println("time:"
						+ timeformat.ConvertDLTimeToTimestamp(
								line.split("\"")[1].split("\\|")[0], " "));
*/
				// line = FileReader.readLine();
				line = buffer_list.get(list_index--);

				// n++;
			}
			// System.out.println("Line: " + line);
			if (list_index < 0) {
				System.err
						.println("No newer Date found!...import next sensor.");
			//	System.out.println("return false");
				return false;
			}
		} else {
			System.err.println("No SensorID found!...import next sensor.");
		//	System.out.println("return false");
			return false;
		}
		//System.out.println("return true");
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
			if (FilesInFolder[i].contains("min"))
				SearchedFiles.add(FilesInFolder[i]);
		}

		// Search for the oldest file in list
		String date_str = "";
		long date_ts = 0;
		file_str_lowest_date = "";
		long date_ts_lowest = 0;

		// Search for the oldest file in list
		date_str = "";
		date_ts = 0;
		file_str_lowest_date = "";

		for (int i = 0; i < SearchedFiles.size(); i++) {
			// System.out.println("SearchedFiles.get(i): " +
			// SearchedFiles.get(i));
			date_str = SearchedFiles.get(i).split("min")[1].split(".js")[0];
			// System.out.println("date_str: " + date_str);
			date_ts = Long.parseLong(date_str);
			// System.out.println("date_ts: " + date_ts);
			if (i == 0) {
				file_str_lowest_date = SearchedFiles.get(0);
				date_ts_lowest = date_ts;
			} else if (date_ts < date_ts_lowest) {
				file_str_lowest_date = SearchedFiles.get(i);
				date_ts_lowest = date_ts;
			}

		}
		// System.out.println("file_str_lowest_date: " + file_str_lowest_date);
		// System.out.println("date_str: " + date_str);

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
		/*
		 * try { Thread.sleep(20000); } catch (InterruptedException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */
	}

	public boolean IsAllBatched(String folder_str) {
		// actualfolder = folder_str;

		// At first the program searches the folder content
		File Folder = new File(folder_str);
		if (!Folder.exists()) {
			System.out.println("Could not find folder'" + folder_str
					+ "'! Exit System!");
			System.exit(0);

		}

		String[] FilesInFolder = Folder.list();
		ArrayList<String> SearchedFiles = new ArrayList<String>();
		for (int i = 0; i < FilesInFolder.length; i++) {
			if (FilesInFolder[i].contains("min"))
				SearchedFiles.add(FilesInFolder[i]);
		}

		// Return true if folder contains less then one file
		if (SearchedFiles.size() < 1) {
			// System.out.println("return true")
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

	@Override
	public String GetElement(String column) {
		//System.out.println("line: " + line);

		if (list_index >= 0) {
			// System.out.println("csvarray[0] " + csvarray[0]);
			// System.out.println("csvarray[1] " + csvarray[1]);

			StringTokenizer st = new StringTokenizer(line.split("\"")[1], "\\|");

			/*
			 * try { Thread.sleep(10000); } catch (InterruptedException e) { //
			 * TODO Auto-generated catch block e.printStackTrace(); }
			 */
			// st.
			// StringTokenizer st = new StringTokenizer(line, "\\|");
			String date = st.nextToken();
			// String value = st.nextToken();
			if (column == "timestamp") {
				// System.out.println("date: " + date);
				return date;
			} else if (column == "value") {

				String inverter = null;
				String value = null;
				for (int i = 0; i <= csvarray[0]; i++) {
					inverter = st.nextToken();

				}

				// Create token
				StringTokenizer st2 = new StringTokenizer(inverter, ";");
				for (int j = 0; j <= csvarray[1]; j++) {
					value = st2.nextToken();

				}
				// System.out.println("value: " + value);
				return value;
			}

		}
		return null;

	}

	@Override
	public boolean GotoNextElement() {
		/*
		 * try { if ((line = FileReader..readLine()) != null) { return true; }
		 * else { return false; } } catch (IOException e) { // TODO
		 * Auto-generated catch block
		 * 
		 * e.printStackTrace(); return false; }
		 */
	//	System.out.println("list_index: " + list_index);
		list_index--;
		if (list_index < 0) {
			return false;
		} else {

			line = buffer_list.get(list_index);
			return true;
		}

	}

}
