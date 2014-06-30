package com.ipv.sensetrace.cephandlerservice;



public interface ICEPHandlerService {
	void HandleStaticCEPError(String sensorid, String from_s, String to_s, int add, String value, float oldvalue);
		
}
