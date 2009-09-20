var timeout = API.getValue("bye.timeout");
var delta = 20000;
if (timeout) delta = new Date().getTime() - timeout;
// Only say bye at most each 20 seconds
if (delta >= 20000) {
	message = message.toLowerCase();
	if (message.indexOf("i gotta go") >= 0
			|| message.indexOf("i got to go") >= 0
			|| message.indexOf("bye bye") >= 0
			|| message.indexOf("im leaving now") >= 0
			|| message.indexOf("i'm leaving now") >= 0
			|| message.indexOf("bye every") >= 0
			|| message.indexOf("bye for now") >= 0
			|| message.indexOf("ciao") >= 0
			) {
		response = "Bye bye " + nick + "!";
		response_to = channel;
		API.setValue("bye.timeout", new Date().getTime());

	} else if (message.indexOf("brb") >= 0 || message.match(/[^a-z]*bbl/) || message.indexOf("be right back") >= 0) {
		var u = [
			"See ya soon $nick.",
			"See you in a bit then, $nick",
			"Well see you real soon $nick.",
			"See ya, $nick!",
			"I'll be waiting for you, $nick.",
		];
		response = u[Math.floor(Math.random() * u.length)].replace(/\$nick/, nick);
		response_to = channel;
		API.setValue("bye.timeout", new Date().getTime());

	}
}
