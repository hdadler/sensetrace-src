package com.ipv.sensetrace.mailservice;



import com.ipv.sensetrace.mailservice.intern.Mail;
import com.ipv.sensetrace.rdfdmservice.RDFDmService;


public class MailService implements IMailService {
	RDFDmService rdfservice;
	Mail mail;

	public void Init(String mail_from, String mail_to, String smpt_server, String smpt_server_port,
			String username, String pwd, String smtp_auth, String starttls_str) {
		boolean auth=false;
		boolean starttls=false;
		if(smtp_auth!=null && smtp_auth.equals("1"))
		{
			auth=true;
		}
		if(starttls_str!=null && starttls_str.equals("1"))
		{
			starttls=true;
		}

		mail = new Mail(mail_from, mail_to,smpt_server,smpt_server_port, username, pwd, auth, starttls);

	}

	@Override
	public void SendStatusMail() {

		mail.SendStatusMail();

	}

	@Override
	public void RegisterCEPError(String msg) {
		// TODO Auto-generated method stub
		mail.RegisterCEPError(msg);

	}

	@Override
	public void RegisterStaticProblem(String msg) {
		// TODO Auto-generated method stub
		mail.RegisterStaticProblem(msg);
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

	@Override
	public void SendCVSFilesIncomplete() {
		// TODO Auto-generated method stub
		mail.SendCVSFilesIncomplete();
	}

	@Override
	public void SendFolderNotAvailable() {
		// TODO Auto-generated method stub
		mail.SendFolderNotAvailable();
	}
}
