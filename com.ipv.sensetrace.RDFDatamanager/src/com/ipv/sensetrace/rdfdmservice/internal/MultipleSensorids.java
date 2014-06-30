package com.ipv.sensetrace.rdfdmservice.internal;


import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Model;


public class MultipleSensorids {
	ResultSet results;
	Query query;
	QueryExecution qe;

	// static Model tmpmodel=null;

	public void QueryForMultipleSensorids(Model model) {

		// Der RDF-Store wird abgefragt mit einem SPARQL-Befehl um mysqlid,
		// postresid,
		// lowerlimit und upperlimit zu ermitteln. Die Antwort erhalten wir als
		String queryString = "PREFIX dc:      <http://purl.org/dc/elements/1.1/> "
				+ "PREFIX rdf:        <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
				+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> "
				+ "PREFIX time: <http://www.w3.org/2006/time#> "
				+ "SELECT  ?id (count(?id) AS ?count)"
				+ " WHERE { "
				+ "?group dc:id ?id. }"
				+ " GROUP BY ?id "
				+ " HAVING (count(?id)>1)";
		// /System.out.println("QueryString: " + queryString + "N-TRIPLE");
		// model.write(System.out, "N-TRIPLE");
		qe = QueryExecutionFactory.create(queryString, model);
		results = qe.execSelect();
	}
	public void QueryForDifferentDbIds(Model model) {

		// Der RDF-Store wird abgefragt mit einem SPARQL-Befehl um mysqlid,
		// postresid,
		// lowerlimit und upperlimit zu ermitteln. Die Antwort erhalten wir als
		String queryString = "PREFIX dc:      <http://purl.org/dc/elements/1.1/> "
				+ "PREFIX rdf:        <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
				+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> "
				+ "PREFIX time: <http://www.w3.org/2006/time#> "
				+ "SELECT  distinct ?id "
				+ "WHERE { "
				+ "?o dc:id ?id. " 
				+ "?o dc:mysqlid ?mid. "
				+ " FILTER (?mid != ?id)"
				+ "}";
		// /System.out.println("QueryString: " + queryString + "N-TRIPLE");
		// model.write(System.out, "N-TRIPLE");
		qe = QueryExecutionFactory.create(queryString, model);
		results = qe.execSelect();
	}
	public String GetResult() {
		String result_s = "";
		while (results.hasNext()) {
			QuerySolution row = results.next();
			// RDFNode thing= row.get("rule");
			// result_s = row.getLiteral("id").getString();

			result_s = result_s + row.getLiteral("id").getString() + "," ;
		}
		if (!result_s.equals(""))
		{
		result_s=result_s.substring(0, result_s.length()-1);
		}
		return result_s;
	}

}
