// @description Search and display information about a Popmundo character
// @example !who
// @example !who Jonas Zapatero
// @example !who 3 Mark Ward
var p = API.encode(param);
var line = API.getPageAsText("http://popodeus.com/namesearch/find.jsp?q="+p);
API.info(line);
if (line) {
	API.say(channel, line);
	var lnick = "" + nick.toLowerCase();
	if (line.indexOf("1:") == 0) {
		// reply contained multiple number choices
		API.setValue("who."+lnick+"!"+ident+"@host", param);
		API.setValue("who.time."+lnick+"!"+ident+"@host", Math.floor(new Date().getTime()/1000));
	} else {
		// remove any query
		API.setValue("who."+lnick+"!"+ident+"@host", null);
		API.setValue("who.time."+lnick+"!"+ident+"@host", null);
	}
}
