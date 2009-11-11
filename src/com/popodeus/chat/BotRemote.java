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
	private ScriptManager scriptmanager;

	public BotRemote(final int port, BotCallbackAPI bot, final ScriptManager scriptmanager) throws IOException {
		log.entering("BotRemote", "constructor", port);
		this.bot = bot;
		this.scriptmanager = scriptmanager;
		InetAddress ipaddr = InetAddress.getLocalHost();
		this.socket = new ServerSocket(port, maxconnections, ipaddr);
		// socket.setSoTimeout(0); // wait infinitely
		this.callbacks = new HashMap<String, ScriptableObject>();
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
				printConsole(pw, "Welcome to MsLuvaLuva. Type 'help' for commands.");
				String line;
				while ((line = br.readLine()) != null) {
					log.info(s.getRemoteSocketAddress() + ": " + line);
					if (line.startsWith("help")) {
						printConsole(pw, "ping");
						printConsole(pw, "quit");
						printConsole(pw, "reset [scriptname]");
						printConsole(pw, "run scriptname[.js] [params]");
						printConsole(pw, "!scriptname [#channel] [params]");

						printConsole(pw, "say <#channel|nick> message");
						printConsole(pw, "notice <#channel|nick> message");
						printConsole(pw, "join #channel");
						printConsole(pw, "part #channel");
						printConsole(pw, "list #channel");
						printConsole(pw, "status");

						printConsole(pw, "BYE   (be careful with this one!)");
					}
					if (line.startsWith("ping")) {
						printConsole(pw, "PONG");
					}
					if (line.startsWith("quit")) {
						printConsole(pw, "Bye bye baby!");
						s.close();
						break;
					}
					if (line.startsWith("!") && line.contains(" ")) {
						// !who #Popodeus 12345 => Prints out value to #Popmundo
						// !who 12345 => should return value to telnet. Right now won't work...
						String target, msg, cmd, param;
						int idx = line.indexOf(" ");
						int idx2 = line.indexOf(" ", idx);
						cmd = line.substring(1, idx);
						target = line.substring(idx+1, idx2);
						param = line.substring(idx2+1);
						scriptmanager.runTriggerScript(
								bot, "__telnet", "telnet", "127.0.0.1",
								line,
								target,
								cmd,
								param
						);
					}
					if (line.startsWith("reset")) {
						printConsole(pw, "Clearing cache for all scripts");
						scriptmanager.compileEventScripts();
						scriptmanager.clearTriggerSciptCache();
					}
					if (line.startsWith("reset ")) {
						String name = line.substring(line.indexOf(" ") + 1);
						printConsole(pw, "Clearing cache for " + name);
						scriptmanager.compileTriggerScript(name);
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
							printConsole(pw, "Saying to " + parts[1] + ": " + parts[2]);
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
						String chan = line.substring(line.indexOf(" ") + 1);
						printConsole(pw, "Leaving " + chan);
						bot.part(chan);
					}
					if (line.startsWith("list #")) {
						StringBuilder sb = new StringBuilder(400);
						for (User u : bot.getUsers(line.substring(line.indexOf("#")))) {
							sb.append(u.toString() + ", ");
						}
						printConsole(pw, sb.toString());
						sb = null;
					}

					if (line.startsWith("callback:")) {
						String[] parts = line.split(":", 3);
						if (parts.length == 3) {
							printConsole(pw, "Callback called for " + parts[1] + " with " + parts[2]);
							notifyCallbacks(parts[1], parts[2]);
						}
					}
					if (line.startsWith("status")) {
						StringBuilder ch = new StringBuilder(256);
						for (String c : bot.getChannels()) {
							ch.append(c + ",");
						}

						printConsole(pw, "Bot nick:" + bot.getNick() +
								"Startup time:" + bot.getStartupTime() +
								"Connected time:" + bot.getConnectTime() +
								"On channels:" + ch.toString());
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
	public void printConsole(final PrintWriter pw, final String message) {
		pw.println(">>> " + message);
		pw.flush();
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
			// TODO so uh... there should be an interface to call here
			// nothing is implemented yet
		}
	}
}
