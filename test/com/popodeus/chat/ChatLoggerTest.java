package com.popodeus.chat;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.File;
import java.util.Iterator;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * ChatLogger Tester
 */
public class ChatLoggerTest extends TestCase {
	private ChatLogger logger;
	private	File logdir;
	
	public ChatLoggerTest(String name) {
        super(name);
    }
	
    public void setUp() throws Exception {
		Logger.getLogger(ChatLogger.class.getName()).setLevel(Level.OFF);
		logdir =  new File("/tmp", "chatlogger");
		logdir.mkdirs();
		logger = new ChatLogger(logdir);
    }

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

		assertNull(logger.getLastActivity(chan, nickname));
		logger.log(chan, nickname, "Saying something");
		assertNotNull(logger.getLastActivity(chan, nickname));
		logger.leaveChannel(chan);

		logger.joinChannel(chan);
		System.out.println(logger.getLastLine(chan));
		assertNotNull(logger.getLastActivity(chan, nickname));
		logger.leaveChannel(chan);
	}
	
	/**
     *
     * Method: leaveChannel(final String _channel)
     *
     */
    public void testLeaveChannel() throws Exception {
		logger.joinChannel("#Testing");
		assertTrue(logger.hasJoined("#Testing"));
		logger.leaveChannel("#Testing");
		assertFalse(logger.hasJoined("#Testing"));
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
    public void testGetLastActivity1() throws Exception {
		final String xchan = "x1";
		final String someone = "someone";
		
		logger.joinChannel(xchan);
		assertNull(logger.getLastActivity(xchan, someone));
		logger.log(xchan, someone, "Hello");
		assertNotNull(logger.getLastActivity(xchan, someone));
	}

	public void testGetLastActivity2() throws Exception {
		final String xchan2 = "x2";
		final String someone = "someone";

		logger.joinChannel(xchan2);
		assertNull(logger.getLastActivity(xchan2, someone));
		logger.logAction(xchan2, someone, "Hello");
		assertNotNull(logger.getLastActivity(xchan2, someone));
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


    public static Test suite() {
        return new TestSuite(ChatLoggerTest.class);
    }
}
