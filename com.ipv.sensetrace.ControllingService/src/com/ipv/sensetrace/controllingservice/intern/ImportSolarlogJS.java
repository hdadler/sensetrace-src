/*
 * Diese Datei kann gelöscht werden!
 */

package com.ipv.sensetrace.controllingservice.intern;


import com.ipv.sensetrace.cepservice.CEPDatastreamAnalyzerService;
import com.ipv.sensetrace.pgsqlservice.PgService;
import com.ipv.sensetrace.rdfdmservice.RDFDmService;
import com.ipv.sensetrace.solarlogcsvservice.SolarlogCSVService;

public class ImportSolarlogJS {
	private PgService pgsqlservice;
	private SolarlogCSVService dlservice=new SolarlogCSVService();
	private RDFDmService rdfservice;
	private Config conf;
	private TimeFormat timeformat = new TimeFormat();

	public ImportSolarlogJS(
			CEPDatastreamAnalyzerService cepserviceref,PgService pgsqlservicerdf,
			RDFDmService rdfserviceref, SolarlogCSVService dlserviceref, Config confref) {
		//dlservice = dlserviceref;
		rdfservice = rdfserviceref;
		pgsqlservice = pgsqlservicerdf;
		conf = confref;

	}

	public boolean IsAllBatched()
	{
	//	System.out.println("conf.getProperty(solarlogjsfiles)"+conf.getProperty("solarlogjsfiles"));
		return dlservice.IsAllBatched(conf.getProperty("solarlogjsfiles"));
		
	}
	public String RemoveOldestFilesToBatch()
	{
		return dlservice.RemoveOldestFilesToBatch(conf.getProperty("solarlogjsfiles"));
			
	}
	

	
	public void StartImport() {



		/*
		 * Give parameters to dataloggerbundle
		 */


		// Catch sensordate to be downloaded from datalogger (all sensors)
		rdfservice.QuerySensors(true);
		//System.out.println("after querysensors");

		// String sensorname = "W_alpha_1Sec";
		String postgresid = rdfservice.GetNextSensor("postgresid");
	//	rdfservice.GetNextSensor("postgresid");
		//String ftplink = rdfservice.GetNextSensor("ftplink");
		String solarlogfilenamelink = rdfservice.GetNextSensor("solarlogfilenamelink");
		//String definition = rdfservice.GetNextSensor("definition");
		String folder=conf.getProperty("solarlogjsfiles");
		String csvarray=rdfservice.GetNextSensor("csvarray");
		while ((postgresid != null)) {
		/*	System.out.println("folder: " + folder);
			System.out.println("csvarray: " + csvarray);
			System.out.println("solarlogfilenamelink: " + solarlogfilenamelink);
			System.out.println("Import Sensor with postgresid: " + postgresid
					+" solarlogfilenamelink: " +solarlogfilenamelink);*/
		/*	try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		
			/*
			 * Letzten Wert aus der Datenbank holen - Intervalanfang
			 */
			long nextpgtimestamp = 0;
			long lastpgtimestamp_l = 0;
			int csvarray_int[]={0,0};
			 csvarray_int[0]=Integer.parseInt(csvarray.split(",")[0]);
			 csvarray_int[1]=Integer.parseInt(csvarray.split(",")[1]);
						String nextpgvalue = null;
			//System.out.println("Get Last Timestamp");
			String lastpgtimestamp = pgsqlservice.GetLastTimestamp(postgresid);
			if (lastpgtimestamp != null) {
				lastpgtimestamp_l = timeformat
						.ConvertSQLTimeToTimestamp(lastpgtimestamp);
				nextpgtimestamp = lastpgtimestamp_l + 60000;
				nextpgvalue = pgsqlservice.GetLastValue(postgresid);

			}
			
			//System.out.println("lastpgtimestamp: " + lastpgtimestamp);
	
		//	System.out.println("solarlogfilenamelink: " + solarlogfilenamelink);

			
			if (dlservice.FetchData(  lastpgtimestamp,
					folder, solarlogfilenamelink, csvarray_int)) {

				/*
				 * Import all data from last imported timestamp
				 */
				String lastdltime = dlservice.GetElement("timestamp");
				int n = 0;
				/*System.out.println("lastdltimestamp: " + lastdltime
						+ " lastpgtimestamp: " + lastpgtimestamp);
				System.out.println("lastdltimestamp: "
						+ timeformat.ConvertSLTimeToTimestamp(lastdltime," ")
						+ " lastpgtimestamp: " + lastpgtimestamp_l);*/
				//If pgvalue was null, then get first value and timestamp from csv file
				//lastpgtimestamp
				/*try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/

				while ((lastdltime = dlservice.GetElement("timestamp")) != null) {
					String lastdbvalue = dlservice.GetElement("value");
					/*System.out.println("value: " + lastdbvalue);
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}*/
					if (lastdbvalue.equals("invalid")) {
						lastdbvalue = null;
					}

					long lastdltimestamp = timeformat
							.ConvertSLTimeToTimestamp(lastdltime," ");

					/*
					 * If Pg-Database still empty, get values from mysql
					 */
					if (lastpgtimestamp_l == 0) {

						nextpgtimestamp = lastdltimestamp;
						nextpgvalue = lastdbvalue;
					}

					while (nextpgtimestamp < lastdltimestamp) {
						//Just one conversation, faster
						lastpgtimestamp = timeformat.ConvertMillisecondsToSQLTime(nextpgtimestamp);
					/*	System.out.println("lastpgtimestamp: " + lastpgtimestamp);
						System.out.println("postgresid: " + postgresid);
						System.out.println("nextpgvalue: " + nextpgvalue);*/
						/*try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}*/
						pgsqlservice.AddValueToBatch(lastpgtimestamp,
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
						nextpgtimestamp = nextpgtimestamp + 60000;
					}
					/*
					 * Insert last value
					 */
					if (nextpgtimestamp == lastdltimestamp) {

						nextpgvalue = lastdbvalue;
						//Just one conversation, faster
						lastpgtimestamp = timeformat.ConvertMillisecondsToSQLTime(nextpgtimestamp);
						//System.out.println(ts);
						pgsqlservice.AddValueToBatch(lastpgtimestamp,
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
						//Go on for one second (1000) , for one minute 60000
						nextpgtimestamp = nextpgtimestamp + 60000;
					}
					
					
					dlservice.GotoNextElement();
			
				}
				
				pgsqlservice.ExecuteBatch();
				pgsqlservice.SaveLastMeasurementValue();
				pgsqlservice.Commit_Con();
				//System.out.println("Next Sensor");
				
				
				// postgresid = rdfservice.GetNextSensor("postgresid");

			}
			//System.out.println("Next Sensor:");
			postgresid = rdfservice.GetNextSensor("postgresid");
			csvarray = rdfservice.GetNextSensor("csvarray");
		//	solarlogfilenamelink = rdfservice.GetNextSensor("ftplink");
			//definition = rdfservice.GetNextSensor("definition");
			solarlogfilenamelink= rdfservice.GetNextSensor("solarlogfilenamelink");
		}
	}
}
