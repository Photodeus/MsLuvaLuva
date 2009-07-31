// @private
if (param == nick) {
	bot.sendMessage(nick, "Please specify a nickname to warn")
} else {
	var warning = "You have been warned by an op. Please tone down your conversation or you may be banned from chat"
	bot.sendNotice(param, warning);
}