package com.popodeus.chat;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.html.DomElement;
import org.jibble.pircbot.User;
import org.w3c.dom.Element;

import java.util.List;
import java.util.SortedMap;
import java.util.Map;
import java.util.Iterator;
import java.util.logging.Level;

/**
 * API for scripts/plugins
 * <br/>photodeus
 * Sep 7, 2009
 * 9:45:51 AM
 */
public interface ScriptAPI {

	/**
	 * Adds someone to command ignore, thus disabling them from using script
	 * @param nick
	 * @param ident may be null
	 * @param host may be null
	 * @see #isIgnored(String, String, String)
	 * @see #removeIgnore(String, String, String)
	 */
	boolean addIgnore(String nick, String ident, String host);

	/**
	 * Adds someone to command ignore for specified time in minutes
	 * @param nick
	 * @param ident
	 * @param host
	 * @param minutes
	 * @see #removeIgnore(String, String, String)
v	 */
	boolean addIgnore(String nick, String ident, String host, int minutes);

	/**
	 * Removes a previously set ignore
	 * @see #addIgnore(String, String, String)
	 */
	boolean removeIgnore(String nick, String ident, String host);
	void removeAllIgnores();

	/**
	 * Checks if someone is ignored
	 * @see #addIgnore(String, String, String)
	 */
	boolean isIgnored(String nick, String ident, String host);
	String[] listIgnores();

	User[] getUsers(String channel);
	/**
	 * Reads a Page (object) from the web. JavaScript is disabled when fetching
	 * pages and default timeout is 8 seconds.
	 * Multiple concurrent requests are not
	 * possible, and this call is blocking. 
	 * After script execution, the page object should be set to null.
	 * @return null if request times out or another request is still pending. 
	 * @see com.gargoylesoftware.htmlunit.html.HtmlPage
	 * @see com.gargoylesoftware.htmlunit.xml.XmlPage 
	 */
	Page getPage(String url);

	/**
	 * Get page contents as text, no matter if it's html, xml, text, json etc.
	 * Only on request can be active at the same time.
	 * @param url
	 * @return null if request times out or another request is still pending. 
	 */
	String getPageAsText(String url);

	/**
	 * Get specified node from html or xml page as text
	 * @param page Page object that was returned by {@link #getPage(String)}
	 * @param xpath XPath expression pointing to the node.
	 */
	String getAsText(Page page, String xpath);
	/**
	 * Get specified nodes from html or xml page
	 * @param page Page object that was returned by {@link #getPage(String)}
	 * @param xpath XPath expression pointing to the node.
	 * @return an array of DomElements or an empty array 
	 */
	//DomElement getByXpath(Page page, String xpath);
	DomElement[] getByXpath(Page page, String xpath);
	DomElement getFirstByXpath(Page page, String xpath);

	/**
	 * URLEncodes the string as UTF-8 escaped string, so it can be added to requests
	 * @example API.getPageAsText( "http://example.com/search?query=" + API.encode("xyzåäö") );
	 */
	String encode(String param);

	/**
	 * Return any html or xml element that has the id attribute. &lt;div id="example">this is returned&lt;/div>
	 * @param id the name of the id 
	 */
	Element getElementById(Page page, String id);
	List getElementsByAttribute(final Element parent, String tagname, String attribute, String value);

	/**
	 * Check the timeout in seconds that is active on this script.
	 * If no specific timeout has been set, some sensible default value will
	 * be returned
	 * @param script Script name, for example "who", "stalk" or "ud"
	 */
	long getTimeout(String script);

	/**
	 * Sets timeout in seconds for a script, for example "ud"
	 * @param script The name of the script, if the command is !stalk, pass in the string "stalk"
	 * @param timeout Time in seconds. Do not use values under 1 and values that are too large.
	 */
	void setTimeout(String script, int timeout);

	/**
	 * Sets a value in global scope accessible to all scripts.
	 * Meant to store variables so scripts can inter-communicate with each other.
	 * Use with API.setValue( "foo", "value" );
	 * @param key
	 * @param value
	 */
	void setValue(String key, Object value);
	/**
	 * Gets a value from global scope which is accessible to all scripts.
	 * Meant to store variables so scripts can inter-communicate with each other.
	 * Use with API.getValue( "foo" );
	 * @param key
	 * @return Returns null if the global variable has not been set
	 */
	Object getValue(String key);
	/**
	 * Removes a globally scoped value.
	 * @param key
	 */
	Object removeValue(String key);

	/**
	 * Says something directly to target nick or channel.
	 * @param target
	 * @param line
	 */
	void say(String target, String line);
	void action(String target, String line);
	void notice(String target, String line);

	/**
	 * Reinitialize the script host. Use with care!
	 */
	void reinit();
	boolean email(String recipient, String topic, String htmlmessage);

	/**
	 * Return in seconds how long time ago the bot was started up.
	 * @return
	 */
	long getStartupTime();

	/**
	 * Return in seconds how long time ago the bot last (re)connected to the IRC server.
	 * @return
	 */
	long getConnectTime();

	/**
	 * Logs a message internally on the server
	 * @param line Any text to log. The length if this may be limited for security reasons.
	 */
	void info(String line);

	/**
	 * Logs a debug line on the server if debugging is enabled.
	 * @param line Any text to log. The length if this may be limited for security reasons.
	 */
	void debug(String line);

	void setDebugLevel(final Level level);
	Level getDebugLevel();
	/**
	 * When script was last run.
	 * Each script will only get the last runtime for the same script
	 * @return A timestamp
	 */
	long getLastRun();

	String getBotNick();

	boolean hasVoice(String channel, String nick);
	boolean isHalfOp(String channel, String nick);
	boolean isOp(String channel, String nick);
	boolean isAdmin(String channel, String nick);
	boolean isOwner(String channel, String nick);

	void clearScriptCacheFor(String cmd);
	void clearScriptCache();
	void reloadLoggingConfig();
	String getGreeting();
	void reloadGreetings();

	String formatNum(double num);
	String secondsAsPassedTime(int seconds);

	void flushScriptVars();
	//void loadScriptVars();

	Long getLastActiveTime(String nick, final String channel);
	Iterator<Map.Entry<Integer, Integer>> getLineCounts(final String channel, final int... minutes);
	
	String getConfigValue(String settingName, String defaultValue);
	boolean getConfigValueAsBoolean(String settingName, boolean defaultValue);
	Iterator<ChatLogger.ChatLine> getLines(String channel);
}