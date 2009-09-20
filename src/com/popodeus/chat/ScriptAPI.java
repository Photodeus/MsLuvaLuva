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
	 * @see {@link #isIgnored(String, String, String)} 
	 */
	boolean addIgnore(String nick, String ident, String host);
	boolean addIgnore(String nick, String ident, String host, int minutes);
	boolean removeIgnore(String nick, String ident, String host);
	boolean isIgnored(String nick, String ident, String host);
	String[] listIgnores();

	User[] getUsers(String channel);
	/**
	 * @see com.gargoylesoftware.htmlunit.html.HtmlPage
	 * @see com.gargoylesoftware.htmlunit.xml.XmlPage 
	 */
	Page getPage(String url);
	String getPageAsText(String url);
	String getAsText(Page url, String xpath);
	DomElement getByXpath(Page url, String xpath);
	String encode(String param);
	Element getElementById(Page page, String id);
	List getElementsByAttribute(final Element parent, String tagname, String attribute, String value);

	int getTimeout(String script);
	void setTimeout(String script, int timeout);

	/**
	 * Sets a value in global scope accessible to all scripts
	 * @param key
	 * @param value
	 */
	void setValue(String key, Object value);
	/**
	 * Gets a value from global scope which is accessible to all scripts
	 * @param key
	 */
	Object getValue(String key);
	/**
	 * Removes a globally scoped value 
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