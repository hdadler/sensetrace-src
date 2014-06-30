package com.ipv.sensetrace.controllingservice;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

import com.ipv.sensetrace.cepservice.CEPDatastreamAnalyzerService;
import com.ipv.sensetrace.controllingservice.intern.Config;
import com.ipv.sensetrace.controllingservice.intern.ImportDataloggerFTP;
import com.ipv.sensetrace.controllingservice.intern.ImportDataloggerFolder;
import com.ipv.sensetrace.controllingservice.intern.ImportSensorDataFromDelphineCSV;
import com.ipv.sensetrace.controllingservice.intern.ImportSensorDataFromPg;
import com.ipv.sensetrace.controllingservice.intern.ProcessSensorMLFiles;
import com.ipv.sensetrace.controllingservice.intern.TimeFormat;
import com.ipv.sensetrace.delphincsvservice.DelphinCSVService;
import com.ipv.sensetrace.delphinftpservice.DelphinFTPService;
import com.ipv.sensetrace.mailservice.MailService;
//import com.ipv.sensetrace.mysqlservice.MySQLService;
import com.ipv.sensetrace.pgsqlservice.PgService;
import com.ipv.sensetrace.rdfdmservice.RDFDmService;


public class ControlService implements IControlService {

	/**
	 * Here we define our services binded by component declaration in OSGI-INF
	 */
	// private MySQLService mysqlservice;
	private static RDFDmService rdfservice;
	private static PgService pgsqlservice;
	private static DelphinFTPService dlftpservice;
	private static DelphinCSVService dlcsvservice;
	private static CEPDatastreamAnalyzerService cepservice;
	// private CEPHandlerService cephandlersrevice;
	private static MailService mailservice;
	// private static CommandProvider commandservice;

	// Other classes
	static Config conf = null;
	static TimeFormat timeformat = null;
	ArrayList<String> sensor_list = new ArrayList<String>();

	/**
	 * This is our method for initializing the objekt containing the config
	 * file.
	 */

	private void init_for_pg_bundle() {
		// Check if Config folder exi
		CheckForConfigurationFolder();
		try {
			/**
			 * Create an object for reading the config parameters, e.g.
			 * pgsqlconnectionstring
			 * 
			 */
			conf = new Config();
			timeformat = new TimeFormat();

			// cepservice.Init(mailservice);

		} catch (Exception e) {
			// e.printStackTrace();
			System.err.println("Program exit. Cause: No Config file found.");
			// System.exit(0);
		}

	}

	void activate() {
		System.out.println("Activate ControllingService.");
	}

	public void DeleteFromCLTable(boolean justavgs) {
		pgsqlservice.DeleteFromCLTable(rdfservice.GetCLIds(true),
				timeintervall[0], timeintervall[1], justavgs);
	}

	public void SetVirtualDsOptions(boolean classify_ref,
			boolean errorcheck_dynamic_ref, boolean errorcheck_static_ref) {
		errorcheck_static = errorcheck_static_ref;
		errorcheck_dynamic = errorcheck_dynamic_ref;
		classify = classify_ref;
		System.out.println("classify: " + classify);
	}

	public void SetDate(String from, String to) {

		timeintervall[0] = from;
		timeintervall[1] = to;
	}

	public void SetLastImportDate() {
		if (lastimportdate != null)
			pgsqlservice.SetLastImportDate(lastimportdate);

	}

	public void SetLastImportDateToNull() {
		lastimportdate = null;
		pgsqlservice.SetLastImportDate(lastimportdate);

	}

	public void SetTimeIntervallFromLastImportTillNow() {
		// System.out.println("pgsqlservice.GetLastImportDate()"+pgsqlservice.GetLastImportDate());
		timeintervall[0] = pgsqlservice.GetLastImportDate();
		System.out.println("System.currentTimeMillis()"
				+ System.currentTimeMillis());
		System.out
				.println("ConvertMillisecondsToSQLTime(System.currentTimeMillis())"
						+ timeformat.ConvertMillisecondsToSQLTime(System
								.currentTimeMillis()));
		timeintervall[1] = timeformat.ConvertMillisecondsToSQLTime(System
				.currentTimeMillis());
	}

	public void SetTestTimeIntervallFromLastImportTillNow() {
		// System.out.println("pgsqlservice.GetLastImportDate()"+pgsqlservice.GetLastImportDate());
		timeintervall[0] = "2014-01-01 00:00:00";
		System.out.println("System.currentTimeMillis()"
				+ System.currentTimeMillis());
		System.out
				.println("ConvertMillisecondsToSQLTime(System.currentTimeMillis())"
						+ timeformat.ConvertMillisecondsToSQLTime(System
								.currentTimeMillis()));
		timeintervall[1] = "2014-01-01 03:00:00";
	}

