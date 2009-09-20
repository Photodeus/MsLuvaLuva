// @private
if (API.isOwner(channel, nick) || API.isAdmin(channel, nick) || API.isOwner(channel, nick)) {
	var tmp = param.match(/(\#[a-zA-Z]+)\s(.+)/);
	if (tmp) {
		API.say(tmp[1], tmp[2]);
	}
}
no_timeout = true;
