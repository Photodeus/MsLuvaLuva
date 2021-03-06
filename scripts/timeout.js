// @description Op command only. Allows to set the minimum timeout in seconds between bot commands
// @example !timeout
// @example !timeout 10
// @example !timeout 10 command
// @notimeout
if (API.isOp(channel, nick) || API.isOwner(channel, nick) || API.isAdmin(channel, nick)) {
	if (param.match(/\d+/)) {
		var time = parseInt(param.match(/\d+/));
		if (time > 0 && time <= 120) {
			API.notice(nick, "Ok: Setting command timeout to " + time + " seconds");
			API.setTimeout("*", time * 1000);
		} else {
			API.say(channel, "Command timeout must be between 1 and 120 seconds");
		}
	} else {
		API.say(channel, "Command timeout is " + (API.getTimeout("*")/1000) + " seconds");
	}
} else {
	API.say(channel, "Command timeout is " + (API.getTimeout("*")/1000) + " seconds");
}
no_timeout = true;