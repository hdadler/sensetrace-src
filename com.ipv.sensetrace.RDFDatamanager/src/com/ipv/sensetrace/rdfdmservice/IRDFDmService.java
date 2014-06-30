package com.ipv.sensetrace.rdfdmservice;

public interface IRDFDmService {
	public void StoreRDFData(String rdfstring);

	boolean DeleteOnServer(String address);


	
	boolean UpdateOnServer(String address);

	//void QuerySensors();
	
	String GetNextSensor(String key);

	void QueryAllSensors();

	void QuerySensors();


	String ResolveSensor(String id, boolean active);

	//void QueryActivatedSensors();
}
