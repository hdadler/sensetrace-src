package com.ipv.sensetrace.delphinftpservice;



public interface IDelphinFTPService {
	boolean FetchData(String ftplink, String tagid, String dateto, String user, String pwd);
	public String GetElement(String column);
	public boolean GotoNextElement();
}