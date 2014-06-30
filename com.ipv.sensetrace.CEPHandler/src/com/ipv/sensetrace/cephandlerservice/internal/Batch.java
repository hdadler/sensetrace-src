package com.ipv.sensetrace.cephandlerservice.internal;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ipv.sensetrace.mailservice.MailService;
import com.ipv.sensetrace.pgsqlservice.PgService;
import com.ipv.sensetrace.rdfdmservice.RDFDmService;

public class Batch implements Runnable {

	boolean taskover = true;
	PgService pgsqlservice = null;
	ArrayList<String> CEPRuleClEvents;
	ArrayList<String> CEPRuleErrorEvents;
	TimeFormat time = new TimeFormat();
	private final Queue<String> messageQueue;
	MailService mailservice = null;
	private RDFDmService rdfservice = null;

	// Mit Init zusammenlegen
	public Batch(Queue<String> messageQueue_r, PgService pgsqlserviceref,
			ArrayList<String> CEPRuleClEvents_ref,
			ArrayList<String> CEPRuleErrorEvents_ref,
			MailService mailserviceref, RDFDmService rdfserviceref) {
		this.messageQueue = messageQueue_r;
		CEPRuleClEvents = CEPRuleClEvents_ref;
		CEPRuleErrorEvents = CEPRuleErrorEvents_ref;
		pgsqlservice = pgsqlserviceref;
		mailservice = mailserviceref;
		rdfservice = rdfserviceref;
	}

	public boolean IsTaskOverAndQueueEmpty() {
		synchronized (messageQueue) {
			return (taskover && messageQueue.isEmpty());
		}

	}

	public void run() {

		System.out.println("Connect to Queue!");
		handling();
	}

