// Listens to lines consisting of only a number
if (message.match(/^\d+$/)) {
	var lnick = "" + nick.toLowerCase();
	// Check values previously set in who.js and whod.js
	var lastq = API.getValue("who."+lnick+"!"+ident+"@"+host);
	var time = API.getValue("who.time."+lnick+"!"+ident+"@"+host);
	// API.info(nick + " lastq => " + lastq);
	// We expect a reply within 30 seconds, or otherwise we just ignore the command
	if (lastq && time && (new Date().getTime()/1000-time < 30)) {
		var url = "http://popodeus.com/namesearch/find.jsp?";
		// New query is same as calling !who number lastmessage
		var q = "q="+ API.encode(message + " " + lastq);
		if (API.getValue("who."+lnick+".dflag")) {
			// Did we look for deceaced chars
			q = q + "&deceased=include";
		}
		// Query the external name database 
		var line = API.getPageAsText(url + q);
		//API.info(line);
		if (line != null) {
			API.say(channel, line);
			// No further processing of other scripts
			cancel = true;
			// Cleanup
			API.removeValue("who."+lnick+"!"+ident+"@"+host);
			API.removeValue("who."+lnick+".dflag");
		}
	}
}