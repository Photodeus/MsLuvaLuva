package test.com.popodeus.chat;

import javax.script.Bindings;

import java.io.*;
import java.util.logging.Logger;

/**
 * photodeus
 * Nov 10, 2009
 * 12:53:59 AM
 */
public class SaveLoadVars {
	private static Logger log = Logger.getLogger("test.com.popodeus.chat.SaveLoadVars");

	static String scriptDataDir = "/home/photodeus/Documents/devel/java/MsLuvaLuva-mini/bin/scriptdata";

	public static void loadScriptVars() throws Exception {
		File fo = new File(scriptDataDir, "variables.dat");
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fo));
		for (File f : new File("/home/photodeus/Documents/devel/java/MsLuvaLuva-mini/bin").listFiles(new FileFilter() {
			public boolean accept(final File f) {
				return f.getName().startsWith("variables-") && f.getName().endsWith(".dat");
			}
		})) {
			System.out.println("=======" + f);
			ObjectInputStream ois = null;
			try {
				ois = new ObjectInputStream(new FileInputStream(f));
				while (ois.available() > 0) {
					String key = ois.readUTF();
					Object value = ois.readObject();
					System.out.println(key + " => " + value);
					oos.writeUTF(key);
					oos.writeObject(value);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (ois != null) {
					try {
						ois.close();
					} catch (IOException e) {
					}
				}
			}
		}
		if (oos != null) {
			try {
				oos.close();
			} catch (IOException e) {
			}
		}
	}

	public static void main(String[] args) {
		try {
			loadScriptVars();
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*
		RhinoScriptEngine engine = new RhinoScriptEngine();
		Bindings b = new SimpleBindings();
		b.put("stest", "value");
		b.put("itest", 1234);

		try {
			CompiledScript csc = engine.compile(
					"var someval = 444; var addition = itest;\n" +
					"var mys = 'Mypys';\n" +
					"summa = someval + itest;"
			);
			csc.eval(b);

			testVar(b, "stest");
			testVar(b, "mys");
			testVar(b, "someval");
			testVar(b, "addition");
			testVar(b, "summa");

		} catch (ScriptException e) {
			System.err.println(e);
		}
		*/
	}

	private static void testVar
			(
					final Bindings b,
					final String varname) {
		Object o = b.get(varname);
		System.out.println(varname + ": " + o + (o == null ? "" : ", " + o.getClass().getName()));
	}
}
