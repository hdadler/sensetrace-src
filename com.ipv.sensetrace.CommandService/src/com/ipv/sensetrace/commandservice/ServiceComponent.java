package com.ipv.sensetrace.commandservice;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

import com.ipv.sensetrace.controllingservice.ControlService;

// referenced in component.xml
public class ServiceComponent implements CommandProvider {

	Timer timer = new Timer();
	Lock filelock = new Lock();
	ControlService controlservice;

	// private DictionaryService dictionary;

	public void _calc_avgs(CommandInterpreter ci) {
		timer.SetKillAfterOneHour(false);
		System.out.println("Init configuration and sensor-xml-files.");
		controlservice.Init(false);
		System.out.println("Calculate averages.");
		String from = ci.nextArgument();
		String date_from = ci.nextArgument();
		date_from = date_from + " " + ci.nextArgument();
		String to = ci.nextArgument();
		String date_to = ci.nextArgument();
		date_to = date_to + " " + ci.nextArgument();
		String sensor_str = null;
		;
		String sensors[] = {};
		System.out.println("Date_From: " + date_from);
		System.out.println("Date_To: " + date_to);
		// Do some checks
		if (!from.equals("from")) {
			return;
		} else if (!to.equals("to")) {
			return;
		} else {

			System.out.println("Date_From: " + date_from);
			System.out.println("Date_To: " + date_to);
			controlservice.SetDate(date_from, date_to);
			sensor_str = ci.nextArgument();
			if (sensor_str.equals("auto")) {
				controlservice.CalcAvgsForGivenSensors(true);
			} else {

				if (sensor_str != null) {
					sensors = sensor_str.split(",");
				}
				controlservice.SetSensors(sensors);
				controlservice.CalcAvgsForGivenSensors(false);
			}
			System.out.println("Exit System!");
			System.exit(0);
		}
	}

