package com.ipv.sensetrace.delphinftpservice.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class MemConverter {
	
	private void execute(String str)
	{
		String s = ""; // Lesepuffer
		// Stream zum Einlesen der Prozeßausgabe
		BufferedReader in;
		PrintWriter out = new PrintWriter(System.out);
		try {
			// Prozeß anlegen
			System.out.println("Registration of mem_convert_options...");
			Process proc = Runtime.getRuntime().exec(str);
			// Eingabestream holen
			in = new BufferedReader(
					new InputStreamReader(proc.getInputStream()));
			proc.waitFor();
			System.out.println("Registration of mem_convert_options done");
			
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(
					proc.getInputStream()));

			BufferedReader stdError = new BufferedReader(new InputStreamReader(
					proc.getErrorStream()));

			// read the output from the command
			System.out.println("Here is the standard output of the command:\n");
			while ((s = stdInput.readLine()) != null) {
				System.out.println(s);
			}

			// read any errors from the attempted command
			System.out
					.println("Here is the standard error of the command (if any):\n");
			while ((s = stdError.readLine()) != null) {
				System.out.println(s);
			}
			Thread.sleep(2000);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	
	public void convert(String memconvert, String input, String output) {
//execute("wine regedit.exe /home/hendrik/Release1/workspacesensetrace/com.ipv.sensetrace.DataloggerService/options_mem_convert.reg");

execute("wine regedit.exe "+memconvert+"/options_mem_convert.reg");
System.out.println("input: " + input);
System.out.println("output: " + output);
execute("wine "+memconvert+"/MemConvert.exe"+" " + input +" " + output);

	}

	/*
	 * String[] cmd = {"wine regedit.exe options_mem_convert.reg"}; //String[]
	 * cmd = {"wine MemConvert.exe memory.mem test.asc"}; try {
	 * Runtime.getRuntime().exec(cmd); } catch (IOException e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); } }
	 */
}