	public void handling() {

		int n = 0;
		// Helps to improve speed, if event found in one list (e.g. errorlist)
		// don't search the other lists.
		boolean found = false;
		/*
		 * BufferedReader br = null; try { br = new BufferedReader(new
		 * InputStreamReader( socket.getInputStream())); } catch (IOException
		 * e1) { // TODO Auto-generated catch block e1.printStackTrace(); }
		 */
		String line;

		// Terminates, if Server shutd down
		// br.readLine() blocks till new line received

		while (true) {

			try {
				synchronized (messageQueue) {

					messageQueue.wait();

				}
			} catch (InterruptedException ex) {
				Logger.getLogger(Batch.class.getName()).log(Level.SEVERE, null,
						ex);
			}
			System.out.println("Set taskover:false");
			taskover = false;
			while (!messageQueue.isEmpty()) {

				line = messageQueue.poll();

				// /if (line!=null)

				// New message received
				// Write Info to DB!
				// Write To DB
				// eventHandler.HandleEvent(line);
				n = 0;
				found = false;
				System.out.println("Received Event from Queue: " + line);
				String[] nametokens = line.split("\\(|\\)");
				String[] valuetokens = null;
				// System.out.println("CEPRuleClEvents.size(): " +
				// CEPRuleClEvents.size());
				while (n < CEPRuleClEvents.size()) {
					/*
					 * System.out.println("n: "+n);
					 * System.out.println("CEPRuleClEvents.get(n): "
					 * +CEPRuleClEvents.get(n));
					 * System.out.println("nametokens[0]): " +nametokens[0]);
					 */

					if (CEPRuleClEvents.get(n).split(":")[0]
							.equals(nametokens[0])) {
						valuetokens = nametokens[1].split(", ");
						// Received an timeintervall
						/*
						 * if (nametokens[1].contains(",")) {
						 * System.out.println("Received Error-Rule: " +
						 * nametokens[0] + " for intervall: " + nametokens[1]);
						 * // Received an timestamp } else {
						 */

						// Split error rule to get sensorid
						String[] ruletoken = CEPRuleClEvents.get(n).split(":");
						System.out.println("Received Classification-Rule: "
								+ nametokens[0] + " for intervall,value: "
								+ nametokens[1] + " from: " + valuetokens[0]
								+ " to: " + valuetokens[1] + " Sensorid: "
								+ ruletoken[1]);
						WriteRuleToDb(valuetokens[0], valuetokens[1],
								ruletoken[1]);

						// }
						found = true;
						/*
						 * // Received an timeintervall if
						 * (nametokens[1].contains(",")) { System.out.println
						 * ("Received Classification-Rule: " + nametokens[0] +
						 * " for intervall: " + nametokens[1]); // Received an
						 * timestamp valuetokens = nametokens[1].split(", ");
						 * WriteRuleToDb(valuetokens[0], valuetokens[1],
						 * nametokens[0]); } else { System.out.println(
						 * "Received Classification-Rule: " + nametokens[0] +
						 * " for timestamp: " + nametokens[1]);
						 * WriteRuleToDb(nametokens[0], nametokens[1],
						 * nametokens[1]); } found = true;
						 */
					}
					n++;
				}
				if (found == false) {
					n = 0;
					while (n < CEPRuleErrorEvents.size()) {
						if (CEPRuleErrorEvents.get(n).split(":")[0]
								.equals(nametokens[0])) {
							valuetokens = nametokens[1].split(", ");
							// Received an timeintervall
							/*
							 * if (nametokens[1].contains(",")) { System.
							 * out.println("Received Error-Rule: " +
							 * nametokens[0] + " for intervall: " +
							 * nametokens[1]); // Received an timestamp } else {
							 */

							// Split error rule to get sensorid
							String[] ruletoken = CEPRuleErrorEvents.get(n)
									.split(":");
							System.out.println("Received Error-Rule: "
									+ nametokens[0] + " for intervall: "
									+ nametokens[1] + " from: "
									+ valuetokens[0] + " to: " + valuetokens[1]
									+ " value: " + valuetokens[2]
									+ " Sensorid: " + ruletoken[1]);
							//System.out.println("Write correction to db.");

							/*
							 * try { Thread.sleep(1000); } catch
							 * (InterruptedException e) { // TODO Auto-generated
							 * catch block // e.print }
							 */
							WriteCorrectionToDb(nametokens[0], valuetokens[0],
									valuetokens[1], ruletoken[1],
									valuetokens[2]);
						//	System.out.println("Written correction to db.");
							found = true;
						}
						n++;
					}

					if (found == false) {
						System.out.println("Received unknown Event: " + line);
					}

				}

			}
			//System.out.println("Set taskoverandqueueistempty:true");
			taskover = true;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		// eventHandler.CloseEventHandler();

	}

	public long ParseTimeString(String time_s) {
		long timefrom = 0;
		time_s = time_s.replace("'", "");

		if (time_s.contains("+")) {
			String[] nametokens = time_s.split("\\+");
			timefrom = Integer.parseInt(nametokens[0]);
			timefrom = timefrom + Integer.parseInt(nametokens[1]);
		} else if (time_s.contains("-")) {
			String[] nametokens = time_s.split("\\-");
			timefrom = Integer.parseInt(nametokens[0]);
			timefrom = timefrom - Integer.parseInt(nametokens[1]);
		} else {

			timefrom = Integer.parseInt(time_s);
		}
		return timefrom;
	}

	public void WriteCorrectionToDb(String rulename, String timefrom_s,
			String timeto_s, String sensorid, String value) {
		value = value.replace("'", "");
		// System.out.println("timefrom: " + timefrom_s);
		// System.out.println("timeto: " + timeto_s);
		long timefrom = ParseTimeString(timefrom_s);
		long timeto = ParseTimeString(timeto_s);
		mailservice.RegisterCEPError("Detected pattern '" + rulename
				+ "' from "
				+ time.ConvertMillisecondsToSQLTime(timefrom * 1000) + " to "
				+ time.ConvertMillisecondsToSQLTime(timeto * 1000)
				+ ". For this intervall new value '" + value
				+ "' will be applied to sensor '"
				+ rdfservice.ResolveSensor(sensorid, false) + "' (id="
				+ sensorid + ").");
		if (value.equals("interpolate")) {
			try {
				Object startvalue_o = pgsqlservice.GetData(
						time.ConvertMillisecondsToSQLTime(timefrom * 1000),
						sensorid, true);
				Object endvalue_o = pgsqlservice.GetData(
						time.ConvertMillisecondsToSQLTime(timeto * 1000),
						sensorid, true);
				// Catch values before and after interval

				if ((startvalue_o != null) && (endvalue_o != null)) {

					float startvalue = (float) startvalue_o;
					float endvalue = (float) endvalue_o;
					// Calculate step
					long interval_length = timeto - timefrom;
					float value_diff = endvalue - startvalue;
					float step = value_diff / interval_length;
					float currentvalue = startvalue;
					// System.out.println("start: " + startvalue);
					// System.out.println("end: " + endvalue);
					// System.out.println("Correction of sensor with id " +
					// sensorid
					// + " from time " + timefrom_s + " to " + timeto_s);
					// System.out.println("Writing value " + value);
					// System.out.println("Writing value " + currentvalue +
					// "ts:"
					// + time.ConvertMillisecondsToSQLTime(timefrom * 1000));
					// register error in mailservice
					/*pgsqlservice.DeleteFromErrorJtalis(timefrom, timeto,
							sensorid);*/
					while (timefrom <= timeto) {

						currentvalue = currentvalue + step;
						pgsqlservice.AddCorrectedSensorToJtalisBatch(
								timefrom * 1000,
								timefrom * 1000,sensorid, Float.toString(currentvalue));
					/*	pgsqlservice.AddCorrectedSensorToJtalisBatch(time
								.ConvertMillisecondsToSQLTime(timefrom * 1000),
								sensorid, String.valueOf(currentvalue));*/
						timefrom = timefrom + 1;

					}
					/*pgsqlservice.AddCorrectedSensorToJtalisBatch(
							timefrom * 1000,
							timeto * 1000,sensorid, value);*/
				} else {
				/*	pgsqlservice.DeleteFromErrorJtalis(timefrom, timeto,
							sensorid);*/
					/*while (timefrom <= timeto) {

						pgsqlservice.AddCorrectedSensorToJtalisBatch(time
								.ConvertMillisecondsToSQLTime(timefrom * 1000),
								sensorid, null);
						timefrom = timefrom + 1;

					}*/
					pgsqlservice.AddCorrectedSensorToJtalisBatch(
							timefrom * 1000,
							timeto * 1000,sensorid, null);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {

			System.out.println("Correction of sensor with id " + sensorid
					+ " from time " + timefrom_s + " to " + timeto_s);
			System.out.println("Writing value " + value);
			//int n = 0;
			pgsqlservice.AddCorrectedSensorToJtalisBatch(
					timefrom * 1000,
					timeto * 1000,sensorid, value);
			//pgsqlservice.DeleteFromErrorJtalis(timefrom, timeto, sensorid);
			/*while (timefrom <= timeto) {

				pgsqlservice.AddCorrectedSensorToJtalisBatch(
						time.ConvertMillisecondsToSQLTime(timefrom * 1000),
						sensorid, value);
				timefrom = timefrom + 1;
				if (n > 1000) {
					pgsqlservice.ExecuteJtalisBatch();
					n = 0;
				}
				n++;
			}*/

			// System.out.println("before  pgsqlservice.ExecuteJtalisBatch()");
		
			// System.out.println("after  pgsqlservice.ExecuteJtalisBatch()");
		}
		//pgsqlservice.ExecuteJtalisBatch();
	}

	public void WriteRuleToDb(String timefrom_s, String timeto_s, String clid) {

		long timeto = ParseTimeString(timeto_s);
		long timefrom = ParseTimeString(timefrom_s);
		System.out.println("Classification with id " + clid + " from time "
				+ timefrom_s + " to " + timeto_s);

		System.out.println("timefrom: "
				+ time.ConvertMillisecondsToSQLTime(timefrom * 1000));
		System.out.println("timeto: "
				+ time.ConvertMillisecondsToSQLTime(timeto * 1000));

		// int n = 0;

		pgsqlservice.AddClRuleToBatch(timefrom, timeto, clid);
		/*
		 * while (timefrom <= timeto) {
		 * 
		 * pgsqlservice.AddClRuleToBatch(
		 * time.ConvertMillisecondsToSQLTime(timefrom * 1000), clid); timefrom =
		 * timefrom + 1;
		 * 
		 * if (n > 1000) { pgsqlservice.ExecuteJtalisBatch(); n = 0; } n++; }
		 * pgsqlservice.ExecuteJtalisBatch();
		 */
	}

}