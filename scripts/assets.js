// @description Search and display information about a Popmundo character's assets
// @example !assets
// @example !assets Jonas Zapatero
// @example !assets 3 Mark Ward
var line = ""+bot.fetchUrl("http://popodeus.com/namesearch/find.jsp?a=" + bot.encode(param));
if (line) bot.sendMessage(channel, line);
