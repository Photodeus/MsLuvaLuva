// @description Search and display Age/Sex/Location information about a Popmundo character.
// @description Querying A/S/L for anyone is so lame, that's why this is so cool.  
// @example !who
// @example !who Jonas Zapatero
// @example !who 3 Mark Ward
var line = ""+bot.fetchUrl("http://popodeus.com/namesearch/find.jsp?asl=" + bot.encode(param));
if (line) bot.sendMessage(channel, line);
