package com.ipv.sensetrace.rdfdmservice;

import java.util.ArrayList;

import com.ipv.sensetrace.rdfdmservice.external.RDFServer;
import com.ipv.sensetrace.rdfdmservice.internal.ModelStore;
import com.ipv.sensetrace.rdfdmservice.internal.MultipleSensorids;
import com.ipv.sensetrace.rdfdmservice.internal.RDFResultCEPRules;
import com.ipv.sensetrace.rdfdmservice.internal.RDFResultSensors;

public class RDFDmService implements IRDFDmService {
	ModelStore rdfmodel = new ModelStore();
	RDFResultSensors rdfsensorresult = new RDFResultSensors();
	RDFResultCEPRules rdfcepresult = new RDFResultCEPRules();
	RDFServer rdfserver = new RDFServer();

	@Override
	public void StoreRDFData(String rdfstring) {
		rdfmodel.create(rdfstring);

	}

	public String QueryForMultipleSensorids() {
		MultipleSensorids mlsensors = new MultipleSensorids();
		mlsensors.QueryForMultipleSensorids(rdfmodel.GetModel());
		return mlsensors.GetResult();
	}

	public String QueryForDifferentDbIds() {
		MultipleSensorids mlsensors = new MultipleSensorids();
		mlsensors.QueryForDifferentDbIds(rdfmodel.GetModel());
		return mlsensors.GetResult();
	}

	// public ArrayList<String> GetErrorIDs() {
	// rdfcepresult.QueryErrorRules(rdfmodel.GetModel());
	// return rdfcepresult.GetIDList();
	// }

	public ArrayList<String> GetCLIds(boolean active) {
		rdfcepresult.QueryClRules(rdfmodel.GetModel(), active);
		return rdfcepresult.GetIDList();
	}

	public ArrayList<String> GetClRules(boolean active) {

		// ArrayList<result> resultlist = new ArrayList<result>();
		System.out.println("GetClRules");
		rdfcepresult.QueryClRules(rdfmodel.GetModel(), active);
		return rdfcepresult.GetResult();
	}

	public ArrayList<String> SensorsToReplace(boolean active) {
		int n = 0;
		ArrayList<String> getErrorRules = GetErrorRules(true);
		ArrayList<String> sensorwitherrors = new ArrayList<String>();
		while (n < getErrorRules.size()) {
			String[] tokens = getErrorRules.get(n).toString().split(":");
			System.out.println("Sensoridtoreplace:" + tokens[2]);
			n++;
			sensorwitherrors.add(tokens[2]);
		}
		return sensorwitherrors;
	}

	public ArrayList<String> GetErrorRules(boolean active) {

		// ArrayList<result> resultlist = new ArrayList<result>();
		System.out.println("GetErrorRules");
		rdfcepresult.QueryErrorRules(rdfmodel.GetModel(), active);
		return rdfcepresult.GetResult();
	}

	public ArrayList<String> GetHelperRules(boolean active) {

		// ArrayList<result> resultlist = new ArrayList<result>();
		System.out.println("GetHelperRules");
		rdfcepresult.QueryHelperRules(rdfmodel.GetModel(), active);
		return rdfcepresult.GetResult();
	}

	public ArrayList<String> GetClassificationSensors(String range,
			boolean active) {
		return rdfcepresult.GetClassificationSensors(rdfmodel.GetModel(),
				range, active);
	}

	public ArrayList<String> GetErrorSensors(String range, boolean active) {
		return rdfcepresult.GetErrorSensors(rdfmodel.GetModel(), range, active);
	}

	public ArrayList<String> GetSensors(boolean active) {
		return rdfcepresult.GetSensors(rdfmodel.GetModel(),active);
	}
	@Override
	public boolean UpdateOnServer(String address) {
		rdfserver.Update(address, rdfmodel.GetN3String());
		return true;
	}

	@Override
	public boolean DeleteOnServer(String address) {
		return rdfserver.Delete(address);
	}
	
	@Override
	public String  ResolveSensor(String id, boolean active) {
		System.out.println("Query id: " + id);
		 return rdfcepresult. ResolveSensorID(rdfmodel.GetModel(), id, active);
	}

	// Not the deactived sensors
	@Override
	public void QuerySensors(boolean solarlog) {
		rdfsensorresult.QuerySensors(rdfmodel.GetModel(),solarlog);
	}

	@Override
	public void QueryAllSensors(boolean solarlog) {
		rdfsensorresult.QueryAllSensors(rdfmodel.GetModel(),solarlog);
	}

	/*
	 * public void QueryActivatedSensors() {
	 * rdfsensorresult.QueryActivatedSensors(rdfmodel.GetModel()); }
	 */
	public boolean IsStaticCheckActive(String postgresid)
	{
		return rdfcepresult.IsStaticCheckActive(rdfmodel.GetModel(), postgresid);
	}
	
	@Override
	public String GetNextSensor(String key) {

		if (key == "definition") {
			return rdfsensorresult.GetNextDefinition();
		}
		else if (key == "mysqlid") {
			return rdfsensorresult.GetNextMysqlid();
		} else if (key == "postgresid") {
			String tmp=rdfsensorresult.GetNextPostgresid();
			return tmp;
		} else if (key == "lowerlimit") {
			return rdfsensorresult.GetNextlowerLimit();
		} else if (key == "csvarray") {
			String tmp=rdfsensorresult.GetNextCSVArray();
			return tmp;
		} else if (key == "upperlimit") {
			return rdfsensorresult.GetNextupperLimit();
		} else if (key == "ftplink") {
			return rdfsensorresult.GetNextFTPLink();
		} else if (key == "solarlogfilenamelink") {
			return rdfsensorresult.GetNextSolarlogLink();
		} else if (key == "dlfilenamelink") {
			return rdfsensorresult.GetNextDlFilenameLink();
		} else if (key == "differencetopreviousvalue") {
			String tmp = rdfsensorresult.GetNextDifferenceToPreviousValue();
			// System.out.println("GetNextDifferenceToPreviousValue: " + tmp);
			if (tmp == null || tmp.equals("")) {
				return null;
			} else {
				return tmp;
			}
		} else
			return "unknown";

	}
}
