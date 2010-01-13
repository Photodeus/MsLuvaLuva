package com.popodeus.chat;

/**
 * photodeus
 * Dec 10, 2009
 * 3:41:25 AM
 */
public class ScriptInvokerParameters {

	public final String sender;
	public final String login;
	public final String hostname;
	public final String message;
	public final String channel;
	public final String cmd;
	public final String param;

	public ScriptInvokerParameters(final String sender, final String login, final String hostname, final String message, final String channel, final String cmd, final String param) {
		this.sender = sender;
		this.login = login;
		this.hostname = hostname;
		this.message = message;
		this.channel = channel;
		this.cmd = cmd;
		this.param = param;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		ScriptInvokerParameters that = (ScriptInvokerParameters) o;

		if (channel != null ? !channel.equals(that.channel) : that.channel != null) {
			return false;
		}
		if (cmd != null ? !cmd.equals(that.cmd) : that.cmd != null) {
			return false;
		}
		if (hostname != null ? !hostname.equals(that.hostname) : that.hostname != null) {
			return false;
		}
		if (login != null ? !login.equals(that.login) : that.login != null) {
			return false;
		}
		if (message != null ? !message.equals(that.message) : that.message != null) {
			return false;
		}
		if (param != null ? !param.equals(that.param) : that.param != null) {
			return false;
		}
		if (sender != null ? !sender.equals(that.sender) : that.sender != null) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result;
		result = (sender != null ? sender.hashCode() : 0);
		result = 31 * result + (login != null ? login.hashCode() : 0);
		result = 31 * result + (hostname != null ? hostname.hashCode() : 0);
		result = 31 * result + (message != null ? message.hashCode() : 0);
		result = 31 * result + (channel != null ? channel.hashCode() : 0);
		result = 31 * result + (cmd != null ? cmd.hashCode() : 0);
		result = 31 * result + (param != null ? param.hashCode() : 0);
		return result;
	}


}
