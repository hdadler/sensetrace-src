package com.ipv.sensetrace.controllingservice.intern;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ipv.sensetrace.cepservice.CEPDatastreamAnalyzerService;
//import com.ipv.sensetrace.mysqlservice.MySQLService;
import com.ipv.sensetrace.pgsqlservice.PgService;
import com.ipv.sensetrace.rdfdmservice.RDFDmService;

public class ImportSensorDataFromPg {
	private PgService pgsqlservice;
	private RDFDmService rdfservice;
	CEPDatastreamAnalyzerService cepservice;
	private TimeFormat timeformat = new TimeFormat();
	private boolean errorcheck_static = false;
	private boolean errorcheck_dynamic = false;
	private boolean classify = false;
	ArrayList<String> generate_stream_from_sensor = null;
	String[] TimeIntervall;

	public ImportSensorDataFromPg(CEPDatastreamAnalyzerService cepserviceref,
			PgService pgsqlservicerdf, RDFDmService rdfserviceref,
			Config confref, boolean classify_ref,
			boolean errorcheck_static_ref, boolean errorcheck_dynamic_ref,
			ArrayList<String> generate_stream_from_sensor_ref,
			String[] TimeIntervall_ref) {
		cepservice = cepserviceref;
		rdfservice = rdfserviceref;
		pgsqlservice = pgsqlservicerdf;
		//conf = confref;
		classify = classify_ref;
		errorcheck_static = errorcheck_static_ref;
		errorcheck_dynamic = errorcheck_dynamic_ref;
		generate_stream_from_sensor = generate_stream_from_sensor_ref;
		TimeIntervall = TimeIntervall_ref;
	}

