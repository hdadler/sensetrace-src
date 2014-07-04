package com.ipv.sensetrace.controllingservice;

public interface IControlService {

	public void start(boolean test, boolean downloadfromcsv,boolean downloadfromdatalogger_ftp,boolean downloadfromdatalogger_folder,
			boolean downloadfromsolarlogj, boolean generate_v_data_stream);
}
