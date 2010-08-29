package com.popodeus.chat;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.html.DomElement;
import org.jibble.pircbot.User;
import org.w3c.dom.Element;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * Created by IntelliJ IDEA.
 * User: Seppo
 * Date: 2010-aug-27
 * Time: 17:53:18
 */
public class ScriptAPIAdaptor implements ScriptAPI {
	public boolean addIgnore(String nick, String ident, String host) {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

	public boolean addIgnore(String nick, String ident, String host, int minutes) {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

	public boolean removeIgnore(String nick, String ident, String host) {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

	public void removeAllIgnores() {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	public boolean isIgnored(String nick, String ident, String host) {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

	public String[] listIgnores() {
		return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
	}

	public User[] getUsers(String channel) {
		return new User[0];  //To change body of implemented methods use File | Settings | File Templates.
	}

	public Page getPage(String url) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	public String getPageAsText(String url) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	public String getAsText(Page page, String xpath) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	public DomElement[] getByXpath(Page page, String xpath) {
		return new DomElement[0];  //To change body of implemented methods use File | Settings | File Templates.
	}

	public DomElement getFirstByXpath(Page page, String xpath) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	public String encode(String param) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	public Element getElementById(Page page, String id) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	public List getElementsByAttribute(Element parent, String tagname, String attribute, String value) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	public long getTimeout(String script) {
		return 0;  //To change body of implemented methods use File | Settings | File Templates.
	}

	public void setTimeout(String script, int timeout) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	public void setValue(String key, Object value) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	public Object getValue(String key) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	public Object removeValue(String key) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	public void say(String target, String line) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	public void action(String target, String line) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	public void notice(String target, String line) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	public void reinit() {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	public boolean email(String recipient, String topic, String htmlmessage) {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

	public long getStartupTime() {
		return 0;  //To change body of implemented methods use File | Settings | File Templates.
	}

	public long getConnectTime() {
		return 0;  //To change body of implemented methods use File | Settings | File Templates.
	}

	public void info(String line) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	public void debug(String line) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	public void setDebugLevel(Level level) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	public Level getDebugLevel() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	public long getLastRun() {
		return 0;  //To change body of implemented methods use File | Settings | File Templates.
	}

	public String getBotNick() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	public void fixBotNick() {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	public void setBotNick(String newnick) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	public boolean hasVoice(String channel, String nick) {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

	public boolean isHalfOp(String channel, String nick) {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

	public boolean isOp(String channel, String nick) {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

	public boolean isAdmin(String channel, String nick) {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

	public boolean isOwner(String channel, String nick) {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

	public void clearScriptCacheFor(String cmd) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	public void clearScriptCache() {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	public void reloadLoggingConfig() {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	public String getGreeting() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	public void reloadGreetings() {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	public String formatNum(double num) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	public String secondsAsPassedTime(int seconds) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	public void flushScriptVars() {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	public Long getLastActiveTime(String nick, String channel) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	public Iterator<Map.Entry<Integer, Integer>> getLineCounts(String channel, int... minutes) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	public String getConfigValue(String settingName, String defaultValue) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	public boolean getConfigValueAsBoolean(String settingName, boolean defaultValue) {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

	public Iterator<ChatLogger.ChatLine> getLines(String channel) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}
}
