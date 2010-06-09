package com.popodeus.chat;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.xml.XmlPage;
import org.jibble.pircbot.User;
import org.w3c.dom.Element;

import javax.script.Bindings;
import javax.script.SimpleBindings;
import java.io.*;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Loads and runs scripts that act as commands
 * photodeus
 * Sep 2, 2009
 */
public class ScriptManager {
	private static Logger log = Logger.getLogger("com.popodeus.chat.ScriptManager");

	public static final String UTF8 = "UTF-8";
	public static final int DEFAULT_HTTP_TIMEOUT = 8000;

	private final Set<String> ignoredNicks;
	private final Map<String, TriggerScript> scriptcache;
	private final Map<Event, List<EventScript>> eventscriptcache;
	private final Bindings globalBindings;

	private BotCallbackAPI bot;
	private ChatLogger logger;
	private int DEFAULT_IGNORE_TIME = 10 * 60; // 10 hours
	private File scriptDir;
	private File variableCacheDir;
	private SecurityManager security;

	public ScriptManager(final BotCallbackAPI bot, final ChatLogger logger, final File scriptDir, final File scriptVariableDir) {
		this.bot = bot;
		this.logger = logger;
		this.scriptcache = new HashMap<String, TriggerScript>(64);
		this.eventscriptcache = new HashMap<Event, List<EventScript>>(Event.values().length*2);
		this.globalBindings = new SimpleBindings();
		this.ignoredNicks = new HashSet<String>(10);
		this.scriptDir = scriptDir;
		this.variableCacheDir = scriptVariableDir;
		//ContextFactory.initGlobal(new SandboxContextFactory());
		compileEventScripts();
		loadScriptVars();
	}

	public void loadScriptVars() {
		log.entering(getClass().getName(), "loadScriptVars", variableCacheDir);
		File[] fs = variableCacheDir.listFiles(new FileFilter() {
			public boolean accept(final File file) {
				return file.getName().endsWith(".dat") && file.length() > 100;
			}
		});
		if (fs != null && fs.length > 0) {
			log.info(variableCacheDir + " contains " + fs.length + " valid files");
			Arrays.sort(fs, new Comparator<File>() {
				public int compare(final File file1, final File file2) {
					// Sort by actual file date, even though file name sort should do it too
					return (int) Math.signum(file1.lastModified()-file2.lastModified());
				}
			});
			// Just pick the last element, since it's the one with most recent date
			File f = fs[fs.length-1];
			log.info("Loading script variables from " + f);
			ObjectInputStream ois = null;
			try {
				ois = new ObjectInputStream(new FileInputStream(f));
				while (ois.available() > 0) {
					String key = null;
					try {
						key = ois.readUTF();
						Object value;
						value = ois.readObject();
						log.info("\t" + key + " => " + value + " ("+value.getClass()+")");
						if (key.endsWith(".time")) {
							// special handling?
						}
						//log.finest(key + ": " + value);
						globalBindings.put(key, value);
						key = null;
					} catch (Exception ex) {
						log.warning("Bad value: " + (key != null ? key + " :" : "") + ex.toString());
					}
				}
			} catch (Exception e) {
				System.err.println(e);
				e.printStackTrace();
			} finally {
				if (ois != null) {
					try {
						ois.close();
					} catch (IOException e) {
					}
				}
			}
		} else {
			log.warning("Could not find any saved script variables in " + variableCacheDir);
		}
	}

