package com.ipv.sensetrace.delphincsvservice;



public interface IDelphinCSVService {
	boolean FetchData(String ftplink, String dlid, String datefrom,
			String folder_str, String stringinfile);
	public String GetElement(String column);
	public boolean GotoNextElement();
}