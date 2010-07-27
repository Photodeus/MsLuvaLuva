// @description	Shows definitions from Urban dictionary
// @example !ud jinxed

var pg = 1, num = 1;
if (param.match("\\d+\\s+.+")) {
	var parts = param.split(" ", 2);
	num = parseInt(parts[0]);
	param = parts[1];
	pg = 1+Math.floor((num-1)/7);
}
if (param.match("^[^0-9]+\\s+\\d+$")) {
	API.notice(nick, "hint, the correct format is !ud <number> search words");
}

var url = "http://www.urbandictionary.com/define.php?term="+API.encode(param)+"&page="+pg;
var page = API.getPage(url);
if (page) {
	var table = API.getElementById(page, "entries");
	
	if (table == null || table == "") {
		response = "Nothing found for " + param;
		response_to = channel;
	} else {
		var idx = API.getElementsByAttribute(table, "td", "class", "index");
		var word = API.getElementsByAttribute(table, "td", "class", "word");
		var def = API.getElementsByAttribute(table, "div", "class", "definition");
		var exm = API.getElementsByAttribute(table, "div", "class", "example");
		var numnum = num + ".";
		var which = (num-1)%7;
	
		if (idx.size() < which) {
	
		} else {
			var tablecell = idx.get(which);
			var id = tablecell.asText();
			if (id.equals(numnum)) {
				response = "";
				response += word.get(which).asText().trim() + ": ";
				var deffo = def.get(which).asText().replaceAll("\n", ". ");
				var example = exm.get(which).asText().replaceAll("\n", " / ").replaceAll("\\s+", " ").trim();
				response += deffo + ". ";
				response += "Example: " + example;
				response_to = channel;
			}
		}
	}
	page = null;
}