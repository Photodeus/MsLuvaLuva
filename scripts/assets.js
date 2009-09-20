// @description Search and display information about a Popmundo character's assets
// @example !assets
// @example !assets Jonas Zapatero
// @example !assets 3 Mark Ward
var line = ""+API.fetchUrl("http://popodeus.com/namesearch/find.jsp?a=" + API.encode(param));
if (line) API.say(channel, line);
