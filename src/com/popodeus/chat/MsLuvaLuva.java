package com.popodeus.chat;

import com.sun.script.javascript.RhinoScriptEngine;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.PircBot;
import org.jibble.pircbot.User;

import javax.script.Bindings;
import javax.script.CompiledScript;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Pattern;

/**
 * photodeus
 * Jul 24, 2009
 * 5:31:17 AM
 */
public class MsLuvaLuva extends PircBot implements Runnable {

	private float version = 1.3f;
	private Log log = LogFactory.getLog(MsLuvaLuva.class);
	private Thread runner;
	protected Random random;

	protected long timeout = 10 * 1000L;
	private long last_act = 0;
	private RhinoScriptEngine engine;
	private Bindings bindings;
	private Map<String, CompiledScript> scriptcache;
	private Map<Event, List<CSC>> eventscripts;
	private Map<String, Object> scriptvars;
	private Map<String, Map<String, String>> channel_users;
	public Object last_result;
	public String quitmsg = "Bye bye!";

	protected List<String> greetings;
	protected boolean silence;
	private static final String PROP_NICK = "nick";
	private static final String SCRIPTVAR_NICK = PROP_NICK;
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
	private static final String SCRIPTVAR_NO_TIMEOUT = "no_timeout";
	private static final String UTF8 = "UTF-8";
	private boolean showGreeting;

	protected ResourceBundle properties;
	private static final String PROP_GREETINGS_FILE = "greetings.file";
	private static final String PROP_JOINMSG = "joinmsg";
	private static final String PROP_QUITMSG = "quitmsg";
	private static final String PROP_VARIABLE_CACHE = "variable.cache";
	private static final String PROP_SERVER = "server";
	private static final String PROP_PASSWORD = "password";
	private static final String PROP_CHANNELS = "channels";
	private static final String PROP_SCRIPT_DIR = "script.dir";

	enum Event {
		JOIN,
		MESSAGE,
		ACTION,
		NICKCHANGE,
		RIGHTS,
		KICK,
		LEAVE,
		TOPIC,
		QUIT,
	}

	public MsLuvaLuva() {
		log.info("MsLuvaLuva v" + version + " is starting...");
		setVersion("Popodeus IRC bot http://popodeus.com/chat/bot/");
		//InputStream is = getClass().getClassLoader().getResourceAsStream("config");
		reinit();
		loadScriptVars();
		scriptcache = new HashMap<String, CompiledScript>(64);
		showGreeting = true;
		scriptvars = new HashMap<String, Object>(32);
		random = new Random();
		engine = new RhinoScriptEngine();
		bindings = new SimpleBindings();
		runner = new Thread(this);
		runner.start();
	}

	public void reinit() {
		try {
			properties = ResourceBundle.getBundle("config");
			setLogin(properties.getString(PROP_NICK));
			quitmsg = properties.getString(PROP_QUITMSG);
		} catch (Error ex) {
			log.fatal(ex, ex);
		} catch (Exception ex) {
			log.fatal(ex, ex);
		}
	}

	private void loadScriptVars() {
		String x = properties.getString(PROP_VARIABLE_CACHE);
		try {
			File f = new File(x);
			if (f.exists()) {
				ObjectInputStream oos = new ObjectInputStream(new FileInputStream(x));
				scriptvars = (Map<String, Object>) oos.readObject();
				oos.close();
			}
		} catch (Exception ex) {
			log.error("Failed to load variable cache from " + x + ". ", ex);
		}
	}