	public void StartImport(String window, ArrayList<String> data_resolution) {
	//	System.out.println("startimport!!!!");
		//System.out.println("window:" + window);
		// System.out.println("window:"+window);
		/*try {
			Thread.sleep(3000);
		} catch (InterruptedException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}*/

		/**
		 * Tell pgqlservice to connect to db Read connection parameters from
		 * config file
		 */

		/*
		 * pgsqlservice.CreateConnection(conf.getProperty("pgsqlcs"),
		 * conf.getProperty("pgsqluser"), conf.getProperty("pgsqlpwd"));
		 */

		/**
		 * Import data in 1 month steps in order to create a virtual
		 * sensor-datastream *
		 */
		/**
		 * Set point in time from which to start import! Db-Import from Mysql:
		 * Always start from beginning! Import from Datalogger: After import
		 * store last imported timestamp!
		 * 
		 */
		ArrayList<String> sensorsforclassification_1sec = new ArrayList<String>();
		ArrayList<String> sensorsforerrorcheck_1sec = new ArrayList<String>();
		ArrayList<String> sensorsforclassification_1min = new ArrayList<String>();
		ArrayList<String> sensorsforerrorcheck_1min = new ArrayList<String>();
		ArrayList<String> sensorsforclassification_15min = new ArrayList<String>();
		ArrayList<String> sensorsforerrorcheck_15min = new ArrayList<String>();
		ArrayList<String> sensorsforclassification_1h = new ArrayList<String>();
		ArrayList<String> sensorsforerrorcheck_1h = new ArrayList<String>();
		ArrayList<String> sensorsforclassification_1day = new ArrayList<String>();
		ArrayList<String> sensorsforerrorcheck_1day = new ArrayList<String>();
		ArrayList<String> sensorsforclassification_1month = new ArrayList<String>();
		ArrayList<String> sensorsforerrorcheck_1month = new ArrayList<String>();

		if (window == "1day") {
			// GetListwithSensors
			if (data_resolution.contains("1sec")) {
				sensorsforclassification_1sec = rdfservice
						.GetClassificationSensors("1sec", true);
				System.out.println("get one second sensors!");
				sensorsforerrorcheck_1sec = rdfservice.GetErrorSensors("1sec",
						true);
			}
			if (data_resolution.contains("1min")) {
				sensorsforclassification_1min = rdfservice
						.GetClassificationSensors("1min", true);
				sensorsforerrorcheck_1min = rdfservice.GetErrorSensors("1min",
						true);
			}
			if (data_resolution.contains("15min")) {
				sensorsforclassification_15min = rdfservice
						.GetClassificationSensors("15min", true);
				sensorsforerrorcheck_15min = rdfservice.GetErrorSensors(
						"15min", true);
			}
			if (data_resolution.contains("1h")) {
				sensorsforclassification_1h = rdfservice
						.GetClassificationSensors("1h", true);
				sensorsforerrorcheck_1h = rdfservice
						.GetErrorSensors("1h", true);
			}
			if (data_resolution.contains("1day")) {
				sensorsforclassification_1day = rdfservice
						.GetClassificationSensors("1day", true);
				sensorsforerrorcheck_1day = rdfservice.GetErrorSensors("1day",
						true);
				System.out.println("Fetch 1day ...." + sensorsforerrorcheck_1day);
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else {
			if (data_resolution.contains("1day")) {
				sensorsforclassification_1day = rdfservice
						.GetClassificationSensors("1day", true);
				sensorsforerrorcheck_1day = rdfservice.GetErrorSensors("1day",
						true);
			}
			if (data_resolution.contains("1month")) {
				sensorsforclassification_1month = rdfservice
						.GetClassificationSensors("1month", true);
				sensorsforerrorcheck_1month = rdfservice.GetErrorSensors(
						"1monat", true);
			}
		}

		ArrayList<String> sensorsforclassification = null;// new
															// ArrayList<String>();
		ArrayList<String> sensorsforerrorcheck = null;// new
														// ArrayList<String>();
		/*
		 * sensorsforclassification.addAll(sensorsforclassification_1sec);
		 * sensorsforclassification.addAll(sensorsforclassification_1min);
		 * sensorsforclassification.addAll(sensorsforclassification_1h);
		 * sensorsforclassification.addAll(sensorsforclassification_1day);
		 * sensorsforclassification.addAll(sensorsforclassification_1monat);
		 * 
		 * sensorsforerrorcheck.addAll(sensorsforerrorcheck_1sec);
		 * sensorsforerrorcheck.addAll(sensorsforerrorcheck_1min);
		 * sensorsforerrorcheck.addAll(sensorsforerrorcheck_1h);
		 * sensorsforerrorcheck.addAll(sensorsforerrorcheck_1day);
		 * sensorsforerrorcheck.addAll(sensorsforerrorcheck_1monat);
		 */
		/*try {
			Thread.sleep(3000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/

		/* || generate_stream_from_sensor.size()==0 */
	/*	System.out.println("ClassificationSensors");
		for (String e : sensorsforclassification_1sec) {
			System.out.println("1sec: " + e);
		}
		for (String e : sensorsforclassification_1min) {
			System.out.println("1min: " + e);
		}
		for (String e : sensorsforclassification_1h) {
			System.out.println("1h: " + e);
		}
		for (String e : sensorsforclassification_1day) {
			System.out.println("1day: " + e);
		}
		for (String e : sensorsforclassification_1month) {
			System.out.println("1month: " + e);
		}
		System.out.println("ErrorSensors");
		for (String e : sensorsforerrorcheck_1sec) {
			System.out.println("1sec: " + e);
		}
		for (String e : sensorsforerrorcheck_1min) {
			System.out.println("1min: " + e);
		}
		for (String e : sensorsforerrorcheck_1h) {
			System.out.println("1h: " + e);
		}
		for (String e : sensorsforerrorcheck_1day) {
			System.out.println("1day: " + e);
		}
		for (String e : sensorsforerrorcheck_1month) {
			System.out.println("1month: " + e);
		}
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/

		/*
		 * try { Thread.sleep(2000); } catch (InterruptedException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */
		/*
		 * try { Thread.sleep(10000); } catch (InterruptedException e1) { //
		 * TODO Auto-generated catch block e1.printStackTrace(); }
		 */
		// long time
		Calendar cal = Calendar.getInstance(Locale.GERMAN);
		// cal.set(2006, 0, 1, 0, 0, 0); // setze auf 30.4.2004
		// java.util.Date tilldatetoimp = new java.util.Date();
		long tilldatetoimp_l = 0;
		// System.out.println(cal.getTime());
		// int n = 0;
		// timeformat.ConvertMillisecondsToSQLTime(cal.getTimeInMillis());

		/**
		 * Import till current date or value from time intervall and from 2006
		 * or given time intervall
		 */
		long lastdatetoinms = 0;
		if (TimeIntervall != null) {
			cal.setTimeInMillis(timeformat
					.ConvertSQLTimeToTimestamp(TimeIntervall[0]));
			lastdatetoinms = cal.getTimeInMillis();
			tilldatetoimp_l = timeformat
					.ConvertSQLTimeToTimestamp(TimeIntervall[1]);
		} else {
			cal.set(2006, 3, 19, 0, 0, 0); // setze auf 30.4.2004
			lastdatetoinms = cal.getTimeInMillis();
			tilldatetoimp_l = System.currentTimeMillis();
		}

		/*
		 * Import one month after the other till reaching current point in time
		 */
		while (lastdatetoinms < tilldatetoimp_l) {
			System.out.println("lastdatetoinms: "
					+ timeformat.ConvertMillisecondsToSQLTime(lastdatetoinms));
			System.out.println("tilldatetoimp_l: "
					+ timeformat.ConvertMillisecondsToSQLTime(tilldatetoimp_l));
			// mysqlservice.ReadData(datefrom, dateto, mysql_sensorid);
			/*
			 * Create Intervall of one month
			 */
			String datefrom = timeformat.ConvertMillisecondsToSQLTime(cal
					.getTimeInMillis());
			/*
			 * Count 1 day further
			 */
			// cal.add(Calendar.MONTH, 1);
			// Tagesweise einlesen
			String dateto = null;
			if (window.equals("1day")) {
				cal.add(Calendar.DAY_OF_YEAR, 1);
				System.out.println("range:" + window);
				System.out.println("Add one day!");

				if (cal.getTimeInMillis() >= tilldatetoimp_l) {
					dateto = timeformat
							.ConvertMillisecondsToSQLTime(tilldatetoimp_l);
					lastdatetoinms = tilldatetoimp_l;
					// System.out.println("Diff one day!");
					// cal.add(Calendar.DAY_OF_YEAR, -1);
				} else {
					dateto = timeformat.ConvertMillisecondsToSQLTime(cal
							.getTimeInMillis());
					lastdatetoinms = cal.getTimeInMillis();
				}
			}
			// jahresweise einlesen
			else {
				cal.add(Calendar.YEAR, 1);
				System.out.println("range:" + window);
				System.out.println("Add one year!");
				if (cal.getTimeInMillis() >= tilldatetoimp_l) {
					// System.out.println("Diff one year!");
					// cal.add(Calendar.YEAR, -1);
					dateto = timeformat
							.ConvertMillisecondsToSQLTime(tilldatetoimp_l);
					lastdatetoinms = tilldatetoimp_l;
				} else {
					dateto = timeformat.ConvertMillisecondsToSQLTime(cal
							.getTimeInMillis());
					lastdatetoinms = cal.getTimeInMillis();
				}
			}

			// System.out.println("dateto: " +
			// timeformat.ConvertMillisecondsToSQLTime(dateto));
			// Restart Jtalis
			/*try {
				Thread.sleep(3000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}*/
			System.out.println("Next day: Reset JTALIS");
			cepservice.ResetJtalis();
			/*
			 * try { Thread.sleep(10000); } catch (InterruptedException e) { //
			 * TODO Auto-generated catch block e.printStackTrace(); }
			 */

			rdfservice.QueryAllSensors();
			// String mysqlid = rdfservice.GetNextSensor("mysqlid");
			// If in jtalis-only mode, catch data only for sensors we need!
			String postgresid = rdfservice.GetNextSensor("postgresid");
			String lowerlimit = rdfservice.GetNextSensor("lowerlimit");
			String upperlimit = rdfservice.GetNextSensor("upperlimit");
			// Send value only if it changes by this difference to former value
			String differencetopreviousvalue = rdfservice
					.GetNextSensor("differencetopreviousvalue");

			while (postgresid != null) {

				// Wenn boardercheck aus ist und der sensor nicht in der liste
				// ist
				// überspringe die Verarbeitung des Sensors!
				// System.out.println("!checkboarders: "
				// +!checkboarders);
				// System.out.println("!generate_stream_from_sensor.contains(postgresid): "
				// +!generate_stream_from_sensor.contains(postgresid));
				// System.out.println("!sensorsforjtalis.contains(postgresid): "
				// +!sensorsforjtalis.contains(postgresid));
				// try {
				// Thread.sleep(1000);
				// } catch (InterruptedException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
				/*
				 * System.out.println("postgresid:" + postgresid);
				 * System.out.println("checkboarders:" + checkboarders);
				 */
				/*
				 * System.out.println("sensorsforjtalis.contains(postgresid):" +
				 * sensorsforjtalis.contains(postgresid)); try {
				 * Thread.sleep(1000); } catch (InterruptedException e1) { //
				 * TODO Auto-generated catch block e1.printStackTrace(); }
				 */
				int n = 0;
				String range = null;

				// Generate Sensorstream also for average values
				while (n < 6) {
					if (n == 0) {
						sensorsforclassification = sensorsforclassification_1sec;
						sensorsforerrorcheck = sensorsforerrorcheck_1sec;
						cepservice.SetRange("1sec");
						range = "1sec";
					}
					if (n == 1) {
						sensorsforclassification = sensorsforclassification_1min;
						sensorsforerrorcheck = sensorsforerrorcheck_1min;
						cepservice.SetRange("1min");
						range = "1min";
					}
					if (n == 2) {
						sensorsforclassification = sensorsforclassification_15min;
						sensorsforerrorcheck = sensorsforerrorcheck_15min;
						cepservice.SetRange("15min");
						range = "15min";
					}
					if (n == 3) {
						sensorsforclassification = sensorsforclassification_1h;
						sensorsforerrorcheck = sensorsforerrorcheck_1h;
						cepservice.SetRange("1hour");
						range = "1hour";
					}
					if (n == 4) {
						sensorsforclassification = sensorsforclassification_1day;
						sensorsforerrorcheck = sensorsforerrorcheck_1day;
						cepservice.SetRange("1day");
						range = "1day";
						
					}
					if (n == 5) {
						sensorsforclassification = sensorsforclassification_1month;
						sensorsforerrorcheck = sensorsforerrorcheck_1month;
						cepservice.SetRange("1month");
						range = "1month";
					}
					if ((classify && (sensorsforclassification
							.contains(postgresid) && (generate_stream_from_sensor
							.contains(postgresid) || generate_stream_from_sensor
							.size() == 0)))
							|| (errorcheck_dynamic && (sensorsforerrorcheck
									.contains(postgresid) && (generate_stream_from_sensor
									.contains(postgresid) || generate_stream_from_sensor
									.size() == 0)))
							|| ((errorcheck_static && rdfservice.IsStaticCheckActive(postgresid)) && (n==0)
									&& (generate_stream_from_sensor
											.contains(postgresid) || generate_stream_from_sensor
											.size() == 0)									
									)
									/*&& !classify
									&& !errorcheck_dynamic && (generate_stream_from_sensor
									.size() == 0)*/
									) {

						/*
						 * Check if sensordata must be send to
						 * staticcheckfunction or jtalis
						 */
						boolean useetalis = false;
						boolean boardercheck = false;
						if ((classify && sensorsforclassification
								.contains(postgresid))
								|| (errorcheck_dynamic && sensorsforerrorcheck
										.contains(postgresid))) {
							useetalis = true;
						}

						if (errorcheck_static && rdfservice.IsStaticCheckActive(postgresid)) {
							boardercheck = true;
						}

						System.out.println("Import: postgresid: " + postgresid);
						/*System.out.println("datefrom: " + datefrom);
						System.out.println("dateto: " + dateto);
						System.out.println("ll: " + lowerlimit);
						System.out.println("ul: " + upperlimit);
						System.out.println("difference_to_previous_value: "
								+ differencetopreviousvalue);*/

					
						
						try {
							pgsqlservice.FetchData(range, datefrom, dateto,
									postgresid);
						} catch (SQLException ex) {
							Logger lgr = Logger.getLogger(PgService.class.getName());
							lgr.log(Level.WARNING, ex.getMessage(), ex);
							// TODO Auto-generated catch block
							ex.printStackTrace();
							System.exit(0);
						}
						// mysqlservice.GotoNextElement();
						// We need it to catch first timestamp/value

						/*
						 * Import as long there are data (normally data of a
						 * month)
						 */
						String pgtimeold = null;
						String pgtime = null;
						String pgvalue = null;
						if (pgsqlservice.GotoNextElement()) {
							pgtime = pgsqlservice.GetElement("timestamp");
							pgvalue = pgsqlservice.GetElement("value");
						}

						// Register sensor at cepservice
						cepservice.NewSensor(postgresid,
								Float.parseFloat(lowerlimit),
								Float.parseFloat(upperlimit),
								differencetopreviousvalue, useetalis,
								boardercheck);

						// Wenn kein Wert in Datenbank, sende dennoch Intervall
						// zu
						// jtalis
						if (pgtime == null) {
							/*
							 * System.out.println("Send to jtalis");
							 * cepservice.SendData(datefrom, "null");
							 * cepservice.SendData(dateto, "null");
							 */
						}
						while (pgtime != null) {
							/*
							 * Here do something with the datastream
							 */
							// System.out.println("pgtime"+pgtime);
						
							if (range.contains("1day"))
							{
								 System.out.println("1day data: pgtime" + pgtime);
								try {
									Thread.sleep(2000);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							cepservice.SendData(pgtime, pgvalue);

							pgtimeold = pgtime;
							pgtime = null;
							pgvalue = null;
							if (pgsqlservice.GotoNextElement()) {
								pgtime = pgsqlservice.GetElement("timestamp");
								pgvalue = pgsqlservice.GetElement("value");
							}

							// Write last values in intervall
							if (pgtime == null) {
								//System.out.println("Store last intervall!");
								cepservice.NextSensor(pgtimeold);
							}
						}
					}
					n++;
				}
				// System.out.println("Next Sensor:");
				pgsqlservice.ExecuteBatch();

				postgresid = rdfservice.GetNextSensor("postgresid");
				lowerlimit = rdfservice.GetNextSensor("lowerlimit");
				upperlimit = rdfservice.GetNextSensor("upperlimit");
				differencetopreviousvalue = rdfservice
						.GetNextSensor("differencetopreviousvalue");

			}

			// tilldatetoimp = new java.util.Date();
			// tilldatetoimp_l = tilldatetoimp.getTime();
			/*
			 * try { Thread.sleep(1000); } catch (InterruptedException e) { //
			 * TODO Auto-generated catch block e.printStackTrace(); }
			 */
	
			//Jtalis could need time for calculations before exiting...
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Datastream finished...");
		}

	}
}
