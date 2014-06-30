package com.ipv.sensetrace.mailservice;


public interface IMailService {
	void SendStatusMail();
	void SendCVSFilesIncomplete();
	void RegisterCEPError(String msg);

	void RegisterStaticProblem(String msg);
	void SendFolderNotAvailable();
}
