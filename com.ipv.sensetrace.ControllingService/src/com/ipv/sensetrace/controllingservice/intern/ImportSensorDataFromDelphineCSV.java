/*
 * Diese Datei kann gelöscht werden!
 */

package com.ipv.sensetrace.controllingservice.intern;


import com.ipv.sensetrace.cepservice.CEPDatastreamAnalyzerService;
import com.ipv.sensetrace.pgsqlservice.PgService;
import com.ipv.sensetrace.rdfdmservice.RDFDmService;

public class ImportSensorDataFromDelphineCSV {
	private PgService pgsqlservice;
	private ReadFROMCSV dlservice=new ReadFROMCSV();
	private RDFDmService rdfservice;
	private Config conf;
	private TimeFormat timeformat = new TimeFormat();

	public ImportSensorDataFromDelphineCSV(
			CEPDatastreamAnalyzerService cepserviceref,PgService pgsqlservicerdf,
			RDFDmService rdfserviceref,Config confref) {
		//dlservice = dlserviceref;
		rdfservice = rdfserviceref;
		pgsqlservice = pgsqlservicerdf;
		//cepservice = cepserviceref;
		conf = confref;

	}

	public void StartImport() {



		/*
		 * Give parameters to dataloggerbundle
		 */
		dlservice.Init(conf.getProperty("memfiles"),
				conf.getProperty("memconvert"));
		
		// rdfservice.QuerySensors();
		// String mysqlid = "";
		// mysqlid = rdfservice.GetNextSensor("mysqlid");

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
		// Look for last point in database for every sensor

		// Catch sensordate to be downloaded from datalogger (all sensors)
		rdfservice.QuerySensors();
		//System.out.println("after querysensors");

		String postgresid = "-1";
		String ftplink = "";
		String definition = "";
		// String sensorname = "W_alpha_1Sec";
		postgresid = rdfservice.GetNextSensor("postgresid");
		ftplink = rdfservice.GetNextSensor("ftplink");
		definition = rdfservice.GetNextSensor("definition");
		String lowerlimit = rdfservice.GetNextSensor("lowerlimit");
		String upperlimit = rdfservice.GetNextSensor("upperlimit");
		//go to sensor 122
/*
while (!postgresid.equals("122")) {
//		while (!postgresid.equals("172")) {
			postgresid = rdfservice.GetNextSensor("postgresid");
			ftplink = rdfservice.GetNextSensor("ftplink");
			definition = rdfservice.GetNextSensor("definition");
			 lowerlimit = rdfservice.GetNextSensor("lowerlimit");
			 upperlimit = rdfservice.GetNextSensor("upperlimit");
		}*/
		while (/*(!postgresid.equals("122")) && */(postgresid != null)) {

			System.out.println("Import Sensor with postgresid: " + postgresid
					+ " Definition: " + definition);
		
			/*
			 * Letzten Wert aus der Datenbank holen - Intervalanfang
			 */
			long nextpgtimestamp = 0;
			long lastpgtimestamp_l = 0;
			String nextpgvalue = null;
			String lastpgtimestamp = pgsqlservice.GetLastTimestamp(postgresid);
			 System.out.println("LastTimestampinDB: " + lastpgtimestamp);
			if (lastpgtimestamp != null) {
				lastpgtimestamp_l = timeformat
						.ConvertSQLTimeToTimestamp(lastpgtimestamp);
				nextpgtimestamp = lastpgtimestamp_l + 1000;
				nextpgvalue = pgsqlservice.GetLastValue(postgresid);

			}
			System.out.println("lastpgtimestamp: " + lastpgtimestamp);
			if (dlservice.FetchData(ftplink, definition, lastpgtimestamp)) {
				System.out.println("Fetchdata");

				/*
				 * Import all data from last imported timestamp
				 */
				String lastdltime = dlservice.GetElement("timestamp");
				int n = 0;
				System.out.println("lastdltimestamp: " + lastdltime
						+ " lastpgtimestamp: " + lastpgtimestamp);
				System.out.println("lastdltimestamp: "
						+ timeformat.ConvertDLTimeToTimestamp(lastdltime,",")
						+ " lastpgtimestamp: " + lastpgtimestamp_l);
				/*try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/

				while ((lastdltime = dlservice.GetElement("timestamp")) != null) {
					String lastdbvalue = dlservice.GetElement("value");
					if (lastdbvalue.equals("invalid")) {
						lastdbvalue = null;
					}

					long lastdltimestamp = timeformat
							.ConvertDLTimeToTimestamp(lastdltime,",");

					/*
					 * If Pg-Database still empty, get values from mysql
					 */
					if (lastpgtimestamp_l == 0) {

						nextpgtimestamp = lastdltimestamp;
						nextpgvalue = lastdbvalue;
					}

					while (nextpgtimestamp < lastdltimestamp) {
						//Just one conversation, faster
						String ts = timeformat.ConvertMillisecondsToSQLTime(nextpgtimestamp);
						pgsqlservice.AddValueToBatch(ts,
								postgresid, nextpgvalue);
						//cepservice.SendData(ts, nextpgvalue);

						/*
						 * try { Thread.sleep(1000); } catch
						 * (InterruptedException e) { // TODO Auto-generated
						 * catch block e.printStackTrace(); }
						 */
						n++;
						if (n > 100000) {
							//System.out.println("Value: " + nextpgvalue
								//	+ " Timestamp: " + nextpgtimestamp);
							n = 0;
							pgsqlservice.ExecuteBatch();
							//System.out.println("pgsqlservice.ExecuteBatch();");
						}
						lastpgtimestamp_l = nextpgtimestamp;
						nextpgtimestamp = nextpgtimestamp + 1000;
					}
					/*
					 * Insert last value
					 */
					if (nextpgtimestamp == lastdltimestamp) {

						nextpgvalue = lastdbvalue;
						//Just one conversation, faster
						String ts = timeformat.ConvertMillisecondsToSQLTime(nextpgtimestamp);
						//System.out.println(ts);
						pgsqlservice.AddValueToBatch(ts,
								postgresid, nextpgvalue);
						//cepservice.SendData(ts, nextpgvalue);

						n++;
						if (n > 100000) {
							/*System.out.println("Value: " + nextpgvalue
									+ " Timestamp: " + nextpgtimestamp);*/
							n = 0;
							pgsqlservice.ExecuteBatch();
							//System.out.println("pgsqlservice.ExecuteBatch();");
						}
						lastpgtimestamp_l = nextpgtimestamp;
						nextpgtimestamp = nextpgtimestamp + 1000;
					}
					
					
					dlservice.GotoNextElement();
			
				}
				
				pgsqlservice.ExecuteBatch();
				pgsqlservice.SaveLastMeasurementValue();
				pgsqlservice.Commit_Con();
				System.out.println("Next Sensor:");
				
				// postgresid = rdfservice.GetNextSensor("postgresid");

			}
			postgresid = rdfservice.GetNextSensor("postgresid");
			ftplink = rdfservice.GetNextSensor("ftplink");
			definition = rdfservice.GetNextSensor("definition");
		}
	}
}
