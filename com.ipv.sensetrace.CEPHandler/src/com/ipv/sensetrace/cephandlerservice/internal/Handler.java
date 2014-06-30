package com.ipv.sensetrace.cephandlerservice.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Queue;

import com.ipv.sensetrace.pgsqlservice.PgService;

public class Handler implements Runnable {
	private final Queue<String> messageQueue;
	static java.net.Socket socket = null;
	static PrintWriter out = null;
	static BufferedReader in = null;
	PgService pgsqlservice = null;
	ArrayList<String> CEPRuleClEvents;
	ArrayList<String> CEPRuleErrorEvents;
	TimeFormat time = new TimeFormat();

	public Handler(Queue<String> messageQueue_r) {
		this.messageQueue = messageQueue_r;
	}

	public void run() {

		System.out.println("Connect to Jtalis!");
		connect();
		handling();
	}

	public void handling() {

		// EventHandler eventHandler = new EventHandler();
		// eventHandler.HandleEvent("ou2t_of_range('1', -0.17657, 1252233235)");

		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String line;

		// Terminates, if Server shutd down
		// br.readLine() blocks till new line received

		try {
			while ((line = br.readLine()) != null) {

				// System.out.println("Received Event: " + line);
				messageQueue.offer(line);

				synchronized (messageQueue) {
					messageQueue.notifyAll();
				}
				 System.out.println("Received event: " + line
				 +" and pushed to queue.");

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// eventHandler.CloseEventHandler();

	}

	public static void connect() {
		try {
			socket = new java.net.Socket("127.0.0.1", 8888);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			System.out
					.println("Jtalis System successfully connected on internal socket!");
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host: localhost.");
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for "
					+ "the connection to: localhost.");
			System.exit(1);
		}
	}

	static void leseNachricht(java.net.Socket socket) throws IOException {

		BufferedReader br = new BufferedReader(new InputStreamReader(
				socket.getInputStream()));
		String line;

		// Terminates, if Server shutd down
		// br.readLine() blocks till new line received

		while ((line = br.readLine()) != null) {

			// line = br.readLine();
			// System.out.println("System shoud block now!");

			// /if (line!=null)
			System.out.println(line);

		}

		// return line;
	}

	static void schreibeNachricht(java.net.Socket socket, String nachricht)
			throws IOException {
		PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(
				socket.getOutputStream()));
		printWriter.print(nachricht);
		printWriter.flush();
	}
}