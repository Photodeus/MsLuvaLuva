package com.popodeus.chat;

import com.gargoylesoftware.htmlunit.*;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.PircBot;
import org.jibble.pircbot.User;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Our own little amusing infobot. Meet MsLuvaLuva!
 * <br/>By photodeus
 * @since Jul 24, 2009 5:31:17 AM GMT+1
 */
public class MsLuvaLuva extends PircBot implements Runnable, BotCallbackAPI {
	
	private float version = 3.4f;
	private Logger log = Logger.getLogger("com.popodeus.chat.MsLuvaLuva");
	private Thread runner;
	protected Random random;

	private ScriptManager scriptmanager;

	private ChatLogger logger;
	private BotRemote remote;

	private String quitmsg = "Bye bye!";
	private long startupTime, connectTime;
	private boolean rejoinmessageEnabled = false;

	protected List<String> greetings;
	protected boolean silence;

	protected ResourceBundle messages;
	protected ResourceBundle properties;
	private WebClient client;
	private AtomicBoolean httplock = new AtomicBoolean(false); 

	public MsLuvaLuva() throws Exception {
		startupTime = System.currentTimeMillis();
		log.info("MsLuvaLuva v" + version + " is starting...");
		random = new Random();

		reinitialize();

		scriptmanager = new ScriptManager(
				this,
				logger, new File(properties.getString(Config.SCRIPT_DIR)),
				new File(properties.getString(Config.VARIABLE_CACHE_DIR))
		);

		runner = new Thread(this);
		runner.start();
	}

	public boolean byebye() {
		if (remote != null) remote.shutDown();
		runner.interrupt();
		return true;
	}

	public void reinitialize() {
		try {
			log.info("MsLuvaLuva reinitialize");
			System.out.println("Free memory: " + java.lang.Runtime.getRuntime().freeMemory());
			System.out.println(" Max memory: " + java.lang.Runtime.getRuntime().maxMemory());
			messages = ResourceBundle.getBundle("messages");
			log.info(messages.getString("loaded.translated.strings"));
			properties = ResourceBundle.getBundle("config");
			setLogin(properties.getString(Config.NICK));
			setVersion(properties.getString(Config.VERSION_STRING));
			quitmsg = properties.getString(Config.QUITMSG);
			rejoinmessageEnabled = Boolean.parseBoolean(properties.getString(Config.REJOINMSG_ENABLED));

			String telnet = properties.getString(Config.TELNET_PORT);
			if (telnet != null) {
				int listenport = Integer.parseInt(telnet);
				if (listenport > 1024) {
					if (remote != null) {
						remote.shutDown();
					}
					remote = new BotRemote(listenport, this, scriptmanager);
				}
			}
			
			// Do logger reinit last
			if (logger != null) logger.closeAll();
			logger = new ChatLogger(new File(properties.getString(Config.LOG_DIR)));
		} catch (Error ex) {
			log.log(Level.SEVERE, ex.getMessage(), ex);
		} catch (Exception ex) {
			log.log(Level.SEVERE, ex.getMessage(), ex);
		}
	}

	public void run() {
		boolean keeprunning = true;
		if (keeprunning && do_connect()) {
			while (keeprunning) {
				try {
					// Check interruption status every 5 seconds
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					keeprunning = false;
					break;
				}
			}
			log.info("MsLuvaLuva is disconnecting...");
			logger.closeAll();
			quitServer(quitmsg);
		}
		scriptmanager.saveScriptVars();
		System.exit(0);
	}

	@Override
	protected void onDisconnect() {
		log.info(messages.getString("was.disconnected"));
		logger.logAction(null, null, messages.getString("disconnected.from.server"));
		int counter = 500;
		while (!isConnected()) {
			log.info(new Date() + " attempting reconnect");
			logger.logAction(null, null, MessageFormat.format(messages.getString("attempting.to.connect.to.server"), counter), true);
			if (do_connect()) {
				if (rejoinmessageEnabled) {
					String rejoinmsg = properties.getString(Config.REJOINMSG);
					if (rejoinmsg != null && rejoinmsg.length() > 0) {
						for (String channel : getChannels()) {
							say(channel, rejoinmsg);
						}
					}
				}
				return;
			}
			try {
				long sleep = 30000 + random.nextInt(30) * 1000;
				log.fine(MessageFormat.format(messages.getString("sleeping"), sleep));
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
			}
			if (--counter == 0) {
				break;
			}
		}
	}