	private void saveScriptVars() {
		String x = properties.getString(PROP_VARIABLE_CACHE);
		try {
			if (x != null) {
				ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(x));
				oos.writeObject(scriptvars);
				oos.close();
			}
		} catch (Exception ex) {
			log.error("Failed to save variable cache into " + x + ". ", ex);
		}
	}


	class CSC {
		String name;
		CompiledScript csc;

		CSC(final String name, final CompiledScript csc) {
			this.name = name;
			this.csc = csc;
		}
	}

	@Override
	public void run() {
		compileEventScripts();
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

		saveScriptVars();
		System.exit(0);
	}

	public void byebye() {
		runner.interrupt();
	}

	@Override
	protected void onDisconnect() {
		log.info("Was disconnected...");
		int counter = 500;
		while (!isConnected()) {
			log.info(new Date() + " attempting reconnect");
			if (do_connect()) {
				return;
			}
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
			}
			if (--counter == 0) {
				break;
			}
		}
	}

	protected boolean do_connect() {
		try {
			channel_users = new HashMap<String, Map<String, String>>();
			setName(properties.getString(PROP_NICK));
			if (!isConnected()) {
				connect(properties.getString(PROP_SERVER));
			} else {
				reconnect();
			}
			identify(properties.getString(PROP_PASSWORD));
		} catch (NickAlreadyInUseException naius) {
			log.info("Nick MsLuvaLuva was already in use...");
			setName(properties.getString(PROP_NICK) + "_");
			try {
				if (!isConnected()) {
					connect(properties.getString(PROP_SERVER));
				} else {
					reconnect();
				}
				//identify(PASSWD);
				sendRawLine("NICKSERV GHOST " + properties.getString(PROP_NICK) + " " + properties.getString(PROP_PASSWORD));
				setName(properties.getString(PROP_NICK));
			} catch (Exception e) {
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		for (String channel : properties.getString(PROP_CHANNELS).split(",")) {
			joinChannel(channel);
		}
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
			if (showGreeting) {
				sendMessage(channel, properties.getString(PROP_JOINMSG));
			}
		}
	}

	public void reloadGreetings() {
		greetings = new ArrayList<String>(40);
		try {
			BufferedReader br = new BufferedReader(new FileReader(properties.getString(PROP_GREETINGS_FILE)));
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
		runOnEventScript(Event.TOPIC, setBy, "" + date, null, topic, channel);
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
			long time = 15000;
			if (reason.matches("\\d+")) {
				try {
					time = Integer.parseInt(Pattern.compile(".*(\\d+).*").matcher(reason).group(1));
					if (time > 0 && time <= 300) {
						time *= 1000;
					} else {
						time = 15000;
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
				sendNotice(sender, "Too fast - Timeout is " + (timeout / 1000) + "s. Try again later.");
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
				sendNotice(sender, "Too fast - Timeout is " + (timeout / 1000) + "s. Try again later.");
			}
		} else {
			runOnEventScript(Event.MESSAGE, sender, login, hostname, message, channel);
		}
	}

	@Override
	protected void onAction(final String sender, final String login, final String hostname, final String target, final String action) {
		super.onAction(sender, login, hostname, target, action);
		runOnEventScript(Event.ACTION, sender, login, hostname, action, target);
	}

	@Override
	protected void onQuit(final String sourceNick, final String sourceLogin, final String sourceHostname, final String reason) {
		super.onQuit(sourceNick, sourceLogin, sourceHostname, reason);
		runOnEventScript(Event.QUIT, sourceNick, sourceLogin, sourceHostname, reason, null);
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

	public void clearCacheForScript(final String cmd) {
		log.info("clearCacheForScript: " + cmd);
		scriptcache.remove(cmd);
	}

	protected boolean runTriggerScript(final String sender, final String login, final String hostname, final String message, final String channel, final String cmd, final String param) {
		CompiledScript csc = null;
		if (scriptcache.containsKey(cmd)) {
			log.trace("Script has compiled cache for " + cmd);
			csc = scriptcache.get(cmd);
		} else {
			File script = new File(properties.getString(PROP_SCRIPT_DIR), cmd + ".js");
			log.debug("Locating script file " + script);
			if (script.exists()) {
				FileReader reader = null;
				try {
					reader = new FileReader(script);
					log.info("Compiling " + script);
					csc = engine.compile(reader);
					scriptcache.put(cmd, csc);
				} catch (Exception e) {
					log.error(e, e);
				} catch (Error e) {
					log.error(e, e);
				} finally {
					if (reader != null) {
						try {
							reader.close();
						} catch (IOException e) { /* no op */ }
					}
				}
			} else {
				log.debug(script + " not found");
			}
		}

		if (csc != null) {
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

			bindings.remove(SCRIPTVAR_CANCEL);
			bindings.remove(SCRIPTVAR_RESPONSE);
			bindings.remove(SCRIPTVAR_RESPONSE_TO);
			bindings.remove(SCRIPTVAR_NO_TIMEOUT);
			try {
				log.trace("Evaluating " + cmd);
				csc.eval(bindings);
			} catch (ScriptException e) {
				log.error(cmd + ": " + e);
			}
			last_result = bindings.get(SCRIPTVAR_RESULT);

			Object response = bindings.get(SCRIPTVAR_RESPONSE);
			Object response_to = bindings.get(SCRIPTVAR_RESPONSE_TO);
			if (null != response && null != response_to) {
				sendMessage(response_to.toString(), response.toString());
			}
			if (bindings.get(SCRIPTVAR_NO_TIMEOUT) != null) {
				return false;
			}
			return true;
		} else {
			sendNotice(sender, "Unknown command: " + cmd);
			log.info(sender + "@" + hostname + " tried to invoke unknown command: " + cmd);
		}
		return false;
	}

	public void compileEventScripts() {
		log.info("Compiling event scripts...");
		eventscripts = new HashMap<Event, List<CSC>>(Event.values().length * 2);
		for (Event evt : Event.values()) {
			File dir = new File(properties.getString(PROP_SCRIPT_DIR), evt.name());
			if (dir.exists()) {
				File[] scripts = dir.listFiles(new FilenameFilter() {
					public boolean accept(final File dir, final String name) {
						return name.endsWith(".js");
					}
				});
				Arrays.sort(scripts);
				List<CSC> cscs = new ArrayList<CSC>(scripts.length);
				for (File script : scripts) {
					FileReader reader = null;
					try {
						reader = new FileReader(script);
						log.debug("Compiling " + script);
						cscs.add(new CSC(script.getName(), engine.compile(reader)));
					} catch (Exception e) {
						log.error(script.getName() + ": " + e);
					} finally {
						try {
							if (reader != null) {
								reader.close();
							}
						} catch (IOException e) {
						}
					}
				}
				log.info(evt + ": " + cscs.size() + " scripts");
				eventscripts.put(evt, cscs);
			}
		}
	}

	private void runOnEventScript(final Event event, final String sender, final String login, final String hostname, final String message, final String channel) {
		log.debug(sender + ", " + event);
		List<CSC> scripts = eventscripts.get(event);
		if (scripts != null) {
			for (CSC csc : scripts) {
				//bindings.clear();
				bindings.put(SCRIPTVAR_NICK, sender);
				bindings.put(SCRIPTVAR_CHANNEL, channel);
				bindings.put(SCRIPTVAR_IDENT, login);
				bindings.put(SCRIPTVAR_HOST, hostname);
				bindings.put(SCRIPTVAR_MESSAGE, message);
				bindings.put(SCRIPTVAR_BOT, this);
				bindings.put(SCRIPTVAR_LAST_RESULT, last_result);
				bindings.put(SCRIPTVAR_RESULT, new Object());

				bindings.remove(SCRIPTVAR_CANCEL);
				bindings.remove(SCRIPTVAR_RESPONSE);
				bindings.remove(SCRIPTVAR_RESPONSE_TO);
				try {
					log.trace("running: " + csc.name);
					csc.csc.eval(bindings);
				} catch (ScriptException e) {
					log.error(csc.name + ": " + e);
				}
				last_result = bindings.get(SCRIPTVAR_RESULT);

				Object response = bindings.get(SCRIPTVAR_RESPONSE);
				Object response_to = bindings.get(SCRIPTVAR_RESPONSE_TO);
				if (null != response && null != response_to) {
					sendMessage(response_to.toString(), response.toString());
				}
				if (bindings.get(SCRIPTVAR_CANCEL) != null) {
					log.debug(csc.name + ": early cancel. No processing of other scripts");
					break;
				}
			}
		} else {
			log.debug("No event scripts for " + event);
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
		log.trace("setValue: " + key + " => " + value + " (" + value.getClass().getName() + ")");
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
