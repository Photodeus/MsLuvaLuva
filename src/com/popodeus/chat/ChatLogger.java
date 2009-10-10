package com.popodeus.chat;

import java.io.*;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.BlockingDeque;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * photodeus
 * Sep 2, 2009
 * 10:01:11 PM
 */
public class ChatLogger {

	DateFormat dateformat = new SimpleDateFormat("HH:mm:ss");

	class ChatLine implements Comparable {
		long timestamp;
		String nick;
		String line;

		ChatLine(final String nick, final String line) {
			this.timestamp = System.currentTimeMillis();
			this.nick = nick;
			this.line = line;
		}

		ChatLine(final long timestamp, final String nick, final String line) {
			this.timestamp = timestamp;
			this.nick = nick;
			this.line = line;
		}

		@Override
		public int compareTo(final Object o) {
			if (o instanceof ChatLine) {
				ChatLine cl = (ChatLine) o;
				return (int) Math.signum(this.timestamp - cl.timestamp);
			}
			return 0;
		}

		@Override
		public String toString() {
			String localtime = dateformat.format(timestamp);
			return timestamp + " [" + localtime + "] <" + nick + "> " + line;
		}
	}
	class ActionLine extends ChatLine {
		ActionLine(final String line) {
			super(null, line);
		}

		@Override
		public String toString() {
			String localtime = dateformat.format(timestamp);
			String prefix = "-!- ";
			if (line.startsWith("*")) prefix = "";
			return timestamp + " [" + localtime + "] " + prefix + line;
		}
	}

	class ChannelLog {
		private BufferedWriter writer;
		private BlockingDeque<ChatLine> lines;

		ChannelLog(final FileWriter writer) {
			this.writer = new BufferedWriter(writer, 800);
			this.lines = new LinkedBlockingDeque<ChatLine>(256);
		}
		public void close() {
			try {
				writer.write("--- Log closed " + new Date());
				writer.newLine();
				writer.flush();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		public void log(final String nick, final String line) {
			try {
				if (lines.remainingCapacity() == 0) {
					lines.pollLast();
				}
				ChatLine chatline = new ChatLine(nick, line);
				lines.offerFirst(chatline);
				writer.write(chatline.toString());
				writer.newLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		public void logAction(final String line) {
			try {
				if (lines.remainingCapacity() == 0) {
					lines.pollLast();
				}
				ActionLine action = new ActionLine(line);
				lines.offerFirst(action);
				writer.write(action.toString());
				writer.newLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		public void flush() {
			try {
				writer.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private File logdir;
	private Map<String, ChannelLog> channels;

	protected ChatLogger() { }

	public ChatLogger(final File logdir) {
		System.out.println(new Date() + " New ChatLogger logging into " + logdir.toString());
		this.logdir = logdir;
		this.channels = new HashMap<String, ChannelLog>(5);
	}

	public void joinChannel(final String _channel) {
		String channel = _channel.toLowerCase();
		if (channels.containsKey(channel)) {
			ChannelLog tmp = channels.get(channel);
			tmp.close();
		}
		File outputfile = getLogFile(channel);
		try {
			ChannelLog log = new ChannelLog(new FileWriter(outputfile, true));
			channels.put(channel.toLowerCase(), log);
			log.writer.write("--- Log opened " + new Date());
			log.writer.newLine();
			log.writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void leaveChannel(final String _channel) {
		ChannelLog log = channels.get(_channel.toLowerCase());
		if (log != null) {
			System.out.println(new Date() + " Closing " + _channel + " log");
			log.close();
		}
	}

	private File getLogFile(final String channel) {
		//return new File(logdir, date + File.separatorChar + channel);
		return new File(logdir, channel.toLowerCase().replaceAll("[#&]", "") + ".log");
	}

	/**
	 * Logs specified line for channel. If channel is null, writes
	 * events into all channel logs.
	 * @param channel Channel name or null
	 * @param nick Nickname of the one generating the event
	 * @param line Message to log
	 */
	public void log(final String channel, String nick, String line) {
		log(channel, nick, line, false);
	}
	public void log(final String channel, String nick, String line, boolean flush) {
		if (channel == null) {
			for (ChannelLog log : channels.values()) {
				log.log(nick, line);
				if (flush) log.flush();
			}
		} else {
			ChannelLog output = channels.get(channel.toLowerCase());
			if (output != null) {
				output.log(nick, line);
				if (flush) output.flush();
			}
		}
	}
	public void logAction(final String channel, String line) {
		logAction(channel, line, false);
	}
	public void logAction(final String channel, String line, boolean flush) {
		if (channel == null) {
			for (ChannelLog log : channels.values()) {
				log.logAction(line);
				if (flush) log.flush();
			}
		} else {
			ChannelLog output = channels.get(channel.toLowerCase());
			if (output != null) {
				output.logAction(line);
				if (flush) output.flush();
			}
		}
	}

	/**
	 * Closes all open logs
	 */
	public void closeAll() {
		System.out.println(new Date() + " ChatLogger.closeAll");
		for (ChannelLog log : channels.values()) {
			log.close();
		}
		channels.clear();
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		closeAll();
	}

	public ChatLine getLastLine(final String channel) {
		ChannelLog log = channels.get(channel.toLowerCase());
		ChatLine retval = null;
		if (log != null) {
			retval = log.lines.peek();
		}
		return retval;
	}

	public Iterator<ChatLine> getLines(final String channel) {
		ChannelLog log = channels.get(channel.toLowerCase());
		if (log != null) {
			return log.lines.iterator();
		}
		// Return empty iterator
		return new Iterator<ChatLine>() {
			public boolean hasNext() {
				return false;
			}
			public ChatLine next() {
				return null;
			}
			public void remove() {
			}
		};
	}
}
