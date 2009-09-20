// @description Ignores a certain nick and host from using the bot for a certain amount of time

if (API.isOwner(channel, nick) || API.isAdmin(channel, nick) || API.isOp(channel, nick)) {
	param = param.toLowerCase();
	if (param.match(/^\+.*/)) {
		param = param.substring(1);
		API.addIgnore(param, null, null);
		API.notice(nick, "Okay, ignored " + param);
	}
	if (param.match(/^\-.*/)) {
		param = param.substring(1);
		API.removeIgnore(param, null, null);
		API.notice(nick, "Okay, unignored " + param);
	}

} else {
	API.notice(nick, "Not enough privileges to do that");
}