	protected boolean do_connect() {
		String basenick = properties.getString(Config.NICK);
		String altnick = properties.getString(Config.ALTNICK);
		// Default altnick is nick_
		if (altnick == null) {
			altnick = basenick + "_";
		}
		try {
			//channel_users = new HashMap<String, Set<User>>();
			setName(basenick);
			if (!isConnected()) {
				connect(properties.getString(Config.SERVER));
			} else {
				reconnect();
			}
			// Tell nickserv our password
			identify(properties.getString(Config.PASSWORD));
		} catch (NickAlreadyInUseException naius) {
			log.info(MessageFormat.format(messages.getString("nick.was.already.in.use"), basenick));
			setName(altnick);
			try {
				if (!isConnected()) {
					connect(properties.getString(Config.SERVER));
				} else {
					reconnect();
				}
			} catch (Exception e) {
				System.err.println(e);
				return false;
			}
			// Kill anyone using our nick
			sendRawLine(MessageFormat.format(
					properties.getString("nickserv.ghost.command"), // use a configurable command
					basenick, // first parameter is nick 
					properties.getString(Config.PASSWORD) // second one is the password 
			));
			setName(basenick);
		} catch (java.net.UnknownHostException e) {
			System.err.println(e);
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		connectTime = System.currentTimeMillis();
		for (String channel : properties.getString(Config.CHANNELS).split(",")) {
			join(channel);
		}
		log.info("Setting mode +B");
		setMode(getNick(), "+B");
		return true;
	}

	@Override
	protected synchronized void onUserList(final String channel, final User[] users) {
		StringBuilder line = new StringBuilder(400);
		for (User u : users) {
			line.append(u.toString() + ", ");
		}
		logger.logAction(channel, null, "User list for " + channel + ": " + line.toString());
		/*
		Set<User> u = new TreeSet<User>();
		u.addAll(Arrays.asList(users));
		channel_users.put(channel, u);
		*/
	}

	@Override
	protected void onUserMode(final String targetNick, final String sourceNick, final String sourceLogin, final String sourceHostname, final String mode) {
		log.info(targetNick + " => " + mode + " (" + sourceNick + ")");
	}

	@Override
	protected void onJoin(final String channel, final String sender, final String login, final String hostname) {
		//System.out.println("============================================");
		//System.out.println("ONJOIN : " + channel + " :: " + sender + " ... " + getNick());
		final boolean isBotSender = getNick().equalsIgnoreCase(sender);
		
		if (isBotSender) {
			try {
				logger.joinChannel(channel);
			} catch (Exception e) {
				log.warning(e.toString());
			}
		} 
		
		//if (properties.getString(allow.join.events.bot))
		if (!isBotSender) {
			log.fine("onJoin: " + sender + "!" + login +  "@" + hostname + " => " + channel);
			logger.logAction(channel, sender, sender + " [" + login + "@" + hostname + "] has joined " + channel);
			scriptmanager.runOnEventScript(this, Event.JOIN, sender, login, hostname, null, channel);
		}
	}

	/**
	 * TODO this should be moved to {@link com.popodeus.chat.ScriptManager}
	 * @see #reloadGreetings() 
	 * @return A random greeting from the greetings array, stored in memory
	 */
	public String getGreeting() {
		if (greetings == null) {
			reloadGreetings();
		}
		if (greetings != null) {
			return greetings.get(random.nextInt(greetings.size()));
		}
		return messages.getString("hi.nick");
	}

	public void channelBan(final String channel, final String hostmask) {
		logger.logAction(channel, null, "Trying to set ban on host " + hostmask, true);
		ban(channel, hostmask);
	}

	public void channelUnban(final String channel, final String hostmask) {
		logger.logAction(channel, null, "Trying to remove ban from host " + hostmask, true);
		unBan(channel, hostmask);
	}

	/**
	 * Invites someone to a channel.
	 * Might not work unless bot is op
	 */
	public void invite(final String nick, final String channel) {
		logger.logAction(channel, null, "Inviting " + nick);
		sendInvite(nick, channel);
	}

	/**
	 * Loads the welcome greetings file and stores it in memory.
	 */
	public void reloadGreetings() {
		greetings = new ArrayList<String>(100);
		try {
			BufferedReader br = new BufferedReader(new FileReader(properties.getString(Config.GREETINGS_FILE)));
			String s;
			while ((s = br.readLine()) != null) {
				String tmp = s.trim();
				if (tmp.length() > 0) {
					greetings.add(s);
				}
			}
			br.close();
		} catch (Exception e) {
			log.warning(e.toString());
		}
	}

	@Override
	protected void onNickChange(final String oldNick, final String login, final String hostname, final String newNick) {
		log.info(oldNick + " is now known as " + newNick);
		for (String channel : getChannels()) {
			if (isNickInChannel(channel, oldNick) || isNickInChannel(channel,  newNick)) {
				logger.logAction(null, oldNick, oldNick + " is now known as " + newNick, true);
				logger.nickChange(oldNick, newNick);
			}
		}
		/*
		User olduser = new User(User.Prefix.NONE, oldNick);
		for (String channel : channel_users.keySet()) {
			Set<User> u = channel_users.get(channel);
			if (u.contains(olduser)) {
				// Save the new nick and prefix
				u.remove(olduser);
				u.add(new User(olduser.getPrefix(), newNick));
			}
		}
		*/
		scriptmanager.runOnEventScript(this, Event.NICKCHANGE, newNick, login, hostname, oldNick, null);
	}

	@Override
	protected void onMode(final String channel, final String sourceNick, final String sourceLogin, final String sourceHostname, final String mode) {
		log.info(sourceNick + "!" + sourceLogin + "@" + sourceHostname + " ==> " + channel + " " + mode);
		logger.logAction(channel, sourceNick, "mode/"+channel + " [" + mode + " " + sourceNick + "]");
		scriptmanager.runOnEventScript(this, Event.RIGHTS, mode, sourceLogin, sourceHostname, mode, channel);
		/*
		if (mode.startsWith("+v")) {
			String[] r = mode.split(" ");
			for (int i = 1; i < r.length; i++) {
				channel_users.get(channel).put(r[i].toLowerCase(), "+");
			}
		}
		if (mode.startsWith("+o")) {
			String[] r = mode.split(" ");
			for (int i = 1; i < r.length; i++) {
				channel_users.get(channel).put(r[i].toLowerCase(), "@");
			}
		}
		if (mode.startsWith("+q")) {
			String[] r = mode.split(" ");
			for (int i = 1; i < r.length; i++) {
				channel_users.get(channel).put(r[i].toLowerCase(), "~");
			}
		}
		if (mode.startsWith("+h")) {
			String[] r = mode.split(" ");
			for (int i = 1; i < r.length; i++) {
				channel_users.get(channel).put(r[i].toLowerCase(), "%");
			}
		}
		if (mode.startsWith("+a")) {
			// Protect
			String[] r = mode.split(" ");
			for (int i = 1; i < r.length; i++) {
				channel_users.get(channel).put(r[i].toLowerCase(), "%");
			}
		}
		*/
	}

	@Override
	protected void onInvite(final String targetNick, final String sourceNick, final String sourceLogin, final String sourceHostname, final String channel) {
		String[] channels = properties.getString(Config.CHANNELS).toLowerCase().split(",");
		Arrays.sort(channels);
		if (Arrays.binarySearch(channels, channel.toLowerCase()) >= 0) {
			join(channel);
		} else {
			sendNotice(sourceNick, "I'm not supposed to be on that channel.");
		}
	}

	@Override
	protected void onTopic(final String channel, final String topic, final String setBy, final long date, final boolean changed) {
		logger.logAction(channel, setBy, setBy + " sets topic to: " + topic);
		scriptmanager.runOnEventScript(this, Event.TOPIC, setBy, "" + date, null, topic, channel);
	}


	@Override
	protected void onKick(final String channel, final String kickerNick, final String kickerLogin, final String kickerHostname, final String recipientNick, final String reason) {
		logger.logAction(channel, kickerNick, recipientNick + " was kicked from " + channel + " by " + kickerNick + " ["+reason+"]");
		log.fine("Kicked: " + recipientNick);
		if (recipientNick.equals(getNick())) {
			final MsLuvaLuva _this = this;
			TimerTask tt = new TimerTask() {
				public void run() {
					_this.join(channel);
				}
			};
			final int KICKOUT_TIME = 15000;
			long time = KICKOUT_TIME;
			if (reason.matches(".*(\\d+).*")) {
				try {
					final Matcher m = Pattern.compile("(\\d+)").matcher(reason);
					m.find();
					time = Integer.parseInt(m.group(1));
					if (time > 0 && time <= 300) {
						time *= 1000;
					} else {
						time = KICKOUT_TIME;
					}
				} catch (Exception e) {
				}
			}
			log.info("Was kicked, rejoining " + channel + " in " + time + "ms");
			Timer timer = new Timer();
			timer.schedule(tt, time);
		} else {
			/*
			Set<User> u = channel_users.get(channel);
			if (u != null) {
				u.remove(new User(User.Prefix.NONE, recipientNick));
			}
			*/
			scriptmanager.runOnEventScript(this, Event.KICK, recipientNick, kickerLogin, kickerHostname, null, channel);
		}
	}

	@Override
	protected void onPrivateMessage(final String sender, final String login, final String hostname, final String message) {
		if (message.startsWith(properties.getString("trigger")) && message.trim().length() > 1) {
			if (scriptmanager.getAPI(null).isIgnored(sender, login, hostname)) {
				sendNotice(sender, messages.getString("reply.you.are.on.bot.ignore.list"));
				return;
			}
			String[] tmp = message.split(" ", 2);
			String cmd = tmp[0].substring(1);

			actOnTrigger(sender, login, hostname, message, null);
		} else {
			scriptmanager.runOnEventScript(this, Event.MESSAGE, sender, login, hostname, message, sender);
		}
	}

	@Override
	protected void onMessage(final String channel, final String sender, final String login, final String hostname, final String message) {
		logger.log(channel, sender, message, true);
		if (message.startsWith(properties.getString("trigger")) && message.trim().length() > 1) {
			boolean special = isOp(channel, sender) || isOwner(channel,  sender) || isAdmin(channel, sender);
			/* sender.startsWith("@") || sender.startsWith("~") ||
					"@".equals(getPrefix(channel, sender)) ||
					"~".equals(getPrefix(channel, sender)); */
			if (special) {

			} else {
				if (!hasVoice(channel, getNick()) || !isOp(channel, getNick()) || !isHalfOp(channel, getNick())) {
					sendNotice(sender, MessageFormat.format(messages.getString("without.a.voice"), getNick(), message));
					return;
				}
				if (sender.toLowerCase().equals(sender)) {
					sendNotice(sender, messages.getString("listen.improper.lowercase.nicknames"));
					return;
				}
			}
			actOnTrigger(sender, login, hostname, message, channel);
		} else {
			scriptmanager.runOnEventScript(this, Event.MESSAGE, sender, login, hostname, message, channel);
		}
	}

	@Override
	protected void onAction(final String sender, final String login, final String hostname, final String target, final String action) {
		logger.logAction(target, sender, "* " + sender + " " + action, true);
		super.onAction(sender, login, hostname, target, action);
		scriptmanager.runOnEventScript(this, Event.ACTION, sender, login, hostname, action, target);
	}

	@Override
	protected void onQuit(final String sourceNick, final String sourceLogin, final String sourceHostname, final String reason) {
		logger.logAction(null, sourceNick, sourceNick + " [" + sourceLogin + "@" + sourceHostname + "] has quit [" + reason + "]");
		super.onQuit(sourceNick, sourceLogin, sourceHostname, reason);
		scriptmanager.runOnEventScript(this, Event.QUIT, sourceNick, sourceLogin, sourceHostname, reason, null);
	}

	@Override
	protected void onPart(final String channel, final String sender, final String login, final String hostname, final String reason) {
		logger.logAction(null, sender, sender + " [" + login + "@" + hostname + "] has left " + channel + " [" + reason + "]");
		scriptmanager.runOnEventScript(this, Event.LEAVE, sender, login, hostname, reason, channel);
	}

	@Override
	protected void onUnknown(final String line) {
		log.info(line);
		super.onUnknown(line);
		scriptmanager.runOnEventScript(this, Event.UNSUPPORTED, null, null, null, line, null);
	}

	@Override
	protected void onServerResponse(final int code, final String response) {
		log.info("Server response: " + code + ": " + response);
	}

	
	
	public void join(final String channel) {
		joinChannel(channel);
	}

	public void join(final String channel, final String key) {
		joinChannel(channel, key);
	}

	public void part(final String channel) {
		partChannel(channel);
	}

	public final void say(String target, String message) {
		if (target.startsWith("#")) {
			logger.log(target, getNick(), message, true);
		}
		sendMessage(target, message);
	}

	public final void act(String target, String message) {
		if (target.startsWith("#")) {
			logger.log(target, getNick(), message, true);
		}
		sendAction(target, message);
	}

	public final void notice(String target, String message) {
		if (target.startsWith("#")) {
			logger.log(target, getNick(), message, true);
		}
		sendNotice(target, message);
	}

	public long getStartupTime() {
		return startupTime;
	}

	public long getConnectTime() {
		return connectTime;
	}

	public boolean isNormal(final String channel, final String nick) {
		for (User user : getUsers(channel)) {
			if (user.equals(nick)) {
				return user.getPrefix().isEmpty();
			}
		}
		return false;
	}

	public boolean hasVoice(final String channel, final String nick) {
		User[] users = getUsers(channel);
		for (User user : users) {
			if (user.equals(nick)) {
				return user.isVoice();
			}
		}
		return false;
	}

	public boolean isHalfOp(final String channel, final String nick) {
		for (User user : getUsers(channel)) {
			if (user.equals(nick)) {
				return user.isHalfOp();
			}
		}
		return false;
	}

	public boolean isOp(final String channel, final String nick) {
		for (User user : getUsers(channel)) {
			if (user.equals(nick)) {
				return user.isOp();
			}
		}
		return false;
	}

	public boolean isAdmin(final String channel, final String nick) {
		for (User user : getUsers(channel)) {
			if (user.equals(nick)) {
				return user.isAdmin();
			}
		}
		return false;
	}

	public boolean isOwner(final String channel, final String nick) {
		for (User user : getUsers(channel)) {
			if (user.equals(nick)) {
				return user.isOwner();
			}
		}
		return false;
	}

	public String getPrefix(final String channel, final String nick) {
		for (User user : getUsers(channel)) {
			if (user.equals(nick)) {
				StringBuilder sb = new StringBuilder(4);
				for (User.Prefix prefix : user.getPrefix()) {
					sb.append(prefix.toString());
				}
				return sb.toString();
			}
		}
		return "";
	}

	private boolean actOnTrigger(final String sender, final String login, final String hostname, final String message, final String _channel) {
		boolean retval = false;
		String channel = _channel;
		if (_channel == null) {
			channel = sender;
		}
		String[] tmp = message.split(" ", 2);
		String cmd = tmp[0].substring(properties.getString("trigger").length());
		// make sure it's a valid ASCII string
		if (cmd.matches("[0-9a-zA-Z_]+")) {
			String param;
			if (tmp.length == 1) {
				param = sender;
			} else {
				param = tmp[1].trim();
			}
			retval = scriptmanager.runTriggerScript(this,
					sender, login, hostname, message, channel, cmd, param);
		}
		return retval;
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
			log.warning(_url + " => " + e.toString());
		}
		return line;
	}

