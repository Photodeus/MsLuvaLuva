package com.popodeus.chat;

import javax.script.*;
import java.io.BufferedReader;
import java.io.Reader;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * photodeus
 * Sep 19, 2009
 * 1:16:48 PM
 */
public abstract class ScriptBase {

	public static final String SCRIPTVAR_NICK = "nick";
	public static final String SCRIPTVAR_CHANNEL = "channel";
	public static final String SCRIPTVAR_IDENT = "ident";
	public static final String SCRIPTVAR_HOST = "host";
	public static final String SCRIPTVAR_MESSAGE = "message";
	public static final String SCRIPTVAR_COMMAND = "command";
	public static final String SCRIPTVAR_PARAM = "param";
	//public static final String SCRIPTVAR_BOT = "bot";
	//public static final String SCRIPTVAR_LAST_RESULT = "last_result";
	public static final String SCRIPTVAR_RESULT = "result";
	public static final String SCRIPTVAR_RESPONSE = "response";
	public static final String SCRIPTVAR_RESPONSE_TO = "response_to";
	public static final String SCRIPTVAR_CANCEL = "cancel";
	public static final String SCRIPTVAR_NO_TIMEOUT = "no_timeout";
	public static final String SCRIPTVAR_API = "API";

	private Pattern ptrn = Pattern.compile(".*@timeout\\s+(\\d+).*");
	private Pattern notimeout = Pattern.compile(".*@notimeout.*");
	private static ScriptEngine engine;
	private static long DEFAULT_TIMEOUT = 10 * 1000L; // milliseconds
	//private CompiledScript csc;
	protected Logger log = Logger.getLogger("com.popodeus.chat.ScriptBase");
	private long lastRun;
	private long timeout = DEFAULT_TIMEOUT;
	private String name;
	private StringBuilder source;
	protected ProtectedBindings b;
	final LinkedBlockingDeque<Long> runtimes;

	class ProtectedBindings extends SimpleBindings {
		boolean isProtected = false;

		ProtectedBindings(boolean aProtected) {
			isProtected = aProtected;
		}

		public boolean isProtected() {
			return isProtected;
		}

		public void setProtected(boolean aProtected) {
			isProtected = aProtected;
		}
		@Override
		public Object put(String name, Object value) {
			if (isProtected() && SCRIPTVAR_API.equals(name)) {
				return null;
			}
			return super.put(name, value);
		}

		@Override
		public void putAll(Map<? extends String, ? extends Object> toMerge) {
			if (isProtected() && toMerge != null && toMerge.containsKey(SCRIPTVAR_API)) toMerge.remove(SCRIPTVAR_API);
			super.putAll(toMerge);
		}

		@Override
		public Object remove(Object key) {
			if (isProtected() && SCRIPTVAR_API.equals(key)) {
				return null;
			}
			return super.remove(key);
		}
	}
	public ScriptBase(final String name, final Reader src) {
		if (engine == null) {
			engine = new ScriptEngineManager().getEngineByExtension("js");
			//engine.setContext(ContextFactory.getGlobal().);
		}
		this.name = name;
		// TODO restore bindings from file on startup?
		this.b = new ProtectedBindings(false);
		this.runtimes = new LinkedBlockingDeque<Long>(50);
		boolean timeout_found = false;
		try {
			final BufferedReader scriptsrc = new BufferedReader(src, 8*1024);
			source = new StringBuilder(2048);
			while (true) {
				String line = scriptsrc.readLine();
				if (line == null) break;
				if (!timeout_found) {
					Matcher m = ptrn.matcher(line.trim());
					if (m.find()) {
						timeout = Integer.parseInt(m.group(1));
						// Timeout inside scripts is written as seconds, and needs
						// to be converted into millis
						if (timeout < 120) timeout *= 1000;
						if (timeout < 0) timeout = DEFAULT_TIMEOUT;
						timeout_found = true;
					}
					m = notimeout.matcher(line.trim());
					if (m.find()) {
						timeout = 0;
						timeout_found = true;
					}
				}
				if (!line.startsWith("//")) source.append(line + "\n");
			}
			//log.finer("Compiling script " + name);
			//scriptsrc.mark(8 * 1024);
			//timeout = getTimeoutForScript(scriptsrc, 10000);
			log.finest("Timeout is " + timeout + "ms");
			//scriptsrc.reset();
			//csc = engine.compile(source.toString());
		} catch (Exception e) {
			System.err.println(name);
			e.printStackTrace();
		}
	}

