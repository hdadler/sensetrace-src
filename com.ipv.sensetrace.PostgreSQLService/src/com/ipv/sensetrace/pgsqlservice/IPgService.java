package com.ipv.sensetrace.pgsqlservice;


public interface IPgService {
	boolean CreateConnection(String cs, String user, String pwd);
	public void ExecuteBatch();
	public void AddValueToBatch(String timestamp, String sensorid, String value);
	public String GetLastTimestamp(String postgresid);
	public String GetLastValue(String postgresid); 
	//public void AddErrorToBatch(String sensorid, String timestamp);
	
}