	public void saveScriptVars() {
		log.entering(getClass().getName(), "saveScriptVars", variableCacheDir);
		File f = new File(variableCacheDir, "variables-" + System.currentTimeMillis() + ".dat");
		log.info("Saving script variables into " + f);
		ObjectOutputStream oos = null;
		NumberFormat df = DecimalFormat.getIntegerInstance();
		try {
			oos = new ObjectOutputStream(new FileOutputStream(f));
			for (Map.Entry<String, Object> e : globalBindings.entrySet()) {
				String key = e.getKey();
				Object val = e.getValue();
				try {
					log.fine("\t" + key + " => " + val + " (" + val.getClass() + ")");
					//if (val instanceof Serializable) {
					//	Serializable s = (Serializable) val;
					oos.writeUTF(key);
					if (key.endsWith(".time")) {
						//oos.writeInt(Integer.parseInt(val.toString(), 16));
						oos.writeInt(df.parse(val.toString()).intValue());
					} else {
						oos.writeObject(val);
					}
					//}
				} catch (Exception ex) {
					log.info("Warning: Unable to serialize: " + key + " => " + val + ", " + ex);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (oos != null) {
				try {
					oos.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 * Recompiles all scripts in in the script directory
	 * that was designated during construction of this ScriptManager.
	 */
	public void compileEventScripts() {
		compileEventScripts(scriptDir);
	}

	/**
	 * (Re)Compiles a set of scripts found in the given directory (reading certain named
	 * subdirectories under it) and fills the current script cache with compiled script
	 * instances.
	 * Giving a different script directory as parameter will not change the default
	 * directory that was given during construction of this ScriptManager.
	 * @param scriptdir The directory which to scan for sub-directories
	 * @see #setScriptDir
	 */
	public void compileEventScripts(File scriptdir) {
		log.info("Compiling event scripts...");
		eventscriptcache.clear();
		FilenameFilter ff = new FilenameFilter() {
			public boolean accept(final File dir, final String name) {
				return name.endsWith(".js");
			}
		};
		for (Event evt : Event.values()) {
			final File dir = new File(scriptdir, evt.name());
			if (dir.exists()) {
				File[] scripts = dir.listFiles(ff);
				Arrays.sort(scripts);
				List<EventScript> trsc = new ArrayList<EventScript>(scripts.length);
				for (File script : scripts) {
					log.finer("Compiling: " + script.toString());
					Reader reader = null;
					try {
						reader = new FileReader(script);
						trsc.add(new EventScript(script.getName(), reader));
					} catch (Exception e) {
						log.log(Level.SEVERE, script.getName() + ": " + e);
					} finally {
						try {
							if (reader != null) {
								reader.close();
							}
						} catch (IOException e) {
						}
					}
				}
				log.fine(evt + ": " + trsc.size() + " scripts");
				eventscriptcache.put(evt, trsc);
			} else {
				log.warning(dir + " didn't exist. Skipping.");
			}
		}
		log.info("Done compiling");
	}

	public void runOnEventScript(final BotCallbackAPI bot, final Event event, final String sender, final String login, final String hostname, final String message, final String channel) {
		log.entering(getClass().getName(), "runOnEventScript", new String[]{
				sender, message, channel
		});
		log.finer(sender + "!"+ login + "@" + hostname + ", event: " + event);
		List<EventScript> scripts = eventscriptcache.get(event);
		if (scripts != null) {
			for (EventScript eventscript : scripts) {
				log.finest("evaluating: " + eventscript.getName());
				if (eventscript.runScript(getAPI(eventscript), sender,
						login, hostname, message,
						channel, eventscript.getName(), message)) {
					log.finer(eventscript.getName() + ": early cancel. No processing of other scripts");
					break;
				}
			}
		} else {
			log.finest("No event scripts for " + event);
		}
	}

	public File getScriptDir() {
		return scriptDir;
	}

	/**
	 * Sets the default directory from where to load scripts
	 * @param scriptDir
	 */
	public void setScriptDir(final File scriptDir) {
		this.scriptDir = scriptDir;
	}

	public TriggerScript getTriggerScript(final String cmd) {
		TriggerScript triggerScript;
		if (scriptcache.containsKey(cmd)) {
			log.finest("Script has compiled cache for " + cmd);
			triggerScript = scriptcache.get(cmd);
		} else {
			triggerScript = compileTriggerScript(cmd);
			scriptcache.put(cmd, triggerScript);
		}
		return triggerScript;
	}
	public void clearTriggerSciptCache() {
		scriptcache.clear();
	}

	protected boolean runTriggerScript(final BotCallbackAPI bot,
									   final String sender,
									   final String login, final String hostname,
									   final String message,
									   final String channel,
									   final String cmd,
									   final String param) {
		TriggerScript triggerScript = getTriggerScript(cmd);
		log.fine("triggerScript: " + triggerScript);

		if (ignoredNicks.contains(sender) || ignoredNicks.contains(hostname)) {
			bot.notice(sender, bot.getMessges().getString("reply.you.are.on.bot.ignore.list"));
			log.fine("Ignored user " + sender + "!" + login + "@" + hostname + " was declined to use " + cmd);
			return false;
		}

		if (triggerScript != null) {
			log.finer("triggerScript.hasTimeoutPassed: " + triggerScript.hasTimeoutPassed());
			if (!triggerScript.hasTimeoutPassed()) {
				final String timeoutmsg = "Too fast - Timeout is " + Math.round(triggerScript.getTimeout() / 100) / 10 + "s. Try again later.";
				bot.notice(sender, timeoutmsg);
			} else {
				return triggerScript.runScript(getAPI(triggerScript), sender, login, hostname, message,
						channel, cmd, param);
			}
			/*
			Scriptable response = (Scriptable) bindings.get(SCRIPTVAR_RESPONSE);
			ScriptableObject response_to = (ScriptableObject) bindings.get(SCRIPTVAR_RESPONSE_TO);
			if (null != response && null != response_to) {
				API.say(response_to.toString(), response.toString());
			}
			if (bindings.get(SCRIPTVAR_NO_TIMEOUT) != null) {
				return false;
			}
			*/
		} else {
			bot.notice(sender, "Unknown command: " + cmd);
			log.fine(sender + "@" + hostname + " tried to invoke unknown command: " + cmd);
		}
		return false;
	}

	protected TriggerScript compileTriggerScript(final String cmd) {
		TriggerScript retval = null;
		final File script = new File(scriptDir, cmd + ".js");
		log.finest("Locating script file " + script);
		if (script.exists()) {
			Reader reader = null;
			try {
				reader = new FileReader(script);
				log.finer("Compiling " + script);
				retval = new TriggerScript(cmd, reader);
			} catch (Exception e) {
				log.log(Level.SEVERE, e.getMessage(), e);
			} catch (Error e) {
				log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e) { /* no op */ }
				}
			}
		} else {
			log.finer(script + " not found");
		}
		return retval;
	}

	public String normalize(final String nick) {
		return nick.replace("_", "").replaceAll("[&~%@+]", "").toLowerCase();
	}

	public ScriptAPI getAPI(final ScriptBase script) {
		return new ScriptAPI() {
			public boolean addIgnore(final String nick, final String ident, final String host) {
				return addIgnore(nick, ident, host, DEFAULT_IGNORE_TIME);
			}

			public boolean addIgnore(final String nick, final String ident, final String host, final int minutes) {
				if (nick != null) {
					ignoredNicks.add(normalize(nick));
				}
				if (host != null) {
					ignoredNicks.add(host);
				}
				return true;
			}

			public boolean removeIgnore(final String nick, final String ident, final String host) {
				if (nick != null) {
					ignoredNicks.remove(normalize(nick));
				}
				if (host != null) {
					ignoredNicks.remove(host);
				}
				return true;
			}

			public boolean isIgnored(final String nick, final String ident, final String host) {
				return ignoredNicks.contains(normalize(nick))
						|| ignoredNicks.contains(host);
			}

			public String[] listIgnores() {
				String[] retval = new String[ignoredNicks.size()];
				Iterator<String> it = ignoredNicks.iterator();
				int i = 0;
				while (it.hasNext()) {
					retval[i++] = it.next();
				}
				return retval;
			}
			public void removeAllIgnores() {
				ignoredNicks.clear();
			}

			public User[] getUsers(final String channel) {
				return bot.getUsers(channel);
			}

			public Page getPage(final String url) {
				return getPage(url, DEFAULT_HTTP_TIMEOUT);
			}

			public String getPageAsText(final String url) {
				log.finer("getPageAsText: " + url);
				final Page page = getPage(url);
				if (page != null) {
					String x = page.getWebResponse().getContentAsString();
					log.finest(x);
					return x;
				}
				return "";
			}

			public Page getPage(final String url, final int timeout) {
				return bot.getPage(url, timeout);
			}

			public DomElement getFirstByXpath(final Page page, final String xpath) {
				if (page instanceof HtmlPage) {
					HtmlPage html = (HtmlPage) page;
					return html.getFirstByXPath(xpath);
				}
				if (page instanceof XmlPage) {
					XmlPage xml = (XmlPage) page;
					return xml.getFirstByXPath(xpath);
				}
				return null;
			}

			/**
			 * Returns the DomElement given by the XPath statement or null if nothing found
			 * @param page Either an XmlPage or HtmlPage as returned by {@link #getPage(String)}
			 * @param xpath XPath expression
			 * @see XmlPage
			 * @see HtmlPage
			 * @see #getAsText(com.gargoylesoftware.htmlunit.Page, String)
			 */
			public DomElement[] getByXpath(final Page page, final String xpath) {
				if (page == null) {
					return new DomElement[0];
				}
				DomElement[] retval;
				if (page instanceof HtmlPage) {
					HtmlPage html = (HtmlPage) page;
					List<? extends DomElement> list = (List<? extends DomElement>) html.getByXPath(xpath);
					/*
					for (int i = 0; i < list.size(); i++) {
						System.out.println("\t\t" + list.get(i).getClass() + ": " + list.get(i) );
					}
					*/
					retval = new DomElement[list.size()];
					for (int i=0; i<list.size(); i++) {
						//System.out.println(list.get(i).getClass());
						retval[i] = list.get(i);
					}
				} else if (page instanceof XmlPage) {
					XmlPage xml = (XmlPage) page;
					List<? extends DomElement> list = (List<? extends DomElement>) xml.getByXPath(xpath);
					retval = new DomElement[list.size()];
					for (int i=0; i<list.size(); i++) {
						//System.out.println(list.get(i).getClass());
						retval[i] = list.get(i);
					}
				} else {
					retval = new DomElement[0];
				}
				return retval;
			}

			public String encode(final String param) {
				try {
					return URLEncoder.encode(param, UTF8);
				} catch (UnsupportedEncodingException e) {
					return param;
				}
			}

			public Element getElementById(final Page page, final String id) {
				if (page instanceof HtmlPage) {
					HtmlPage html = (HtmlPage) page;
					return html.getElementById(id);
				}
				if (page instanceof XmlPage) {
					XmlPage xml = (XmlPage) page;
					return xml.getElementById(id);
				}
				return null;
			}

			public List getElementsByAttribute(final Element parent, final String tagname, final String attribute, final String value) {
				if (parent instanceof HtmlElement) {
					return ((HtmlElement)parent).getElementsByAttribute(tagname, attribute, value);
				}
				if (parent instanceof DomElement) {
					return ((DomElement)parent).getElementsByTagName(tagname);
				}
				return new ArrayList(0);
			}

			/**
			 * Either returns the asText() value of a DomElement or empty zero length string if
			 * nothing was found.
			 * @param page Either an XmlPage or HtmlPage as returned by {@link #getPage(String)}
			 * @param xpath XPath expression
			 * @see XmlPage
			 * @see HtmlPage
			 */
			public String getAsText(final Page page, final String xpath) {
				DomElement elm = getFirstByXpath(page, xpath);
				if (elm != null) {
					return elm.asText();
				}
				return "";
			}

			public long getTimeout(final String script) {
				ScriptBase csc = scriptcache.get(script);
				if (csc != null) {
					return csc.getTimeout();
				}
				return -1;
			}

			public void setTimeout(final String script, final int timeout) {
				if (script == null || "*".equals(script)) {
					for (ScriptBase csc : scriptcache.values()) {
						csc.setTimeout(timeout);
					}
				} else {
					ScriptBase csc = scriptcache.get(script);
					if (csc != null) {
						csc.setTimeout(timeout);
					}
				}
			}

			public void setValue(final String key, final Object value) {
				log.finest("setValue: " + key + " => " + value);
				globalBindings.put(key, value);
			}

			public Object removeValue(final String key) {
				Object removed = globalBindings.remove(key);
				log.finest("removeValue: " + key + " => " + removed);
				return removed;
			}

			public Object getValue(final String key) {
				return globalBindings.get(key);
			}

			public void say(final String target, final String line) {
				log.fine("Say: " + target + ": " + line);
				bot.say(target, line);
			}

			public void action(final String target, final String line) {
				log.fine("Act: " + target + ": " + line);
				bot.act(target, line);
			}

			public void notice(final String notice, final String line) {
				log.fine("Notice: " + notice + ": " + line);
				bot.notice(notice, line);
			}

			public void reinit() {
				log.info("Reinitialize");
				bot.reinitialize();
			}

			public boolean email(final String recipient, final String topic, final String htmlmessage) {
				log.info("email => " + recipient);
				// TODO email function
				return false;
			}

			public long getStartupTime() {
				return bot.getStartupTime();
			}

			public long getConnectTime() {
				return bot.getConnectTime();
			}

			public void info(final String message) {
				log.info(message);
			}

			public void debug(final String message) {
				log.finer(message);
			}
			
			public void setDebugLevel(final Level level) {
				log.setLevel(level);
			}

			public Level getDebugLevel() {
				return log.getLevel(); 
			}

			public long getLastRun() {
				return script.getLastRun();
			}

			public String getBotNick() {
				return bot.getNick();
			}

			public boolean hasVoice(final String channel, final String nick) {
				return bot.hasVoice(channel, nick);
			}

			public boolean isHalfOp(final String channel, final String nick) {
				return bot.isHalfOp(channel, nick);
			}

			public boolean isOp(final String channel, final String nick) {
				return bot.isOp(channel, nick);
			}

			public boolean isAdmin(final String channel, final String nick) {
				return bot.isAdmin(channel, nick);
			}

			public boolean isOwner(final String channel, final String nick) {
				return bot.isOwner(channel, nick);
			}

			public void clearScriptCacheFor(final String cmd) {
				log.finer("Clearing cache for " + cmd);
				scriptcache.remove(cmd);
			}
			public void clearScriptCache() {
				log.finer("Clearing all script caches");
				scriptcache.clear();
				compileEventScripts(scriptDir);
			}

			public void reloadLoggingConfig() {
				log.info("Reloading logging configuration");
				try {
					LogManager.getLogManager().readConfiguration();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			public String getGreeting() {
				return bot.getGreeting();
			}
			public void reloadGreetings() {
				bot.reloadGreetings();
			}

			public String formatNum(final double num) {
				return NumberFormat.getNumberInstance().format(num);
			}

			public String secondsAsPassedTime(final int seconds) {
				// Untested
				if (seconds < 0) {
					return String.valueOf(seconds);
				}
				if (seconds < 60) {
					return seconds + " seconds ago";
				}
				if (seconds < 60*60) {
					return Math.floor(seconds/60) + " minutes ago";
				}
				if (seconds < 24*60*60) {
					int h = seconds / (60 * 60);
					int min = (seconds - (h * 60)) / 60;
					return h + " hours and " + min + " minutes ago";
				}
				int d = seconds / (24 * 60 * 60);
				int h = (seconds - d * 24 * 60 * 60) / (60 * 60);
				int m = seconds % 60;
				return h + " days and " + h + " hours ago";
			}

			public void flushScriptVars() {
				saveScriptVars();
			}

			public Long  getLastActiveTime(final String nick, final String channel) {
				return logger.getLastActivity(channel, nick);
			}

			public Iterator<Map.Entry<Integer, Integer>> getLineCounts(final String channel, final int... minutes) {
				return logger.getLineCounts(channel, minutes);
			}

			public String getConfigValue(final String settingName, final String defaultValue) {
				String retval = bot.getConfig().getString(settingName);
				if (retval == null) retval = defaultValue;
				return retval;
			}

			public boolean getConfigValueAsBoolean(final String settingName, final boolean defaultValue) {
				boolean retval;
				String s = bot.getConfig().getString(settingName);
				if (s == null) retval = defaultValue;
				else retval = Boolean.parseBoolean(s);
				return retval;
			}

			public Iterator<ChatLogger.ChatLine> getLines(final String channel) {
				return logger.getLines(channel);
			}

			/**
			 * Shuts down the bot.
			 * TODO privilege check
			 */
			public boolean byebye() {
				log.info("Shutting down bot from " + script.getName());
				//if (hasPrivileges(nick, ident, host))
				return bot.byebye();
			}

		};
	}
}
