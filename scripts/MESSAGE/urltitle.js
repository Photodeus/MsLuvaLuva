function endsWith(string, suffix)
{
	var lastIndex = string.lastIndexOf(suffix);
	return (lastIndex != -1) && (lastIndex + suffix.length == string.length);
}

if (message.indexOf('http://') >= 0) {
	var link = message.match(/.*(http:\/\/[^ ]+).*/);
	//API.info('urltitle: ' + link);
	if (link) {
		link = link[1];
		//API.info("Fetching link: " + link);
		if (link.indexOf('.jpg') > 0
				|| endsWith(link, '.jpeg')
				|| endsWith(link, '.png')
				|| endsWith(link, '.gif')
				|| endsWith(link, '.flv')
				|| link.match(/\.facebook\.com/)
				|| link.match(/http:\/\/open\.spotify\.com/)
				|| link.match(/http:\/\/pics\.livejournal\.com\/volare\/pic/)
				) {
			// do nothing
			// API.info("Link ignored: is on disallowed list. ");
		} else {
			/*
			 // Original title code from urltitle.jsp converted into JavaScript
			 var param = ""+API.encode(link);
			 var url = "http://popodeus.com/chat/bot/urltitle.jsp?"+param;
			 var title = ""+API.getPageAsText(url)
			 if (title.length > 4) {
			 API.info("Title: " + title);
			 response = title;
			 response_to = channel;
			 cancel = true;
			 }
			 */
			if (link.indexOf('youtube.com/watch') > 0) {
				var code = link.match(/\?v=([0-9a-zA-Z_\\-]+)/)[1];
				var output = "";
				if (code) {
					var page = API.getPage("http://gdata.youtube.com/feeds/api/videos/" + code);
					var title = API.getAsText(page, "//media:title");
					var err = "";
					var attributes;

					API.info("page: " + page + " title: " + title);
					try {
						if (title) {
							output += title;
						} else {
							output += "Title couldn't be fetched. Blame Connie";
						}
					} catch (e) { err += e + "\n"; }

					try {
						var duration = API.getByXpath(page, "//yt:duration");
						if (duration != null) {
							attributes = duration.getAttributes();
							var seconds = parseInt(attributes.getNamedItem("seconds").getNodeValue());
							var minutes = Math.floor(seconds / 60);
							if (minutes >= 1) { seconds = seconds % 60; }
							output += " [" + (minutes < 10 ? "0" : "") + minutes + ":" + (seconds < 10 ? "0" : "") + seconds + "]";
						}
					} catch (e) { err += e + "\n"; }

					try {
						var rating = API.getByXpath(page, "//gd:rating");
						if (rating != null) {
							attributes = rating.getAttributes();
							var r = Math.round(parseFloat(attributes.getNamedItem("average").getNodeValue()) * 100.) / 100.;
							var votes = API.formatNum(parseInt(attributes.getNamedItem("numRaters").getNodeValue()));
							output += " Avg.rating: " + r + "★ of " + votes + " votes.";
						}
					} catch (e) { err += e + "\n"; }

					try {
						var stats = API.getByXpath(page, "//yt:statistics");
						if (stats != null) {
							attributes = stats.getAttributes();
							var vc = parseInt(attributes.getNamedItem("viewCount").getNodeValue());
							var fc = parseInt(attributes.getNamedItem("favoriteCount").getNodeValue());
							output += " Viewed: " + API.formatNum(vc) + " times, favorited: " + API.formatNum(fc) + " times.";
						}
					} catch (e) { err += e + "\n"; }

					try {
						var state = API.getByXpath(page, "//yt:state[@name=\"restricted\"]");
						if (state != null) {
							output += ". " + state.asText();
						}
					} catch (e) { err += e + "\n"; }

					try {
						var name = API.getByXpath(page, "//author/name");
						if (name != null) {
							output += ". By " + name.asText();
						}
					} catch (e) { err += e + "\n"; }

					if (err.length > 0) API.info(err);

					response = output;
					response_to = channel;
					cancel = true;
				}
			} else {
				var page = API.getPage(link);
				var title = page.getTitleText();
				if (title != "") {
					response = "" + title;
					response_to = channel;
					cancel = true;
				}
			}
		}
	}
} else if (message.match(/\d{5,8}\.\d{1,4}/)) {
	var id = message.match(/(\d{5,8}\.\d{1,4})/)[1];
	if (parseInt(id.split('.')[1]) <= 1000) {
		var line = "" + API.getPageAsText('http://popodeus.com/forum-search/findthreadbyid.jsp?id=' + id);
		if (line.length > 4) {
			response = line;
			response_to = channel;
			cancel = true;
		}
	}
}
