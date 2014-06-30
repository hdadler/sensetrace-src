package com.ipv.sensetrace.rdfdmservice.internal;


import java.util.ArrayList;

import com.hp.hpl.jena.query.*;

import com.hp.hpl.jena.rdf.model.Model;


public class RDFResultCEPRules {
	ResultSet results;
	Query query;
	QueryExecution qe;

	// static Model tmpmodel=null;

	public void QueryClRules(Model model, boolean active) {
		String active_str="";
		if (active)
		{
			active_str="?o dc:active \"true\". ";
		}
		// Der RDF-Store wird abgefragt mit einem SPARQL-Befehl um mysqlid,
		// postresid,
		// lowerlimit und upperlimit zu ermitteln. Die Antwort erhalten wir als
		String queryString = "PREFIX dc:      <http://purl.org/dc/elements/1.1/> "
				+ "PREFIX rdf:        <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
				+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> "
				+ "PREFIX time: <http://www.w3.org/2006/time#> "
				+ "SELECT  ?rule ?clid ?win "
				+ "WHERE { "
				+ "?o dc:ceprule ?rule. "
				+ active_str
				+ "?o dc:window ?win. "
				+ "?o dc:type \"classify\". "
				+ "?o dc:clid ?clid." + "}";
		// /System.out.println("QueryString: " + queryString + "N-TRIPLE");
		// model.write(System.out, "N-TRIPLE");
		qe = QueryExecutionFactory.create(queryString, model);
		results = qe.execSelect();
	}


	
	public void QueryErrorRules(Model model, boolean active) {
		String active_str="";
		if (active)
		{
			active_str="?o dc:active \"true\". ";
		}

		// Der RDF-Store wird abgefragt mit einem SPARQL-Befehl um mysqlid,
		// postresid,
		// lowerlimit und upperlimit zu ermitteln. Die Antwort erhalten wir als
		String queryString = "PREFIX dc:      <http://purl.org/dc/elements/1.1/> "
				+ "PREFIX rdf:        <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
				+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> "
				+ "PREFIX time: <http://www.w3.org/2006/time#> "
				+ "SELECT   ?rule ?clid ?win "
				+ "WHERE { "
				+ "?o dc:ceprule ?rule. "
				+ active_str
				+ "?o dc:window ?win. "
				+ "?o dc:type \"error\". "
				+ "?o dc:replacesensor ?clid." + "}";
		 //System.out.println("QueryString: " + queryString + "N-TRIPLE");
		// model.write(System.out, "N-TRIPLE");
		qe = QueryExecutionFactory.create(queryString, model);
		results = qe.execSelect();
	}

	public void QueryHelperRules(Model model, boolean active) {
		String active_str="";
		if (active)
		{
			active_str="?o dc:active \"true\". ";
		}

		// Der RDF-Store wird abgefragt mit einem SPARQL-Befehl um mysqlid,
		// postresid,
		// lowerlimit und upperlimit zu ermitteln. Die Antwort erhalten wir als
		String queryString = "PREFIX dc:      <http://purl.org/dc/elements/1.1/> "
				+ "PREFIX rdf:        <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
				+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> "
				+ "PREFIX time: <http://www.w3.org/2006/time#> "
				+ "SELECT  ?rule ?clid ?win "
				+ "WHERE { "
				+ "?o dc:ceprule ?rule. "
				+ active_str 
				+ "?o dc:window ?win. " + "?o dc:type \"helper\". " + "}";
		// System.out.println("QueryString: " + queryString + "N-TRIPLE");
		// model.write(System.out, "N-TRIPLE");
		qe = QueryExecutionFactory.create(queryString, model);
		results = qe.execSelect();
	}

	public ArrayList<String> GetResult() {
		String result_s;
		String window;
		ArrayList<String> resultlist = new ArrayList<String>();
		while (results.hasNext()) {
			QuerySolution row = results.next();
			if (row.getLiteral("win") != null) {
				window = row.getLiteral("win").getString();
			} else {
				window = " ";
			}
			// RDFNode thing= row.get("rule");
			result_s = row.getLiteral("rule").getString() + ":" + window;
			// System.out.println(result_s);
			// If no helper rule
			if (row.getLiteral("clid") != null) {
				result_s = result_s + ":" + row.getLiteral("clid").getString();
			}

			resultlist.add(result_s);
			//System.out.println("rule_s: " + result_s);

		}
		return resultlist;
	}

	public ArrayList<String> GetIDList() {
		String result_s;
		ArrayList<String> resultlist = new ArrayList<String>();
		while (results.hasNext()) {
			
			// RDFNode thing= row.get("rule");
			QuerySolution row = results.next();
			result_s = row.getLiteral("clid").getString();
			resultlist.add(result_s);
			
			// System.out.println("rule_s: " + resultlist.get(0));

		}
		return resultlist;
	}

