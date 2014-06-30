package com.ipv.sensetrace.controllingservice.intern;

import java.io.File;
import java.io.FilenameFilter;

import com.ipv.sensetrace.rdfdmservice.RDFDmService;

/**
 
 *         This class is for processing our SensorML-Files. Transforms
 *         SensorML-Files via XSLT to RDF-String. Writes RDF-String to internal
 *         RDF-Store in RDF-Bundle and synchronizes it with Fuseki.
 * 
 */
public class ProcessSensorMLFiles {
	RDFDmService rdfservice;
	Config conf;

	/**
	 * @param refrdfservice
	 *            Reference to RDFService
	 * @param refconf
	 *            Reference to Configuration class
	 */
	public ProcessSensorMLFiles(RDFDmService refrdfservice, Config refconf) {
		// TODO Auto-generated constructor stub
		rdfservice = refrdfservice;
		conf = refconf;
	}

	/**
	 * @throws Exception
	 *             This method is for processing the SensorML-Files. Transforms
	 *             SensorML-Files via XSLT to RDF-String. Writes RDF-String to
	 *             internal RDF-Store in RDF-Bundle and synchronizes it with
	 *             Fuseki.
	 */
	public void Process() throws Exception {

		// create new filename filter
		FilenameFilter fileNameFilter = new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				if (name.lastIndexOf('.') > 0) {
					// get last index for '.' char
					int lastIndex = name.lastIndexOf('.');

					// get extension
					String str = name.substring(lastIndex);

					// match path name extension
					if (str.equals(".xml")) {
						return true;
					}
				}
				return false;
			}
		};

		/**
		 * Convert SensorML-File to RDF-String
		 */

		/*
		 * At first convert CEP-Rules-File
		 */
		XMLToRDF xmltordf = new XMLToRDF();
		String ceprules = null;
		String rdfdataplants = null;
		String rdfdatalocationpartition = null;
		ceprules = xmltordf.convert(
				new File(conf.getProperty("ceprulesfile")).getAbsolutePath(),
				conf.getProperty("xsltfolder") + "/ceprules.xslt");
		// System.out.println("RDF-String CEPRules: " + ceprules);
		/*
		 * Convert other SensorML-Files
		 */
		File f = new File(conf.getProperty("plantfile"));
		File[] fileArray = f.listFiles(fileNameFilter);

		for (int i = 0; i < fileArray.length; i++) {

			if (fileArray[i].isFile()) {

				rdfdataplants = xmltordf.convert(fileArray[i].getAbsolutePath()
						.toString(), conf.getProperty("xsltfolder")
						+ "/location.xslt");

				// System.out.println("RDF-String Plants: " +
				// rdfdataplants);

				rdfdatalocationpartition = xmltordf.convert(fileArray[i]
						.getAbsolutePath().toString(),
						conf.getProperty("xsltfolder")
								+ "/location-partition.xslt");
				// System.out.println("RDF-String: " +
				// rdfdatalocationpartition);

				/**
				 * Save RDF-String in RDFDatamanager
				 */
				rdfservice.StoreRDFData(rdfdataplants);
				rdfservice.StoreRDFData(rdfdatalocationpartition);
				rdfservice.StoreRDFData(ceprules);
			}

		}
		String rdfsensors = null;
		String rdfpartitionsensor = null;
		String ftpaccess = null;
		f = new File(conf.getProperty("sensormlfiles"));
		fileArray = f.listFiles(fileNameFilter);
		for (int i = 0; i < fileArray.length; i++) {
			if (fileArray[i].isFile()) {

				rdfpartitionsensor = xmltordf.convert(fileArray[i]
						.getAbsolutePath().toString(),
						conf.getProperty("xsltfolder")
								+ "/partition-sensors.xslt");

				// System.out.println("RDF-String: " + rdfpartitionsensor);

				rdfsensors = xmltordf.convert(fileArray[i].getAbsolutePath(),
						conf.getProperty("xsltfolder") + "/sensors.xslt");
				// System.out.println("RDF-String: " + rdfsensors);

				ftpaccess = xmltordf.convert(fileArray[i].getAbsolutePath(),
						conf.getProperty("xsltfolder") + "/ftpaccess.xslt");
				// System.out.println("FTP-String: " + ftpaccess);
				/**
				 * Save RDF-String in RDFDatamanager
				 */
				rdfservice.StoreRDFData(rdfpartitionsensor);
				rdfservice.StoreRDFData(rdfsensors);
				rdfservice.StoreRDFData(ftpaccess);
			}

		}
		/**
		 * Save RDF-String in RDFDatamanager
		 */
		rdfservice.StoreRDFData(rdfdataplants);
		rdfservice.StoreRDFData(rdfdatalocationpartition);
		/**
		 * Synchronize internal RDF-Store with Fuseki Server over http
		 */
		rdfservice.DeleteOnServer(conf.getProperty("RDFServer"));
		rdfservice.UpdateOnServer(conf.getProperty("RDFServer"));

	}
}
