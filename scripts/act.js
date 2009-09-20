// @private
if (API.isOwner(channel, nick) || API.isAdmin(channel, nick)) {
	var tmp = param.match(/(\#[a-zA-Z]+)\s(.+)/);
	if (tmp) {
		API.action(tmp[1], tmp[2]);
	}
}
no_timeout = true;