	public void SetTimeIntervallFromTwoMonthBeforeLastImportTillNow() {
		// set timefrom 63 days before the last importdate
		long timefrom_l = timeformat.ConvertSQLTimeToTimestamp(pgsqlservice
				.GetLastImportDate()) - 5443200;
		timeintervall[0] = timeformat.ConvertMillisecondsToSQLTime(timefrom_l);
		timeintervall[1] = timeformat.ConvertMillisecondsToSQLTime(System
				.currentTimeMillis());
	}

	String range_window = "1day";
	String data_resolution[] = { "1sec" };

	public void SetWindowAndResolution(String range_window_ref,
			String data_resolution_ref[]) {
		range_window = range_window_ref;
		data_resolution = data_resolution_ref;
	}

	public void SetSensors(String[] sensors_ref) {
		sensors = sensors_ref;
	}

	boolean check_if_avg_exists = true;
	boolean errorcheck_static = false;
	boolean errorcheck_dynamic = false;
	boolean classify = false;
	String[] timeintervall = { "2006-01-01 00:00:00", "2007-01-01 00:00:00" };

	String lastimportdate = null;
	String[] sensors = {};

	public void Setcheck_if_avg_exists(boolean flag) {
		check_if_avg_exists = flag;
	}

	public void CalcAvgsForAllSensors() {

		System.out.println("Starting average calculation...");
		pgsqlservice.CalculateAverages(null, null, timeintervall[0],
				timeintervall[1], sensor_list);
		/*
		 * pgsqlservice.CalculateAverages(rdfservice.GetErrorSensors(range,
		 * active), timeintervall[0], timeintervall[1]);
		 */
		System.out.println("Average calculation finished!");
	}

	public void CalcAvgsForErrorSensors() {

		System.out.println("Starting average calculation...");
		/*
		 * boolean sensor_for_resolution_exists = false; int n = 0; while (n <
		 * data_resolution.length) { if
		 * (rdfservice.GetErrorSensors(data_resolution[n], true) != null) {
		 * sensor_for_resolution_exists = true; } n++; } if
		 * (sensor_for_resolution_exists) {
		 */
		pgsqlservice.CalculateAverages(rdfservice.SensorsToReplace(true), null,
				timeintervall[0], timeintervall[1], sensor_list);
		// }
		/*
		 * pgsqlservice.CalculateAverages(rdfservice.GetErrorSensors(range,
		 * active), timeintervall[0], timeintervall[1]);
		 */
		System.out.println("Average calculation finished!");
	}

	public void CalcAvgsWithoutErrorSensors2() {

		System.out.println("Starting average calculation...");

		// Range dynamisch - Änder!!!
		pgsqlservice.CalculateAverages(null, rdfservice.SensorsToReplace(true),
				timeintervall[0], timeintervall[1], sensor_list);
		/*
		 * pgsqlservice.CalculateAverages(rdfservice.GetErrorSensors(range,
		 * active), timeintervall[0], timeintervall[1]);
		 */
		System.out.println("Average calculation finished!");
	}

	public void CalcAvgsForGivenSensors(boolean auto) {
		if (!timeintervall[0]
				.matches("((19|20)\\d\\d)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01]) "
						+ "[0-9]{2}:[0-9]{2}:[0-9]{2}")
				|| !timeintervall[1]
						.matches("((19|20)\\d\\d)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01]) "
								+ "[0-9]{2}:[0-9]{2}:[0-9]{2}")) {
			System.out
					.println("Invalid date intervall. Intervall has to be in the form"
							+ " from date1 to date2. Example:"
							+ " from 2007-01-01 00:00:00 to 2007-01-02 00:00:00");
			System.exit(0);

		} else {

			// Find sensors with missing average data
			if (auto) {
				ArrayList<String> SensorsWithAvg = new ArrayList<String>();
				System.out.println("Find sensors with missing average data");
				long days = (timeformat
						.ConvertSQLTimeToTimestamp(timeintervall[1]) - timeformat
						.ConvertSQLTimeToTimestamp(timeintervall[0]))
						/ (3600000 * 24);
				SensorsWithAvg = pgsqlservice.SensorsWithAvg(timeintervall[0],
						timeintervall[1], days);
				pgsqlservice.CalculateAverages(null, SensorsWithAvg,
						timeintervall[0], timeintervall[1], sensor_list);
			} else {
				System.out.println("Starting average calculation...");
				if (sensors.length == 0) {
					pgsqlservice.CalculateAverages(null, null,
							timeintervall[0], timeintervall[1], sensor_list);
				} else {
					pgsqlservice.CalculateAverages(
							new ArrayList<String>(Arrays.asList(sensors)),
							null, timeintervall[0], timeintervall[1],
							sensor_list);
				}
			}
			System.out.println("Average calculation finished!");
		}
	}

