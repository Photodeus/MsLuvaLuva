// @description Search and display information about a Popmundo character
// @example !who
// @example !who Jonas Zapatero
// @example !who 3 Mark Ward
var p = bot.encode(param);
var line = ""+bot.fetchUrl("http://popodeus.com/namesearch/find.jsp?q="+p);
if (line) {
	bot.sendMessage(channel, line);
	var lnick = "" + nick.toLowerCase();
	if (line.indexOf("1:") == 0) {
		// reply contained multiple number choices
		bot.setValue("who."+lnick+"!"+ident+"@host", param);
	} else {
		// remove any query
		bot.setValue("who."+lnick+"!"+ident+"@host", null);
	}
}
