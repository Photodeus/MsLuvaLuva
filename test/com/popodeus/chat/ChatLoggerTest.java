package com.popodeus.chat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.File;
import java.util.Iterator;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * ChatLogger Tester
 */
public class ChatLoggerTest {
	private ChatLogger logger;
	private	File logdir;

	@Before
    public void setUp() throws Exception {
		Logger.getLogger(ChatLogger.class.getName()).setLevel(Level.OFF);
		logdir = new File("/tmp", "chatlogger");
		logdir.mkdirs();
		logger = new ChatLogger(logdir);
    }

	@After
    public void tearDown() throws Exception {
		logger.closeAll();
		for (File f : logdir.listFiles()) {
			f.delete();
		}
		logdir.delete();
	}

    /**
     *
     * Method: joinChannel(final String _channel)
     *
     */
	@Test
    public void testJoinChannel() throws Exception {
		final String chan = "#Testing";
		final String chanlog = "testing.log";
		final String nickname = "Nickname";
		final File logfile = new File(logdir, chanlog);

		assertFalse(logger.hasJoined(chan));
		assertFalse(logfile.exists());

		logger.joinChannel(chan);
		assertTrue(logger.hasJoined(chan));
		assertFalse(logger.hasJoined("#NotHere"));
		assertTrue(logfile.exists());
		// Not true, the logger writes "--- Log opened " + new Date()
		// assertEquals(0L, logfile.length());
		long len = logfile.length();
		assertTrue(len > 0);

		assertNull(logger.getLastActivity(chan, nickname));
		logger.log(chan, nickname, "Saying something");
		assertNotNull(logger.getLastLine(chan));
		assertEquals(nickname, logger.getLastLine(chan).nick);
		assertNotNull(logger.getLastActivity(chan, nickname));
		logger.leaveChannel(chan);
		// Log should have grown
		assertTrue(logfile.length() > len);

		//Logger.getLogger(ChatLogger.class.getName()).setLevel(Level.FINEST);
		logger.joinChannel(chan);
		//System.out.println("Last line said on " + chan + ": " + logger.getLastLine(chan));
		assertNotNull(logger.getLastActivity(chan, nickname));
		logger.leaveChannel(chan);
		//Logger.getLogger(ChatLogger.class.getName()).setLevel(Level.OFF);
	}
	
	/**
     *
     * Method: leaveChannel(final String _channel)
     *
     */
	@Test
    public void testLeaveChannel() throws Exception {
		logger.joinChannel("#Testing");
		assertTrue(logger.hasJoined("#Testing"));
		logger.leaveChannel("#Testing");
		assertFalse(logger.hasJoined("#Testing"));
		logger.joinChannel("#Testing-2");
		assertTrue(logger.hasJoined("#Testing-2"));
		assertFalse(logger.hasJoined("#Testing"));
		logger.leaveChannel("#Testing-2");
		assertFalse(logger.hasJoined("#Testing-2"));
    }

    /**
     *
     * Method: log(final String channel, String nick, String line)
     *
     */
    public void testLogForChannelNickLine() throws Exception {
        //TODO: Test goes here...
		//assertFalse(true);
	}

    /**
     *
     * Method: log(final String channel, String nick, String line, boolean flush)
     *
     */
    public void testLogForChannelNickLineFlush() throws Exception {
        //TODO: Test goes here...
    }

    /**
     *
     * Method: logAction(final String channel, final String line, final String nick)
     *
     */
    public void testLogActionForChannelLineNick() throws Exception {
        //TODO: Test goes here...
    }

    /**
     *
     * Method: logAction(final String channel, final String line, final String nick, boolean flush)
     *
     */
    public void testLogActionForChannelLineNickFlush() throws Exception {
        //TODO: Test goes here...
    }

    /**
     *
     * Method: closeAll()
     *
     */
	@Test
    public void testCloseAll() throws Exception {
		logger.joinChannel("#Testing1");
		logger.joinChannel("#Testing2");
		assertTrue(logger.hasJoined("#Testing1"));
		assertTrue(logger.hasJoined("#Testing2"));
		logger.closeAll();
		assertFalse(logger.hasJoined("#Testing1"));
		assertFalse(logger.hasJoined("#Testing2"));
    }

    /**
     *
     * Method: getLastLine(final String channel)
     *
     */
	@Test
    public void testGetLastLine() throws Exception {
		final String helloworld = "Hello world!";
		final String channel = "#Testing";
		final String nickname = "Someone";

		logger.joinChannel(channel);
		logger.log(channel, nickname, helloworld);
		assertNotNull(logger.getLastLine(channel));
		assertNotNull(logger.getLastLine(channel).line.endsWith("<" + nickname + "> " + helloworld));
		logger.leaveChannel(channel);
    }

