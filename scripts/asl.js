// @description Search and display Age/Sex/Location information about a Popmundo character.
// @description Querying A/S/L for anyone is so lame, that's why this is so cool.  
// @example !who
// @example !who Jonas Zapatero
// @example !who 3 Mark Ward
var line = API.getPageAsText("http://popodeus.com/namesearch/find.jsp?asl=" + API.encode(param));
if (line) API.say(channel, line);
