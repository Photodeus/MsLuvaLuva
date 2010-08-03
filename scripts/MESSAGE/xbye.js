var bye_timeout;
var delta = 20000;
if (bye_timeout) delta = new Date().getTime() - bye_timeout;

if (delta >= 20000) {
	message = message.toLowerCase();
	var u, r;
	if (message.indexOf("is having a baby") >= 0
			|| message.indexOf("is getting a baby") >= 0
			|| message.indexOf("i'm having a baby") >= 0
			|| message.indexOf("is preggers") >= 0
			|| message.indexOf("i'm pregnant") >= 0
			|| message.indexOf("i'll be a dad") >= 0
			|| message.indexOf("im gonna be a dad") >= 0
			|| message.indexOf("i'm gonna be a dad") >= 0
			|| message.indexOf("i'm gonna be a father") >= 0
			|| message.indexOf("baby was born") >= 0
			|| message.indexOf("baby has been born") >= 0
			) {
		if (message.indexOf("if i'm") < 0) {
			response = "Congrats " + nick + "! Babies are fun! I loooove babies.";
			response_to = channel;
			bye_timeout = new Date().getTime();
		}
		bye_timeout = new Date().getTime();
	} else if (message.indexOf("popogeddon") >= 0
			|| message.indexOf("armageddon") >= 0
			) {
		r = [
			"We shall all die!",
			"Popogeddon is here!",
			"I just wish it all was just a bad dream...",
			"Be afraid... be very afraid.",
			"We're doomed!",
		];
		response = r[Math.floor(Math.random() * r.length)];
		response_to = channel;
		bye_timeout = new Date().getTime() + 10000;
	}
} else {
}
