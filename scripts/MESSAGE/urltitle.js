function endsWith(string, suffix)
{
    var lastIndex = string.lastIndexOf(suffix);
    return (lastIndex != -1) && (lastIndex + suffix.length == string.length);
}

if (message.indexOf('http://') >= 0) {
	var link = message.match(/.*(http:\/\/[^ ]+).*/);
	if (link) {
		link = link[1];
		bot.getLog().info("Fetching link: " + link);
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
			//bot.getLog().info("Link ignored: is on disallowed list. ");
		} else {
			var param = ""+bot.encode(link);
			var url = "http://popodeus.com/chat/bot/urltitle.jsp?"+param;
			var title = ""+bot.fetchUrl(url)
			bot.getLog().info("Title: " + title);
			if (title.length > 4) {
				response = title;
				response_to = channel;
				//cancel = true;
			}
		}
	}
} else if (message.match(/\d{5,8}\.\d{1,4}/)) {
	var id = message.match(/(\d{5,8}\.\d{1,4})/)[1];
	var line = ""+bot.fetchUrl('http://popodeus.com/forum-search/findthreadbyid.jsp?id='+id);
	if (line.length > 4) {
		response = line;
		response_to = channel;
		//cancel = true;
	}
}