	public Page getPage(final String url, final int timeout) {
		log.info("getPage: " + url);
		Page retval = null;
		if (httplock.compareAndSet(false, true)) {
			try {
				if (client == null) {
					log.finest("new WebClient()");
					client = new WebClient(BrowserVersion.INTERNET_EXPLORER_7);
					client.addRequestHeader("Accept-Charset", "UTF-8");
					client.addRequestHeader("Accept-Language", "en-US");
					client.setJavaScriptEnabled(false);
					CookieManager cookiemanager = new CookieManager();
					client.setCookieManager(cookiemanager);
				}
				client.setTimeout(timeout <= 10000 ? timeout : 10000);
				//cookiemanager.clearCookies();
				log.finest("new WebRequestSettings()");
				WebRequestSettings settings = new WebRequestSettings(new URL(url));
				settings.setCharset("UTF-8");
				retval = client.getPage(settings);
				log.finer(url + " => " + retval);
			} catch (Exception e) {
				log.log(Level.INFO, "...exiting", e);
				//log.log(Level.SEVERE, "...exiting", e);
			}
			httplock.set(false);
		}
		return retval;
	}

	public void setBotNick(final String newnick) {
		super.changeNick(newnick);
	}

	public String getDefaultNick() {
		return properties.getString(Config.NICK);
	}
	public String getAltNick() {
		return properties.getString(Config.ALTNICK);
	}
	
	public ResourceBundle getMessges() {
		return messages;
	}

	public ResourceBundle getConfig() {
		return properties;
	}

	public static void main(String[] args) {
		try {
			new MsLuvaLuva();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
