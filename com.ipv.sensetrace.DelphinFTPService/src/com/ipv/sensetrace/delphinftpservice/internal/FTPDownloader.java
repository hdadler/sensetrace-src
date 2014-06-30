package com.ipv.sensetrace.delphinftpservice.internal;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileOutputStream;
import java.util.StringTokenizer;

public class FTPDownloader {
	public boolean download(String filename, String adress,
			String downloadfolder, String user, String pwd) {
		FTPClient client = new FTPClient();
		FileOutputStream fos = null;

		// Extrude servername from link

		StringTokenizer st = new StringTokenizer(adress, "/");
		String server = null;
		if (st.hasMoreTokens()) {
			server = st.nextToken();
		}
		String folderonserver = "";
		while (st.hasMoreTokens()) {
			folderonserver = folderonserver + "/" + st.nextToken();
		}
		;
		/*
		 * System.out.println("adress " + adress);
		 * System.out.println("filename " + filename);
		 * System.out.println("server: " + server);
		 * System.out.println("folderonserver: " + folderonserver);
		 * 
		 * System.out.println("server: " + server);
		 */
		/*
		 * Just download, if asc file in folder is older than 10 hours
		 */
		// Create handler for folder
		File dir = new File(downloadfolder);
		// Create handler for memoryascfile
		File asc = new File(downloadfolder+"/memory.asc");
		if ((asc.lastModified() / 1000 + 160000 < System.currentTimeMillis() / 1000)) {
			System.out.println("File older 80 hours! Download now!");
			deleteFolder(dir);
			dir.mkdir();

			// Create file to write to
			try {
				fos = new FileOutputStream(downloadfolder + "/" + filename);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			try {
				System.out.println("Connect now!");
				/*Timeout after 8 seconds*/
				client.setConnectTimeout(8000);
				client.connect(server);
				
				// Username and PWD
				client.login("user", "pwd");
				// client.login("ftp","ftp");

				client.setFileType(FTP.BINARY_FILE_TYPE);
				client.enterLocalPassiveMode();
				client.setUseEPSVwithIPv4(false);
				
				client.setControlKeepAliveTimeout(300); // set timeout to 5 minutes
				// client.enterRemotePassiveMode();

				//
				// The remote filename to be downloaded.
				//
				// String filename = "sitemap.xml";

				//
				// Download file from FTP server
				//
				System.out.println("Retrieve file!");
				client.retrieveFile(folderonserver + "/" + filename, fos);
				client.logout();
				client.disconnect();

				/*
				 * set folder to modified after download was successfull! If a
				 * download fails, file will be loaded again.
				 */
				dir.setLastModified(System.currentTimeMillis());
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				try {
					client.disconnect();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if (fos != null) {
					try {
						fos.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				deleteFolder(dir);
			}
		} else {
			System.out
					.println("File is not older than 10 hours! No new download.");
		}
		return false;
	}

	public static void deleteFolder(File folder) {
		File[] files = folder.listFiles();
		if (files != null) { // some JVMs return null for empty dirs
			for (File f : files) {
				if (f.isDirectory()) {
					deleteFolder(f);
				} else {
					f.delete();
				}
			}
		}
		folder.delete();
	}
}
