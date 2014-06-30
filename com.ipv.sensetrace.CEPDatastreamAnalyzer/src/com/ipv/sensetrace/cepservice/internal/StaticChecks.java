package com.ipv.sensetrace.cepservice.internal;

import com.ipv.sensetrace.cephandlerservice.CEPHandlerService;

public class StaticChecks {

	public void Init(CEPHandlerService cephservice_ref) {
		cephservice = cephservice_ref;
	}

	CEPHandlerService cephservice;
	String sensorid = "";
	float oldvalue = 0;
	float l_boarder = 0;
	float r_boarder = 0;
	// long oldtimestamp = 0;
	boolean lasttswasfaulty = false;
	TimeFormat timeformat = new TimeFormat();
	String oldtimestamp = null;
	// String timestamp = null;
	String intervallstart = null;
	String intervallend = null;

	public void SetSensorid(String sensorid_s) {
		sensorid = sensorid_s;
	}

	public void SetBoarders(float l_boarder_ref, float r_boarder_ref) {
		l_boarder = l_boarder_ref;
		r_boarder = r_boarder_ref;
	}

	public void InititialTimestamp() {
		// oldtimestamp = ref_timestamp;
	//	oldvalue = -1000;
		lasttswasfaulty = false;
		oldtimestamp = null;
		intervallstart = null;
	}

	public boolean CheckBoarders(String timestamp, float value) {
		 //System.out.println("ts: " +timestamp+" value: "+value);
		
		if ((value < l_boarder) || (value > r_boarder)) {
			intervallend=timestamp;
			// Neues Intervall beginnen
			if (!lasttswasfaulty) {

				intervallstart = timestamp;
			}
			lasttswasfaulty = true;
			// return false;
		}
		// Wenn Value gültig, Intervall beenden
		else {
			if (lasttswasfaulty) {
				UpdateFailureIntervall(intervallstart, timestamp, -1000);
				
				/* try { Thread.sleep(1000); } catch (InterruptedException e) {
				  e.printStackTrace(); }*/
				 
			}
			lasttswasfaulty = false;
		}
		oldvalue=value;
		oldtimestamp = timestamp;
		return lasttswasfaulty;
	}

	/*
	 * public void StoreIntervall(String lasttimestamp) {
	 * //lasttimestamp=lasttimestamp-1; System.out.println("Intervallstart: " +
	 * intervallstart); System.out.println("Intervallend: " + lasttimestamp);
	 * //If faulty intervall if (lasttswasfaulty)
	 * //UpdateFailureIntervall(intervallstart, lasttimestamp,0); }
	 */

	/*
	 * public void CheckSensorFailure(long timestamp, float value) {
	 * 
	 * // float value = Float.parseFloat(value_s); // Timestamp wird nur neu
	 * gesetzt, wenn sich der Wert nach 24 h ändert
	 * 
	 * if (timestamp - oldtimestamp > 86400) {
	 * 
	 * // System.out.println("No no values for longer 10 hours");
	 * 
	 * if (oldvalue != value) { System.out.println("End of Intervall reached");
	 * UpdateFailureIntervall(oldtimestamp, timestamp); try {
	 * Thread.sleep(10000); } catch (InterruptedException e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); } } } if (oldvalue !=
	 * value) { oldvalue = value; oldtimestamp = timestamp; }
	 * 
	 * }
	 */

	void UpdateFailureIntervall(String from_s, String to_s, int add) {

		cephservice.HandleStaticCEPError(sensorid, from_s, to_s, add, null, oldvalue);

	}
	
	public void CloseFailureIntervall(String timestamp) {
		//System.out.println("CloseFailureIntervall()");
		if (lasttswasfaulty)
		{
		System.out.println("Save intervall");
		UpdateFailureIntervall(intervallstart, timestamp, 0 );
		}
		
	}
}
