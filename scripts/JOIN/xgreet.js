
if (API.hasVoice(channel, API.getBotNick())) {
	var greet = API.getGreeting();
	if (greet) {
		greet = ""+greet;
		response = greet.replace(/\$nick/, nick);
		response_to = channel;
	}
}
