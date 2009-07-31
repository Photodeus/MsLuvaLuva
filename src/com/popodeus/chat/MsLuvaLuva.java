package com.popodeus.chat;

import org.jibble.pircbot.PircBot;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.script.ScriptEngine;
import javax.script.Bindings;
import javax.script.SimpleBindings;
import javax.script.ScriptException;
import java.util.*;
import java.util.regex.Pattern;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.io.*;

import com.sun.script.javascript.RhinoScriptEngine;

/**
 * photodeus
 * Jul 24, 2009
 * 5:31:17 AM
 */
public class MsLuvaLuva extends PircBot implements Runnable {

	// TODO for now this is hardcoded, version 0.1
	protected static final String BASE_DIR = ".........";
	protected static final String SCRIPT_DIR = BASE_DIR + "scripts/";
	protected static final String GREETS_FILE = BASE_DIR + "scripts/welcome.txt";

	private float version = 1.2f;
	String SERVER = "irc.mibbit.net";
	private Log log = LogFactory.getLog(MsLuvaLuva.class);
	private Thread runner;

	protected long timeout = 10 * 1000L;
	protected static final String NICK = "MsLuvaLuva";
	private static final String PASSWD = "";
	private long last_act = 0;
	private ScriptEngine engine;
	private Bindings bindings;

	protected boolean silence;
	Map<String, Map<String, String>> channel_users;
	public String quitmsg = "Bye bye lovelies!";
	public Object last_result;
	protected List<String> greetings;
	Random random;
	private Map<String, Object> scriptvars;
	private static final String SCRIPTVAR_NICK = "nick";
	private static final String SCRIPTVAR_CHANNEL = "channel";
	private static final String SCRIPTVAR_IDENT = "ident";
	private static final String SCRIPTVAR_HOST = "host";
	private static final String SCRIPTVAR_MESSAGE = "message";
	private static final String SCRIPTVAR_PARAM = "param";
	private static final String SCRIPTVAR_BOT = "bot";
	private static final String SCRIPTVAR_LAST_RESULT = "last_result";
	private static final String SCRIPTVAR_RESULT = "result";
	private static final String SCRIPTVAR_RESPONSE = "response";
	private static final String SCRIPTVAR_RESPONSE_TO = "response_to";
	private static final String SCRIPTVAR_CANCEL = "cancel";
	private static final String UTF8 = "UTF-8";

	enum Event {
		JOIN,
		MESSAGE,
		ACTION,
		NICKCHANGE,
		RIGHTS,
		KICK,
		LEAVE,
		TOPIC,
	}

	public MsLuvaLuva() {
		log.info("MsLuvaLuva v" + version + " is starting...");
		setVersion("Popodeus IRC bot http://popodeus.com/chat/bot/");
		setLogin(NICK);
		scriptvars = new HashMap<String, Object>(32);
		random = new Random();
		engine = new RhinoScriptEngine();
		bindings = new SimpleBindings();
		runner = new Thread(this);
		runner.start();
	}

