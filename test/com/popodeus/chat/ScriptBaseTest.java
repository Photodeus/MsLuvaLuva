package com.popodeus.chat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.script.ScriptException;

import static org.junit.Assert.*;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Seppo
 * Date: 2010-aug-27
 * Time: 17:44:32
 */
public class ScriptBaseTest {
	private static final String theresult = "The result is: 3";
	private static final String NO_TIMEOUT_META_SCRIPT = "// @notimeout\n" +
			"response = 'OK';\nresponse_to = channel;";
	private static final String NO_TIMEOUT_SCRIPT = "no_timeout = true;\n" +
			"response = 'OK';\nresponse_to = channel;";
	private static final String TIMEOUT_SCRIPT = "no_timeout = false;\n" +
			"response = 'OK';\nresponse_to = channel;";
	private static final String NORMAL_SCRIPT = "var a = 1;\n" +
			"a += 2;\n" +
			"response = 'The result is: ' + a;\n" +
			"response_to = channel;\n";
	private static final String SETVALUE_SCRIPT = "var a = API.getValue('memory');\n" +
			"if (!a) a = 100;\n" +
			"else a = parseInt(a) + 2;" +
			"API.setValue('memory', a);\n"
			;
	private static final String BROKEN_SCRIPT = "var x = a + b + c; // using undefined variables\n" ;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		//
	}

	@Test
	public void testGetLastRun() throws Exception {
		ScriptAPI api = new ScriptAPIAdaptor();
		ScriptBase script = new TriggerScript("testing", new StringReader(NORMAL_SCRIPT));
		long lastrun = script.getLastRun();
		assertEquals(lastrun, 0L);
		assertFalse(script.runScript(api, new ScriptInvokerParameters("JohnDoe", "johndoe", "example.com", "!test Hello World", "#test", "!test", "Hello World")));
		Thread.sleep(100);
		assertTrue(lastrun < script.getLastRun());
	}

	@Test
	public void testTimeout() throws Exception {
		ScriptBase script = new TriggerScript("testing", new StringReader(NORMAL_SCRIPT));
		script.setTimeout(100);
		assertTrue(script.getTimeout() == 100);
		script.setTimeout(400);
		assertTrue(script.getTimeout() == 400);
	}

	@Test
	public void testTimeoutFromHeader() throws Exception {
		ScriptBase script = new TriggerScript("testing", new StringReader(
				"// @timeout 4\n" +
				"var a = 10;\n"
		));
		assertTrue(script.getTimeout() == 4000);

		script = new TriggerScript("testing", new StringReader(
				"// @timeout 11000\n" +
				"var a = 10;\n"
		));
		assertTrue(script.getTimeout() == 11000);
	}

	@Test
	public void testHasTimeoutPassed() throws Exception {
		ScriptBase script = new TriggerScript("testing", new StringReader(NORMAL_SCRIPT));
		ScriptAPI api = new ScriptAPIAdaptor();
		// Default timeout should be other than zero
		assertTrue(script.getTimeout() > 0);
		// Make sure script runs
		assertFalse(script.runScript(api, new ScriptInvokerParameters("JohnDoe", "johndoe", "example.com", "!test Hello World", "#test", "!test", "Hello World")));
		// Timeout should by no means have passed yet
		assertFalse(script.hasTimeoutPassed());
		// Set a short timeout
		script.setTimeout(40);
		// Wait at least double the time
		Thread.sleep(100);
		// No we should be good for another go
		assertTrue(script.hasTimeoutPassed());
		// Run again
		assertFalse(script.runScript(api, new ScriptInvokerParameters("JohnDoe", "johndoe", "example.com", "!test Hello World", "#test", "!test", "Hello World")));
		// Verify that the method returns a proper value again, that there's no odd stuck or cached value
		assertFalse(script.hasTimeoutPassed());
	}
	@Test
	public void testRunScriptLongParams() throws Exception {
		ScriptBase script = new TriggerScript("testing", new StringReader(NORMAL_SCRIPT));
		ScriptAPI api = new ScriptAPIAdaptor() {
			@Override
			public void say(String target, String line) {
				assertEquals(target, "#test");
				assertEquals(line, theresult);
			}
		};
		assertFalse(script.runScript(api, new ScriptInvokerParameters("JohnDoe", "johndoe", "example.com", "!test Hello World", "#test", "!test", "Hello World")));
		assertEquals(3d, script.b.get("a"));
		assertEquals(script.b.get(ScriptBase.SCRIPTVAR_RESPONSE), theresult);
	}

	@Test
	public void testRunScriptShortParams() throws Exception {
		ScriptBase script = new TriggerScript("testing", new StringReader(NORMAL_SCRIPT));
		ScriptAPI api = new ScriptAPIAdaptor() {
			@Override
			public void say(String target, String line) {
				assertEquals(target, "#test");
				assertFalse(target.equals("test"));
				assertEquals(line, theresult);
			}
		};
		assertFalse(script.runScript(api, "JohnDoe", "johndoe", "example.com", "!test Hello World", "#test", "!test", "Hello World"));
		assertEquals(3d, script.b.get("a"));
		assertEquals(script.b.get(ScriptBase.SCRIPTVAR_RESPONSE), theresult);
	}


	@Test
	public void testRunScriptNoTimeoutHeader() throws Exception {
		ScriptAPI api = new ScriptAPIAdaptor();
		ScriptBase script = new TriggerScript("testing", new StringReader(NO_TIMEOUT_META_SCRIPT));
		assertTrue(script.getTimeout() == 0);
		assertTrue(script.runScript(api, new ScriptInvokerParameters("JohnDoe", "johndoe", "example.com",
				"!test", "#jUnit-test", "", "")));
	}

	@Test
	public void testRunScriptNoTimeoutInCode() throws Exception {
		ScriptAPI api = new ScriptAPIAdaptor();
		ScriptBase script = new TriggerScript("testing", new StringReader(NO_TIMEOUT_SCRIPT));
		assertFalse(script.getTimeout() == 0);
		assertTrue(script.runScript(api, new ScriptInvokerParameters("JohnDoe", "johndoe", "example.com",
				"!test", "#jUnit-test", "", "")));
	}

	@Test
	public void testRunScriptNormal() throws Exception {
		ScriptAPI api = new ScriptAPIAdaptor();
		ScriptBase script = new TriggerScript("testing", new StringReader(TIMEOUT_SCRIPT));
		assertFalse(script.runScript(api, new ScriptInvokerParameters("JohnDoe", "johndoe", "example.com",
				"!test", "#jUnit-test", "", "")));
	}

	@Test
	public void testRunScriptTooShort() throws Exception {
		ScriptAPI api = new ScriptAPIAdaptor();
		ScriptBase script = new TriggerScript("testing", new StringReader(
				"var n = 0;"
		));
		assertFalse(script.runScript(api, new ScriptInvokerParameters("JohnDoe", "johndoe", "example.com",
				"!test", "#jUnit-test", "", "")));
	}

	@Test
	public void testGetName() throws Exception {
		ScriptBase script = new TriggerScript("testing", new StringReader("var n = 0;"));
		assertEquals("testing", script.getName());
	}

	@Test
	public void testAPIProtect() throws Exception {
		// The protection is a joke and really not needed for anything
		// There are far more worse ways to override it and cause harm
		ScriptBase script = new TriggerScript("testing", new StringReader(
				"var oAPI = API;\n" +
				"API.say('#Test', 'Yo');\n" +
				"API = { say: function(a, b) { oAPI.say('#Test', 'Fail!'); } }\n" +
				"API.say('#Test', 'Yo');\n"
		));
		ScriptAPI api = new ScriptAPIAdaptor() {
			@Override
			public void say(String target, String line) {
				assertEquals("Yo", line);
			}
		};
		final ScriptInvokerParameters params = new ScriptInvokerParameters("JohnDoe", "johndoe", "example.com", "!test Hello World", "#test", "!test", "Hello World");
		assertFalse(script.runScript(api, params));
	}

	@Test
	public void testVariablePersistence() throws Exception {
		ScriptBase script = new TriggerScript("testing", new StringReader(SETVALUE_SCRIPT));
		ScriptAPI api = new ScriptAPIAdaptor() {
			Map<String, Object> vals = new HashMap<String, Object>();
			@Override
			public void setValue(String key, Object value) {
				//System.out.println("ScriptBaseTest.setValue: " + key + " = " + value + " [" + value.getClass() + "]");
				vals.put(key, value);
			}

			@Override
			public Object getValue(String key) {
				Object retval = vals.get(key);
				//System.out.println("ScriptBaseTest.getValue: " + key + " = " + retval + " [" + (retval == null ? "" : retval.getClass()) + "]");
				return retval;
			}
		};
		final ScriptInvokerParameters params = new ScriptInvokerParameters("JohnDoe", "johndoe", "example.com", "!test Hello World", "#test", "!test", "Hello World");
		assertFalse(script.runScript(api, params));
		//System.out.println(script.b.get("a").getClass());
		assertEquals(100d, script.b.get("a"));
		assertFalse(script.runScript(api, params));
		assertEquals(102d, script.b.get("a"));
		assertFalse(script.runScript(api, params));
		assertEquals(104d, script.b.get("a"));
	}

	@Test(expected = ScriptException.class)
	public void testBrokenScript() throws Exception {
		ScriptAPI api = new ScriptAPIAdaptor();
		ScriptBase script = new TriggerScript("testing", new StringReader(BROKEN_SCRIPT));
		// Will throw a ScriptException 
		script.runScript(api, new ScriptInvokerParameters("JohnDoe", "johndoe", "example.com", "!test Hello World", "#test", "!test", "Hello World"));
	}
}
