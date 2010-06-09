// @description Spill the beans on someone
API.clearScriptCacheFor('confess');

var c = API.getValue("confessions");
if (!c) {
	var page = API.getPage("http://confessions.grouphug.us/random");
	if (page) {
		var div = API.getElementById(page, "squeeze");
		if (div) {
			var list = page.getElementsByAttribute(div, "p", "", "");
			response = list;
			response_to = channel;
		}
	}
}
