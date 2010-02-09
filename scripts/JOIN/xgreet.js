// If bot has a voice on the channel, she will greet people who join
// TODO check for config.properties file for setting "channels.greet"
if (API.hasVoice(channel, API.getBotNick())) {
	var greet = API.getGreeting();
	if (greet) {
		var lnick = ""+nick.toLowerCase();
		var ignore = lnick.equals("bot");
			
		if (!ignore) {
			var gone = API.getValue("seen." + lnick + ".quit.time");
			var now = parseInt(new Date().getTime()/1000);
			if (gone && now - gone < 60) {
				// don't greet same person more than once in a minute
			} else {
				greet = ""+greet;
				response = greet.replace(/\$nick/, nick);
				if (response.indexOf("$number") >= 0) {
					var rand = "";
					for (var i=0; i<8; i++) rand = rand + "" + Math.floor(Math.random()*10);
					response = response.replace(/\$number/, rand);
				}
				response_to = channel;
			}
		}
	}
}