	public long getLastRun() {
		return lastRun;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(final int timeout) {
		this.timeout = timeout;
	}

	/**
	 * @return true if the timeout is over since last run
	 */
	public boolean hasTimeoutPassed() {
		return System.currentTimeMillis() - lastRun >= timeout;
	}

	/**
	 * Runs the script.
	 *
	 * @param API
	 * @param sender
	 * @param login
	 * @param hostname
	 * @param message
	 * @param channel
	 * @param cmd
	 * @param param
	 * @return true if this script wishes no other similar scripts should be run
	 * @see #hasTimeoutPassed()
	 */
	public boolean runScript(final ScriptAPI API,
							 final String sender,
							 final String login,
							 final String hostname,
							 final String message,
							 final String channel,
							 final String cmd,
							 final String param) throws Exception {
		return runScript(API,
				new ScriptInvokerParameters(sender, login, hostname, message, channel, cmd, param));
	}

	/**
	 *
	 * @return Returns true if the script says there is no need for a timeout until next invocation
	 */
	public boolean runScript(final ScriptAPI API, final ScriptInvokerParameters params) throws Exception {
		if (source == null || source.length() <= 10) {
			return false;
		}
		log.entering("com.popodeus.chat.ScriptBase", "runScript (" + name + ")", new Object[]{
				params.sender, params.channel, params.cmd, params.param
		});
		try {
			//b.clear();
			b.put(SCRIPTVAR_NICK, params.sender);
			b.put(SCRIPTVAR_CHANNEL, params.channel);
			b.put(SCRIPTVAR_IDENT, params.login);
			b.put(SCRIPTVAR_HOST, params.hostname);
			b.put(SCRIPTVAR_MESSAGE, params.message);
			b.put(SCRIPTVAR_COMMAND, params.cmd);
			b.put(SCRIPTVAR_PARAM, params.param);
			b.put(SCRIPTVAR_API, API);
			//b.put(SCRIPTVAR_LAST_RESULT, last_result);
			b.put(SCRIPTVAR_RESULT, new Object());

			b.remove(SCRIPTVAR_CANCEL);
			b.remove(SCRIPTVAR_RESPONSE);
			b.remove(SCRIPTVAR_RESPONSE_TO);
			b.remove(SCRIPTVAR_NO_TIMEOUT);

			b.setProtected(true);

			log.finest("evaluating " + name + "...");
			long nano = System.nanoTime();

			// Evalute the script here
			// TODO add a way to stop the evaluation if it takes too long
			//Context ctx = Context.enter();
            engine.put(ScriptEngine.FILENAME, name + ".js");
			Object retval = engine.eval(source.toString(), b); //csc.eval(b);
			//Object retval = engine.eval(source.toString(), b);
			log.finest("retval: " + retval);
			//Context.exit();
			//
			nano = System.nanoTime() - nano;
			log.finer(name + " runtime: " + nano + "ns");
			if (runtimes.remainingCapacity() == 0) {
				runtimes.pollFirst();
			}
			runtimes.offerLast(nano);
			lastRun = System.currentTimeMillis();

			final Object response_to = b.get(SCRIPTVAR_RESPONSE_TO);
			final Object response = b.get(SCRIPTVAR_RESPONSE);
			log.finer("\nResponse_to: " + response_to + "\nResponse: " + response);
			if (null != response && null != response_to) {
				String to = response_to.toString();
				if (to.matches("^[#&].+")) {
					if (API.hasVoice(to, API.getBotNick())) {
						API.say(to, response.toString());
					} else {
						log.finer(API.getBotNick() + " does not have voice on " + to + " so response was ignored");
					}
				} else {
					// No check for nicks etc.
					API.say(to, response.toString());
				}
			}
			if (timeout == 0 || (b.get(SCRIPTVAR_NO_TIMEOUT) != null && b.get(SCRIPTVAR_NO_TIMEOUT).equals(true))) {
				return true;
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, name + ": " + e.getMessage(), e);
			throw e;
			//System.err.println(e);
			//e.printStackTrace();
		}
		return false;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "[" + getClass().getName() + ": " + name + "]";
	}
}
