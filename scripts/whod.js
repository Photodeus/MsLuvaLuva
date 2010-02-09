// @description Search and display information about deceased Popmundo characters 
// @example !whod name of dead character
if (param == '-help') {
	API.say(channel, "!whod [number] [+]name - Search in list for deceased characters");
} else {
	var p = API.encode(param);
	var line = API.getPageAsText("http://popodeus.com/namesearch/find.jsp?deceased=include&q="+p);
	if (line) {
		API.say(channel, line);
		var lnick = "" + nick.toLowerCase();
		if (line.indexOf("1:") == 0) {
			// reply contained multiple number choices, so we store the search parameters
			// for later use in whofollowup.js
			API.setValue("who."+lnick+"!"+ident+"@"+host, param);
			// Also store time so we know when this query was done
			API.setValue("who.time."+lnick+"!"+ident+"@"+host, parseInt(new Date().getTime()/1000));
			API.setValue("who."+lnick+".dflag", true);
		} else {
			// remove any query
			API.removeValue("who."+lnick+"!"+ident+"@"+host);
			API.removeValue("who.time."+lnick+"!"+ident+"@"+host);
			API.removeValue("who."+lnick+".dflag");
		}
	}
}