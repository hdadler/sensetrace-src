package com.ipv.sensetrace.cepservice.internal;

import java.util.ArrayList;


import com.ipv.sensetrace.rdfdmservice.RDFDmService;
import com.jtalis.core.JtalisContextImpl;
import com.jtalis.core.event.EtalisEvent;
import com.jtalis.core.event.EventTimestamp;
import com.jtalis.core.event.ProviderSetupException;
import com.jtalis.core.event.provider.DefaultInputProvider;
import com.jtalis.core.event.provider.DefaultOutputProvider;
import com.jtalis.core.plengine.JPLEngineWrapper;
import com.jtalis.core.plengine.PrologEngineWrapper;

public class Jtalis {
	RDFDmService rdfservice;
	PrologEngineWrapper<?> engine;

	JtalisContextImpl ctx;
	// JtalisContextImpl ctx2;
	TimeFormat time = new TimeFormat();
	ArrayList<String[]> rules = new ArrayList<String[]>();

	public void RegisterRule(String rule) {
		ctx.addDynamicRule("c <- b(X) seq a(Y)");
	}

	public void Event(String event) {

	}

	public void Init(RDFDmService rdfservice_ref) {
		rdfservice = rdfservice_ref;
		//If you activate this Jtalis will output 
		//debug messages
		engine = new JPLEngineWrapper(false);

		ctx = new JtalisContextImpl(engine);

		ctx.registerInputProvider(new DefaultInputProvider(), 1000);
		//ctx.registerOutputProvider(new DefaultOutputProvider(), 1000);
		try {
			ctx.registerOutputProvider(new DefaultOutputProvider(""
					+ "127.0.0.1:8888"));
		} catch (ProviderSetupException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TestTimestampModification();
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// outputprovider.
		/*
		 * try { ctx.registerOutputProvider(new DefaultOutputProvider("" +
		 * "127.0.0.1:8888")); } catch (ProviderSetupException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */
		InitTriggersAndFlags();

	}

	//
	public void InitTriggersAndFlags() {
		// TestTimestampModification();
		ctx.setEtalisFlags("store_fired_events", "off");
		ctx.setEtalisFlags("out_of_order", "on");
		ctx.setEtalisFlags("store_fired_events_java","on");
		//ctx.setEtalisFlags("logging","on");
		ctx.setEtalisFlags("logging_to_file", "off");
		ctx.setEtalisFlags("garbage_clt", "off");
		ctx.setEtalisFlags("garbage_control", "off");
		// ctx.addEventTrigger("xyz");
	//	ctx.setEtalisFlags("windowsvalue", "1");
		ctx.setEtalisFlags("event_consumption_policy", "recent");
		// ctx.setEtalisFlags("event_consumption_policy", "unrestricted");
		// ctx.setEtalisFlags("event_consumption_policy", "chronological");
		// ctx.addEventTrigger("acspike(Time,Time,2)");
		// ctx.addEventTrigger("acspike");
		AddEventTriggers(rdfservice.GetErrorRules(true));
		AddEventTriggers(rdfservice.GetClRules(true));
		System.out
				.println("Total memory:" + Runtime.getRuntime().totalMemory());

		// ctx.waitForInputProviders();
		// ctx.shutdown();
	}

	// Delete stored events and results
	public void Reset() {
		// ctx.reset();
		ctx.getEngineWrapper().executeGoal("reset_etalis");
		InitTriggersAndFlags();
		RegisterRules();

	}

	public void AddEventTriggers(ArrayList<String> list) {

		// ArrayList<String> list = rdfservice.GetErrorRules();
		int n = 0;
		String[] token = null;
		while (n < list.size()) {
			System.out.println("list.get(n): " + list.get(n));
			token = list.get(n).split("<-");
			System.out.println("Set Event Trigger: " + token[0]);
			ctx.addEventTrigger(token[0]);
			n++;
		}
	}

	public void TestTimestampModification() {

		ctx.registerInputProvider(new DefaultInputProvider(), 1000);
		ctx.registerOutputProvider(new DefaultOutputProvider(), 1000);
		// PrologEngineWrapper<?> engine = new JPLEngineWrapper(true);

		// JtalisContextImpl ctx = new JtalisContextImpl(engine);
		ctx.setEtalisFlags("out_of_order", "on");
		ctx.addEventTrigger("_");
		ctx.addDynamicRule("high(T1,T2) <- a(X,T1,T2) where (X>1050)");
		ctx.addDynamicRule("high(T1,T4) <- high(T1,T2) meets a(X,T3,T4)   where (X>1050)");
		ctx.addDynamicRule("highfor20s(T1,T2) <- high(T1,T2) where (T2-T1>19)");
		/*
		 * final List<EtalisEvent> events = new LinkedList<EtalisEvent>();
		 * 
		 * ctx.registerOutputProvider(new AbstractJtalisEventProvider() {
		 * 
		 * @Override public void outputEvent(EtalisEvent event) {
		 * events.add(event); } });
		 */

		// This way I try to modify the timestamps.
		EventTimestamp e1 = new EventTimestamp(
				System.currentTimeMillis() / 1000 - 0, 0);
		EventTimestamp e2 = new EventTimestamp(
				System.currentTimeMillis() / 1000 + 10, 0);
		EtalisEvent eventa = new EtalisEvent("a", e1, e2, 1100,
				System.currentTimeMillis() / 1000,
				System.currentTimeMillis() / 1000 + 10);

		// I use "-10" because event b has happened in the past.
		EventTimestamp e3 = new EventTimestamp(
				System.currentTimeMillis() / 1000 + 10, 0);
		EventTimestamp e4 = new EventTimestamp(
				System.currentTimeMillis() / 1000 + 20, 0);
		EtalisEvent eventb = new EtalisEvent("a", e3, e4, 1100,
				System.currentTimeMillis() / 1000 + 10,
				System.currentTimeMillis() / 1000 + 20);

		ctx.pushEvent(eventa);
		/*
		 * try { Thread.sleep(1000); } catch (InterruptedException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */
		ctx.pushEvent(eventb);

		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ctx.shutdown();

		// Assert.assertEquals(3, events.size());

	}

	Boolean firstevent = false;
	EventTimestamp eold = null;
	// long timestampold = 0;
	float valuenextintervall;
	long timestampnextintervallbegin = 0;
	EventTimestamp enextintervallbegin = null;

	public void PushEvent(String range,String sensorid, String timestamp_s, float value) {
		// System.out.println(" sensorid: " + sensorid +" timestamp_s: " +
		// timestamp_s + " value: " +value);
		/*
		 * long timestamp_l = (time .ConvertSQLTimeToTimestamp(timestamp_s) /
		 * 1000); EtalisEvent eventa = new EtalisEvent("sensor", "Sensor" +
		 * sensorid, value,timestamp_l); eventa.setTimeStarts(new
		 * EventTimestamp(timestamp_l,0)); eventa.setTimeEnds(new
		 * EventTimestamp(timestamp_l,0)); ctx.pushEvent(eventa);
		 */

		long timestampthisintervallend = (time
				.ConvertSQLTimeToTimestamp(timestamp_s) / 1000) - 0;
		if (firstevent
				&& timestampnextintervallbegin <= timestampthisintervallend + 1) {
			// Ende dieses Intervalls

			EventTimestamp ethisintervallend = new EventTimestamp(
					timestampthisintervallend, 0);

			EtalisEvent eventa = new EtalisEvent("sensor", range+"_Sensor" + sensorid,
					valuenextintervall, timestampnextintervallbegin,
					timestampthisintervallend);
			eventa.setTimeStarts(enextintervallbegin);
			eventa.setTimeEnds(ethisintervallend);
			// eventa.setTimeEnds(enextintervallbegin);
			ctx.pushEvent(eventa);
		}
		// Anfang des nächsten Intervall
		timestampnextintervallbegin = timestampthisintervallend;
		enextintervallbegin = new EventTimestamp(timestampnextintervallbegin, 0);
		// Wert des nächsten intervall
		valuenextintervall = value;
		firstevent = true;
		/*
		 * try { Thread.sleep(1000); } catch (InterruptedException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */
		try {
			Thread.sleep(2);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	int n = 0;

	public void AddRule(String rule, String window) {

		// ctx.addDynamicRule("HighRadiation <- sensor(Sensor3,Value,Time) where Value>1000");
		if (rule != null) {
			System.out.println("window" + window);
			System.out.println("rule" + rule);
			rules.add(new String[] { rule, window });
		}
	}

	private void RegisterRules() {

		// ctx.addDynamicRule("HighRadiation <- sensor(Sensor3,Value,Time) where Value>1000");
		int n = 0;
		while (n < rules.size()) {
			System.out.println("addrule");
			if (!rules.get(n)[1].equals("0"))
			{
			System.out.println("Add Rule with ID: rule" + n + "([property(event_rule_window,"
					+ rules.get(n)[1] + ")])" + rules.get(n)[0]);
			ctx.addDynamicRuleWithId(
					"rule" + n + "([property(event_rule_window,"
							+ rules.get(n)[1] + ")])", rules.get(n)[0]);
			}
			else
			{
				System.out.println("Add Rule without id: " + rules.get(n)[0]);
				ctx.addDynamicRuleWithId(
						"unlabeled", rules.get(n)[0]);
				
			}
			
			n++;

		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
