package com.popodeus.chat;

import org.jibble.pircbot.User;

/**
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
	 * @return
	 */
	long getStartupTime();

	/**
	 * Timestamp when last connect to the IRC server was made
	 * @return
	 */
	long getConnectTime();

	/**
	 * Sends a normal message to the designated target nick or channel
	 * @param target
	 * @param line
	 */
	void say(final String target, final String line);
	/**
	 * Sends a CTCP action to the designated target nick or channel
	 * @param target
	 * @param line
	 */
	void act(final String target, final String line);
	/**
	 * Sends a notice to the designated target nick or channel
	 * @param notice
	 * @param line
	 */
	void notice(final String notice, final String line);

	String getNick();

	boolean hasVoice(final String channel, final String nick);
	boolean isHalfOp(final String channel, final String nick);
	boolean isOp(final String channel, final String nick);
	boolean isAdmin(final String channel, final String nick);
	boolean isOwner(final String channel, final String nick);

	User[] getUsers(final String channel);

	String getGreeting();

	String[] getChannels();

	void joinChannel(String channel);
	void joinChannel(String channel, String key);
	void partChannel(String channel);
	// Won't work unless bot is op
	void channelBan(String channel, String hostmask);
	void channelUnban(String channel, String hostmask);
	// Might not work unless bot is op
	void sendInvite(String nick, String channel);
}
