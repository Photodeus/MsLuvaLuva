//if (bot.getValue())
if (message.match(/^\d+$/)) {
	// starts with a number
	var lastq = bot.getValue("who."+nick+"!"+ident+"@host");
	bot.getLog().info(nick + " lastq: " + lastq);
	if (lastq) {
		var p = bot.encode(message + " " + lastq);
		var line = ""+bot.fetchUrl("http://popodeus.com/namesearch/find.jsp?q="+p);
		if (line) {
			bot.setValue("who."+nick+"!"+ident+"@host", null);
			bot.sendMessage(channel, line);
			cancel = true;
		}
	}
}