	// Init RDF and CEP
	public void Init(boolean cep_active) {
		mailservice.Init(conf.getProperty("mail_from"),
				conf.getProperty("mail_to"), conf.getProperty("smtp_server"),
				conf.getProperty("smtp_port"), conf.getProperty("smtp_user"),
				conf.getProperty("smtp_pwd"), conf.getProperty("smtp_auth"),
				conf.getProperty("starttls"));
		System.out.println("Starting up system!");
		/**
		 * Create an object for processing the SensorMLFiles
		 * 
		 */

		ProcessSensorMLFiles ProcessSensorML = new ProcessSensorMLFiles(
				rdfservice, conf);

		try {
			ProcessSensorML.Process();
			// System.out.println("Starting up system!");
			/*
			 * try { Thread.sleep(3000); } catch (InterruptedException e1) { //
			 * TODO Auto-generated catch block e1.printStackTrace(); }
			 */
		} catch (Exception e) {
			e.printStackTrace();
			System.err
					.println("Program exit. Cause: Problem with reading SensorML-Files.");
			System.exit(0);
		}

		// Doing some checks
		// Is there a double sensorid?

		if (!rdfservice.QueryForMultipleSensorids().equals("")) {
			System.out
					.println("SensorML-File uses following Sensorids more then once: "
							+ rdfservice.QueryForMultipleSensorids());
			System.exit(0);
		}
		if (!rdfservice.QueryForDifferentDbIds().equals("")) {
			System.out
					.println("SensorML-File uses different ids for postresid and mysqlid: "
							+ rdfservice.QueryForDifferentDbIds());
			System.exit(0);
		}
		// Check if a rule has changed
		int n = 0;
		while (n < rdfservice.GetClRules(true).size()) {
			String tmp = rdfservice.GetClRules(true).get(n).toString()
					.replace(" ", "");
			// Postgres cant handle a simple "'", just "''"
			// tmp = tmp.replace("'", "''");
			String[] tokens = tmp.split(":");
			System.out.println("id: " + tokens[2] + " rule: " + tokens[0]);

			// if rule is not registered yet, register it
			if (pgsqlservice.GetRegisteredRule(tokens[2], tokens[0]) == null) {
				pgsqlservice.RegisterRule(tokens[2],
						tokens[0].replace("'", "''"));
			}

			// if rule has changed
			System.out.println("tokens[0]: " + tokens[0]);
			System.out.println("db query: "
					+ pgsqlservice.GetRegisteredRule(tokens[2], tokens[0]));
			if (!pgsqlservice.GetRegisteredRule(tokens[2],
					tokens[0].replace("'", "''")).equals(tokens[0])) {

				// Abfrage
				System.out.println("WARNING: Rule with id " + tokens[2]
						+ " has changed!");
				System.out
						.println(" To prevent the system from inconsistency classification "
								+ "values of this rule will be deleted. "
								+ "To do this enter [yes].");
				String input = "";
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(System.in));
				try {
					input = reader.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (input.equals("yes")) {
					pgsqlservice.DeleteFromCLTable(new ArrayList<String>(Arrays
							.asList(tokens[2])));
					pgsqlservice.RegisterRule(tokens[2],
							tokens[0].replace("'", "''"));
				} else {
					System.out.println("Exit Program!");
					System.exit(0);
				}
			}

			n++;
		}

		{
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// Only needen for import mode and virtual_ds
		if (cep_active) {
			cepservice.Init(mailservice);
		}

		// sensor_list is needed for avg_calculation
		System.out.println("Get sensors...");
		sensor_list = rdfservice.GetSensors(false);

	}

	public void CheckForConfigurationFolder() {

		File file = new File("/etc/sensetrace/config.cfg");

		if (!file.exists()) {
			System.err.println("Program exits. Could not find config file!");
			System.exit(0);
		}

	}

	// Checks if the download folder is available and if the number of files is
	// correct
	public void CheckNumberOfCSVFiles() {

		try {
			int n_of_files = new File(conf.getProperty("dataloggerdlfolder"))
					.listFiles().length - 1;
			System.out.println("Number of files:" + n_of_files);
			if (n_of_files
					% Integer.parseInt(conf.getProperty("number_of_files")) != 0) {
				mailservice.Init(conf.getProperty("mail_from"),
						conf.getProperty("mail_to"),
						conf.getProperty("smtp_server"),
						conf.getProperty("smtp_port"),
						conf.getProperty("smtp_user"),
						conf.getProperty("smtp_pwd"),
						conf.getProperty("smtp_auth"),
						conf.getProperty("starttls"));
				mailservice.SendCVSFilesIncomplete();
				System.exit(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Can not reach download folder "
					+ conf.getProperty("dataloggerdlfolder"));
			mailservice.SendFolderNotAvailable();
			System.exit(0);
		}

	}

	public void SendMail() {
		mailservice.SendStatusMail();
	}

	/**
	 * This method controls the system
	 */
	public void start(boolean test, boolean downloadfromcsv,
			boolean downloadfromdatalogger_ftp,
			boolean downloadfromdatalogger_folder,
			boolean generate_v_data_stream) {

		TimeFormat timeformat = new TimeFormat();
		/* Some flags for controlling what the sofware should do */
		boolean markinvaliddata = false;
		boolean deletemark = false;
		deletemark = false;
		// boolean deleteclid = true;
		boolean deleteclid = false;
		// boolean calc_avgs = false;
		// calc avgs of the classifcations in the rule file
		// normally not needed
		// boolean calc_clavgs = false;
		// boolean generate_v_data_stream = true;
		boolean calc_clavgs = false;

		// für was ist das?
		// boolean addnewclassification = false;

		// boolean downloadfromcsv = false;
		// String[] timeintervall = { "2007-01-01 00:00:00",
		// "2008-01-01 00:00:00" };
		String[] clids_to_delete = { "2", "3" };
		// String range_window = "1day";
		// windows must be 1day or 1year.

		// String data_resolution[] = { "1sec","1min" };
		// String[] timeintervall = { "2007","2008" };
		if (timeintervall == null) {
			timeintervall = new String[] {
					"2006-04-22 00:00:00",
					timeformat.ConvertMillisecondsToSQLTime(System
							.currentTimeMillis()) };
		}

		// CheckForCompleteFiles();
		// mailservice.RegisterStaticProblem("sterror");
		// mailservice.RegisterCEPError("ceperror");
		// mailservice.SendStatusMail();
		// try {
		// Thread.sleep(1000000);
		// } catch (InterruptedException e2) {
		// // TODO Auto-generated catch block
		// e2.printStackTrace();
		// }

		// Remember calculated avgs for 2011, 2013
		// String[] sensors = { "14","15","17","22" };
		// String[] sensors = { "11","16","19","21","23","24" };

		// Remember calculated avgs for 2012, 2013
		// String[] sensors = {
		// "15","17","25","38","39","40","41","43","71","69","67","66","68"};

		// Remember calculated avgs for 2007, 2008
		// String[] sensors = {
		// "15","17","25","38","39","40","41","43","71","69","67","66","68",
		// "80","81","82","83","85","94","95","96","97","99"};
		// String[] sensors ={};// { "14","16","19","21","22","24","25" };
		// String[] sensors = {};// { "23","26","27","28","29","30" };
		// String[] sensors = {"42","44","98","100"};// {
		// "23","26","27","28","29","30" };
		// ***************
		// ***************************
		// String[] sensors = { "19","24" };
		// String[] sensors = {};
		// Nur zum löschen von clids

		// String data_resolution[] = { "1day" };
		ArrayList<String> sensor_list = new ArrayList<String>(
				Arrays.asList(sensors));
		ArrayList<String> clid_list = new ArrayList<String>(
				Arrays.asList(clids_to_delete));
		ArrayList<String> timeintervall_list = new ArrayList<String>(
				Arrays.asList(timeintervall));
		ArrayList<String> data_resolution_list = new ArrayList<String>(
				Arrays.asList(data_resolution));

		// Check for range_window and time_resolution
		if (range_window.equals("1year")
				&& (data_resolution_list.contains("1sec")
						|| data_resolution_list.contains("1min")
						|| data_resolution_list.contains("15min") || data_resolution_list
							.contains("1hour"))) {
			System.out
					.println("Exit: Cause window to big for choosen datastream resolution.");
			System.exit(0);
		}

		if (generate_v_data_stream
				&& (classify || errorcheck_static || errorcheck_dynamic)) {
			// Init CEP
			// "2007-01-01 00:00:00", "2008-01-01 00:00:00"
			// timeintervall[0]="2007-01-01 00:00:00";
			System.out.println(timeintervall[0]);
			System.out.println(timeintervall[1]);
			if (!timeintervall[0]
					.matches("((19|20)\\d\\d)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01]) "
							+ "[0-9]{2}:[0-9]{2}:[0-9]{2}")
					|| !timeintervall[1]
							.matches("((19|20)\\d\\d)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01]) "
									+ "[0-9]{2}:[0-9]{2}:[0-9]{2}")) {
				System.out
						.println("Invalid date intervall. Intervall has to be in the form"
								+ " from date1 to date2. Example:"
								+ " from 2007-01-01 00:00:00 to 2007-01-01 00:00:00");
				System.exit(0);
			}

		}
		// Check for range_window and time_resolution
		if (range_window.equals("1day")
				&& (/* data_resolution_list.contains("1day") || */data_resolution_list
						.contains("1month")))

		{
			System.out
					.println("Exit: Cause window bigger or equal as datastream resolution.");
			System.exit(0);
		}
		// Check the input data
		if (timeintervall.length != 2) {
			System.out
					.println("Timeintervall must contain timestamp from and to!");

		}

		/* Some invalid data have to be marked in a defined time interval */
		if (markinvaliddata) {

			// Check the sensorintervall
			if (sensors.length != 1) {
				System.out.println("You must choose one sensor!");
				System.exit(0);

			}

			pgsqlservice.AddCorrectedSensorToBatchErrorByUser(timeintervall[0],
					timeintervall[1], sensors[0], "NULL");

			// pgsqlservice.ExecuteErrorBatch();

			System.out.println("Finished!");
			System.exit(0);
		}

		/* Some invalid data have to be unmarked in a defined time interval */
		if (deletemark) {

			// Check the sensorintervall
			if (sensor_list.size() > 0) {
				pgsqlservice.DeleteFromErrorTableByUser(sensor_list,
						timeintervall[0], timeintervall[1]);
			} else {
				System.out.println("No sensor selected!");
			}
			System.out.println("Finished!");
			System.exit(0);
		}

		// Some clids have to be removed manual
		if (deleteclid) {
			if (clid_list.size() > 0) {
				if (timeintervall_list.size() < 2) {
					pgsqlservice.DeleteFromCLTable(clid_list);
				} else {
					pgsqlservice.DeleteFromCLTable(clid_list, timeintervall[0],
							timeintervall[1], false);
				}
			} else {
				System.out.println("No clids selected!");
				System.exit(0);
			}
			// Classificationsdurschnittswerte neu bestimmen!
			if (timeintervall_list.size() < 2) {
				pgsqlservice.CalulateClassfikationAvgs(clid_list, null, null);
			} else {
				pgsqlservice.CalulateClassfikationAvgs(clid_list,
						timeintervall[0], timeintervall[1]);
			}

			System.out.println("Finished!");
			System.exit(0);
		}

		// Now we start the Import from mysql to pgsql
		/*
		 * ImportSensorData importer = new
		 * ImportSensorData(mysqlservice,pgsqlservice,rdfservice,conf);
		 */
		// start the Import from Datalogger to pgsql and CEP-System

		// For importing from csv
		/*
		 * if (importfrom.equals("csv")) {
		 * 
		 * ImportSensorDataFromCSV importcsv= new
		 * ImportSensorDataFromCSV(cepservice,pgsqlservice, rdfservice, conf);
		 * System.out.println("Import from csv now..."); try {
		 * Thread.sleep(1000); } catch (InterruptedException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 * importcsv.StartImport(); }
		 */

		// Calc avgs defined in the rule-xml-file
		if (calc_clavgs) {
			pgsqlservice.CalulateClassfikationAvgs(rdfservice.GetCLIds(true),
					timeintervall[0], timeintervall[1]);
		}
		System.out
				.println("Before generate_v_data_stream.......................:"
						+ generate_v_data_stream);
		if (generate_v_data_stream
				&& (classify || errorcheck_static || errorcheck_dynamic)) {

			/*
			 * ImportSensorDataFromDatalogger importer = new
			 * ImportSensorDataFromDatalogger( cepservice, dlservice,
			 * pgsqlservice, rdfservice, conf);
			 */

			// Start Jtalis!
			//
			// if (classify) {
			//
			// // Register CEP Helper, Error and Classification rules
			// try {
			// Thread.sleep(2000);
			// } catch (InterruptedException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }

			if (errorcheck_dynamic) {
				if (!data_resolution_list.contains("1sec")
						&& check_if_avg_exists) {

					if (!(pgsqlservice.CheckIfAVGExists(
							rdfservice.GetErrorSensors("15min", true),
							timeintervall[0], timeintervall[1], sensor_list)
							&& pgsqlservice.CheckIfAVGExists(
									rdfservice.GetErrorSensors("1hour", true),
									timeintervall[0], timeintervall[1],
									sensor_list)
							&& pgsqlservice.CheckIfAVGExists(
									rdfservice.GetErrorSensors("1day", true),
									timeintervall[0], timeintervall[1],
									sensor_list)
							&& pgsqlservice.CheckIfAVGExists(
									rdfservice.GetErrorSensors("1month", true),
									timeintervall[0], timeintervall[1],
									sensor_list) && pgsqlservice
								.CheckIfAVGExists(rdfservice.GetErrorSensors(
										"1year", true), timeintervall[0],
										timeintervall[1], sensor_list))) {
						System.out
								.println("Can not generate sensordatastrem, because no avgs for sensor available. "
										+ "Attention: Start a one-second analyses before calculating "
										+ "averages. Press key to exit.");
						BufferedReader reader = new BufferedReader(
								new InputStreamReader(System.in));
						try {
							reader.readLine();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						// For string

						// String text= scan.nextLine();

						System.exit(0);
					}
				}

				cepservice.RegisterErrorRules(rdfservice.GetErrorRules(true));

			}

			cepservice.RegisterCEPHelperRules(rdfservice.GetHelperRules(true));
			System.out.println("Before classify.......................:");
			if (classify) {

				// check for double clids
				int n = 0;
				int m = 0;

				ArrayList<String> tmp = rdfservice.GetCLIds(false);
				while (n < tmp.size()) {
					m = n + 1;
					// System.out.println("n: " + tmp.get(n));
					while (m < tmp.size()) {
						// System.out.println("m: " + tmp.get(m));
						if (tmp.get(n).equals(tmp.get(m))) {
							// System.out
							// .println("Error. Double clid in rule file. clid: "
							// + tmp.get(n));
							System.exit(0);
						}
						m++;
					}
					n++;
				}

				if (!data_resolution_list.contains("1sec")
						&& check_if_avg_exists) {

					if (!(pgsqlservice.CheckIfAVGExists(
							rdfservice.GetClassificationSensors("15min", true),
							timeintervall[0], timeintervall[1], sensor_list)
							&& pgsqlservice.CheckIfAVGExists(rdfservice
									.GetClassificationSensors("1hour", true),
									timeintervall[0], timeintervall[1],
									sensor_list)
							&& pgsqlservice.CheckIfAVGExists(rdfservice
									.GetClassificationSensors("1day", true),
									timeintervall[0], timeintervall[1],
									sensor_list)
							&& pgsqlservice.CheckIfAVGExists(rdfservice
									.GetClassificationSensors("1month", true),
									timeintervall[0], timeintervall[1],
									sensor_list) && pgsqlservice
								.CheckIfAVGExists(
										rdfservice.GetClassificationSensors(
												"1year", true),
										timeintervall[0], timeintervall[1],
										sensor_list))) {
						System.out
								.println("Can not generate sensordatastrem, because no avgs for all needed sensors available. "
										+ "Attention: Start a one-second analyses before calculating "
										+ "averages. Press key to exit.");
						BufferedReader reader = new BufferedReader(
								new InputStreamReader(System.in));
						try {
							reader.readLine();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						System.exit(0);
					}
				}
				/*
				 * 
				 * if addnewclassification=true classifications have to be
				 * deleted in time intervall before reclassification else delete
				 * everything
				 */
				/*
				 * if (addnewclassification == true) {
				 * System.out.println("Delete clid, but just avgs!");
				 * pgsqlservice.DeleteFromCLTable(rdfservice.GetCLIds(true),
				 * timeintervall[0], timeintervall[1], true);
				 * 
				 * } else { System.out.println("Delete clid!");
				 * pgsqlservice.DeleteFromCLTable(rdfservice.GetCLIds(true),
				 * timeintervall[0], timeintervall[1], false); }
				 */

				// Clids aus Table löschen, die nicht mehr in der ceprule-file
				// stehen
				// unabhängig davon, ob active oder nicht.
				System.out.println("rdfservice.GetCLIds(false): "
						+ rdfservice.GetCLIds(false));
				pgsqlservice.DeleteClIDsNotInList(rdfservice.GetCLIds(false));
				cepservice.RegisterClRules(rdfservice.GetClRules(true));

			}
			System.out.println("After checks.......................:");
			// After checks and registration start the import
			if (classify || errorcheck_dynamic || errorcheck_static) {
				// check if we can generate datastream from avg values,
				// exit if not...

				// start the Import from Postgres and Send to CEP-System to
				// re-classify
				System.out.println("Sensor_list: " + sensor_list);
				ImportSensorDataFromPg importer = new ImportSensorDataFromPg(
						cepservice, pgsqlservice, rdfservice, conf, classify,
						errorcheck_static, errorcheck_dynamic, sensor_list,
						timeintervall);

				importer.StartImport(range_window, data_resolution_list);

				// Calv Avgs of the new classifications

				// Recalculate classification avgs
				// Classifikationsdurschnittswerte neu bestimmen!
				// Recalculate classification avgs
				// If queue not empty
				// System.out.println("cepservice.IsTaskOverAndQueueEmpty()"
				// + cepservice.IsTaskOverAndQueueEmpty());
				/*
				 * try { Thread.sleep(10000); } catch (InterruptedException e) {
				 * // TODO Auto-generated catch block e.printStackTrace(); }
				 */
				while (!cepservice.IsTaskOverAndQueueEmpty()) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// System.out.println("cepservice.IsTaskOverAndQueueEmpty()"

				}

				// Berechne Durschnittswerte der eben klassifizierten
				// Sensoren
				if (classify) {

					System.out
							.println("Delete and recalculate classification averages.");
					pgsqlservice.DeleteFromCLTable(rdfservice.GetCLIds(true),
							timeintervall[0], timeintervall[1], true);
					pgsqlservice.CalulateClassfikationAvgs(
							rdfservice.GetCLIds(true), timeintervall[0],
							timeintervall[1]);
				}
				/*
				 * if (errorcheck_static || errorcheck_dynamic) {
				 * System.out.println("Recalculate sensor averages.");
				 * CalcAvgsForErrorSensors(); }
				 */

			}
		}

		// Normaler importmodus vom Datenlogger über FTP
		if (downloadfromdatalogger_ftp) {
			System.out.println("Import from datalogger via ftp.");
			/*
			 * ImportSensorDataFromMySQL importer = new
			 * ImportSensorDataFromMySQL( mysqlservice, pgsqlservice,
			 * rdfservice, conf);
			 */
			// Jtalis sollte immer Initialisiert werden
			// cepservice.Init();
			// cepservice.SetRange("1sec");
			ImportDataloggerFTP importer = new ImportDataloggerFTP(cepservice,
					dlftpservice, pgsqlservice, rdfservice, conf);

			importer.StartImport("1sec");

		}
		// Normaler importmodus vom Datenlogger über ein Verzeichnis
		else if (downloadfromdatalogger_folder) {
			System.out.println("Import from datalogger folder.");

			/*
			 * ImportSensorDataFromMySQL importer = new
			 * ImportSensorDataFromMySQL( mysqlservice, pgsqlservice,
			 * rdfservice, conf);
			 */
			// Jtalis sollte immer Initialisiert werden
			// cepservice.Init();
			// cepservice.SetRange("1sec");
			ImportDataloggerFolder importer = new ImportDataloggerFolder(
					cepservice, pgsqlservice, rdfservice, dlcsvservice, conf);

			while (!importer.IsAllBatched()) {
				System.out.println("Import");
				importer.StartImport("1sec");
				lastimportdate = importer.RemoveOldestFilesToBatch();
			}
			// SetLastImportDate(lastimportdate);
			// System.out.println("lastimportdate:" + lastimportdate);
			// Check for Sensordata older 10 hours
			pgsqlservice.GetNotUpdatedSensors();
			String msg = "";
			while (pgsqlservice.GotoNextElement()) {
				String id = pgsqlservice.GetElement("sensorid");
				String name = rdfservice.ResolveSensor(id, true);
				// System.out.println("id: " + id);

				if (name != null) {
					// System.out.println("name: " + name);
					msg = msg + name + " (id=" + id + ")" + "<br>";
				}
				// System.out.println("Data older 10 hours: "
				// + pgsqlservice.GetElement("sensorid"));
			}
			System.out.println("msg: " + msg);
			mailservice.RegisterStaticProblem(msg);
			// mailservice.SendStatusMail();

		} else if (downloadfromcsv) {
			System.out.println("Import from csv file.");
			/*
			 * ImportSensorDataFromMySQL importer = new
			 * ImportSensorDataFromMySQL( mysqlservice, pgsqlservice,
			 * rdfservice, conf);
			 */
			// Jtalis sollte immer Initialisiert werden
			// cepservice.Init();
			// cepservice.SetRange("1sec");

			ImportSensorDataFromDelphineCSV importer = new ImportSensorDataFromDelphineCSV(
					cepservice, pgsqlservice, rdfservice, conf);
			importer.StartImport();

		} else if (test) {
			pgsqlservice.GetNotUpdatedSensors();
			String msg = "";
			while (pgsqlservice.GotoNextElement()) {
				String id = pgsqlservice.GetElement("sensorid");
				String name = rdfservice.ResolveSensor(id, true);
				// System.out.println("id: " + id);

				if (name != null) {
					// System.out.println("name: " + name);
					msg = msg + name + " (id=" + id + ")" + "<br>";
				}
				// System.out.println("Data older 10 hours: "
				// + pgsqlservice.GetElement("sensorid"));
			}
			System.out.println("msg: " + msg);
			mailservice.RegisterStaticProblem(msg);
		}

	}

	/*
	 * // Method will be used by DS to set the mysql service public synchronized
	 * void RegMySQLService(MySQLService service) {
	 * System.out.println("Register MySQLService"); mysqlservice = service; // I
	 * know I should not use the service here but just for demonstration //
	 * System.out.println(service.getQuote()); init();
	 * mysqlservice.CreateConnection(conf.getProperty("mysqlcs"),
	 * conf.getProperty("mysqluser"), conf.getProperty("mysqlpwd")); }
	 * 
	 * // Method will be used by DS to unset the mysql service public
	 * synchronized void UnregMySQLService(MySQLService service) {
	 * System.out.println("Unregister MySQLService"); if (mysqlservice ==
	 * service) { mysqlservice = null; } }
	 */

	// Method will be used by DS to set the mysql service
	public synchronized void RegPgService(PgService service) {
		System.out.println("Register PgSQLService");
		pgsqlservice = service;
		/**
		 * Tell pgqlservice to connect to db Read connection parameters from
		 * config file
		 */
		init_for_pg_bundle();
		pgsqlservice.CreateConnection(conf.getProperty("pgsqlcs"),
				conf.getProperty("pgsqluser"), conf.getProperty("pgsqlpwd"));
		// I know I should not use the service here but just for demonstration
		// System.out.println(service.getQuote());
	}

	// Method will be used by DS to unset the mysql service
	public synchronized void UnregPgService(PgService service) {
		System.out.println("Unregister PgSQLService");
		if (pgsqlservice == service) {
			pgsqlservice = null;
		}
	}

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
	public synchronized void RegDelphinFTPService(DelphinFTPService service) {
		System.out.println("Register DelphinFTPService");
		dlftpservice = service;
	}

	// Method will be used by DS to unregister the RDFDm service
	public synchronized void UnregDelphinFTPService(DelphinFTPService service) {
		System.out.println("Unregister DelphinFTPService");
		if (dlftpservice == service) {
			dlftpservice = null;
		}
	}

	// Method will be used by DS to set the RDFDm service
	public synchronized void RegDelphinCSVService(DelphinCSVService service) {
		System.out.println("Register DelphinCSVService");
		dlcsvservice = service;
	}

	// Method will be used by DS to unregister the RDFDm service
	public synchronized void UnregDelphinCSVService(DelphinCSVService service) {
		System.out.println("Unregister DelphinCSVService");
		if (dlcsvservice == service) {
			dlcsvservice = null;
		}
	}

	// Method will be used by DS to set the CEP service
	public synchronized void RegCEPService(CEPDatastreamAnalyzerService service) {
		System.out.println("Register CEPService");
		cepservice = service;
	}

	// Method will be used by DS to unregister the CEP service
	public synchronized void UnregCEPService(
			CEPDatastreamAnalyzerService service) {
		System.out.println("Unregister CEPService");
		if (cepservice == service) {
			cepservice = null;
		}
	}

	/*
	 * // Method will be used by DS to set the CEP service public synchronized
	 * void RegCEPHandlerService(CEPHandlerService service) {
	 * System.out.println("Register CEPHandlerService"); cephandlersrevice =
	 * service; }
	 * 
	 * // Method will be used by DS to unregister the CEP service public
	 * synchronized void UnregCEPHandlerService( CEPHandlerService service) {
	 * System.out.println("Unregister CEPHandlerService"); if (cephandlersrevice
	 * == service) { cephandlersrevice = null; } cephandlersrevice = service;
	 * cephandlersrevice.Init(cephandlersrevice); }
	 */
	// Method will be used by DS to set the Mail service
	public synchronized void RegMailService(MailService service) {
		System.out.println("Register MailService");
		mailservice = service;
	}

	// Method will be used by DS to unregister the Mail service
	public synchronized void UnregMailService(MailService service) {
		System.out.println("Unregister MailService");
		if (mailservice == service) {
			mailservice = null;
		}
	}

	// Method will be used by DS to unregister the Command service
	/*
	 * public synchronized void UnregCommandService(CommandProvider service) {
	 * System.out.println("Unregister CommandService"); if (commandservice ==
	 * service) { commandservice = null; } }
	 * 
	 * // Method will be used by DS to register the Command service public
	 * synchronized void RegCommandService(CommandProvider service) {
	 * System.out.println("Register CommandService"); commandservice = service;
	 * if (commandservice == null) {
	 * System.out.println("Error CommandService is null"); } }
	 */
}
