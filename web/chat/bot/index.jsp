<%@ page import="java.io.File, java.util.Arrays, java.io.FileReader, java.nio.CharBuffer, java.io.BufferedReader, java.util.List, java.util.ArrayList" %>
<html>
<head>
	<title>#Popmundo bot commands</title>
<style type="text/css">
html {
	padding: 20px;
	background-color: #BEB;
}
body {
	padding: 10px 20px;
	color: black;
	background-color: white;
	border: 2px solid #222;
}
code {
	color: #008;
}
</style>
</head>
<body>
<h1>#Popmundo chat Bot commands</h1>
<%
	String basepath = "/var/local/ircbot/scripts";
	File[] scripts = new File(basepath).listFiles();
	Arrays.sort(scripts);
	StringBuilder sb = new StringBuilder(4*1024);
	for (File f : scripts) {
		//List<String> lines = new ArrayList<String>(64);
		StringBuilder local = new StringBuilder(1024);
		boolean keep = true;
		if (f.getName().endsWith(".js")) {
			local.append("<h2>!"+f.getName().replace(".js", "")+"</h2>\n");
			BufferedReader r = new BufferedReader(new FileReader(f));
			String line;
			local.append("<pre>");
			while ( (line = r.readLine()) != null) {
				if (line.contains("@private")) {
					keep = false;
					break;
				}
				if (line.contains("@description")) {
					local.append(line.replace("//", "").replace("@description", "").trim());
					local.append("\n");
				}
				if (line.contains("@example")) {
					local.append("<code>"+line.replace("//", "").replace("@example", "").trim()+"</code>");
					local.append("\n");
				}
				if (!line.contains("//")) break;
			}
			r.close();
			local.append("</pre>");
		}
		if (keep) sb.append(local);
	}
%><%=sb.toString()%>
<p>
MsLuvaLuva is a very simple bot. If you're interested in her history and source code,
please visit <a href="http://wiki.github.com/Photodeus/MsLuvaLuva">MsLuvaLuva Github project page</a>.
</p>
</body>
</html>