    /**
     *
     * Method: getLastActivity(final String _channel, final String nick)
     *
     */
	@Test
    public void testGetLastActivity1() throws Exception {
		final String xchan = "x1";
		final String someone = "someone";
		
		logger.joinChannel(xchan);
		assertNull(logger.getLastActivity(xchan, someone));
		logger.log(xchan, someone, "Hello");
		assertNotNull(logger.getLastActivity(xchan, someone));
	}

	@Test
	public void testGetLastActivity2() throws Exception {
		final String xchan2 = "x2";
		final String nickname = "someone";
		final String nickname2 = "someonelse";

		logger.joinChannel(xchan2);
		assertNull(logger.getLastActivity(xchan2, nickname));

		logger.logAction(xchan2, nickname, "* " + nickname + " says Hello World!");
		logger.logAction(xchan2, nickname2, "* " + nickname2 + " says Hello Moon!");
		assertNotNull(logger.getLastActivity(xchan2, nickname));
		assertNotNull(logger.getLastLine(xchan2));
		//System.out.println("logger.getLastLine(chan = " + logger.getLastLine(xchan2));
		//System.out.println("logger.getLastActivity(chan, nickname) = " + logger.getLastActivity(xchan2, nickname));
		logger.leaveChannel(xchan2);

		logger.joinChannel(xchan2);
		assertNotNull(logger.getLastActivity(xchan2, nickname));
		assertNotNull(logger.getLastActivity(xchan2, nickname2));
	}

    /**
     *
     * Method: getLines(final String channel)
     *
     */
    public void testGetLines() throws Exception {
        //TODO: Test goes here...
    }

    /**
     *
     * Method: nickChange(final String oldNick, final String newNick)
     *
     */
	@Test
    public void testNickChange() throws Exception {
		final String chan = "foobar";

		logger.joinChannel(chan);

		assertNull(logger.getLastActivity(chan, "Chameleon"));

		logger.log(chan, "Chameleon", "I am talking");
		assertNotNull(logger.getLastActivity(chan, "Chameleon"));
		assertNull(logger.getLastActivity(chan, "Gorilla"));
		
		logger.nickChange("Chameleon", "Gorilla");

		assertNotNull(logger.getLastActivity(chan, "Chameleon"));
		assertNotNull(logger.getLastActivity(chan, "Gorilla"));
		assertEquals(logger.getLastActivity(chan, "Gorilla"), logger.getLastActivity(chan, "Chameleon"));
		
		logger.leaveChannel(chan);
	}

    /**
     *
     * Method: getLineCounts(final String channel, final int... minutes)
     *
     */
	@Test
    public void testGetLineCounts() throws Exception {
		final String chan = "foo";
		
		logger.joinChannel(chan);
		int count = 0;
		Iterator it = logger.getLines(chan);
		while (it.hasNext()) {
			it.next();
			count++;
		}
		logger.logAction(chan, "Someone", "Hello world");
		logger.log(chan, "SomeoneElse", "Heya");
		int count2 = 0;
		it = logger.getLines(chan);
		while (it.hasNext()) {
			it.next();
			count2++;
		}
		assertTrue(count2 == count+2);
		logger.leaveChannel(chan);
	}

    /**
     *
     * Method: log(final String nick, final String line)
     *
     */
    public void testLogForNickLine() throws Exception {
        //TODO: Test goes here...
    }

    /**
     *
     * Method: logAction(final String line)
     *
     */
    public void testLogActionLine() throws Exception {
        //TODO: Test goes here...
    }

    /**
     *
     * Method: flush()
     *
     */
    public void testFlush() throws Exception {
        //TODO: Test goes here...
    }

    /**
     *
     * Method: touch(final String nick)
     *
     */
    public void testTouchNick() throws Exception {
        //TODO: Test goes here...
    }

    /**
     *
     * Method: touch(final String nick, final long timestamp)
     *
     */
    public void testTouchForNickTimestamp() throws Exception {
        //TODO: Test goes here...
    }

    /**
     *
     * Method: readLine()
     *
     */
    public void testReadLine() throws Exception {
        //TODO: Test goes here...
    }


    /**
     *
     * Method: getLogFile(final String channel)
     *
     */
    public void testGetLogFile() throws Exception {
        //TODO: Test goes here...
        /*
        try {
           Method method = ChatLogger.class.getMethod("getLogFile", final.class);
           method.setAccessible(true);
           method.invoke(<Object>, <Parameters>);
        } catch(NoSuchMethodException e) {
        } catch(IllegalAccessException e) {
        } catch(InvocationTargetException e) {
        }
        */
        }

    /**
     *
     * Method: addit(final ChatLine line)
     *
     */
    public void testAddit() throws Exception {
        //TODO: Test goes here...
        /*
        try {
           Method method = ChatLogger.class.getMethod("addit", final.class);
           method.setAccessible(true);
           method.invoke(<Object>, <Parameters>);
        } catch(NoSuchMethodException e) {
        } catch(IllegalAccessException e) {
        } catch(InvocationTargetException e) {
        }
        */
	}
}