	public void _import_from(CommandInterpreter ci) {
		// Only one instance of sensetrace allowed, when importing data or
		// creating
		// virtual ds. To do this lock file...
		if (filelock.LockFile()) {
		} else {
			System.out.println("File allready locked. Exit Programm");
			System.exit(0);
		}
		timer.SetKillAfterOneHour(false);
		boolean downloadfromcsv = false;
		boolean downloadfromdatalogger_ftp = true;
		boolean downloadfromdatalogger_folder = false;
		boolean generate_v_data_stream = false;
		/*
		 * controlservice.CalcAvgsForErrorSensors(); try { Thread.sleep(20000);
		 * } catch (InterruptedException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */
		String arg = ci.nextArgument();
		String word = ci.nextArgument();
		if (arg == null) {
			return;
		}
		if (arg.equalsIgnoreCase("dl")
				&& (word.equals("ftp") || word.equals("folder"))) {
			controlservice.SetTimeIntervallFromLastImportTillNow();
			// Disable avg check, else program would hang if sensor fails
			controlservice.Setcheck_if_avg_exists(false);
			// first initialize cep
			controlservice.SetLastImportDateToNull();
			controlservice.Init(true);

			if (word.equals("ftp")) {
				downloadfromdatalogger_ftp = true;
				downloadfromdatalogger_folder = false;
			}
			if (word.equals("folder")) {
				downloadfromdatalogger_ftp = false;
				downloadfromdatalogger_folder = true;
				// Check if the count of csv files matches
				controlservice.CheckNumberOfCSVFiles();

			}
			generate_v_data_stream = false;
			downloadfromcsv = false;

			// First import the data
			controlservice.start(false, downloadfromcsv,
					downloadfromdatalogger_ftp, downloadfromdatalogger_folder,
					generate_v_data_stream);
			// Generate a sensor datastream from lastimport to now

			downloadfromcsv = false;
			downloadfromdatalogger_ftp = false;
			downloadfromdatalogger_folder = false;
			generate_v_data_stream = true;
			//controlservice.SetTimeIntervallFromLastImportTillNow();
			// At first classification and errorcheck for 1 second data
			// in one day window
			// Average is calculated automatically
			boolean errorcheck_static = true;
			boolean errorcheck_dynamic = true;
			boolean classify = true;
			System.out
					.println("1) classification and errorcheck in one day intervall of one second data.");
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// controlservice.SetTimeIntervallFromLastImportTillNow();
			// System.out.println("SetVirtualDsOptions");
			controlservice.SetVirtualDsOptions(classify, errorcheck_dynamic,
					errorcheck_static);
			controlservice.SetWindowAndResolution("1day",
					new String[] { "1sec" });
			// System.out.println("Start import");
			// controlservice.DeleteFromCLTable(true);
			controlservice.start(false, downloadfromcsv,
					downloadfromdatalogger_ftp, downloadfromdatalogger_folder,
					generate_v_data_stream);
			// Averages for errorsensors are calculated automatically, now
			// calculate avgs of other sensors also
			// controlservice.CalcAvgsForGivenSensors();
			System.out
					.println("2) classification and errorcheck in one day intervall");
			controlservice.CalcAvgsForAllSensors();
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// At second classification and errorcheck in one day interval
			// in one day window
			errorcheck_static = false;
			errorcheck_dynamic = true;
			classify = true;
			//controlservice.SetTimeIntervallFromLastImportTillNow();
			controlservice.SetVirtualDsOptions(classify, errorcheck_dynamic,
					errorcheck_static);
			controlservice.SetWindowAndResolution("1day", new String[] {
					"1min", "1h", "15min" });
			controlservice.start(false, downloadfromcsv,
					downloadfromdatalogger_ftp, downloadfromdatalogger_folder,
					generate_v_data_stream);
			System.out
					.println("3)classification and errorcheck in one day intervall in year timewindow");
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// At third classification and errorcheck from
			// begin of month till now in year timewindow
			errorcheck_static = false;
			errorcheck_dynamic = true;
			classify = true;
			controlservice
					.SetTimeIntervallFromTwoMonthBeforeLastImportTillNow();
			controlservice.SetVirtualDsOptions(classify, errorcheck_dynamic,
					errorcheck_static);
			controlservice.SetWindowAndResolution("1year", new String[] {
					"1day", "1month" });
			controlservice.start(false, downloadfromcsv,
					downloadfromdatalogger_ftp, downloadfromdatalogger_folder,
					generate_v_data_stream);
			controlservice.CalcAvgsForErrorSensors();
			controlservice.SetLastImportDate();
			controlservice.SendMail();
			System.exit(0);

		} else if (arg.equalsIgnoreCase("dl") && word.equals("test")) {

			// First check if the count of csv files matches
			controlservice.CheckNumberOfCSVFiles();

			// first initialize cep
			controlservice.SetLastImportDateToNull();
			controlservice.Init(true);

			// controlservice.CalcAvgsWithoutErrorSensors();
			/*
			 * controlservice.CalcAvgsWithoutErrorSensors(); try {
			 * Thread.sleep(10000); } catch (InterruptedException e) { // TODO
			 * Auto-generated catch block e.printStackTrace(); }
			 */
			// System.out.println("Begin!!!!!!!!!!!!!!!!!!!!!!!!!!!1.");
			downloadfromcsv = false;
			downloadfromdatalogger_ftp = false;
			downloadfromdatalogger_folder = true;
			generate_v_data_stream = false;

			// First import the data
			// controlservice.start(downloadfromcsv, downloadfromdatalogger_ftp,
			// downloadfromdatalogger_folder, generate_v_data_stream);
			// Generate a sensor datastream from lastimport to now

			downloadfromcsv = false;
			downloadfromdatalogger_ftp = false;
			downloadfromdatalogger_folder = false;
			generate_v_data_stream = true;
			controlservice.SetTestTimeIntervallFromLastImportTillNow();

			System.out
					.println("1) classification and errorcheck in one day intervall of one second data.");
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// At first classification and errorcheck for 1 second data
			// in one day window
			// Average is calculated automatically
			boolean errorcheck_static = true;
			boolean errorcheck_dynamic = true;
			boolean classify = true;
			// controlservice.SetTimeIntervallFromLastImportTillNow();
			// System.out.println("SetVirtualDsOptions");
			controlservice.SetVirtualDsOptions(classify, errorcheck_dynamic,
					errorcheck_static);
			controlservice.SetWindowAndResolution("1day",
					new String[] { "1sec" });
			// System.out.println("Start import");
			controlservice.start(true, downloadfromcsv,
					downloadfromdatalogger_ftp, downloadfromdatalogger_folder,
					generate_v_data_stream);

			System.out
					.println("2) classification and errorcheck in one day intervall");
			controlservice.CalcAvgsForAllSensors();
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// Averages for errorsensors are calculated automatically, now
			// calculate avgs of other sensors also
			// controlservice.CalcAvgsForGivenSensors();
			/*
			 * System.out.println(
			 * "2) classification and errorcheck in one day intervall");
			 * controlservice.CalcAvgsWithoutErrorSensors(); try {
			 * Thread.sleep(10000); } catch (InterruptedException e) { // TODO
			 * Auto-generated catch block e.printStackTrace(); }
			 * 
			 * 
			 * //At second classification and errorcheck in one day interval
			 * //in one day window errorcheck_static = false; errorcheck_dynamic
			 * = true; classify = true;
			 * controlservice.SetTimeIntervallFromLastImportTillNow();
			 * controlservice.SetVirtualDsOptions(classify, errorcheck_dynamic,
			 * errorcheck_static); controlservice.SetWindowAndResolution("1day",
			 * new String[] { "1min","1h","15min" });
			 * controlservice.start(downloadfromcsv, downloadfromdatalogger_ftp,
			 * downloadfromdatalogger_folder, generate_v_data_stream);
			 * System.out.println(
			 * "3)classification and errorcheck in one day intervall in year timewindow"
			 * ); try { Thread.sleep(5000); } catch (InterruptedException e) {
			 * // TODO Auto-generated catch block e.printStackTrace(); }
			 * 
			 * //At third classification and errorcheck from //begin of month
			 * till now in year timewindow errorcheck_static = false;
			 * errorcheck_dynamic = true; classify = true;
			 * controlservice.SetTimeIntervallFromTwoMonthBeforeLastImportTillNow
			 * (); controlservice.SetVirtualDsOptions(classify,
			 * errorcheck_dynamic, errorcheck_static);
			 * controlservice.SetWindowAndResolution("1year", new String[] {
			 * "1day","1month" }); controlservice.start(downloadfromcsv,
			 * downloadfromdatalogger_ftp, downloadfromdatalogger_folder,
			 * generate_v_data_stream);
			 */
			controlservice.CalcAvgsForErrorSensors();
			controlservice.SetLastImportDate();
			controlservice.SendMail();
			System.exit(0);

		} else if (arg.equalsIgnoreCase("csv")) {
			downloadfromcsv = true;
			downloadfromdatalogger_ftp = false;
			downloadfromdatalogger_folder = false;
			generate_v_data_stream = false;
			controlservice.start(false, downloadfromcsv,
					downloadfromdatalogger_ftp, downloadfromdatalogger_folder,
					generate_v_data_stream);
		}
		if (arg.equalsIgnoreCase("languages")) {

		}
	}

