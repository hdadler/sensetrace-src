package com.ipv.sensetrace.cepservice;

import java.util.ArrayList;

import com.ipv.sensetrace.cephandlerservice.CEPHandlerService;
import com.ipv.sensetrace.cepservice.internal.Jtalis;
import com.ipv.sensetrace.cepservice.internal.StaticChecks;
import com.ipv.sensetrace.cepservice.internal.TimeFormat;
import com.ipv.sensetrace.mailservice.MailService;
import com.ipv.sensetrace.rdfdmservice.RDFDmService;


public class CEPDatastreamAnalyzerService implements
		ICEPDatastreamAnalyzerService {
	/**
	 * Here we define our services binded by component declaration in OSGI-INF
	 */
	private CEPHandlerService cephservice;
	private RDFDmService rdfservice;
	// private PgService pgsqlservice;
	private StaticChecks staticchecks = new StaticChecks();
	TimeFormat timeformat = new TimeFormat();
	private Jtalis jtalis = null;
	String sensorid;
	String range;
	boolean sendtojtalis = false;
	float oldvalue = -100000;
	boolean usejtalis = false;
	boolean checkboarders = false;
	boolean send_only_changed_values=false;
	float onepercentdiff = 0;
	boolean norangecheck = false;

	public boolean IsTaskOverAndQueueEmpty() {
		return cephservice.IsTaskOverAndQueueEmpty();
	}

	public void NewSensor(String sensorid_ref, float l_boarder,
			float r_boarder, String difference_to_previous_value,
			boolean usejtalis_ref, boolean checkboarders_ref) {
		if (difference_to_previous_value != null) {
			onepercentdiff = Float.parseFloat(difference_to_previous_value);
		} else {
			onepercentdiff = 5 * (r_boarder - l_boarder) / 100;
		}

		count = 0;
		///countsend = 0;
		oldvalue = -100000;
		sensorid = sensorid_ref;
		staticchecks.SetSensorid(sensorid);
		staticchecks.InititialTimestamp();
		staticchecks.SetBoarders(l_boarder, r_boarder);

		//System.out.println("onepercentdiff: " + onepercentdiff);
		checkboarders = checkboarders_ref;
		usejtalis = usejtalis_ref;
	}

	public void SetRange(String range_ref) {
		range = range_ref;
		if (range.equals("1sec")) {
			norangecheck = false;
			send_only_changed_values=true;
		} else {
			norangecheck = true;
			send_only_changed_values=false;
		}
	}

	public void Init(MailService mailservice) {
		// checkboarders = checkboarders_ref;
		// usejtalis = usejtalis_ref;
		// if (usejtalis) {

		jtalis = new Jtalis();
		jtalis.Init(rdfservice);

		System.out.println("Start thread");
		// Init Handler service

		cephservice.StartMsgHandler(rdfservice, mailservice);
		cephservice.RegisterRules();
		// }
	}

	public void ResetJtalis() {
		jtalis.Reset();
	}

	int count = 0;
	//int countsend = 0;

	public void SendData(String timestamp_s, String pgvalue_s) {
		// countsend++;
		if (usejtalis || checkboarders) {

			if (pgvalue_s != null) {
				if (count > 36000) {
					System.out.println("Import value: timestamp: "
							+ timestamp_s);
					count = 0;
				}
				/*
				 * Preconversations...
				 */

				float value = Float.parseFloat(pgvalue_s);

				// long timestamp =
				// timeformat.ConvertSQLTimeToTimestamp(timestamp_s) / 1000;

				/*
				 * Just do a second check if first check found no error.
				 */
				// Check values that have changed after previous value only
				// if (value != oldvalue) {

				if ((send_only_changed_values==false) || ((value > oldvalue + onepercentdiff)
						|| (value < oldvalue - onepercentdiff)
						|| (norangecheck) /*|| (countsend == 0)*/)) {
				///	countsend = 0;

					// System.out.println("oldvalue: " + oldvalue);
					// System.out.println("value: " + value);
					// System.out.println("onepercentdiff: " + onepercentdiff);
					// System.out.println("timestamp_s: " + timestamp_s);

					// Wenn nur boardercheck aktiviert
					if (checkboarders) {
						// System.out.println("checkboarder: " + oldvalue +
						// " ts: "+timestamp_s+" sensorid" + sensorid);
						staticchecks.CheckBoarders(timestamp_s, value);
						// staticchecks.CheckSensorFailure(timestamp, value);
					}
					if (usejtalis) {
						/*System.out.println("ts: " + timestamp_s + "value: "
								+ value);*/
						jtalis.PushEvent(range, sensorid, timestamp_s, value);
					}
					// Wenn nur jtalis aktiviert

					oldvalue = value;
				}
				count++;
			/*	countsend++;
				// Value wird nach 1h gesendet
				if (countsend > 3599) {
					countsend = 0;
					// System.out.println("Set countsend to 1");
				}*/
			}

		}
	}

	public void NextSensor(String timeto) {
		if (checkboarders) {
			staticchecks.CloseFailureIntervall(timeto);
		}
		if (usejtalis) {
			if (range.equals("1day"))
			{
				timeto=timeformat.ConvertMillisecondsToSQLTime(timeformat.ConvertSQLTimeToTimestamp(timeto)+86399000);
			}
			else if (range.equals("1hour"))
			{
			
				timeto=timeformat.ConvertMillisecondsToSQLTime(timeformat.ConvertSQLTimeToTimestamp(timeto)+3599000);
				System.out.println("timeto: " + timeto);
			}
			else if (range.equals("15min"))
			{
				timeto=timeformat.ConvertMillisecondsToSQLTime(timeformat.ConvertSQLTimeToTimestamp(timeto)+899000);
			}
			else if (range.equals("1min"))
			{
				timeto=timeformat.ConvertMillisecondsToSQLTime(timeformat.ConvertSQLTimeToTimestamp(timeto)+59000);
			}
			
			//System.out.println("Pushing last element of interval: ts: " + timeto + "value: " + oldvalue);
			jtalis.PushEvent(range, sensorid, timeto, oldvalue);
		}
	}

	/*
	 * public void SendData(long timestamp, String pgvalue_s) { if (pgvalue_s !=
	 * null) {
	 */

	/*
	 * Preconversations...
	 */
	/*
	 * float value = Float.parseFloat(pgvalue_s); timestamp=timestamp/1000;
	 * String timestamp_s = timeformat.ConvertMillisecondsToSQLTime(timestamp);
	 */
	/*
	 * Just do a second check if first check found no error.
	 */
	/*
	 * if (staticchecks.CheckBoarders(timestamp_s,value)) {
	 * //staticchecks.CheckSensorFailure(timestamp, value); }
	 * 
	 * } }
	 */

	// Method will be used by DS to set the RDFDm service
	public synchronized void RegRDFDmService(RDFDmService service) {
		System.out.println("Register RDFDmService");
		rdfservice = service;
	}

	// Method will be used by DS to unregister the RDFDm service
	public synchronized void UnregRDFDmService(RDFDmService service) {
		System.out.println("Unregister RDFDmService");
		if (rdfservice == service) {
			rdfservice = null;
		}
	}

	// Method will be used by DS to set the RDFDm service
	public synchronized void RegCEPHandlerService(CEPHandlerService service) {
		System.out.println("Register CEPHandlerService");
		cephservice = service;
		staticchecks.Init(cephservice);
	}

	// Method will be used by DS to unregister the RDFDm service
	public synchronized void UnregCEPHandlerService(CEPHandlerService service) {
		System.out.println("Unregister CEPHandlerService");
		if (cephservice == service) {
			cephservice = null;
		}
	}

	public void RegisterCEPHelperRules(ArrayList<String> getHelperRules) {
		// TODO Auto-generated method stub
		int n = 0;

		while (n < getHelperRules.size()) {
			// System.out.println("Register helper rule:" +
			// getHelperRules.get(n).toString());
			String[] tokens = getHelperRules.get(n).toString().split(":");
			// System.out.println("Register helper rule:" + tokens[0]);
			// System.out.println("Register helper rule:" + tokens[2]);

			jtalis.AddRule(tokens[0], tokens[1]);
			n++;
		}
		/*
		 * try { Thread.sleep(10000); } catch (InterruptedException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */
	}

	public void RegisterClRules(ArrayList<String> getClRules) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		int n = 0;

		while (n < getClRules.size()) {
			String[] tokens = getClRules.get(n).toString().split(":");
			System.out.println("Register classification rule:" + tokens[0]);
			jtalis.AddRule(tokens[0], tokens[1]);
			n++;
		}

	}

	public void RegisterErrorRules(ArrayList<String> getClRules) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		int n = 0;

		while (n < getClRules.size()) {
			String[] tokens = getClRules.get(n).toString().split(":");
			System.out.println("Register error rule:" + tokens[0]);
			jtalis.AddRule(tokens[0], tokens[1]);
			n++;
		}

	}

}