	@Override
	public void run() {
		//setVerbose(true);
		if (do_connect()) {
			while (true) {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					break;
				}
			}
			log.info("MsLuvaLuva is disconnecting...");
			quitServer(quitmsg);
			disconnect();
		}
		System.exit(0);
	}

	public void byebye() {
		runner.interrupt();
	}

	@Override
	protected void onDisconnect() {
		while (!isConnected()) {
			log.info(new Date() + " attempting reconnect");
			if (do_connect()) {
				return;
			}
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
			}
		}
	}

	protected boolean do_connect() {
		try {
			channel_users = new HashMap<String, Map<String, String>>();
			setName(NICK);
			if (!isConnected()) {
				connect(SERVER);
			} else {
				reconnect();
			}
			identify(PASSWD);
		} catch (NickAlreadyInUseException naius) {
			log.info("Nick MsLuvaLuva was already in use...");
			setName(NICK + "_");
			try {
				if (!isConnected()) {
					connect(SERVER);
				} else {
					reconnect();
				}
				//identify(PASSWD);
				sendRawLine("NICKSERV GHOST " + NICK + " " + PASSWD);
				setName(NICK);
			} catch (Exception e) {
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		joinChannel("#Popodeus");
		joinChannel("#Popmundo");
		return true;
	}

	@Override
	protected synchronized void onUserList(final String channel, final User[] users) {
		Map<String, String> u = channel_users.get(channel);
		if (u == null) {
			u = new HashMap<String, String>(users.length);
		}
		for (User user : users) {
			u.put(user.getNick(), user.getPrefix());
		}
		channel_users.put(channel, u);
	}

	@Override
	protected void onUserMode(final String targetNick, final String sourceNick, final String sourceLogin, final String sourceHostname, final String mode) {
		log.info(targetNick + " => " + mode + " (" + sourceNick + ")");
		/*
		for (User u : getUsers(channel)) {

		}
		*/
	}

	@Override
	protected void onJoin(final String channel, final String sender, final String login, final String hostname) {
		if (!getNick().equals(sender)) {
			//if (isVoice(getNick(), channel)) {
			if (greetings == null) {
				reloadGreetings();
			}
			if (greetings != null) {
				String line = greetings.get(random.nextInt(greetings.size()));
				sendMessage(channel, line.replace("$nick", sender));
			}
			//}
			runOnEventScript(Event.JOIN, sender, login, hostname, null, channel);
		} else {
			sendMessage(channel, "Hi everyone! Did you miss me?");
		}
	}

	public void reloadGreetings() {
		greetings = new ArrayList<String>(40);
		try {
			BufferedReader br = new BufferedReader(new FileReader(GREETS_FILE));
			String s;
			while ((s = br.readLine()) != null) {
				String tmp = s.trim();
				if (tmp.length() > 0) {
					greetings.add(s);
				}
			}
			br.close();
		} catch (IOException e) {
		}
	}

	@Override
	protected void onNickChange(final String oldNick, final String login, final String hostname, final String newNick) {
		log.info(oldNick + " changed into " + newNick);
		for (String channel : channel_users.keySet()) {
			Map<String, String> u = channel_users.get(channel);
			if (u.containsKey(oldNick)) {
				// Save the new nick and prefix
				u.put(newNick, u.get(oldNick));
				u.remove(oldNick);
			}
		}
		runOnEventScript(Event.NICKCHANGE, newNick, login, hostname, oldNick, null);
	}

	@Override
	protected void onMode(final String channel, final String sourceNick, final String sourceLogin, final String sourceHostname, final String mode) {
		log.info(sourceNick + " ==> " + mode);
	}

	@Override
	protected void onOp(final String channel, final String sourceNick, final String sourceLogin, final String sourceHostname, final String recipient) {
		log.info(recipient + " received op");
		Map<String, String> u = channel_users.get(channel);
		if (u != null && u.containsKey(recipient)) {
			// Save the new nick and prefix
			u.put(recipient, "@");
		}
		runOnEventScript(Event.RIGHTS, recipient, sourceLogin, sourceHostname, "+@", channel);
	}

	@Override
	protected void onDeop(final String channel, final String sourceNick, final String sourceLogin, final String sourceHostname, final String recipient) {
		log.info(recipient + " lost op");
		Map<String, String> u = channel_users.get(channel);
		if (u != null && u.containsKey(recipient)) {
			u.put(recipient, "");
		}
		runOnEventScript(Event.RIGHTS, recipient, sourceLogin, sourceHostname, "-@", channel);
	}

	@Override
	protected void onVoice(final String channel, final String sourceNick, final String sourceLogin, final String sourceHostname, final String recipient) {
		log.info(recipient + " received voice");
		Map<String, String> u = channel_users.get(channel);
		if (u != null && u.containsKey(recipient)) {
			// Save the new nick and prefix
			u.put(recipient, "+");
		}
		runOnEventScript(Event.RIGHTS, recipient, sourceLogin, sourceHostname, "++", channel);
	}

	@Override
	protected void onDeVoice(final String channel, final String sourceNick, final String sourceLogin, final String sourceHostname, final String recipient) {
		log.info(recipient + " lost voice");
		Map<String, String> u = channel_users.get(channel);
		if (u != null && u.containsKey(recipient)) {
			u.put(recipient, "");
		}
		runOnEventScript(Event.RIGHTS, recipient, sourceLogin, sourceHostname, "-+", channel);
	}

	@Override
	protected void onDeOwner(final String channel, final String sourceNick, final String sourceLogin, final String sourceHostname, final String recipient) {
		log.info(recipient + " lost owner");
		Map<String, String> u = channel_users.get(channel);
		if (u != null && u.containsKey(recipient)) {
			u.put(recipient, "");
		}
		runOnEventScript(Event.RIGHTS, recipient, sourceLogin, sourceHostname, "-~", channel);
	}

	@Override
	protected void onOwner(final String channel, final String sourceNick, final String sourceLogin, final String sourceHostname, final String recipient) {
		log.info(recipient + " received owner");
		Map<String, String> u = channel_users.get(channel);
		if (u != null && u.containsKey(recipient)) {
			u.put(recipient, "~");
		}
		runOnEventScript(Event.RIGHTS, recipient, sourceLogin, sourceHostname, "+~", channel);
	}

	@Override
	protected void onDeHalfOp(final String channel, final String sourceNick, final String sourceLogin, final String sourceHostname, final String recipient) {
		log.info(recipient + " lost halfop");
		Map<String, String> u = channel_users.get(channel);
		if (u != null && u.containsKey(recipient)) {
			u.put(recipient, "");
		}
		runOnEventScript(Event.RIGHTS, recipient, sourceLogin, sourceHostname, "-%", channel);
	}

	@Override
	protected void onHalfOp(final String channel, final String sourceNick, final String sourceLogin, final String sourceHostname, final String recipient) {
		log.info(recipient + " received halfop");
		Map<String, String> u = channel_users.get(channel);
		if (u != null && u.containsKey(recipient)) {
			u.put(recipient, "%");
		}
		runOnEventScript(Event.RIGHTS, recipient, sourceLogin, sourceHostname, "+%", channel);
	}

	@Override
	protected void onInvite(final String targetNick, final String sourceNick, final String sourceLogin, final String sourceHostname, final String channel) {
		joinChannel(channel);
	}

	@Override
	protected void onTopic(final String channel, final String topic, final String setBy, final long date, final boolean changed) {
		runOnEventScript(Event.TOPIC, setBy, ""+date, null, topic, channel);
	}


	@Override
	protected void onKick(final String channel, final String kickerNick, final String kickerLogin, final String kickerHostname, final String recipientNick, final String reason) {
		log.info("Kicked: " + recipientNick);
		if (recipientNick.equals(getNick())) {
			final MsLuvaLuva _this = this;
			TimerTask tt = new TimerTask() {
				public void run() {
					_this.joinChannel(channel);
				}
			};
			long time = 5000;
			if (reason.matches("\\d+\\s*sec(onds?)?")) {
				try {
					time = Integer.parseInt(Pattern.compile("?(\\d+)\\s?.*").matcher(reason).group(1));
					if (time > 0 && time <= 120) {
						time *= 1000;
					} else {
						time = 5000;
					}
				} catch (Exception e) {
				}
			}
			log.info("Was kicked, rejoining " + channel + " in " + time + "ms");
			Timer timer = new Timer();
			timer.schedule(tt, time);
		} else {
			Map<String, String> u = channel_users.get(channel);
			if (u != null) {
				u.remove(recipientNick);
			}
			runOnEventScript(Event.KICK, recipientNick, kickerLogin, kickerHostname, null, channel);
		}
	}

	@Override
	protected void onPrivateMessage(final String sender, final String login, final String hostname, final String message) {
		if (message.startsWith("!") && message.trim().length() > 1) {
			long delta = System.currentTimeMillis() - last_act;
			if (delta >= timeout) {
				actOnTrigger(sender, login, hostname, message, null);
				// if it takes long to react...?
				//last_act = System.currentTimeMillis();
			} else {
				sendNotice(sender, "Too fast - Timeout is " + (timeout/1000) + "s. Try again later.");
			}
		} else {
			runOnEventScript(Event.MESSAGE, sender, login, hostname, message, sender);
		}
	}

	@Override
	protected void onMessage(final String channel, final String sender, final String login, final String hostname, final String message) {
		if (message.startsWith("!") && message.trim().length() > 1) {
			boolean special = sender.startsWith("@") || sender.startsWith("~") ||
					"@".equals(getPrefix(channel, sender)) ||
					"~".equals(getPrefix(channel, sender));
			if (special) {

			} else {
				if (!isVoice(getNick(), channel)) {
					sendNotice(sender, "I've lost my voice, so I'm not able to speak!");
					return;
				}
				if (sender.toLowerCase().equals(sender)) {
					sendNotice(sender, "Sorry, I don't listen to people with improper nicknames.");
					return;
				}
			}
			/*
			if (message.startsWith("!silence ")) {
				if (isHalfOp(sender, channel) || isOp(sender, channel) || isOwner(sender, channel)) {
					silence = message.endsWith(" on");
					sendMessage(channel, silence?
							"Silencing is ON. Bot commands can be reactivated by an op with !silence off":
							"Silencing is OFF");
					return;
				} else {
					sendNotice(sender, "Not enough privileges. Need to be voice or op");
				}
			}

			if (silence) {
				if (isNormal(sender, channel)) {
					sendNotice(sender, "Bot has been silenced by an op. All commands are ignored.");
					return;
				}
			}
			*/
			long delta = System.currentTimeMillis() - last_act;
			if (special || delta >= timeout) {
				actOnTrigger(sender, login, hostname, message, channel);
			} else {
				//sendNotice(sender, "Too fast - Try again later.");
				sendNotice(sender, "Too fast - Timeout is " + (timeout/1000) + "s. Try again later.");
			}
		} else {
			runOnEventScript(Event.MESSAGE, sender, login, hostname, message, channel);
		}
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(final long timeout) {
		this.timeout = timeout;
	}

	public boolean isNormal(final String sender, final String channel) {
		return channel_users.containsKey(channel) &&
				channel_users.get(channel).containsKey(sender) &&
				channel_users.get(channel).get(sender).matches("^[a-ZA-Z]+.+$");
	}

	public boolean isVoice(final String sender, final String channel) {
		return channel_users.containsKey(channel) &&
				channel_users.get(channel).containsKey(sender) &&
				channel_users.get(channel).get(sender).startsWith("+");
	}

	public boolean isHalfOp(final String sender, final String channel) {
		return channel_users.containsKey(channel) &&
				channel_users.get(channel).containsKey(sender) &&
				channel_users.get(channel).get(sender).startsWith("%");
	}

	public boolean isOp(final String sender, final String channel) {
		return channel_users.containsKey(channel) &&
				channel_users.get(channel).containsKey(sender) &&
				channel_users.get(channel).get(sender).startsWith("@");
	}

	public boolean isOwner(final String sender, final String channel) {
		return channel_users.containsKey(channel) &&
				channel_users.get(channel).containsKey(sender) &&
				channel_users.get(channel).get(sender).startsWith("~");
	}

	public String getPrefix(final String channel, final String nick) {
		return (channel_users.containsKey(channel) &&
				channel_users.get(channel).containsKey(nick) ?
				channel_users.get(channel).get(nick) : null);
	}

	private boolean actOnTrigger(final String sender, final String login, final String hostname, final String message, final String _channel) {
		String channel = _channel;
		if (_channel == null) {
			channel = sender;
		}
		String[] tmp = message.split(" ", 2);
		String cmd = tmp[0].substring(1);
		String param;
		if (tmp.length == 1) {
			param = sender;
		} else {
			param = tmp[1].trim();
		}

		/*
		if ("who".equals(cmd) && param.length() > 0) {
			cmdWho(channel, param, "q");
			return true;
		} else if ("asl".equals(cmd) && param.length() > 0) {
			cmdWho(channel, param, "asl");
			return true;
		} else if ("assets".equals(cmd) && param.length() > 0) {
			cmdWho(channel, param, "a");
			return true;
		}
		*/
		boolean retval = false;
		if (cmd.matches("[a-z_]+")) {
			retval = runTriggerScript(sender, login, hostname, message, channel, cmd, param);
		}
		if (retval) {
			last_act = System.currentTimeMillis();
		}
		return retval;
	}

	private boolean runTriggerScript(final String sender, final String login, final String hostname, final String message, final String channel, final String cmd, final String param) {
		File script = new File(SCRIPT_DIR, cmd + ".js");
		if (script.exists()) {
			FileReader reader = null;
			try {
				reader = new FileReader(script);
				//bindings.clear();
				bindings.put(SCRIPTVAR_NICK, sender);
				bindings.put(SCRIPTVAR_CHANNEL, channel);
				bindings.put(SCRIPTVAR_IDENT, login);
				bindings.put(SCRIPTVAR_HOST, hostname);
				bindings.put(SCRIPTVAR_MESSAGE, message);
				bindings.put(SCRIPTVAR_PARAM, param);
				bindings.put(SCRIPTVAR_BOT, this);
				bindings.put(SCRIPTVAR_LAST_RESULT, last_result);
				bindings.put(SCRIPTVAR_RESULT, new Object());

				bindings.remove(SCRIPTVAR_RESPONSE);
				bindings.remove(SCRIPTVAR_RESPONSE_TO);
				engine.eval(reader, bindings);
				last_result = bindings.get(SCRIPTVAR_RESULT);

				Object response = bindings.get(SCRIPTVAR_RESPONSE);
				Object response_to = bindings.get(SCRIPTVAR_RESPONSE_TO);
				if (null != response && null != response_to) {
					sendMessage(response_to.toString(), response.toString());
				}
			} catch (ScriptException e) {
				log.error(e);
			} catch (IOException e) {
				log.error(e);
			} finally {
				try {
					if (reader != null) {
						reader.close();
					}
				} catch (IOException e) {
				}
			}
			return true;
		} else {
			sendNotice(sender, "Unknown command");
			log.info(script + " does not exist");
		}
		return false;
	}

	private void runOnEventScript(final Event event, final String sender, final String login, final String hostname, final String message, final String channel) {
		File dir = new File(SCRIPT_DIR, event.name());
		if (!dir.exists()) {
			log.debug(dir + " not found.");
		} else {
			File[] scripts = dir.listFiles();
			Arrays.sort(scripts);
			for (File script : scripts) {
				FileReader reader = null;
				try {
					reader = new FileReader(script);
					//bindings.clear();
					bindings.put(SCRIPTVAR_NICK, sender);
					bindings.put(SCRIPTVAR_CHANNEL, channel);
					bindings.put(SCRIPTVAR_IDENT, login);
					bindings.put(SCRIPTVAR_HOST, hostname);
					bindings.put(SCRIPTVAR_MESSAGE, message);
					bindings.put(SCRIPTVAR_BOT, this);
					bindings.put(SCRIPTVAR_LAST_RESULT, last_result);
					bindings.put(SCRIPTVAR_RESULT, "");

					bindings.remove(SCRIPTVAR_RESPONSE);
					bindings.remove(SCRIPTVAR_RESPONSE_TO);
					engine.eval(reader, bindings);
					last_result = bindings.get(SCRIPTVAR_RESULT);

					Object response = bindings.get(SCRIPTVAR_RESPONSE);
					Object response_to = bindings.get(SCRIPTVAR_RESPONSE_TO);
					if (null != response && null != response_to) {
						sendMessage(response_to.toString(), response.toString());
					}
					if (bindings.get(SCRIPTVAR_CANCEL) != null) {
						log.debug("Early cancel from "+ event + "/" + script);
						// early cancel of all scripts for this event
						break;
					}
				} catch (ScriptException e) {
					log.error(e);
				} catch (IOException e) {
					log.error(e);
				} finally {
					try {
						if (reader != null) {
							reader.close();
						}
					} catch (IOException e) {
					}
				}
			}
		}
	}

	public String fetchUrl(String _url) {
		String line = null;
		try {
			log.info("fetchUrl: " + _url);
			URL url = new URL(_url);
			HttpURLConnection hurl = (HttpURLConnection) url.openConnection();
			hurl.connect();
			BufferedReader br = new BufferedReader(new InputStreamReader(hurl.getInputStream()));
			line = br.readLine();
			hurl.disconnect();
		} catch (IOException e) {
			log.warn(_url + " => " + e.toString());
		}
		return line;
	}

	public Log getLog() {
		return log;
	}

	public String encode(final String param) {
		try {
			return URLEncoder.encode(param, UTF8);
		} catch (UnsupportedEncodingException e) {
			return param;
		}
	}

	public void setValue(String key, Object value) {
		scriptvars.put(key, value);
	}

	public Object getValue(String key) {
		return scriptvars.get(key);
	}

	public static void main(String[] args) {
		new MsLuvaLuva();
		/*
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			br.readLine();
		} catch (IOException e) {
		}
		luva.byebye();
		*/
	}
}
