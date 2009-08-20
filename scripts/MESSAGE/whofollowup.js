// Listens to lines consisting of only a number
if (message.match(/^\d+$/)) {
	// starts with a number
	var lnick = "" + nick.toLowerCase();
	var lastq = bot.getValue("who."+lnick+"!"+ident+"@host");
	//bot.getLog().info(nick + " lastq => " + lastq);
	if (lastq) {
		var p = bot.encode(message + " " + lastq);
		var line = bot.fetchUrl("http://popodeus.com/namesearch/find.jsp?q="+p);
		//bot.getLog().info(line);
		if (line != null) {
			bot.sendMessage(channel, line);
			cancel = true;
		}
	}
}
// No more who queries after this
bot.removeValue("who."+lnick+"!"+ident+"@host");
