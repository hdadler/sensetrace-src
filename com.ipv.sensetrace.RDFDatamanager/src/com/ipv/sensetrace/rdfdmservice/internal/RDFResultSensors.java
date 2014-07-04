package com.ipv.sensetrace.rdfdmservice.internal;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Model;


public class RDFResultSensors {
	ResultSet results;
	Query query;
	QueryExecution qe;

	ArrayList<Bindings> mysql_pg_list;

	// static Model tmpmodel=null;
	//Don not query the deactive sensors
	public void QuerySensors(Model model) {

		// Der RDF-Store wird abgefragt mit einem SPARQL-Befehl um mysqlid,
		// postresid,
		// lowerlimit und upperlimit zu ermitteln. Die Antwort erhalten wir als
		String queryString = "PREFIX dc:      <http://purl.org/dc/elements/1.1/> "
				+ "PREFIX rdf:        <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
				+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> "
				+ "PREFIX time: <http://www.w3.org/2006/time#> "
				+ "SELECT  ?mysqlid ?postgresid ?definition ?lowerlimit ?csvarray ?upperlimit ?ftplink ?solarlogfolderlink ?dlfolderlink ?differencetopreviousvalue "
				+ "WHERE { "
				+ "?o dc:definition ?definition."
				+ "?o dc:lowerlimit ?lowerlimit."
				+ "?o dc:csvarray ?csvarray."
				+ "?o dc:upperlimit ?upperlimit."
				+ "?o dc:differencetopreviousvalue ?differencetopreviousvalue."
				+ "?o dc:mysqlid ?mysqlid."
				+ "?o dc:id ?postgresid."
				+ "?p dc:sens ?o."
				+ "?p dc:ftp ?ftplink ."
				+ "?p dc:dlfolderlink ?dlfolderlink ."
				+ "?p dc:solarlogfolderlink ?solarlogfolderlink ."
				+ " FILTER (NOT EXISTS { ?o dc:active \"false\". } ) } order by ?mysqlid";
		 System.out.println(queryString);
		query(model, queryString);
		//printquery();
	}

	public void QueryAllSensors(Model model) {

		// Der RDF-Store wird abgefragt mit einem SPARQL-Befehl um mysqlid,
		// postresid,
		// lowerlimit und upperlimit zu ermitteln. Die Antwort erhalten wir als
		String queryString = "PREFIX dc:      <http://purl.org/dc/elements/1.1/> "
				+ "PREFIX rdf:        <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
				+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> "
				+ "PREFIX time: <http://www.w3.org/2006/time#> "
				+ "SELECT  ?mysqlid ?postgresid ?definition ?lowerlimit ?csvarray ?upperlimit ?ftplink ?dlfolderlink  ?solarlogfolderlink ?differencetopreviousvalue "
				+ "WHERE { "
				+ "?o dc:definition ?definition."
				+ "?o dc:csvarray ?csvarray."
				+ "?o dc:lowerlimit ?lowerlimit."
				+ "?o dc:upperlimit ?upperlimit."
				+ "?o dc:differencetopreviousvalue ?differencetopreviousvalue."
				+ "?o dc:mysqlid ?mysqlid."
				+ "?o dc:id ?postgresid."
				+ "?p dc:sens ?o."
				+ "?p dc:ftp ?ftplink ."
				+ "?p dc:dlfolderlink ?dlfolderlink ."
				+ "?p dc:solarlogfolderlink ?solarlogfolderlink ."
				+ " } order by ?mysqlid";
		 System.out.println(queryString);
		System.out.println("Query all sensors!");
		query(model, queryString);
		//printquery();
		}

	public void query(Model model, String queryString) {
		// Für Test 3
		// Model tmpmodel=model;
		// tmpmodel=rdfmodelref;
		//
		// System.out.println("QueryString: " + queryString +"N-TRIPLE");
		// model.write(System.out, "N-TRIPLE");

		qe = QueryExecutionFactory.create(queryString, model);
		results = qe.execSelect();
	//	printquery();
		// Result als Json-String
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ResultSetFormatter.outputAsJSON(out, results);
		
		// baos.write("Hallo".getBytes());
		// OutputStream out = new StringWriter();

		// JSONOutputResultSet set = new JSONOutputResultSet(out);
		// ResultSetApply apply = new ResultSetApply(results, set);
		// apply.apply();

		// System.out.println("out2: " + out.toString());
		
		
		
		//Gson gson = new GsonBuilder().create();		
		//Without created instance it is not working outside eclipse
		Gson gson = new GsonBuilder().registerTypeAdapter(Results.class, new ResultsInstanceCreator()).create();
		Results mess = gson.fromJson(out.toString(), Results.class);
		
		// set.finish(results.next());

		// Important - free up resources used running the query

	//	String json = new Gson().toJson(mess);
		// System.out.println("json: " + json.toString());
		int i = 0;

		mysql_pg_list = new ArrayList<Bindings>();

		while (i < mess.results.bindings.length) {
			mysql_pg_list.add(mess.results.bindings[i]);
			// System.out.println(mess.results.bindings[i].postgresid.value);
			// System.out.println(mess.results.bindings[i].differencetopreviousvalue.value);

			i++;
		}

		// Iteratoren initializieren.
		itmysql = mysql_pg_list.iterator();
		itpostgres = mysql_pg_list.iterator();
		itlowlimit = mysql_pg_list.iterator();
		itcsvarray = mysql_pg_list.iterator();
		ituplimit = mysql_pg_list.iterator();
		itftplink = mysql_pg_list.iterator();
		itdlfolderlink = mysql_pg_list.iterator();
		itsolarlogfolderlink = mysql_pg_list.iterator();
		itdefinition = mysql_pg_list.iterator();
		itdifferencetopreviousvalue = mysql_pg_list.iterator();
		//itdifferencetopreviousvalue2 = mysql_pg_list.iterator();
	}

