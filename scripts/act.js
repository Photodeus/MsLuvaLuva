// @private
var tmp = param.match(/(\#[a-zA-Z]+)\s(.+)/);
if (API.isOwner(tmp[1], nick) || API.isAdmin(tmp[1], nick)) {
	if (tmp) {
		API.action(tmp[1], tmp[2]);
	}
}
no_timeout = true;
