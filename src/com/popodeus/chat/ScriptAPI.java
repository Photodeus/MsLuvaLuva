package com.popodeus.chat;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.html.DomElement;
import sun.org.mozilla.javascript.internal.Scriptable;
import org.jibble.pircbot.User;
import org.w3c.dom.Element;

import java.util.List;

/**
 * API for scripts/plugins
 * <br/>photodeus
 * Sep 7, 2009
 * 9:45:51 AM
 */
public interface ScriptAPI {

	/**
	 * Adds someone to command ignore, this disabling them from using commands
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

	/**
	 * Checks if someone is ignored
	 * @see #addIgnore(String, String, String)
	 */
	boolean isIgnored(String nick, String ident, String host);
	String[] listIgnores();

	User[] getUsers(String channel);
	/**
	 * Reads a Page (object) from the web. JavaScript is disabled when fetching
	 * pages.
	 * @see com.gargoylesoftware.htmlunit.html.HtmlPage
	 * @see com.gargoylesoftware.htmlunit.xml.XmlPage 
	 */
	Page getPage(String url);

	/**
	 * Get page contents as text, no matter if it's html, xml, text, json etc.
	 * @param url
	 * @return
	 */
	String getPageAsText(String url);

	/**
	 * Get specified node from html or xml page as text
	 * @param page Page object that was returned by {@link #getPage(String)}
	 * @param xpath XPath expression pointing to the node.
	 */
	String getAsText(Page page, String xpath);
	/**
	 * Get specified node from html or xml page
	 * @param page Page object that was returned by {@link #getPage(String)}
	 * @param xpath XPath expression pointing to the node.
	 */
	DomElement getByXpath(Page page, String xpath);

	/**
	 * URLEncodes the string as UTF-8 escaped string, so it can be added to requests
	 */
	String encode(String param);

	Element getElementById(Page page, String id);
	List getElementsByAttribute(final Element parent, String tagname, String attribute, String value);

	int getTimeout(String script);
	void setTimeout(String script, int timeout);

	/**
	 * Sets a value in global scope accessible to all scripts.
	 * Meant to store variables so scripts can inter-communicate with each other.
	 * @param key
	 * @param value
	 */
	void setValue(String key, Object value);
	/**
	 * Gets a value from global scope which is accessible to all scripts.
	 * Meant to store variables so scripts can inter-communicate with each other.
	 * @param key
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

	void reinit();
	void email(String recipient, String topic, String htmlmessage);

	long getStartupTime();
	long getConnectTime();

	void info(String line);
	void debug(String line);

	/**
	 * When script was last run
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
}