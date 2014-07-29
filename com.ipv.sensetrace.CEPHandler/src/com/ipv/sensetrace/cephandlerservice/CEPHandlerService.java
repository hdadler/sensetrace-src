package com.ipv.sensetrace.cephandlerservice;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.ipv.sensetrace.cephandlerservice.internal.Batch;
import com.ipv.sensetrace.cephandlerservice.internal.Handler;
import com.ipv.sensetrace.cephandlerservice.internal.TimeFormat;
import com.ipv.sensetrace.mailservice.MailService;
import com.ipv.sensetrace.pgsqlservice.PgService;
import com.ipv.sensetrace.rdfdmservice.RDFDmService;


public class CEPHandlerService implements ICEPHandlerService {
	/**
	 * Here we define our services binded by component declaration in OSGI-INF
	 */
	private RDFDmService rdfservice;
	private PgService pgsqlservice;
	TimeFormat timeformat = new TimeFormat();
	private ArrayList<String> CEPRuleClEvents = new ArrayList<String>();
	private ArrayList<String> CEPRuleErrorEvents = new ArrayList<String>();

	private MailService mailservice = null;

	public void HandleStaticCEPError(String sensorid, String from_s,
			String to_s, int add, String value, float oldvalue) {
		long from = timeformat.ConvertSQLTimeToTimestamp(from_s);
		long to = timeformat.ConvertSQLTimeToTimestamp(to_s) + add;
		System.out.println("From " + from_s + " to " + to_s
				+ " sensor with id " + sensorid + " is out of range.");
		// Send error to mailsservice
		mailservice.RegisterCEPError("From " + from_s + " to " + to_s
				+ " sensor '" 
				+rdfservice.ResolveSensor(sensorid, false)
				+"' (id=" + sensorid + ") is out of range. "
				+"The unreliable value is '" + oldvalue +"'."
				+" Write an entry to Errortable with new value '" +value+"'.");
		int n = 0;
		pgsqlservice.AddCorrectedSensorToBatchError(from, to,
				sensorid,  value);
		/*while (from <= to) {
			System.out.println("From: " + from_s+" to " + to_s);
			pgsqlservice.AddCorrectedSensorToBatchError2(
					timeformat.ConvertMillisecondsToSQLTime(from), sensorid,
					value);
 
			if (n > 10000) {
				pgsqlservice.ExecuteErrorBatch();
				n = 0;
			}
			n++;
			from = from + 1000;
		}


		pgsqlservice.ExecuteErrorBatch();*/

	}

	public void RegisterRules() {

		int n = 0;
		CEPRuleClEvents.clear();
		CEPRuleErrorEvents.clear();
		// Register names of classification rules, with id
		ArrayList<String> CEPRule = new ArrayList<String>();
		CEPRule = rdfservice.GetClRules(true);

		while (n < CEPRule.size()) {
			System.out.println("Init class. rule: " + CEPRule.get(n));
			String[] tokensname = CEPRule.get(n).split("\\(");
			String[] tokensid = CEPRule.get(n).split("\\:");
			// System.out.println("Init class. rule: " + tokensname[0]);
			CEPRuleClEvents.add(tokensname[0] + ":" + tokensid[2]);
			// System.out.println("Init class. rule: " + tokensname[0] + " Id:"
			// + tokensid[1]);
			n++;
		}
		// Register names of classification rules, with id
		n = 0;
		CEPRule = rdfservice.GetErrorRules(true);
		while (n < CEPRule.size()) {
			String[] tokensname = CEPRule.get(n).split("\\(");
			String[] tokensid = CEPRule.get(n).split("\\:");

			CEPRuleErrorEvents.add(tokensname[0] + ":" + tokensid[2]);
			System.out.println("Init error rule: " + tokensname[0] + " Id:"
					+ tokensid[2]);
			n++;
		}
	}

	// Method will be used by DS to set the mysql service
	public synchronized void RegPgService(PgService service) {
		System.out.println("Register PgSQLService");
		pgsqlservice = service;
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

	Handler handler = null;
	Batch batch = null;

	public boolean IsQueueEmpty() {
		return batch.IsQueueEmpty();
	}

	public void StartMsgHandler(RDFDmService rdfservice_ref,
			MailService mailserviceref) {
		mailservice = mailserviceref;
		rdfservice = rdfservice_ref;
		Queue<String> queue = new ConcurrentLinkedQueue<String>();
		// TODO Auto-generated method stub
		handler = new Handler(queue);
		batch = new Batch(queue, pgsqlservice, CEPRuleClEvents,
				CEPRuleErrorEvents, mailservice, rdfservice);
		// batch.Init(pgsqlservice, CEPRuleClEvents, CEPRuleErrorEvents);
		new Thread(batch).start();
		new Thread(handler).start();

	}

}
