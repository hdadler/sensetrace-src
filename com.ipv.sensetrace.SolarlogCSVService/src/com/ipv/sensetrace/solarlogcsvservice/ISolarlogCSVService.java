package com.ipv.sensetrace.solarlogcsvservice;



public interface ISolarlogCSVService {
	boolean FetchData(String datefrom,
			String folder_str, String stringinfile, int[] csvarray);
	public String GetElement(String column);
	public boolean GotoNextElement();
}