	public ArrayList<String> GetClassificationSensors(Model model, String sensorkind, boolean active) {
		String result_s = null;
		ArrayList<String> resultlist = new ArrayList<String>();
		String active_str="";
		if (active)
		{
			active_str="?o dc:active \"true\". ";
		}
		String queryString = "PREFIX dc:      <http://purl.org/dc/elements/1.1/> "
				+ "PREFIX rdf:        <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
				+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> "
				+ "PREFIX time: <http://www.w3.org/2006/time#> "
				+ "SELECT  ?rule "
				+ "WHERE { "
				+ active_str
				+ "?o dc:type ?type. "
				+ "?o dc:ceprule ?rule. " + " " +
						"Filter(?type=(\"classify\") ||" +
						"?type=(\"helper\" ))}";
		 System.out.println("QueryString: " + queryString + "N-TRIPLE");
		// model.write(System.out, "N-TRIPLE");
		qe = QueryExecutionFactory.create(queryString, model);
		results = qe.execSelect();
		
		 System.out.println("active_str: " +active_str);
		while (results.hasNext()) {
			QuerySolution row = results.next();
			// RDFNode thing= row.get("rule");
			result_s = row.getLiteral("rule").getString();

			// If helper rule
			if (row.getLiteral("clid") != null) {
				result_s = result_s + ":" + row.getLiteral("clid").getString();
			}

	
			String phrase = result_s;
			String[] tokens = phrase.split(sensorkind+"_Sensor");
			int n = 1;
			while (n < tokens.length) {
				// Trennzeichen bei allen Zeichen außer Zahl
				String[] tokens2 = tokens[n].split("[^0-9]");

				// Zur Liste hinzufügen
				if (!resultlist.contains(tokens2[0])) {
					resultlist.add(tokens2[0]);
					 System.out.println(tokens2[0] + " ");
				}

				n++;
			}
		}

		return resultlist;
	}

	public String ResolveSensorID(Model model, String id, boolean active)
	{
		ResultSet results;
		QueryExecution qe;
		String active_str="";
		if (active)
		{
			active_str="?name dc:active \"true\". ";
		}
		
		String result_s = null;
		//ArrayList<String> resultlist = new ArrayList<String>();
		String queryString = "PREFIX dc:      <http://purl.org/dc/elements/1.1/> "
				+ "SELECT  ?name "
				+ "WHERE { "
				+ active_str
				+ "?name dc:id "+id+" .}";
		// System.out.println("QueryString: " + queryString + "N-TRIPLE");
		// model.write(System.out, "N-TRIPLE");
		qe = QueryExecutionFactory.create(queryString, model);
		results = qe.execSelect();
		if (results.hasNext()) {
			QuerySolution row = results.next();
			// RDFNode thing= row.get("rule");
			result_s = row.getResource("name").toString();
			return result_s;
		}
		else
		{return null;}
	}
	
	public boolean IsStaticCheckActive(Model model, String id)
	{
		ResultSet results;
		QueryExecution qe;
	
		String queryString = "PREFIX dc:      <http://purl.org/dc/elements/1.1/> "
				+ "SELECT  ?name "
				+ "WHERE { "
				+ "?name dc:statictest \"true\". "
				+ "?name dc:id "+id+" .} ";
		// System.out.println("QueryString: " + queryString + "N-TRIPLE");
		// model.write(System.out, "N-TRIPLE");
		qe = QueryExecutionFactory.create(queryString, model);
		results = qe.execSelect();
		if (results.hasNext()) {
			return true;
		}
		else
		{return false;}
	}
	
	public ArrayList<String> GetErrorSensors(Model model, String range, boolean active) {
		String active_str="";
		if (active)
		{
			active_str="?o dc:active \"true\". ";
		}
		String result_s = null;
		ArrayList<String> resultlist = new ArrayList<String>();
		String queryString = "PREFIX dc:      <http://purl.org/dc/elements/1.1/> "
				+ "PREFIX rdf:        <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
				+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> "
				+ "PREFIX time: <http://www.w3.org/2006/time#> "
				+ "SELECT  ?rule "
				+ "WHERE { "
				+ active_str
				+ "?o dc:type ?type. "
				+ "?o dc:ceprule ?rule. " + " " +
						"Filter(?type=(\"error\") ||" +
						"?type=(\"helper\" ))}";
		// System.out.println("QueryString: " + queryString + "N-TRIPLE");
		// model.write(System.out, "N-TRIPLE");
		qe = QueryExecutionFactory.create(queryString, model);
		results = qe.execSelect();
		while (results.hasNext()) {
			QuerySolution row = results.next();
			// RDFNode thing= row.get("rule");
			result_s = row.getLiteral("rule").getString();

			// If helper rule
			if (row.getLiteral("clid") != null) {
				result_s = result_s + ":" + row.getLiteral("clid").getString();
			}

			// System.out.println("rule_s: " + resultlist.get(0));
			String phrase = result_s;
			String[] tokens = phrase.split(range+"_Sensor");
			int n = 1;
			while (n < tokens.length) {
				// Trennzeichen bei allen Zeichen außer Zahl
				String[] tokens2 = tokens[n].split("[^0-9]");

				// Zur Liste hinzufügen
				if (!resultlist.contains(tokens2[0])) {
					resultlist.add(tokens2[0]);
								}

				n++;
			}
		}

		return resultlist;
	}
	
	public ArrayList<String> GetSensors(Model model, boolean active) {
		String active_str="";
		if (active)
		{
			active_str="?o dc:active \"true\". ";
		}
		String result_s = null;
		ArrayList<String> resultlist = new ArrayList<String>();
		String queryString = "PREFIX dc:      <http://purl.org/dc/elements/1.1/> "
				+ "PREFIX rdf:        <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
				+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> "
				+ "PREFIX time: <http://www.w3.org/2006/time#> "
				+ "SELECT DISTINCT  ?sensorid "
				+ "WHERE { "
				+ active_str
				+ "?o dc:id ?sensorid. }";
		//System.out.println("QueryString: " + queryString + "N-TRIPLE");
		// model.write(System.out, "N-TRIPLE");
		qe = QueryExecutionFactory.create(queryString, model);
		results = qe.execSelect();
		while (results.hasNext()) {
			QuerySolution row = results.next();
			// RDFNode thing= row.get("rule");
			result_s = row.getLiteral("sensorid").getString();
			//System.out.println("Add sensor: "+result_s);
			resultlist.add(result_s);
		}

		return resultlist;
	}

}
