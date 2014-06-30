package com.ipv.sensetrace.rdfdmservice.external;

import com.hp.hpl.jena.query.ARQ;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.update.UpdateExecutionFactory;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateProcessor;
import com.hp.hpl.jena.update.UpdateRequest;


public class RDFServer {

	/**
	 * @param server
	 *            the address of the dataset
	 * @return true if the request was successful, else false
	 */
	public boolean Delete(String server) {
		server = server + "/update";
		/**
		 * This string is for deleting everything on the SPARQL-server.
		 */
		String sparqlRequestString = "delete  {?a ?b ?c} where {?a ?b ?c }";
		UpdateRequest request = UpdateFactory.create(sparqlRequestString);
		ARQ.getContext().setTrue(ARQ.useSAX);
		/**
		 * Executing SPARQL Query and pointing to the SPARQL Endpoint
		 */
		UpdateProcessor processor = UpdateExecutionFactory.createRemote(
				request, server);
		try {
			processor.execute();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Delete data from Fuseki server not possible. ");
			System.out.println("Connection string was: " + server);
			return false;
		}
		return true;

	}

	/**
	 * 
	 * @param server
	 *            the address of the dataset
	 * @return true if the request was successful, else false
	 */
	public boolean Update(String server, String sparqlRequestString) {
		sparqlRequestString = "INSERT DATA {" + sparqlRequestString + "}";
		//System.out.println("Das ist der String: " + sparqlRequestString);
		server = server + "/update";
		/**
		 * This string is for updating the RDFstore on the SPARQL-server.
		 */
		// String sparqlRequestString = "delete  {?a ?b ?c} where {?a ?b ?c }";
		// UpdateRequest request = UpdateFactory.create(sparqlRequestString);
		UpdateRequest request = UpdateFactory.create(sparqlRequestString);
		ARQ.getContext().setTrue(ARQ.useSAX);
		/**
		 * Executing SPARQL Query and pointing to the SPARQL Endpoint
		 */
		UpdateProcessor processor = UpdateExecutionFactory.createRemote(
				request, server);
		try {
			processor.execute();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Delete data from Fuseki server not possible. ");
			System.out.println("Connection string was: " + server);
			return false;
		}
		return true;

	}

	/**
	 * 
	 * @param server
	 *            the address of the dataset
	 * @return true if the query was successful, else false
	 */
	public boolean Query(String server) {
		server = server + "/query";
		String sparqlQueryString = " SELECT ?a " + " WHERE {{ "
				+ " ?a ?b ?c.}}";
		/**
		 * Executing SPARQL Query and pointing to the SPARQL Endpoint
		 */
		Query query = QueryFactory.create(sparqlQueryString);
		ARQ.getContext().setTrue(ARQ.useSAX);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(server,
				query);
		/**
		 * Retrieving the SPARQL Query results
		 */
		ResultSet results;
		try {
			results = qexec.execSelect();
			/**
			 * Iterating over the SPARQL Query results
			 */
			while (results.hasNext()) {
				QuerySolution soln = results.nextSolution();
				/**
				 * Printing DBpedia entries' abstract
				 */
				System.out.println(soln.get("?a"));
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Query data from SPARQL-Server not possible. ");
			System.out.println("Connection string was: " + server);
			return false;
		}

		qexec.close();
		return true;

	}

}