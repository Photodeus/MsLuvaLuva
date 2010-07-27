package com.popodeus.chat;

import org.jibble.pircbot.User;
import com.gargoylesoftware.htmlunit.Page;

import java.util.ResourceBundle;

/**
 * This is the API MsLuvaLuva exposes to the world.
 * Provides callback functions directly related to the bot itself.
 * Can be used from the Logger, ScriptManager etc.
 * <br/>photodeus
 */
public interface BotCallbackAPI {

	/**
	 * Shuts down the bot. Use carefully!
	 * You cannot restart the bot after a shutdown without
	 * having actual command line access to the shell.
	 * TODO some sort of privilege check
	 */
	boolean byebye();

	/**
	 * Reinitialize bot with data from the configuration files.
	 * Also flushes the logs and reopens them for writing.
	 */
	void reinitialize();

	/**
	 * Timestamp of when bot process was started
	 *
	 * @return
	 */
	long getStartupTime();

	/**
	 * Timestamp when last connect to the IRC server was made
	 *
	 * @return
	 */
	long getConnectTime();

	/**
	 * Sends a normal message to the designated target nick or channel
	 *
	 * @param target nick or #channel name
	 * @param line   message
	 */
	void say(final String target, final String line);

	/**
	 * Sends a CTCP action to the designated target nick or channel
	 *
	 * @param target nick or #channel name
	 * @param line   message
	 */
	void act(final String target, final String line);

	/**
	 * Sends a notice to the designated target nick or channel
	 *
	 * @param target nick or #channel name
	 * @param line   message
	 */
	void notice(final String target, final String line);

	/**
	 * The current nick of the bot
	 */
	String getNick();
	String getDefaultNick();
	String getAltNick();
	void setBotNick(String newnick);

	boolean hasVoice(final String channel, final String nick);

	boolean isHalfOp(final String channel, final String nick);

	boolean isOp(final String channel, final String nick);

	boolean isAdmin(final String channel, final String nick);

	boolean isOwner(final String channel, final String nick);

	User[] getUsers(final String channel);

	/**
	 * Get a random greeting from welcome.txt
	 *
	 * @return
	 */
	String getGreeting();

	void reloadGreetings();

	String[] getChannels();

	void join(String channel);

	void join(String channel, String key);

	void part(String channel);

	// Won't work unless bot is op
	void channelBan(String channel, String hostmask);

	void channelUnban(String channel, String hostmask);

	// Might not work unless bot is op
	void invite(String nick, String channel);

	Page getPage(final String url, final int timeout);

	ResourceBundle getMessges();

	ResourceBundle getConfig();
}
