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
			if (link.indexOf('youtube.com') > 0) {
				// TODO port youtube title code from urltitle.jsp
				var param = ""+API.encode(link);
				var url = "http://popodeus.com/chat/bot/urltitle.jsp?"+param;
				var title = ""+API.getPageAsText(url)
				if (title.length > 4) {
					API.info("Title: " + title);
					response = title;
					response_to = channel;
					cancel = true;
				}
			} else {
				var page = API.getPage(link);
				var title = page.getTitleText();
				if (title != "") {
					response = ""+title;
					response_to = channel;
					cancel = true;
				}
			}
		}
	}
} else if (message.match(/\d{5,8}\.\d{1,4}/)) {
	var id = message.match(/(\d{5,8}\.\d{1,4})/)[1];
	if (parseInt(id.split('.')[1]) <= 1000) {
		var line = ""+API.getPageAsText('http://popodeus.com/forum-search/findthreadbyid.jsp?id='+id);
		if (line.length > 4) {
			response = line;
			response_to = channel;
			cancel = true;
		}
	}
}
