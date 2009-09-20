// @private
if (param == nick) {
	API.say(nick, "Please specify a nickname to warn")
} else {
	var warning = "You have been warned by an op. Please tone down your conversation or you may be banned from chat"
	API.notice(param, warning);
}