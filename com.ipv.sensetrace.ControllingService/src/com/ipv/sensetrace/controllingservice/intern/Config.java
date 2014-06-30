package com.ipv.sensetrace.controllingservice.intern;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * This class provides methods for reading the Config-Files
 
 */
public class Config {


	Properties configFile;

	/**
	 * The constructor loads the config-file "config.cfg"
	 * @throws Exception 
	 */
	public Config() throws Exception
	   {
	    configFile = new java.util.Properties();
	    System.out.println("Read in configfile.");
	    File f = new File("/etc/sensetrace/config.cfg");
	    configFile.load(new FileInputStream(f));
	    System.out.println("Load configfile.");
	    //  configFile.load(this.getClass().getClassLoader().
	      //getResourceAsStream("config.cfg"));
	
	   }
	 
	public boolean IsconfigFile()
	{
		if (configFile!=null)
		return true;
		else
			return false;
	}
	   /**
	 * @param key		the key of the value we want to know
	 * @return 			value of a certain key
	 */
	public String getProperty(String key)
	   {
	    String value = this.configFile.getProperty(key);
	    return value;
	   }
	
	

}
