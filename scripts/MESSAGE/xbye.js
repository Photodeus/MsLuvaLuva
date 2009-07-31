var timeout = bot.getValue("bye.timeout");
var delta = 20000;
if (timeout) delta = new Date().getTime() - timeout;

if (delta >= 20000) {
	message = message.toLowerCase();
	if (message.indexOf("i gotta go") >= 0
			|| message.indexOf("bye bye") >= 0
			|| message.indexOf("im leaving now") >= 0
			|| message.indexOf("i'm leaving now") >= 0
			|| message.indexOf("bye every") >= 0
			) {
		response = "Bye bye " + nick + "!";
		response_to = channel;
		bot.setValue("bye.timeout", new Date().getTime());

	} else if (message.indexOf("brb") >= 0 || message.indexOf("bbl") >= 0) {
		response = "See ya soon, " + nick;
		response_to = channel;
		bot.setValue("bye.timeout", new Date().getTime());

	}
} else {
	bot.getLog().info("Delta: " + delta);
}