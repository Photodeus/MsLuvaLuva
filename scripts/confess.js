// @description Tell everyone a sad sad confession.
// @level adult

var p = API.getValue("confessions");
if (!p) {
	// TODO use up all 10 random confessions before pulling new page
	p = API.getPage("http://confessions.grouphug.us/random");
	// API.setValue("confessions", p);
}

if (p) {
	var div = API.getFirstByXpath(p, "//div[@class='content']/p");
	if (div) {
		var msg = (""+div.asText()).replace(/\u8216|&#8216;/g, "'");
		if (msg.match(/cancer|died|kill myself/i)) {
			response = "Mmm, sorry. No new confessions have arrived since last time."; 
			response_to = channel;
		} else {
			response = msg;
			response_to = channel;
		}
	}
}
