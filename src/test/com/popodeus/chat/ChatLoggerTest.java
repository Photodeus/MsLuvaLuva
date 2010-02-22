package com.popodeus.chat;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;

import java.io.File;

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
        logger.joinChannel("#Testing");
		assertTrue(logger.hasJoined("#Testing"));
		assertFalse(logger.hasJoined("#NotHere"));
		assertTrue(new File(logdir, "testing.log").exists());
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
    public void testGetLastActivity() throws Exception {
        //logger.getLastActivity()
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
        //TODO: Test goes here...
    }

    /**
     *
     * Method: getLineCounts(final String channel, final int... minutes)
     *
     */
    public void testGetLineCounts() throws Exception {
        //TODO: Test goes here...
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
