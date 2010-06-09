//
// This script pulls URL titles and shows them in chat
// TODO needs a way to be disabled somehow. e.g. if API.getConfigAsBoolean('urltitle.enabled.'+channel)

function endsWith(string, suffix)
{
	var lastIndex = string.lastIndexOf(suffix);
	return (lastIndex != -1) && (lastIndex + suffix.length == string.length);
}

// Scan every message for a http:// signature
if (message.indexOf('http://') >= 0) {
	// RegExp cuts out the relevant portion, up until a space 
	var link = message.match(/.*(http:\/\/[^ ]+).*/);
	if (link) {
		link = link[1];
		// Some link suffixes are to be avoided. We can't show any metadata for images and videos
		// For privacy, ignore all Facebook, becasuse it usually contains reallife names
		if (link.indexOf('.jpg') > 0
				|| endsWith(link, '.jpeg')
				|| endsWith(link, '.png')
				|| endsWith(link, '.gif')
				|| endsWith(link, '.flv')
				|| endsWith(link, '.avi')
				|| link.match(/\.facebook\.com/)  
				|| link.match(/uploadpie\.com/)  
				|| link.match(/http:\/\/open\.spotify\.com/)
				|| link.match(/http:\/\/pics\.livejournal\.com/)
				) {
			// do nothing
			// API.info("Link ignored: is on disallowed list. ");
		} else {
			// Youtube videos need special handling
			if (link.indexOf('youtube.com/watch') > 0) {
				var code = link.match(/\?v=([0-9a-zA-Z_\\-]+)/)[1];
				var output = "";
				if (code) {
					// Use Youtube API to fetch video info as XML
					var page = API.getPage("http://gdata.youtube.com/feeds/api/videos/" + code);
					if (page) {
						var title = API.getAsText(page, "//media:title");
						var err = "";
						var attributes;
	
						try {
							if (title) {
								output += title;
							} else {
								output += "Title couldn't be fetched. Blame Connie";
							}
						} catch (e) { err += e + "\n"; }
	
						try {
							var duration = API.getFirstByXpath(page, "//yt:duration");
							if (duration != null) {
								attributes = duration.getAttributes();
								var seconds = parseInt(attributes.getNamedItem("seconds").getNodeValue());
								var minutes = Math.floor(seconds / 60);
								if (minutes >= 1) { seconds = seconds % 60; }
								output += " [" + (minutes < 10 ? "0" : "") + minutes + ":" + (seconds < 10 ? "0" : "") + seconds + "]";
							}
						} catch (e) { err += e + "\n"; }
	
						try {
							var rating = API.getFirstByXpath(page, "//gd:rating");
							if (rating != null) {
								attributes = rating.getAttributes();
								var r = Math.round(parseFloat(attributes.getNamedItem("average").getNodeValue()) * 100.) / 100.;
								var votes = API.formatNum(parseInt(attributes.getNamedItem("numRaters").getNodeValue()));
								output += " Avg.rating: " + r + "★ of " + votes + " votes.";
							}
						} catch (e) { err += e + "\n"; }
	
						try {
							var stats = API.getFirstByXpath(page, "//yt:statistics");
							if (stats != null) {
								attributes = stats.getAttributes();
								var vc = parseInt(attributes.getNamedItem("viewCount").getNodeValue());
								var fc = parseInt(attributes.getNamedItem("favoriteCount").getNodeValue());
								output += " Viewed: " + API.formatNum(vc) + " times, favorited: " + API.formatNum(fc) + " times.";
							}
						} catch (e) { err += e + "\n"; }
	
						try {
							var state = API.getFirstByXpath(page, "//yt:state[@name=\"restricted\"]");
							if (state != null) {
								output += ". " + state.asText();
							}
						} catch (e) { err += e + "\n"; }
	
						try {
							var name = API.getFirstByXpath(page, "//author/name");
							if (name != null) {
								output += ". By " + name.asText();
							}
						} catch (e) { err += e + "\n"; }
	
						if (err.length > 0) API.info(err);
	
						response = output;
						response_to = channel;
						cancel = true;
						
						page = null;
					}
				}
			} else {
				cancel = true;
				var page = API.getPage(link);
				if (page) {
					try {
						var title = page.getTitleText();
						if (title) {
							response = "" + title;
							response_to = channel;
						}
						page = null;
					} catch(ex) {
						API.info(ex)
					}
				}
			}
		}
	}
} else if (message.match(/\d{5,8}\.\d{1,4}/)) {
	
	// Scan if message has a numeric id that resembles a forum post id
	var id = message.match(/(\d{5,8}\.\d{1,4})/)[1];
	var parts = id.split('.');
	if (parseInt(parts[1]) <= 1000) {
		// if there's a match, query forum search for it
		page = API.getPage("http://popodeus.com/forum-search/legacy.jsp?q="+id);
		if (page) {
			var table = API.getElementById(page, "result-table");
			// Display information about the forum post
			if (table) {
				var row = table.getRow(1);
				var topic = row.getCell(1).asText();
				var from = row.getCell(2).asText();
				var to = row.getCell(3).asText();
				var date = row.getCell(4).asText();
				var forum = row.getCell(5).asText();
				response = "\"" + topic + "\"" +
						" by " + from + (to.length > 0 ? " in response to " + to + "" : "") +
						" on " + date +
						" in " + forum +
						".";
				response += " http://popodeus.com/forumlink?"+id;
				response_to = channel;
			}
			cancel = true;
			page = null;
		}
	}
}