	Iterator<Bindings> itmysql;
	Iterator<Bindings> itpostgres;
	Iterator<Bindings> itlowlimit;
	Iterator<Bindings> itcsvarray;
	Iterator<Bindings> ituplimit;
	Iterator<Bindings> itftplink;
	Iterator<Bindings> itdefinition;
	Iterator<Bindings> itdifferencetopreviousvalue;
	Iterator<Bindings> itdlfolderlink;
	Iterator<Bindings> itsolarlogfolderlink;
	//Iterator<Bindings> itdifferencetopreviousvalue2;

	public String GetNextMysqlid() {

		if (itmysql.hasNext()) {
			// System.out.println(it.next());
			return itmysql.next().mysqlid.value;
		} else
			return null;
	}

	public String GetNextPostgresid() {

		if (itpostgres.hasNext()) {
			// System.out.println(it.next());
			return itpostgres.next().postgresid.value;
		} else
			return null;
	}

	public String GetNextupperLimit() {

		if (ituplimit.hasNext()) {
			// System.out.println(it.next());
			return ituplimit.next().upperlimit.value;
		} else
			return null;
	}

	public String GetNextlowerLimit() {

		if (itlowlimit.hasNext()) {
			// System.out.println(it.next());
			return itlowlimit.next().lowerlimit.value;
		} else
			return null;
	}
	
	public String GetNextCSVArray() {

		if (itcsvarray.hasNext()) {
			// System.out.println(it.next());
			return itcsvarray.next().csvarray.value;
		} else
			return null;
	}

	public String GetNextFTPLink() {

		if (itftplink.hasNext()) {
			// System.out.println(it.next());
			return itftplink.next().ftplink.value;
		} else
			return null;
	}

	public String GetNextDlFilenameLink() {

		if (itdlfolderlink.hasNext()) {
			// System.out.println(it.next());
			return itdlfolderlink.next().dlfolderlink.value;
		} else
			return null;
	}
	
	public String GetNextSolarlogLink() {

		if (itsolarlogfolderlink.hasNext()) {
			// System.out.println(it.next());
			return itsolarlogfolderlink.next().solarlogfolderlink.value;
		} else
			return null;
	}
	
	public String GetNextDefinition() {
		if (itdefinition.hasNext()) {
			// System.out.println(it.next());
			return itdefinition.next().definition.value;
		} else
			return null;
	}

	public String GetNextDifferenceToPreviousValue() {
		if (itdifferencetopreviousvalue.hasNext()) {

			return itdifferencetopreviousvalue.next().differencetopreviousvalue.value;

		} else
		return null;
		
	}

	public static class Postgresid {
		public String value;
		public String type;

	};

	public static class lowerlimit {
		public String value;
		public String type;

	};
	public static class csvarray {
		public String value;
		public String type;

	};

	public static class upperlimit {
		public String value;
		public String type;

	};

	public static class Mysqlid {
		public String value;
		public String type;
	};

	public static class creationdate {
		public String value;
		public String type;
	};

	public static class ftplink {
		public String value;
		public String type;

	};
	
	public static class dlfolderlink {
		public String value;
		public String type;

	};
	
	public static class solarlogfolderlink {
		public String value;
		public String type;

	};

	public static class definition {
		public String value;
		public String type;

	};
	       
	public static class differencetopreviousvalue {
		public String value;
		public String type;

	};

	public static class Bindings {
		public Mysqlid mysqlid;
		public Postgresid postgresid;
		public creationdate creationdate;
		public lowerlimit lowerlimit;
		public csvarray csvarray;
		public upperlimit upperlimit;
		public ftplink ftplink;
		public dlfolderlink dlfolderlink;
		public solarlogfolderlink solarlogfolderlink;
		public definition definition;
		public differencetopreviousvalue differencetopreviousvalue;
		// public String mysqlid;
	};

	public static class Result {
		Result()
		{
			
		}
		public Bindings[] bindings;
	};

	
	//Without an instance we got problems outside eclipse
	class ResultsInstanceCreator implements InstanceCreator<Results>
	{
	  @Override
	  public Results createInstance(Type type)
	  {
	    return new Results();
	  }
	}
	
	public  class Results {
		Results()
		{
			
		}
		public  Result results;
	};

	void printquery() {
		// Output query results
		ResultSetFormatter.out(System.out, results, query);
		qe.close();
	}

}
