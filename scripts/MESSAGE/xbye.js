var timeout = API.getValue("bye.timeout");
var delta = 20000;
if (timeout) delta = new Date().getTime() - timeout;

if (delta >= 20000) {
	message = message.toLowerCase();
	var u, r;
	if (message.indexOf("melvin") >= 0 && message.indexOf("!who") < 0 && message.length > 10) {
		/*
		r = [
			"Who is pregnant with Melvin's son..."
		];
		"All hail Melvin!",
		"All hail Melvin!",
		"All hail Melvin! For he's the only ruler!",
		"Melvin is the greatest!",
		"Melvin even has a cola named after him, he must be good!",
		"Melvin is my hero!",
		"Melvin is my hero! He should be your hero too.",
		"Melviiiiiiiin!",
		"Melvin is the only one I will give my virginity to.",
		"Drink Melvin cola!",
		response = r[Math.floor(Math.random() * r.length)];
		response_to = channel;
		API.setValue("bye.timeout", new Date().getTime()+240000);
		*/
	}
	if (message.indexOf("is having a baby") >= 0
			|| message.indexOf("is getting a baby") >= 0
			|| message.indexOf("i'm having a baby") >= 0
			|| message.indexOf("is preggers") >= 0
			|| message.indexOf("i'm pregnant") >= 0
			|| message.indexOf("i'll be a daddy") >= 0
			|| message.indexOf("i'm gonna be a daddy") >= 0
			) {
		response = "Congrats " + nick + "! Babies are fun! And tasty...";
		response_to = channel;
		API.setValue("bye.timeout", new Date().getTime());
	}
	if (message.indexOf("popogeddon") >= 0
			|| message.indexOf("armageddon") >= 0
			|| message.indexOf("999") >= 0
			) {
		r = [
			"We shall all die!",
			"Popogeddon is here!",
			"I just wish it all was a bad dream...",
			"Be afraid... be very afraid.",
			"We're doomed!",
		];
		response = r[Math.floor(Math.random() * r.length)];
		response_to = channel;
		API.setValue("bye.timeout", new Date().getTime()+10000);
	}
	if (message.indexOf("i gotta go") >= 0
			|| message.indexOf("i got to go") >= 0
			|| message.indexOf("bye bye") >= 0
			|| message.indexOf("im leaving now") >= 0
			|| message.indexOf("i'm leaving now") >= 0
			|| message.indexOf("bye every") >= 0
			|| message.indexOf("bye for now") >= 0
			|| message.indexOf("i'm off now") >= 0
			|| message.indexOf("i'm off for now") >= 0
			|| message.indexOf("im off now") >= 0
			|| message.indexOf("imma roll") >= 0
			|| message.indexOf("laters all") >= 0
			|| message.indexOf("laters every") >= 0
			) {
		u = [
			"Bye byes $nick!",
			"Good bye, $nick.",
			"Hope to have you come back sometime soon, $nick.",
			"I'll be here waiting for your return, $nick.",
			"Happy to have you here, $nick.",
		];
		response = u[Math.floor(Math.random() * u.length)].replace(/\$nick/, nick);
		response_to = channel;
		API.setValue("bye.timeout", new Date().getTime());

	} else if (message.indexOf("brb") >= 0 || message.indexOf("be right back") >= 0) {
		u = [
			"See ya soon $nick.",
			"See you in a bit then, $nick",
			"Well see you real soon $nick.",
			"See ya, $nick!",
			"I'll be waiting for you, $nick.",
			"Don't be gone for too long, $nick!",
		];
		response = u[Math.floor(Math.random() * u.length)].replace(/\$nick/, nick);
		response_to = channel;
		API.setValue("bye.timeout", new Date().getTime());

	}

} else {
	//API.debug("xbye Delta: " + delta);
	if (message.toLowerCase().indexOf("alanas") >= 0) {
		API.info("\n\n:    ALANAS MENTIONED\n");
	}
}
