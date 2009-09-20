// Listens to lines consisting of only a number
if (message.match(/^\d+$/)) {
	// starts with a number
	var lnick = "" + nick.toLowerCase();
	var lastq = API.getValue("who."+lnick+"!"+ident+"@host");
	var time = API.getValue("who.time."+lnick+"!"+ident+"@host");
	//API.info(nick + " lastq => " + lastq);
	if (lastq && time && (new Date().getTime()/1000-time < 30)) {
		var p = API.encode(message + " " + lastq);
		var line = API.getPageAsText("http://popodeus.com/namesearch/find.jsp?q="+p);
		//API.info(line);
		if (line != null) {
			API.say(channel, line);
			cancel = true;
			API.setValue("who."+lnick+"!"+ident+"@host", null);
		}
	}
}
// No more who queries after this
//API.removeValue("who."+lnick+"!"+ident+"@host");
//API.setValue("who."+lnick+"!"+ident+"@host", null);
