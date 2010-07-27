// @description Search and display information about a Popmundo character
// @example !who
// @example !who Jonas Zapatero
// @example !who 3 Mark Ward
if (param == '-help') {
	API.say(channel, "!who [number] [+]name or character id (+ means to match exact name length)");
	
} else {
	var p = API.encode(param);
	if (param.equalsIgnoreCase(API.getBotNick())) {
		API.action(channel, "is a bitch of the highest caliber, member of the Evil Bot Parade. Location: Here, you stupid mofo. Attitude: Up yours! Affected by: Frustration. Entered: Many days ago. I was here before you, sucker. http://popodeus.com/chat/bot/");
	} else {
		var line = API.getPageAsText("http://popodeus.com/namesearch/find.jsp?q="+p);
		if (line) {
			API.say(channel, line);
			var lnick = "" + nick.toLowerCase();
			if (line.indexOf("1:") == 0) {
				// reply contained multiple number choices, so we store the search parameters
				// for later use in whofollowup.js
				API.setValue("who."+lnick+"!"+ident+"@"+host, param);
				// Also store time so we know when this query was done
				API.setValue("who.time."+lnick+"!"+ident+"@"+host, parseInt(new Date().getTime()/1000));
				// make sure dflag is not set. Only the !whod script sets it
				API.removeValue("who."+lnick+".dflag");
			} else {
				// remove any query
				API.removeValue("who."+lnick+"!"+ident+"@"+host);
				API.removeValue("who.time."+lnick+"!"+ident+"@"+host);
			}
		}
	}
}