	public void _virtual_ds(CommandInterpreter ci) {
		// Only one instance of sensetrace allowed, when importing data or
		// creating
		// virtual ds. To do this lock file...
		if (filelock.LockFile()) {
		} else {
			System.out.println("File allready locked. Exit Programm");
			System.exit(0);
		}
		timer.SetKillAfterOneHour(false);
		boolean downloadfromcsv = false;
		boolean downloadfromdatalogger_ftp = false;
		boolean downloadfromdatalogger_folder = false;
		boolean generate_v_data_stream = true;
		boolean errorcheck_dynamic = false;
		boolean errorcheck_static = false;
		boolean classify = false;

		// String command = ci.nextArgument();
		String from = ci.nextArgument();
		String date_from = ci.nextArgument();
		date_from = date_from + " " + ci.nextArgument();
		String to = ci.nextArgument();
		String date_to = ci.nextArgument();
		date_to = date_to + " " + ci.nextArgument();
		String sensor_str = null;
		String[] sensors = null;
		String command = ci.nextArgument();
		// command = command + ci.nextArgument(); // command = command +
		// ci.nextArgument();

		// Do some checks
		if (!from.equals("from")) {
			return;
		} else if (!to.equals("to")) {
			return;
		} else {
			sensor_str = ci.nextArgument();
			if (sensor_str != null) {
				sensors = sensor_str.split(",");
				controlservice.SetSensors(sensors);
			}

			controlservice.Init(true);
			controlservice.SetDate(date_from, date_to);
			//System.out.print("command: " + command);
			if (command.contains("errorcheck_dynamic")) {
				errorcheck_dynamic = true;
			}
			if (command.contains("errorcheck_static")) {
				errorcheck_static = true;
			}
			if (command.contains("errorcheck_static_and_dynamic")) {
				//System.out.print("command found: " + command);
				errorcheck_static = true;
				errorcheck_dynamic = true;
			}
			if (command.contains("classify")) {
				System.out.println("Delete old classifications in intervall!");
				controlservice.DeleteFromCLTable(false);
				classify = true;
			}

			System.out
					.println("1) classification and/or errorcheck in one day intervall of one second data.");
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// controlservice.SetTimeIntervallFromLastImportTillNow();
			// System.out.println("SetVirtualDsOptions");
			controlservice.SetVirtualDsOptions(classify, errorcheck_dynamic,
					errorcheck_static);
			controlservice.SetWindowAndResolution("1day",
					new String[] { "1sec" });
			System.out.println("Start import");
			System.out.println("errorcheck_dynamic" + errorcheck_dynamic);
			System.out.println("errorcheck_static" + errorcheck_static);
			controlservice.start(false, downloadfromcsv,
					downloadfromdatalogger_ftp, downloadfromdatalogger_folder,
					generate_v_data_stream);

			// Averages for errorsensors are calculated automatically, now
			// calculate avgs of other sensors also
			// controlservice.CalcAvgsForGivenSensors();
			System.out
					.println("2) Classification and/or errorcheck in one day intervall");
			// controlservice.CalcAvgsWithoutErrorSensors();
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// At second classification and errorcheck in one day interval
			// in one day window

			// errorcheck_dynamic = true;
			// classify = true;
			controlservice.SetVirtualDsOptions(classify, errorcheck_dynamic,
					false);
			controlservice.SetWindowAndResolution("1day", new String[] {
					"1min", "1h", "15min", "1day" });
			controlservice.start(false, downloadfromcsv,
					downloadfromdatalogger_ftp, downloadfromdatalogger_folder,
					generate_v_data_stream);
			System.out
					.println("3) Classification and/or errorcheck in one day intervall in year timewindow");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// At third classification and errorcheck from
			// begin of month till now in year timewindow
			controlservice.SetVirtualDsOptions(classify, errorcheck_dynamic,
					false);
			controlservice.SetWindowAndResolution("1year", new String[] {
					"1day", "1month" });

			controlservice.start(false, downloadfromcsv,
					downloadfromdatalogger_ftp, downloadfromdatalogger_folder,
					generate_v_data_stream);

			// This is important, because values of sensors could have changed
			// controlservice.CalcAvgsForErrorSensors();

			/******************
			 * + downloadfromcsv = false; downloadfromdatalogger_ftp = false;
			 * downloadfromdatalogger_folder = false; generate_v_data_stream =
			 * true; calc_avgs = false; controlservice.SetDate(date_from,
			 * date_to); controlservice.SetVirtualDsOptions(classify,
			 * errorcheck_dynamic, errorcheck_static);
			 * controlservice.start(false, downloadfromcsv,
			 * downloadfromdatalogger_ftp, downloadfromdatalogger_folder,
			 * generate_v_data_stream);
			 */
			// ////////////////
			System.exit(0);

		}
	}

	public String getHelp() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("---Generated by PDE---\n");
		buffer.append("\tdict check <word> - check for the existence of a word\n");
		buffer.append("\tdict languages - list the languages available\n");
		return buffer.toString();
	}

	// Method will be used by DS to unregister the Command service
	public synchronized void UnbindControlService(ControlService service) {
		System.out.println("Unregister ControlService");
		if (controlservice == service) {
			controlservice = null;
		}

	}

	// Method will be used by DS to register the Command service
	public synchronized void BindControlService(ControlService service) {
		System.out.println("Register ControlService");
		controlservice = service;

		// Timer thread starts counting, after one hour it will kill the task
		timer.start();
	}
}