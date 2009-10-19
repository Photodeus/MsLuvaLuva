package com.popodeus.chat;

import sun.org.mozilla.javascript.internal.ScriptableObject;

import java.net.ServerSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.io.*;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Logger;

import org.jibble.pircbot.User;

/**
 * photodeus
 * Sep 22, 2009
 * 11:09:41 AM
 */
public class BotRemote extends Thread {
	private static int maxconnections = 5;
	static Logger log = Logger.getLogger("com.popodeus.chat.BotRemote");
	private ServerSocket socket;
	private Map<String, ScriptableObject> callbacks;
	private BotCallbackAPI bot;
	private ScriptAPI scripts;

	public BotRemote(final int port, BotCallbackAPI bot, final ScriptAPI scripts) throws IOException {
		log.entering("BotRemote", "constructor", port);
		this.bot = bot;
		this.scripts = scripts;
		InetAddress ipaddr = InetAddress.getLocalHost();
		socket = new ServerSocket(port, maxconnections, ipaddr);
		// socket.setSoTimeout(0); // wait infinitely
		callbacks = new HashMap<String, ScriptableObject>();
		this.start();
	}

	@Override
	public void run() {
		log.entering("BotRemote", "run");
		boolean shouldrun = true;
		try {
			do {
				log.info("Network socket listening");
				// Block until someone connects
				Socket s = socket.accept();

				log.info("Network socket accepted connection from " + s.getRemoteSocketAddress());

				BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF-8"));
				PrintWriter pw = new PrintWriter(s.getOutputStream(), true);
				// TODO ask for password?
				pw.println("Welcome to MsLuvaLuva. Type 'help' for commands.");
				String line;
				while ((line = br.readLine()) != null) {
					log.info(s.getRemoteSocketAddress() + ": " + line);
					if (line.startsWith("help")) {
						pw.println("ping");
						pw.println("quit");
						pw.println("reset [scriptname]");

						pw.println("say <#channel|nick> message");
						pw.println("notice <#channel|nick> message");
						pw.println("join #channel");
						pw.println("part #channel");
						pw.println("list #channel");
						pw.println("status");

						pw.println("BYE   (be careful with this one!)");
					}
					if (line.startsWith("ping")) {
						pw.write("PONG");
					}
					if (line.startsWith("quit")) {
						pw.write("Bye bye baby!\n");
						s.close();
						break;
					}
					if (line.startsWith("reset")) {
						scripts.clearScriptCache();
					}
					if (line.startsWith("reset ")) {
						scripts.clearScriptCacheFor(line.substring(line.indexOf(" ") + 1));
					}
					if (line.startsWith("BYE")) {
						log.info("QUITTING bot via remote");
						shouldrun = false;
						log.info(s.getRemoteSocketAddress() + " closing...");
						s.close();
						break;
					}
					if (line.startsWith("say ")) {
						String[] parts = line.split(" ", 3);
						if (parts.length == 3) {
							bot.say(parts[1], parts[2]);
						}
					}
					if (line.startsWith("notice ")) {
						String[] parts = line.split(" ", 3);
						if (parts.length == 3) {
							bot.notice(parts[1], parts[2]);
						}
					}
					if (line.startsWith("join ")) {
						String[] parts = line.split(" ", 3);
						if (parts.length == 3) {
							bot.join(parts[1], parts[2]);
						}
						if (parts.length == 2) {
							bot.join(parts[1]);
						}
					}
					if (line.startsWith("part ")) {
						bot.part(line.substring(line.indexOf(" ") + 1));
					}
					if (line.startsWith("list #")) {
						StringBuilder sb = new StringBuilder(400);
						for (User u : bot.getUsers(line.substring(line.indexOf("#")))) {
							sb.append(u.toString() + ", ");
						}
						pw.write(sb.toString());
						pw.flush();
						sb = null;
					}

					if (line.startsWith("callback:")) {
						String[] parts = line.split(":", 3);
						if (parts.length == 3) {
							notifyCallbacks(parts[1], parts[2]);
						}
					}
					if (line.startsWith("status")) {
						StringBuilder ch = new StringBuilder(256);
						for (String c : bot.getChannels()) {
							ch.append(c + ",");
						}
						String status =
								"Bot nick:" + bot.getNick() +
										"Startup time:" + bot.getStartupTime() +
										"Connected time:" + bot.getConnectTime() +
										"On channels:" + ch.toString();
						pw.write(status);
						pw.flush();
					}
				}
				pw.close();
				s.close();
			} while (shouldrun);

			socket.close();
			bot.byebye();

		} catch (SocketException e) {
			System.err.println(e);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void shutDown() {
		try {
			socket.close();
			socket = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String sendCommand(String command) {
		log.entering("com.popodeus.chat.BotRemote", "sendCommand", command);
		String response = "";

		return response;
	}

	public void addCallback(String event, ScriptableObject callbackfunc) {
		log.entering("com.popodeus.chat.BotRemote", "addCallback", callbackfunc);
		//callbackfunc.
		if (!callbacks.containsKey(event)) {
			callbacks.put(event, callbackfunc);
		}
	}

	public void removeCallback(String event) {
		log.entering("com.popodeus.chat.BotRemote", "removeCallback (event)", event);
		callbacks.remove(event);
	}

	private void notifyCallbacks(String event, String data) {
		log.entering("com.popodeus.chat.BotRemote", "notifyCallbacks", new Object[]{
				event,
				data
		});
		ScriptableObject obj = callbacks.get(event);
		if (obj != null) {

		}
	}
}
